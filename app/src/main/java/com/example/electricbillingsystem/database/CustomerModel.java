package com.example.electricbillingsystem.database;

public class CustomerModel {

    private int customerID;
    private String firstName;
    private String middleName;
    private String lastName;
    private String title;
    private String address;
    private String email;
    private int pin;

    public CustomerModel(int customerID, String firstName, String middleName, String lastName, String title, String address, String email, int pin) {
        this.customerID = customerID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.title = title;
        this.address = address;
        this.email = email;
        this.pin = pin;
    }

    public CustomerModel(){

    }
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }
}
