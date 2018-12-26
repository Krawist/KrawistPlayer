package com.example.krawist.krawistmediaplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.activity.PlayingMusic;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Musique;

import java.util.ArrayList;

public class AllMusicAdapter extends RecyclerView.Adapter {

    ArrayList<Musique> listOfSong;
    Context context;

    public AllMusicAdapter(Context context, ArrayList<Musique> listOfSong){
        this.listOfSong = listOfSong;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new AllMusicViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_music_item,viewGroup,false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

           ((AllMusicViewHolder)viewHolder).putData(listOfSong.get(i));
    }

    @Override
    public int getItemCount() {
        return listOfSong.size();
    }

    public class AllMusicViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewArtist;
        TextView textViewDuration;
        RelativeLayout rootLayout;


        public AllMusicViewHolder(View view){

            super(view);

            textViewArtist = view.findViewById(R.id.textview_all_music_artist);
            textViewDuration = view.findViewById(R.id.textview_all_music_duration);
            textViewTitle = view.findViewById(R.id.textview_all_music_title);
            rootLayout = view.findViewById(R.id.layout_root);


        }

        public void putData(final Musique musique){
            textViewTitle.setText(musique.getMusicTitle());
            textViewDuration.setText(musique.DurationToString());
            textViewArtist.setText(musique.getAlbumName());

            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),PlayingMusic.class);
                    intent.putExtra(Helper.PLAYING_SONG,musique);
                    intent.putExtra(Helper.PLAYING_MUSIC_LIST,listOfSong);
                    itemView.getContext().startActivity(intent);
                }
            });


        }
    }
}
