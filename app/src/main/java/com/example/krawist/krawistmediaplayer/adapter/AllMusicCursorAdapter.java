package com.example.krawist.krawistmediaplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Musique;

import org.w3c.dom.Text;

public class AllMusicCursorAdapter extends CursorAdapter {

    Context context;
    Cursor cursor;

    public  AllMusicCursorAdapter(Context context, Cursor cursor, int flags){

        super(context,cursor,flags);
        this.context = context;
        this.cursor = cursor;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.all_music_item,viewGroup,false);
        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView songName = view.findViewById(R.id.textview_all_music_title);
        TextView songArtist = view.findViewById(R.id.textview_all_music_artist);
        TextView songDuration = view.findViewById(R.id.textview_all_music_duration);

        Musique musique = Helper.matchCursorLineToMusic(context,cursor);
        songArtist.setText(musique.getArtistName());
        songDuration.setText(musique.DurationToString());
        songName.setText(musique.getAlbumName());
    }

}
