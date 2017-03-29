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
import com.ioyouyun.utils.FunctionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 卫彪 on 2016/11/24.
 */
public class GagMemberAdapter extends RecyclerView.Adapter<GagMemberAdapter.GagMemberViewHolder> {

    private LayoutInflater inflater;
    private OnItemClickLitener onItemClickLitener;
    private List<String> gagMemberList = new ArrayList<>();
    Random random = new Random(37);
    private Integer[] colors = new Integer[]{
            Color.parseColor("#22b277"), Color.parseColor("#da7294"), Color.parseColor("#2d74c4"),
            Color.parseColor("#a071c4"), Color.parseColor("#cb5372"), Color.parseColor("#ec7b2f"),
            Color.parseColor("#0099e5"), Color.parseColor("#12b2b0"), Color.parseColor("#a071c4")
    };

    public GagMemberAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setGagMemberList(List<String> gagMemberList) {
        this.gagMemberList = gagMemberList;
    }

    @Override
    public GagMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_users2, parent, false);
        GagMemberViewHolder myViewHolder = new GagMemberViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final GagMemberViewHolder holder, int position) {
        final String id = gagMemberList.get(position);
        holder.groupNameText.setText(id);
        holder.gagStatus.setText("剩余：xxx分钟");
        String end = id.substring(id.length() - 1, id.length());
        TextDrawable drawable = TextDrawable.builder().buildRound(end, colors[random.nextInt(9)]);
        holder.avatarImage.setImageDrawable(drawable);
        holder.delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDelClickLitener != null)
                    onDelClickLitener.onClick(v, id);

            }
        });

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
        return gagMemberList.size();
    }

    public String getItem(int position){
        return gagMemberList.get(position);
    }

    public interface OnDelClickLitener {
        void onClick(View view, String uid);
    }

    private OnDelClickLitener onDelClickLitener;
    public void setOnDelClickLitener(OnDelClickLitener onDelClickLitener) {
        this.onDelClickLitener = onDelClickLitener;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    class GagMemberViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarImage;
        TextView groupNameText;
        ImageView delImageView;
        TextView gagStatus;

        public GagMemberViewHolder(View itemView) {
            super(itemView);
            avatarImage = (ImageView) itemView.findViewById(R.id.iv_avatar);
            groupNameText = (TextView) itemView.findViewById(R.id.tv_nickname);
            delImageView = (ImageView) itemView.findViewById(R.id.iv_del);
            delImageView.setVisibility(View.VISIBLE);
            gagStatus = (TextView) itemView.findViewById(R.id.tv_middle);
            gagStatus.setVisibility(View.VISIBLE);
        }
    }
}
