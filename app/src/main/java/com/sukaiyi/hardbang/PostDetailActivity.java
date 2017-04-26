package com.sukaiyi.hardbang;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.mukesh.MarkdownView;
import com.sukaiyi.hardbang.adapter.CommentListAdapter;
import com.sukaiyi.hardbang.bean.Comment;
import com.sukaiyi.hardbang.bean.Post;
import com.sukaiyi.hardbang.utils.TimeUtils;
import com.sukaiyi.hardbang.view.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PostDetailActivity extends AppCompatActivity implements BootstrapDropDown.OnDropDownItemClickListener, CommentListAdapter.OnCommentClickListener {

    private MarkdownView mPostDetailView;
    private TextView mComeFromView;
    private TextView mTitleView;
    private TextView mCreateTime;
    private TextView mAuthor;
    private TextView mNumOfJoinedView;
    private BootstrapButton mIWantJoinButton;
    private RecyclerView mPostCommentsView;

    private BootstrapDropDown mMoreActionDropDown;

    private Post post;
    private List<BmobUser> mJoinedUsers;
    private List<Comment> mComments;
    private CommentListAdapter mCommentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("活动详情");

        Bundle bundle = getIntent().getExtras();
        post = (Post) bundle.getSerializable("post");
        if(post==null){
            Snackbar.make(toolbar, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
            this.finish();
            return;
        }

        mJoinedUsers = new ArrayList<>();

        BmobQuery<BmobUser> query = new BmobQuery<>();
        query.addWhereRelatedTo("joinedUsers",new BmobPointer(post));
        query.findObjects(this,new FindListener<BmobUser>(){
            @Override
            public void onSuccess(List<BmobUser> list) {
                mJoinedUsers.clear();
                mJoinedUsers.addAll(list);
                mNumOfJoinedView.setText("已有"+mJoinedUsers.size()+"人报名");
                BmobUser currentUser = BmobUser.getCurrentUser(PostDetailActivity.this);
                for(BmobUser u:list){
                    if(u.getObjectId().equals(currentUser.getObjectId())){
                        mIWantJoinButton.setText("您已报名");
                        mIWantJoinButton.setEnabled(false);
                        break;
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.e("TAG", "onError: "+i+s);
            }
        });

        mComeFromView = (TextView) findViewById(R.id.come_from_organization);
        mComeFromView.setText("来自:"+post.getOrganization().getName());

        mTitleView = (TextView) findViewById(R.id.post_title);
        mTitleView.setText(post.getTitle());

        mCreateTime = (TextView) findViewById(R.id.create_time);
        mCreateTime.setText(TimeUtils.getDayAndTimeName(post.getTime()));

        mPostDetailView = (MarkdownView) findViewById(R.id.post_detail_view);
        mPostDetailView.setMarkDownText(post.getContent()==null?"":post.getContent());

        mAuthor = (TextView) findViewById(R.id.author);
        mAuthor.setText(post.getAuthor().getUsername());
        mAuthor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    mAuthor.setTextColor(Color.GREEN);
                    return true;
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    int color = ContextCompat.getColor(PostDetailActivity.this, android.R.color.holo_blue_dark);
                    mAuthor.setTextColor(color);
                    Snackbar.make(view, "" +
                            "待完善的 （用户详情界面）", Snackbar.LENGTH_LONG)
                            .setAction("确定", null).show();
                    return true;
                }
                return false;
            }
        });

        mNumOfJoinedView = (TextView) findViewById(R.id.num_of_joined);
        mNumOfJoinedView.setText("已有"+mJoinedUsers.size()+"人报名");

        mIWantJoinButton = (BootstrapButton) findViewById(R.id.i_want_join);
        mIWantJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobRelation relation = new BmobRelation();
                relation.add(BmobUser.getCurrentUser(PostDetailActivity.this));
                post.setJoinedUsers(relation);
                post.update(PostDetailActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        mIWantJoinButton.setText("报名成功");
                        mIWantJoinButton.setEnabled(false);
                        mNumOfJoinedView.setText("已有"+(mJoinedUsers.size()+1)+"人报名");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        });

        mPostCommentsView = (RecyclerView) findViewById(R.id.post_comment);
        mPostCommentsView.setLayoutManager(new LinearLayoutManager(this));
        mPostCommentsView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, R.drawable.message_recyclerview_divider));

        mComments = new ArrayList<>();
        mCommentListAdapter = new CommentListAdapter(this,mComments);
        mPostCommentsView.setAdapter(mCommentListAdapter);
        mCommentListAdapter.setOnCommentClickListener(this);

        refreshComment();

        mMoreActionDropDown = (BootstrapDropDown) findViewById(R.id.more_action_drop_down);
        mMoreActionDropDown.setOnDropDownItemClickListener(this);
    }

    private void refreshComment(){
        BmobQuery<Comment> commentBmobQuery = new BmobQuery<>();
        commentBmobQuery.addWhereEqualTo("post",post);
        commentBmobQuery.include("author,comment,comment.author");
        commentBmobQuery.setLimit(20);
        commentBmobQuery.order("-time");
        commentBmobQuery.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                mComments.clear();
                mComments.addAll(list);
                mCommentListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(PostDetailActivity.this, "获取评论失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(ViewGroup parent, View v, int id) {
        CharSequence[] r = this.getResources().getTextArray(R.array.post_action_dropdown_data);
        if(r[id].equals("写评论")){
            new MaterialDialog.Builder(this)
                    .title(r[id])
                    .content("写下自己的看法^_^")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if(TextUtils.isEmpty(input)){
                                return;
                            }
                            Comment comment = new Comment();
                            comment.setPost(post);
                            comment.setTime(new Date().getTime());
                            comment.setContent(input.toString());
                            comment.setAuthor(BmobUser.getCurrentUser(PostDetailActivity.this));
                            comment.save(PostDetailActivity.this, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    refreshComment();
                                    Toast.makeText(PostDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(PostDetailActivity.this, "评论失败:" + s, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).show();

        }else if(r[id].equals("咨询")){

        }else if(r[id].equals("举报")){

        }
    }

    @Override
    public void onClick(View view, int type, Object v) {
        if(type==CommentListAdapter.TYPE_AVATAR){
            BmobUser user = (BmobUser)v;
            Log.d("PostDetailActivity", user.getUsername());
        }else if(type==CommentListAdapter.TYPE_COMMENT){
            final Comment comment = (Comment)v;
            new MaterialDialog.Builder(this)
                    .title("回复"+comment.getAuthor().getUsername()+":")
                    .content(comment.getContent())
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if(TextUtils.isEmpty(input)){
                                return;
                            }
                            Comment c = new Comment();
                            c.setPost(post);
                            c.setTime(new Date().getTime());
                            c.setContent(input.toString());
                            c.setComment(comment);
                            c.setAuthor(BmobUser.getCurrentUser(PostDetailActivity.this));
                            c.save(PostDetailActivity.this, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    refreshComment();
                                    Toast.makeText(PostDetailActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(PostDetailActivity.this, "回复失败:" + s, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).show();
        }
    }
}
