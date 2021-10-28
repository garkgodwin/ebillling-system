package com.example.electricbillingsystem.database;

public class NotificationModel {
    private int electricityBoardID;
    private String dueDate;
    private double netAmount;
    private double paidAmount;
    private String callName;


    public NotificationModel(int electricityBoardID, String dueDate, double netAmount, double paidAmount, String callName) {
        this.electricityBoardID = electricityBoardID;
        this.dueDate = dueDate;
        this.netAmount = netAmount;
        this.paidAmount = paidAmount;
        this.callName = callName;
    }

    @Override
    public String toString() {
        return "NotifcationModel{" +
                "electricityBoardID=" + electricityBoardID +
                ", dueDate='" + dueDate + '\'' +
                ", netAmount=" + netAmount +
                ", paidAmount=" + paidAmount +
                ", callName='" + callName + '\'' +
                '}';
    }

    public int getElectricityBoardID() {
        return electricityBoardID;
    }

    public void setElectricityBoardID(int electricityBoardID) {
        this.electricityBoardID = electricityBoardID;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }
}
