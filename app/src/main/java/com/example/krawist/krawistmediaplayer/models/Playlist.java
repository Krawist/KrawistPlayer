package com.example.krawist.krawistmediaplayer.models;

public class Playlist {

    private String name;
    private long id;
    private long numberOfSongs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(long numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }
}
