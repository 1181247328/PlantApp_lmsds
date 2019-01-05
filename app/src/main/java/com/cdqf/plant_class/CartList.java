package com.cdqf.plant_class;

/**
 * Created by liu on 2017/12/7.
 */

public class CartList {
    private int scId;
    private int commId;
    private String commName;
    private String commPic;
    private String commHttpPic;
    private int number;
    private int price;
    private boolean isCart = false;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isCart() {
        return isCart;
    }

    public void setCart(boolean cart) {
        isCart = cart;
    }
}
