package com.example.electricbillingsystem.Notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.electricbillingsystem.HomeActivity;
import com.example.electricbillingsystem.MainActivity;
import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.DatabaseHelper;
import com.example.electricbillingsystem.database.NotificationModel;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;

public class NotificationHomeActivity extends AppCompatActivity {

    RecyclerView rvNotif;
    EditText etSearch;

    //for hint
    CircleButton btnNotifHint;
    PopupWindow popupWindow;



    ArrayList<String> electricityIDs, netAmounts, paidAmounts, dueDates, callNames, isDue;
    DatabaseHelper databaseHelper;
    public CustomAdapter customAdapter;


    String callName = "";


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_home);
        databaseHelper = new DatabaseHelper(NotificationHomeActivity.this);
        setViews();
        setVariables();
        searchData(etSearch.getText().toString());
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchData(etSearch.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnNotifHint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        openHint();
                        return true;

                    case MotionEvent.ACTION_UP:
                        popupWindow.dismiss();
                        return false;
                }
                return false;
            }
        });
    }
    private void openHint(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hint_notif, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

    private void setViews(){
        rvNotif = findViewById(R.id.rvNotifs);
        etSearch = findViewById(R.id.etSearchName);
        btnNotifHint = findViewById(R.id.btnNotifHint);
    }
    private void setVariables(){
        electricityIDs = new ArrayList<>();
        netAmounts = new ArrayList<>();
        paidAmounts = new ArrayList<>();
        dueDates = new ArrayList<>();
        callNames = new ArrayList<>();
        isDue = new ArrayList<>();
    }
    private void storeDataInArrays(String callName){
        try {
            Cursor cursor = databaseHelper.getNotif(callName);
            electricityIDs.clear();
            dueDates.clear();
            paidAmounts.clear();
            netAmounts.clear();
            callNames.clear();
            if (cursor.moveToFirst()) {
                do{
                    electricityIDs.add(cursor.getString(0));

                    String date = cursor.getString(1);
                    dueDates.add(date);
                    netAmounts.add(cursor.getString(2));
                    paidAmounts.add(cursor.getString(3));
                    callNames.add(cursor.getString(4));
                }
                while (cursor.moveToNext());
            }
            else{

                Toast.makeText(NotificationHomeActivity.this, "You do not have any pending payment", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(NotificationHomeActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void searchData(String callName){
        storeDataInArrays(callName);
        customAdapter = new CustomAdapter(NotificationHomeActivity.this, electricityIDs, netAmounts, paidAmounts, dueDates, callNames);
        rvNotif.setAdapter(customAdapter);
        rvNotif.setLayoutManager(new LinearLayoutManager(NotificationHomeActivity.this));
    }





}