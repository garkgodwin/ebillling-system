package com.example.electricbillingsystem.MeterBoard;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.DatabaseHelper;
import com.example.electricbillingsystem.database.MeterBoardModel;

public class MeterBoardFormActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    EditText  etTotalKwh, etIcp, etCallName;
    Spinner spStatus;
    Button btnCreate, btnUpdate, btnClear;

    //Use these data to send it to form for update
    int meterBoardID = 0;
    int action = 0;


    ArrayAdapter arrayAdapter;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        resetData();
        Intent i = new Intent(getBaseContext(), MeterBoardHomeActivity.class);
        startActivity(i);
    }

    private void resetData(){
        meterBoardID = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_board_form);

        databaseHelper = new DatabaseHelper(MeterBoardFormActivity.this);

        SetControls();
        DisableButtons();
        SetSpinner();

        setExtraValues(savedInstanceState);
        setFormValues();

        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MeterBoardModel meterBoardModel = new MeterBoardModel();
                meterBoardModel.setStatus(spStatus.getSelectedItem().toString());
                meterBoardModel.setTotalKWH(Double.parseDouble(etTotalKwh.getText().toString()));
                meterBoardModel.setInstallationControlPoint(etIcp.getText().toString());
                meterBoardModel.setCallName(etCallName.getText().toString());
                boolean inserted = databaseHelper.registerMeterBoard(meterBoardModel);
                if (inserted) {
                    Toast.makeText(MeterBoardFormActivity.this, "Inserted", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getBaseContext(), MeterBoardHomeActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(MeterBoardFormActivity.this, "Failed to insert", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String id = meterBoardID + "";
                String totalKwh = etTotalKwh.getText().toString();
                String status = spStatus.getSelectedItem().toString();
                String icp = etIcp.getText().toString();
                String callName = etCallName.getText().toString();
                boolean updated = databaseHelper.updateMeterBoard(id, icp, callName, totalKwh, status);
                if(updated){
                    Toast.makeText(MeterBoardFormActivity.this, "Meter board Updated!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getBaseContext(), MeterBoardHomeActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(MeterBoardFormActivity.this, "Meter board Failed to Updated!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //initialize views
    private void SetControls(){
        spStatus = findViewById(R.id.spStatus);
        etTotalKwh = findViewById(R.id.etTotalKwh);
        etCallName = findViewById(R.id.etCallName);
        etIcp = findViewById(R.id.etIcp);
        btnCreate = findViewById(R.id.btnCreateMeterData);
        btnUpdate = findViewById(R.id.btnUpdateMeterData);
        etTotalKwh.setEnabled(false);
        etTotalKwh.setText("0");
    }

    //set spinner value and design
    private void SetSpinner(){
        String spin[] = new String[2];
        spin[0] = "Awake";
        spin[1] = "Sleeping";
        arrayAdapter = new ArrayAdapter(this, R.layout.spinner_custom, spin);
        spStatus.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        spStatus.setAdapter(arrayAdapter);
        spStatus.setSelection(0);
    }

    //set buttons disabled depending on the action from MeterBoardHome activity
    private void DisableButtons(){
        btnCreate.setEnabled(false);
        btnUpdate.setEnabled(false);
    }

    //set extras
    private void setExtraValues(Bundle savedInstanceState){
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                meterBoardID = 0;
                action = 0;
            }
            else{
                meterBoardID = extras.getInt("id");
                action = extras.getInt("action");
            }
        }
        else{
            meterBoardID = (int) savedInstanceState.getSerializable("id");
            action = (int) savedInstanceState.getSerializable("action");
        }
    }

    //set form values
    private void setFormValues() {
        if (action == 1) {
            btnCreate.setEnabled(true);
        } else if (action == 2) {
            btnUpdate.setEnabled(true);
            getMeterData();
        }



    }


    private void getMeterData(){
        Cursor cursor = databaseHelper.getMeterBoardByID(meterBoardID);
        if(cursor.moveToFirst()){
            do{
                etTotalKwh.setText(cursor.getString(cursor.getColumnIndex("totalKWH")));
                etCallName.setText(cursor.getString(cursor.getColumnIndex("callName")));
                etIcp.setText(cursor.getString(cursor.getColumnIndex("installationControlPoint")));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                spStatus.setSelection(arrayAdapter.getPosition(status));
            }
            while (cursor.moveToNext());
        }
    }

}