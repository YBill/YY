package com.ioyouyun.group;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.ioyouyun.group.model.ChatRoomInfoEntity;
import com.ioyouyun.group.model.GroupMemberEntity;

import java.util.List;

/**
 * Created by 卫彪 on 2016/11/22.
 */
public class TitleDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;// 画笔
    private Rect mBounds;//用于存放测量文字Rect
    private int mTitlePaddingLeft;//title据左边距离
    private int mTitleHeight;//title的高
    private static int mTitleFontSize;//title字体大小
    private static final int mTitleFontColor = Color.parseColor("#FF000000");//title字体颜色
    private static final int mTitleBgColor = Color.parseColor("#FFDFDFDF");//title背景颜色

    private List<?> groupMemberEntityList;

    public TitleDividerItemDecoration(Context context) {
        mPaint = new Paint();
        mBounds = new Rect();
        mTitlePaddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
        mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
    }

    public void setGroupMemberEntityList(List<?> groupMemberEntityList) {
        this.groupMemberEntityList = groupMemberEntityList;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (position > -1) {
            if (position == 0) {
                if(groupMemberEntityList != null && groupMemberEntityList.size() > 0)
                    outRect.set(0, mTitleHeight, 0, 0);
            } else {
                if(groupMemberEntityList != null && groupMemberEntityList.size() > 1){
                    int role = -1;
                    int prevRole = -1;
                    if(groupMemberEntityList.get(position) instanceof GroupMemberEntity){
                        GroupMemberEntity groupMemberEntity = (GroupMemberEntity) groupMemberEntityList.get(position);
                        GroupMemberEntity groupMemberEntity2 = (GroupMemberEntity)  groupMemberEntityList.get(position - 1);
                        role = groupMemberEntity.getRole();
                        prevRole = groupMemberEntity2.getRole();
                    } else if(groupMemberEntityList.get(position) instanceof ChatRoomInfoEntity){
                        ChatRoomInfoEntity chatRoomInfoEntity = (ChatRoomInfoEntity) groupMemberEntityList.get(position);
                        ChatRoomInfoEntity chatRoomInfoEntity2 = (ChatRoomInfoEntity)  groupMemberEntityList.get(position - 1);
                        role = chatRoomInfoEntity.getRole();
                        prevRole = chatRoomInfoEntity2.getRole();
                    }
                    if(role == 1 && (prevRole == 3 || prevRole == 4)){
                        outRect.set(0, mTitleHeight, 0, 0);
                    }else {
                        outRect.set(0, 0, 0, 0);
                    }
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewLayoutPosition();
            if (position > -1) {
                if(position == 0){
                    if(groupMemberEntityList != null && groupMemberEntityList.size() > 0)
                        drawTitleArea(canvas, left, right, child, params, "群主、管理员");
                } else {
                    if(groupMemberEntityList != null && groupMemberEntityList.size() > 1){
                        int role = -1;
                        int prevRole = -1;
                        if(groupMemberEntityList.get(position) instanceof GroupMemberEntity){
                            GroupMemberEntity groupMemberEntity = (GroupMemberEntity) groupMemberEntityList.get(position);
                            GroupMemberEntity groupMemberEntity2 = (GroupMemberEntity)  groupMemberEntityList.get(position - 1);
                            role = groupMemberEntity.getRole();
                            prevRole = groupMemberEntity2.getRole();
                        } else if(groupMemberEntityList.get(position) instanceof ChatRoomInfoEntity){
                            ChatRoomInfoEntity chatRoomInfoEntity = (ChatRoomInfoEntity) groupMemberEntityList.get(position);
                            ChatRoomInfoEntity chatRoomInfoEntity2 = (ChatRoomInfoEntity)  groupMemberEntityList.get(position - 1);
                            role = chatRoomInfoEntity.getRole();
                            prevRole = chatRoomInfoEntity2.getRole();
                        }
                        if(role == 1 && (prevRole == 3 || prevRole == 4)){
                            drawTitleArea(canvas, left, right, child, params, "普通成员");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    /**
     * 画title
     * @param canvas
     * @param left
     * @param right
     * @param child
     * @param params
     * @param text
     */
    private void drawTitleArea(Canvas canvas, int left, int right, View child, RecyclerView.LayoutParams params, String text) {
        mPaint.setColor(mTitleBgColor);
        canvas.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(mTitleFontColor);
        mPaint.getTextBounds(text, 0, text.length(), mBounds);
        canvas.drawText(text, child.getPaddingLeft() + mTitlePaddingLeft, child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2), mPaint);
    }

}