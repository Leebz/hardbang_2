package com.sukaiyi.hardbang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.sukaiyi.hardbang.R;

import java.util.List;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.v3.BmobUser;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<BmobIMMessage> mMessages;

    public MessageListAdapter(Context context, List<BmobIMMessage> messages) {
        mContext = context;
        mMessages = messages;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;

        if(viewType==0){
            View view = mLayoutInflater.inflate(R.layout.message_item_right_item, parent, false);
            holder = new RightViewHolder(view);
        }else if(viewType==1){
            View view = mLayoutInflater.inflate(R.layout.message_item_left_item, parent, false);
            holder = new LeftViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BmobIMMessage message = mMessages.get(position);
        if(holder instanceof RightViewHolder){
            ((RightViewHolder) holder).mMessageText.setText(message.getContent());
        }else if(holder instanceof LeftViewHolder){
            ((LeftViewHolder) holder).mMessageText.setText(message.getContent());
        }
    }

    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = mMessages.get(position);
        if (message.getFromId().equals(BmobUser.getCurrentUser(mContext).getObjectId())){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return mMessages == null ? 0 : mMessages.size();
    }


    public static class LeftViewHolder extends RecyclerView.ViewHolder {
        View mRootView;
        TextView mMessageText;
        BootstrapCircleThumbnail avatar;

        LeftViewHolder(View view) {
            super(view);
            mRootView = view;
            mMessageText = (TextView) view.findViewById(R.id.message_text);
            avatar = (BootstrapCircleThumbnail) view.findViewById(R.id.avatar);
        }
    }

    public static class RightViewHolder extends RecyclerView.ViewHolder {
        View mRootView;
        TextView mMessageText;
        BootstrapCircleThumbnail avatar;

        RightViewHolder(View view) {
            super(view);
            mRootView = view;
            mMessageText = (TextView) view.findViewById(R.id.message_text);
            avatar = (BootstrapCircleThumbnail) view.findViewById(R.id.avatar);
        }
    }
}