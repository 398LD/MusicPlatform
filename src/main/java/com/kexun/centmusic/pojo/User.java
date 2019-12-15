package com.kexun.centmusic.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("user")
public class User {

    private int id;

    private String username;

    private String password;

    @TableField("showName")
    private String showName;


    //是否是会员 现在暂时不开头会员制度
    @TableField("isMember")
    private int isMember;
    //会员过期时间
    @TableField("expirationTime")
    private long expirationTime;
    @TableField("createTime")
    private long createTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getIsMember() {
        return isMember;
    }

    public void setIsMember(int isMember) {
        this.isMember = isMember;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", showName='" + showName + '\'' +
                ", isMember=" + isMember +
                ", expirationTime=" + expirationTime +
                ", createTime=" + createTime +
                '}';
    }
}
