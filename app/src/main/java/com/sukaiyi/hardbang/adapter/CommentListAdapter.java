package com.sukaiyi.hardbang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.mukesh.MarkdownView;
import com.sukaiyi.hardbang.R;
import com.sukaiyi.hardbang.bean.Comment;
import com.sukaiyi.hardbang.bean.Post;
import com.sukaiyi.hardbang.utils.MarkdownUtils;
import com.sukaiyi.hardbang.utils.TimeUtils;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    public static final int TYPE_AVATAR = 0;
    public static final int TYPE_COMMENT = 1;

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<Comment> mComments;//数据

    private OnCommentClickListener mListener = null;

    public CommentListAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.post_comment_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null){
                    mListener.onClick(view,TYPE_AVATAR,view.getTag());
                }
            }
        });
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null){
                    mListener.onClick(view,TYPE_COMMENT,view.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Comment comment = mComments.get(position);
        holder.mAuthor.setText(comment.getAuthor().getUsername());
        holder.mAvatar.setTag(comment.getAuthor());
        holder.mRootView.setTag(comment);
        holder.mTimeView.setText(TimeUtils.getDayAndTimeName(comment.getTime()));

        Comment parent = comment.getComment();
        if(parent==null){
            holder.mCommentView.setText(comment.getContent());
            holder.mParentCommentContainer.setVisibility(View.GONE);
        }else {
            holder.mParentCommentContainer.setVisibility(View.VISIBLE);
            BmobUser parentAuthor = parent.getAuthor();
            String parentContent = parent.getContent();
            holder.mParentCommentAuthor.setText(parentAuthor.getUsername());
            holder.mParentCommentContent.setText(parentContent);
            String html = "回复"+"<font color='#58B2DC'>"+parentAuthor.getUsername()+"</font>:"+comment.getContent();
            holder.mCommentView.setText(Html.fromHtml(html));
        }
        holder.mRootView.setTag(comment);
    }

    @Override
    public int getItemCount() {
        return mComments == null ? 0 : mComments.size();
    }

    public void setOnCommentClickListener(OnCommentClickListener listener) {
        this.mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;
        BootstrapCircleThumbnail mAvatar;
        TextView mAuthor;
        TextView mCommentView;
        TextView mTimeView;

        View mParentCommentContainer;
        TextView mParentCommentAuthor;
        TextView mParentCommentContent;
        ViewHolder(View view) {
            super(view);
            mRootView = view;
            mAvatar = (BootstrapCircleThumbnail) view.findViewById(R.id.avatar);
            mAuthor = (TextView) view.findViewById(R.id.comment_author);
            mCommentView = (TextView) view.findViewById(R.id.comment_content);
            mTimeView = (TextView) view.findViewById(R.id.comment_time);

            mParentCommentContainer = view.findViewById(R.id.parent_comment_container);
            mParentCommentAuthor = (TextView) view.findViewById(R.id.parent_comment_author);
            mParentCommentContent = (TextView) view.findViewById(R.id.parent_comment_content);
        }
    }

    public interface OnCommentClickListener{
        void onClick(View view, int type, Object v);
    }
}