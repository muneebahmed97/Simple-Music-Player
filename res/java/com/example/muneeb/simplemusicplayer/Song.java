package com.example.muneeb.simplemusicplayer;

public class Song {
    private long Id;
    private String title;
    private String artist;

    public Song(long songId, String songTitle, String songArtist){
        Id = songId;
        title = songTitle;
        artist = songArtist;
    }

    public long getId(){ return Id; }

    public String getTitle(){ return title; }

    public String getArtist(){ return artist; }
}
