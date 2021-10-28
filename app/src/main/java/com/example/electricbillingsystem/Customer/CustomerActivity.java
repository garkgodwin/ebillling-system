package com.example.electricbillingsystem.Customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.Settings.SettingsHomeActivity;
import com.example.electricbillingsystem.database.CustomerModel;
import com.example.electricbillingsystem.database.DatabaseHelper;

public class CustomerActivity extends AppCompatActivity {

    EditText etFirstName, etMiddleName, etLastName, etTitle, etAddress, etEmail, etPin;
    Button btnClear, btnSubmit;
    TextView txtTitle;

    DatabaseHelper databaseHelper;

    int action = 0;
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        int count = databaseHelper.countCustomer();
        if(count == 0){
            Toast.makeText(CustomerActivity.this, "Application closed.", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent i = new Intent(getBaseContext(), SettingsHomeActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.electricbillingsystem.R.layout.activity_customer);

        //set views from xml
        setControls();
        clearAll();
        databaseHelper = new DatabaseHelper(this);


        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                //set defaults which is empty
                action = 0;
            }
            else{
                //set extras to edittexts fields
                action = extras.getInt("action");
            }
        }
        else{
            action = (int) savedInstanceState.getSerializable("action");
        }

        if(action == 1){
            //create
            btnSubmit.setText("SUBMIT");
        }
        else if(action == 2){
            showData();
            btnSubmit.setText("UPDATE");
            txtTitle.setText("Update Customer");
            //update
            //set data to texts

        }


        btnClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerActivity.this);
                builder.setTitle("Clear Input");
                builder.setMessage("Are you sure you want to clear all your input?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CustomerActivity.this, "INPUT CLEARED", Toast.LENGTH_SHORT).show();
                        clearAll();
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

        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //Set the strings of the fields
                String firstName = etFirstName.getText().toString();
                String middleName = etMiddleName.getText().toString();
                String lastName = etLastName.getText().toString();
                String title = etTitle.getText().toString();
                String address = etAddress.getText().toString();
                String email = etEmail.getText().toString();
                int pin = Integer.parseInt(etPin.getText().toString());
                //check first before dialog box

                //This condition checks if both the required fields are not empty
                if(!TextUtils.isEmpty(etFirstName.getText()) && !TextUtils.isEmpty(etLastName.getText())){

                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerActivity.this);
                    builder.setTitle("Confirm Data:");
                    builder.setMessage("First Name: " + firstName +
                            "\nMiddle Name: " + middleName +
                            "\nLast Name: " + lastName +
                            "\nTitle: " + title +
                            "\nAddress: " + address +
                            "\nEmail: " + email +
                            "\nPin: " + pin);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            CustomerModel customerModel = new CustomerModel(-1, firstName, middleName, lastName, title, address, email, pin);
                            if(action == 1) {
                                boolean inserted = databaseHelper.registerCustomer(customerModel);
                                if (inserted == false) {
                                    Toast.makeText(CustomerActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                                } else {
                                    clearAll();
                                    Intent i = new Intent(getBaseContext(), ProfileHomeActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }
                            }
                            else{
                                databaseHelper.updateCustomer("1", firstName, middleName, lastName,title, address, email, etPin.getText().toString());
                                clearAll();
                                Toast.makeText(CustomerActivity.this, "UPDATED!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getBaseContext(), ProfileHomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                            //Submit data to database
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                    Toast.makeText(CustomerActivity.this, "Please do not leave the required fields empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showInfo(){
        CustomerModel customerModel = databaseHelper.selectCustomer();
        etFirstName.setText(customerModel.getFirstName());
        etMiddleName.setText(customerModel.getMiddleName());
        etLastName.setText(customerModel.getLastName());
        etTitle.setText(customerModel.getTitle());
        etAddress.setText(customerModel.getAddress());
        etEmail.setText(customerModel.getEmail());
        etPin.setText(customerModel.getPin());
    }


    private void showData(){
        Cursor cursor = databaseHelper.selectCustomerCursor();
        if(cursor.moveToFirst()){
            do{
                etFirstName.setText(cursor.getString(cursor.getColumnIndex("firstName")));
                etMiddleName.setText(cursor.getString(cursor.getColumnIndex("middleName")));
                etLastName.setText(cursor.getString(cursor.getColumnIndex("lastName")));
                etTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
                etAddress.setText(cursor.getString(cursor.getColumnIndex("address")));
                etEmail.setText(cursor.getString(cursor.getColumnIndex("email")));
                String pin = cursor.getString(cursor.getColumnIndex("pin"));
                if(pin.length() == 3){
                    pin = "0" + pin;
                }
                etPin.setText(pin);
            }
            while (cursor.moveToNext());
        }
    }

    private void setControls(){
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etTitle = findViewById(R.id.etTitle);
        etAddress = findViewById(R.id.etAddress);
        etEmail = findViewById(R.id.etEmail);
        etPin = findViewById(R.id.etPin);
        btnClear = findViewById(R.id.btnClear);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtTitle = findViewById(R.id.txtTitle);
    }

    private void clearAll(){
        etFirstName.setText("");
        etMiddleName.setText("");
        etLastName.setText("");
        etTitle.setText("");
        etAddress.setText("");
        etEmail.setText("");
        etPin.setText("");
        action = 1;
    }




}