package com.example.electricbillingsystem.QuickView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.DatabaseHelper;

import java.util.ArrayList;

public class QuickViewCustomAdapter  extends RecyclerView.Adapter<QuickViewCustomAdapter.MyViewHolder> {
    Context context;
    ArrayList invoiceIDs, netAmounts, readingDates, dueDates, paidAmounts;
    DatabaseHelper databaseHelper;

    public QuickViewCustomAdapter(Context context,
                                  ArrayList invoiceIDs,
                                  ArrayList netAmounts,
                                  ArrayList readingDates,
                                  ArrayList dueDates,
                                  ArrayList paidAmounts,
                                  DatabaseHelper databaseHelper) {
        this.context = context;
        this.invoiceIDs = invoiceIDs;
        this.netAmounts = netAmounts;
        this.readingDates = readingDates;
        this.dueDates = dueDates;
        this.paidAmounts = paidAmounts;
        this.databaseHelper = databaseHelper;
    }
//return invoiceID +"\t\t\t\t" + netAmount +"\t\t\t\t" + readingDate + "\t\t\t\t" + dueDate + "\t\t\t\t" + paidAmount ;

    @NonNull
    @Override
    public QuickViewCustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.quick_row, parent, false);
        databaseHelper = new DatabaseHelper(context);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuickViewCustomAdapter.MyViewHolder holder, int position) {
        showData(holder, position);
    }

    private void showData(@NonNull QuickViewCustomAdapter.MyViewHolder holder, int position) {

        Double paid = Double.parseDouble(String.valueOf(paidAmounts.get(position)));
        Double net = Double.parseDouble(String.valueOf(netAmounts.get(position)));
        String output = "";
        if(paid >= net){
            output = "Paid";
        }
        else if(paid < net && paid > 0){
            output = "Not Fully Paid";
        }
        else{
            output = "Not Paid";
        }
        holder.txtInvoiceID.setText(String.valueOf(invoiceIDs.get(position)));
        holder.txtNetAmount.setText("₱"+String.valueOf(netAmounts.get(position)));
        holder.txtReadingDate.setText(String.valueOf(readingDates.get(position)));
        holder.txtDueDate.setText(String.valueOf(dueDates.get(position)));
        holder.txtPaidAmount.setText(output);

    }


        @Override
    public int getItemCount() {
        return invoiceIDs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtInvoiceID, txtNetAmount, txtReadingDate, txtDueDate, txtPaidAmount;
        CardView cardQuickView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardQuickView = itemView.findViewById(R.id.cardQuickView);
            txtInvoiceID = itemView.findViewById(R.id.txtBillIDVal);
            txtNetAmount = itemView.findViewById(R.id.txtNetAmount);
            txtReadingDate = itemView.findViewById(R.id.txtPayementDateDesc);
            txtDueDate = itemView.findViewById(R.id.txtDueDate);
            txtPaidAmount = itemView.findViewById(R.id.txtEmergyVal);
        }
    }
}
