package com.example.krawist.krawistmediaplayer.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.adapter.AllMusicAdapter;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Musique;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllMusicFragment extends Fragment {

   ArrayList<Musique> listOfSong;

    Context context;

    RecyclerView recyclerView;

    AllMusicAdapter adapter;

    View rootView = null;

    private static final String TAG = AllMusicFragment.class.getSimpleName();

    private static final int MY_PERMISSION_READ_EXTERNAL_STORAGE = 1;

    public AllMusicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         rootView =  inflater.inflate(R.layout.fragment_base_layout, container, false);
        this.context = container.getContext();

        listOfSong = Helper.getAllMusique(context);

        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         adapter = new AllMusicAdapter(context, listOfSong);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context,LinearLayoutManager.VERTICAL));
        registerForContextMenu(recyclerView);
        return rootView;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Musique song = listOfSong.get(item.getGroupId());
        switch (item.getItemId()){
            case R.id.action_share:

                Log.e(TAG,"on a clique sur share de "+song.getMusicTitle());
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("file/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(song.getMusicPath()));
                startActivity(Intent.createChooser(shareIntent,"Partager via"));
                break;

            case R.id.action_delete:
                Log.e(TAG,"on a clique sur delete de "+Uri.parse(song.getMusicPath()).getPath());
                File file=  new File(song.getMusicPath());
                file.delete();
                if(file.exists()){
                    Toast.makeText(context,song.getMusicTitle()+" existe",Toast.LENGTH_SHORT);
                }else{
                    Toast.makeText(context,song.getMusicTitle()+" n'existe pas",Toast.LENGTH_SHORT);
                }
                /*boolean deleted = file.delete();
                if(deleted){
                    Toast.makeText(context,song.getMusicTitle()+" a été supprimé",Toast.LENGTH_SHORT);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(context,song.getMusicTitle()+" echec de la suppression ",Toast.LENGTH_SHORT);
                }*/
                break;

            case R.id.action_detail:
                openDialogDetail(song);
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void openDialogDetail(Musique musique){
        final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.detail_song_dialog_layout);
        TextView titre = dialog.findViewById(R.id.textview_detail_song_dialog_song_title);
        TextView album = dialog.findViewById(R.id.textview_detail_song_dialog_song_album);
        TextView artist = dialog.findViewById(R.id.textview_detail_song_dialog_song_artist);
        TextView track = dialog.findViewById(R.id.textview_detail_song_dialog_song_track);
        TextView size = dialog.findViewById(R.id.textview_detail_song_dialog_song_size);
        TextView duration = dialog.findViewById(R.id.textview_detail_song_dialog_song_duration);
        Button cancel = dialog.findViewById(R.id.button_detail_song_dialog_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        titre.setText(musique.getMusicTitle());
        album.setText(musique.getAlbumName());
        artist.setText(musique.getArtistName());
        track.setText(musique.getMusicTrack()+"");
        duration.setText(musique.DurationToString());
        size.setText(musique.getSize()+"");

        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }
}
