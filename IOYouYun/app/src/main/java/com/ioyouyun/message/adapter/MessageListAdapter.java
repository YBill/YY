package com.ioyouyun.message.adapter;

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
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.utils.TimeUtil;
import com.ioyouyun.wchat.message.ConvType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 卫彪 on 2016/6/21.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

    private LayoutInflater inflater;
    private OnItemClickLitener onItemClickLitener;
    private OnItemLongClickLitener onItemLongClickListener;
    private List<ChatMsgEntity> messageList = new ArrayList<>();
    Random random = new Random(37);
    private Integer[] colors = new Integer[]{
            Color.parseColor("#22b277"), Color.parseColor("#da7294"), Color.parseColor("#2d74c4"),
            Color.parseColor("#a071c4"), Color.parseColor("#cb5372"), Color.parseColor("#ec7b2f"),
            Color.parseColor("#0099e5"), Color.parseColor("#12b2b0"), Color.parseColor("#a071c4")
    };

    public MessageListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setMessageList(List<ChatMsgEntity> messageList) {
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_users, parent, false);
        MessageViewHolder myViewHolder = new MessageViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        ChatMsgEntity entity = messageList.get(position);
        int msgType = entity.getMsgType();
        if (entity.getUnreadMsgNum() > 0) {
            holder.notifyText.setText("" + entity.getUnreadMsgNum());
            holder.notifyText.setVisibility(View.VISIBLE);
        } else
            holder.notifyText.setVisibility(View.GONE);
        holder.nicknameText.setText(entity.getName());
        holder.timeText.setText(TimeUtil.format(entity.getTimestamp()));
        String nickName = entity.getName();
        String end = nickName.substring(nickName.length() - 1, nickName.length());
        TextDrawable drawable;
        if(ConvType.single == entity.getConvType()){
            drawable = TextDrawable.builder().buildRound(end, colors[random.nextInt(9)]);
        } else if(ConvType.room == entity.getConvType()){
            drawable = TextDrawable.builder().buildRoundRect("聊", colors[0], 10);
        } else if(ConvType.group == entity.getConvType()){
            drawable = TextDrawable.builder().buildRoundRect("群", colors[1], 10);
        } else {
            drawable = TextDrawable.builder().buildRoundRect(end, colors[2], 10);
        }
        holder.avatarImage.setImageDrawable(drawable);

        switch (msgType) {
            case ChatMsgEntity.CHAT_TYPE_RECV_TEXT:
            case ChatMsgEntity.CHAT_TYPE_SEND_TEXT:
                holder.messageText.setText(entity.getText());
                break;
            case ChatMsgEntity.CHAT_TYPE_RECV_AUDIO:
            case ChatMsgEntity.CHAT_TYPE_SEND_AUDIO:
                holder.messageText.setText("[语音]");
                break;
            case ChatMsgEntity.CHAT_TYPE_RECV_IMAGE:
            case ChatMsgEntity.CHAT_TYPE_SEND_IMAGE:
                holder.messageText.setText("[图片]");
                break;
            case ChatMsgEntity.CHAT_TYPE_RECV_FILE:
            case ChatMsgEntity.CHAT_TYPE_SEND_FILE:
                holder.messageText.setText("[文件]");
                break;
            case ChatMsgEntity.CHAT_TYPE_RECV_EMOJI:
            case ChatMsgEntity.CHAT_TYPE_SEND_EMOJI:
                holder.messageText.setText("[表情]");
                break;
            case ChatMsgEntity.CHAT_TYPE_RECV_POS:
            case ChatMsgEntity.CHAT_TYPE_SEND_POS:
                holder.messageText.setText("[位置]");
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickLitener != null) {
                    int position = holder.getLayoutPosition();
                    onItemClickLitener.onItemClick(holder.itemView, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    int position = holder.getLayoutPosition();
                    onItemLongClickListener.onItemClick(holder.itemView, position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public interface OnItemLongClickLitener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    public void setOnItemLongClickListener(OnItemLongClickLitener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarImage;
        TextView nicknameText;
        TextView messageText;
        TextView timeText;
        TextView notifyText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            avatarImage = (ImageView) itemView.findViewById(R.id.iv_avatar);
            nicknameText = (TextView) itemView.findViewById(R.id.tv_nickname);
            messageText = (TextView) itemView.findViewById(R.id.tv_message);
            timeText = (TextView) itemView.findViewById(R.id.tv_time);
            timeText.setVisibility(View.VISIBLE);
            notifyText = (TextView) itemView.findViewById(R.id.tv_notify);
        }
    }
}
