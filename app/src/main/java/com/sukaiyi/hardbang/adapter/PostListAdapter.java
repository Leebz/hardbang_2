package com.sukaiyi.hardbang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mukesh.MarkdownView;
import com.sukaiyi.hardbang.R;
import com.sukaiyi.hardbang.bean.Post;
import com.sukaiyi.hardbang.utils.MarkdownUtils;
import com.sukaiyi.hardbang.utils.TimeUtils;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> implements View.OnClickListener {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<Post> mPosts;//数据

    private View.OnClickListener mListener = null;

    public PostListAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts = posts;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.post_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.mRootView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        String content = post.getContent();
        holder.mOrganizationView.setText("来自:"+(post.getOrganization()==null?"null":post.getOrganization().getName()));
        holder.mPostTitleView.setText(post.getTitle());
        holder.mMarkdownView.setMarkDownText(MarkdownUtils.getConciseMarkdown(content));
        holder.mTimeView.setText(TimeUtils.getDayAndTimeName(post.getTime()));

        holder.mRootView.setTag(post);
    }

    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : mPosts.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onClick(view);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;
        MarkdownView mMarkdownView;
        TextView mPostTitleView;
        TextView mOrganizationView;
        TextView mTimeView;
        ViewHolder(View view) {
            super(view);
            mRootView = view;
            mMarkdownView = (MarkdownView) view.findViewById(R.id.post_item_preview);
            mMarkdownView.setClickable(false);

            mPostTitleView = (TextView) view.findViewById(R.id.post_title);
            mOrganizationView = (TextView) view.findViewById(R.id.come_from_organization);
            mTimeView = (TextView) view.findViewById(R.id.post_time);
        }
    }
}