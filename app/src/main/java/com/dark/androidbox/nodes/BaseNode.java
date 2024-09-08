package com.dark.androidbox.nodes;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.dark.androidbox.databinding.BaseNodeBinding;

public class BaseNode extends FrameLayout {

    BaseNodeBinding binding;

    public BaseNode(Context context) {
        super(context);
        init();
    }

    public BaseNode(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseNode(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BaseNode(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        binding = BaseNodeBinding.inflate(LayoutInflater.from(getContext()), this, true);
        setBackgroundColor(Color.WHITE);
    }
}
