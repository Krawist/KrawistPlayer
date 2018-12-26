package com.example.krawist.krawistmediaplayer.fragment;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
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

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_album, container, false);

        FrameLayout layout = view.findViewById(R.id.layout_root);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = (size.x)/3;
        int height = width;

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width,height);
        layout.setLayoutParams(layoutParams);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(),3));
        this.context = container.getContext();
        listOfAlbum = Helper.getAllAlbum(context);
        AlbumAdapter adapter = new AlbumAdapter(listOfAlbum, context);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
