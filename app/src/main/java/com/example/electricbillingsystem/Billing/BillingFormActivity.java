package com.example.electricbillingsystem.Billing;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.BillingModel;
import com.example.electricbillingsystem.database.DatabaseHelper;
import com.example.electricbillingsystem.database.ElectricityBoardModel;
import com.example.electricbillingsystem.database.InvoiceModel;

import java.util.Calendar;
import java.util.List;

public class BillingFormActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    DatabaseHelper databaseHelper;

    EditText txtExtra1, txtExtra2, txtExtra3, txtExtra4;


    //for selecting meterboard
    Button btnChangeSearch;
    Spinner spMeterBoards;
    String type = "name"; //or icp
    String callName = "";
    String icp = "";


    //for electricityBoard
    EditText etPrevRead, etPresRead, etReadDate, etDueDate, etConsumptionUnit;

    //for invoice
    EditText etFixedCharge, etEnergyCharge, etTax, etOthers,
            etBillAmount, etInterest, etPrevBalance, etPrevInterest, etNetAmount;


    //for billing
    EditText etPaidAmount, etPaymentDateTime;

    //for date picker
    Button btnChangeReadDate, btnChangeDueDate, btnCPaymentDateTime;
    Button btnClear, btnCancel, btnAdd;
    int etType = 0; //this variable is to know which button clicked for dates



    //===================from the Home data
    //these mostly for update
    int action = 0;
    //for meter board spinner
    String oldCallName = "";
    //for electricity board
    int electricityBoardID = 0;
    double oldPrevReading = 0;
    double oldPresReading = 0;
    String oldReadDate = "";
    String oldDueDate = "";
    double oldConsUnit = 0;
    //for invoice
    int invoiceID = 0;
    double oldFixedCharge = 0;
    double oldEnergyCharge = 0;
    double oldTax = 0;
    double oldOthers = 0;
    double oldBillAmount = 0;
    double oldInterest = 0;
    double oldPrevBalance = 0;
    double oldPrevInterest= 0;
    double oldNetAmount = 0;
    //for billing
    int billID = 0;
    double paidAmount = 0;
    String paymentDate = "";

    ArrayAdapter arrayAdapter;
    @Override
    public void onBackPressed() {

        Intent i = new Intent(getBaseContext(), BillingHomeActivity.class);
        startActivity(i);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_form);

        setControls();
        databaseHelper = new DatabaseHelper(BillingFormActivity.this);


        //change meterboard
        showMeterBoards(type);






        //get extras
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                //set defaults which is empty
                action = 0;
                billID = 0;
                oldCallName = "";
            }
            else{
                //set extras to edittexts fields
                action = extras.getInt("action");
                billID = extras.getInt("billID");
                oldCallName = extras.getString("callName");
            }
        }
        else{
            action = (int) savedInstanceState.getSerializable("action");
            billID = (int) savedInstanceState.getSerializable("billID");
            oldCallName = (String) savedInstanceState.getSerializable("callName");
        }

        //set button
        if(action == 1){
            btnAdd.setText("Add");
        }
        else if(action == 2){
            btnAdd.setText("Update");
            SetOldData();
        }
        else{
            Toast.makeText(BillingFormActivity.this, "No action", Toast.LENGTH_SHORT).show();
        }




        //SEARCH METER BOARD FOR SPINNER
        btnChangeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type == "name"){
                    type = "icp";
                    showMeterBoards(type);
                }
                else if(type == "icp"){
                    type = "name";
                    showMeterBoards(type);
                }
            }
        });
        spMeterBoards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                callName = spMeterBoards.getSelectedItem().toString();
                icp = spMeterBoards.getSelectedItem().toString();
                getSelectedMeterBoard(callName, icp);
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setTextSize(14);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //set the dates to be clickable only
        btnChangeReadDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                etType = 1;
                showDatePickerDialog();
            }
        });
        btnChangeDueDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                etType = 2;
                Toast.makeText(BillingFormActivity.this, "Clicked due date", Toast.LENGTH_SHORT).show();

                showDatePickerDialog();
            }
        });

        btnCPaymentDateTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                etType = 3;
                showDatePickerDialog();
            }
        });

        //listeners that will calculate the consumption unit
        etPrevRead.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    if (!TextUtils.isEmpty(etPresRead.getText())) {
                        double presRead = Double.parseDouble(etPresRead.getText().toString());
                        double prevRead = Double.parseDouble(etPrevRead.getText().toString());
                        if (presRead >= prevRead) {
                            calculateConsumptionUnit();
                        }
                    }
                    else{
                        etConsumptionUnit.setText("");
                    }
                }
                else{
                    etConsumptionUnit.setText("");
                }

                EnableInvoice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPresRead.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    if (!TextUtils.isEmpty(etPrevRead.getText())) {
                        double presRead = Double.parseDouble(etPresRead.getText().toString());
                        double prevRead = Double.parseDouble(etPrevRead.getText().toString());
                        if (presRead >= prevRead) {
                            calculateConsumptionUnit();
                        }
                        else{
                            etConsumptionUnit.setText("");
                        }
                    }
                }
                else{
                    etConsumptionUnit.setText("");
                }

                EnableInvoice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //listeners for invoice
        etFixedCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    calculateNetAmount();
                    if (!TextUtils.isEmpty(etEnergyCharge.getText())) {
                        calculateBillAmount();
                        calculateTax();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etEnergyCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    calculateNetAmount();
                    if (!TextUtils.isEmpty(etEnergyCharge.getText())) {
                        calculateTax();
                        calculateBillAmount();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etTax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateBillAmount();
                calculateNetAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etOthers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateBillAmount();
                calculateNetAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etBillAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateNetAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etInterest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateNetAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPrevBalance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateNetAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPrevInterest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateNetAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etNetAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EnablePayment();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        //NOW FOR THE FINALE
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if there is data from everything
                if(!TextUtils.isEmpty(etPrevRead.getText())
                    && !TextUtils.isEmpty(etPresRead.getText())
                    && !TextUtils.isEmpty(etReadDate.getText())
                    && !TextUtils.isEmpty(etDueDate.getText())
                    && !TextUtils.isEmpty(etConsumptionUnit.getText())
                    && !TextUtils.isEmpty(etFixedCharge.getText())
                    && !TextUtils.isEmpty(etEnergyCharge.getText())
                    && !TextUtils.isEmpty(etTax.getText())
                    && !TextUtils.isEmpty(etOthers.getText())
                    && !TextUtils.isEmpty(etBillAmount.getText())
                    && !TextUtils.isEmpty(etInterest.getText())
                    && !TextUtils.isEmpty(etPrevBalance.getText())
                    && !TextUtils.isEmpty(etPrevInterest.getText())
                    && !TextUtils.isEmpty(etNetAmount.getText())
                    //&& !TextUtils.isEmpty(etPaidAmount.getText())
                    //&& !TextUtils.isEmpty(etPaymentDateTime.getText())
                ){
                    if(action == 1) {
                        if(TextUtils.isEmpty(etPaidAmount.getText())
                        || TextUtils.isEmpty(etPaymentDateTime.getText())){

                            AlertDialog.Builder builder = new AlertDialog.Builder(BillingFormActivity.this);
                            builder.setTitle("System Message");
                            builder.setMessage("Are you sure your payment info is empty?");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    etPaidAmount.setText("0");
                                    if(Double.parseDouble(etPaidAmount.getText().toString()) == 0) {
                                        etPaymentDateTime.setText("");
                                    }
                                    InsertData();
                                    databaseHelper.setSumOfConsumptionUnit();
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else{
                            Double paid =  Double.parseDouble(etPaidAmount.getText().toString());
                            Double net = Double.parseDouble(etNetAmount.getText().toString());
                            if(paid < net){
                                Toast.makeText(BillingFormActivity.this, "Please make sure your payment is equal or greater than the net amount.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else{
                                InsertData();

                                databaseHelper.setSumOfConsumptionUnit();
                            }
                        }
                    }
                    else if(action == 2){
                        if(TextUtils.isEmpty(etPaidAmount.getText())
                                || TextUtils.isEmpty(etPaymentDateTime.getText())){
                            AlertDialog.Builder builder = new AlertDialog.Builder(BillingFormActivity.this);
                            builder.setTitle("System Message");
                            builder.setMessage("Are you sure your payment info is empty?");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    etPaidAmount.setText("0");
                                    if(Double.parseDouble(etPaidAmount.getText().toString()) == 0) {
                                        etPaymentDateTime.setText("");
                                    }
                                    UpdateData();

                                    databaseHelper.setSumOfConsumptionUnit();
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else {
                            Double paid = Double.parseDouble(etPaidAmount.getText().toString());
                            Double net = Double.parseDouble(etNetAmount.getText().toString());
                            if (paid < net) {
                                Toast.makeText(BillingFormActivity.this, "Please make sure your payment is equal or greater than the net amount.", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                UpdateData();

                                databaseHelper.setSumOfConsumptionUnit();
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(BillingFormActivity.this, "Please complete the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BillingFormActivity.this);
                builder.setTitle("Cancel Form");
                builder.setMessage("Are you sure you want to cancel this form?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i=new Intent(getBaseContext(), BillingHomeActivity.class);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(etPrevRead.getText())
                        && !TextUtils.isEmpty(etPresRead.getText())
                        && !TextUtils.isEmpty(etReadDate.getText())
                        && !TextUtils.isEmpty(etDueDate.getText())
                        && !TextUtils.isEmpty(etConsumptionUnit.getText())
                        && !TextUtils.isEmpty(etFixedCharge.getText())
                        && !TextUtils.isEmpty(etEnergyCharge.getText())
                        && !TextUtils.isEmpty(etTax.getText())
                        && !TextUtils.isEmpty(etOthers.getText())
                        && !TextUtils.isEmpty(etBillAmount.getText())
                        && !TextUtils.isEmpty(etInterest.getText())
                        && !TextUtils.isEmpty(etPrevBalance.getText())
                        && !TextUtils.isEmpty(etPrevInterest.getText())
                        && !TextUtils.isEmpty(etNetAmount.getText())){

                    AlertDialog.Builder builder = new AlertDialog.Builder(BillingFormActivity.this);
                    builder.setTitle("Clear Form");
                    builder.setMessage("Are you sure you want to clear the values you inputted in this form?" +
                            "\nRe-input will take time.");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            clearFields();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else{
                    Toast.makeText(BillingFormActivity.this, "Clear what now?", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //CLEAR FIELDS
    private void clearFields(){
        etPrevRead.setText("");
        etPresRead.setText("");
        etReadDate.setText("");
        etDueDate.setText("");
        etConsumptionUnit.setText("");
        etFixedCharge.setText("");
        etEnergyCharge.setText("");
        etTax.setText("");
        etOthers.setText("");
        etBillAmount.setText("");
        etInterest.setText("");
        etPrevBalance.setText("");
        etPrevInterest.setText("");
        etNetAmount.setText("");
        etPaidAmount.setText("");
        etPaymentDateTime.setText("");
    }



    //these three methods run at the onCreate
    private void setControls(){
        //main buttons
        btnClear = findViewById(R.id.btnBillingClear);
        btnCancel = findViewById(R.id.btnBillingCancel);
        btnAdd = findViewById(R.id.btnBillingAdd);

        //for meterboard selection
        spMeterBoards = findViewById(R.id.spMeterBoards);
        btnChangeSearch = findViewById(R.id.btnChangeSearch);

        //for electricity board form
        etPrevRead = findViewById(R.id.etPrevRead);
        etPresRead = findViewById(R.id.etPresRead);
        etReadDate = findViewById(R.id.etReadDate);
        etDueDate = findViewById(R.id.etDueDate);
        etConsumptionUnit = findViewById(R.id.etConsumptionUnit);

        //for invoice
        etFixedCharge = findViewById(R.id.etFixedCharge);
        etEnergyCharge = findViewById(R.id.etEnergyCharge);
        etTax = findViewById(R.id.etTax);
        etOthers = findViewById(R.id.etOthers);
        etBillAmount = findViewById(R.id.etBillAmount);
        etInterest = findViewById(R.id.etInterest);
        etPrevBalance = findViewById(R.id.etPrevBalance);
        etPrevInterest = findViewById(R.id.etPrevInterest);
        etNetAmount = findViewById(R.id.etNetAmount);


        //for billing
        etPaymentDateTime = findViewById(R.id.etPaymentDateTime);
        etPaidAmount = findViewById(R.id.etPaidAmount);


        //for date
        btnChangeReadDate = findViewById(R.id.btnChangeReadDate);
        btnChangeDueDate = findViewById(R.id.btnChangeDueDate);
        btnCPaymentDateTime = findViewById(R.id.btnCPaymentDateTime);


        DisableAllTexts();
        ResetAllTexts();
    }
    private void DisableAllTexts(){
        etPrevRead.setEnabled(false);
        etPresRead.setEnabled(false);
        etReadDate.setEnabled(false);
        etDueDate.setEnabled(false);
        etConsumptionUnit.setEnabled(false);
        etFixedCharge.setEnabled(false);
        etEnergyCharge.setEnabled(false);
        etTax.setEnabled(false);
        etOthers.setEnabled(false);
        etBillAmount.setEnabled(false);
        etInterest.setEnabled(false);
        etPrevBalance.setEnabled(false);
        etPrevInterest.setEnabled(false);
        etNetAmount.setEnabled(false);
        etPaidAmount.setEnabled(false);
        etPaymentDateTime.setEnabled(false);

        //button disable
        btnCPaymentDateTime.setEnabled(false);
    }
    private void ResetAllTexts(){
        etPrevRead.setText("");
        etPresRead.setText("");
        etReadDate.setText("");
        etDueDate.setText("");
        etConsumptionUnit.setText("");
        etFixedCharge.setText("");
        etEnergyCharge.setText("");
        etTax.setText("");
        etOthers.setText("");
        etBillAmount.setText("");
        etInterest.setText("");
        etPrevBalance.setText("");
        etPrevInterest.setText("");
        etNetAmount.setText("");
        etPaidAmount.setText("");
        etPaymentDateTime.setText("");
    }


    //to enable the electicity board
    private void getSelectedMeterBoard(String callName, String icp){
        //database if the data exists in database
        try {
            boolean exists = databaseHelper.meterBoardExists(callName, icp);
            if (exists) {
                //ENABLE TO ELECTRIC BOARD PART EXCEPT FOR THE CONSUMPTION UNIT
                etPrevRead.setEnabled(true);
                etPresRead.setEnabled(true);
            } else {
                etPrevRead.setEnabled(false);
                etPresRead.setEnabled(false);
            }
        }
        catch (Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void calculateConsumptionUnit(){

        //get prev and pres read
        // subtract prev read from presRead
        //set consumption unit from the total
            double presRead = Double.parseDouble(etPresRead.getText().toString());
            double prevRead = Double.parseDouble(etPrevRead.getText().toString());
            double total = presRead - prevRead;
            String tots = String.format("%.2f", total);
            etConsumptionUnit.setText(tots);

    }

    //for datees
    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        String date = "";
        if(month < 9){
            date = year + "-0" + (month+1) + "-" + dayOfMonth;
        }
        else{
            date = year + "-" + (month+1) + "-" + dayOfMonth;
        }
        if(etType == 1){
            //read change
            int meterBoardID = databaseHelper.getMeterBoardID(callName, icp);

            etReadDate.setText(date);
        }
        else if(etType == 2){
            etDueDate.setText(date);
        }
        else if(etType == 3){
            etPaymentDateTime.setText(date);
        }
    }


    //IF THE Consumption unit is not empty: enable the invoice edittexts
    private void EnableInvoice(){
        if(TextUtils.isEmpty(etConsumptionUnit.getText())){
            etFixedCharge.setEnabled(false);
            etEnergyCharge.setEnabled(false);
            etTax.setEnabled(false);
            etOthers.setEnabled(false);
            etBillAmount.setEnabled(false);
            etInterest.setEnabled(false);
            etPrevBalance.setEnabled(false);
            etPrevInterest.setEnabled(false);
            etNetAmount.setEnabled(false);
        }
        else{
            etFixedCharge.setEnabled(true);
            etEnergyCharge.setEnabled(true);
            etTax.setEnabled(true);
            etOthers.setEnabled(true);
            etBillAmount.setEnabled(true);
            etInterest.setEnabled(true);
            etPrevBalance.setEnabled(true);
            etPrevInterest.setEnabled(true);
            etNetAmount.setEnabled(true);
        }
    }
    //Calculate the netamount in invoice
    private void calculateNetAmount(){
        double fixedCharge, energyCharge, tax, others, billAmount, interest, previousBalance, previousInterest, netAmount;
        if(!TextUtils.isEmpty(etFixedCharge.getText())
            && !TextUtils.isEmpty(etEnergyCharge.getText())
            && !TextUtils.isEmpty(etTax.getText())
            && !TextUtils.isEmpty(etOthers.getText())
            && !TextUtils.isEmpty(etBillAmount.getText())
            && !TextUtils.isEmpty(etInterest.getText())
            && !TextUtils.isEmpty(etPrevBalance.getText())
            && !TextUtils.isEmpty(etPrevInterest.getText())){

            try {
                //add fixed charge and energy charge then get tax(default)

                others = Double.parseDouble(etOthers.getText().toString());
                billAmount = Double.parseDouble(etBillAmount.getText().toString());
                interest = Double.parseDouble(etInterest.getText().toString());
                previousBalance = Double.parseDouble(etPrevBalance.getText().toString());
                previousInterest = Double.parseDouble(etPrevInterest.getText().toString());
                fixedCharge = Double.parseDouble(etFixedCharge.getText().toString());
                energyCharge = Double.parseDouble(etEnergyCharge.getText().toString());
                tax = Double.parseDouble(etTax.getText().toString());

                //ADD ALL
                netAmount = billAmount + interest + previousBalance + previousInterest;
                String netS = String.format("%.2f", netAmount);
                etNetAmount.setText(netS);
                EnablePayment();
            }
            catch (Exception ex){
                Toast.makeText(BillingFormActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
//calculate tax
    private void calculateTax(){
        double fixedCharge = Double.parseDouble(etFixedCharge.getText().toString());
        double energyCharge = Double.parseDouble(etEnergyCharge.getText().toString());
        double totalTax = fixedCharge + energyCharge;
        totalTax = totalTax * .2;
        String tots = String.format("%.2f", totalTax);
        etTax.setText(tots);
    }
    //calculate bill amount
    private void calculateBillAmount(){
        //fixedCharge + energyCharge + tax + others +

        if(!TextUtils.isEmpty(etFixedCharge.getText())
        && !TextUtils.isEmpty(etEnergyCharge.getText())
        && !TextUtils.isEmpty(etTax.getText())
        && !TextUtils.isEmpty(etOthers.getText()))
        {
            double fixedCharge = Double.parseDouble(etFixedCharge.getText().toString());
            double energyCharge = Double.parseDouble(etEnergyCharge.getText().toString());
            double totalTax = fixedCharge + energyCharge;
            totalTax = totalTax * .2;
            double others = Double.parseDouble(etOthers.getText().toString());

            double totalBill = fixedCharge + energyCharge + totalTax + others;

            etBillAmount.setText(totalBill + "");
        }

    }


    //IF THE netAmount is not empty open payment/TblBilling
    private void EnablePayment(){
        if(!TextUtils.isEmpty(etNetAmount.getText())){
            etPaidAmount.setEnabled(true);
            btnCPaymentDateTime.setEnabled(true);
        }
        else{
            etPaidAmount.setEnabled(false);
            btnCPaymentDateTime.setEnabled(false);
        }
    }


    //show meter board name or icp
    private void showMeterBoards(String type) {
        try {
            List<String> list = databaseHelper.getMeterBoards(type);
            if (list == null) {
                Toast.makeText(BillingFormActivity.this, "Cannot proceed without at least 1 meter board.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            arrayAdapter = new ArrayAdapter<String>(BillingFormActivity.this,
                    android.R.layout.simple_list_item_1, list) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    /// Get the Item from ListView
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    // Set the text size 25 dip for ListView each item
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                    // Return the view
                    return view;
                }
            };
            spMeterBoards.setAdapter(arrayAdapter);
            spMeterBoards.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            icp = spMeterBoards.getSelectedItem().toString();
            callName = spMeterBoards.getSelectedItem().toString();
        }
        catch (Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }




    //for insert
    private int NewElectricBoardData(){
        int meterBoardID = databaseHelper.getMeterBoardID(callName, icp);
        ElectricityBoardModel electricityBoardModel = new ElectricityBoardModel();
        electricityBoardModel.setConsumptionUnit(Double.parseDouble(etConsumptionUnit.getText().toString()));
        electricityBoardModel.setPreviousReading(Double.parseDouble(etPrevRead.getText().toString()));
        electricityBoardModel.setPresentReading(Double.parseDouble(etPresRead.getText().toString()));
        electricityBoardModel.setDueDate(etDueDate.getText().toString());
        electricityBoardModel.setReadingDate(etReadDate.getText().toString());
        electricityBoardModel.setMeterBoardID(meterBoardID);
        int insertedID = databaseHelper.addElectricityData(electricityBoardModel);
        return insertedID;
    }

    private int NewInvoiceData(){
        int idOfElectricityBoard = NewElectricBoardData();
        if(idOfElectricityBoard == -1){
            Toast.makeText(BillingFormActivity.this, "Failed to insert Electricity Board Data", Toast.LENGTH_SHORT).show();
            return -1;
        }
        else{
            //new invoice
            InvoiceModel invoiceModel = new InvoiceModel();
            invoiceModel.setElectricityBoardID(idOfElectricityBoard);
            invoiceModel.setFixedCharge(Double.parseDouble(etFixedCharge.getText().toString()));
            invoiceModel.setEnergyCharge(Double.parseDouble(etEnergyCharge.getText().toString()));
            invoiceModel.setTax(Double.parseDouble(etTax.getText().toString()));
            invoiceModel.setBillAmount(Double.parseDouble(etBillAmount.getText().toString()));
            invoiceModel.setInterest(Double.parseDouble(etInterest.getText().toString()));
            invoiceModel.setPreviousBalance(Double.parseDouble(etPrevBalance.getText().toString()));
            invoiceModel.setInterestPreBalance(Double.parseDouble(etPrevInterest.getText().toString()));
            invoiceModel.setOthers(Double.parseDouble(etOthers.getText().toString()));

            Double value = Double.parseDouble(etNetAmount.getText().toString());
            String netAmount = String.format("%.2f", value);
            invoiceModel.setNetAmount(Double.parseDouble(netAmount));
            int insertedID = databaseHelper.addInvoiceData(invoiceModel);
            return insertedID;
        }
    }

    private boolean NewBillingData(){
        boolean added = false;
        int meterBoardID = databaseHelper.getMeterBoardID(callName, icp);
        int invoiceID = NewInvoiceData();
        if(invoiceID == -1){
            Toast.makeText(BillingFormActivity.this, "Failed to insert Invoice Data", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            //new billing
            BillingModel billingModel = new BillingModel();
            billingModel.setMeterBoardID(meterBoardID);
            billingModel.setInvoiceID(invoiceID);
            billingModel.setPaidAmount(Double.parseDouble(etPaidAmount.getText().toString()));
            billingModel.setPaymentDateTime(etPaymentDateTime.getText().toString());
            added = databaseHelper.addBillingData(billingModel);
        }

        return  added;
    }


    private void InsertData(){
        //execute insert here if everything is not empty
        //before that, check if the due date does not exist in database together with the meter board

        int meterBoardID = databaseHelper.getMeterBoardID(callName, icp);
        int dueDateCount = databaseHelper.countExistingDueDate(meterBoardID, etDueDate.getText().toString());
        if(dueDateCount > 0){
            Toast.makeText(BillingFormActivity.this, "Cannot proceed, due date exist.", Toast.LENGTH_SHORT).show();
        }
        else{
            //insert here
            AlertDialog.Builder builder = new AlertDialog.Builder(BillingFormActivity.this);
            builder.setTitle("System Message");
            builder.setMessage("Are you sure you want to add this data?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    NewBillingData();
                    Toast.makeText(BillingFormActivity.this, "You have added new Billing data", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(getBaseContext(), BillingHomeActivity.class);
                    startActivity(i);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }



    //for update
    //get all old data
    private void SetOldData(){
        //for electricity board data
        ElectricityBoardModel electricityBoardModel = databaseHelper.viewElectricityBoardData(billID);
        electricityBoardID = electricityBoardModel.getElectricityBoardID();
        etPrevRead.setText(electricityBoardModel.getPreviousReading() + "");
        etPresRead.setText(electricityBoardModel.getPresentReading() + "");
        etReadDate.setText(electricityBoardModel.getReadingDate() + "");
        etDueDate.setText(electricityBoardModel.getDueDate() + "");
        etConsumptionUnit.setText(electricityBoardModel.getConsumptionUnit() + "");
        EnableInvoice();
        //for invoice data
        InvoiceModel invoiceModel = databaseHelper.viewInvoiceData(billID);
        invoiceID = invoiceModel.getInvoiceID();
        etFixedCharge.setText(invoiceModel.getFixedCharge() + "");
        etEnergyCharge.setText(invoiceModel.getEnergyCharge() + "");
        etTax.setText(invoiceModel.getTax() + "");
        etOthers.setText(invoiceModel.getOthers() + "");
        etBillAmount.setText(invoiceModel.getBillAmount() + "");
        etInterest.setText(invoiceModel.getInterest() + "");
        etPrevBalance.setText(invoiceModel.getPreviousBalance() + "");
        etPrevInterest.setText(invoiceModel.getInterestPreBalance() + "");
        etNetAmount.setText(invoiceModel.getNetAmount() + "");
        EnablePayment();
        BillingModel billingModel = databaseHelper.viewBillingData(billID);
        try {
            etPaymentDateTime.setText(billingModel.getPaymentDateTime());
            etPaidAmount.setText(billingModel.getPaidAmount() + "");
        }
        catch (Exception ex){
            Toast.makeText(BillingFormActivity.this, ex.toString(), Toast.LENGTH_SHORT);
        }

        /*
        //for callName/meterboard
        int count = spMeterBoards.getCount();
        for(int i = 0; i < count; i++){
            if(name == oldCallName){
                spMeterBoards.setSelection(i);
            }
        }

         */

        spMeterBoards.setSelection(arrayAdapter.getPosition(oldCallName));
    }

    int countUpdated = 0;

    private boolean UpdatedElectricBoardData(){
        boolean updated = false;
        int meterBoardID = databaseHelper.getMeterBoardID(callName, icp);
        updated = databaseHelper.updateElectricityData(electricityBoardID+"",
                meterBoardID+"",
                etPrevRead.getText().toString(),
                etPresRead.getText().toString(),
                etConsumptionUnit.getText().toString(),
                etReadDate.getText().toString(),
                etDueDate.getText().toString());
        return updated;
    }
    private boolean UpdatedInvoiceData(){
        boolean updated = false;
        if(UpdatedElectricBoardData()){
            countUpdated+=1;
            updated = databaseHelper.updateInvoiceData(invoiceID+"",
                    electricityBoardID + "",
                    etFixedCharge.getText().toString(),
                    etEnergyCharge.getText().toString(),
                    etTax.getText().toString(),
                    etBillAmount.getText().toString(),
                    etInterest.getText().toString(),
                    etPrevBalance.getText().toString(),
                    etPrevInterest.getText().toString(),
                    etOthers.getText().toString(),
                    etNetAmount.getText().toString());
        }
        else{
            Toast.makeText(BillingFormActivity.this, "Failed update at electricity", Toast.LENGTH_SHORT).show();
            updated = false;
        }

        return updated;
    }
    private boolean UpdatedBillingData(){
        boolean updated = false;
        int meterBoardID = databaseHelper.getMeterBoardID(callName, icp);
        if(UpdatedInvoiceData()){
            countUpdated+=1;
            updated = databaseHelper.updateBillingData(billID + "",
                    meterBoardID+"",
                    invoiceID +"",
                    etPaymentDateTime.getText().toString(),
                    etPaidAmount.getText().toString());
        }
        else{
            Toast.makeText(BillingFormActivity.this, "Failed update at invoice", Toast.LENGTH_SHORT).show();
            updated =  false;
        }

        return updated;
    }

    private void UpdateData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BillingFormActivity.this);
        builder.setTitle("System Message");
        builder.setMessage("Are you sure you want to Updated these data?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(UpdatedBillingData()){
                    //all three updated
                    countUpdated+=1;
                    Toast.makeText(BillingFormActivity.this, "Bill updated!",
                            Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(getBaseContext(), BillingHomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    dialog.dismiss();
                }
                else{
                    Toast.makeText(BillingFormActivity.this, "Failed at Billing",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

}