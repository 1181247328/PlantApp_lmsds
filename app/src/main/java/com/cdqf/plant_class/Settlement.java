package com.cdqf.plant_class;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by liu on 2017/12/11.
 */

public class Settlement {
    private int postFree;
    private double allCommTotalPrice;
    private int totalNum;
    private String commIds;
    private String commNums;
    private List<Commlist> commList = new CopyOnWriteArrayList<>();
    private Receivingaddress receivingAddress = null;

    public Settlement(){

    }

    public int getPostFree() {
        return postFree;
    }

    public void setPostFree(int postFree) {
        this.postFree = postFree;
    }

    public double getAllCommTotalPrice() {
        return allCommTotalPrice;
    }

    public void setAllCommTotalPrice(double allCommTotalPrice) {
        this.allCommTotalPrice = allCommTotalPrice;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public String getCommIds() {
        return commIds;
    }

    public void setCommIds(String commIds) {
        this.commIds = commIds;
    }

    public String getCommNums() {
        return commNums;
    }

    public void setCommNums(String commNums) {
        this.commNums = commNums;
    }

    public List<Commlist> getCommList() {
        return commList;
    }

    public void setCommList(List<Commlist> commList) {
        this.commList = commList;
    }

    public Receivingaddress getReceivingAddress() {
        return receivingAddress;
    }

    public void setReceivingAddress(Receivingaddress receivingAddress) {
        this.receivingAddress = receivingAddress;
    }

    public class Commlist {

        private int netContent;
        private int postage;
        private boolean isPostFree;
        private boolean isOriginalPrice;
        private double totalPrice;
        private int scId;
        private int commId;
        private String commName;
        private String commPic;
        private String commHttpPic;
        private int number;
        private double price;

        public int getNetContent() {
            return netContent;
        }

        public void setNetContent(int netContent) {
            this.netContent = netContent;
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

        public boolean isOriginalPrice() {
            return isOriginalPrice;
        }

        public void setOriginalPrice(boolean originalPrice) {
            isOriginalPrice = originalPrice;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public int getScId() {
            return scId;
        }

        public void setScId(int scId) {
            this.scId = scId;
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

        public String getCommPic() {
            return commPic;
        }

        public void setCommPic(String commPic) {
            this.commPic = commPic;
        }

        public String getCommHttpPic() {
            return commHttpPic;
        }

        public void setCommHttpPic(String commHttpPic) {
            this.commHttpPic = commHttpPic;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    public class Receivingaddress {

        private int crId;
        private String contacts;
        private String contactMobile;
        private String fullName;

        public String getContacts() {
            return contacts;
        }

        public void setContacts(String contacts) {
            this.contacts = contacts;
        }

        public int getCrId() {
            return crId;
        }

        public void setCrId(int crId) {
            this.crId = crId;
        }

        public String getContactMobile() {
            return contactMobile;
        }

        public void setContactMobile(String contactMobile) {
            this.contactMobile = contactMobile;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}
