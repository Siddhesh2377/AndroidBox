package com.dark.androidbox.model;

import com.dark.androidbox.types.NodeTypes;

public class NodeData {


    public String title;
    public String code;
    public NodeTypes types;


    public NodeData(String title, String code, NodeTypes types) {
        this.title = title;
        this.code = code;
        this.types = types;
    }


}
