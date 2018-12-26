package com.example.krawist.krawistmediaplayer.models;

public class Artist {
    long artistId;
    String artistName;
    long numberOfSong;
    long numberOfAlbums;

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getNumberOfSong() {
        return numberOfSong;
    }

    public void setNumberOfSong(long numberOfSong) {
        this.numberOfSong = numberOfSong;
    }

    public long getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public void setNumberOfAlbums(long numberOfAlbums) {
        this.numberOfAlbums = numberOfAlbums;
    }
}
