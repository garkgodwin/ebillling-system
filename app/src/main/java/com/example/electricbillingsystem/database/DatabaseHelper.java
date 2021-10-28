package com.example.electricbillingsystem.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;



    public DatabaseHelper(@Nullable Context context) {
        super(context, "electricBilling.db", null, 11 );
    }


    //this is called the first time a database is accessed. There should be code in here to create a new database.
    @Override
    public void onCreate(SQLiteDatabase db) {

        //This part is for the customer data
        String createCustomerTableStatement = "CREATE TABLE " +
                "TblCustomer(" +
                "customerID integer PRIMARY KEY, " +
                "firstName text, " +
                "middleName text, " +
                "lastName text, " +
                "title text, " +
                "address text, " +
                "email text," +
                "pin integer" +
                ");";
        //This part is to create the customer's meter boards data
        String createMeterBoardDataTableStatement = "CREATE TABLE " +
                "TblMeterBoard(" +
                "meterBoardID integer PRIMARY KEY, " +
                "installationControlPoint text, " +
                "callName text, " + //This meterBoardID will my rrNumber
                "totalKWH double, " + //This will be the kWh of the meter board
                "status text" +
                ");"; //this will get the status if the meter board is running or not(let's go with sleeping)



        //ELECTRICITY BOARD TABLE : This will be implemented when the reader comes to read the meter board
        // the board will have all the records of the meter board
        String createElectricityBoardTableStatement = "CREATE TABLE " +
                "TblElectricityBoard(" +
                "electricityBoardID integer PRIMARY KEY, " +
                "previousReading double, " +
                "presentReading double, " +
                "consumptionUnit double, " +
                "readingDate date, " +
                "dueDate date, " +
                "meterBoardID integer, " +
                "FOREIGN KEY (meterBoardID) REFERENCES TblMeterBoard(meterBoardID)" +
                ")";


        //THE BILLING TABLE : this will be implemented after payment
        //total of fee is inside the Invoice Table to avoid redundancy
        String createBillingTableStatement = "CREATE TABLE " +
                "TblBilling(" +
                "billID integer PRIMARY KEY, " +
                "paymentDateTime datetime, " +
                "paidAmount decimal(10,2), " +
                "meterBoardID integer, " +
                "invoiceID integer, "+
                "FOREIGN KEY (meterBoardID) REFERENCES TblMeterBoard(meterBoardID)," + //info of meter board
                "FOREIGN KEY (invoiceID) REFERENCES TblInvoice(invoiceID)" +          //info of invoice
                ");";



        //This table is implemented after billing
        String createInvoiceTableStatement = "CREATE TABLE " +
                "TblInvoice(" +
                "invoiceID integer PRIMARY KEY, " +
                "fixedCharge decimal(10, 2), " +    //minimum fixed charge
                "energyCharge decimal(10, 2), " +   //energy charge
                "tax decimal(10, 2), " +            //tax amount in total
                "billAmount decimal(10, 2), " +     //total bill amount = tax + energy charge + fixed charge
                "interest decimal(10, 2), " +       //interest in percentage
                "previousBalance decimal(10, 2), " +    //previous balance
                "interestPreBalance decimal(10, 2), " + //previous interest balance amount
                "others decimal(10, 2), " +         //other charges
                "netAmount decimal(10, 2), " +    //GRAND TOTAL
                "electricityBoardID integer, "+
                "FOREIGN KEY (electricityBoardID) REFERENCES TblElectricityBoard(electricityBoardID)" + //includes: reading date, due date
                ");";

        /*String finalStatement = createCustomerTableStatement +
                createMeterBoardDataTableStatement +
                createElectricityBoardTableStatement +
                createBillingTableStatement +
                createInvoiceTableStatement;
*/
        db.execSQL(createCustomerTableStatement);
        db.execSQL(createMeterBoardDataTableStatement);
        db.execSQL(createElectricityBoardTableStatement);
        db.execSQL(createBillingTableStatement);
        db.execSQL(createInvoiceTableStatement);
    }
    // this is called if the database version number changes. It prevents previous users apps from breaking when you change the database design.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TblCustomer");
        db.execSQL("DROP TABLE IF EXISTS TblMeterBoard");
        db.execSQL("DROP TABLE IF EXISTS TblElectricityBoard");
        db.execSQL("DROP TABLE IF EXISTS TblBilling");
        db.execSQL("DROP TABLE IF EXISTS TblInvoice");
        onCreate(db);
    }

    //IF CUSTOMER DATA IS EMPTY SO IS EVERYTHING, THIS ALGORITHM WILL RUN ONCE DATA IS EMPTY
    public int countCustomer(){
        int count = 0;
        String sql = "SELECT COUNT(*) FROM TblCustomer";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        count = cursor.getInt(0);
        cursor.close();
        return count;
    }




    //==============================CUSTOMER======================
    public boolean registerCustomer(CustomerModel customerModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("firstName", customerModel.getFirstName());
        cv.put("middleName", customerModel.getMiddleName());
        cv.put("lastName", customerModel.getLastName());
        cv.put("title", customerModel.getTitle());
        cv.put("address", customerModel.getAddress());
        cv.put("email", customerModel.getEmail());
        cv.put("pin", customerModel.getPin());

        long insert = db.insert("TblCustomer", null, cv);
        if(insert == -1){
            return false;
        }
        else{
            return  true;
        }

    }

    public boolean updateCustomer(String id, String firstName, String middleName, String lastName,
                                  String title, String address, String email, String pin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("customerID", id);
        cv.put("firstName", firstName);
        cv.put("middleName", middleName);
        cv.put("lastName", lastName);
        cv.put("title", title);
        cv.put("address", address);
        cv.put("email", email);
        cv.put("pin", pin);
        String whereClause = "customerID = ?";
        int update = db.update("TblCustomer", cv, whereClause, new String[]{ id });
        if(update == 1){
            return true;
        }
        else{
            return false;
        }
    }


    public boolean isPinRight(String pin){
        if(pin == ""){
            return false;
        }
        String sql = "SELECT * FROM TblCustomer WHERE pin = " + pin;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return true;
        }
        else{
            return false;
        }
    }

    public CustomerModel selectCustomer(){
        SQLiteDatabase db = this.getWritableDatabase();
        CustomerModel customerModel = new CustomerModel();
        String sql = "SELECT * FROM TblCustomer WHERE customerID = (SELECT min(customerID) FROM TblCustomer)";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.getCount() >= 1) {
            while (cursor.moveToNext()) {
                customerModel.setCustomerID(cursor.getInt(cursor.getColumnIndex("customerID")));
                customerModel.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
                customerModel.setMiddleName(cursor.getString(cursor.getColumnIndex("middleName")));
                customerModel.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
                customerModel.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                customerModel.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                customerModel.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                customerModel.setPin(cursor.getInt(cursor.getColumnIndex("pin")));
            }
        }

        return customerModel;
    }

    public Cursor selectCustomerCursor(){
        String sql = "SELECT * FROM TblCustomer";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    //============================INSIDE PROFILE NONE PROFILE INFO:
    public Cursor getMeterSummary(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT (SELECT COUNT(*) FROM TblMeterBoard WHERE status='Awake') as awake, " +
                "(SELECT COUNT(*) FROM TblMeterBoard WHERE status='Sleeping') AS asleep, " +
                "COUNT(*) AS totalCount, " +
                "SUM(totalKWH)  as sumOfTotals " +
                "FROM TblMeterBoard;";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public Cursor getMaxEnergy(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT SUM(consumptionUnit) AS sumEnergy, TblMeterBoard.callName FROM TblElectricityBoard " +
                "INNER JOIN TblMeterBoard " +
                "ON TblMeterBoard.meterBoardID = TblElectricityBoard.meterBoardID " +
                "GROUP BY TblElectricityBoard.meterBoardID;";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public Cursor getUnpaidBills(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT COUNT(*) as unpaidCount, callName FROM TblBilling " +
                "INNER JOIN TblMeterBoard " +
                "ON TblMeterBoard.meterBoardID = TblBilling.meterBoardID " +
                "WHERE paidAmount = 0 " +
                "GROUP BY TblBilling.meterBoardID;";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public Cursor getTotalBills(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT (SELECT SUM(netAmount) FROM TblBilling " +
                "INNER JOIN TblInvoice " +
                "ON TblInvoice.invoiceID = TblBilling.billID " +
                "WHERE TblBilling.paidAmount = 0) AS unpaidTotal, " +
                "(SELECT SUM(netAmount) FROM TblBilling " +
                "INNER JOIN TblInvoice " +
                "ON TblInvoice.invoiceID = TblBilling.billID " +
                "WHERE TblBilling.paidAmount != 0) AS paidTotal ";
        Cursor cursor = db.rawQuery(sql ,null);
        return cursor;
    }
    //============================METER BOARD =====================
    //register and update for meter board, if we implement delete, we should also delete the following data
    //that contains the id of this meter board
    public boolean registerMeterBoard(MeterBoardModel meterBoardModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("totalKWH", meterBoardModel.getTotalKWH());
        cv.put("installationControlPoint", meterBoardModel.getInstallationControlPoint());
        cv.put("callName", meterBoardModel.getCallName());
        cv.put("status", meterBoardModel.getStatus());
        long insert = db.insert("TblMeterBoard", null, cv);
        if(insert == -1){
            return  false;
        }
        else{
            return  true;
        }
    }

    public boolean updateMeterBoard(String id, String icp, String callName, String totalKWH, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("meterBoardID", id);
        cv.put("installationControlPoint", icp);
        cv.put("callName", callName);
        cv.put("totalKWH", totalKWH);
        cv.put("status", status);
        String whereClause = "meterBoardID = ?";
        int update = db.update("TblMeterBoard", cv, whereClause, new String[]{id});
        if(update == 1){
            return  true;
        }
        else{
            return  false;
        }
    }

    //This will count the meterBoardID inside the TblElectricityBoard
    public int countElectricityBoardData(int meterBoardID){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql1 = "SELECT * FROM TblElectricityBoard WHERE meterBoardID = " + meterBoardID;

        Cursor cursor = db.rawQuery(sql1, null);
        if(cursor.getCount() >= 1){
            return cursor.getCount();
        }
        else{
            return 0;
        }
    }
    //before here: go to countElectricityBoardData
    public boolean deleteMeterBoard(int meterBoardID){
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "DELETE FROM TblMeterBoard WHERE meterBoardID = " + meterBoardID;

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return  true;
        }
        else{
            return false;
        }
    }

    public int countMeterBoard(){
        String sql = "SELECT * FROM TblMeterBoard";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor.getCount();
        }
        else{
            return 0;
        }
    }
    //for electricHome count
    public int countElectricBoard(String meterName){
        String sql = "SELECT * FROM TblElectricityBoard \n" +
                "INNER JOIN TblMeterBoard \n" +
                "ON TblMeterBoard.meterBoardID = TblElectricityBoard.meterBoardID \n" +
                "WHERE TblMeterBoard.callName = '" +meterName + "' " +
                "GROUP BY TblElectricityBoard.electricityBoardID;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor.getCount();
        }
        else{
            return 0;
        }
    }
//this reads all for meterboard data
    public List<MeterBoardModel> readMeterBoards() {
        List<MeterBoardModel> recordsList = new ArrayList<>();
        String sql = "SELECT * FROM TblMeterBoard";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("meterBoardID")));
                double totalKWH = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totalKWH")));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                String icp = cursor.getString(cursor.getColumnIndex("installationControlPoint"));
                String callName = cursor.getString(cursor.getColumnIndex("callName"));
                MeterBoardModel meterBoardModel = new MeterBoardModel(id, icp, callName, totalKWH, status);
                recordsList.add(meterBoardModel);
            } while (cursor.moveToNext());
        }
        else{
            return  null;
        }
        cursor.close();
        db.close();
        return recordsList;
    }


    public Cursor getMeterBoardsCursor(String search){
        String sql = "SELECT * FROM TblMeterBoard WHERE callName LIKE '%" +search+ "%'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }
    public Cursor getMeterBoardByID(int id){
        String sql = "SELECT * FROM TblMeterBoard WHERE meterBoardID = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    //this method will set the sum of consumption unit if the value is null from db
    public void setSumOfConsumptionUnit(){
        String sql = "UPDATE TblMeterBoard SET totalKWH = " +
                "(SELECT SUM(consumptionUnit) " +
                "FROM TblElectricityBoard " +
                "WHERE TblElectricityBoard.meterBoardID = TblMeterBoard.meterBoardID);";
        String sql1 = "UPDATE TblMeterBoard SET totalKWH = 0 " +
                "WHERE totalKWH IS NULL;";
        String[] statements = new String[]{sql, sql1};
        SQLiteDatabase db = this.getWritableDatabase();
        for(String sqlValue : statements){
            db.execSQL(sqlValue );
        }
    }


    //gets only 1 type
    public List<String> getMeterBoardNamesForElectricityBoard(){
        List<String> meterBoards = new ArrayList<>();
        SQLiteDatabase db=  this.getWritableDatabase();
        String sql = "SELECT callName FROM TblMeterBoard WHERE totalKWH != 0";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                String data = cursor.getString(cursor.getColumnIndex("callName"));
                meterBoards.add(data);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return meterBoards;
    }
//this read 1 data from meterboard to billing form activtiy
    public List<String> getMeterBoards(String type){
        List<String> meterBoards = new ArrayList<String>();
        String column = "callName";
        if(type == "name"){
            column = "callName";
        }
        else if(type == "icp"){
            column = "installationControlPoint";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + column + " FROM TblMeterBoard";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                String data = cursor.getString(cursor.getColumnIndex(column));
                meterBoards.add(data);
            }
            while (cursor.moveToNext());
        }
        else{
            return  null;
        }
        cursor.close();
        db.close();
        return  meterBoards;


    }


    public boolean meterBoardExists(String callName1, String icp){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM TblMeterBoard WHERE callName = '" + callName1 + "' OR installationControlPoint = '" + icp +"'";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return  true;
        }
        else{
            return  false;
        }
    }
    //==============================ELECTRICITY BOARD ===============

    public int addElectricityData(ElectricityBoardModel electricityBoardModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("meterBoardID", electricityBoardModel.getMeterBoardID());
        cv.put("previousReading", electricityBoardModel.getPreviousReading());
        cv.put("presentReading", electricityBoardModel.getPresentReading());
        cv.put("consumptionUnit", electricityBoardModel.getConsumptionUnit());
        cv.put("readingDate", electricityBoardModel.getReadingDate());
        cv.put("dueDate", electricityBoardModel.getDueDate());

        long insert = db.insert("TblElectricityBoard", null, cv);

        return (int)insert;
    }
    public boolean updateElectricityData(String id, String meterBoardID, String previousReading, String presentReading,
                                         String consumptionUnit, String readingDate, String dueDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String sql = "SELECT * FROM TblElectricityBoard " +
                "WHERE dueDate = '" + dueDate + "'" +
                " AND meterBoardID = '"+meterBoardID+"'" +
                " AND electricityBoardID != " + id;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.getCount() == 0) {
            cv.put("meterBoardID", meterBoardID);
            cv.put("previousReading", previousReading);
            cv.put("presentReading", presentReading);
            cv.put("consumptionUnit", consumptionUnit);
            cv.put("readingDate", readingDate);
            cv.put("dueDate", dueDate);

            String whereClause = "electricityBoardID = ?";
            int update = db.update("TblElectricityBoard", cv, whereClause, new String[]{id});
            if (update == 1) {
                return true;
            } else {
                return false;
            }
        }
        else{
            return false;
        }
    }
    public boolean deleteElectricityData(ElectricityBoardModel electricityBoardModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM TblElectricityBoard WHERE electricityBoardID = " + electricityBoardModel.getElectricityBoardID();

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            return  true;
        }
        else{
            return false;
        }
    }

    public int getMeterBoardID(String callName, String icp){
        int returnData = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT meterBoardID FROM TblMeterBoard " +
                "WHERE callName = '"+callName+"' OR installationControlPoint = '"+icp+"'";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            returnData = Integer.parseInt(cursor.getString(cursor.getColumnIndex("meterBoardID")));
        }
        else{
            returnData = 0;
        }

        return returnData;
    }

    //===========================BILLING============================
    public boolean addBillingData(BillingModel billingModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("meterBoardID", billingModel.getMeterBoardID());
        cv.put("invoiceID", billingModel.getInvoiceID());
        cv.put("paymentDateTime", billingModel.getPaymentDateTime());
        cv.put("paidAmount", billingModel.getPaidAmount());
        long insert = db.insert("TblBilling", null, cv);
        if(insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean updateBillingData(String id, String meterBoardID, String invoiceID,
                                     String paymentDateTime, String paidAmount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("billID", id);
        cv.put("meterBoardID", meterBoardID);
        cv.put("invoiceID", invoiceID);
        cv.put("paymentDateTime", paymentDateTime);
        cv.put("paidAmount", paidAmount);
        String whereClause = "billID = ?";
        int update = db.update("TblBilling", cv, whereClause, new String[]{id});
        if(update == 1){
            return  true;
        }
        else{
            return  false;
        }
    }
    public boolean updateAllEmptyBills(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE TblBilling " +
                "SET paidAmount = 0 " +
                "WHERE billID != 0 " +
                "AND paidAmount IS NULL " +
                "OR paidAmount = ''";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            cursor.close();
            db.close();
            return true;
        }
        else{
            cursor.close();
            db.close();
            return false;
        }
    }
    public boolean deleteBillingData(BillingModel billingModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM TblBilling WHERE billID = " + billingModel.getBillID();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            return true;
        }
        else{
            return false;
        }
    }


    //==========================INVOICE================================
    public int addInvoiceData(InvoiceModel invoiceModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("electricityBoardID", invoiceModel.getElectricityBoardID());
        cv.put("fixedCharge", invoiceModel.getFixedCharge());
        cv.put("energyCharge", invoiceModel.getEnergyCharge());
        cv.put("tax", invoiceModel.getTax());
        cv.put("billAmount", invoiceModel.getBillAmount());
        cv.put("interest", invoiceModel.getInterest());
        cv.put("previousBalance", invoiceModel.getPreviousBalance());
        cv.put("interestPreBalance", invoiceModel.getInterestPreBalance());
        cv.put("others", invoiceModel.getOthers());
        cv.put("netAmount", invoiceModel.getNetAmount());
        long insert  = db.insert("TblInvoice", null, cv);
        return (int) insert;
    }
    public boolean updateInvoiceData(String id, String electricityBoardID, String fixedCharge, String energyCharge,
                                     String tax, String billAmount, String interest,
                                     String previousBalance, String interestPreBalance,
                                     String others, String netAmount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("invoiceID", id);
        cv.put("electricityBoardID", electricityBoardID);
        cv.put("fixedCharge", fixedCharge);
        cv.put("energyCharge", energyCharge);
        cv.put("tax", tax);
        cv.put("billAmount", billAmount);
        cv.put("interest", interest);
        cv.put("previousBalance", previousBalance);
        cv.put("interestPreBalance", interestPreBalance);
        cv.put("others", others);
        cv.put("netAmount", netAmount);
        String whereClause = "invoiceID = ?";
        int update = db.update("TblInvoice", cv, whereClause, new String[]{id});
        if(update == 1){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean deleteInvoiceData(InvoiceModel invoiceModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM TblInvoice WHERE invoiceID = " + invoiceModel.getInvoiceID();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            return true;
        }
        else{
            return false;
        }
    }




    //=============BILLING - INVOICE - METER BOARD - ELECTRICITY BOARD===========
    public Cursor viewAllMain(String search) {
        String sql = "SELECT TblBilling.billID, " +
                "TblMeterBoard.callName, " +
                "TblInvoice.netAmount, " +
                "TblBilling.paymentDateTime, " +
                "TblElectricityBoard.consumptionUnit " +
                "FROM TblBilling " +
                "LEFT JOIN TblInvoice " +
                "ON TblInvoice.invoiceID = TblBilling.invoiceID " +
                "LEFT JOIN TblElectricityBoard " +
                "ON TblElectricityBoard.electricityBoardID = TblInvoice.electricityBoardID " +
                "LEFT JOIN TblMeterBoard " +
                "ON TblMeterBoard.meterBoardID = TblBilling.meterBoardID " +
                "WHERE TblMeterBoard.callName LIKE '%" + search + "%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    //count check the database
    //This method will check if the due date of electricity board already exist
    public int countExistingDueDate(int id, String dueDate){
        int data = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM TblElectricityBoard WHERE meterBoardID = '" + id + "' AND dueDate = '" + dueDate+ "'";
        Cursor cursor = db.rawQuery(sql, null);
        data = cursor.getCount();
        cursor.close();
        return data;
    }

    //view single rows using bill id for form
    public ElectricityBoardModel viewElectricityBoardData(int billID){
        SQLiteDatabase db = this.getWritableDatabase();
        ElectricityBoardModel electricityBoardModel;
        String sql = "SELECT * FROM TblElectricityBoard " +
                "INNER JOIN TblInvoice " +
                "ON TblInvoice.electricityBoardID = TblElectricityBoard.electricityBoardID " +
                "INNER JOIN TblBilling " +
                "ON TblBilling.invoiceID = TblInvoice.invoiceID " +
                "WHERE TblBilling.billID = " + billID;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("electricityBoardID")));
                double previousReading = Double.parseDouble(cursor.getString(cursor.getColumnIndex("previousReading")));
                double presentReading = Double.parseDouble(cursor.getString(cursor.getColumnIndex("presentReading")));
                double consumptionUnit = Double.parseDouble(cursor.getString(cursor.getColumnIndex("consumptionUnit")));
                String readingDate = cursor.getString(cursor.getColumnIndex("readingDate"));
                String dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));
                int meterBoardID = 0;
                electricityBoardModel = new ElectricityBoardModel(
                        id, meterBoardID, previousReading, presentReading, consumptionUnit, readingDate, dueDate);
            }
            while (cursor.moveToNext());
        }
        else{
            electricityBoardModel = null;
        }
        cursor.close();
        db.close();
        return electricityBoardModel;
    }

    public InvoiceModel viewInvoiceData(int billID){
        SQLiteDatabase db = this.getWritableDatabase();
        InvoiceModel invoiceModel;
        String sql = "SELECT * FROM TblInvoice WHERE invoiceID = " +
                "(SELECT invoiceID FROM TblBilling WHERE billID = " + billID + ")";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("invoiceID")));
                double fixedCharge  = Double.parseDouble(cursor.getString(cursor.getColumnIndex("fixedCharge")));
                double energyCharge  = Double.parseDouble(cursor.getString(cursor.getColumnIndex("energyCharge")));
                double tax  = Double.parseDouble(cursor.getString(cursor.getColumnIndex("tax")));
                double billAmount  = Double.parseDouble(cursor.getString(cursor.getColumnIndex("billAmount")));
                double interest  = Double.parseDouble(cursor.getString(cursor.getColumnIndex("interest")));
                double previousBalance  = Double.parseDouble(cursor.getString(cursor.getColumnIndex("previousBalance")));
                double interestPreBalance  = Double.parseDouble(cursor.getString(cursor.getColumnIndex("interestPreBalance")));
                double others  = Double.parseDouble(cursor.getString(cursor.getColumnIndex("others")));
                double netAmount  = Double.parseDouble(cursor.getString(cursor.getColumnIndex("netAmount")));
                int electricityBoardID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("electricityBoardID")));
                invoiceModel = new InvoiceModel(id, electricityBoardID,
                        fixedCharge, energyCharge, tax, billAmount, interest,
                        previousBalance, interestPreBalance, others, netAmount);
            }
            while (cursor.moveToNext());
        }
        else{
            invoiceModel = null;
        }
        cursor.close();
        db.close();
        return  invoiceModel;
    }

    public BillingModel viewBillingData(int billID){
        SQLiteDatabase db = this.getWritableDatabase();
        BillingModel billingModel;
        String sql =  "SELECT * FROM TblBilling WHERE billID = " + billID;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                int id = billID;
                String paymentDateTime = cursor.getString(cursor.getColumnIndex("paymentDateTime"));
                double paidAmount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("paidAmount")));
                int meterBoardID = 0;
                int invoiceID = 0;
                billingModel = new BillingModel(billID, meterBoardID, invoiceID, paymentDateTime, paidAmount);
            }
            while (cursor.moveToNext());
        }
        else{
            billingModel = null;
        }
        cursor.close();
        db.close();
        return billingModel;

    }


    public boolean deleteEBoard(int billID){
        boolean deleted = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM TblElectricityBoard " +
                "WHERE electricityBoardID " +
                "NOT IN(SELECT electricityBoardID " +
                "FROM TblInvoice WHERE invoiceID)";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()) {
            deleted = true;
        }
        else {
            deleted = false;
        }
        cursor.close();
        db.close();
        return deleted;
    }

    public boolean deleteInvoice(int billID){
        boolean deleted = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM TblInvoice WHERE invoiceID " +
                "NOT IN(SELECT invoiceID FROM TblBilling)";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            deleted = true;
        }
        else{
            deleted = false;
        }
        cursor.close();
        db.close();
        return deleted;
    }

    public boolean  deleteBilling(int billID){
        boolean deleted = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM TblBilling WHERE billID = " + billID;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            deleted = true;
        }
        else{
            deleted = false;
        }
        cursor.close();
        db.close();
        return deleted;
    }


    public List<ElectricityBoardModel> displayElectricityBoard(){

        List<ElectricityBoardModel> recordsList = new ArrayList<>();
        String sql = "SELECT * FROM TblElectricityBoard";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("electricityBoardID")));
                double prevReading = Double.parseDouble(cursor.getString(cursor.getColumnIndex("previousReading")));
                double presentReading = Double.parseDouble(cursor.getString(cursor.getColumnIndex("presentReading")));
                double consumptionUnit = cursor.getDouble(cursor.getColumnIndex("consumptionUnit"));
                String readingDate = cursor.getString(cursor.getColumnIndex("readingDate"));
                String dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));
                int meterBoardID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("meterBoardID")));
                ElectricityBoardModel electricityBoardModel = new ElectricityBoardModel(id, meterBoardID, prevReading, presentReading,
                        consumptionUnit, readingDate, dueDate);
                recordsList.add(electricityBoardModel);
            } while (cursor.moveToNext());
        }
        else{
            return  null;
        }
        cursor.close();
        db.close();
        return recordsList;
    }



    //===================================FOR ELECTRICITY BOARD MODEL


    public int countElectricityData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM TblELectricityBoard";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor.getCount();
        }
        else{
            return 0;
        }
    }

    public Cursor getEData(int meterBoardID){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT readingDate, consumptionUnit FROM TblElectricityBoard WHERE meterBoardID = '"+meterBoardID+"'ORDER BY readingDate ASC;";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor;
        }
        else{
            return null;
        }
    }

    public String selectMaxDate(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT MAX(readingDate) as readingDate FROM TblElectricityBoard";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor.getString(cursor.getColumnIndex("readingDate"));
        }
        else{
            return "";
        }
    }

    public String selectMinDate(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT MIN(readingDate) as readingDate FROM TblElectricityBoard";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor.getString(cursor.getColumnIndex("readingDate"));
        }
        else{
            return "";
        }
    }


    public Double selectMaxEnergy(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT MAX(consumptionUnit) as consumptionUnit FROM TblElectricityBoard";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor.getDouble(cursor.getColumnIndex("consumptionUnit"));
        }
        else{
            return null;
        }
    }

    public Double selectMinEnergy(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT MIN(consumptionUnit) as consumptionUnit FROM TblElectricityBoard";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor.getDouble(cursor.getColumnIndex("consumptionUnit"));
        }
        else{
            return null;
        }
    }




    //====================================================QUICK VIEW

    public int getBillingCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT COUNT(billID) as billCount FROM TblBilling";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor.getInt(cursor.getColumnIndex("billCount"));
        }
        else{
            return 0;
        }
    }


    public Cursor getBillingGeneralData(){ //default muna natin one hay no sleep
        SQLiteDatabase db = this.getReadableDatabase();
        String sql ="SELECT TblMeterBoard.meterBoardID, " +
                "SUM(TblInvoice.netAmount) AS totalNetAmount " +
                "FROM TblBilling " +
                "INNER JOIN TblInvoice " +
                "ON TblInvoice.invoiceID = TblBilling.invoiceID " +
                "INNER JOIN TblElectricityBoard " +
                "ON TblElectricityBoard.electricityBoardID = TblInvoice.invoiceID " +
                "INNER JOIN TblMeterBoard " +
                "ON TblMeterBoard.meterBoardID = TblElectricityBoard.meterBoardID " +
                "GROUP BY TblMeterBoard.meterBoardID";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            return cursor;
        }
        else{
            return null;
        }
    }

    public Cursor getQuickView(int meterBoardID){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT TblInvoice.invoiceID, " +
                "dueDate, " +
                "netAmount, " +
                "paidAmount, " +
                "readingDate, " +
                "TblMeterBoard.callName " +
                "FROM TblInvoice " +
                "INNER JOIN TblElectricityBoard " +
                "ON TblElectricityBoard.electricityBoardID = TblInvoice.electricityBoardID " +
                "INNER JOIN TblMeterBoard " +
                "ON TblMeterBoard.meterBoardID = TblElectricityBoard.meterBoardID " +
                "INNER JOIN TblBilling " +
                "ON TblBilling.invoiceID = TblInvoice.invoiceID " +
                "WHERE TblMeterBoard.meterBoardID = " + meterBoardID;
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public double getMaxY(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT SUM(TblInvoice.netAmount) AS totalNetAmount " +
                "FROM TblBilling " +
                "INNER JOIN TblInvoice " +
                "ON TblInvoice.invoiceID = TblBilling.invoiceID " +
                "INNER JOIN TblElectricityBoard " +
                "ON TblElectricityBoard.electricityBoardID = TblInvoice.invoiceID " +
                "INNER JOIN TblMeterBoard " +
                "ON TblMeterBoard.meterBoardID = TblElectricityBoard.meterBoardID " +
                "GROUP BY TblMeterBoard.meterBoardID;";
        Cursor cursor = db.rawQuery(sql, null);
        double max = 0;
        if(cursor.moveToFirst()){
            do{
                double totalNetAmount = cursor.getDouble(cursor.getColumnIndex("totalNetAmount"));
                if(max == 0){
                    max = totalNetAmount;
                }
                else if(max < totalNetAmount){
                    max = totalNetAmount;
                }
            }
            while (cursor.moveToNext());
        }
        return max;
    }


    public Cursor getNotif(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT TblBilling.billID, " +
                "dueDate, " +
                "netAmount, " +
                "paidAmount, " +
                "callName " +
                "FROM TblElectricityBoard " +
                "INNER JOIN TblInvoice " +
                "ON TblInvoice.electricityBoardID = TblElectricityBoard.electricityBoardID " +
                "INNER JOIN TblBilling " +
                "ON TblBilling.invoiceID = TblInvoice.invoiceID " +
                "INNER JOIN TblMeterBoard " +
                "ON  TblMeterBoard.meterBoardID = TblElectricityBoard.meterBoardID " +
                "WHERE callName LIKE '%" + name + "%' " +
                "AND paidAmount = 0";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }


    public boolean updateBillPayment(String billID, String payment, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("paymentDateTime", date);
        cv.put("paidAmount", payment);
        String whereClause = "billID = ?";
        int update = db.update("TblBilling", cv, whereClause, new String[]{billID});
        if(update == 1){
            return true;
        }
        else{
            return false;
        }
    }


    //QUERY FOR DELETING THE DATA OF THE APP
    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlCustomer = "DELETE FROM TblCustomer;";
        String sqlBilling = "DELETE FROM TblBilling;";
        String sqlInvoice = "DELETE FROM TblInvoice;";
        String sqlElectricityBoard = "DELETE FROM TblElectricityBoard;";
        String sqlMeterBoard = "DELETE FROM TblMeterBoard;";
        ArrayList<String> queries = new ArrayList<String>();
        queries.add(sqlCustomer);
        queries.add(sqlBilling);
        queries.add(sqlInvoice);
        queries.add(sqlElectricityBoard);
        queries.add(sqlMeterBoard);
        for(String query : queries){
            Cursor cursor = db.rawQuery(query, null);
            if(cursor.moveToNext()) {
                cursor.close();
            }
        }
    }

}
