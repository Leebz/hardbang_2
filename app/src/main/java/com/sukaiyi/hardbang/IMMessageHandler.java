package com.sukaiyi.hardbang;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.v3.BmobUser;

import static android.content.Context.NOTIFICATION_SERVICE;

public class IMMessageHandler extends BmobIMMessageHandler {

    private BmobIMMessageHandler handler = null;
    private Context mContext;

    //标志用户是否正在聊天
    private boolean mChating = false;
    //记录用户正在聊天的对象
    private BmobUser mChatingUser;
    private static final IMMessageHandler instance = new IMMessageHandler();

    private IMMessageHandler() {

    }

    @Override
    public void onMessageReceive(final MessageEvent event) {
        if(handler!=null){
            handler.onMessageReceive(event);
        }
        if(mChating&&event.getFromUserInfo().getName().equals(mChatingUser.getUsername())){
            return;
        }
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

        Intent intent = new Intent(mContext, ChatActivity.class);
        Bundle bundle = new Bundle();
        BmobUser user = new BmobUser();
        user.setUsername(event.getFromUserInfo().getName());
        bundle.putSerializable("BmobUser",user);
        bundle.putSerializable("BmobIMConversation",event.getConversation());
        intent.putExtras(bundle);

        mBuilder.setContentTitle(event.getFromUserInfo().getName())//设置通知栏标题\
                .setAutoCancel(true)
                .setContentText(event.getMessage().getContent()) //设置通知栏显示内容
                .setContentIntent(PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)) //设置通知栏点击意图
                .setTicker(event.getFromUserInfo().getName()+":"+event.getMessage().getContent()) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON

        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
        if(handler!=null){
            handler.onOfflineReceive(event);
        }
    }

    public void setHandler(BmobIMMessageHandler handler) {
        this.handler = handler;
    }

    public static IMMessageHandler getInstance() {
        return instance;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public boolean isChating() {
        return mChating;
    }

    public void setChating(boolean mChating) {
        this.mChating = mChating;
    }

    public BmobUser getChatingUser() {
        return mChatingUser;
    }

    public void setChatingUser(BmobUser mChatingUser) {
        this.mChatingUser = mChatingUser;
    }
}