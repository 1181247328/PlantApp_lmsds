package com.cdqf.plant_class;

/**
 * Created by liu on 2017/12/29.
 */

public class Refund {
    private int rgId;
    private int orderId;
    private int commId;
    private String pic;
    private String httpPic;
    private String commName;
    private String brief;
    private int returnGoodsType;
    private String strReturnGoodsType;
    private int rgStatus;
    private String strReturnGoodsStatus;
    private int commNum;

    public int getRgId() {
        return rgId;
    }

    public void setRgId(int rgId) {
        this.rgId = rgId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCommId() {
        return commId;
    }

    public void setCommId(int commId) {
        this.commId = commId;
    }

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

    public String getCommName() {
        return commName;
    }

    public void setCommName(String commName) {
        this.commName = commName;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public int getReturnGoodsType() {
        return returnGoodsType;
    }

    public void setReturnGoodsType(int returnGoodsType) {
        this.returnGoodsType = returnGoodsType;
    }

    public String getStrReturnGoodsType() {
        return strReturnGoodsType;
    }

    public void setStrReturnGoodsType(String strReturnGoodsType) {
        this.strReturnGoodsType = strReturnGoodsType;
    }

    public int getRgStatus() {
        return rgStatus;
    }

    public void setRgStatus(int rgStatus) {
        this.rgStatus = rgStatus;
    }

    public String getStrReturnGoodsStatus() {
        return strReturnGoodsStatus;
    }

    public void setStrReturnGoodsStatus(String strReturnGoodsStatus) {
        this.strReturnGoodsStatus = strReturnGoodsStatus;
    }

    public int getCommNum() {
        return commNum;
    }

    public void setCommNum(int commNum) {
        this.commNum = commNum;
    }
}
