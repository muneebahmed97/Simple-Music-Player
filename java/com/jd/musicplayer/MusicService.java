package com.example.muneeb.simplemusicplayer;

import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {
    @Nullable

    //for the Media Player
    private MediaPlayer mediaPlayer;
    //for the List of the songs
    private ArrayList<Song> songs;
    //for the Current Position of the song
    private int songPosition;

    private String songTitle = "";
    private static final int NOTIFY_ID = 1;

    private boolean shuffle = false;
    private Random rand;

    AudioManager audioManager;

    private final IBinder musicBind = new MusicBinder();

    public void onCreate(){
        //creating the service
        super.onCreate();

        songPosition = 0; //initialize value of the song
        mediaPlayer = new MediaPlayer(); //creating player

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        initMusicPlayer();

        rand = new Random();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE) ;
    }

    public void initMusicPlayer(){
        //Setting the properties of the player
        mediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK); //Music continues to play in the playback while the screen goes idle
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //auto generated method
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Stopping the Service
        mediaPlayer.stop();
        mediaPlayer.release();

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaPlayer.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //auto generated methods
        mp.start(); //starting the Playback

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendIntent = PendingIntent.getActivity(this,
                0,
                notIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendIntent)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle) ;

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        }

        startForeground(NOTIFY_ID, notification);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void SetSong(int songIndex) {
        songPosition = songIndex; //in order for the user to select songs
    }

    public void SetList(ArrayList<Song> theSongs){
        songs = theSongs;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void PlaySong(){
        //playing a Song
        mediaPlayer.reset(); //Playing from the start of the song

        //getting the song to play
        Song playSong = songs.get(songPosition);

        songTitle = playSong.getTitle();

        //getting the current song
        long currentSong = playSong.getId();

        //Setting the Uri
        Uri trackUri = ContentUris.withAppendedId(MediaStore.
                        Audio.
                        Media.
                        EXTERNAL_CONTENT_URI,
                currentSong);

        //Trying to set trackUri as the DataSource
        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception exp) {
            Log.e("MUSIC SERVICE", "Error Setting Data Source", exp);
        }

        mediaPlayer.prepareAsync(); //preparing Media Player with Asynchronous Method
    }

    public int getSongPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer() {
        mediaPlayer.pause();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public void go() {
        mediaPlayer.start();
    }

    public void playPrev() {
        songPosition--;

        if (songPosition < 0) songPosition = songs.size() - 1;

        PlaySong();
    }

    public void playNext() {
        if (shuffle) {
            int newSong = songPosition;
            while (newSong == songPosition) {
                newSong = rand.nextInt(songs.size());
            }
            songPosition = newSong;
        }
        else
        {
            songPosition++;
            if (songPosition >= songs.size()) songPosition = 0;
        }
        PlaySong();
    }

    public void setShuffle() {
        if (shuffle) shuffle = false;

        else shuffle = true;
    }
}
