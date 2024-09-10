package com.dark.androidbox.nodes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.dark.androidbox.databinding.ClassNodeBinding;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void postInit() {
        super.postInit();

        if (data.value.data != null && data.value.data instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration declaration = (ClassOrInterfaceDeclaration) data.value.data;

            String c =  "Type   : class\n" +
                    "\n" +
                    "Implementation : " + declaration.getImplementedTypes().toString().replace("[", "").replace("]", "") + "\n" +
                    "\n" +
                    "Extended : " + declaration.getExtendedTypes().toString().replace("[", "").replace("]", "");

            binding.ctx.setText(processText(c, declaration));
        }
    }

    private SpannableString processText(String text, ClassOrInterfaceDeclaration declaration) {

        String imp = declaration.getImplementedTypes().toString().replace("[", "").replace("]", "");
        String ext = declaration.getExtendedTypes().toString().replace("[", "").replace("]", "");

        SpannableString spannableString = new SpannableString(text);

        // Apply color spans
        int startIndex = text.indexOf("Type   : ") + "Type   : ".length();
        int endIndex = startIndex + "class".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#B276FF")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        startIndex = text.indexOf("Implementation : ") + "Implementation : ".length();
        endIndex = startIndex + imp.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#E49D33")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        startIndex = text.indexOf("Extended : ") + "Extended : ".length();
        endIndex = startIndex + ext.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#E49D33")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Return the SpannableString to be set in the TextView
        return spannableString;
    }
}
