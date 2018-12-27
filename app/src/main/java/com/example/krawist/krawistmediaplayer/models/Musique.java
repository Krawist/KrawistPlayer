package com.example.krawist.krawistmediaplayer.models;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class Musique implements Serializable {

    long musicId;
    long albumId;
    long artistId;
    String albumName;
    String artistName;
    String musicPath;
    String pochette;
    int musicDuration;
    String musicTitle;
    long musicTrack;
    long size;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getMusicTrack() {
        return musicTrack;
    }

    public void setMusicTrack(long musicTrack) {
        this.musicTrack = musicTrack;
    }

    public int getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(int musicDuration) {
        this.musicDuration = musicDuration;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public void setPochette(String pochette){
        this.pochette = pochette;
    }

    public String getPochette(){
        return this.pochette;
    }

    public long getMusicId() {
        return musicId;
    }

    public void setMusicId(long musicId) {
        this.musicId = musicId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
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

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String DurationToString(){
        long hour = TimeUnit.MILLISECONDS.toHours(this.musicDuration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(this.musicDuration)-(hour*60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(this.musicDuration)-(minutes*60);


        String minutesString = ""+minutes;
        String secondsString = ""+seconds;
        String hourString = ""+hour;

        if(minutes<10){
            minutesString = "0"+minutes;
        }
        if(seconds<10){
            secondsString = "0"+seconds;
        }
        if(hour>0 && hour <10){
            hourString = "0"+hour;
            return hourString+":"+minutesString+":"+secondsString;
        }else
            return minutesString+":"+secondsString;
    }


}
