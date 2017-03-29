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
import com.ioyouyun.group.model.ChatRoomInfoEntity;
import com.ioyouyun.group.model.GroupMemberEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 卫彪 on 2016/11/22.
 * GroupMemberAdapter用listview实现,这里直接改为RecycleView
 * 比较恶心的是聊天室和群组返回的数据格式不一致,这里直接在群组的基础上兼容聊天室了
 */
public class GroupMemberAdapter2 extends RecyclerView.Adapter<GroupMemberAdapter2.GroupMemberViewHolder> {

    private LayoutInflater inflater;
    private int currentRole;
    private OnItemClickLitener onItemClickLitener;
    private List<?> groupMemberEntityList = new ArrayList<>();
    Random random = new Random(37);
    private Integer[] colors = new Integer[]{
            Color.parseColor("#22b277"), Color.parseColor("#da7294"), Color.parseColor("#2d74c4"),
            Color.parseColor("#a071c4"), Color.parseColor("#cb5372"), Color.parseColor("#ec7b2f"),
            Color.parseColor("#0099e5"), Color.parseColor("#12b2b0"), Color.parseColor("#a071c4")
    };

    public GroupMemberAdapter2(Context context, int currentRole) {
        inflater = LayoutInflater.from(context);
        this.currentRole = currentRole;
    }

    public void setGroupMemberList(List<?> groupMemberEntityList) {
        this.groupMemberEntityList = groupMemberEntityList;
    }

    @Override
    public GroupMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_users2, parent, false);
        GroupMemberViewHolder myViewHolder = new GroupMemberViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final GroupMemberViewHolder holder, int position) {
        String nickName = "";
        int role = 0;
        boolean gag = false;
        Object entity = groupMemberEntityList.get(position);
        if(entity instanceof GroupMemberEntity){
            GroupMemberEntity groupMemberEntity = (GroupMemberEntity) entity;
            nickName = groupMemberEntity.getNickname();
            role = groupMemberEntity.getRole();
            gag = groupMemberEntity.getGroupuser_props() != null ? groupMemberEntity.getGroupuser_props().isGag() : false;
        } else if(entity instanceof ChatRoomInfoEntity){
            ChatRoomInfoEntity chatRoomInfoEntity = (ChatRoomInfoEntity) entity;
            nickName = chatRoomInfoEntity.getUid();
            role = chatRoomInfoEntity.getRole();
            gag = chatRoomInfoEntity.getProps() != null ? chatRoomInfoEntity.getProps().isGag() : false;
        } else {
            try {
                throw new ClassNotFoundException("格式不匹配");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(gag && (currentRole == 4 || currentRole == 3)){
            // 群主和管理员知道禁言，用户自己屏蔽
            holder.gagStatus.setText("已禁言");
        }else{
            holder.gagStatus.setText("");
        }
        if(role == 4){
            holder.identityText.setText("群主");
            holder.identityText.setTextColor(Color.parseColor("#FF0000"));
        } else if(role == 3){
            holder.identityText.setText("管理员");
            holder.identityText.setTextColor(Color.parseColor("#FF6600"));
        } else{
            holder.identityText.setText("");
        }

        holder.groupNameText.setText(nickName);
        String end = nickName.substring(nickName.length() - 1, nickName.length());
        TextDrawable drawable = TextDrawable.builder().buildRound(end, colors[random.nextInt(9)]);
        holder.avatarImage.setImageDrawable(drawable);

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
        return groupMemberEntityList.size();
    }

    public Object getItem(int position){
        return groupMemberEntityList.get(position);
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    class GroupMemberViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarImage;
        TextView groupNameText;
        TextView identityText;
        TextView gagStatus;

        public GroupMemberViewHolder(View itemView) {
            super(itemView);
            avatarImage = (ImageView) itemView.findViewById(R.id.iv_avatar);
            groupNameText = (TextView) itemView.findViewById(R.id.tv_nickname);
            identityText = (TextView) itemView.findViewById(R.id.tv_right);
            identityText.setVisibility(View.VISIBLE);
            gagStatus = (TextView) itemView.findViewById(R.id.tv_middle);
            gagStatus.setVisibility(View.VISIBLE);
        }
    }
}
