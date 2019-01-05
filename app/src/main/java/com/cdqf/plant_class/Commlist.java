package com.cdqf.plant_class;

/**
 * Created by liu on 2017/11/27.
 */

public class Commlist {
    private int commid;
    private String commname;
    private String picture;
    private String imgpicture;
    private int price;
    private int postage;
    private boolean ispostfree;
    private boolean isrecommend;
    private int recommendedorder;
    private int payer;
    private boolean isoriginalprice;

    public Commlist() {

    }

    public Commlist(int commid, String commname, String picture, String imgpicture, int price, int postage, boolean ispostfree, boolean isrecommend, int recommendedorder, int payer, boolean isoriginalprice) {
        this.commid = commid;
        this.commname = commname;
        this.picture = picture;
        this.imgpicture = imgpicture;
        this.price = price;
        this.postage = postage;
        this.ispostfree = ispostfree;
        this.isrecommend = isrecommend;
        this.recommendedorder = recommendedorder;
        this.payer = payer;
        this.isoriginalprice = isoriginalprice;
    }

    public int getCommid() {
        return commid;
    }

    public void setCommid(int commid) {
        this.commid = commid;
    }

    public String getCommname() {
        return commname;
    }

    public void setCommname(String commname) {
        this.commname = commname;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getImgpicture() {
        return imgpicture;
    }

    public void setImgpicture(String imgpicture) {
        this.imgpicture = imgpicture;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPostage() {
        return postage;
    }

    public void setPostage(int postage) {
        this.postage = postage;
    }

    public boolean ispostfree() {
        return ispostfree;
    }

    public void setIspostfree(boolean ispostfree) {
        this.ispostfree = ispostfree;
    }

    public boolean isrecommend() {
        return isrecommend;
    }

    public void setIsrecommend(boolean isrecommend) {
        this.isrecommend = isrecommend;
    }

    public int getRecommendedorder() {
        return recommendedorder;
    }

    public void setRecommendedorder(int recommendedorder) {
        this.recommendedorder = recommendedorder;
    }

    public int getPayer() {
        return payer;
    }

    public void setPayer(int payer) {
        this.payer = payer;
    }

    public boolean isoriginalprice() {
        return isoriginalprice;
    }

    public void setIsoriginalprice(boolean isoriginalprice) {
        this.isoriginalprice = isoriginalprice;
    }
}
