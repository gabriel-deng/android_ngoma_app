package com.example.ngomapp.Models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Playlist {
 @Id
 public  long id;
 String name, created_at;
 int tracks= 0;

    public int getTracks() {
        return tracks;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public Playlist() {
    }

    public Playlist(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
