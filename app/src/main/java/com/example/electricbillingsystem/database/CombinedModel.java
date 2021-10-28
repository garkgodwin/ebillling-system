package com.example.electricbillingsystem.database;

public class CombinedModel {

    private int billID;
    private String callName;
    private double netAmount;
    private String paymentDateTime;
    private double consumptionUnit;



    public CombinedModel(int billID, String callName, double netAmount, String paymentDateTime, double consumptionUnit) {
        this.billID = billID;
        this.callName = callName;
        this.netAmount = netAmount;
        this.paymentDateTime = paymentDateTime;
        this.consumptionUnit = consumptionUnit;
    }

    public CombinedModel(){

    }

    @Override
    public String toString() {
        return billID  + "\t\t" +  callName + "\t\t\t\t\t" + netAmount + "\t\t\t\t\t\t" + paymentDateTime + "\t\t\t\t\t\t" + consumptionUnit;
    }


    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public String getPaymentDateTime() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(String paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

    public double getConsumptionUnit() {
        return consumptionUnit;
    }

    public void setConsumptionUnit(double consumptionUnit) {
        this.consumptionUnit = consumptionUnit;
    }
}
