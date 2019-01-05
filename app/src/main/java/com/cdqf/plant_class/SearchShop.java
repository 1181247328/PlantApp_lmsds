package com.cdqf.plant_class;

/**
 * Created by liu on 2018/1/4.
 */

public class SearchShop {
    private int commId;
    private String commName;
    private String picture;
    private String imgPicture;
    private int price;
    private int postage;
    private boolean isPostFree;
    private boolean isRecommend;
    private int recommendedOrder;
    private int payer;
    private boolean isOriginalPrice;

    public int getCommId() {
        return commId;
    }

    public void setCommId(int commId) {
        this.commId = commId;
    }

    public String getCommName() {
        return commName;
    }

    public void setCommName(String commName) {
        this.commName = commName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getImgPicture() {
        return imgPicture;
    }

    public void setImgPicture(String imgPicture) {
        this.imgPicture = imgPicture;
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

    public boolean isPostFree() {
        return isPostFree;
    }

    public void setPostFree(boolean postFree) {
        isPostFree = postFree;
    }

    public boolean isRecommend() {
        return isRecommend;
    }

    public void setRecommend(boolean recommend) {
        isRecommend = recommend;
    }

    public int getRecommendedOrder() {
        return recommendedOrder;
    }

    public void setRecommendedOrder(int recommendedOrder) {
        this.recommendedOrder = recommendedOrder;
    }

    public int getPayer() {
        return payer;
    }

    public void setPayer(int payer) {
        this.payer = payer;
    }

    public boolean isOriginalPrice() {
        return isOriginalPrice;
    }

    public void setOriginalPrice(boolean originalPrice) {
        isOriginalPrice = originalPrice;
    }
}
