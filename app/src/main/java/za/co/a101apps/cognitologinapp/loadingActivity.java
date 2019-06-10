package za.co.a101apps.cognitologinapp;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;

public class loadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        final Context context = getApplicationContext();
        CognitoSettings cognitoSettings = new CognitoSettings(context);
        final CognitoUser currentUser = cognitoSettings.getUserPool().getCurrentUser();
        Log.v("czyZalogowan",currentUser.getUserId());
        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);

                    //jeżeli już ktoś zalogowany to do MainActivity a jesli nie to do log_in, może zmienna boolean po zapamiętaniu logowania Tickbox
                    //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    Intent intent;
                    if(!currentUser.getUserId().isEmpty())
                    {intent = new Intent(getApplicationContext(),MainScreenActivity.class);
                    startActivity(intent);}
                    else {
                        intent=new Intent(getApplicationContext(),Menu.class);
                        startActivity(intent);
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


    }

}
