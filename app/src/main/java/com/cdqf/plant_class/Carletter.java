package com.cdqf.plant_class;

/**
 * Created by liu on 2018/1/9.
 */

public class Carletter {
    private int Id;
    private int ConsumerId;
    private String PlateNumber;
    private String EntryTime;
    private String LeaveTime;
    private int ParkingTime;
    private String ParkNo;
    private int ParkingTotalFee;
    private int ParkingPaidFee;
    private int ParkingPayFee;
    private int ParkingFreeInterval;
    private int ParkingLeaveInterval;
    private boolean PayStatus;
    private String AddTime;
    private boolean IsDelete;
    private String PaymentParkingNo;
    private String EntryTimeStr;
    private String LeaveTimeStr;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getConsumerId() {
        return ConsumerId;
    }

    public void setConsumerId(int consumerId) {
        ConsumerId = consumerId;
    }

    public String getPlateNumber() {
        return PlateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        PlateNumber = plateNumber;
    }

    public String getEntryTime() {
        return EntryTime;
    }

    public void setEntryTime(String entryTime) {
        EntryTime = entryTime;
    }

    public String getLeaveTime() {
        return LeaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        LeaveTime = leaveTime;
    }

    public int getParkingTime() {
        return ParkingTime;
    }

    public void setParkingTime(int parkingTime) {
        ParkingTime = parkingTime;
    }

    public String getParkNo() {
        return ParkNo;
    }

    public void setParkNo(String parkNo) {
        ParkNo = parkNo;
    }

    public int getParkingTotalFee() {
        return ParkingTotalFee;
    }

    public void setParkingTotalFee(int parkingTotalFee) {
        ParkingTotalFee = parkingTotalFee;
    }

    public int getParkingPaidFee() {
        return ParkingPaidFee;
    }

    public void setParkingPaidFee(int parkingPaidFee) {
        ParkingPaidFee = parkingPaidFee;
    }

    public int getParkingPayFee() {
        return ParkingPayFee;
    }

    public void setParkingPayFee(int parkingPayFee) {
        ParkingPayFee = parkingPayFee;
    }

    public int getParkingFreeInterval() {
        return ParkingFreeInterval;
    }

    public void setParkingFreeInterval(int parkingFreeInterval) {
        ParkingFreeInterval = parkingFreeInterval;
    }

    public int getParkingLeaveInterval() {
        return ParkingLeaveInterval;
    }

    public void setParkingLeaveInterval(int parkingLeaveInterval) {
        ParkingLeaveInterval = parkingLeaveInterval;
    }

    public boolean isPayStatus() {
        return PayStatus;
    }

    public void setPayStatus(boolean payStatus) {
        PayStatus = payStatus;
    }

    public String getAddTime() {
        return AddTime;
    }

    public void setAddTime(String addTime) {
        AddTime = addTime;
    }

    public boolean isDelete() {
        return IsDelete;
    }

    public void setDelete(boolean delete) {
        IsDelete = delete;
    }

    public String getPaymentParkingNo() {
        return PaymentParkingNo;
    }

    public void setPaymentParkingNo(String paymentParkingNo) {
        PaymentParkingNo = paymentParkingNo;
    }

    public String getEntryTimeStr() {
        return EntryTimeStr;
    }

    public void setEntryTimeStr(String entryTimeStr) {
        EntryTimeStr = entryTimeStr;
    }

    public String getLeaveTimeStr() {
        return LeaveTimeStr;
    }

    public void setLeaveTimeStr(String leaveTimeStr) {
        LeaveTimeStr = leaveTimeStr;
    }
}
