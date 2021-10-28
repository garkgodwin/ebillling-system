package com.example.electricbillingsystem.database;

public class QuickViewModel {
    private int invoiceID;
    private double netAmount;
    private String readingDate;
    private String dueDate;
    private double paidAmount;

    public QuickViewModel(int invoiceID, double netAmount, String readingDate, String dueDate, double paidAmount) {
        this.invoiceID = invoiceID;
        this.netAmount = netAmount;
        this.readingDate = readingDate;
        this.dueDate = dueDate;
        this.paidAmount = paidAmount;
    }

    public QuickViewModel(){

    }

    @Override
    public String toString() {
        return invoiceID +"\t\t\t\t" + netAmount +"\t\t\t\t" + readingDate + "\t\t\t\t" + dueDate + "\t\t\t\t" + paidAmount ;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public String getReadingDate() {
        return readingDate;
    }

    public void setReadingDate(String readingDate) {
        this.readingDate = readingDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }
}
