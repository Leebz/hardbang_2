package com.sukaiyi.hardbang.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sukaiyi.hardbang.PostDetailActivity;
import com.sukaiyi.hardbang.R;
import com.sukaiyi.hardbang.adapter.PostListAdapter;
import com.sukaiyi.hardbang.bean.Post;
import com.sukaiyi.hardbang.view.RecycleViewDivider;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainListFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mPostListView;
    private List<Post> mPosts;
    private PostListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MainListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainListFragment.
     */
    public static MainListFragment newInstance(OnFragmentInteractionListener listener) {
        MainListFragment fragment = new MainListFragment();
        fragment.setArguments(new Bundle());
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);
        mPostListView = (RecyclerView) rootView.findViewById(R.id.post_list);
        mPostListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPostListView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, R.drawable.message_recyclerview_divider));

        mPosts = new ArrayList<>();
        mAdapter = new PostListAdapter(getContext(),mPosts);
        mPostListView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(this);

        if (!restoreStateFromArguments()) {
            BmobQuery<Post> query = new BmobQuery<>();
            query.include("author,organization");
            query.findObjects(getContext(), new FindListener<Post>(){

                @Override
                public void onSuccess(List<Post> list) {
                    mPosts.addAll(list);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        Post post = (Post) view.getTag();
        Bundle bundle = new Bundle();
        bundle.putSerializable("post",post);
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        BmobQuery<Post> query = new BmobQuery<>();
        query.include("author,organization");
        query.findObjects(getContext(), new FindListener<Post>(){

            @Override
            public void onSuccess(List<Post> list) {
                mPosts.clear();
                mPosts.addAll(list);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getContext(), "刷新失败", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    Bundle savedState;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!restoreStateFromArguments()) {

        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveStateToArguments();
    }
    private void saveStateToArguments() {
        savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            b.putBundle("internalSavedViewState8954201239547", savedState);
        }
    }
    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        savedState = b.getBundle("internalSavedViewState8954201239547");
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }

    private void restoreState() {
        if (savedState != null) {
            mPosts.clear();
            Post[] posts = (Post[]) savedState.getSerializable("posts");
            mPosts.addAll(Arrays.asList(posts));
            mAdapter.notifyDataSetChanged();
        }
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        Post[] posts = new Post[mPosts.size()];
        mPosts.toArray(posts);
        state.putSerializable("posts",posts);
        return state;
    }
}
