package com.jd.musicplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import java.util.IllegalFormatCodePointException;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mp;
    SeekBar sb;
    int seekValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = new MediaPlayer();

        sb = (SeekBar) findViewById(R.id.media_seekBar);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                seekValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekValue);
            }
        });
        SeekThread seekThread = new SeekThread() ;
        seekThread.start();
    }

    public void playMedia(View view) {
        try {
            mp = new MediaPlayer();
            mp.setDataSource("http://server6.mp3quran.net/thubti/001.mp3");
            mp.prepare();
            sb.setMax(mp.getDuration());
            mp.start();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void pauseMedia(View view) {
        if (mp.isPlaying()) {
            mp.pause();
        }
    }

    public void stopMedia(View view) {
        if (mp.isPlaying()) {
            mp.release();
            mp = null;
        }
    }

    class SeekThread extends Thread {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mp!=null){
                            sb.setProgress(mp.getCurrentPosition());
                        }
                    }
                });
            }
        }
    }
}