package com.sukaiyi.hardbang.bean;

import cn.bmob.v3.BmobUser;

/**
 * 该实体类用于保存最近联系人列表中的最近一条消息预览，以及最近联系的时间。
 * Created by sukai on 2017/04/20.
 */

public class LastMessageInfo {
    public String getLastMessageText() {
        return mLastMessageText;
    }

    public long getLastMessageTime() {
        return mLastMessageTime;
    }

    public void setLastMessageText(String mLastMessageText) {
        this.mLastMessageText = mLastMessageText;
    }

    public void setLastMessageTime(long mLastMessageTime) {
        this.mLastMessageTime = mLastMessageTime;
    }

    private String mLastMessageText;
    private long mLastMessageTime;
}
