package com.gyso.treeview.events;

import com.gyso.treeview.model.NodeModel;

public interface NodeEventManager {
    void OnDrag(NodeModel<?> model);
}
