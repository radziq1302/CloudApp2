package za.co.a101apps.cognitologinapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;

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
    private FrameLayout fab2; // dlatego jest tyle tych zmiennych
    private FrameLayout fab3;
    private TextView fab1_2;
    private TextView fab2_2;
    private TextView fab3_2;


    private boolean nie_istnieje = false;
    private boolean isFABOpen = false;

    private int wyliczone_max_woda = 2500;
    private int wyliczone_max_kroki = 10000;


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
        fab2 = (FrameLayout) findViewById(R.id.fab2);
        fab3 = (FrameLayout) findViewById(R.id.fab3);
        fab1_2 = (TextView) findViewById(R.id.fab1_2);
        fab2_2 = (TextView) findViewById(R.id.fab2_2);
        fab3_2 = (TextView) findViewById(R.id.fab3_2);

        final CognitoSettings cognitoSettings = new CognitoSettings(this);
        CognitoUser currentUser = cognitoSettings.getUserPool().getCurrentUser();

        nazwa_uzytkownika.setText(currentUser.getUserId());

        progres_woda.setMax(wyliczone_max_woda);
        progres_kroki.setMax(wyliczone_max_kroki);

        aktualna_woda.setText(Integer.toString(progres_woda.getProgress()) + " / " + Integer.toString(wyliczone_max_woda));
        aktualne_kroki.setText(Integer.toString(progres_kroki.getProgress()) + " / " + Integer.toString(wyliczone_max_kroki));


        // floating action button
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


        porada.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(MainScreenActivity.this, "Kup wersję Premium!", Toast.LENGTH_SHORT).show();
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
        fab2_2.setVisibility(View.VISIBLE);
        fab3_2.setVisibility(View.VISIBLE);
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1_2.setVisibility(View.VISIBLE);
        fab2_2.setVisibility(View.VISIBLE);
        fab3_2.setVisibility(View.VISIBLE);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
    }


}
