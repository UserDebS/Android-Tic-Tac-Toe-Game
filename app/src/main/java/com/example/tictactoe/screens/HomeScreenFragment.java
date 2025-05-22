package com.example.tictactoe.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.tictactoe.R;

public class HomeScreenFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatButton navPlayerGameButton = view.findViewById(R.id.button_nav_player);
        AppCompatButton navComputerGameButton = view.findViewById(R.id.button_nav_computer);

        // set up listeners
        navComputerGameButton.setOnClickListener(currentView -> {
            if(isAdded()) requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_main, GameScreenFragment.newInstance(true))
                    .addToBackStack(null)
                    .commit();
        });


        navPlayerGameButton.setOnClickListener(currentView -> {
            if(isAdded()) requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_main, GameScreenFragment.newInstance(false))
                    .addToBackStack(null)
                    .commit();
        });
    }
}
