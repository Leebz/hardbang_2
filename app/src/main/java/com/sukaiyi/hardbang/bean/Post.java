package com.sukaiyi.hardbang.bean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * 活动帖子
 * Created by sukai on 2017/04/21.
 */

public class Post extends BmobObject {
    private String title;               //帖子标题
    private String content;             //帖子内容 （markdown格式）
    private long time;                  //创建时间
    private BmobUser author;            //发帖人
    private Organization organization;  //所属的社团
    private BmobRelation joinedUsers;   //已经参见的人

    public Post(){
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Organization getOrganization() {
        return organization;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public BmobUser getAuthor() {
        return author;
    }

    public void setAuthor(BmobUser author) {
        this.author = author;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public BmobRelation getJoinedUsers() {
        return joinedUsers;
    }

    public void setJoinedUsers(BmobRelation joinedUsers) {
        this.joinedUsers = joinedUsers;
    }


}
