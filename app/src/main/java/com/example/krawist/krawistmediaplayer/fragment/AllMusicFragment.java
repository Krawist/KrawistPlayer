package com.example.krawist.krawistmediaplayer.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.adapter.AllMusicAdapter;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Musique;

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
        return rootView;
    }

}
