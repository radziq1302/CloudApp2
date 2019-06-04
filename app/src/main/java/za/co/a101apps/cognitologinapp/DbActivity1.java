package za.co.a101apps.cognitologinapp;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DbActivity1 extends AppCompatActivity {

    private String TAG = "DynamoDb_Demo";
    private TextView textViewItem;
    public CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSettings cognitoSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        cognitoSettings = new CognitoSettings(this);
        credentialsProvider = cognitoSettings.getCredentialsProvider();
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
                sprawdz();

            }
        });
    }
    private void sprawdz(){

        CognitoUser currentUser = cognitoSettings.getUserPool().getCurrentUser();
        currentUser.getSessionInBackground(new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                if (userSession.isValid()) {
                    String idToken = userSession.getIdToken().getJWTToken();
                    if (idToken.length() > 0) {
                        // Set up as a credentials provider.
                        Log.i(TAG, "got id token - setting credentials using token");
                        Map<String, String> logins = new HashMap<>();
                        logins.put("cognito-idp.us-east-2.amazonaws.com/us-east-2_JbhhbQMsj", idToken);
                        credentialsProvider.setLogins(logins);

                        Log.i(TAG, "using credentials for the logged in user");

                        /*refresh provider off main thread*/
                        Log.i(TAG, "refreshing credentials provider in asynctask..");
                        new DbActivity1.RefreshAsyncTask().execute();

                    } else {
                        Log.i(TAG, "no token...");
                    }
                } else {
                    Log.i(TAG, "user session not valid - using identity pool credentials - guest user");
                }

                AddItemAsyncTask addItemTask = new AddItemAsyncTask();
                Log.i(TAG, "getting random contact...");
                Woda woda = new Woda("4","10","2019-02-12");
                textViewItem.setText("Adding contact: " + woda.getID());
                Log.i(TAG, "Adding contact with telephone number: " + woda.getID());
                Gson gson = new Gson();
                String json = gson.toJson(woda);
                Document doc = Document.fromJson(json);
                addItemTask.execute(doc);

            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {

            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {

            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {

            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
    }
    private class RefreshAsyncTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            Log.i(TAG, "in asynctask doInBackground()");
            credentialsProvider.refresh();
            return integers[0];
        }
    }
    public void addDataToDB(String dataType, DBObject object, Context context) {

        DbActivity1.this.attachBaseContext(context);

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
            DbAccess1 databaseAccess = DbAccess1.getInstance(DbActivity1.this,credentialsProvider);
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
            DbAccess1 databaseAccess = DbAccess1.getInstance(DbActivity1.this,credentialsProvider);
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