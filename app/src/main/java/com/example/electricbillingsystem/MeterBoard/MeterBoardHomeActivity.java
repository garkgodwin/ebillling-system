package com.example.electricbillingsystem.MeterBoard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electricbillingsystem.Billing.BillingCustomAdapter;
import com.example.electricbillingsystem.Billing.BillingHomeActivity;
import com.example.electricbillingsystem.Customer.CustomerActivity;
import com.example.electricbillingsystem.HomeActivity;
import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.DatabaseHelper;
import com.example.electricbillingsystem.database.MeterBoardModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeterBoardHomeActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;

    Button btnCreate;
    EditText etSearch;
    //FOR RECYCLERVIEW
    RecyclerView rvMeter;
    ArrayList<String> meterIDs, callNames, icps, totalKwhs, statuss;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(getBaseContext(), HomeActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_board_home);

        databaseHelper = new DatabaseHelper(MeterBoardHomeActivity.this);
        setControls();
        setVariables();

        //UPDATE METER CONSUMPTION TOTAL
        databaseHelper.setSumOfConsumptionUnit();

        setAndGetData();


        //button click to go to "MeterBoardFormActivity" as new data creation
        btnCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getBaseContext(), MeterBoardFormActivity.class);
                i.putExtra("action", 1);
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

    //initialize views
    private void setControls(){
        btnCreate = findViewById(R.id.btnCreateMainMeter);
        rvMeter = findViewById(R.id.rvMeter);
        etSearch = findViewById(R.id.etSearchMeterName);
    }

    //initialize variables for adapter
    private void setVariables(){
        meterIDs = new ArrayList<>();
        callNames = new ArrayList<>();
        icps = new ArrayList<>();
        totalKwhs = new ArrayList<>();
        statuss = new ArrayList<>();
    }

    //call this method to store data in array : used to display meter board data
    private void storeDataInArrays(String search){
        Cursor cursor = databaseHelper.getMeterBoardsCursor(search);
        meterIDs.clear();
        callNames.clear();
        icps.clear();
        totalKwhs.clear();
        statuss.clear();
        if(cursor.moveToFirst()){
            do{
                meterIDs.add(cursor.getString(cursor.getColumnIndex("meterBoardID")));
                callNames.add(cursor.getString(cursor.getColumnIndex("callName")));
                icps.add(cursor.getString(cursor.getColumnIndex("installationControlPoint")));
                totalKwhs.add(cursor.getString(cursor.getColumnIndex("totalKWH")));
                statuss.add(cursor.getString(cursor.getColumnIndex("status")));
            }
            while (cursor.moveToNext());
        }
        else{
            Toast.makeText(MeterBoardHomeActivity.this, "Empty Data", Toast.LENGTH_SHORT).show();
        }

    }
    //call this method to get the stored data in arrays and set in the adapter
    private void setViewAdapter(){
        MeterCustomAdapter meterCustomAdapter= new MeterCustomAdapter(MeterBoardHomeActivity.this,
                meterIDs, callNames, icps, totalKwhs, statuss);
        rvMeter.setAdapter(meterCustomAdapter);
        rvMeter.setLayoutManager(new LinearLayoutManager(MeterBoardHomeActivity.this));
    }
}