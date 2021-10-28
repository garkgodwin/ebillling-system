package com.example.electricbillingsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electricbillingsystem.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);



        // MediaPlayer ring= MediaPlayer.create(MainActivity.this,R.raw.shock);
        // ring.start();
/****** Create Thread that will sleep for 5 seconds****/
        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(1*1000);

                    // After 5 seconds redirect to another intent
                    Intent i= new Intent(getBaseContext(),HomeActivity.class);
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

