package com.example.krawist.krawistmediaplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.activity.DetailAlbum;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Album;


import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter {
    ArrayList<Album> listOfAlbum;
    Activity activity;
    LruCache<String, Bitmap> memoryCache;
    /* marquer ceci comme un preference */
    public static final int numberOfItemInLine = 3;
    Context context;
    private static final String TAG = AlbumAdapter.class.getSimpleName();
    private int gridSize = numberOfItemInLine;

    public AlbumAdapter(ArrayList<Album> listOfAlbum, Context context, Activity activity, int gridSize){
        this.listOfAlbum = listOfAlbum;
        this.context = context;
        this.activity = activity;
        this.gridSize = gridSize;
        configureCache();
    }

    public void configureCache(){
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize = maxMemory/8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap bitmap) {
                return bitmap.getByteCount()/1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap){
        memoryCache.put(key,bitmap);
    }

    public Bitmap getBitmapFromMemoryCache(String key){
        return memoryCache.get(key);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        /*on recupere la taille de l'interface*/
        //int gridSize = PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getResources().getString(R.string.album_grid_preference_size_key),AlbumAdapter.numberOfItemInLine);
        Display display = activity.getWindowManager().getDefaultDisplay();
        int size = display.getWidth()/gridSize;

        RelativeLayout rootView = new RelativeLayout(context);
        rootView.setPadding(3,3,3,3);
        rootView.setLayoutParams(new RelativeLayout.LayoutParams(size,size));
        View insideView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_item,viewGroup,false);

        rootView.addView(insideView,new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        return new AlbumViewHolder(rootView,activity );

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ((AlbumViewHolder)viewHolder).putData(listOfAlbum.get(i));

    }

    @Override
    public int getItemCount() {
        return listOfAlbum.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewArtist;
        ImageView albumArt;
        RelativeLayout layout;
        FragmentActivity fragmentActivity;

        public AlbumViewHolder(View view, Activity activity){

            super(view);
            fragmentActivity = (FragmentActivity)activity;
            textViewArtist = view.findViewById(R.id.textview_album_artist);
            textViewTitle = view.findViewById(R.id.textview_album_title);
            albumArt = view.findViewById(R.id.imageview_album_art);
            layout = view.findViewById(R.id.layout_root);
        }

        public void putData(final Album album){

            textViewTitle.setText(album.getAlbumName());
            textViewArtist.setText(album.getArtistName());
            String albumArtPath = album.getAlbumArt();

            Bitmap bitmap = getBitmapFromMemoryCache(albumArtPath);

            if(bitmap==null){

                if(albumArtPath.equals(Helper.DEFAULT_ALBUM_ART_STRING)){
                    bitmap = Helper.decodeSampleBitmap(context.getResources(),R.drawable.default_background,117,117);
                }else{
                    bitmap = Helper.decodeSampleBitmap(albumArtPath,117,117);
                }
                if(bitmap==null)
                    bitmap = Helper.decodeSampleBitmap(context.getResources(),R.drawable.default_background,117,117);

                addBitmapToMemoryCache(albumArtPath,bitmap);

            }

            albumArt.setImageBitmap(bitmap);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                Intent intent = new Intent(itemView.getContext(),DetailAlbum.class);
                intent.putExtra(Helper.INTENT_ALBUM_ID_KEY,album.getAlbumId());
                itemView.getContext().startActivity(intent);

                }
            });
        }

    }

    public void swapItems(ArrayList<Album> list){
        this.listOfAlbum = list;
        notifyDataSetChanged();
    }
}
