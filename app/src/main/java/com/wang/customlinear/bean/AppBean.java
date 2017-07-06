package com.wang.customlinear.bean;

/**
 * Created by Administrator on 2017/7/6.
 */

public class AppBean {
    private int icon;
    private String name;

    public AppBean(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
