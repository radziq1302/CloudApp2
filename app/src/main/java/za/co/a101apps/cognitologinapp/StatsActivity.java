package za.co.a101apps.cognitologinapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Scroller;
import android.widget.Spinner;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class StatsActivity extends AppCompatActivity {

    private ArrayList<DBUserData> statystyki = new ArrayList<>();
    private ArrayList<String> daysArray = new ArrayList<>();
    private ArrayList<Integer> stepsArray = new ArrayList<>();
    private ArrayList<Integer> weightArray = new ArrayList<>();
    private ArrayList<Integer> sleepArray = new ArrayList<>();
    private ArrayList<Integer> waterArray = new ArrayList<>();

    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        final String nazwa_uz = (String) getIntent().getStringExtra("idZasrane");

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final String currentDateandTime = sdf.format(new Date());
        final String earliestDate = "20190607";

        try {
            Date startDate = sdf.parse(earliestDate);
            Date endDate = sdf.parse(currentDateandTime);

            Calendar start = Calendar.getInstance();
            start.setTime(startDate);

            Calendar end = Calendar.getInstance();
            end.setTime(endDate);


            while( !start.after(end)){
                Date targetDay = start.getTime();

                String dateString = sdf.format(targetDay.getTime());

                daysArray.add(dateString);

                Log.i("KOLEJNA DATA: ", dateString);
                Log.i("-", "-");

                String id=dateString+"-"+nazwa_uz;

                StatsActivity.GetItemAsyncTask getItemTask = new StatsActivity.GetItemAsyncTask();
                getItemTask.execute(id);

                start.add(Calendar.DATE, 1);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


        spinner = (Spinner) findViewById(R.id.wybor_wykresu);

        // chosen_plot = spinner.getSelectedItem().toString();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if(selectedItem.equals("Waga"))
                {
                    String[] axisData = new String[daysArray.size()];
                    axisData = daysArray.toArray(axisData);

                    Integer[] yAxisData = new Integer[weightArray.size()];
                    yAxisData = weightArray.toArray(yAxisData);

                    Log.i("LISTA INTEGER : ", yAxisData.toString());

                    drawPlot(axisData, yAxisData);
                }

                if(selectedItem.equals("Kroki"))
                {

                    String[] axisData = new String[daysArray.size()];
                    axisData = daysArray.toArray(axisData);

                    Integer[] yAxisData = new Integer[stepsArray.size()];
                    yAxisData = stepsArray.toArray(yAxisData);

                    drawPlot(axisData, yAxisData);
                }

                if(selectedItem.equals("Woda"))
                {
                    String[] axisData = new String[daysArray.size()];
                    axisData = daysArray.toArray(axisData);

                    Integer[] yAxisData = new Integer[waterArray.size()];
                    yAxisData = waterArray.toArray(yAxisData);

                    drawPlot(axisData, yAxisData);
                }

                if(selectedItem.equals("Sen"))
                {
                    // do your stuff
                }


            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });



    }


    private void drawPlot(String[] axisData, Integer[] yAxisData) {

        LineChartView lineChartView = findViewById(R.id.chart);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));


        for(int i = 0; i < axisData.length; i++){
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++){
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }


        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis yAxis = new Axis();
        data.setAxisYLeft(yAxis);

        Axis xAxis = new Axis();
        data.setAxisYLeft(xAxis);

        xAxis.setTextSize(5);

        lineChartView.setLineChartData(data);

    }





    private String TAG = "get data from db";

    private class GetItemAsyncTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... id) {

            Document document = null;

            Log.i(TAG, "in GetItemAsyncTask doInBackground....");
            DbAccess databaseAccess = DbAccess.getInstance(StatsActivity.this);
            try {
                Log.i(TAG, "getting data: " + id[0]);
                document = databaseAccess.getItem(id[0]);
            }
            catch (Exception e) {
                Log.i(TAG, "error getting data: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*update text views on main thread*/
                        //wartosc_waga.setText("Error getting data");
                        //wartosc_waga.setBackgroundColor(Color.RED);

                    }
                });
            }
            return document;
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            //load text view

            if (document != null) {
                String id_z_bazy = String.valueOf(document.get("ID").asString());
                String waga_z_bazy = String.valueOf(document.get("waga").asString());
                String kroki_z_bazy = String.valueOf(document.get("kroki").asString());
                String woda_z_bazy = String.valueOf(document.get("woda").asString());
               // String sen_z_bazy = String.valueOf(document.get("sen").asString());

                try {
                    String jsonDocument = Document.toJson(document);
                    Log.i("juzNieMoge", "Contact: " + jsonDocument);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("juzNieMoge", "error in GetItemAsyncTask show contact as json: " + e.getLocalizedMessage());
                }

                if (waga_z_bazy != null && woda_z_bazy != null && kroki_z_bazy != null) {

                    DBUserData tempUser = new DBUserData(id_z_bazy, waga_z_bazy, kroki_z_bazy, woda_z_bazy);
                    // statystyki.add(tempUser);

                    stepsArray.add(Integer.parseInt(kroki_z_bazy));
                    waterArray.add(Integer.parseInt(woda_z_bazy));
                    weightArray.add(Integer.parseInt(waga_z_bazy));
                   // sleepArray.add(Integer.parseInt(sen_z_bazy));

                    Log.i(TAG, "PRZYSZŁO Z BAZY: " + tempUser.toString());

                } else {
                    Log.i(TAG, "error getting data: " + "1: " + waga_z_bazy  + "2: " + woda_z_bazy  + "3: " + kroki_z_bazy);
                }
            } else {
                Log.i(TAG, "error getting data: " + "ktores jest nullem");
            }
        }
    }


}
