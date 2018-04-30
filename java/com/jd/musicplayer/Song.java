package com.jd.musicplayer;

public class Song {
    private long ID;
    private String title;
    private String artist;

    public Song(long songID, String songTitle, String songArtist){
        ID = songID;
        title = songTitle;
        artist = songArtist;
    }

    public long getID(){ return ID; }

    public String getTitle(){ return title; }

    public String getArtist(){ return artist; }
}
