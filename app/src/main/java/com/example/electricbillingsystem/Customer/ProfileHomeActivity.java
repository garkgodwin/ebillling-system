package com.example.electricbillingsystem.Customer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electricbillingsystem.HomeActivity;
import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.Settings.SettingsHomeActivity;
import com.example.electricbillingsystem.database.CustomerModel;
import com.example.electricbillingsystem.database.DatabaseHelper;

import at.markushi.ui.CircleButton;

public class ProfileHomeActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;

    EditText etFulllName, etAddress, etEmail;
    CircleButton btnSettings;


    //for pop up
    CircleButton btnPopPin;
    EditText etPopPin;


    //FOR NONE PROFILE INFO
    TextView txtMeterSummary, txtMaxEnergy, txtUnpaidMeters, txtTotalBill;

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        Intent i = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_home);

        etFulllName = findViewById(R.id.etFullName);
        etAddress = findViewById(R.id.etA);
        etEmail = findViewById(R.id.etE);
        btnSettings = findViewById(R.id.btnSettings);


        //for none profile
        txtMeterSummary = findViewById(R.id.txtMeterSummary);
        txtMaxEnergy = findViewById(R.id.txtMaxEnergy);
        txtUnpaidMeters = findViewById(R.id.txtUnpaidMeters);
        txtTotalBill = findViewById(R.id.txtTotalBill);


        databaseHelper = new DatabaseHelper(ProfileHomeActivity.this);
        showInfo();
        DisableTexts();




        //Opens the settings if selected : Yes
        btnSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setAlert();
            }
        });



    }

    private void setAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileHomeActivity.this);
        builder.setTitle("System Message");
        builder.setMessage("Enter your pin first to enter settings?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                openPasswordField();
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

    private void openPasswordField(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_up_pin, null);
        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(false);
        etPopPin = view.findViewById(R.id.etPopPin);
        btnPopPin = view.findViewById(R.id.btnPopPin);
        showKeyboard(etPopPin);
        btnPopPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check pin from database
                if(!TextUtils.isEmpty(etPopPin.getText())) {
                    if(etPopPin.getText().toString().length() < 4){
                        Toast.makeText(ProfileHomeActivity.this, "Please complete the 4 digit code...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        boolean isPinRight = databaseHelper.isPinRight(etPopPin.getText().toString());
                        if (isPinRight) {
                            //proceed to next activity
                            Intent i = new Intent(getBaseContext(), SettingsHomeActivity.class);
                            startActivity(i);
                            popupWindow.dismiss();

                        } else {
                            Toast.makeText(ProfileHomeActivity.this, "You entered a wrong pin.\n" +
                                    "Please try again to access your settings.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(ProfileHomeActivity.this, "Please make sure you enter something...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void showKeyboard(EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) editText.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
    private void DisableTexts(){
        etFulllName.setFocusable(false);
        etFulllName.setEnabled(false);
        etEmail.setFocusable(false);
        etEmail.setEnabled(false);
        etAddress.setFocusable(false);
        etAddress.setEnabled(false);
    }


    //for showing profile info
    //this methods is for non basic info
    private String getMeterSummary(){
        String result = "";
        Cursor cursor = databaseHelper.getMeterSummary();
        if(cursor.moveToNext()){
            int meterCount = cursor.getInt(cursor.getColumnIndex("totalCount"));
            int meterAwake = cursor.getInt(cursor.getColumnIndex("awake"));
            int meterAsleep = cursor.getInt(cursor.getColumnIndex("asleep"));
            double sumOfTotals = cursor.getDouble(cursor.getColumnIndex("sumOfTotals"));
            if(meterCount != 0){
                result += "Number of meters: " + meterCount + "\n";
                result += "Awake: " + meterAwake + "\t\t||\t\t" + "Asleep: " + meterAsleep+"\n";
                result += "Total energy consumption(All meter): \n" + sumOfTotals+" Kwh\n";
            }
            else{
                result = "You have no meter board created.";
            }
        }
        else{
            result = "You have no meter board created.";
        }
        return result;
    }
    private String getMaxEnergy(){
        String result = "";
        Double highestEnergy = 0.0;
        int countHighestEnergy = 0;
        Cursor cursor = databaseHelper.getMaxEnergy();
        if(cursor.moveToNext()){
            String callName = cursor.getString(cursor.getColumnIndex("callName"));
            Double sumEnergy = cursor.getDouble(cursor.getColumnIndex("sumEnergy"));
            if (highestEnergy == 0){
                highestEnergy = sumEnergy;
            }
            else{
                if(highestEnergy < sumEnergy){
                    highestEnergy = sumEnergy;
                    countHighestEnergy = 0;
                }
                else if(highestEnergy == sumEnergy){
                    //there are more than 1 same highest energy
                    countHighestEnergy ++;
                }
            }
            result += "Peak Energy Consumption: \n" + highestEnergy + " KwH\n";
            if(countHighestEnergy > 1){
                result+= "There are " + countHighestEnergy + " Meter with Energy Consumption as the Highest.";
            }
        }
        else{
            result = "You have no data for energy consumption.";
        }
        return result;
    }
    private String getUnpaidBills(){
        String result = "Meters with unpaid bills: \n";
        Cursor cursor = databaseHelper.getUnpaidBills();
        if(cursor.moveToFirst()){
            do {
                String meter = cursor.getString(cursor.getColumnIndex("callName"));
                int count = cursor.getInt(cursor.getColumnIndex("unpaidCount"));
                result += meter + "\t\t||\t\t" + "Unpaid bill count: " + count + "\n";
            }
            while (cursor.moveToNext());
        }
        else{
            result = "You have no unpaid bills";
        }
        return result;
    }
    private String getTotalBill(){
        String result = "";
        Cursor cursor = databaseHelper.getTotalBills();
        if(cursor.moveToNext()){
            double unpaid = cursor.getDouble(cursor.getColumnIndex("unpaidTotal"));
            double paid = cursor.getDouble(cursor.getColumnIndex("paidTotal"));
            result += "Total Paid Amount: ₱"+paid +"\nTotal Unpaid Amount: ₱" + unpaid;
        }
        return result;
    }

    //this info is to set the profile values included the non basic info
    private void showInfo(){
        CustomerModel customerModel = databaseHelper.selectCustomer();
        etFulllName.setText(customerModel.getTitle() + " " +customerModel.getFirstName()+" " +customerModel.getMiddleName() + " " + customerModel.getLastName());
        etAddress.setText(customerModel.getAddress());
        etEmail.setText(customerModel.getEmail());

        //non profile
        txtMeterSummary.setText(getMeterSummary());
        txtMaxEnergy.setText(getMaxEnergy());
        txtUnpaidMeters.setText(getUnpaidBills());
        txtTotalBill.setText(getTotalBill());
    }
}