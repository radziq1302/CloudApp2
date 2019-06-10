package za.co.a101apps.cognitologinapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;


public class MainWindowActivity extends AppCompatActivity implements View.OnClickListener {
    CognitoUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);

        Button buttonGetTokens = findViewById(R.id.buttonGetTokens);
        buttonGetTokens.setOnClickListener(this);

        Button buttonGetUserDetails = findViewById(R.id.buttonGetDetails);
        buttonGetUserDetails.setOnClickListener(this);

        Button buttonShowMe = findViewById(R.id.buttonShowMe);
        buttonShowMe.setOnClickListener(this);

        Button buttonNew = findViewById(R.id.buttonNew);
        buttonNew.setOnClickListener(this);

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);
        final Context context = getApplicationContext();
        CognitoSettings cognitoSettings = new CognitoSettings(context);
        currentUser = cognitoSettings.getUserPool().getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonGetDetails:
                Intent intentGetUserDetails = new Intent(this, GetUserDetailsActivity.class);
                startActivity(intentGetUserDetails);
                break;
            case R.id.buttonShowMe:
                Intent intentShowMe = new Intent(this, ShowMeActivity1.class);
                startActivity(intentShowMe);
                break;

            case R.id.buttonGetTokens:
                Intent intentGetTokens = new Intent(this, DbActivity.class);//GetTokensActivity.class);
                startActivity(intentGetTokens);
                break;

            case R.id.buttonNew:
                Log.i("Tutaj", "Adding new object to database:  ?????? ");
                DBUserData userdata = new DBUserData("7", "Kasia", "123", "123", "123", "K", "N");
                //DbActivity dba = new DbActivity();
                Context context = MainWindowActivity.this;
                //dba.addDataToDB("User data", userdata, context);
                Log.i("Tutaj", "Dodano  ?????? ");
                break;
            case R.id.buttonLogout:
                Log.i("Przed", currentUser.getUserId()+"");
                currentUser.signOut();
                GenericHandler handler = new GenericHandler() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }
                };
                AuthenticationHandler aHandler = new AuthenticationHandler() {
                    @Override
                    public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {

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
                };
                currentUser.globalSignOutInBackground(handler);
                currentUser.globalSignOut(handler);
                currentUser.getSession(aHandler);

                Log.i("Po", currentUser.getUserId()+"");
                Intent i = new Intent(this, LogoutActivity.class);
                startActivity(i);
        }
    }

}
