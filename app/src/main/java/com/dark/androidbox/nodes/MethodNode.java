package com.dark.androidbox.nodes;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.dark.androidbox.MainActivity;
import com.dark.androidbox.adapter.MethodSelectAdapter;
import com.dark.androidbox.databinding.MethodNodeBinding;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class MethodNode extends BaseNode {

    private MethodNodeBinding binding;

    public MethodNode(Context context) {
        super(context);
    }

    public MethodNode(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MethodNode(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MethodNode(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void init() {
        super.init();
        binding = MethodNodeBinding.inflate(LayoutInflater.from(getContext()), this, true);
        setBackgroundColor(Color.GREEN);
    }

    @Override
    public void postInit() {
        super.postInit();
        binding.title.setVisibility(GONE);

        setup(binding.choose);

    }


    void setup(AppCompatSpinner spinner) {

        MethodSelectAdapter adapter = new MethodSelectAdapter(getContext(),    (List<MethodDeclaration>) data.getParentNode().value.data);



        // Bind the adapter to the Spinner
        spinner.setAdapter(adapter);

        // Set a listener to handle item selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                binding.title.setText(selectedItem);
                MainActivity.treeViewStatic.getEditor().setWantEdit(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no item is selected
            }
        });
    }
}
