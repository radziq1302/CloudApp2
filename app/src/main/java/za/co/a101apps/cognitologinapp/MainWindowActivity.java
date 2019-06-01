package za.co.a101apps.cognitologinapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


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

        Button buttonNew = findViewById(R.id.buttonNew);
        buttonNew.setOnClickListener(this);
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
                DbActivity dba = new DbActivity();
                Context context = MainWindowActivity.this;
                dba.addDataToDB("User data", userdata, context);
                Log.i("Tutaj", "Dodano  ?????? ");
        }
    }

}
