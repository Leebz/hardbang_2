package com.sukaiyi.hardbang.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sukaiyi.hardbang.ChatActivity;
import com.sukaiyi.hardbang.R;
import com.sukaiyi.hardbang.adapter.MainMessageRecyclerViewAdapter;
import com.sukaiyi.hardbang.bean.LastMessageInfo;
import com.sukaiyi.hardbang.view.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMessageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private ArrayList<BmobUser> mUsers;
    private List<LastMessageInfo> mLastMessageInfos;
    private MainMessageRecyclerViewAdapter mAdapter;

    public MainMessageFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static MainMessageFragment newInstance(OnFragmentInteractionListener listener) {
        MainMessageFragment fragment = new MainMessageFragment();
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_message, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, R.drawable.recyclerview_divider));


        mUsers = new ArrayList<>();
        mLastMessageInfos = new ArrayList<>();
        mAdapter = new MainMessageRecyclerViewAdapter(getContext(),mUsers,mLastMessageInfos);
        mAdapter.setOnItemClickListener(new MainMessageRecyclerViewAdapter.OnMainMessageRecyclerViewItemListener() {
            @Override
            public void onItemClick(View view, Object data) {
                final BmobUser user = (BmobUser) data;
                BmobIMUserInfo userInfo = new BmobIMUserInfo();
                userInfo.setUserId(user.getObjectId());
                userInfo.setName(user.getUsername());
                BmobIM.getInstance().startPrivateConversation(userInfo, new ConversationListener() {
                    @Override
                    public void done(BmobIMConversation c, BmobException e) {
                        if (e == null) {
                            Intent intent = new Intent(getContext(), ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("BmobUser",user);
                            bundle.putSerializable("BmobIMConversation",c);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Log.d("ChatActivity", e.getMessage() + "(" + e.getErrorCode() + ")");
                        }
                    }
                });
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        query.addWhereNotEqualTo("username", BmobUser.getCurrentUser(getContext()).getUsername());
        query.findObjects(getContext(), new FindListener<BmobUser>() {
            @Override
            public void onSuccess(List<BmobUser> list) {
                for(BmobUser u : list){
                    mUsers.add(u);
                    SharedPreferences sp = getContext().getSharedPreferences("last_history_message", Context.MODE_PRIVATE);
                    LastMessageInfo lastMessageInfo = new LastMessageInfo();
                    lastMessageInfo.setLastMessageText(sp.getString(u.getUsername() + "_content", ""));
                    lastMessageInfo.setLastMessageTime(sp.getLong(u.getUsername() + "_time", 0));
                    mLastMessageInfos.add(lastMessageInfo);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Log.e("MainMessageFragment", i+","+s);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        for(int i=0;i<mUsers.size();i++){
            BmobUser user = mUsers.get(i);
            SharedPreferences sp = getContext().getSharedPreferences("last_history_message",Context.MODE_PRIVATE);
            mLastMessageInfos.get(i).setLastMessageText(sp.getString(user.getUsername()+"_content",""));
            mLastMessageInfos.get(i).setLastMessageTime(sp.getLong(user.getUsername()+"_time",0));
        }
        mAdapter.notifyDataSetChanged();
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
}
