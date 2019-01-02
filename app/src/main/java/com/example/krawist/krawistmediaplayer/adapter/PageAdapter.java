package com.example.krawist.krawistmediaplayer.adapter;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.krawist.krawistmediaplayer.fragment.AlbumFragment;
import com.example.krawist.krawistmediaplayer.fragment.AllMusicFragment;
import com.example.krawist.krawistmediaplayer.fragment.ArtistFragment;

public class PageAdapter extends FragmentPagerAdapter {

    Context context;

    String TAG = PageAdapter.class.getSimpleName();

    public PageAdapter(FragmentManager fragmentManager,Context context){
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new Fragment();
        switch (i){
            case 0:
                fragment =  new AllMusicFragment();
                Log.e(TAG,"dans le getItem de allMusicFragment");
                break;
            case 1:
                fragment = new AlbumFragment();
                Log.e(TAG,"dans le getItem de albumFragment");
                break;
/*            case 2:
                //fragment = new ArtistFragment();
                //fragment =  new AllMusicFragment();
                Log.e(TAG,"dans le getItem de artistFragment");
                break;
            case 3:
                //fragment = new ArtistFragment();
                //fragment =  new AllMusicFragment();
                Log.e(TAG,"dans le getItem de artistFragment");
                break;*/
        }

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Chansons";
                break;
            case 1:
                title = "Albums";
                break;
/*            case 2:
                title = "Artistes";
                break;

            case 3:
                title = "Genres";
                break;*/
        }
       return title;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
