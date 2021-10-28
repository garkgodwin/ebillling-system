package com.example.electricbillingsystem.Electricity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electricbillingsystem.R;
import com.example.electricbillingsystem.database.DatabaseHelper;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ElectricityHomeActitvity extends AppCompatActivity  {

    Spinner spMeterBoards;
    DatabaseHelper databaseHelper;
    GraphView graphView;

    LineGraphSeries<DataPoint> series = null;

    EditText  etEnergy, etEnergyDate;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity_home_actitvity);
        databaseHelper = new DatabaseHelper(ElectricityHomeActitvity.this);
        setViews();
        databaseHelper.setSumOfConsumptionUnit();
        setMeterChoices("name");
        String name = spMeterBoards.getSelectedItem().toString();
        int meterBoardID = databaseHelper.getMeterBoardID(name, "");
        if(meterBoardID != 0){
            getDataPoint(meterBoardID);
        }
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return sdf.format(new Date((long) value));
                }
                else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        spMeterBoards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = spMeterBoards.getSelectedItem().toString();
                int meterBoardID = databaseHelper.getMeterBoardID(name, "");
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setTextSize(18);
                if(meterBoardID != 0){
                    etEnergy.setText("");
                    etEnergyDate.setText("");
                    getDataPoint(meterBoardID);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSeriesData(List<DataPoint>  dataPoints){
        series = new LineGraphSeries<>(
                dataPoints.toArray(new DataPoint[0]));
        series.setColor(Color.RED);
        series.setThickness(10);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.GRAY);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setTitle("Total Energy per Month");
        series.setAnimated(true);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                try {
                    if(dataPoint.getY() == 0){
                        etEnergy.setText("You have no data for this month.");
                        etEnergyDate.setText("No Date Available." );
                    }
                    else {
                        etEnergy.setText("Energy Consumption: " + dataPoint.getY() + "Kw");
                        double val = dataPoint.getX();
                        long l = (long) val;
                        Date date = new Date(l);
                        String s = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(date);
                        etEnergyDate.setText("Reading date: " + s);
                    }
                }
                catch (Exception ex){
                    Toast.makeText(ElectricityHomeActitvity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    int count = 4;
    private void setGraphDesign(){
        graphView.removeAllSeries();
        graphView.addSeries(series);
        graphView.setBackgroundColor(Color.rgb(45,45,45));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(count);
        graphView.getGridLabelRenderer().setNumVerticalLabels(5);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Read Date");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Energy Consumption (Kilowatts)");
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(135);
        graphView.getGridLabelRenderer().setLabelsSpace(15);
        graphView.getGridLabelRenderer().setGridColor(Color.WHITE);
        graphView.getGridLabelRenderer().setPadding(60);
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setVerticalAxisTitleColor(Color.GRAY);
        graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.GRAY);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        try {
            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setYAxisBoundsManual(true);
            Double minY = databaseHelper.selectMinEnergy();
            Double maxY = databaseHelper.selectMaxEnergy();
            graphView.getViewport().setMinY(0);
            graphView.getViewport().setMaxY(maxY);
            graphView.getGridLabelRenderer().setHumanRounding(false, true);
        }
        catch (Exception ex){
            Toast.makeText(ElectricityHomeActitvity.this, "Cannot set min and max for graph.\n" + ex.toString() , Toast.LENGTH_SHORT).show();
        }

        graphView.getViewport().setScrollable(true);  // activate horizontal scrolling
        graphView.getViewport().scrollToEnd();
        graphView.getGridLabelRenderer().reloadStyles();
    }

    private void getDataPoint(int meterBoardID){
        graphView.onDataChanged(false, false);
        Cursor cursor = databaseHelper.getEData(meterBoardID);
        Date d = null;
        double energy = 0.0;
            if (cursor.moveToFirst()) {
                List<DataPoint> dataPoints = new ArrayList<DataPoint>();
                do {
                        try{
                        String d1 = cursor.getString(cursor.getColumnIndex("readingDate"));
                        d = new SimpleDateFormat("yyyy-MM-dd").parse(d1);
                        energy = Math.round(cursor.getDouble(cursor.getColumnIndex("consumptionUnit")));
                        dataPoints.add(
                                new DataPoint(d.getTime(),
                                        energy));
                        }
                        catch (Exception ex){
                        }
                }
                while (cursor.moveToNext());
                //add one more data?
                if(dataPoints.size() == 1){
                    Toast.makeText(ElectricityHomeActitvity.this, "You only have 1 data", Toast.LENGTH_SHORT);
                    graphView.setVisibility(View.GONE);
                    try {
                        double val = d.getTime();
                        long l = (long)val;
                        Date date = new Date(l);
                        String s = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(date);
                        etEnergyDate.setText("Reading date: " + s);
                        etEnergy.setText(energy + "Kw");
                    }
                    catch (Exception ex){

                    }
                }
                else {
                    graphView.setVisibility(View.VISIBLE);
                    setSeriesData(dataPoints);
                    setGraphDesign();
                }
            }
    }


    //Set meter choices in spinner
    private void setMeterChoices(String name){
        try {
            List<String> list = databaseHelper.getMeterBoardNamesForElectricityBoard();
            if (list == null) {
                Toast.makeText(ElectricityHomeActitvity.this, "Cannot proceed without at least 1 meter board.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayAdapter allAdapter = new ArrayAdapter<String>(ElectricityHomeActitvity.this,
                    android.R.layout.simple_list_item_1, list)
            {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    /// Get the Item from ListView
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    // Set the text size 25 dip for ListView each item
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    tv.setBackgroundColor(Color.parseColor("#333333"));
                    // Return the view
                    return view;
                }
            };
            spMeterBoards.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            spMeterBoards.setAdapter(allAdapter);
        }
        catch (Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //initializes the views
    private void setViews(){
        graphView = findViewById(R.id.graphView);
        spMeterBoards = findViewById(R.id.spEMeterBoard);
        etEnergy = findViewById(R.id.etEnergyC);
        etEnergyDate = findViewById(R.id.etEnergyDate);

    }

}