package za.co.a101apps.cognitologinapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputMeasuresActivity extends AppCompatActivity {

    private EditText wprowadzona_wartosc;
    private boolean valid = false;
    private Button wyslij;
    private Button powrot;
    private String type;
    private Intent come_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_measures);

        // zmienna type mowi czy to jest waga czy sen
        type = getIntent().getStringExtra("type");

        wprowadzona_wartosc = (EditText) findViewById(R.id.input_value);
        wyslij = (Button) findViewById(R.id.button_send);
        powrot = (Button) findViewById(R.id.button_back);

        come_back = new Intent(InputMeasuresActivity.this, MainScreenActivity.class);

        // jesli "powrot" - ok, wracamy

        powrot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(come_back);
            }
        });

        // jesli "wyslij"
        // tutaj to sie musi gdzies zapisac i wyslac i wrocic do glownego ekranu
        // switch ze zmienna "type"

        wyslij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (wprowadzona_wartosc.getText().toString().equals("")) {
                    valid = false;
                    Log.i("warunek",wprowadzona_wartosc.getText().toString());
                } else {
                    valid = true;
                    Log.i("warunek",wprowadzona_wartosc.getText().toString());
                }

                if(valid) {
                    switch (type) {
                        case "WAGA":
                            Log.i("dostalismy wartosc: ","waga");
                            startActivity(come_back);
                            break;
                        case "SEN":
                            Log.i("dostalismy wartosc: ","sen");
                            startActivity(come_back);
                            break;
                    }
                } else {
                    Toast.makeText(InputMeasuresActivity.this, "wprowadz dane", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
