package com.dark.androidbox.nodes;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.dark.androidbox.databinding.ClassNodeBinding;

public class ClassNode extends BaseNode {

    private ClassNodeBinding binding;

    public ClassNode(Context context) {
        super(context);
    }

    public ClassNode(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClassNode(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ClassNode(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void init() {
        super.init();
        binding = ClassNodeBinding.inflate(LayoutInflater.from(getContext()), this, true);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    public void postInit() {
        super.postInit();
        binding.title.setText(data.value.title);
    }
}
