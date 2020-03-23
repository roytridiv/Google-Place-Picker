package com.example.google_place_picker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends AppCompatActivity {
    GoogleApiClient mGoogleApiClient ;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.bar);


        progressBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));

        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(3000);
                } catch (Exception ignored) {
                } finally {

                    Intent intent = new Intent(MainActivity.this, MyPlacePicker.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.isConnected();
        }
    }

    protected void onResume() {

        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.isConnected();
        }
    }


    }

