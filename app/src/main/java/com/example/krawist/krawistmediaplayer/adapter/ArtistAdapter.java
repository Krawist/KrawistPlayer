package com.example.krawist.krawistmediaplayer.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;

import com.example.krawist.krawistmediaplayer.models.Artist;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter{

    ArrayList<Artist> listOfArtist;

    public ArtistAdapter(ArrayList<Artist> listOfArtist){
        this.listOfArtist= listOfArtist;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new ArtistViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_item,viewGroup,false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ((ArtistViewHolder)viewHolder).putData(listOfArtist.get(i));

    }

    @Override
    public int getItemCount() {
        return listOfArtist.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder{

        TextView artistName;
        TextView numberOfSongOrAlbum;
        ImageView dropdownButton;

        public ArtistViewHolder(View view){

            super(view);

            artistName = view.findViewById(R.id.textview_artist_artistname);
            numberOfSongOrAlbum = view.findViewById(R.id.textview_artist_numberofsong);
            dropdownButton = view.findViewById(R.id.imageview_dropdown);

        }

        public void putData(Artist artist){

            artistName.setText(artist.getArtistName());
            numberOfSongOrAlbum.setText(String.valueOf(artist.getNumberOfSong())+" Musiques");
        }
    }
}
