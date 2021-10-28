package com.example.electricbillingsystem.QuickView;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.DatabaseHelper;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.List;

public class QuickViewHomeActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;



    GraphView graphView;
    TextView txtTotal;
    LineGraphSeries<DataPoint> series;

    BarGraphSeries<DataPoint> series1;

    boolean isInside = false;


    //for recyclerview
    //return invoiceID +"\t\t\t\t" + netAmount +"\t\t\t\t" + readingDate + "\t\t\t\t" + dueDate + "\t\t\t\t" + paidAmount ;

    RecyclerView rvQuick;
    ArrayList<String> invoiceIDs, netAmounts, readingDates, dueDates, paidAmounts;
    TextView txtName;
    QuickViewCustomAdapter customAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_view_home);
        databaseHelper = new DatabaseHelper(QuickViewHomeActivity.this);

        graphView = findViewById(R.id.graphQuick);
        rvQuick = findViewById(R.id.rvQuickView);
        txtName = findViewById(R.id.txtName);
        txtTotal = findViewById(R.id.txtQuickTotal);

        invoiceIDs = new ArrayList<>();
        netAmounts = new ArrayList<>();
        readingDates = new ArrayList<>();
        dueDates = new ArrayList<>();
        paidAmounts = new ArrayList<>();



        int countOfBilling = databaseHelper.getBillingCount();

        if (countOfBilling != 0) {
            setGeneral();

        }
            series1.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    int meterBoardID = (int) dataPoint.getX();
                    storeDataInArrays(meterBoardID);

                    customAdapter = new QuickViewCustomAdapter(QuickViewHomeActivity.this, invoiceIDs, netAmounts, readingDates, dueDates, paidAmounts, databaseHelper);

                    rvQuick.setAdapter(customAdapter);
                    rvQuick.setLayoutManager(new LinearLayoutManager(QuickViewHomeActivity.this));

                }
            });


            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        return "ID: " + (int) value;
                    } else {
                        return "₱" + value;
                    }
                }
            });
        }

    //populate recycleview
    private void storeDataInArrays(int meterBoardID){
        String name = txtName.getText().toString();
        try {
            Cursor cursor = databaseHelper.getQuickView(meterBoardID);
            double sum = 0;
            if (cursor.moveToFirst()) {
                invoiceIDs.clear();
                dueDates.clear();
                netAmounts.clear();
                paidAmounts.clear();
                readingDates.clear();
                txtName.setText("");
                do{
                    invoiceIDs.add(cursor.getString(0));
                    dueDates.add(cursor.getString(1));
                    netAmounts.add(cursor.getString(2));
                    double netAmount = Double.parseDouble(cursor.getString(2));
                    sum += netAmount;
                    paidAmounts.add(cursor.getString(3));
                    readingDates.add(cursor.getString(4));
                    if(TextUtils.isEmpty(txtName.getText())){
                        //set title
                        txtName.setText("Meter board: " + cursor.getString(5));
                    }
                }
                while (cursor.moveToNext());

                String format = String.format("%.2f", sum);
                txtTotal.setText("Total: ₱" + format);
            }
            else{

                Toast.makeText(QuickViewHomeActivity.this, "Empty data", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(QuickViewHomeActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    //populate graph

    double maxID = 0;
    private void populateWithMeterIDAndTotalNet(){
            Cursor cursor = databaseHelper.getBillingGeneralData();
            if (cursor != null) {
                //get data
                if (cursor.moveToFirst()) {
                    List<DataPoint> dataPoints = new ArrayList<DataPoint>();
                    //for minimum
                    do {
                        double data = Math.round(cursor.getFloat(cursor.getColumnIndex("totalNetAmount")));
                        double meterBoardID = cursor.getDouble(cursor.getColumnIndex("meterBoardID"));
                        maxID = meterBoardID;
                                //Double.parseDouble(df.format("#.##",cursor.getDouble(cursor.getColumnIndex("totalNetAmount"))));
                        dataPoints.add(
                                new DataPoint(meterBoardID, data));

                    }
                    while (cursor.moveToNext());

                    series1 = new BarGraphSeries<>(
                            dataPoints.toArray(new DataPoint[0])
                    );
                    series1.setValuesOnTopColor(Color.WHITE);
                    series1.setValuesOnTopSize(24);
                    series1.setSpacing(5);
                    series1.setAnimated(true);
                    series1.setDrawValuesOnTop(true);
                    graphView.addSeries(series1);

                    series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                        @Override
                        public int get(DataPoint data) {
                            if(data.getX() % 2 != 0){
                                return Color.argb((float) 0.5, 0, 120, 120);
                            }
                            else{
                                return Color.argb((float) 0.8, 0, 100, 100);
                            }
                        }
                    });
                }

            }
    }

    int count = 0;
    private void setGeneral(){
        count = databaseHelper.countMeterBoard();
        graphView.removeAllSeries();
        populateWithMeterIDAndTotalNet();
        isInside = false;
        graphView.getGridLabelRenderer().setVerticalAxisTitleColor(Color.GRAY);
        graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.GRAY);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(count);
        graphView.getGridLabelRenderer().setNumVerticalLabels(5);
        graphView.getGridLabelRenderer().setLabelsSpace(20);
        graphView.getGridLabelRenderer().setGridColor(Color.WHITE);
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setPadding(50);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        //THIS TWO LINES WILL GET ALSO THE LAST VALUES

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxY(databaseHelper.getMaxY());
        graphView.getGridLabelRenderer().setHumanRounding(true, true);


        graphView.getViewport().setScrollable(true);  // activate horizontal scrolling

        graphView.setBackgroundColor(Color.rgb(45,45,45));

        graphView.getViewport().scrollToEnd();

        //series.setAnimated(true);


    }
}