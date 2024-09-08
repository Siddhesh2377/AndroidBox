package com.gyso.treeview.line;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.View;

import com.gyso.treeview.adapter.DrawInfo;
import com.gyso.treeview.adapter.TreeViewHolder;
import com.gyso.treeview.cache_pool.PointPool;
import com.gyso.treeview.layout.TreeLayoutManager;
import com.gyso.treeview.model.NodeModel;
import com.gyso.treeview.util.DensityUtils;

/**
 * @Author: 怪兽N
 * @Time: 2021/5/8  17:41
 * @Email: 674149099@qq.com
 * @WeChat: guaishouN
 * @Describe:
 * DashPathEffect line
 */
public class DashLine extends BaseLine {
    public static final int DEFAULT_LINE_WIDTH_DP = 3;
    private int lineColor = Color.parseColor("#E57373");
    private int lineWidth = DEFAULT_LINE_WIDTH_DP;
    private final static DashPathEffect effect = new DashPathEffect(new float[]{20,10},10);
    public DashLine() {
        super();
    }

    public DashLine(int lineColor, int lineWidth_dp) {
        this();
        this.lineColor = lineColor;
        this.lineWidth = lineWidth_dp;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public void draw(DrawInfo drawInfo) {
        Canvas canvas = drawInfo.getCanvas();
        TreeViewHolder<?> fromHolder = drawInfo.getFromHolder();
        TreeViewHolder<?> toHolder = drawInfo.getToHolder();
        Paint mPaint = drawInfo.getPaint();
        Path mPath = drawInfo.getPath();
        int holderLayoutType = toHolder.getHolderLayoutType();
        int spacePeerToPeer = drawInfo.getSpacePeerToPeer();
        int spaceParentToChild = drawInfo.getSpaceParentToChild();

        //get view and node
        View fromView = fromHolder.getView();
        NodeModel<?> fromNode = fromHolder.getNode();
        View toView = toHolder.getView();
        NodeModel<?> toNode = toHolder.getNode();
        Context context = fromView.getContext();

        PointF startPoint,point1,endPoint,point2;
        if(holderLayoutType== TreeLayoutManager.LAYOUT_TYPE_HORIZON_RIGHT){
            startPoint = PointPool.obtain(fromView.getRight(),(fromView.getTop()+fromView.getBottom())/2f);
            point1 = PointPool.obtain(startPoint.x+DensityUtils.dp2px(context,15),startPoint.y);
            endPoint =  PointPool.obtain(toView.getLeft(),(toView.getTop()+toView.getBottom())/2f);
            point2 = PointPool.obtain(startPoint.x,endPoint.y);
        }else if(holderLayoutType== TreeLayoutManager.LAYOUT_TYPE_HORIZON_LEFT){
            startPoint = PointPool.obtain(fromView.getLeft(),(fromView.getTop()+fromView.getBottom())/2f);
            point1 = PointPool.obtain(startPoint.x-DensityUtils.dp2px(context,15),startPoint.y);
            endPoint =  PointPool.obtain(toView.getRight(),(toView.getTop()+toView.getBottom())/2f);
            point2 = PointPool.obtain(startPoint.x,endPoint.y);
        }else if(holderLayoutType== TreeLayoutManager.LAYOUT_TYPE_VERTICAL_DOWN){
            startPoint = PointPool.obtain((fromView.getLeft()+fromView.getRight())/2f,fromView.getBottom());
            point1 = PointPool.obtain(startPoint.x,startPoint.y+DensityUtils.dp2px(context,15));
            endPoint =  PointPool.obtain((toView.getLeft()+toView.getRight())/2f,toView.getTop());
            point2 = PointPool.obtain(endPoint.x,startPoint.y);
        }else if(holderLayoutType== TreeLayoutManager.LAYOUT_TYPE_VERTICAL_UP){
            startPoint = PointPool.obtain((fromView.getLeft()+fromView.getRight())/2f,fromView.getTop());
            point1 = PointPool.obtain(startPoint.x,startPoint.y-DensityUtils.dp2px(context,15));
            endPoint =  PointPool.obtain((toView.getLeft()+toView.getRight())/2f,toView.getBottom());
            point2 = PointPool.obtain(endPoint.x,startPoint.y);
        }else{
            super.draw(drawInfo);
            return;
        }

        mPaint.reset();
        mPath.reset();
        //set paint
        mPaint.setColor(lineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DensityUtils.dp2px(context,lineWidth));
        mPaint.setAntiAlias(true);
        mPaint.setPathEffect(effect);

        mPath.moveTo(startPoint.x,startPoint.y);
        mPath.cubicTo(
                point1.x,point1.y,
                point2.x,point2.y,
                endPoint.x,endPoint.y);

        //do not forget release
        PointPool.free(startPoint);
        PointPool.free(point1);
        PointPool.free(point2);
        PointPool.free(endPoint);
        //draw
        canvas.drawPath(mPath,mPaint);
    }
}
