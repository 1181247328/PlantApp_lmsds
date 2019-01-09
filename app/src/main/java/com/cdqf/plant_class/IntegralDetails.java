package com.cdqf.plant_class;

import java.util.List;

/**
 * Created by liu on 2018/1/16.
 */

public class IntegralDetails {
    private int commId;
    private String commName;
    private int integralNumber;
    private double integralMoney;
    private double unitPrice;
    private String introduction;
    private int postage;
    private String tel;
    private List<PicList> picList;

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

    public int getIntegralNumber() {
        return integralNumber;
    }

    public void setIntegralNumber(int integralNumber) {
        this.integralNumber = integralNumber;
    }

    public double getIntegralMoney() {
        return integralMoney;
    }

    public void setIntegralMoney(double integralMoney) {
        this.integralMoney = integralMoney;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getPostage() {
        return postage;
    }

    public void setPostage(int postage) {
        this.postage = postage;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public List<PicList> getPicList() {
        return picList;
    }

    public void setPicList(List<PicList> picList) {
        this.picList = picList;
    }

    public class PicList{
        private String pic;
        private String httpPic;

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getHttpPic() {
            return httpPic;
        }

        public void setHttpPic(String httpPic) {
            this.httpPic = httpPic;
        }
    }
}
