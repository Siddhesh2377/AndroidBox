package com.dark.androidbox.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.dark.androidbox.databinding.NodeviewBinding;
import com.dark.androidbox.model.NodeData;
import com.gyso.treeview.adapter.DrawInfo;
import com.gyso.treeview.adapter.TreeViewAdapter;
import com.gyso.treeview.adapter.TreeViewHolder;
import com.gyso.treeview.line.BaseLine;
import com.gyso.treeview.model.NodeModel;


public class NodeViewAdapter extends TreeViewAdapter<NodeData> {

    @Override
    public TreeViewHolder<NodeData> onCreateViewHolder(@NonNull ViewGroup viewGroup, NodeModel<NodeData> model) {
        return new TreeViewHolder<>(NodeviewBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false).getRoot(), model);
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder<NodeData> holder) {
        View root = holder.getView();
        NodeModel<NodeData> data = holder.getNode();
        NodeviewBinding binding = NodeviewBinding.bind(root);

        binding.root.removeAllViews();
        binding.root.addView(data.value.node);
        //binding.txt.setText(data.value.title);
    }

    @Override
    public BaseLine onDrawLine(DrawInfo drawInfo) {
        return null;
    }


}
