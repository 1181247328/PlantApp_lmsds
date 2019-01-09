package com.cdqf.plant_class;

/**
 * Created by liu on 2017/11/27.
 */

public class Commlist {
//    private int commid;
//    private String commname;
//    private String picture;
//    private String imgpicture;
//    private double price;
//    private int postage;
//    private boolean ispostfree;
//    private boolean isrecommend;
//    private int recommendedorder;
//    private int payer;
//    private boolean isoriginalprice;

    private int commId;
    private String commName;
    private String imgPicture;
    private boolean isOriginalPrice;
    private boolean isPostFree;
    private boolean isRecommend;
    private int payer;
    private int postage;
    private double price;
    private int recommendedOrder;

    public Commlist() {

    }

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

    public String getImgPicture() {
        return imgPicture;
    }

    public void setImgPicture(String imgPicture) {
        this.imgPicture = imgPicture;
    }

    public boolean isOriginalPrice() {
        return isOriginalPrice;
    }

    public void setOriginalPrice(boolean originalPrice) {
        isOriginalPrice = originalPrice;
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

    public int getPayer() {
        return payer;
    }

    public void setPayer(int payer) {
        this.payer = payer;
    }

    public int getPostage() {
        return postage;
    }

    public void setPostage(int postage) {
        this.postage = postage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getRecommendedOrder() {
        return recommendedOrder;
    }

    public void setRecommendedOrder(int recommendedOrder) {
        this.recommendedOrder = recommendedOrder;
    }
}
