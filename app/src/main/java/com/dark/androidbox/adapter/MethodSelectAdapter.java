package com.dark.androidbox.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dark.androidbox.databinding.MethodsListBinding;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;

public class MethodSelectAdapter extends BaseAdapter {

    private List<MethodDeclaration> data;
    private Context context;
    private LayoutInflater inflater;

    public MethodSelectAdapter(Context context, List<MethodDeclaration> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            MethodsListBinding binding = MethodsListBinding.inflate(inflater, viewGroup, false);
            holder = new ViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MethodDeclaration method = data.get(i);
        holder.binding.title.setText(method.getNameAsString());

        return convertView;
    }

    static class ViewHolder {
        MethodsListBinding binding;

        ViewHolder(MethodsListBinding binding) {
            this.binding = binding;
        }
    }
}
