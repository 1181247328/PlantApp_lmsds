package com.cdqf.plant_class;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2017/12/27.
 */

public class OrderDetails {
    private int orderId;
    private int dealStatus;
    private boolean isSign;
    private boolean isDeliverGoods;
    private String orderStatus;
    private String receivingContacts;
    private int receivingAddressId;
    private String strReceivingAddress;
    private String orderNo;
    private String orderDate;
    private int dealPrice;
    private int postage;
    private String deliverGoodDate;
    private String strDeliverGoodDate;
    private String payDate;
    private String strPayDate;
    private int paymentType;
    private String strPaymentType;
    private List<Commlist> commList = new ArrayList<>();
    private String logisticsNum;
    private String expressZhName;
    private String tel;
    private int commTotalNum;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(int dealStatus) {
        this.dealStatus = dealStatus;
    }

    public boolean isSign() {
        return isSign;
    }

    public void setSign(boolean sign) {
        isSign = sign;
    }

    public boolean isDeliverGoods() {
        return isDeliverGoods;
    }

    public void setDeliverGoods(boolean deliverGoods) {
        isDeliverGoods = deliverGoods;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getReceivingContacts() {
        return receivingContacts;
    }

    public void setReceivingContacts(String receivingContacts) {
        this.receivingContacts = receivingContacts;
    }

    public int getReceivingAddressId() {
        return receivingAddressId;
    }

    public void setReceivingAddressId(int receivingAddressId) {
        this.receivingAddressId = receivingAddressId;
    }

    public String getStrReceivingAddress() {
        return strReceivingAddress;
    }

    public void setStrReceivingAddress(String strReceivingAddress) {
        this.strReceivingAddress = strReceivingAddress;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(int dealPrice) {
        this.dealPrice = dealPrice;
    }

    public int getPostage() {
        return postage;
    }

    public void setPostage(int postage) {
        this.postage = postage;
    }

    public String getDeliverGoodDate() {
        return deliverGoodDate;
    }

    public void setDeliverGoodDate(String deliverGoodDate) {
        this.deliverGoodDate = deliverGoodDate;
    }

    public String getStrDeliverGoodDate() {
        return strDeliverGoodDate;
    }

    public void setStrDeliverGoodDate(String strDeliverGoodDate) {
        this.strDeliverGoodDate = strDeliverGoodDate;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getStrPayDate() {
        return strPayDate;
    }

    public void setStrPayDate(String strPayDate) {
        this.strPayDate = strPayDate;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public String getStrPaymentType() {
        return strPaymentType;
    }

    public void setStrPaymentType(String strPaymentType) {
        this.strPaymentType = strPaymentType;
    }

    public List<Commlist> getCommList() {
        return commList;
    }

    public void setCommList(List<Commlist> commList) {
        this.commList = commList;
    }

    public String getLogisticsNum() {
        return logisticsNum;
    }

    public void setLogisticsNum(String logisticsNum) {
        this.logisticsNum = logisticsNum;
    }

    public String getExpressZhName() {
        return expressZhName;
    }

    public void setExpressZhName(String expressZhName) {
        this.expressZhName = expressZhName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getCommTotalNum() {
        return commTotalNum;
    }

    public void setCommTotalNum(int commTotalNum) {
        this.commTotalNum = commTotalNum;
    }

    public class Commlist {

        private int commId;
        private String commName;
        private int commNum;
        private String commPic;
        private String imgCommPic;
        private int commPrice;
        private int totalPrice;
        private String brif;
        private int netContent;
        private boolean isTicket;
        private boolean isReturnGoods;
        private int returnGoodsStatus;
        private String strReturnGoodsStatus;

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

        public int getCommPrice() {
            return commPrice;
        }

        public void setCommPrice(int commPrice) {
            this.commPrice = commPrice;
        }

        public int getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(int totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getBrif() {
            return brif;
        }

        public void setBrif(String brif) {
            this.brif = brif;
        }

        public int getNetContent() {
            return netContent;
        }

        public void setNetContent(int netContent) {
            this.netContent = netContent;
        }

        public boolean isTicket() {
            return isTicket;
        }

        public void setTicket(boolean ticket) {
            isTicket = ticket;
        }

        public boolean isReturnGoods() {
            return isReturnGoods;
        }

        public void setReturnGoods(boolean returnGoods) {
            isReturnGoods = returnGoods;
        }

        public int getReturnGoodsStatus() {
            return returnGoodsStatus;
        }

        public void setReturnGoodsStatus(int returnGoodsStatus) {
            this.returnGoodsStatus = returnGoodsStatus;
        }

        public String getStrReturnGoodsStatus() {
            return strReturnGoodsStatus;
        }

        public void setStrReturnGoodsStatus(String strReturnGoodsStatus) {
            this.strReturnGoodsStatus = strReturnGoodsStatus;
        }
    }
}
