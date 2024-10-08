package com.gyso.treeview.layout;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

import com.gyso.treeview.TreeViewContainer;
import com.gyso.treeview.adapter.TreeViewHolder;
import com.gyso.treeview.line.BaseLine;
import com.gyso.treeview.model.NodeModel;
import com.gyso.treeview.model.TreeModel;
import com.gyso.treeview.util.ViewBox;

import java.util.LinkedList;

public class BoxHorizonLeftAndRightLayoutManager extends BoxRightTreeLayoutManager {
    private static final String TAG = BoxHorizonLeftAndRightLayoutManager.class.getSimpleName();
    private boolean isJustCalculate;

    public BoxHorizonLeftAndRightLayoutManager(Context context, int spaceParentToChild, int spacePeerToPeer, BaseLine baseline) {
        super(context, spaceParentToChild, spacePeerToPeer, baseline);
    }

    @Override
    public int getTreeLayoutType() {
        return LAYOUT_TYPE_HORIZON_LEFT_AND_RIGHT;
    }

    @Override
    public void onManagerFinishMeasureAllNodes(TreeViewContainer treeViewContainer) {
        getPadding(treeViewContainer);
        mContentViewBox.bottom += (paddingBox.bottom + paddingBox.top);
        extraDeltaX = mContentViewBox.right;
        mContentViewBox.right += (paddingBox.left + paddingBox.right + extraDeltaX);
        fixedViewBox.setValues(mContentViewBox);
        if (winHeight == 0 || winWidth == 0) {
            return;
        }
        float scale = 1f * winWidth / winHeight;
        float wr = 1f * mContentViewBox.getWidth() / winWidth;
        float hr = 1f * mContentViewBox.getHeight() / winHeight;
        if (wr >= hr) {
            float bh = mContentViewBox.getWidth() / scale;
            fixedViewBox.bottom = (int) bh;
        } else {
            float bw = mContentViewBox.getHeight() * scale;
            fixedViewBox.right = (int) bw;
        }
        mFixedDx = fixedViewBox.getWidth() / 2;
        mFixedDy = (fixedViewBox.getHeight() - mContentViewBox.getHeight()) / 2;
    }

    @Override
    public void performLayout(final TreeViewContainer treeViewContainer) {
        isJustCalculate = true;
        super.performLayout(treeViewContainer);
        isJustCalculate = false;
        final TreeModel<?> mTreeModel = treeViewContainer.getTreeModel();
        if (mTreeModel != null) {
            NodeModel<?> rootNode = mTreeModel.getRootNode();
            TreeViewHolder<?> rootNodeHolder = treeViewContainer.getTreeViewHolder(rootNode);
            View rootNodeView = rootNodeHolder == null ? null : rootNodeHolder.getView();
            if (rootNodeView == null) {
                throw new NullPointerException(" rootNodeView can not be null");
            }

            setNodePos(rootNode, treeViewContainer);

            int rootCx = rootNodeView.getLeft() + rootNodeView.getMeasuredWidth() / 2;
            int rootCy = rootNodeView.getTop() + rootNodeView.getMeasuredHeight() / 2;
            //divide equally by two
            LinkedList<? extends NodeModel<?>> rootNodeChildNodes = rootNode.getChildNodes();
            Point divideDy = getDivideDy(rootNode, treeViewContainer);
            int centerAy = divideDy.x;
            int centerBy = divideDy.y;
            int divider = rootNodeChildNodes.size() / 2;
            int count = 0;
            for (NodeModel<?> node : rootNodeChildNodes) {
                if (count < divider) {
                    //move to mid
                    node.traverseIncludeSelf(n -> moveDy(n, treeViewContainer, (rootCy - centerAy)));
                } else {
                    //move to other side
                    node.traverseIncludeSelf(n -> mirrorByCxDy(n, treeViewContainer, rootCx, (rootCy - centerBy)));
                }
                count++;
                setNodePos(node, treeViewContainer);
            }
            onManagerFinishLayoutAllNodes(treeViewContainer);
        }
    }

    private void setNodePos(NodeModel<?> node, TreeViewContainer treeViewContainer) {
        TreeViewHolder<?> nodeHolder = treeViewContainer.getTreeViewHolder(node);
        View nodeView = nodeHolder == null ? null : nodeHolder.getView();

        if (nodeView == null) {
            throw new NullPointerException(" nodeView can not be null");
        }

        node.setX(nodeView.getX());
        node.setY(nodeView.getY());
    }

    private Point getDivideDy(NodeModel<?> rootNode, TreeViewContainer treeViewContainer) {
        LinkedList<? extends NodeModel<?>> rootNodeChildNodes = rootNode.getChildNodes();
        int divider = rootNodeChildNodes.size() / 2;
        int count = 0;
        int minA, maxA, minB, maxB;
        minA = minB = Integer.MAX_VALUE;
        maxA = maxB = Integer.MIN_VALUE;
        for (NodeModel<?> currentNode : rootNodeChildNodes) {
            TreeViewHolder<?> currentHolder = treeViewContainer.getTreeViewHolder(currentNode);
            View currentNodeView = currentHolder == null ? null : currentHolder.getView();
            if (currentNodeView == null) {
                throw new NullPointerException(" currentNodeView can not be null");
            }
            int left = currentNodeView.getLeft();
            int top = currentNodeView.getTop();
            int currentHeight = currentNodeView.getMeasuredHeight();
            int currentWidth = currentNodeView.getMeasuredWidth();
            if (count < divider) {
                minA = Math.min(minA, top);
                maxA = Math.max(maxA, top + currentHeight);
            } else {
                minB = Math.min(minB, top);
                maxB = Math.max(maxB, top + currentHeight);
            }
            count++;
        }
        return new Point((maxA + minA) / 2, (maxB + minB) / 2);
    }

    private void moveDy(NodeModel<?> currentNode, TreeViewContainer treeViewContainer, int deltaY) {
        TreeViewHolder<?> currentHolder = treeViewContainer.getTreeViewHolder(currentNode);
        View currentNodeView = currentHolder == null ? null : currentHolder.getView();
        if (currentNodeView == null) {
            throw new NullPointerException(" currentNodeView can not be null");
        }
        currentHolder.setHolderLayoutType(LAYOUT_TYPE_HORIZON_RIGHT);
        int currentWidth = currentNodeView.getMeasuredWidth();
        int currentHeight = currentNodeView.getMeasuredHeight();
        int left = currentNodeView.getLeft();
        int right = left + currentWidth;
        int top = deltaY + currentNodeView.getTop();
        int bottom = top + currentHeight;
        ViewBox finalLocation = new ViewBox(top, left, bottom, right);
        onManagerLayoutNode(currentNode, currentNodeView, finalLocation, treeViewContainer);
    }

    private void mirrorByCxDy(NodeModel<?> currentNode, TreeViewContainer treeViewContainer, int centerX, int deltaY) {
        TreeViewHolder<?> currentHolder = treeViewContainer.getTreeViewHolder(currentNode);
        View currentNodeView = currentHolder == null ? null : currentHolder.getView();
        if (currentNodeView == null) {
            throw new NullPointerException(" currentNodeView can not be null");
        }
        currentHolder.setHolderLayoutType(LAYOUT_TYPE_HORIZON_LEFT);
        int right = centerX * 2 - currentNodeView.getLeft();
        int left = centerX * 2 - currentNodeView.getRight();
        int top = deltaY + currentNodeView.getTop();
        int bottom = deltaY + currentNodeView.getBottom();
        ViewBox finalLocation = new ViewBox(top, left, bottom, right);
        onManagerLayoutNode(currentNode, currentNodeView, finalLocation, treeViewContainer);
    }

    @Override
    public void onManagerLayoutNode(NodeModel<?> currentNode, View currentNodeView, ViewBox finalLocation, TreeViewContainer treeViewContainer) {
        if (isJustCalculate) {
            currentNodeView.layout(finalLocation.left, finalLocation.top, finalLocation.right, finalLocation.bottom);
            return;
        }
        if (!layoutAnimatePrepare(currentNode, currentNodeView, finalLocation, treeViewContainer)) {
            currentNodeView.layout(finalLocation.left, finalLocation.top, finalLocation.right, finalLocation.bottom);
        }
    }

    @Override
    public void onManagerFinishLayoutAllNodes(TreeViewContainer treeViewContainer) {
        if (!isJustCalculate) {
            super.onManagerFinishLayoutAllNodes(treeViewContainer);
        }
    }
}
