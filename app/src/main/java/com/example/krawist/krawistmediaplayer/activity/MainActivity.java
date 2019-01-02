package com.example.krawist.krawistmediaplayer.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.adapter.PageAdapter;
import com.example.krawist.krawistmediaplayer.models.Musique;
import com.example.krawist.krawistmediaplayer.service.PlayerService;

import java.nio.InvalidMarkException;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionButton floatingActionButton;
    PlayerService playerService = null;
    boolean bound = false;
    TextView songName;
    TextView songArtist;
    ImageButton nextButton;
    ImageButton playButton ;
    ImageButton previousButton ;
    LinearLayout layout;
    Musique musiqueEnCours;

    Handler handler = new Handler();

    BottomNavigationView navigationView;

    static final String TAG = MainActivity.class.getSimpleName();

    private static final int MY_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (!hasPermission(MainActivity.this,PERMISSIONS)) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    PERMISSIONS,MY_PERMISSION_READ_EXTERNAL_STORAGE );

        } else {

            initialiseApp();
        }

        //configureViewPager();
    }

    private void initialiseApp(){
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        configureViewPager();
        initialiseVariable();

        if(playerService==null){
            Intent playerServiceIntent = new Intent(this,PlayerService.class);
            startService(playerServiceIntent);
            bindService(playerServiceIntent,connection,Context.BIND_AUTO_CREATE);
        }
    }

    public boolean hasPermission(Context context, String... permissions){

        if(context!=null && permissions!=null){
            for(String permission: permissions){
                if(ContextCompat.checkSelfPermission(context,permission)!=PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }

        return true;
    }

    private void initialiseVariable(){
        songName = findViewById(R.id.textview_song_title);
        nextButton = findViewById(R.id.imageview_next_button);
         songArtist= findViewById(R.id.textview_song_artist);
         playButton= findViewById(R.id.imageview_play_button);
         previousButton = findViewById(R.id.imageview_previous_button);
         layout = findViewById(R.id.layout_option);
    }

    private void configureBottonNaviagtion(){
        if(playerService!=null) {

                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int state = playerService.setMusicPause();

                        if (state == PlayerService.SONG_IS_PLAYING) {
                            playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                        } else if (state == PlayerService.SONG_IN_PAUSE) {
                            playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        }
                    }
                });

                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playerService.playNextSong();
                    }
                });

                previousButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playerService.playPreviousSong();
                    }
                });

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,PlayingMusic.class));
                }
            });
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder)service;
            playerService = binder.getService();
            bound = true;
            configureBottonNaviagtion();
            handler.postDelayed(new UpdateUserInterface(),1000);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_container,fragment);
        //transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialiseApp();
                }
            }
        }
    }

    private void configureViewPager(){
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(),MainActivity.this));

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class UpdateUserInterface implements Runnable{
        @Override
        public void run() {
            musiqueEnCours = playerService.getPlayingMusique();
            if(musiqueEnCours!=null){
                songArtist.setText(musiqueEnCours.getArtistName());
                songName.setText(musiqueEnCours.getMusicTitle());
                if(playerService.isPlaying()){
                    playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                }else{
                    playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
            }
            handler.postDelayed(this,1000);
        }
    }

}
