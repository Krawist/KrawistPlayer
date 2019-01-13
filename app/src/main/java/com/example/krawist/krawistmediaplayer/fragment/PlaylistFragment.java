package com.example.krawist.krawistmediaplayer.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.adapter.PlaylistAdapter;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Playlist;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {

    RecyclerView recyclerView;
    PlaylistAdapter adapter;
    ArrayList<Playlist> playlists;

    public PlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlists = Helper.getAllPlaylists(getContext());

        recyclerView = view.findViewById(R.id.recyclerview_all_playslist);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new PlaylistAdapter(getContext(),playlists);
        recyclerView.setAdapter(adapter);

        return view;

    }

}
