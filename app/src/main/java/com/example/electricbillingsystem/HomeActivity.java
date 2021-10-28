package com.example.electricbillingsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.electricbillingsystem.Billing.BillingHomeActivity;
import com.example.electricbillingsystem.Customer.CustomerActivity;
import com.example.electricbillingsystem.Customer.ProfileHomeActivity;
import com.example.electricbillingsystem.Electricity.ElectricityHomeActitvity;
import com.example.electricbillingsystem.MeterBoard.MeterBoardHomeActivity;
import com.example.electricbillingsystem.Notification.NotificationHomeActivity;
import com.example.electricbillingsystem.QuickView.QuickViewHomeActivity;
import com.example.electricbillingsystem.database.CustomerModel;
import com.example.electricbillingsystem.database.DatabaseHelper;
import com.example.electricbillingsystem.database.MeterBoardModel;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class HomeActivity extends AppCompatActivity {


    DatabaseHelper databaseHelper;
    CustomerModel customerModel;
    MeterBoardModel meterBoardModel;


    CardView cardProfile, cardMeter, cardElectricity, cardBilling, cardQuickView, cardNotification;
    TextView txtNotifDesc;

    GifImageView bell;


    TextView txtTest;
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("System Message");
        builder.setMessage("Would you like to close the app?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(HomeActivity.this, "Application closed.", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        databaseHelper = new DatabaseHelper(this);
        //For new user
        CheckDetails();
        cardProfile = findViewById(R.id.cardProfile);
        cardMeter = findViewById(R.id.cardMeter);
        cardElectricity = findViewById(R.id.card_electricity);
        cardBilling = findViewById(R.id.cardBilling);
        cardQuickView = findViewById(R.id.card_quickView);
        cardNotification = findViewById(R.id.card_notification);

        bell = findViewById(R.id.imageBell);


        txtNotifDesc = findViewById(R.id.txtNotifDesc);
        Cursor cursor = databaseHelper.getNotif("");
        if(cursor.getCount() != 0) {
            int count = Integer.parseInt(String.valueOf(cursor.getCount()));
            txtNotifDesc.setText("Number of notifications: " + count);
        }

        setBell(cursor.getCount());

        cardProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getBaseContext(), ProfileHomeActivity.class);
                startActivity(i);
            }
        });

        cardMeter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MeterBoardHomeActivity.class);
                startActivity(i);
            }
        });

        cardElectricity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int count = databaseHelper.countElectricityData();
                    if (count <= 0) {
                        Toast.makeText(HomeActivity.this, "EMPTY DATA", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(getBaseContext(), ElectricityHomeActitvity.class);
                        startActivity(i);
                    }
            }
        });

        cardBilling.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getBaseContext(), BillingHomeActivity.class);
                startActivity(i);
            }
        });

        cardQuickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = databaseHelper.countElectricityData();
                if(count <= 0){
                    Toast.makeText(HomeActivity.this, "EMPTY DATA", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(getBaseContext(), QuickViewHomeActivity.class);
                    startActivity(i);
                }
            }
        });


        cardNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), NotificationHomeActivity.class);
                startActivity(i);
            }
        });


        //TODO:TEST
        txtTest = findViewById(R.id.txtHome);
        txtTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Test", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setBell(int count){
        //count from db
        if(count == 0) {
            ((GifDrawable) bell.getDrawable()).stop();
        }
        else{
            ((GifDrawable) bell.getDrawable()).start();
        }

    }

    //createNew() method executes inside CheckDetails() method
    //this method provides an algorith to check the database's customer table (count of customer)
    private boolean createNew(){
        boolean createNew = false;
        int count = databaseHelper.countCustomer();

        if(count == 0){
            createNew = true;
        }
        else if(count == 1){
            createNew = false;
        }
        else if(count > 1){
            Toast.makeText(this, "Count is greater than 1(BUG<THIS IS A BUG)\n" +
                    "INSIDE createNew() method", Toast.LENGTH_SHORT).show();
            createNew = false;
        }

        return createNew;
    }

    /*
    CheckDetails() method will initiate certain instances.
    Once the createNew() method returns true, which means the customer data doesn't exist,
    it will run the CustomerActivity in which it is inside the registration part of the customer act.
     */
    private void CheckDetails(){
        if(createNew() == true){
            //create new customerData
            //create new meter board
            Toast.makeText(this, "You have installed the new app,\n" +
                    "proceeding to create new data in few seconds.", Toast.LENGTH_SHORT).show();
            Thread background = new Thread() {
                public void run() {
                    try {
                        // Thread will sleep for 5 seconds
                        sleep(3*1000);

                        // After 3 seconds redirect to another intent
                        Intent i=new Intent(getBaseContext(), CustomerActivity.class);
                        i.putExtra("action", 1);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                        //Remove activity
                        finish();
                    } catch (Exception e) {
                    }
                }
            };
            // start thread
            background.start();
        }
    }


}