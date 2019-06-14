package za.co.a101apps.cognitologinapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InputMeasuresActivity extends AppCompatActivity {

    private EditText wprowadzona_wartosc_waga;
    private EditText wprowadzona_wartosc_sen;
    private boolean valid = false;
    private Button wyslij;
    private Button powrot;
    private String type;
    private Intent come_back;
    private String TAG = "InputMeasures";
    private String kroki;
    private String woda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_measures);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final String currentDateandTime = sdf.format(new Date());
        Log.v("data691", currentDateandTime+"");
        // zmienna type mowi czy to jest waga czy sen

        kroki = getIntent().getStringExtra("kroki");
        woda = getIntent().getStringExtra("woda");

        wprowadzona_wartosc_sen = (EditText) findViewById(R.id.sen);
        wprowadzona_wartosc_waga = (EditText) findViewById(R.id.waga);

        wyslij = (Button) findViewById(R.id.button_send);
        powrot = (Button) findViewById(R.id.button_back);

        come_back = new Intent(InputMeasuresActivity.this, MainScreenActivity.class);

        // jesli "powrot" - ok, wracamy

        powrot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(come_back);
            }
        });

        // jesli "wyslij"
        // tutaj to sie musi gdzies zapisac i wyslac i wrocic do glownego ekranu
        // switch ze zmienna "type"

        wyslij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (wprowadzona_wartosc_sen.getText().toString().equals("") && wprowadzona_wartosc_waga.getText().toString().equals("")) {
                    valid = false;
                    Log.i("warunek sen",wprowadzona_wartosc_sen.getText().toString());
                    Log.i("warunek waga",wprowadzona_wartosc_waga.getText().toString());
                } else {
                    valid = true;

                }



                if(valid) {

                    Log.i("dostalismy wartosc: ","waga");


                    final String nazwa_uz = (String) getIntent().getStringExtra("idUser");
                    Log.v("data6969", nazwa_uz+"");
                    String id=currentDateandTime+"-"+nazwa_uz.toString();

                    Log.v("data6969", kroki);
                    Log.v("data6969", woda);

                    DBUserData userdata = new DBUserData(id, wprowadzona_wartosc_waga.getText().toString(), kroki, woda, wprowadzona_wartosc_sen.getText().toString());
                    InputMeasuresActivity.AddItemAsyncTask addItemTask;
                    if (userdata != null) {
                        Log.v("cojest",userdata.getID()+userdata.getWaga()+"");
                        addItemTask = new InputMeasuresActivity.AddItemAsyncTask();
                        Log.i(TAG, "getting random contact...");
                        Gson gson = new Gson();
                        String json = gson.toJson(userdata);
                        Document doc = Document.fromJson(json);
                        addItemTask.execute(doc); }
                    Log.i("input measures","poszlo do bazy");


                    startActivity(come_back);

                } else {
                    Toast.makeText(InputMeasuresActivity.this, "wprowadz dane", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
    private class AddItemAsyncTask extends AsyncTask<Document, Void, Document> {
        @Override
        protected Document doInBackground(Document... documents) {
            Document result = null;
            Log.i(TAG, "in AddItemAsyncTask doInBackground....");
            DbAccess databaseAccess = DbAccess.getInstance(InputMeasuresActivity.this);
            try {
                result = databaseAccess.addContact(documents[0]);
            } catch (Exception e) {
                Log.i(TAG, "error adding contact: " + e.getMessage());

            }
            return result;
        }

        @Override
        protected void onPostExecute(Document result) {
            super.onPostExecute(result);
            String message;

            if (result == null) {
                Log.i(TAG, "Contact added, no return data");
                message = "Contact added, no return data";
            } else {
                Log.i(TAG, "Contact added, return data" + result);
                message = "Contact added, return data: " + result;
            }

            Log.i(TAG, "adding item result: " + message);
        }
    }
    public void addDataToDB(String dataType, DBObject object, Context context) {

        InputMeasuresActivity.this.attachBaseContext(context);

        InputMeasuresActivity.AddItemAsyncTask addItemTask = new InputMeasuresActivity.AddItemAsyncTask();
        /*get a random contact (name and telephone only)*/
        Log.i(TAG, "getting the passed object of type..." + dataType);

        Log.i(TAG, "Adding new object to database: " + object);

        Gson gson = new Gson();
        String json = gson.toJson(object);

        Document doc = Document.fromJson(json);

        addItemTask.execute(doc);

        Log.i(TAG, "Chyba dodano ?? Obiekt typu " + object);

    }
}
