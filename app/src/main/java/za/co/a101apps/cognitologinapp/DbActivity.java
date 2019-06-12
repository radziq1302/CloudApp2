package za.co.a101apps.cognitologinapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.UserStateListener;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class DbActivity extends AppCompatActivity {

    private String TAG = "DynamoDb_Demo";
    private TextView textViewItem;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private Button buttonBack;
    private DBUserData dane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        try {
            dane = (DBUserData) getIntent().getSerializableExtra("daneFormularza");
            final String ajDi = (String) getIntent().getStringExtra("idZasrane");
            dane.setID(ajDi);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        textViewItem = findViewById(R.id.textViewItem);
        textViewItem.setText("Press a button...");
        textViewItem.setBackgroundColor(Color.GREEN);
        Button buttonGetItem = findViewById(R.id.buttonGetItem);
        buttonBack=findViewById(R.id.buttonBack);


        //final CognitoSettings cognitoSettings = new CognitoSettings(this);
        //credentialsProvider = cognitoSettings.getCredentialsProvider();
        //CognitoUser currentUser = cognitoSettings.getUserPool().getCurrentUser();

        AWSMobileClient.getInstance().addUserStateListener(new UserStateListener() {
            @Override
            public void onUserStateChanged(UserStateDetails userStateDetails) {
                switch (userStateDetails.getUserState()){
                    case GUEST:
                        Log.i("userState", "user is in guest mode");
                        break;
                    case SIGNED_OUT:
                        Log.i("userState", "user is signed out");
                        break;
                    case SIGNED_IN:
                        Log.i("userState", "user is signed in");
                        break;
                    case SIGNED_OUT_USER_POOLS_TOKENS_INVALID:
                        Log.i("userState", "need to login again");
                        break;
                    case SIGNED_OUT_FEDERATED_TOKENS_INVALID:
                        Log.i("userState", "user logged in via federation, but currently needs new tokens");
                        break;
                    default:
                        Log.e("userState", "unsupported");
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemAsyncTask addItemTask;
                if (dane != null) {
                    Log.v("cojest",dane.getID()+dane.getWaga()+"");
                    addItemTask = new AddItemAsyncTask();
                    Log.i(TAG, "getting random contact...");
                    textViewItem.setText("Adding contact: ");
                    Log.i(TAG, "Adding data: ");
                    Gson gson = new Gson();
                    String json = gson.toJson(dane);
                    Document doc = Document.fromJson(json);
                    addItemTask.execute(doc); }
                Intent i = new Intent(DbActivity.this, MainScreenActivity.class);
                startActivity(i);
            }
        });

        buttonGetItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetItemAsyncTask getItemTask = new GetItemAsyncTask();
                /*get a random telephone */
                String ID = "1";
                textViewItem.setText("Busy getting contact: " + ID);


                Log.i(TAG, "Getting contact for telephone number: " + ID);
                getItemTask.execute(ID);
            }
        });

        Button buttonPutItem = findViewById(R.id.buttonPutItem);

        buttonPutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemAsyncTask addItemTask = new AddItemAsyncTask();

                Woda woda = new Woda("4","10","2019-02-12");
                textViewItem.setText("Adding contact: " + woda.getID());


                Log.i(TAG, "Adding contact with telephone number: " + woda.getID());


                Gson gson = new Gson();
                String json = gson.toJson(woda);

                /*convert json string to document*/
                Document doc = Document.fromJson(json);

                /*add contact to table*/
                addItemTask.execute(doc);
            }
        });
    }

    public void addDataToDB(String dataType, DBObject object, Context context) {

        DbActivity.this.attachBaseContext(context);

        AddItemAsyncTask addItemTask = new AddItemAsyncTask();
        /*get a random contact (name and telephone only)*/
        Log.i(TAG, "getting the passed object of type..." + dataType);

      /*  switch(dataType) {
            case "WODA":
                // code block
                break;
            case "USER_DATA":
                // code block
                break;
            default:
                Log.i(TAG, "Cos poszlo nie tak przy wybieraniu switch ");
        }*/

        Log.i(TAG, "Adding new object to database: " + object);

        Gson gson = new Gson();
        String json = gson.toJson(object);

        Document doc = Document.fromJson(json);

        addItemTask.execute(doc);

        Log.i(TAG, "Chyba dodano ?? Obiekt typu " + object);

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

                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewItem.setText("Error adding contact");
                        textViewItem.setBackgroundColor(Color.RED);
                    }
                });*/
            }
            return result;
        }

        @Override
        protected void onPostExecute(Document result) {
            super.onPostExecute(result);
            String message;

            if (result == null) {
                /*for ReturnValue.NONE*/
                //textViewItem.setText("Contact added");
                //textViewItem.setBackgroundColor(Color.GREEN);
                Log.i(TAG, "Contact added, no return data");
                message = "Contact added, no return data";
            } else {
                /*for ReturnValue.ALL_OLD*/
                //textViewItem.setText("Contact added");
                //textViewItem.setBackgroundColor(Color.YELLOW);
                Log.i(TAG, "Contact added, return data" + result);
                message = "Contact added, return data: " + result;
            }

            Log.i(TAG, "adding item result: " + message);
        }
    }



}