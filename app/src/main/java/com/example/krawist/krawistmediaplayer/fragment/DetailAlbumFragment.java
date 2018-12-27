package com.example.krawist.krawistmediaplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.activity.DetailAlbum;
import com.example.krawist.krawistmediaplayer.adapter.AlbumSongsAdapter;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Album;
import com.example.krawist.krawistmediaplayer.models.Musique;

import java.io.File;
import java.util.ArrayList;

public class DetailAlbumFragment extends Fragment {

    View rootView;
    long albumId;
    Album album;
    ArrayList<Musique> listOfAlbumSong;
    Context context;
    RecyclerView recyclerView;
    RelativeLayout albumDescription;
    private static final String TAG = DetailAlbumFragment.class.getSimpleName();

    public void setAlbumId(long albumId){
        this.albumId = albumId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_detail_album,container,false);

        /*toolbar.setTitle("");
        toolbarLabel = toolbar.findViewById(R.id.toolbar_label);
        setSupportActionBar(toolbar);*/

        recyclerView = rootView.findViewById(R.id.recyclerview);
        context = container.getContext();
        loadData();
        configureRecyclerView();
       // Log.e("Krawist","on a fini le onCreateView");
        return rootView;
    }

    public DetailAlbumFragment(){

    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    private void loadData(){
        /*Intent intent = getIntent();
        long albumId = intent.getLongExtra(Helper.INTENT_ALBUM_ID_KEY,-1);
*/
        if(albumId!=-1){
            album = Helper.getSpecificAlbum(context,albumId);
            //toolbarLabel.setText(album.getAlbumName());
            listOfAlbumSong = Helper.getAllAlbumSongs(context,album.getAlbumId());
        }

        //Log.e("Krawist","on fini dans le loadData");
    }

    private void configureRecyclerView(){

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        AlbumSongsAdapter adapter = new AlbumSongsAdapter(listOfAlbumSong,album);
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(context,LinearLayoutManager.VERTICAL));
        //Log.e("Krawist","on fini dans le configureRecyclerView");
        registerForContextMenu(recyclerView);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Musique song = listOfAlbumSong.get(item.getGroupId());
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
        }

        return super.onContextItemSelected(item);
    }
}
