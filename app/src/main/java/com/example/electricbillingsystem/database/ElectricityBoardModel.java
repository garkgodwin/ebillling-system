package com.example.electricbillingsystem.database;

public class ElectricityBoardModel {


    private int electricityBoardID;
    private int meterBoardID;
    private double previousReading;
    private double presentReading;
    private double consumptionUnit;
    private String readingDate;
    private String dueDate;

    public ElectricityBoardModel(int electricityBoardID, int meterBoardID,
                                 double previousReading, double presentReading, double consumptionUnit, String readingDate, String dueDate) {
        this.electricityBoardID = electricityBoardID;
        this.meterBoardID = meterBoardID;
        this.previousReading = previousReading;
        this.presentReading = presentReading;
        this.consumptionUnit = consumptionUnit;
        this.readingDate = readingDate;
        this.dueDate = dueDate;
    }

    public ElectricityBoardModel(){

    }

    public int getElectricityBoardID() {
        return electricityBoardID;
    }

    public void setElectricityBoardID(int electricityBoardID) {
        this.electricityBoardID = electricityBoardID;
    }

    public int getMeterBoardID() {
        return meterBoardID;
    }

    public void setMeterBoardID(int meterBoardID) {
        this.meterBoardID = meterBoardID;
    }

    public double getPreviousReading() {
        return previousReading;
    }

    public void setPreviousReading(double previousReading) {
        this.previousReading = previousReading;
    }

    public double getPresentReading() {
        return presentReading;
    }

    public void setPresentReading(double presentReading) {
        this.presentReading = presentReading;
    }

    public double getConsumptionUnit() {
        return consumptionUnit;
    }

    public void setConsumptionUnit(double consumptionUnit) {
        this.consumptionUnit = consumptionUnit;
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
}
