package com.example.krawist.krawistmediaplayer.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.adapter.AlbumAdapter;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Album;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {

    ArrayList<Album> listOfAlbum;
    Context context;
    AlbumAdapter adapter;
    private static final String TAG = AlbumFragment.class.getSimpleName();
    RecyclerView recyclerView;

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_album, container, false);
        this.context = container.getContext();
        setHasOptionsMenu(true);
        int gridSize = PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getResources().getString(R.string.album_grid_preference_size_key),AlbumAdapter.numberOfItemInLine);

/*        FrameLayout layout = view.findViewById(R.id.layout_root);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = (size.x)/gridSize;
        int height = width;


        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width,height);
        layout.setLayoutParams(layoutParams);*/

        recyclerView = view.findViewById(R.id.recyclerview);
        listOfAlbum = Helper.getAllAlbum(context);
        adapter = new AlbumAdapter(listOfAlbum, context,getActivity(),gridSize);

        configureRecyclerView(gridSize);
        return view;
    }

    private void configureRecyclerView(int gridSize){

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(context,gridSize));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int value = AlbumAdapter.numberOfItemInLine;

        switch (item.getItemId()){

            case R.id.action_grid_colums_2:
                value = 2;
                updateRecyclerView(value);
                break;

            case R.id.action_grid_colums_3:
                value = 3;
                updateRecyclerView(value);
                break;

            case R.id.action_grid_colums_4:
                value = 4;
                updateRecyclerView(value);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateRecyclerView(int value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        adapter = new AlbumAdapter(listOfAlbum, context,getActivity(),value);
        recyclerView.setLayoutManager(new GridLayoutManager(context,value));
        recyclerView.setAdapter(adapter);
        sharedPreferences.edit().putInt(context.getResources().getString(R.string.album_grid_preference_size_key),value).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        listOfAlbum = Helper.getAllAlbum(context);
        adapter.swapItems(listOfAlbum);
    }
}
