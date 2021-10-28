package com.example.electricbillingsystem.database;

public class BillingModel {

    private int billID;
    private int meterBoardID;
    private int invoiceID;
    private String paymentDateTime;
    private double paidAmount;


    public BillingModel(int billID, int meterBoardID, int invoiceID, String paymentDateTime, double paidAmount) {
        this.billID = billID;
        this.meterBoardID = meterBoardID;
        this.invoiceID = invoiceID;
        this.paymentDateTime = paymentDateTime;
        this.paidAmount = paidAmount;
    }

    public BillingModel(){

    }

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }


    public int getMeterBoardID() {
        return meterBoardID;
    }

    public void setMeterBoardID(int meterBoardID) {
        this.meterBoardID = meterBoardID;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getPaymentDateTime() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(String paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }


    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }
}
