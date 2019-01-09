package com.cdqf.plant_class;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品详情
 * Created by liu on 2017/11/28.
 */

public class GoodsDetails {
    private int commId;
    private String commName;
    private double price;
    private int postage;
    private boolean isPostFree;
    private boolean isRecommend;
    private int recommendedOrder;
    private int payer;
    private boolean isCollection;
    private int netContent;
    private String brief;
    private String introduction;
    private String placeOfOrigin;
    private String storageMode;
    private String useSymptoms;
    private String contraindication;
    private String manufactureDate;
    private String strmanufacturedate;
    private String expirationDate;
    private String strExpirationDate;
    private int gender;
    private String strGender;
    private String mixedIngredients;
    private int inventory;
    private boolean isOriginalPrice;
    private List<Picturelist> pictureList = new ArrayList<Picturelist>();
    private List<ServiceList> serviceList  = new ArrayList<ServiceList>();

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public int getNetContent() {
        return netContent;
    }

    public void setNetContent(int netContent) {
        this.netContent = netContent;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPlaceOfOrigin() {
        return placeOfOrigin;
    }

    public void setPlaceOfOrigin(String placeOfOrigin) {
        this.placeOfOrigin = placeOfOrigin;
    }

    public String getStorageMode() {
        return storageMode;
    }

    public void setStorageMode(String storageMode) {
        this.storageMode = storageMode;
    }

    public String getUseSymptoms() {
        return useSymptoms;
    }

    public void setUseSymptoms(String useSymptoms) {
        this.useSymptoms = useSymptoms;
    }

    public String getContraindication() {
        return contraindication;
    }

    public void setContraindication(String contraindication) {
        this.contraindication = contraindication;
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(String manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public String getStrmanufacturedate() {
        return strmanufacturedate;
    }

    public void setStrmanufacturedate(String strmanufacturedate) {
        this.strmanufacturedate = strmanufacturedate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getStrExpirationDate() {
        return strExpirationDate;
    }

    public void setStrExpirationDate(String strExpirationDate) {
        this.strExpirationDate = strExpirationDate;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getStrGender() {
        return strGender;
    }

    public void setStrGender(String strGender) {
        this.strGender = strGender;
    }

    public String getMixedIngredients() {
        return mixedIngredients;
    }

    public void setMixedIngredients(String mixedIngredients) {
        this.mixedIngredients = mixedIngredients;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public boolean isOriginalPrice() {
        return isOriginalPrice;
    }

    public void setOriginalPrice(boolean originalPrice) {
        isOriginalPrice = originalPrice;
    }

    public List<Picturelist> getPicturelist() {
        return pictureList;
    }

    public void setPicturelist(List<Picturelist> picturelist) {
        this.pictureList = picturelist;
    }

    public List<Picturelist> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<Picturelist> pictureList) {
        this.pictureList = pictureList;
    }

    public List<ServiceList> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<ServiceList> serviceList) {
        this.serviceList = serviceList;
    }
}
