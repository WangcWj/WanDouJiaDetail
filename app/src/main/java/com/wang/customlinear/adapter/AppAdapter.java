package com.wang.customlinear.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wang.customlinear.R;
import com.wang.customlinear.bean.AppBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/6.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.MyViewHolder> {
     List<AppBean> list = new ArrayList<>();
     Context context;
    LayoutInflater inflater;
    MOnClickListener mOnClickListener;

    public AppAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    public void setRefreshData( List<AppBean> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setmOnClickListener(MOnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(R.layout.app_layout_item, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        AppBean appBean = list.get(position);
        holder.imageView.setImageResource(appBean.getIcon());
        holder.textView.setText(appBean.getName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickListener != null){
                    mOnClickListener.mOnClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class  MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        LinearLayout item;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.app_item_image);
            textView  = (TextView) itemView.findViewById(R.id.app_item_name);
            item = (LinearLayout) itemView.findViewById(R.id.item);
        }
    }
    public interface MOnClickListener{
        void mOnClick(View view,int position);
    }
}
