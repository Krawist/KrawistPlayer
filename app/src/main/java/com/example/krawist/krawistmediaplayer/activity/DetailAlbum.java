package com.example.krawist.krawistmediaplayer.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.adapter.AlbumSongsAdapter;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Album;
import com.example.krawist.krawistmediaplayer.models.Musique;
import com.example.krawist.krawistmediaplayer.service.PlayerService;

import java.util.ArrayList;

public class DetailAlbum extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Musique> listOfAlbumSong;
    Album album;
    ImageButton backButton;
    TextView toolbarLabel;
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;



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
        AlbumSongsAdapter adapter = new AlbumSongsAdapter(listOfAlbumSong,album);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));


    }
}
