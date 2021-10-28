package com.example.electricbillingsystem.Billing;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electricbillingsystem.HomeActivity;
import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.DatabaseHelper;

import java.util.ArrayList;

public class BillingHomeActivity extends AppCompatActivity {

    Button btnAdd;// btnUpdate, btnDelete;
    EditText etSearch;

    RecyclerView recyclerView;

    DatabaseHelper databaseHelper;
    String searchCallName = "";


    public int billID = 0;
    String callName = "";


    ArrayList<String> billIDs, callNames, netAmounts, paymentDates, energyUnits;
    BillingCustomAdapter billingCustomAdapter;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_home);
        databaseHelper = new DatabaseHelper(BillingHomeActivity.this);

        setControls();
        resetVariables();



        setAndGetData();

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(getBaseContext(), BillingFormActivity.class);
                    i.putExtra("action", 1);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setAndGetData();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setAndGetData(){
        storeDataInArrays(etSearch.getText().toString());
        setViewAdapter();
    }
    private void setControls(){
        btnAdd = findViewById(R.id.btnNewBillPayment);
        //btnUpdate = findViewById(R.id.btnUpdateBillPayment);
        //btnDelete = findViewById(R.id.btnDeleteBillPayment);
        etSearch = findViewById(R.id.etSearchMainBilling);
        recyclerView = findViewById(R.id.rvBilling);
    }
    private void resetVariables(){
        billID = 0;
        callName = "";

        billIDs = new ArrayList<>();
        callNames = new ArrayList<>();
        netAmounts = new ArrayList<>();
        paymentDates = new ArrayList<>();
        energyUnits = new ArrayList<>();
    }

    public void storeDataInArrays(String search){
        try{
            Cursor cursor = databaseHelper.viewAllMain(search);
            billIDs.clear();
            callNames.clear();
            netAmounts.clear();
            paymentDates.clear();
            energyUnits.clear();
            if(cursor.moveToFirst()){
                do{
                    billIDs.add(cursor.getString(0));
                    callNames.add(cursor.getString(1));
                    netAmounts.add(cursor.getString(2));
                    paymentDates.add(cursor.getString(3));
                    energyUnits.add(cursor.getString(4));
                }
                while (cursor.moveToNext());
            }
            else{
                Toast.makeText(BillingHomeActivity.this, "Your billing data is empty.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(BillingHomeActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setViewAdapter(){
        billingCustomAdapter = new BillingCustomAdapter(BillingHomeActivity.this,
                billIDs, callNames, netAmounts, paymentDates, energyUnits);
        recyclerView.setAdapter(billingCustomAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(BillingHomeActivity.this));
    }


    private void DeleteData(int billID){
        if(billID != 0){
            if(!databaseHelper.deleteBilling(billID)){
                if(!databaseHelper.deleteInvoice(billID)){
                    if(!databaseHelper.deleteEBoard(billID)){

                        storeDataInArrays(etSearch.getText().toString());
                        resetVariables();

                        Toast.makeText(BillingHomeActivity.this, "Bill deleted!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}