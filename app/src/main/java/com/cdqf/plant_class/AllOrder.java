package com.cdqf.plant_class;

import java.util.List;

/**
 * Created by liu on 2017/12/26.
 */

public class AllOrder {
    private int orderId;
    private int commCount;
    private int postage;
    private double totalPrice;
    private int dealStatus;
    private boolean isDeliverGoods;
    private boolean isSign;
    private String orderStatus;
    private List<Ordercommlist> orderCommList;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCommCount() {
        return commCount;
    }

    public void setCommCount(int commCount) {
        this.commCount = commCount;
    }

    public int getPostage() {
        return postage;
    }

    public void setPostage(int postage) {
        this.postage = postage;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(int dealStatus) {
        this.dealStatus = dealStatus;
    }

    public boolean isDeliverGoods() {
        return isDeliverGoods;
    }

    public void setDeliverGoods(boolean deliverGoods) {
        isDeliverGoods = deliverGoods;
    }

    public boolean isSign() {
        return isSign;
    }

    public void setSign(boolean sign) {
        isSign = sign;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<Ordercommlist> getOrderCommList() {
        return orderCommList;
    }

    public void setOrderCommList(List<Ordercommlist> orderCommList) {
        this.orderCommList = orderCommList;
    }

    public class Ordercommlist{
        private String commName;
        private int commNum;
        private String commPic;
        private String imgCommPic;
        private double commPrice;

        public String getCommName() {
            return commName;
        }

        public void setCommName(String commName) {
            this.commName = commName;
        }

        public int getCommNum() {
            return commNum;
        }

        public void setCommNum(int commNum) {
            this.commNum = commNum;
        }

        public String getCommPic() {
            return commPic;
        }

        public void setCommPic(String commPic) {
            this.commPic = commPic;
        }

        public String getImgCommPic() {
            return imgCommPic;
        }

        public void setImgCommPic(String imgCommPic) {
            this.imgCommPic = imgCommPic;
        }

        public double getCommPrice() {
            return commPrice;
        }

        public void setCommPrice(double commPrice) {
            this.commPrice = commPrice;
        }
    }
}
