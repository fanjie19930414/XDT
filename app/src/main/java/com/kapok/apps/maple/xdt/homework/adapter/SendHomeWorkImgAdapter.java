package com.kapok.apps.maple.xdt.homework.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.kapok.apps.maple.xdt.R;
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkImgBean;
import com.kapok.apps.maple.xdt.utils.MyItemTouchCallback;
import com.kotlin.baselibrary.custom.CustomRoundAngleImageView;

import java.util.Collections;
import java.util.List;

/**
 * 发作业Adapter
 */
public class SendHomeWorkImgAdapter extends RecyclerView.Adapter<SendHomeWorkImgAdapter.ViewHolder> implements MyItemTouchCallback.ItemTouchAdapter {
    private Context context;
    private int maxPicNums;
    private List<HomeWorkImgBean> datas;
    private OnClickAddLongPic onClickAddLongPic;

    public SendHomeWorkImgAdapter(Context context, int maxPic, List<HomeWorkImgBean> datas) {
        this.context = context;
        this.maxPicNums = maxPic;
        this.datas = datas;
    }

    public void setMaxPic(int maxPicNum) {
        this.maxPicNums = maxPicNum;
        notifyDataSetChanged();
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if (fromPosition >= datas.size() || toPosition >= datas.size()) {
            return;
        }
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(datas, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(datas, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onSwiped(int position) {
        datas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        if (datas == null || datas.size() == 0) {
            return 1;
        } else {
            if (datas.size() < maxPicNums) {
                return datas.size() + 1;
            } else {
                return maxPicNums;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SendHomeWorkImgAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_send_homework_img, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        // 正常显示图片
        if (viewHolder.getAdapterPosition() < datas.size()) {
            if (datas.get(viewHolder.getAdapterPosition()).getSelected()) {
                viewHolder.ivVideo.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivVideo.setVisibility(View.GONE);
            }
            Glide.with(context).load(datas.get(viewHolder.getAdapterPosition()).getPath()).into(viewHolder.ivSendHomeWorkImg);
            viewHolder.ivSendHomeWorkImg.setTag(R.id.image_key,false);
            viewHolder.ivSendHomeWorkImgDel.setVisibility(View.VISIBLE);
        } else {
            // 显示Add图片
            viewHolder.ivSendHomeWorkImg.setImageResource(R.mipmap.icon_img_upload);
            viewHolder.ivSendHomeWorkImg.setTag(R.id.image_key,true);
            viewHolder.ivSendHomeWorkImgDel.setVisibility(View.GONE);
            viewHolder.ivVideo.setVisibility(View.GONE);
        }

        // 删除事件
        viewHolder.ivSendHomeWorkImgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnClickAddLongPic() != null) {
                    onClickAddLongPic.onClick(v,viewHolder.getAdapterPosition());
                }
            }
        });

        // 添加事件
        viewHolder.ivSendHomeWorkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnClickAddLongPic() != null) {
                    onClickAddLongPic.onClick(v,viewHolder.getAdapterPosition());
                }
            }
        });

        // 长按事件
        viewHolder.ivSendHomeWorkImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onClickAddLongPic.onLongClick(v, viewHolder, viewHolder.getAdapterPosition());
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomRoundAngleImageView ivSendHomeWorkImg;
        ImageView ivSendHomeWorkImgDel;
        ImageView ivVideo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSendHomeWorkImg = itemView.findViewById(R.id.ivSendHomeWorkImg);
            ivSendHomeWorkImgDel = itemView.findViewById(R.id.ivSendHomeWorkImgDel);
            ivVideo = itemView.findViewById(R.id.ivVideo);
        }
    }

    public void setOnClickAddLongPic(OnClickAddLongPic onClickAddLongPic) {
        this.onClickAddLongPic = onClickAddLongPic;
    }

    private OnClickAddLongPic getOnClickAddLongPic() {
        return onClickAddLongPic;
    }

    public interface OnClickAddLongPic {
        void onClick(View v, int position);

        boolean onLongClick(View v, ViewHolder viewHolder, int adapterPosition);
    }
}
