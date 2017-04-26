package com.sukaiyi.hardbang;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.sukaiyi.hardbang.adapter.MessageListAdapter;
import com.sukaiyi.hardbang.view.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mChatEdit;
    private BootstrapButton mSendButton;
    private BmobUser mUser;
    private BmobIMConversation mConversation;
    private RecyclerView mMessageList;
    private MessageListAdapter mAdapter;
    private List<BmobIMMessage> mMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        BmobIMConversation c = (BmobIMConversation) bundle.getSerializable("BmobIMConversation");
        mConversation = BmobIMConversation.obtain(BmobIMClient.getInstance(),c);
        mUser = (BmobUser) bundle.getSerializable("BmobUser");

        setContentView(R.layout.activity_chat);
        setTitle(mUser.getUsername());

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mChatEdit = (EditText) findViewById(R.id.send_text_edit);
        mChatEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = mChatEdit.getText().toString();
                if(TextUtils.isEmpty(text)){
                    mSendButton.setEnabled(false);
                    mSendButton.setShowOutline(true);
                }else{
                    mSendButton.setEnabled(true);
                    mSendButton.setShowOutline(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mChatEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    mMessageList.scrollToPosition(mMessages.size()-1);
                }
            }
        });
        mSendButton = (BootstrapButton) findViewById(R.id.send);
        mSendButton.setEnabled(false);
        mSendButton.setShowOutline(true);
        mSendButton.setOnClickListener(this);

        mMessageList = (RecyclerView) findViewById(R.id.message_list);
        mMessageList.setLayoutManager(new LinearLayoutManager(this));
        mMessageList.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, R.drawable.message_recyclerview_divider));
        mMessages = new ArrayList<>();
        mAdapter = new MessageListAdapter(this,mMessages);
        mMessageList.setAdapter(mAdapter);

        IMMessageHandler.getInstance().setHandler(new BmobIMMessageHandler(){
            @Override
            public void onMessageReceive(MessageEvent messageEvent) {
                super.onMessageReceive(messageEvent);
                if(messageEvent.getFromUserInfo().getName().equals(mUser.getUsername())){
                    mMessages.add(messageEvent.getMessage());
                    mAdapter.notifyDataSetChanged();
                    mMessageList.scrollToPosition(mMessages.size()-1);
                }
            }

            @Override
            public void onOfflineReceive(OfflineMessageEvent offlineMessageEvent) {

                super.onOfflineReceive(offlineMessageEvent);
            }
        });
        mConversation.queryMessages(null, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        for(BmobIMMessage msg:list){
                            mMessages.add(msg);
                        }
                        mAdapter.notifyDataSetChanged();
                        mMessageList.scrollToPosition(mMessages.size()-1);
                    }
                } else {
                    Log.d("ChatActivity", e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if(mMessages!=null&&mMessages.size()>0){
            SharedPreferences sp = getSharedPreferences("last_history_message",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(mUser.getUsername()+"_content",mMessages.get(mMessages.size()-1).getContent());
            editor.putLong(mUser.getUsername()+"_time",mMessages.get(mMessages.size()-1).getCreateTime());
            editor.commit();
        }
        IMMessageHandler.getInstance().setChating(false);
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        String msgText = mChatEdit.getText().toString();
        if(TextUtils.isEmpty(msgText)){
            return;
        }
        BmobIMTextMessage message = new BmobIMTextMessage();
        message.setContent(msgText);
        mConversation.sendMessage(message, new MessageSendListener() {
            @Override
            public void onStart(BmobIMMessage bmobIMMessage) {
                super.onStart(bmobIMMessage);
                Log.d("ChatActivity:onStart", bmobIMMessage.getContent());
            }

            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if(e==null){
                    mMessages.add(bmobIMMessage);
                    mAdapter.notifyDataSetChanged();
                    mMessageList.scrollToPosition(mMessages.size()-1);
                    mChatEdit.setText("");
                }else{
                    Log.e("ChatActivity", e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IMMessageHandler.getInstance().setChating(true);
        IMMessageHandler.getInstance().setChatingUser(mUser);
    }
}
