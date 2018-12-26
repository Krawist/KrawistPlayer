package com.example.krawist.krawistmediaplayer.models;

public class Album {

    long albumId;
    String albumName;
    String artistName;
    String albumArt;
    long numberOfSong;

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public long getNumberOfSong() {
        return numberOfSong;
    }

    public void setNumberOfSong(long numberOfSong) {
        this.numberOfSong = numberOfSong;
    }
}
