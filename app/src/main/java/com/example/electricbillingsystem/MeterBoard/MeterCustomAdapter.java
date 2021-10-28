package com.example.electricbillingsystem.MeterBoard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MeterCustomAdapter extends RecyclerView.Adapter<MeterCustomAdapter.MyViewHolder> {

    public int meterID = 0;
    double totalKwh;
    String callName;
    String icp;
    int status;
    private int lastPosition = -1;
    int row_index = -1;
    Context context;
    ArrayList meterIDs, callNames, icps, totalKwhs, statuss;
    DatabaseHelper databaseHelper;


    MeterCustomAdapter(Context context,
                       ArrayList meterIDs,
                       ArrayList callNames,
                       ArrayList icps,
                       ArrayList totalKwhs,
                       ArrayList statuss){
        this.context = context;
        this.meterIDs = meterIDs;
        this.callNames = callNames;
        this.icps = icps;
        this.totalKwhs = totalKwhs;
        this.statuss = statuss;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_meter_home, parent, false);
        databaseHelper = new DatabaseHelper(context);
        return new MeterCustomAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        showData(holder, position);


        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meterID = Integer.parseInt(holder.txtMeterID.getText().toString());
                if(meterID != 0){
                    Intent i = new Intent(context, MeterBoardFormActivity.class);
                    i.putExtra("id", meterID);
                    i.putExtra("action", 2);
                    context.startActivity(i);
                }
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meterID = Integer.parseInt(holder.txtMeterID.getText().toString());
                int count = databaseHelper.countElectricityBoardData(meterID);
                if(count > 0){
                    String bee = "";
                    if(count == 1){
                        bee = "is";
                    }
                    else{
                        bee = "are";
                    }
                    Toast.makeText(context, "There "+ bee + " "+ count + " data inside the Electricity Board." +
                            "\nCannot delete this Meter Board.", Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Meter Board");
                    builder.setMessage("Are you sure you want to delete this meter board?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            boolean deleted = databaseHelper.deleteMeterBoard(meterID);
                            if(!deleted){
                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(context, MeterBoardHomeActivity.class);
                                context.startActivity(i);
                            }
                            else{
                                Toast.makeText(context, "Failed to delete!!!", Toast.LENGTH_SHORT).show();
                            }

                            dialog.dismiss();
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return meterIDs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMeterID, txtCallName, txtIcp, txtTotal, txtStatus;
        CardView cardMeterRow;
        ImageButton btnUpdate, btnDelete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMeterRow = itemView.findViewById(R.id.cardMeterRow);
            txtMeterID = itemView.findViewById(R.id.txtMeterIDVal);
            txtCallName = itemView.findViewById(R.id.txtCallNameVal);
            txtIcp = itemView.findViewById(R.id.txtIcpVal);
            txtTotal = itemView.findViewById(R.id.txtTotalKwhVal);
            txtStatus = itemView.findViewById(R.id.txtStatusVal);
            btnUpdate = itemView.findViewById(R.id.btnUpdateMeter);
            btnDelete = itemView.findViewById(R.id.btnDeleteMeter);
        }
    }


    private void showData(MyViewHolder holder, int position){
        holder.txtMeterID.setText(String.valueOf(meterIDs.get(position)));
        holder.txtCallName.setText(String.valueOf(callNames.get(position)));
        holder.txtIcp.setText(String.valueOf(icps.get(position)));
        holder.txtTotal.setText(String.valueOf(totalKwhs.get(position)));
        holder.txtStatus.setText(String.valueOf(statuss.get(position)));
    }

}
