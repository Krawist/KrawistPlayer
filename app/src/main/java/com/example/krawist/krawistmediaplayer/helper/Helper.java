package com.example.krawist.krawistmediaplayer.helper;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.krawist.krawistmediaplayer.models.Album;
import com.example.krawist.krawistmediaplayer.models.Artist;
import com.example.krawist.krawistmediaplayer.models.Musique;

import java.util.ArrayList;

public class Helper {

    public static final String INTENT_ALBUM_ID_KEY="com.example.krawist.krawistmediaplayer.idkey";

    public static final String INTENT_SONG_ID_KEY="com.example.krawist.krawistmediaplayer.idkey";

    public static final String DETAIL_ALBUM_FRAGMENT = "com.example.krawist.krawistmediaplayer.fragment";

    public static final String PLAYING_MUSIC_LIST= "com.example.krawist.krawistmediaplayer.songs";

    public static final String PLAYING_SONG = "com.example.krawist.krawistmediaplayer.song";

    public static final String PLAYING_MUSIC_LIST_ID_KEY = "com.example.krawist.krawistmediaplayer.albumid";

    public static final String DEFAULT_ALBUM_ART_STRING = "default_album_art";

    public static ArrayList<Musique> getAllMusique(Context context){

        ArrayList<Musique> listOfSong = new ArrayList<>();

        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.SIZE};

        String selection = MediaStore.Audio.Media.IS_MUSIC + " = ?";
        String[] selectionArgs = {1+""};


        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,MediaStore.Audio.Media.TITLE);

        while(cursor.moveToNext()){
            listOfSong.add(matchCursorLineToMusic(context,cursor));
        }
        cursor.close();

        return listOfSong;
    }

    public static ArrayList<Album> getAllAlbum(Context context){
        ArrayList<Album> listOfAlbum = new ArrayList<>();

        String[] projection = {MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS};


        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,projection,null,null,MediaStore.Audio.Albums.ALBUM);
        while(cursor.moveToNext()){
            //Log.e("Krawist",""+cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)));
            listOfAlbum.add(matchCursorLineToAlbum(context,cursor));
        }

        cursor.close();

        return listOfAlbum;
    }

    public static ArrayList<Musique> getAllAlbumSongs(Context context, long albumId){
        ArrayList<Musique> listOfMusique = new ArrayList<>();

        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.SIZE};

        String selection = MediaStore.Audio.Media.ALBUM_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(albumId)};


        listOfMusique.add(new Musique());
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,MediaStore.Audio.Media.TRACK);
        while(cursor.moveToNext()){

            listOfMusique.add(matchCursorLineToMusic(context,cursor));
        }

        cursor.close();

        return listOfMusique;
    }

    public static Musique matchCursorLineToMusic(Context context,Cursor cursor){
        Musique musique = new Musique();

        musique.setMusicId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        musique.setAlbumId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
        musique.setAlbumName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
        musique.setArtistId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)));
        musique.setMusicPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        musique.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        musique.setMusicTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        musique.setMusicDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        musique.setMusicTrack(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)));
        musique.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
        musique.setPochette(Helper.DEFAULT_ALBUM_ART_STRING);

        String[] projection = {MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums._ID};
        String[] selectionArgs = {musique.getAlbumId()+""};
        //Log.e("Krawist"," Album Id "+musique.getAlbumId());
        String selection = MediaStore.Audio.Albums._ID + " = ?";
        Cursor anotherCursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,null);
        if(anotherCursor.moveToFirst()){
            if(anotherCursor.getString(anotherCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))==null){
                musique.setPochette(Helper.DEFAULT_ALBUM_ART_STRING);
            }else{
                musique.setPochette(anotherCursor.getString(anotherCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
            }

        }

        return musique;
    }

    public static ArrayList<Musique> updateMusic(Context context, ArrayList<Musique> listOfSong){

        for(Musique musique: listOfSong){
            String[] projection = {MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums._ID};
            String[] selectionArgs = {musique.getAlbumId()+""};
            //Log.e("Krawist"," Album Id "+musique.getAlbumId());
            String selection = MediaStore.Audio.Albums._ID + " = ?";
            Cursor anotherCursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,null);
            if(anotherCursor.moveToFirst()){
                if(anotherCursor.getString(anotherCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))==null){
                    musique.setPochette(Helper.DEFAULT_ALBUM_ART_STRING);
                }else{
                    musique.setPochette(anotherCursor.getString(anotherCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                }

            }
        }

        return listOfSong;
    }

    /*public void get(Context context){
        context.getContentResolver().query(MediaStore.Audio.)
    }*/

    public static Album matchCursorLineToAlbum(Context context,Cursor cursor){
        Album album = new Album();

        String albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
        if(albumArt==null){
            album.setAlbumArt(Helper.DEFAULT_ALBUM_ART_STRING);
        }else{
            album.setAlbumArt(albumArt);
        }
        album.setAlbumId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
        album.setAlbumName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
        album.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
        album.setNumberOfSong(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));

        return album;
    }

    public static Album getSpecificAlbum(Context context, long albumId){
        Album album = new Album();

        String[] projection = {MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS};

        String selection = MediaStore.Audio.Albums._ID + " = ? ";
        String[] selectionArgs = {String.valueOf(albumId)};


        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,MediaStore.Audio.Albums.ALBUM);
        if(cursor.moveToFirst()){
            //Log.e("Krawist",""+cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)));
            album = matchCursorLineToAlbum(context,cursor);
        }

        cursor.close();

        return album;
    }

    public static  ArrayList<Artist> getAllArtist(Context context){

        ArrayList<Artist> listOfArtist = new ArrayList<>();

        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,projection, null, null,MediaStore.Audio.Artists.ARTIST);

        while (cursor.moveToNext()){
            listOfArtist.add(matchCursorLineToArtist(cursor));
        }

        cursor.close();
        return listOfArtist;
    }

    public static Artist matchCursorLineToArtist(Cursor cursor){
        Artist artist = new Artist();

        artist.setArtistId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
        artist.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
        artist.setNumberOfAlbums(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
        artist.setNumberOfSong(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));

        return artist;
    }

    public static Musique getSpecificSong(Context context, long songId){
        Musique musique = new Musique();

        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.SIZE};

        String selection = MediaStore.Audio.Media._ID + " = ? ";
        String[] selectionArgs = {String.valueOf(songId)};


        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,null);
        if(cursor.moveToFirst()){
            //Log.e("Krawist",""+cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)));
            musique= matchCursorLineToMusic(context,cursor);
        }

        cursor.close();

        return musique;
    }

    public static Bitmap decodeSampleBitmap(String path, int reqWidth, int reqHeight){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path,options);

        options.inSampleSize = calculateInSampleSize(options,reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path,options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if(options.outHeight>reqHeight || options.outWidth>reqWidth){
            final int halfHeight = height/2;
            final int halfWidth = width/2;

            while (((halfHeight / inSampleSize) >= reqHeight) && ((halfWidth / inSampleSize) >= reqWidth) ){
                inSampleSize *=2;
            }
        }

        return inSampleSize;
    }

    public static void deleteSpecificSong(Context context,Long... ids){
        String selection = "";
        String[] selectionArgs = new String[ids.length];
        if(ids.length>0){
            for(int i=0;i<ids.length;i++){
                if(i==ids.length-1)
                    selection = selection + MediaStore.Audio.Media._ID  +" = ? ";
                else
                    selection = selection + MediaStore.Audio.Media._ID  +" = ? OR ";

                selectionArgs[i]=String.valueOf(ids[i]);
            }
        }

        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,selection,selectionArgs);
    }

    public static Bitmap decodeSampleBitmap(Resources res, int resId, int reqWidth, int reqHeight){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(res, resId,options);

        options.inSampleSize = calculateInSampleSize(options,reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res,resId,options);
    }

}
