package com.example.cloudapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import za.co.a101apps.cognitologinapp.DbActivity;
import za.co.a101apps.cognitologinapp.ForgotPasswordActivity;
import za.co.a101apps.cognitologinapp.GetConfirmationCodeActivity;
import za.co.a101apps.cognitologinapp.GetUserDetailsActivity;
import za.co.a101apps.cognitologinapp.LogoutActivity;
import za.co.a101apps.cognitologinapp.R;
import za.co.a101apps.cognitologinapp.ShowMeActivity1;


public class MainWindowActivity extends AppCompatActivity implements View.OnClickListener {


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
        }
    }

}
