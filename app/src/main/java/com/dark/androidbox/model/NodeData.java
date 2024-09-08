package com.dark.androidbox.model;

import com.dark.androidbox.nodes.BaseNode;
import com.dark.androidbox.types.NodeTypes;

public class NodeData {


    public String title;
    public String code;
    public NodeTypes types;
    public BaseNode node;


    public NodeData(String title, String code, NodeTypes types, BaseNode node) {
        this.title = title;
        this.code = code;
        this.types = types;
        this.node = node;
    }


}
