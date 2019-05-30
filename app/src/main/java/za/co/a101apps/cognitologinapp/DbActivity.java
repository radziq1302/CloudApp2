package za.co.a101apps.cognitologinapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class DbActivity extends AppCompatActivity {

    private String TAG = "DynamoDb_Demo";
    private TextView textViewItem;
    private CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        textViewItem = findViewById(R.id.textViewItem);
        textViewItem.setText("Press a button...");
        textViewItem.setBackgroundColor(Color.GREEN);
        Button buttonGetItem = findViewById(R.id.buttonGetItem);
        buttonGetItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetItemAsyncTask getItemTask = new GetItemAsyncTask();
                /*get a random telephone */
                String telephoneNumber = "ID1";
                textViewItem.setText("Busy getting contact: " + telephoneNumber);


                Log.i(TAG, "Getting contact for telephone number: " + telephoneNumber);
                getItemTask.execute(telephoneNumber);
            }
        });
        Button buttonPutItem = findViewById(R.id.buttonPutItem);
        buttonPutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemAsyncTask addItemTask = new AddItemAsyncTask();
                /*get a random contact (name and telephone only)*/
                Log.i(TAG, "getting random contact...");
                Woda woda = new Woda("2","10","2019-02-12");
                textViewItem.setText("Adding contact: " + woda.getID());


                Log.i(TAG, "Adding contact with telephone number: " + woda.getID());


                /*convert contact to json string - not necessary though*/

                /*You can convert between JSON and Document objects. However
                , you will lose fidelity when converting from a Document object to JSON.
                This is because not all data types that can be stored in DynamoDB can be represented in JSON.
                Use Document.fromJson() and Document.toJson() to perform the conversion.*/
                Gson gson = new Gson();
                String json = gson.toJson(woda);

                /*convert json string to document*/
                Document doc = Document.fromJson(json);

                /*add contact to table*/
                addItemTask.execute(doc);
            }
        });
    }
    private class GetItemAsyncTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... id) {

            Document document = null;

            Log.i(TAG, "in GetItemAsyncTask doInBackground....");
            DbAccess databaseAccess = DbAccess.getInstance(DbActivity.this);
            try {
                Log.i(TAG, "getting contact for telephone: " + id[0]);
                document = databaseAccess.getItem(id[0]);
            } catch (Exception e) {
                Log.i(TAG, "error getting contacts: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*update text views on main thread*/
                        textViewItem.setText("Error getting contact");
                        textViewItem.setBackgroundColor(Color.RED);
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
                String number = String.valueOf(document.get("number").asString());

                try {
                    String jsonDocument = Document.toJson(document);
                    Log.i(TAG, "Contact: " + jsonDocument);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "error in GetItemAsyncTask show contact as json: " + e.getLocalizedMessage());
                }

                if (number != null) {
                    textViewItem.setText(number);
                    textViewItem.setBackgroundColor(Color.GREEN);
                } else {
                    textViewItem.setText("Contact not found");
                    textViewItem.setBackgroundColor(Color.YELLOW);
                }
            } else {
                textViewItem.setText("Contact not found");
                textViewItem.setBackgroundColor(Color.YELLOW);
            }
        }
    }
    private class AddItemAsyncTask extends AsyncTask<Document, Void, Document> {
        @Override
        protected Document doInBackground(Document... documents) {
            Document result = null;
            Log.i(TAG, "in AddItemAsyncTask doInBackground....");
            DbAccess databaseAccess = DbAccess.getInstance(DbActivity.this);
            try {
                result = databaseAccess.addContact(documents[0]);
            } catch (Exception e) {
                Log.i(TAG, "error adding contact: " + e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewItem.setText("Error adding contact");
                        textViewItem.setBackgroundColor(Color.RED);
                    }
                });
            }
            return result;
        }

        @Override
        protected void onPostExecute(Document result) {
            super.onPostExecute(result);
            String message;

            if (result == null) {
                /*for ReturnValue.NONE*/
                textViewItem.setText("Contact added");
                textViewItem.setBackgroundColor(Color.GREEN);
                message = "Contact added, no return data";
            } else {
                /*for ReturnValue.ALL_OLD*/
                textViewItem.setText("Contact added");
                textViewItem.setBackgroundColor(Color.YELLOW);
                message = "Contact added, return data: " + result;
            }

            Log.i(TAG, "adding item result: " + message);
        }
    }

}