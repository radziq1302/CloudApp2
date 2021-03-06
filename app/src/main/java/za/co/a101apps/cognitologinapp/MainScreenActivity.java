package za.co.a101apps.cognitologinapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainScreenActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 0;
    private ImageView imageView;
    private Button pomiar;
    private Button wykresy;
    private Button porada;
    private Button galeria;
    private ProgressBar progres_kroki;
    private ProgressBar progres_woda;
    private TextView nazwa_uzytkownika;
    private TextView aktualna_woda;
    private TextView aktualne_kroki;
    private TextView wartosc_waga;
    private TextView wartosc_sen;

    private FloatingActionButton fab; // floating button glowny
    private FrameLayout fab1; // kazdy pomniejszy sklada sie z frameLayout + text view, ktory musialam schowac zanim sie otworzy
    private TextView fab1_2;


    private boolean nie_istnieje = false;
    private boolean isFABOpen = false;

    private int wyliczone_max_woda = 2500;
    private int wyliczone_max_kroki = 10000;

    private String wprowadzony_sen;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        imageView = (ImageView) findViewById(android.R.id.icon);
        wykresy = (Button) findViewById(R.id.plot_button);
        porada = (Button) findViewById(R.id.diet_tip_button);
        galeria = (Button) findViewById(R.id.gallery_button);
        progres_kroki = (ProgressBar) findViewById(R.id.steps_progress);
        progres_woda = (ProgressBar) findViewById(R.id.water_progress);
        nazwa_uzytkownika = (TextView) findViewById(R.id.username);
        aktualna_woda = (TextView) findViewById(R.id.water_current_value);
        aktualne_kroki = (TextView) findViewById(R.id.steps_current_value);
        wartosc_waga = (TextView) findViewById(R.id.weight_value);
        wartosc_sen = (TextView) findViewById(R.id.sleep_value);

        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab1 = (FrameLayout) findViewById(R.id.fab1);
        fab1_2 = (TextView) findViewById(R.id.fab1_2);



        final CognitoSettings cognitoSettings = new CognitoSettings(this);
        final CognitoUser currentUser = cognitoSettings.getUserPool().getCurrentUser();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        nazwa_uzytkownika.setText(currentUser.getUserId());
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final String currentDateandTime = sdf.format(new Date());
        MainScreenActivity.GetItemAsyncTask getItemTask = new MainScreenActivity.GetItemAsyncTask();
        String ID = currentDateandTime+"-"+currentUser.getUserId();
        getItemTask.execute(ID);
        // ustawienie poczatkowych wartosci progressu - na razie zawsze od 0

        progres_woda.setMax(wyliczone_max_woda);
        progres_kroki.setMax(wyliczone_max_kroki);

        aktualna_woda.setText(Integer.toString(progres_woda.getProgress()) + " / " + Integer.toString(wyliczone_max_woda));
        aktualne_kroki.setText(Integer.toString(progres_kroki.getProgress()) + " / " + Integer.toString(wyliczone_max_kroki));


        wprowadzony_sen = getIntent().getStringExtra("sen");

        if(wprowadzony_sen != null) {

            wartosc_sen.setText(wprowadzony_sen + " h");
        }

        // floating action button pokazywanie i chowanie
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });


        // to sie dzieje kiedy klikniemy w ktorys z tych wyjezdzajacych floating buttonow

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("kupa",currentUser.getUserId()+"");
                Intent getMeasures = new Intent(MainScreenActivity.this, InputMeasuresActivity.class);
                getMeasures.putExtra("type","WAGA");
                getMeasures.putExtra("idUser",currentUser.getUserId());
                getMeasures.putExtra("woda",Integer.toString(progres_woda.getProgress()));
                getMeasures.putExtra("kroki",Integer.toString(progres_kroki.getProgress()));
                getMeasures.putExtra("idUser",currentUser.getUserId());

                startActivity(getMeasures);

            }
        });



        // reaktywnosc paskow progresu - w sumie gotowe tylko ze te dane sie nigdzie nie zapisuja

        progres_kroki.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                progres_kroki.setProgress(progres_kroki.getProgress()+500);
                aktualne_kroki.setText(Integer.toString(progres_kroki.getProgress()) + " / " + Integer.toString(wyliczone_max_kroki));
            }
        });


        progres_woda.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                progres_woda.setProgress(progres_woda.getProgress()+250);
                aktualna_woda.setText(Integer.toString(progres_woda.getProgress()) + " / " + Integer.toString(wyliczone_max_woda));
            }
        });

        // foteczka
        // nie_istnieje jest na sztywno wpisane i nic nie sprawdza czy uzytkownik juz ma zdjecie czy nie

        if(nie_istnieje) {

            imageView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(i,SELECT_PICTURE);
                }
            });

        } else {

            Drawable drawable = this.getResources().getDrawable(R.drawable.rys_2);
            imageView.setImageDrawable(drawable);
        }

        // nasze ulubione - skonczone xd

        porada.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(MainScreenActivity.this, "Kup wersję Premium!", Toast.LENGTH_SHORT).show();
            }
        });


        wykresy.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent wykresyOkienko = new Intent(MainScreenActivity.this, StatsActivity.class);
                wykresyOkienko.putExtra("idUser",currentUser.getUserId());
                startActivity(wykresyOkienko);
            }
        });


        galeria.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent galeriaOkienko = new Intent(MainScreenActivity.this, ShowMeActivity1.class);
                galeriaOkienko.putExtra("idUser",currentUser.getUserId());
                startActivity(galeriaOkienko);
            }
        });


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = getPath(data.getData());
            imageView.setImageBitmap(bitmap);
        }
    }



    private Bitmap getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        // cursor.close();
        // Convert file path into bitmap image using below line.
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        return bitmap;
    }

    private void selectImage( View v) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }


    private void showFABMenu(){
        isFABOpen=true;
        fab1_2.setVisibility(View.VISIBLE);
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));


    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1_2.setVisibility(View.INVISIBLE);
        fab1.animate().translationY(0);
    }

    private class GetItemAsyncTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... id) {

            Document document = null;

            Log.i("z bazy", "in GetItemAsyncTask doInBackground....");
            DbAccess databaseAccess = DbAccess.getInstance(MainScreenActivity.this);
            try {
                Log.i("z bazy", "getting data: " + id[0]);
                document = databaseAccess.getItem(id[0]);
            }
            catch (Exception e) {
                Log.i("z bazy", "error getting data: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*update text views on main thread*/
                        wartosc_waga.setText("Error getting data");
                        wartosc_waga.setBackgroundColor(Color.RED);
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
                String waga_z_bazy = String.valueOf(document.get("waga").asString());
                String kroki_z_bazy = String.valueOf(document.get("kroki").asString());
                String woda_z_bazy = String.valueOf(document.get("woda").asString());
                String sen_z_bazy = String.valueOf(document.get("sen").asString());

//waga tutaj
                try {
                    String jsonDocument = Document.toJson(document);
                    Log.i("json z bazy", "Contact: " + jsonDocument);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("json z bazy", "error in GetItemAsyncTask show contact as json: " + e.getLocalizedMessage());
                }

                if (waga_z_bazy != null && woda_z_bazy != null && kroki_z_bazy != null ) {
                    wartosc_waga.setText(waga_z_bazy + " kg");
                    wartosc_sen.setText(sen_z_bazy + " h");

                    progres_woda.setProgress(Integer.parseInt(woda_z_bazy));
                    aktualna_woda.setText(woda_z_bazy + " / " + Integer.toString(wyliczone_max_woda));

                    progres_kroki.setProgress(Integer.parseInt(kroki_z_bazy));
                    aktualne_kroki.setText(kroki_z_bazy + " / " + Integer.toString(wyliczone_max_woda));


                } else {
                    wartosc_waga.setText("data not found");
                    wartosc_waga.setBackgroundColor(Color.YELLOW);
                }
            } else {
                wartosc_waga.setText("data not found");
                wartosc_waga.setBackgroundColor(Color.YELLOW);
            }
        }
    }

 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.wyloguj:
                Intent intent = new Intent(this, LogoutActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


}
