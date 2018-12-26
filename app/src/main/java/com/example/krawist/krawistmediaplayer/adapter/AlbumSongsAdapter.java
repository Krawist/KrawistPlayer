package com.example.krawist.krawistmediaplayer.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.activity.DetailAlbum;
import com.example.krawist.krawistmediaplayer.activity.PlayingMusic;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Album;
import com.example.krawist.krawistmediaplayer.models.Musique;

import java.security.KeyStore;
import java.util.ArrayList;

public class AlbumSongsAdapter extends RecyclerView.Adapter{
    ArrayList<Musique> listOfMusic;
    Album album;

    public AlbumSongsAdapter(ArrayList<Musique> listOfMusic, Album album){
        this.listOfMusic = listOfMusic;
        this.album = album;

    }

    @Override
    public int getItemViewType(int position) {
       // Log.e("Krawist","Position, dans le getItemViewType "+position);
        if(position==0){
            return R.layout.album_presentation_layout;
        }else{
            return R.layout.all_music_item;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new AlbumSongsViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(i,viewGroup,false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        //Log.e("Krawist","le i dans le onBindViewHolder "+i);
        ((AlbumSongsViewHolder)viewHolder).putData(listOfMusic.get(i),i,album);

    }

    @Override
    public int getItemCount() {
        return listOfMusic.size();
    }

    public class AlbumSongsViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewArtist;
        TextView textViewDuration;
        ImageView pochetteAlbum;
        TextView numberOfSong;
        TextView artistName;
        RelativeLayout racine;
        long albumId;

        public AlbumSongsViewHolder(View view){

            super(view);


            if(view.getId()==R.id.racine){
                numberOfSong = view.findViewById(R.id.textview_album_numberofsong);
                artistName = view.findViewById(R.id.textview_album_artistname);
                pochetteAlbum = view.findViewById(R.id.imageview_album_art);
                racine = view.findViewById(R.id.racine);
            }else{
                racine = view.findViewById(R.id.layout_root);
                textViewArtist = view.findViewById(R.id.textview_all_music_artist);
                textViewDuration = view.findViewById(R.id.textview_all_music_duration);
                textViewTitle = view.findViewById(R.id.textview_all_music_title);
            }



        }

        public void putData(final Musique musique, int position, Album album){

            //Log.e("Krawist","le i dans le putData "+position);
            albumId = album.getAlbumId();
            if(position==0){
                if(album.getNumberOfSong()>1)
                    numberOfSong.setText(album.getNumberOfSong()+" Musiques");
                else{
                    numberOfSong.setText(album.getNumberOfSong()+" Musique");
                }
                artistName.setText(album.getArtistName());

                if(album.getAlbumArt().equals(Helper.DEFAULT_ALBUM_ART_STRING)){
                    racine.setBackground(new BitmapDrawable(Helper.decodeSampleBitmap(itemView.getContext().getResources(),
                    R.drawable.default_background,
                            350,
                            350)));
                }else{
                    racine.setBackground(new BitmapDrawable(Helper.decodeSampleBitmap(album.getAlbumArt(),350,350)));
                }

            }else {
                textViewTitle.setText(musique.getMusicTitle());
                textViewDuration.setText(musique.DurationToString());
                textViewArtist.setText(musique.getArtistName());
                racine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(itemView.getContext(),PlayingMusic.class);
                        intent.putExtra(Helper.PLAYING_MUSIC_LIST,listOfMusic);
                        intent.putExtra(Helper.PLAYING_SONG,musique);
                        itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
    }
}
