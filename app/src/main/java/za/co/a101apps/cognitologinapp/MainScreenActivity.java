package za.co.a101apps.cognitologinapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import za.co.a101apps.cognitologinapp.R;

public class MainScreenActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 0;
    private ImageView imageView;
    private Button pomiar;
    private Button wykresy;
    private Button dieta;
    private Button galeria;
    private ProgressBar kroki;
    private ProgressBar woda;
    private TextView nazwa_uzytkownika;
    private TextView max_woda;
    private TextView max_kroki;
    private TextView wartosc_waga;
    private TextView wartosc_sen;

    private boolean nie_istnieje = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        imageView = (ImageView) findViewById(android.R.id.icon);
        pomiar = (Button) findViewById(R.id.measure_button);
        wykresy = (Button) findViewById(R.id.plot_button);
        dieta = (Button) findViewById(R.id.diet_button);
        galeria = (Button) findViewById(R.id.gallery_button);
        kroki = (ProgressBar) findViewById(R.id.steps_progress);
        woda = (ProgressBar) findViewById(R.id.water_progress);
        nazwa_uzytkownika = (TextView) findViewById(R.id.username);
        max_woda = (TextView) findViewById(R.id.water_max_value);
        max_kroki = (TextView) findViewById(R.id.steps_max_value);
        wartosc_waga = (TextView) findViewById(R.id.weight_value);
        wartosc_sen = (TextView) findViewById(R.id.sleep_value);


        if(nie_istnieje) {

            imageView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(i,SELECT_PICTURE);
                }
            });


        } else {

            // Bitmap bitmap;

           // Drawable d = new BitmapDrawable(getResources(), );

            Drawable drawable = this.getResources().getDrawable(R.drawable.rys_2);

            imageView.setImageDrawable(drawable);


        }

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

}
