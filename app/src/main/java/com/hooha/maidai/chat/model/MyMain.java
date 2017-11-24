package com.hooha.maidai.chat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MG on 2016/11/3.
 */
public class MyMain {
    private String picture;
    private String name;
    private String contribution;
    private List<Attention> list = new ArrayList<>();

    private static MyMain ourInstance = new MyMain();

    public static MyMain getInstance() {
        return ourInstance;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContribution() {
        return contribution;
    }

    public void setContribution(String contribution) {
        this.contribution = contribution;
    }

    public List<Attention> getList() {
        return list;
    }

    public void setList(List<Attention> list) {
        this.list = list;
    }
}
