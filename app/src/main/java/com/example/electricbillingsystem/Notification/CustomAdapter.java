package com.example.electricbillingsystem.Notification;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements DatePickerDialog.OnDateSetListener{

    private int lastPosition = -1;
    int row_index = -1;
    Context context;
    ArrayList electricityIDs, netAmounts, paidAmounts, dueDates, callNames;
    DatabaseHelper databaseHelper;


    EditText  etPaid, etDate;
    TextView txtPopID;
    Button btnUpdate, btnChange;

    CustomAdapter(Context context,
                  ArrayList electricityIDs,
                  ArrayList netAmounts,
                  ArrayList paidAmounts,
                  ArrayList dueDates,
                  ArrayList callNames){
        this.context = context;
        this.electricityIDs = electricityIDs;
        this.netAmounts = netAmounts;
        this.paidAmounts = paidAmounts;
        this.dueDates = dueDates;
        this.callNames = callNames;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notif_row, parent, false);
        databaseHelper = new DatabaseHelper(context);
        return new MyViewHolder(view);
    }

    ColorDrawable cdWhite = new ColorDrawable(Color.WHITE);
    ColorDrawable cdBlack = new ColorDrawable(0x000);
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        showData(holder, position);

        holder.cardNotif.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Double netAmount = Double.parseDouble(String.valueOf(netAmounts.get(position)));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("System Message");
                builder.setMessage("Would you like to update this billing info?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //UPDATE BILLING
                        //Show pop_up_edit_payment
                        //billID
                            int billID = 0;
                            billID = Integer.parseInt(String.valueOf(electricityIDs.get(position)));
                            if (billID != 0) {
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.activity_edit_pop_up, null);
                                PopupWindow popupWindow = new PopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                                popupWindow.setOutsideTouchable(false);

                                //fields
                                etPaid = view.findViewById(R.id.etPaymentAmount);
                                etDate = view.findViewById(R.id.etPaymentDate);
                                etDate.setEnabled(false);
                                txtPopID = view.findViewById(R.id.txtPopID);
                                btnUpdate = view.findViewById(R.id.btnUpdateNotif);
                                btnChange = view.findViewById(R.id.btnChangeNotifDate);
                                txtPopID.setText("Bill ID: " + billID);

                                int finalBillID = billID;
                                btnUpdate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Double paymentAmount = (double) 0;
                                        if (!TextUtils.isEmpty(etPaid.getText())
                                                && !TextUtils.isEmpty(etDate.getText())) {
                                            String id = finalBillID + "";
                                            String payment = etPaid.getText().toString();
                                            paymentAmount = Double.valueOf(payment);
                                            if(paymentAmount < netAmount){
                                                Toast.makeText(context, "Your payment must be greater than net amount.", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                String date = etDate.getText().toString();
                                                databaseHelper.updateBillPayment(id, payment, date);
                                                Toast.makeText(context, "Payment Updated", Toast.LENGTH_SHORT).show();
                                                popupWindow.dismiss();
                                                Intent i = new Intent(context, NotificationHomeActivity.class);
                                                context.startActivity(i);
                                            }
                                        }
                                        else {
                                            Toast.makeText(context, "Please make sure the fields are complete.", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });

                                btnChange.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDatePickerDialog();
                                    }
                                });
                            }
                            dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        holder.cardNotif.setBackgroundColor(Color.parseColor("#333333"));
                        holder.cardNotif.setCardBackgroundColor(Color.parseColor("#333333"));
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                row_index = position;
                notifyDataSetChanged();
            }

        });


        if(row_index == position){
            holder.cardNotif.setCardBackgroundColor(Color.BLACK);
            holder.cardNotif.setBackgroundColor(Color.BLACK);
        }
        else
        {
            holder.cardNotif.setCardBackgroundColor(Color.parseColor("#333333"));
            holder.cardNotif.setBackgroundColor(Color.parseColor("#333333"));
        }


    }

    private void showData(@NonNull MyViewHolder holder, int position){
        holder.txtID.setText("Bill ID\n" + String.valueOf(electricityIDs.get(position)));
        holder.txtCallName.setText(String.valueOf("CallName: " + callNames.get(position)));
        holder.txtNetAmount.setText("₱"+String.valueOf(netAmounts.get(position)));
        holder.txtPaidAmount.setText("Unpaid");
        holder.txtDueDate.setText(String.valueOf(dueDates.get(position)));



        String data = calculateDue(String.valueOf(dueDates.get(position)));
        if(data == "overDue"){
            holder.txtDueDate.setBackgroundColor(Color.RED);
        }
        else if(data == "almostDue"){
            holder.txtDueDate.setBackgroundColor(Color.YELLOW);
        }
        else{
            holder.txtDueDate.setBackgroundColor(Color.GREEN);
        }
    }

    private int getDifference(String dueDate){
        int diff = 0;
        Date firstDate;
        Date secondDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            firstDate = sdf.parse(String.valueOf(LocalDateTime.now()));
            secondDate = sdf.parse(dueDate);


            long diffInMillies =  Math.abs(secondDate.getTime()) - firstDate.getTime();
            diff = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }
        catch (Exception ex){

        }
        return diff;
    }


    private String calculateDue(String dueDate){
        if(getDifference(dueDate) <= 0 ){
            return "overDue";
        }
        else if(getDifference(dueDate) <= 7){
            return "almostDue";
        }
        else{
            return "okay";
        }
    }


    @Override
    public int getItemCount() {
        return electricityIDs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtID, txtCallName, txtNetAmount, txtPaidAmount, txtDueDate;
        CardView cardNotif;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardNotif = itemView.findViewById(R.id.cardNotif);
            txtID = itemView.findViewById(R.id.txtBillIDVal);
            txtCallName = itemView.findViewById(R.id.txtCallName);
            txtNetAmount = itemView.findViewById(R.id.txtNetAmount);
            txtPaidAmount = itemView.findViewById(R.id.txtPayementDateDesc);
            txtDueDate = itemView.findViewById(R.id.txtDueDate);
        }
    }



    //for datees
    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        String date = "";
        if(month < 9){
            date = year + "-0" + (month+1) + "-" + dayOfMonth;
        }
        else{
            date = year + "-" + (month+1) + "-" + dayOfMonth;
        }
        etDate.setText(date);
    }

}
