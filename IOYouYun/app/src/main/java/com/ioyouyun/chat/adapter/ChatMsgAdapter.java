package com.ioyouyun.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.ioyouyun.R;
import com.ioyouyun.chat.ChatBigImageActivity;
import com.ioyouyun.chat.LocationActivity;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.chat.model.FileEntity;
import com.ioyouyun.chat.util.VoicePlay;
import com.ioyouyun.chat.widget.MaskView;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.TimeUtil;
import com.ioyouyun.wchat.message.ConvType;
import com.melink.baseframe.utils.DensityUtils;
import com.melink.bqmmsdk.widget.BQMMMessageText;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 卫彪 on 2016/6/13.
 */
public class ChatMsgAdapter extends BaseAdapter {

    public final int screenWidth1p3 = FunctionUtil.mScreenWidth / 3;
    public final int screenWidth1p4 = FunctionUtil.mScreenWidth / 4;
    private Context context;
    private LayoutInflater inflater;
    private List<ChatMsgEntity> msgEntityList = new ArrayList<>();
    private Integer[] colors = new Integer[]{
            Color.parseColor("#cb5372"), Color.parseColor("#2d74c4")
    };

    public ChatMsgAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setMsgEntityList(List<ChatMsgEntity> list) {
        if (list != null)
            this.msgEntityList = list;
    }

    private FileClickListener fileClickListener;
    public void setOnFileClickListener(FileClickListener fileClickListener){
        this.fileClickListener = fileClickListener;
    }

    public interface FileClickListener{
        void download(ChatMsgEntity entity);
    }

    /**
     * 得到Item的类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        ChatMsgEntity entity = getItem(position);
        return entity.getMsgType();
    }

    /**
     * Item类型的总数
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return ChatMsgEntity.CHAT_TYPE_NUM;
    }

    @Override
    public int getCount() {
        return msgEntityList.size();
    }

    @Override
    public ChatMsgEntity getItem(int position) {
        return msgEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatMsgEntity entity = getItem(position);
        int viewType = getItemViewType(position);
        if (viewType == ChatMsgEntity.CHAT_TYPE_RECV_TEXT) {
            TextHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof TextHolder)) {
                holder = new TextHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_text_left, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.contentText = (TextView) convertView.findViewById(R.id.tv_chat_content);
                convertView.setTag(holder);
            } else {
                holder = (TextHolder) convertView.getTag();
            }
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            if (ConvType.single == entity.getConvType()){
                drawable = TextDrawable.builder().buildRound(end, colors[0]);
            } else {
                drawable = TextDrawable.builder().buildRoundRect(end, colors[0], 10);
            }
            holder.avatarImage.setImageDrawable(drawable);*/
            holder.contentText.setText(entity.getText());

        } else if (viewType == ChatMsgEntity.CHAT_TYPE_SEND_TEXT) {
            TextHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof TextHolder)) {
                holder = new TextHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_text_right, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.contentText = (TextView) convertView.findViewById(R.id.tv_chat_content);
                convertView.setTag(holder);
            } else {
                holder = (TextHolder) convertView.getTag();
            }
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            drawable = TextDrawable.builder().buildRound(end, colors[1]);
            holder.avatarImage.setImageDrawable(drawable);*/
            holder.contentText.setText(entity.getText());
        } else if (viewType == ChatMsgEntity.CHAT_TYPE_RECV_EMOJI) {
            EmojiHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof EmojiHolder)) {
                holder = new EmojiHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_emoji_left, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.contentText = (BQMMMessageText) convertView.findViewById(R.id.tv_chat_content);
                holder.contentText.getBackground().setAlpha(0);
                holder.contentText.setBigEmojiShowSize(DensityUtils.dip2px(context, 100));
                holder.contentText.setSmallEmojiShowSize(DensityUtils.dip2px(context, 20));
                convertView.setTag(holder);
            } else {
                holder = (EmojiHolder) convertView.getTag();
            }
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            if (ConvType.single == entity.getConvType()){
                drawable = TextDrawable.builder().buildRound(end, colors[0]);
            } else {
                drawable = TextDrawable.builder().buildRoundRect(end, colors[0], 10);
            }
            holder.avatarImage.setImageDrawable(drawable);*/
            holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));

            JSONArray array = new JSONArray();
            array.put(entity.getText());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(array);
            holder.contentText.showSticker(jsonArray);
        } else if (viewType == ChatMsgEntity.CHAT_TYPE_SEND_EMOJI) {
            EmojiHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof EmojiHolder)) {
                holder = new EmojiHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_emoji_right, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.contentText = (BQMMMessageText) convertView.findViewById(R.id.tv_chat_content);
                holder.contentText.getBackground().setAlpha(0);
                holder.contentText.setBigEmojiShowSize(DensityUtils.dip2px(context, 100));
                holder.contentText.setSmallEmojiShowSize(DensityUtils.dip2px(context, 20));
                convertView.setTag(holder);
            } else {
                holder = (EmojiHolder) convertView.getTag();
            }
            holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            drawable = TextDrawable.builder().buildRound(end, colors[1]);
            holder.avatarImage.setImageDrawable(drawable);*/

            JSONArray array = new JSONArray();
            array.put(entity.getText());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(array);
            holder.contentText.showSticker(jsonArray);
        } else if (viewType == ChatMsgEntity.CHAT_TYPE_RECV_AUDIO) {
            AudioHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof AudioHolder)) {
                holder = new AudioHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_audio_left, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.audioText = (TextView) convertView.findViewById(R.id.tv_chat_audio);
                holder.durationText = (TextView) convertView.findViewById(R.id.tv_duration);
                holder.unreadFlag = (ImageView) convertView.findViewById(R.id.iv_unread_flag);
                convertView.setTag(holder);
            } else {
                holder = (AudioHolder) convertView.getTag();
            }
            setItemClickListener(holder.audioText, position);
            if(entity.isAudioRead()){
                holder.unreadFlag.setVisibility(View.GONE);
            }else{
                holder.unreadFlag.setVisibility(View.VISIBLE);
            }
            if (entity.isAudioPlaying()) {
                holder.audioText.setSelected(true);
                setAudioDrawableAnim(holder.audioText, R.drawable.chating_audio_anim_left, 1);
            } else {
                holder.audioText.setSelected(false);
                setAudioDrawableUnAnim(holder.audioText, R.drawable.chat_icon_speech_left3, 1);
            }
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            int viewWidth = 0;
            try {
                viewWidth = FunctionUtil.calAudioViewWidth(Integer.parseInt(entity.getAudioTime()));
            } catch (NumberFormatException e) {
                e.printStackTrace();

            }
            holder.audioText.setWidth(viewWidth);
            holder.durationText.setText(entity.getAudioTime() + "\"");
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            if (ConvType.single == entity.getConvType()){
                drawable = TextDrawable.builder().buildRound(end, colors[0]);
            } else {
                drawable = TextDrawable.builder().buildRoundRect(end, colors[0], 10);
            }
            holder.avatarImage.setImageDrawable(drawable);*/
        } else if (viewType == ChatMsgEntity.CHAT_TYPE_SEND_AUDIO) {
            AudioHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof AudioHolder)) {
                holder = new AudioHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_audio_right, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.audioText = (TextView) convertView.findViewById(R.id.tv_chat_audio);
                holder.durationText = (TextView) convertView.findViewById(R.id.tv_duration);
                convertView.setTag(holder);
            } else {
                holder = (AudioHolder) convertView.getTag();
            }
            setItemClickListener(holder.audioText, position);
            if (entity.isAudioPlaying()) {
                holder.audioText.setSelected(true);
                setAudioDrawableAnim(holder.audioText, R.drawable.chating_audio_anim_right, 2);
            } else {
                holder.audioText.setSelected(false);
                setAudioDrawableUnAnim(holder.audioText, R.drawable.chat_icon_speech_right3, 2);
            }
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            int viewWidth = FunctionUtil.calAudioViewWidth(Integer.parseInt(entity.getAudioTime()));
            holder.audioText.setWidth(viewWidth);
            holder.durationText.setText(entity.getAudioTime() + "\"");
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            drawable = TextDrawable.builder().buildRound(end, colors[1]);
            holder.avatarImage.setImageDrawable(drawable);*/
        } else if (viewType == ChatMsgEntity.CHAT_TYPE_RECV_IMAGE) {
            ImageHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof ImageHolder)) {
                holder = new ImageHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_image_left, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.imgParent = (ViewGroup) convertView.findViewById(R.id.img_parent);
                holder.imageLayout = convertView.findViewById(R.id.image_layout);
                convertView.setTag(holder);
            } else {
                holder = (ImageHolder) convertView.getTag();
            }
            setItemClickListener(holder.imageLayout, position);
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            if (ConvType.single == entity.getConvType()){
                drawable = TextDrawable.builder().buildRound(end, colors[0]);
            } else {
                drawable = TextDrawable.builder().buildRoundRect(end, colors[0], 10);
            }
            holder.avatarImage.setImageDrawable(drawable);*/
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(entity.getImgThumbnail())) {
                Bitmap bitmap = BitmapFactory.decodeFile(entity.getImgThumbnail());
                MaskView imgView = new MaskView(
                        context, bitmap,
                        (NinePatchDrawable) context.getResources().getDrawable(R.drawable.chat_img_left_mask),
                        screenWidth1p3, screenWidth1p3,
                        screenWidth1p4, screenWidth1p4);
                holder.imgParent.removeAllViews();
                holder.imgParent.addView(imgView);
                ViewGroup.LayoutParams layoutParams = imgView.getLayoutParams();
                layoutParams.height = imgView.getMaskViewSize() != null ?
                        imgView.getMaskViewSize().viewHeight : layoutParams.height;
                layoutParams.width = imgView.getMaskViewSize() != null ?
                        imgView.getMaskViewSize().viewWidth : layoutParams.width;
            }

        } else if (viewType == ChatMsgEntity.CHAT_TYPE_SEND_IMAGE) {
            ImageHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof ImageHolder)) {
                holder = new ImageHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_image_right, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.imgParent = (ViewGroup) convertView.findViewById(R.id.img_parent);
                holder.imageLayout = convertView.findViewById(R.id.image_layout);
                convertView.setTag(holder);
            } else {
                holder = (ImageHolder) convertView.getTag();
            }
            setItemClickListener(holder.imageLayout, position);
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            drawable = TextDrawable.builder().buildRound(end, colors[1]);
            holder.avatarImage.setImageDrawable(drawable);*/
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(entity.getImgThumbnail())) {
                Bitmap bitmap = BitmapFactory.decodeFile(entity.getImgThumbnail());
                MaskView imgView = new MaskView(
                        context, bitmap,
                        (NinePatchDrawable) context.getResources().getDrawable(R.drawable.chat_img_right_mask),
                        screenWidth1p3, screenWidth1p3,
                        screenWidth1p4, screenWidth1p4);
                holder.imgParent.removeAllViews();
                holder.imgParent.addView(imgView);
                ViewGroup.LayoutParams layoutParams = imgView.getLayoutParams();
                layoutParams.height = imgView.getMaskViewSize() != null ?
                        imgView.getMaskViewSize().viewHeight : layoutParams.height;
                layoutParams.width = imgView.getMaskViewSize() != null ?
                        imgView.getMaskViewSize().viewWidth : layoutParams.width;
            }

        } else if (viewType == ChatMsgEntity.CHAT_TYPE_RECV_FILE) {
            FileHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof FileHolder)) {
                holder = new FileHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_file_left, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.fileLayout = (RelativeLayout) convertView.findViewById(R.id.rl_file);
                holder.fileNameText = (TextView) convertView.findViewById(R.id.tv_file_name);
                holder.fileSizeText = (TextView) convertView.findViewById(R.id.tv_file_size);
                holder.fileStatusText = (TextView) convertView.findViewById(R.id.tv_file_status);
                holder.fileProgressText = (TextView) convertView.findViewById(R.id.tv_file_progress);
                convertView.setTag(holder);
            } else {
                holder = (FileHolder) convertView.getTag();
            }
            setItemClickListener(holder.fileLayout, position);
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            if (ConvType.single == entity.getConvType()){
                drawable = TextDrawable.builder().buildRound(end, colors[0]);
            } else {
                drawable = TextDrawable.builder().buildRoundRect(end, colors[0], 10);
            }
            holder.avatarImage.setImageDrawable(drawable);*/
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            holder.fileNameText.setText(entity.getText());
            FileEntity fileEntity = entity.getFileEntity();
            if(fileEntity != null){
                if(FunctionUtil.downloadFileStatus.containsKey(fileEntity.getFileId())){
                    if(FunctionUtil.downloadFileStatus.get(fileEntity.getFileId()) == 3){
                        holder.fileProgressText.setVisibility(View.VISIBLE);
                        holder.fileProgressText.setText(entity.getFileProgress() + "%");
                        holder.fileStatusText.setText("正在下载");
                    } else if(FunctionUtil.downloadFileStatus.get(fileEntity.getFileId()) == 2){
                        FunctionUtil.downloadFileStatus.remove(fileEntity.getFileId());
                        holder.fileStatusText.setText("已下载");
                        holder.fileProgressText.setVisibility(View.GONE);
                    }
                }else{
                    holder.fileSizeText.setText("" + FunctionUtil.bytes2kb(fileEntity.getFileLength()));
                    holder.fileProgressText.setVisibility(View.GONE);
                    if(TextUtils.isEmpty(fileEntity.getFileLocal())){
                        FunctionUtil.downloadFileStatus.put(fileEntity.getFileId(), 1);
                        holder.fileStatusText.setText("未下载");
                    }else{
                        holder.fileStatusText.setText("已下载");
                    }
                }
            }
        } else if (viewType == ChatMsgEntity.CHAT_TYPE_SEND_FILE) {
            FileHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof FileHolder)) {
                holder = new FileHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_file_right, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.fileLayout = (RelativeLayout) convertView.findViewById(R.id.rl_file);
                holder.fileNameText = (TextView) convertView.findViewById(R.id.tv_file_name);
                holder.fileSizeText = (TextView) convertView.findViewById(R.id.tv_file_size);
                holder.fileStatusText = (TextView) convertView.findViewById(R.id.tv_file_status);
                holder.fileProgressText = (TextView) convertView.findViewById(R.id.tv_file_progress);
                convertView.setTag(holder);
            } else {
                holder = (FileHolder) convertView.getTag();
            }
            setItemClickListener(holder.fileLayout, position);
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            drawable = TextDrawable.builder().buildRound(end, colors[1]);
            holder.avatarImage.setImageDrawable(drawable);*/
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            holder.fileNameText.setText(entity.getText());
            FileEntity fileEntity = entity.getFileEntity();
            if(fileEntity != null){
                holder.fileSizeText.setText("" + FunctionUtil.bytes2kb(fileEntity.getFileLength()));
                if(FunctionUtil.sendFileStatus.contains(entity.getMsgId())){
                    holder.fileStatusText.setText("正在发送");
                    holder.fileProgressText.setVisibility(View.VISIBLE);
                    holder.fileProgressText.setText(entity.getFileProgress() + "%");
                }else{
                    holder.fileStatusText.setText("已发送");
                    holder.fileProgressText.setVisibility(View.GONE);
                }
            }
        } else if (viewType == ChatMsgEntity.CHAT_TYPE_RECV_POS) {
            PositionHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof PositionHolder)) {
                holder = new PositionHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_pos_left, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                holder.pos_layout = convertView.findViewById(R.id.pos_layout);
                holder.posImgLayout = (ViewGroup) convertView.findViewById(R.id.pos_img_layout);
                MaskView imgView = new MaskView(context, BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.s_chat_gps_left), (NinePatchDrawable) context.getResources().getDrawable(
                        R.drawable.chat_img_left_mask), screenWidth1p3, screenWidth1p3, screenWidth1p4, screenWidth1p4);
                holder.posImgLayout.removeAllViews();
                holder.posImgLayout.addView(imgView);
                ViewGroup.LayoutParams layoutParams = imgView.getLayoutParams();
                layoutParams.height = imgView.getMaskViewSize() != null ? imgView.getMaskViewSize().viewHeight : layoutParams.height;
                layoutParams.width = imgView.getMaskViewSize() != null ? imgView.getMaskViewSize().viewWidth : layoutParams.width;
                ViewGroup.LayoutParams layoutParams2 = holder.address.getLayoutParams();
                layoutParams2.width = imgView.getMaskViewSize() != null ? imgView.getMaskViewSize().viewWidth : layoutParams.width;
                convertView.setTag(holder);
            } else {
                holder = (PositionHolder) convertView.getTag();
            }
            setItemClickListener(holder.pos_layout, position);
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            if (ConvType.single == entity.getConvType()){
                drawable = TextDrawable.builder().buildRound(end, colors[0]);
            } else {
                drawable = TextDrawable.builder().buildRoundRect(end, colors[0], 10);
            }
            holder.avatarImage.setImageDrawable(drawable);*/
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            if(entity.getPoiInfo() != null && !TextUtils.isEmpty(entity.getPoiInfo().getAddress()))
                holder.address.setText(entity.getPoiInfo().getAddress());

        } else if (viewType == ChatMsgEntity.CHAT_TYPE_SEND_POS) {
            PositionHolder holder;
            if (convertView == null || !(convertView.getTag() instanceof PositionHolder)) {
                holder = new PositionHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_pos_right, null);
                holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_head);
                holder.dataText = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                holder.pos_layout = convertView.findViewById(R.id.pos_layout);
                holder.posImgLayout = (ViewGroup) convertView.findViewById(R.id.pos_img_layout);
                MaskView imgView = new MaskView(context, BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.s_chat_gps_right), (NinePatchDrawable) context.getResources().getDrawable(
                        R.drawable.chat_img_right_mask), screenWidth1p3, screenWidth1p3, screenWidth1p4, screenWidth1p4);
                holder.posImgLayout.removeAllViews();
                holder.posImgLayout.addView(imgView);
                ViewGroup.LayoutParams layoutParams = imgView.getLayoutParams();
                layoutParams.height = imgView.getMaskViewSize() != null ? imgView.getMaskViewSize().viewHeight : layoutParams.height;
                layoutParams.width = imgView.getMaskViewSize() != null ? imgView.getMaskViewSize().viewWidth : layoutParams.width;
                ViewGroup.LayoutParams layoutParams2 = holder.address.getLayoutParams();
                layoutParams2.width = imgView.getMaskViewSize() != null ? imgView.getMaskViewSize().viewWidth : layoutParams.width;
                convertView.setTag(holder);
            } else {
                holder = (PositionHolder) convertView.getTag();
            }
            setItemClickListener(holder.pos_layout, position);
            /*String nickName = entity.getName();
            String end = nickName.substring(nickName.length() - 1, nickName.length());
            TextDrawable drawable;
            drawable = TextDrawable.builder().buildRound(end, colors[1]);
            holder.avatarImage.setImageDrawable(drawable);*/
            if (entity.isShowTime()) {
                holder.dataText.setVisibility(View.VISIBLE);
                holder.dataText.setText(TimeUtil.format2(entity.getTimestamp()));
            } else {
                holder.dataText.setVisibility(View.GONE);
            }
            if(entity.getPoiInfo() != null && !TextUtils.isEmpty(entity.getPoiInfo().getAddress()))
                holder.address.setText(entity.getPoiInfo().getAddress());

        } else {
            // 不兼容的消息
            SysMsgHolder holder;
            if (convertView == null) {
                holder = new SysMsgHolder();
                convertView = inflater.inflate(R.layout.adapter_chat_sysmsg_line, null);
                holder.sysMsgText = (TextView) convertView.findViewById(R.id.tv_sys_msg);
                convertView.setTag(holder);
            } else {
                holder = (SysMsgHolder) convertView.getTag();
            }
        }
        return convertView;
    }

    private void setItemClickListener(final View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int infoType = getItemViewType(position);
                final ChatMsgEntity entity = getItem(position);
                if (infoType == ChatMsgEntity.CHAT_TYPE_RECV_AUDIO || infoType == ChatMsgEntity.CHAT_TYPE_SEND_AUDIO) {
                    VoicePlay.playVoice(entity.getText(), new VoicePlay.OnPlayListener() {
                        @Override
                        public void audioPlay() {
                            entity.setAudioPlaying(true);
                            if(!entity.isAudioRead()){
                                entity.setAudioRead(true);
                                String toId = "";
                                if(ConvType.group == entity.getConvType()){
                                    toId = entity.getToId();
                                }else if(ConvType.single == entity.getConvType()){
                                    toId = entity.getFromId();
                                }
                                String name = FunctionUtil.jointTableName(toId);
                                YouyunDbManager.getIntance().updateAudioUnRead(entity.getMsgId(), name);
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void audioStop() {
                            entity.setAudioPlaying(false);
                            notifyDataSetChanged();
                        }
                    });
                } else if (infoType == ChatMsgEntity.CHAT_TYPE_RECV_IMAGE || infoType == ChatMsgEntity.CHAT_TYPE_SEND_IMAGE) {
                    ChatBigImageActivity.startActivity(context, entity.getMsgId(), entity.getFileEntity(), position);
                } else if(infoType == ChatMsgEntity.CHAT_TYPE_RECV_FILE || infoType == ChatMsgEntity.CHAT_TYPE_SEND_FILE) {
                    FileEntity fileEntity = entity.getFileEntity();
                    if (TextUtils.isEmpty(fileEntity.getFileLocal())) {
                        if (fileClickListener != null) {
                            if(!FunctionUtil.sendFileStatus.contains(fileEntity.getFileId()) || !FunctionUtil.downloadFileStatus.containsKey(fileEntity.getFileId())){
                                fileClickListener.download(entity);
                            }
                        }
                    } else {
                        FunctionUtil.openFile(context, fileEntity.getFileLocal());
                    }

                } else if (infoType == ChatMsgEntity.CHAT_TYPE_RECV_POS || infoType == ChatMsgEntity.CHAT_TYPE_SEND_POS) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("key_flag", 2);
                    bundle.putParcelable("key_entity", entity.getPoiInfo());
                    Intent intent = new Intent(context, LocationActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
    }

    private void setAudioDrawableAnim(TextView tv, int res, int direction) {
        AnimationDrawable animationDrawable = (AnimationDrawable) context.getResources().getDrawable(res);
        animationDrawable.setBounds(0, 0, animationDrawable.getMinimumWidth(), animationDrawable.getMinimumHeight());
        switch (direction) {
            case 1:// left
                tv.setCompoundDrawables(animationDrawable, null, null, null);
                break;
            case 2:// right
                tv.setCompoundDrawables(null, null, animationDrawable, null);
                break;
        }
        animationDrawable.start();
    }

    public void setAudioDrawableUnAnim(TextView tv, int res, int direction) {
        Drawable drawable = context.getResources().getDrawable(res);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        switch (direction) {
            case 1:// left
                tv.setCompoundDrawables(drawable, null, null, null);
                break;
            case 2:// right
                tv.setCompoundDrawables(null, null, drawable, null);
                break;
        }
    }

    private static class PositionHolder {
        ImageView avatarImage;
        TextView dataText;
        TextView address;
        View pos_layout;
        ViewGroup posImgLayout;
    }

    private static class FileHolder {
        ImageView avatarImage;
        RelativeLayout fileLayout;
        TextView fileNameText;
        TextView fileSizeText;
        TextView fileStatusText;
        TextView dataText;
        TextView fileProgressText;
    }

    private static class EmojiHolder {
        ImageView avatarImage;
        BQMMMessageText contentText;
        TextView dataText;
    }

    private static class TextHolder {
        ImageView avatarImage;
        TextView dataText;
        TextView contentText;
    }

    private static class AudioHolder {
        ImageView avatarImage;
        TextView dataText;
        TextView audioText;
        TextView durationText;
        ImageView unreadFlag;
    }

    private static class ImageHolder {
        ImageView avatarImage;
        TextView dataText;
        ViewGroup imgParent;
        View imageLayout;
    }

    private static class SysMsgHolder {
        TextView sysMsgText;
    }

}
