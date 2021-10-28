package com.example.electricbillingsystem.Notification;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.electricbillingsystem.R;

public class EditPopUpActivity extends AppCompatActivity {

    EditText etPayment, etDate;
    Button btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pop_up);
    }
}