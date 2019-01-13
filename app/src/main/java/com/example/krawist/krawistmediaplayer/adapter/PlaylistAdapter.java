package com.example.krawist.krawistmediaplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.models.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Playlist> listOfPlaylist;

    public PlaylistAdapter(Context context, ArrayList<Playlist> listOfPlaylist){
        this.context = context;
        this.listOfPlaylist = listOfPlaylist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_item,viewGroup,false),context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((ViewHolder)viewHolder).putData(listOfPlaylist.get(i));
    }

    @Override
    public int getItemCount() {
        return listOfPlaylist.size();
    }


    private class ViewHolder extends RecyclerView.ViewHolder{

        TextView playlistNameView;
        TextView playListNumberOfSongsView;
        Context context;

        public ViewHolder(View view,Context context){
            super(view);
            this.context = context;
            playlistNameView = view.findViewById(R.id.textview_playlist_name);
            playListNumberOfSongsView = view.findViewById(R.id.textview_playlist_number_of_song);
        }

        public void putData(Playlist playlist){
            playlistNameView.setText(playlist.getName());
            String message="";
            if(playlist.getNumberOfSongs()<=1){
                message = "Chanson";
            }else{
                message = "Chansons";
            }

            playListNumberOfSongsView.setText(playlist.getNumberOfSongs() + " " + message);
        }
    }
}


