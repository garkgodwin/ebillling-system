package com.example.electricbillingsystem.Billing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.DatabaseHelper;

import java.util.ArrayList;

public class BillingCustomAdapter extends RecyclerView.Adapter<BillingCustomAdapter.MyViewHolder>{

    public int billID = 0;
    private int lastPosition = -1;
    int row_index = -1;
    Context context;
    ArrayList billIDs, callNames, netAmounts, paymentDates, energyUnits;
    DatabaseHelper databaseHelper;

    BillingCustomAdapter(Context context,
                         ArrayList billIDs,
                         ArrayList callNames,
                         ArrayList netAmounts,
                         ArrayList paymentDates,
                         ArrayList energyUnits){
        this.context = context;
        this.billIDs = billIDs;
        this.callNames = callNames;
        this.netAmounts = netAmounts;
        this.paymentDates = paymentDates;
        this.energyUnits = energyUnits;
        this.callNames = callNames;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_billing_home, parent, false);
        databaseHelper = new DatabaseHelper(context);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        showData(holder, position);

        holder.cardBill.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                billID = Integer.parseInt(String.valueOf(billIDs.get(position)));
                row_index = position;
                notifyDataSetChanged();
            }
        });

        holder.btnRowUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int billID = Integer.parseInt(String.valueOf(billIDs.get(position)));
                if (billID != 0) {

                    //This will update the remaining nulls to zeros
                    databaseHelper.updateAllEmptyBills();
                    //show form
                    Intent i = new Intent(context, BillingFormActivity.class);
                    i.putExtra("action", 2);
                    i.putExtra("billID",  billID);
                    i.putExtra("callName", String.valueOf(callNames.get(position)));
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);

                } else {
                    Toast.makeText(context, "Please make sure you chose a row to update." + billID, Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnRowDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int billID = Integer.parseInt(String.valueOf(billIDs.get(position)));
                if(billID != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("System Message");
                    builder.setMessage("Are you sure you want to DELETE these data?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //This will delete the datas from the three tables
                            DeleteData(billID);

                            Intent i = new Intent(context, BillingHomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(i);

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

                else{
                    Toast.makeText(context, "Please select row to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(row_index == position){
            holder.cardBill.setCardBackgroundColor(Color.BLACK);
            holder.cardBill.setBackgroundColor(Color.BLACK);
        }
        else
        {
            holder.cardBill.setCardBackgroundColor(Color.parseColor("#2f2f2f"));
            holder.cardBill.setBackgroundColor(Color.parseColor("#2f2f2f"));
        }


    }

    private void showData(@NonNull MyViewHolder holder, int position){
        holder.txtBillID.setText(String.valueOf(billIDs.get(position)));
        holder.txtCallName.setText(String.valueOf(callNames.get(position)));
        holder.txtNetAmount.setText(String.valueOf(netAmounts.get(position)));
        holder.txtPaymentDate.setText(String.valueOf(paymentDates.get(position)));
        String payDateDesc = holder.txtPaymentDate.getText().toString();
        if(payDateDesc == "" || TextUtils.isEmpty(holder.txtPaymentDate.getText())){
            holder.txtPaymentDate.setText("Unpaid");
            holder.txtPaymentDate.setTextSize(16);
            holder.txtPaymentDate.setTextColor(Color.RED);
            holder.txtPaymentDateDesc.setTextColor(Color.TRANSPARENT);
        }
        else{
            holder.txtPaymentDate.setTextSize(12);
            holder.txtPaymentDate.setTextColor(Color.WHITE);
            holder.txtPaymentDateDesc.setTextColor(Color.WHITE);
        }
        holder.txtConsumption.setText(String.valueOf(energyUnits.get(position)));
    }


    @Override
    public int getItemCount() {
        return billIDs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtBillID, txtCallName, txtNetAmount, txtPaymentDate, txtConsumption;
        TextView txtPaymentDateDesc;
        CardView cardBill;
        ImageButton btnRowUpdate, btnRowDelete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardBill = itemView.findViewById(R.id.cardBill);
            txtBillID = itemView.findViewById(R.id.txtBillIDVal);
            txtCallName = itemView.findViewById(R.id.txtCallNameVal);
            txtNetAmount = itemView.findViewById(R.id.txtNetAmountVal);
            txtPaymentDate = itemView.findViewById(R.id.txtPaymentDateVal);
            txtConsumption = itemView.findViewById(R.id.txtEmergyVal);

            btnRowDelete = itemView.findViewById(R.id.btnRowDelete);
            btnRowUpdate = itemView.findViewById(R.id.btnRowUpdate);

            txtPaymentDateDesc = itemView.findViewById(R.id.txtPayementDateDesc);
        }
    }



    //methods
    private void DeleteData(int billID){
        if(billID != 0){
            if(!databaseHelper.deleteBilling(billID)){
                if(!databaseHelper.deleteInvoice(billID)){
                    if(!databaseHelper.deleteEBoard(billID)){
                        notifyDataSetChanged();
                        billID = 0;
                        Toast.makeText(context, "Bill deleted!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}
