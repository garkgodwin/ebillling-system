package com.example.electricbillingsystem.database;

public class MeterBoardModel {

    private int meterBoardID;
    private String installationControlPoint;
    private String callName;
    private double totalKWH;
    private String status;


    public MeterBoardModel(int meterBoardID, String installationControlPoint, String callName, double totalKWH, String status) {
        this.meterBoardID = meterBoardID;
        this.installationControlPoint = installationControlPoint;
        this.callName = callName;
        this.totalKWH = totalKWH;
        this.status = status;
    }

    public MeterBoardModel(){

    }


    @Override
    public String toString() {
        return meterBoardID +"\t\t\t\t" + installationControlPoint + "\t\t\t\t\t\t" +
                callName + "\t\t\t\t\t\t" + totalKWH + "\t\t\t" + status;
    }

    public String getInstallationControlPoint() {
        return installationControlPoint;
    }

    public void setInstallationControlPoint(String installationControlPoint) {
        this.installationControlPoint = installationControlPoint;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public int getMeterBoardID() {
        return meterBoardID;
    }

    public void setMeterBoardID(int meterBoardID) {
        this.meterBoardID = meterBoardID;
    }

    public double getTotalKWH() {
        return totalKWH;
    }

    public void setTotalKWH(double totalKWH) {
        this.totalKWH = totalKWH;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
