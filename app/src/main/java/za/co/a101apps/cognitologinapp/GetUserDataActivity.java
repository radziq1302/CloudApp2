package za.co.a101apps.cognitologinapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

public class GetUserDataActivity extends AppCompatActivity {

    private static final String TAG = "Cognito";

    private Button wyslij;
    private TextInputEditText wzrost;
    private TextInputEditText waga;
    private TextInputEditText wiek;
    private Spinner plec; //gender_spinner
    private Spinner aktywnosc; //activity_spinner
    private String username;

    private boolean valid = true;
    PopupWindow popUp;

    private CognitoCachingCredentialsProvider credentialsProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_data);

        wyslij = (Button) findViewById(R.id.button_send);
        wzrost = (TextInputEditText) findViewById(R.id.textInputEdit1);
        waga = (TextInputEditText) findViewById(R.id.textInputEdit2);
        wiek =(TextInputEditText) findViewById(R.id.textInputEdit3);
        plec = (Spinner) findViewById(R.id.gender_spinner);
        aktywnosc = (Spinner) findViewById(R.id.activity_spinner);

        final CognitoSettings cognitoSettings = new CognitoSettings(this);
        CognitoUser currentUser = cognitoSettings.getUserPool().getCurrentUser();
        credentialsProvider = cognitoSettings.getCredentialsProvider();
        username = currentUser.getUserId();

        wyslij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wzrost.getText().toString().equals("") || waga.getText().toString().equals("") || wiek.getText().toString().equals("")) {
                    valid = false;
                    Log.i("warunek","true");
                } else {
                    valid = true;
                    Log.i("warunek","false");
                }

                if(valid) {
                    Log.i("hi","jest ok");
                    // Log.i("nowy wzrost : ", wzrost.getText().toString());
                    DBUserData userdata = new DBUserData("10", username.toString(), waga.getText().toString(), wzrost.getText().toString(), wiek.getText().toString(),
                            plec.getSelectedItem().toString(),aktywnosc.getSelectedItem().toString());
                    //DbActivity dba = new DbActivity();
                    Context context = GetUserDataActivity.this;
                    //context=GetUserDataActivity.this;
                    //dba.addDataToDB("User data", userdata, context);
                    Log.v("kurwaaaa",userdata.getID());
                    Intent validDataSent = new Intent(GetUserDataActivity.this, DbActivity.class);
                    validDataSent.putExtra("daneFormularza",userdata);
                    validDataSent.putExtra("idZasrane",userdata.getID());
                    startActivity(validDataSent);
                }
                else {
                    Toast.makeText(GetUserDataActivity.this, "wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()... clearing credentials provider");
        /*clear the cached/saved credentials so we don't use them for guest user if not logged in*/
        credentialsProvider.clear();
    }

}