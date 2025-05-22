package com.example.tictactoe.screens;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.tictactoe.R;
import com.example.tictactoe.gameai.GameAI;

public class GameScreenFragment extends Fragment {
    private Context context = null;
    private AppCompatButton resetButton;
    private ImageButton navHomeButton;
    private GridLayout gridPlayground;
    private ImageButton[][] imageButtons;
    private ImageView lineThroughView = null;
    private GameAI ai;
    private boolean[][] imageButtonsClicked;
    private byte[][] gameBoard;
    /* gameBoard =>
     * 0 means empty
     * 1 means cross
     * 2 means circle
     */

    private static final int gridCounter = 3;
    private boolean crossTurn = false;
    private boolean isVsAI = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            isVsAI = getArguments().getBoolean("is_vs_ai", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // views init
        resetButton = view.findViewById(R.id.resetButton);
        navHomeButton = view.findViewById(R.id.button_nav_home);
        gridPlayground = view.findViewById(R.id.playground);

        // variables init
        context = requireContext();
        gameBoard = new byte[gridCounter][gridCounter];
        imageButtons = new ImageButton[gridCounter][gridCounter];
        imageButtonsClicked = new boolean[gridCounter][gridCounter];
        ai = (isVsAI)? new GameAI(imageButtons) : null;

        // grid button init
        for(int row = 0; row < gridCounter; row++) {
            for(int column = 0; column < gridCounter; column++){
                // imageButton creation and id assignment
                ImageButton imageButton = new ImageButton(context);
                imageButton.setId(View.generateViewId());
                imageButton.setBackgroundColor(Color.TRANSPARENT);

                //size and margin to conversion
                int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, getResources().getDisplayMetrics());
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());

                // imageButton layout design
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.height = size;
                layoutParams.width = size;
                int marginLeft = margin, marginRight = margin, marginBottom = margin, marginTop = margin;
                if(row == 0) {
                    marginTop = 0;
                } else if(row == 2) {
                    marginBottom = 0;
                }

                if(column == 0) {
                    marginLeft = 0;
                } else if(column == 2) {
                    marginRight = 0;
                }
                layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                imageButton.setLayoutParams(layoutParams);

                // button listener set up
                final int finalRow = row;
                final int finalColumn = column;

                // on click listener
                imageButton.setOnClickListener(currentView -> {
                    if(imageButtonsClicked[finalRow][finalColumn]) return;

                    imageButtonsClicked[finalRow][finalColumn] = true;
                    if(crossTurn) {
                        currentView.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_cross_sign));
                        gameBoard[finalRow][finalColumn] = 1;
                        crossTurn = (!crossTurn);
                    } else {
                        currentView.setBackground(AppCompatResources.getDrawable(context, R.drawable.ic_circle_sign));
                        gameBoard[finalRow][finalColumn] = 2;
                        crossTurn = (!crossTurn);

                        //right after user's turn
                        if(isVsAI && ai != null) ai.nextStep(gameBoard);
                    }

                    byte result = isGameCompleted();

                    if(result == 1) { //cross wins
                        disableBoard();
                    } else if(result == 2) { //circle wins
                        disableBoard();
                    } else if(result == 0) { // draw
                        disableBoard();
                    }

                });

                // imageButton stored in imageButtons array, gameBoard and imageButtonClicked set up
                imageButtons[row][column] = imageButton;
                imageButtonsClicked[row][column] = false;
                gameBoard[row][column] = 0;

                // adding imageButton into gridPlayground
                gridPlayground.addView(imageButton);
            }
        }

        resetButton.setOnClickListener(currentView -> {
            stateReset();
        });

        navHomeButton.setOnClickListener(currentView -> {
            if(isAdded()) requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
    }

    private void stateReset() {
        crossTurn = false;

        if(lineThroughView != null) {
            ((FrameLayout) gridPlayground.getParent()).removeView(lineThroughView);
            lineThroughView = null;
        }

        for(int row = 0; row < gridCounter; row++) {
            for(int column = 0; column < gridCounter; column++) {
                imageButtonsClicked[row][column] = false;
                gameBoard[row][column] = 0;
                imageButtons[row][column].setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private void disableBoard() {
        for(int row = 0; row < gridCounter; row++) {
            for(int column = 0; column < gridCounter; column++) {
                imageButtonsClicked[row][column] = true;
            }
        }
    }

    private byte isGameCompleted() {
        /*
         * ## returns
         *   0 if draw
         *   1 if cross wins
         *   2 if circle wins
         *   -1 if match is not finished
         *
         * ## 8 ways to win a game by any player
         * ## Total 8 * 2(2 players) = 16 way to win
         *
         * r1 for first row covered
         * r2, r3..
         *
         * c1 for first column covered
         * c2, c3..
         *
         * d1, d2 for 2 diagonals covered
         *
         * Then all of them for both cross and circle
         */

        boolean noMovesLeft = true;

        byte crossR1 = 1, crossR2 = 1, crossR3 = 1, crossC1 = 1, crossC2 = 1, crossC3 = 1, crossD1 = 1, crossD2 = 1;
        byte circleR1 = 2, circleR2 = 2, circleR3 = 2, circleC1 = 2, circleC2 = 2, circleC3 = 2, circleD1 = 2, circleD2 = 2;

        for(int row = 0; row < gridCounter; row++) {
            for(int column = 0; column < gridCounter; column++) {
                noMovesLeft = (noMovesLeft && imageButtonsClicked[row][column]);
                // for rows
                if(row == 0) {
                    crossR1 = (byte) (crossR1 & gameBoard[row][column]);
                    circleR1 = (byte) (circleR1 & gameBoard[row][column]);
                } else if(row == 1) {
                    crossR2 = (byte) (crossR2 & gameBoard[row][column]);
                    circleR2 = (byte) (circleR2 & gameBoard[row][column]);
                } else {
                    crossR3 = (byte) (crossR3 & gameBoard[row][column]);
                    circleR3 = (byte) (circleR3 & gameBoard[row][column]);
                }

                //for columns
                if(column == 0) {
                    crossC1 = (byte) (crossC1 & gameBoard[row][column]);
                    circleC1 = (byte) (circleC1 & gameBoard[row][column]);
                } else if(column == 1) {
                    crossC2 = (byte) (crossC2 & gameBoard[row][column]);
                    circleC2 = (byte) (circleC2 & gameBoard[row][column]);
                } else {
                    crossC3 = (byte) (crossC3 & gameBoard[row][column]);
                    circleC3 = (byte) (circleC3 & gameBoard[row][column]);
                }

                //for diagonals
                if(row == column) {
                    crossD1 = (byte) (crossD1 & gameBoard[row][column]);
                    circleD1 = (byte) (circleD1 & gameBoard[row][column]);

                    crossD2 = (byte) (crossD2 & gameBoard[row][gridCounter - column - 1]);
                    circleD2 = (byte) (circleD2 & gameBoard[row][gridCounter - column - 1]);
                }
            }
        }

        // winnings ways handling
        if(crossR1 == 1 || circleR1 == 2) { // r1
            drawLineThrough(R.drawable.ic_win_r1);
        }
        if(crossR2 == 1 || circleR2 == 2) { // r2
            drawLineThrough(R.drawable.ic_win_r2);
        }
        if(crossR3 == 1 || circleR3 == 2) { // r3
            drawLineThrough(R.drawable.ic_win_r3);
        }
        if(crossC1 == 1 || circleC1 == 2) { // c1
            drawLineThrough(R.drawable.ic_win_c1);
        }
        if(crossC2 == 1 || circleC2 == 2) { // c2
            drawLineThrough(R.drawable.ic_win_c2);
        }
        if(crossC3 == 1 || circleC3 == 2) { // c3
            drawLineThrough(R.drawable.ic_win_c3);
        }
        if(crossD1 == 1 || circleD1 == 2) { // d1
            drawLineThrough(R.drawable.ic_win_d1);
        }
        if(crossD2 == 1 || circleD2 == 2) { // d2
            drawLineThrough(R.drawable.ic_win_d2);
        }


        // winner handling
        if(crossR1 == 1 || crossC1 == 1) return 1;
        if(crossR2 == 1 || crossC2 == 1) return 1;
        if(crossR3 == 1 || crossC3 == 1) return 1;
        if(crossD1 == 1 || crossD2 == 1) return 1;

        if(circleR1 == 2 || circleC1 == 2) return 2;
        if(circleR2 == 2 || circleC2 == 2) return 2;
        if(circleR3 == 2 || circleC3 == 2) return 2;
        if(circleD1 == 2 || circleD2 == 2) return 2;

        // if no moves left then draw else match is still on... >:)
        return (byte) (noMovesLeft? 0 : -1);
    }

    private void drawLineThrough(@DrawableRes int drawableID) {
        if(lineThroughView != null) return;

        lineThroughView = new ImageView(context);
        lineThroughView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        lineThroughView.setImageDrawable(AppCompatResources.getDrawable(context, drawableID));
        lineThroughView.setScaleType(ImageView.ScaleType.FIT_XY);

        ((FrameLayout) gridPlayground.getParent()).addView(lineThroughView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resetButton = null;
        gridPlayground = null;
        lineThroughView = null;
        imageButtons = null;
        navHomeButton = null;

        ai = null;
        gameBoard = null;
        imageButtonsClicked = null;
    }

    public static GameScreenFragment newInstance(boolean isVsAI) {
        GameScreenFragment gameScreenFragment = new GameScreenFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_vs_ai", isVsAI);
        gameScreenFragment.setArguments(bundle);
        return gameScreenFragment;
    }
}
