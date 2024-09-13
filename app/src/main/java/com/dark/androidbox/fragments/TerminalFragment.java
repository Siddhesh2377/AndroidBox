package com.dark.androidbox.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dark.androidbox.databinding.FragmentEditorBinding;
import com.dark.androidbox.databinding.TerminalFragmentBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalFragment extends Fragment {

    TerminalFragmentBinding binding;

    public TerminalFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TerminalFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       executeCommand();
    }


    private void executeCommand() {
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", "cd /data/data/com.dark.androidbox && ls");
                Process process = processBuilder.start();

                // Read the standard output
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                // Read the error output
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                StringBuilder output = new StringBuilder();
                String line;
                while ((line = stdInput.readLine()) != null) {
                    output.append(line).append("\n");
                }

                StringBuilder error = new StringBuilder();
                while ((line = stdError.readLine()) != null) {
                    error.append(line).append("\n");
                }

                // Set text color and content based on output
                requireActivity().runOnUiThread(() -> {
                    if (output.length() > 0) {
                        binding.outputView.setTextColor(Color.WHITE);
                        binding.outputView.setText(output.toString());
                    } else if (error.length() > 0) {
                        binding.outputView.setTextColor(Color.RED);
                        binding.outputView.setText(error.toString());
                    }
                });

            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> {
                    binding.outputView.setTextColor(Color.RED);
                    binding.outputView.setText(e.toString());
                });
                Log.e("TerminalFragment", "Error executing command", e);
            }
        }).start();
    }





}
