package com.sukaiyi.hardbang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.sukaiyi.hardbang.R;
import com.sukaiyi.hardbang.bean.LastMessageInfo;
import com.sukaiyi.hardbang.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class MainMessageRecyclerViewAdapter extends RecyclerView.Adapter<MainMessageRecyclerViewAdapter.ViewHolder> implements SwipeLayout.SwipeListener, View.OnTouchListener {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<BmobUser> mUsers;//数据
    private List<LastMessageInfo> mLastMessageInfos;

    private OnMainMessageRecyclerViewItemListener mListener = null;

    //记录是否为点击（如果手指有滑动,则认为不是点击）
    private boolean mClick = false;

    public MainMessageRecyclerViewAdapter(Context context, List<BmobUser> users,List<LastMessageInfo> lastMessageInfos) {
        mContext = context;
        mUsers = users;
        mLastMessageInfos = lastMessageInfos;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.recent_friends_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.mRootView.setOnTouchListener(this);
        holder.mRootView.addSwipeListener(this);

        holder.mRootView.setShowMode(SwipeLayout.ShowMode.LayDown);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mUsername.setText(mUsers.get(position).getUsername());
        holder.mRootView.setTag(mUsers.get(position));

        String lastMessage = mLastMessageInfos.get(position).getLastMessageText();
        if(!TextUtils.isEmpty(lastMessage)){
            holder.mPreviewMessage.setText(lastMessage);
        }
        long lastTime = mLastMessageInfos.get(position).getLastMessageTime();
        if(lastTime>0){
            SimpleDateFormat format;
            String toShow;
            if (TimeUtils.isTheSameDay(new Date(lastTime), new Date())) {
                format = new SimpleDateFormat("HH:mm");
                toShow = format.format(new Date(lastTime));
            } else {
                toShow = TimeUtils.getDayName(lastTime);
            }
            holder.mTimeView.setText(toShow);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
            mClick = true;
        }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
            if(mClick){
                if (mListener != null) {
                    mListener.onItemClick(view, view.getTag());
                }
            }
        }
        return false;
    }

    public void setOnItemClickListener(OnMainMessageRecyclerViewItemListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onStartOpen(SwipeLayout layout) {
    }

    @Override
    public void onOpen(SwipeLayout layout) {
    }

    @Override
    public void onStartClose(SwipeLayout layout) {
    }

    @Override
    public void onClose(SwipeLayout layout) {
    }

    @Override
    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
        mClick = false;
    }

    @Override
    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout mRootView;
        TextView mUsername;
        TextView mPreviewMessage;
        TextView mTimeView;

        ViewHolder(View view) {
            super(view);
            mRootView = (SwipeLayout)view;
            mUsername = (TextView) view.findViewById(R.id.user_name);
            mPreviewMessage = (TextView) view.findViewById(R.id.preview_message);
            mTimeView = (TextView) view.findViewById(R.id.time);
        }
    }

    public static interface OnMainMessageRecyclerViewItemListener {
        void onItemClick(View view , Object data);
    }
}