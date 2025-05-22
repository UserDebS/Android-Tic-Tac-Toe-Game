package com.example.tictactoe.gameai.algorithm;

public class MiniMax {
    /*
    Note : 1 -> AI
           2 -> Player
           0 -> Empty cell
    */
    private final int gridCounter;

    public MiniMax() {
        this.gridCounter = 3;
    }
    public int[] nextStep(byte[][] currentState) {
        if(isTerminalState(currentState)) return null;

        int bestRow = -1, bestColumn = -1;
        int bestValue = Integer.MIN_VALUE, value;

        for(int row = 0; row < gridCounter; row++) {
            for(int column = 0; column < gridCounter; column++) {
                if(currentState[row][column] != 0) continue;
                currentState[row][column] = (byte) (1); // for ai's turn
                value = this.run(currentState, false);
                currentState[row][column] = (byte) (0); // reset

                if(bestValue < value) { // if better result
                    bestValue = value;
                    bestRow = row;
                    bestColumn = column;
                }
            }
        }

        return (bestRow != -1 || bestColumn != -1)? new int[]{bestRow, bestColumn} : null;
    }

    private int getStateValue(byte[][] state) { // for recursive calls
        byte row1 = (byte) (state[0][0] & state[0][1] & state[0][2]);
        byte row2 = (byte) (state[1][0] & state[1][1] & state[1][2]);
        byte row3 = (byte) (state[2][0] & state[2][1] & state[2][2]);
        byte column1 = (byte) (state[0][0] & state[1][0] & state[2][0]);
        byte column2 = (byte) (state[0][1] & state[1][1] & state[2][1]);
        byte column3 = (byte) (state[0][2] & state[1][2] & state[2][2]);
        byte diagonal1 = (byte) (state[0][0] & state[1][1] & state[2][2]);
        byte diagonal2 = (byte) (state[0][2] & state[1][1] & state[2][0]);
        if(row1 != 0) return row1 == 1? 10 : -10;
        if(row2 != 0) return row2 == 1? 10 : -10;
        if(row3 != 0) return row3 == 1? 10 : -10;
        if(column1 != 0) return column1 == 1? 10 : -10;
        if(column2 != 0) return column2 == 1? 10 : -10;
        if(column3 != 0) return column3 == 1? 10 : -10;
        if(diagonal1 != 0) return diagonal1 == 1? 10 : -10;
        if(diagonal2 != 0) return diagonal2 == 1? 10 : -10;
        return 0;
    }

    private boolean isTerminalState(byte[][] state) {
        if(getStateValue(state) != 0) return true;

        for(int row = 0; row < gridCounter; row++) {
            for(int column = 0; column < gridCounter; column++) {
                if(state[row][column] == 0) return false;
            }
        }

        return true;
    }

    private int run(byte[][] state, boolean isMaximizingPlayer) {
        if(isTerminalState(state)) return getStateValue(state);
        int bestValue;

        if(isMaximizingPlayer) {
            bestValue = Integer.MIN_VALUE;

            for(int row = 0; row < gridCounter; row++) {
                for(int column = 0; column < gridCounter; column++) {
                    if(state[row][column] != 0) continue;
                    state[row][column] = (byte) (1);
                    bestValue = Math.max(bestValue, run(state, false));
                    state[row][column] = (byte) (0);
                }
            }

        } else {
            bestValue = Integer.MAX_VALUE;

            for(int row = 0; row < gridCounter; row++) {
                for(int column = 0; column < gridCounter; column++) {
                    if(state[row][column] != 0) continue;
                    state[row][column] = (byte) (2);
                    bestValue = Math.min(bestValue, run(state, true));
                    state[row][column] = (byte) (0);
                }
            }

        }

        return bestValue;
    }
}
