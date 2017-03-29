package com.ioyouyun.group.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.ioyouyun.R;
import com.ioyouyun.group.model.GroupInfoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫彪 on 2016/8/9.
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {

    private LayoutInflater inflater;
    private OnItemClickLitener onItemClickLitener;
    private List<GroupInfoEntity> groupList = new ArrayList<>();
    private Integer[] colors = new Integer[]{
            Color.parseColor("#22b277"), Color.parseColor("#da7294"), Color.parseColor("#2d74c4")
    };

    public GroupListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setGroupList(List<GroupInfoEntity> groupList) {
        this.groupList = groupList;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_users, parent, false);
        GroupViewHolder myViewHolder = new GroupViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder holder, int position) {
        GroupInfoEntity entity = groupList.get(position);
        holder.groupNameText.setText(entity.getName());
        holder.groipIdText.setText(entity.getGid());
        String role = "已加入";
        if(entity.getRole() == 4){
            role = "已创建";
        }
        holder.createOrAddText.setText(role);

        String text = "";
        int colorIndex = 0;
        if(3 == entity.getCat1()){
            text = "聊";
            colorIndex = 0;
        } else if(2 == entity.getCat1()){
            text = "群";
            colorIndex = 1;
        }else if(0 == entity.getCat1()){
            text = "临";
            colorIndex = 2;
        }
        TextDrawable drawable2 = TextDrawable.builder().buildRoundRect(text, colors[colorIndex], 10);
        holder.avatarImage.setImageDrawable(drawable2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickLitener != null) {
                    int position = holder.getLayoutPosition();
                    onItemClickLitener.onItemClick(holder.itemView, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public GroupInfoEntity getItem(int position){
        return groupList.get(position);
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarImage;
        TextView groupNameText;
        TextView groipIdText;
        TextView createOrAddText;
        TextView notifyText;

        public GroupViewHolder(View itemView) {
            super(itemView);
            avatarImage = (ImageView) itemView.findViewById(R.id.iv_avatar);
            groupNameText = (TextView) itemView.findViewById(R.id.tv_nickname);
            groipIdText = (TextView) itemView.findViewById(R.id.tv_message);
            createOrAddText = (TextView) itemView.findViewById(R.id.tv_time);
            createOrAddText.setVisibility(View.VISIBLE);
            notifyText = (TextView) itemView.findViewById(R.id.tv_notify);
        }
    }
}
