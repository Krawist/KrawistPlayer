package com.example.krawist.krawistmediaplayer.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.adapter.AlbumSongsAdapter;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Album;
import com.example.krawist.krawistmediaplayer.models.Musique;
import com.example.krawist.krawistmediaplayer.service.PlayerService;

import java.io.File;
import java.util.ArrayList;

public class DetailAlbum extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Musique> listOfAlbumSong;
    Album album;
    ImageButton backButton;
    TextView toolbarLabel;
    Toolbar toolbar;
    AlbumSongsAdapter adapter;
    FloatingActionButton floatingActionButton;
    private static final String TAG = DetailAlbum.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_album);

        toolbar = findViewById(R.id.toolbar);
        backButton = toolbar.findViewById(R.id.imagebutton_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailAlbum.super.onBackPressed();
            }
        });
        toolbar.setTitle("");
        toolbarLabel = toolbar.findViewById(R.id.toolbar_label);
        setSupportActionBar(toolbar);

        loadData();

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailAlbum.this,PlayerService.class);
                intent.putExtra(Helper.PLAYING_SONG,listOfAlbumSong.get(1));
                intent.putExtra(Helper.PLAYING_MUSIC_LIST,listOfAlbumSong);
                startService(intent);
            }
        });
        configureRecyclerView();

    }

    private void loadData(){
       Intent intent = getIntent();
       long albumId = intent.getLongExtra(Helper.INTENT_ALBUM_ID_KEY,-1);

        if(albumId!=-1){
            album = Helper.getSpecificAlbum(DetailAlbum.this,albumId);
            toolbarLabel.setText(album.getAlbumName());
            listOfAlbumSong = Helper.getAllAlbumSongs(DetailAlbum.this,album.getAlbumId());
        }
    }

    private void configureRecyclerView(){

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlbumSongsAdapter(listOfAlbumSong,album);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        registerForContextMenu(recyclerView);

    }

    private void deleteFile(Musique song, int position){

        File file=  new File(song.getMusicPath());
        file.delete();
        Helper.deleteSpecificSong(this,song.getMusicId());
        adapter.removeItem(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        final Musique song = listOfAlbumSong.get(item.getGroupId());
        switch (item.getItemId()){
            case R.id.action_share:

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("file/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(song.getMusicPath()));
                startActivity(Intent.createChooser(shareIntent,"Partager via"));
                break;

            case R.id.action_delete:
                Log.e(TAG,"on est dans le delete de "+song.getMusicTitle());
                final Dialog deletedDialog = new Dialog(this);
                deletedDialog.setContentView(R.layout.delete_dialog_layout);
                Button cancel = deletedDialog.findViewById(R.id.button_delete_dialog_cancel);
                Button validate = deletedDialog.findViewById(R.id.button_delete_dialog_validate);
                deletedDialog.setCanceledOnTouchOutside(false);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletedDialog.dismiss();
                    }
                });

                validate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteFile(song,item.getGroupId());
                        deletedDialog.dismiss();
                    }
                });

                deletedDialog.show();
                break;

            case R.id.action_detail:
                openDialogDetail(song);
                break;

        }

        return super.onContextItemSelected(item);
    }

    private void openDialogDetail(Musique musique){
        final Dialog dialog = new Dialog(this);

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
