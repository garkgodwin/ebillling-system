package com.example.electricbillingsystem.database;

public class InvoiceModel {

    private int invoiceID;
    private int electricityBoardID;
    private double fixedCharge;
    private double energyCharge;
    private double tax;
    private double billAmount;
    private double interest;
    private double previousBalance;
    private double interestPreBalance;
    private double others;
    private double netAmount;

    public InvoiceModel(int invoiceID, int electricityBoardID,
                        double fixedCharge, double energyCharge, double tax,
                        double billAmount, double interest, double previousBalance,
                        double interestPreBalance, double others, double netAmount) {
        this.invoiceID = invoiceID;
        this.electricityBoardID = electricityBoardID;
        this.fixedCharge = fixedCharge;
        this.energyCharge = energyCharge;
        this.tax = tax;
        this.billAmount = billAmount;
        this.interest = interest;
        this.previousBalance = previousBalance;
        this.interestPreBalance = interestPreBalance;
        this.others = others;
        this.netAmount = netAmount;
    }

    public InvoiceModel(){

    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public int getElectricityBoardID() {
        return electricityBoardID;
    }

    public void setElectricityBoardID(int electricityBoardID) {
        this.electricityBoardID = electricityBoardID;
    }

    public double getFixedCharge() {
        return fixedCharge;
    }

    public void setFixedCharge(double fixedCharge) {
        this.fixedCharge = fixedCharge;
    }

    public double getEnergyCharge() {
        return energyCharge;
    }

    public void setEnergyCharge(double energyCharge) {
        this.energyCharge = energyCharge;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(double previousBalance) {
        this.previousBalance = previousBalance;
    }

    public double getInterestPreBalance() {
        return interestPreBalance;
    }

    public void setInterestPreBalance(double interestPreBalance) {
        this.interestPreBalance = interestPreBalance;
    }

    public double getOthers() {
        return others;
    }

    public void setOthers(double others) {
        this.others = others;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }
}
