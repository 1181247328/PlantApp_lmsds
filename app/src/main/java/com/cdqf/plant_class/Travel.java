package com.cdqf.plant_class;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by liu on 2017/12/19.
 */

public class Travel {

    //要上传的图片名
    private String name;

    private String putName;

    //true == 图片，false == 文字
    private boolean isText = false;

    private String text;

    private List<String> pictureList = new CopyOnWriteArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPutName() {
        return putName;
    }

    public void setPutName(String putName) {
        this.putName = putName;
    }

    public boolean isText() {
        return isText;
    }

    public void setText(boolean text) {
        isText = text;
    }

    public List<String> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<String> pictureList) {
        this.pictureList = pictureList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

