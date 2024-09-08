package com.dark.androidbox.nodes;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.dark.androidbox.R;

public class ClassNode extends BaseNode{

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
        setBackgroundColor(Color.GREEN);
    }
}
