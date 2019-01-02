package com.example.krawist.krawistmediaplayer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaDrm;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Musique;
import com.example.krawist.krawistmediaplayer.service.PlayerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayingMusic extends AppCompatActivity {

    /*list of static variable*/
    private static final String TAG = PlayingMusic.class.getSimpleName();
    private static final int REFRESH_INTERVAL_IN_MILLISECOND = 500;

    Toolbar toolbar;
    AppCompatImageButton backButton;
    TextView songName;
    TextView songAlbum;
    ImageView pochette;
    SeekBar seekBar;
    TextView lowSongLimit;
    TextView hightSongLimit;
    AppCompatImageButton shuffleButton;
    AppCompatImageButton nextButton;
    AppCompatImageButton playButton;
    AppCompatImageButton previousButton;
    AppCompatImageButton replayButton;
    ArrayList<CharSequence> listOfSongId = new ArrayList<>();
    ArrayList<Musique> listOfSong = new ArrayList<>();
    Musique selectedMusique = new Musique();
    MediaPlayer mediaPlayer = new MediaPlayer();

    UpdateSeekBarPosition seekBarPosition = new UpdateSeekBarPosition();


    PlayerService playerService = null;
    boolean bound = false;

    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_music_layout);

        initialiseVariable();

        if(getIntent().getExtras()!=null){
            /* les extras sont different de null, donc c'est moi qui ai lancé l'activité
            * et j'ai defini des données pour la lecture*/

            selectedMusique = (Musique)getIntent().getExtras().getSerializable(Helper.PLAYING_SONG);
            if(selectedMusique!=null)
                    loadData();
        }else{
            /* extra egal a null, donc c'est peut etre moi qui ai lancé sans les données de lecture ou c'est la systeme
            qui a lancé l'activité
             */
            if(getIntent().getType()!=null){
                /* si le type vaut quelque chose alors c'est le systeme qui a lancé l'activité*/
                if(getIntent().getType().equals("audio/*")){
                    Uri data = getIntent().getData();
                    selectedMusique = Helper.getSpecificSong(PlayingMusic.this,getSongIdViaUri(data));
                    Intent playerServiceIntent = new Intent(this,PlayerService.class);
                    listOfSong.clear();
                    listOfSong.add(new Musique());
                    listOfSong.add(selectedMusique);
                    playerServiceIntent.putExtra(Helper.PLAYING_SONG,selectedMusique);
                    playerServiceIntent.putExtra(Helper.PLAYING_MUSIC_LIST,listOfSong);
                    startService(playerServiceIntent);
                    bindService(playerServiceIntent,connection,Context.BIND_AUTO_CREATE);

                }
            }else {
                // Log.e(TAG,"nous sommes dans le else du onCretae de PlayingMusic");
                Intent playerServiceIntent = new Intent(this, PlayerService.class);
                startService(playerServiceIntent);
                bindService(playerServiceIntent, connection, Context.BIND_AUTO_CREATE);
            }
        }

        putAction();

        //playMusic();
    }

    @Override
    protected void onResume() {
        Log.e(TAG,"on est dans le resume");
        super.onResume();
    }

    /*return the id of a song via the uri of that song*/
    private int getSongIdViaUri(Uri uri){
        String correctString = "";
        int i = uri.getPath().toString().length()-1;
        String chaine = uri.getPath().toString();
        do{
            String sous = chaine.substring(i);
            try{
                int numero = Integer.parseInt(sous);
                correctString = sous;
            }catch (NumberFormatException e){
                break;
            }
            i--;
        }while(i>0);

        int numero = Integer.parseInt(correctString);

        return numero;

    }

    private void putAction(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayingMusic.super.onBackPressed();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayingMusic.this.seekBar.setProgress(seekBar.getProgress());
                playerService.setMusicPosition(seekBar.getProgress());
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int state = playerService.setMusicPause();

               if(state==PlayerService.SONG_IS_PLAYING){
                   playButton.setImageResource(R.drawable.ic_pause_black_24dp);
                   handler.postDelayed(seekBarPosition,REFRESH_INTERVAL_IN_MILLISECOND);
               }else if(state==PlayerService.SONG_IN_PAUSE){
                   playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                   handler.removeCallbacks(seekBarPosition);
               }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerService!=null){
                    playerService.playNextSong();
                    selectedMusique = playerService.getPlayingMusique();
                    updateInterface();
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerService!=null){
                    playerService.playPreviousSong();
                    selectedMusique = playerService.getPlayingMusique();
                    updateInterface();
                }
            }
        });

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerService!=null){
                    playerService.setMusicRepeatMode();
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerService!=null){
                    playerService.setRandomMode();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        //unbindService(connection);
        super.onDestroy();
    }

    private void initialiseVariable(){
        backButton = findViewById(R.id.imageview_playing_music_backbutton);
        songName = findViewById(R.id.textview_song_name);
        songAlbum = findViewById(R.id.textview_song_album);
        pochette = findViewById(R.id.imageview_pochette);
        seekBar = findViewById(R.id.seekbar);
        lowSongLimit = findViewById(R.id.textview_low_time);
        hightSongLimit = findViewById(R.id.textview_high_time);
        shuffleButton = findViewById(R.id.imageview_shufflebutton);
        previousButton = findViewById(R.id.imageview_previous_button);
        playButton = findViewById(R.id.imageview_play_button);
        nextButton = findViewById(R.id.imageview_next_button);
        replayButton = findViewById(R.id.imageview_repeatbutton);
    }

    private void loadData(){
        Intent intent = getIntent();
        selectedMusique = (Musique)intent.getExtras().getSerializable(Helper.PLAYING_SONG);
        listOfSong = (ArrayList<Musique>)intent.getExtras().getSerializable(Helper.PLAYING_MUSIC_LIST);
        if(selectedMusique!=null){
            Intent playerServiceIntent = new Intent(this,PlayerService.class);
            playerServiceIntent.putExtra(Helper.PLAYING_SONG,selectedMusique);
            playerServiceIntent.putExtra(Helper.PLAYING_MUSIC_LIST,listOfSong);
            if(playerService==null){
                startService(playerServiceIntent);
                bindService(playerServiceIntent,connection,Context.BIND_AUTO_CREATE);
            }
            //Log.e("Krawist","le service a été démarré");
        }
    }

    private void playMusic(){

        try {
            mediaPlayer.stop();
            mediaPlayer.setDataSource(selectedMusique.getMusicPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            updateInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap createBlurBitmap(Bitmap bitmap){
        int i = 19;
        int j = 4;
        int a = bitmap.getWidth() - ((j-1)*(bitmap.getWidth()/j));
        int b = bitmap.getHeight() - ((i-1)*(bitmap.getHeight()/i));
        int width = bitmap.getWidth() - 2*a;
        int height = bitmap.getHeight()-2*b;
        Matrix matrix = new Matrix();
        matrix.postScale(1,1);
        bitmap = Bitmap.createBitmap(bitmap,a,b,width,height,matrix,false);
        RenderScript renderScript = RenderScript.create(this);
        Allocation input = Allocation.createFromBitmap(renderScript,bitmap);
        Allocation output = Allocation.createTyped(renderScript,input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript,Element.U8_4(renderScript));

        script.setRadius(25f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        return bitmap;

    }

    private void updateInterface(){
        if(selectedMusique!=null){
            songAlbum.setText(selectedMusique.getAlbumName());
            songName.setText(selectedMusique.getMusicTitle());
            //Log.e("Krawist","Song Path "+selectedMusique.getPochette());
            Bitmap bitmap;
            if(selectedMusique.getPochette().equals(Helper.DEFAULT_ALBUM_ART_STRING)){
                bitmap = Helper.decodeSampleBitmap(getResources(),R.drawable.default_background,600,600);
            }else{
                bitmap = Helper.decodeSampleBitmap(selectedMusique.getPochette(),600,600);
            }
            if(bitmap==null){
                bitmap = Helper.decodeSampleBitmap(getResources(),R.drawable.default_background,600,600);
            }
            pochette.setImageBitmap(bitmap);
            RelativeLayout root = findViewById(R.id.layout_root);
            root.setBackground(new BitmapDrawable(createBlurBitmap(bitmap)));


            lowSongLimit.setText(timeToString(0));
            hightSongLimit.setText(timeToString(selectedMusique.getMusicDuration()));
            playButton.setImageResource(R.drawable.ic_pause_black_24dp);
            if(playerService.isPlaying()){
                playButton.setImageResource(R.drawable.ic_pause_black_24dp);
            }else{
                playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }

            switch (playerService.getLooping()){
                case PlayerService.SONG_IS_LOOPING_ALL:
                    replayButton.setImageResource(R.drawable.ic_repeat_black_24dp);
                    replayButton.setAlpha(500);
                    break;
                case PlayerService.SONG_IS_LOOPING_ONE:
                    replayButton.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                    replayButton.setAlpha(500);
                    break;
                    case PlayerService.SONG_IS_NOT_LOOPING:
                        replayButton.setImageResource(R.drawable.ic_repeat_black_24dp);
                        replayButton.setAlpha(150);
                        break;
            }
            shuffleButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            if(playerService.getRandomPlaying()==PlayerService.RANDOM_PLAYING_DISABLED){
                shuffleButton.setAlpha(150);
            }else{
                shuffleButton.setAlpha(500);
            }
        }
    }

    private String timeToString(int timeInMillisecond){

        long hour = TimeUnit.MILLISECONDS.toHours(timeInMillisecond);
        long minute = TimeUnit.MILLISECONDS.toMinutes(timeInMillisecond)-(hour*60);
        long second = TimeUnit.MILLISECONDS.toSeconds(timeInMillisecond)-(minute*60);

        String hourString=""+hour;
        String minuteString=""+minute;
        String secondString=""+second;

        String time="";

       if(minute<10){
           minuteString="0"+minute;
       }
       if(second<10){
           secondString="0"+second;
       }

       return minuteString+":"+secondString;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder)service;
            playerService = binder.getService();
            bound = true;
            selectedMusique = playerService.getPlayingMusique();
            updateInterface();
            handler.postDelayed(seekBarPosition,REFRESH_INTERVAL_IN_MILLISECOND);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    public class UpdateSeekBarPosition implements Runnable{

        @Override
        public void run() {
            if(selectedMusique!=null){
                if(playerService.isPlaying()){
                    selectedMusique = playerService.getPlayingMusique();
                    int currentPosition = playerService.getMusicPosition();
                    updateInterface();
                    seekBar.setMax(selectedMusique.getMusicDuration());
                    seekBar.setProgress(currentPosition);
                    lowSongLimit.setText(timeToString(currentPosition));
                }
                handler.postDelayed(this,REFRESH_INTERVAL_IN_MILLISECOND);
            }
        }
    }
    
}
