package com.example.muneeb.simplemusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInfo; //using LayoutInflater to map the title and artist names of the songs to the TextView

    public SongAdapter(Context context, ArrayList<Song> theSongs){
        songs = theSongs;
        songInfo = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        //auto generated method, getting the size of the songs
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        //auto generated method
        return null;
    }

    @Override
    public long getItemId(int position) {
        //auto generated method
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //auto generated method
        //mapping to song layout
        LinearLayout songLayout = (LinearLayout) songInfo.
                inflate(R.layout.song, parent, false);

        //getting title and artist in TextViews
        TextView tvSongTitle = (TextView) songLayout.findViewById(R.id.tv_songTitle);
        TextView tvSongArtist = (TextView) songLayout.findViewById(R.id.tv_songArtist);

        //getting the position of the current song playing
        Song currentSong = songs.get(position);

        //getting Title and Artist String
        tvSongTitle.setText(currentSong.getTitle());
        tvSongArtist.setText(currentSong.getArtist());

        //setting the position as tag
        songLayout.setTag(position);
        return songLayout;
    }
}
