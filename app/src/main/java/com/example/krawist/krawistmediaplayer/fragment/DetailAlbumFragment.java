package com.example.krawist.krawistmediaplayer.fragment;

import android.app.Dialog;
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    AlbumSongsAdapter adapter;
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
        adapter = new AlbumSongsAdapter(listOfAlbumSong,album);
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(context,LinearLayoutManager.VERTICAL));
        //Log.e("Krawist","on fini dans le configureRecyclerView");
        registerForContextMenu(recyclerView);
    }

    private void deleteFile(Musique song, int position){

        File file=  new File(song.getMusicPath());
        file.delete();
        Helper.deleteSpecificSong(context,song.getMusicId());
        adapter.removeItem(position);
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
                final Dialog deletedDialog = new Dialog(context);
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
