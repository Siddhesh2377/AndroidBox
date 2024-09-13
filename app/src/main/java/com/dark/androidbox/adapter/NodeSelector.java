package com.dark.androidbox.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dark.androidbox.R;
import com.dark.androidbox.databinding.MethodsListBinding;
import com.dark.androidbox.model.NodeData;
import com.gyso.treeview.model.NodeModel;

import java.util.List;

public class NodeSelector extends BaseAdapter {

    private final List<NodeModel<NodeData>> data;
    private final Context context;
    private final LayoutInflater inflater;

    public NodeSelector(Context context, List<NodeModel<NodeData>> data) {
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
        convertView.setBackgroundResource(R.drawable.bg_node_list_view);
        if (data.get(i).value.title.equals("Var"))
            holder.binding.title.setTextColor(Color.parseColor("#ee596e"));
        else if (data.get(i).value.title.equals("Methods"))
            holder.binding.title.setTextColor(Color.parseColor("#E49D33"));
        else holder.binding.title.setTextColor(Color.DKGRAY);
        holder.binding.title.setText(data.get(i).value.title);
        return convertView;
    }

    static class ViewHolder {
        MethodsListBinding binding;

        ViewHolder(MethodsListBinding binding) {
            this.binding = binding;
        }
    }
}
