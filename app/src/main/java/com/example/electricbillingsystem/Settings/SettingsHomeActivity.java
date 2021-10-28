package com.example.electricbillingsystem.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.electricbillingsystem.Billing.BillingFormActivity;
import com.example.electricbillingsystem.Customer.CustomerActivity;
import com.example.electricbillingsystem.Customer.ProfileHomeActivity;
import com.example.electricbillingsystem.HomeActivity;
import com.example.electricbillingsystem.MainActivity;
import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.DatabaseHelper;

public class SettingsHomeActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Button btnUpdate, btnReset;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getBaseContext(), ProfileHomeActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_home_actitivity);
        databaseHelper = new DatabaseHelper(SettingsHomeActivity.this);
        btnUpdate = findViewById(R.id.btnUpdateProfile);
        btnReset = findViewById(R.id.btnResetData);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show intent
                Intent i = new Intent(getBaseContext(), CustomerActivity.class);
                i.putExtra("action", 2);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reset data
                //set dialog box before delete

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsHomeActivity.this);
                builder.setTitle("System Message");
                builder.setMessage("THIS ACTION WILL DELETE ALL YOUR DATA INPUTTED IN THIS APP.\n" +
                        "DO YOU WISH TO PROCEED?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteAllData();
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(i);
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
    }
}