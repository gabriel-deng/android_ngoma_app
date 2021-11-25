package com.example.ngomapp.Models;

import android.widget.TextView;


public class Song {
    String name, data, album, artist;

    boolean isPlaying= false;

    public Song(String name, String data, String album, String artist) {
        this.name = name;
        this.data = data;
        this.album = album;
        this.artist = artist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Song(String name) {
        this.name = name;
    }
}
