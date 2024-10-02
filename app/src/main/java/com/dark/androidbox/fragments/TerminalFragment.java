package com.dark.androidbox.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dark.androidbox.databinding.TerminalFragmentBinding;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;

@SuppressLint("SetTextI18n")
public class TerminalFragment extends Fragment {

    private Context ctx;
    private TerminalFragmentBinding binding;

    public TerminalFragment() {
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
        ctx = requireContext();
        init();
    }

    private void init() {
        try {
            compileAndRunWithJanino(readJavaFile("/storage/emulated/0/AndroidBox/Main.java"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readJavaFile(String filePath) throws IOException {
        StringBuilder javaCode = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            javaCode.append(line).append("\n");
        }
        reader.close();
        return javaCode.toString();
    }

    private void compileAndRunWithJanino(String javaCode) {
        try {
            SimpleCompiler compiler = new SimpleCompiler();
            compiler.cook(javaCode);

            Class<?> clazz = compiler.getClassLoader().loadClass("Main");
            Method method = clazz.getMethod("main", String[].class);

            // Redirect output
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(outContent);
            PrintStream oldOut = System.out;
            System.setOut(ps);

            // Execute main method
            method.invoke(null, (Object) new String[]{});

            // Restore output
            System.out.flush();
            System.setOut(oldOut);

            // Display output
            String output = outContent.toString();
            binding.output.setText(output);

        } catch (CompileException e) {
            binding.output.setText("Compilation Error: " + e);
        } catch (Exception e) {
            binding.output.setText("Execution Error: " + e);
        }
    }


}
