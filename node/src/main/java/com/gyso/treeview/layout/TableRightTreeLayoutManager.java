package com.gyso.treeview.layout;

import android.content.Context;
import android.view.View;

import com.gyso.treeview.TreeViewContainer;
import com.gyso.treeview.adapter.TreeViewHolder;
import com.gyso.treeview.algorithm.table.Table;
import com.gyso.treeview.line.BaseLine;
import com.gyso.treeview.model.ITraversal;
import com.gyso.treeview.model.NodeModel;
import com.gyso.treeview.model.TreeModel;
import com.gyso.treeview.util.TreeViewLog;
import com.gyso.treeview.util.ViewBox;

/**
 * guaishouN xw 674149099@qq.com
 */
public class TableRightTreeLayoutManager extends TreeLayoutManager {
    private static final String TAG = TableRightTreeLayoutManager.class.getSimpleName();

    public TableRightTreeLayoutManager(Context context, int spaceParentToChild, int spacePeerToPeer, BaseLine baseline) {
        super(context, spaceParentToChild, spacePeerToPeer, baseline);
    }

    @Override
    public void calculateByLayoutAlgorithm(TreeModel<?> mTreeModel) {
        new Table().reconstruction(mTreeModel,Table.LOOSE_TABLE);
    }

    @Override
    public int getTreeLayoutType() {
        return LAYOUT_TYPE_HORIZON_RIGHT;
    }

    @Override
    public void performMeasure(TreeViewContainer treeViewContainer) {
        final TreeModel<?> mTreeModel = treeViewContainer.getTreeModel();
        if (mTreeModel != null) {
            mContentViewBox.clear();
            floorMax.clear();
            deepMax.clear();
            ITraversal<NodeModel<?>> traversal = new ITraversal<NodeModel<?>>() {
                @Override
                public void next(NodeModel<?> next) {
                    measure(next, treeViewContainer);
                }

                @Override
                public void finish() {
                    onManagerFinishMeasureAllNodes(treeViewContainer);
                }
            };
            mTreeModel.doTraversalNodes(traversal);
        }
    }

    @Override
    public void onManagerFinishMeasureAllNodes(TreeViewContainer treeViewContainer) {
        getPadding(treeViewContainer);
        mContentViewBox.bottom += (paddingBox.bottom+paddingBox.top);
        mContentViewBox.right  += (paddingBox.left+paddingBox.right);
        fixedViewBox.setValues(mContentViewBox);
        if(winHeight == 0 || winWidth==0){
            return;
        }
        float scale = 1f*winWidth/winHeight;
        float wr = 1f* mContentViewBox.getWidth()/winWidth;
        float hr = 1f* mContentViewBox.getHeight()/winHeight;
        if(wr>=hr){
            float bh =  mContentViewBox.getWidth()/scale;
            fixedViewBox.bottom = (int)bh;
        }else{
            float bw =  mContentViewBox.getHeight()*scale;
            fixedViewBox.right = (int)bw;
        }
        mFixedDx = (fixedViewBox.getWidth()-mContentViewBox.getWidth())/2;
        mFixedDy = (fixedViewBox.getHeight()-mContentViewBox.getHeight())/2;

        //compute floor start position
        for (int i = 0; i <= floorMax.size(); i++) {
            int fn = (i == floorMax.size())?floorMax.size():floorMax.keyAt(i);
            int preStart = floorStart.get(fn - 1, 0);
            int preMax = floorMax.get(fn - 1, 0);
            int startPos = (fn==0?(mFixedDx + paddingBox.left):spaceParentToChild) + preStart + preMax;
            floorStart.put(fn,startPos);
        }

        //compute deep start position
        for (int i = 0; i <= deepMax.size(); i++) {
            int dn = (i == deepMax.size())?deepMax.size():deepMax.keyAt(i);
            int preStart = deepStart.get(dn - 1, 0);
            int preMax = deepMax.get(dn - 1, 0);
            int startPos = (dn==0?(mFixedDy + paddingBox.top):spacePeerToPeer) + preStart + preMax;
            deepStart.put(dn,startPos);
        }
    }


    private void measure(NodeModel<?> node, TreeViewContainer treeViewContainer) {
        TreeViewHolder<?> currentHolder = treeViewContainer.getTreeViewHolder(node);
        View currentNodeView =  currentHolder==null?null:currentHolder.getView();
        if(currentNodeView==null){
            throw new NullPointerException(" currentNodeView can not be null");
        }
        int preMaxW = floorMax.get(node.floor);
        int curW = currentNodeView.getMeasuredWidth();
        if(preMaxW < curW){
            floorMax.put(node.floor,curW);
            int delta = spaceParentToChild +curW-preMaxW;
            mContentViewBox.right += delta;
        }

        int preMaxH = deepMax.get(node.deep);
        int curH = currentNodeView.getMeasuredHeight();
        if(preMaxH < curH){
            deepMax.put(node.deep,curH);
            int delta = spacePeerToPeer +curH-preMaxH;
            mContentViewBox.bottom += delta;
        }
    }

    @Override
    public void performLayout(final TreeViewContainer treeViewContainer) {
        final TreeModel<?> mTreeModel = treeViewContainer.getTreeModel();
        if (mTreeModel != null) {
            mTreeModel.doTraversalNodes(new ITraversal<NodeModel<?>>() {
                @Override
                public void next(NodeModel<?> next) {
                    layoutNodes(next, treeViewContainer);
                }

                @Override
                public void finish() {
                    onManagerFinishLayoutAllNodes(treeViewContainer);
                }
            });
        }
    }

    @Override
    public ViewBox getTreeLayoutBox() {
        return fixedViewBox;
    }

    private void layoutNodes(NodeModel<?> currentNode, TreeViewContainer treeViewContainer){
        TreeViewLog.e(TAG,"onLayout: "+currentNode);
        TreeViewHolder<?> currentHolder = treeViewContainer.getTreeViewHolder(currentNode);
        View currentNodeView =  currentHolder==null?null:currentHolder.getView();
        int deep = currentNode.deep;
        int floor = currentNode.floor;
        int leafCount = currentNode.leafCount;

        if(currentNodeView==null){
            throw new NullPointerException(" currentNodeView can not be null");
        }

        int currentWidth = currentNodeView.getMeasuredWidth();
        int currentHeight = currentNodeView.getMeasuredHeight();
        int horizonCenterFix = Math.abs(currentHeight - deepMax.get(deep))/2;

        int deltaHeight = 0;
        if(leafCount>1){
            deltaHeight = (deepStart.get(deep + leafCount) - deepStart.get(deep)-currentHeight)/2-horizonCenterFix;
            deltaHeight -= spacePeerToPeer/2;
        }

        int top  = deepStart.get(deep)+horizonCenterFix+deltaHeight+extraDeltaY;
        int left = extraDeltaX+(floor==0?floorStart.get(0):0);
        if(currentNode.getParentNode()!=null){
            NodeModel<?> parentNode = currentNode.getParentNode();
            TreeViewHolder<?> parentHolder = treeViewContainer.getTreeViewHolder(parentNode);
            View parentNodeView =  parentHolder==null?null:parentHolder.getView();
            if(parentNodeView!=null){
                left += parentNodeView.getRight()+floor*spaceParentToChild;
            }else{
                left += paddingBox.left;
            }
        }
        int bottom = top+currentHeight;
        int right = left+currentWidth;

        ViewBox finalLocation = new ViewBox(top, left, bottom, right);
        onManagerLayoutNode(currentNode, currentNodeView, finalLocation, treeViewContainer);
    }

    @Override
    public void onManagerLayoutNode(NodeModel<?> currentNode,
                                    View currentNodeView,
                                    ViewBox finalLocation,
                                    TreeViewContainer treeViewContainer){
        if (!layoutAnimatePrepare(currentNode, currentNodeView, finalLocation, treeViewContainer)) {
            currentNodeView.layout(finalLocation.left, finalLocation.top, finalLocation.right, finalLocation.bottom);
        }
    }

    @Override
    public void onManagerFinishLayoutAllNodes(TreeViewContainer treeViewContainer){
        layoutAnimate(treeViewContainer);
    }
}
