package com.example.muneeb.simplemusicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.muneeb.simplemusicplayer.MusicService.MusicBinder;

public class MainActivity extends Activity implements MediaPlayerControl {

    private ArrayList<Song> songArrayList;
    private ListView songListView;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;

    private boolean paused = false ;
    private boolean playbackPaused = false;

    private MusicController musicController;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Because, the version updates
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]
                                {Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

        songListView = (ListView) findViewById(R.id.lv_songList);
        songArrayList = new ArrayList<Song>();

        //Function calling here
        GetSongList();

        //Sorting the songs by title, alphabetically
        Collections.sort(songArrayList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        SongAdapter songAdapter = new SongAdapter(this, songArrayList);
        songListView.setAdapter(songAdapter);

        //Function Calling here
        setController();
    }

    //Connecting with the Service
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder musicBinder = (MusicBinder) service;

            musicService = musicBinder.getService(); //getting the Service

            musicService.SetList(songArrayList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent,
                    musicConnection,
                    Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        if (musicBound) unbindService(musicConnection);
        stopService(playIntent);
        musicService = null;
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Selecting the Menu Items
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicService.setShuffle();
                break;

            case R.id.action_end:
                stopService(playIntent);
                musicService = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Retrieving all the data of the songs in the form of list
    public void GetSongList() {
        //The ContentResolver object communicates with the provider object, an instance of a class that implements ContentProvider.
        ContentResolver musicResolver = getContentResolver();
        //Unified Resource Identifier is a string of characters used to identify a resource either by location or name or both.
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //Cursor provides random read-write access to the result set returned by a DB query.
        Cursor musicCursor = musicResolver.query(musicUri,
                null,
                null,
                null,
                null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //getting Columns
            //getting title Column
            int titleColumn = musicCursor.
                    getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            //getting Artist Column
            int artistColumn = musicCursor.
                    getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            //getting ID Column
            int idColumn = musicCursor.
                    getColumnIndex(android.provider.MediaStore.Audio.Media._ID);

            //Adding All Songs to List
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);

                songArrayList.add(new Song(thisId, thisTitle, thisArtist));
            } while (musicCursor.moveToNext());
        }
    }

    public void SongPicked(View view) {
        musicService.SetSong(Integer.parseInt(view.getTag().toString()));
        musicService.PlaySong();

        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        musicController.show(0);
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicService.pausePlayer();
    }

    @Override
    protected void onStop() {
        musicController.hide();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (paused){
            setController();
            paused = false;
        }
    }

    @Override
    public int getDuration() {
        if (musicService != null && musicBound && musicService.isPlaying()) return musicService.getDuration();

        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicService != null && musicBound && musicService.isPlaying()) return musicService.getSongPosition();

        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicService.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        if (musicService != null && musicBound) return musicService.isPlaying();

        else return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void setController() {
        //Setting up the Music Controller
        musicController = new MusicController(this);

        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        musicController.setMediaPlayer(this);
        musicController.setAnchorView(findViewById(R.id.lv_songList));
        musicController.setEnabled(true);
    }

    public void playNext() {
        musicService.playNext();

        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        musicController.show(0);
    }

    public void playPrev() {
        musicService.playPrev();

        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        musicController.show(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
