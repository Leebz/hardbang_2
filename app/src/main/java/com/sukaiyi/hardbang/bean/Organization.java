package com.sukaiyi.hardbang.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * 实体类，一个社团、组织、团队、俱乐部......
 * Created by sukai on 2017/04/21.
 */

public class Organization extends BmobObject{

    public BmobUser getManager() {
        return manager;
    }

    public void setManager(BmobUser manager) {
        this.manager = manager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    private BmobUser manager;    //管理员
    private String name;         //名称
    private String introduction; //简介

}
