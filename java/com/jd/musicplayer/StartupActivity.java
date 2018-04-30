package com.example.muneeb.simplemusicplayer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartupActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        //Here, we are going to use handler to process Runnable Objects.
        //Handler allows to send and process Msg and Runnable Objects.
        //postDelayed() Causes the Runnable to be added to the msg queue, to be run for specified time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(StartupActivity.this, MainActivity.class); //Opening a new class
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
