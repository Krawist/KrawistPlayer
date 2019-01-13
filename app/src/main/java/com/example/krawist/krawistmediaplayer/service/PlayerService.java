package com.example.krawist.krawistmediaplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.activity.PlayingMusic;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Musique;

import java.util.ArrayList;
import java.util.Random;

public class PlayerService extends Service {

    /*static variable*/
    public static final int SONG_IN_PAUSE = 1;
    public static final int SONG_IS_PLAYING = 2;
    public static final int SONG_IS_LOOPING_ONE = 3;
    public static final int SONG_IS_LOOPING_ALL = 4;
    public static final int SONG_IS_NOT_LOOPING = 5;
    public static final int NOTIFICATION_REQUEST_CODE = 6;
    public static final int ACTION_PLAY_REQUEST_CODE = 7;
    public static final int RANDOM_PLAYING_ENABLED = 8;
    public static final int RANDOM_PLAYING_DISABLED = 10;
    public static final String ACTION_PLAY = "com.example.krawist.krawistmediaplayer.actionplay";
    public static final String ACTION_NEXT = "com.example.krawist.krawistmediaplayer.actionnext";
    public static final String ACTION_PREVIOUS = "com.example.krawist.krawistmediaplayer.actionprevious";

    private final IBinder binder = new LocalBinder();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String TAG = PlayerService.class.getSimpleName();
    private Intent receivedIntent;
    private int repeatMode=0;
    private int randomMode = RANDOM_PLAYING_DISABLED;
    private boolean isPlaying = false;
    private ArrayList<Musique> listOfSong = new ArrayList<>();
    private ArrayList<Integer> listOfPlayedSongPosition = new ArrayList<>();
    private int looping = SONG_IS_NOT_LOOPING;

    private int currentSongPosition;
    private int previousSongPosition;
    private int nextSongPosition;

    private Musique playingMusic;

    public Musique getPlayingMusique(){
        return this.playingMusic;
    }

    public PlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(this.ACTION_PLAY);
        filter.addAction(this.ACTION_NEXT);
        filter.addAction(this.ACTION_PREVIOUS);
        registerReceiver(new ServiceReceiver(),filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.receivedIntent = intent;
        if(intent!=null && intent.getExtras()!=null){

            playingMusic = (Musique)intent.getExtras().getSerializable(Helper.PLAYING_SONG);
            listOfSong = (ArrayList<Musique>)intent.getExtras().getSerializable(Helper.PLAYING_MUSIC_LIST);

            isPlaying = true;

            playingMusicThread.interrupt();
            playingMusicThread.start();

        }

        createNotificationChannel();

        startNotificationForeground();

        return START_STICKY;

    }

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

            if(currentSongPosition==listOfSong.size()-1){
               if(looping==SONG_IS_LOOPING_ALL) {

                   playNextSong();

               }else if(looping!=SONG_IS_LOOPING_ONE){

                   isPlaying = false;

                   startNotificationForeground();

                   stopForeground(false);
               }
            }else if(looping==SONG_IS_NOT_LOOPING || looping==SONG_IS_LOOPING_ALL){

                playNextSong();

                startNotificationForeground();
            }

        }
    };

    private int getRandomNumber(int limit){
        Random rand = new Random();
        int nb = rand.nextInt(limit);
        while(listOfPlayedSongPosition.contains(nb)){
            nb = rand.nextInt(limit);
        }
        listOfPlayedSongPosition.add(nb);
        return nb;
    }

    private void updatePosition(int currentSongPosition){
        if(listOfSong.size()>0){
            if(randomMode==RANDOM_PLAYING_ENABLED){

                nextSongPosition = getRandomNumber(listOfSong.size());
                previousSongPosition = getRandomNumber(listOfSong.size());
            }else{
                previousSongPosition = (currentSongPosition-1)%listOfSong.size();
                nextSongPosition = (currentSongPosition+1)%listOfSong.size();
            }

            this.currentSongPosition = currentSongPosition;
            if(previousSongPosition==-1 || previousSongPosition ==0){
                previousSongPosition = listOfSong.size()-1;
            }
            if(nextSongPosition==0){
                nextSongPosition = 1;
            }
        }
    }

    private void startNotificationForeground(){

        //Intent intent = new Intent(getBaseContext(),PlayingMusic.class);
        //PendingIntent pendingIntent = new PendingIntent()

        RemoteViews notificationLayout = new RemoteViews(getPackageName(),R.layout.notification_layout);
        if(playingMusic !=null){

            Intent playIntent = new Intent();
            playIntent.setAction(ACTION_PLAY);
            PendingIntent playPendingIntent = PendingIntent.getBroadcast(getBaseContext(),ACTION_PLAY_REQUEST_CODE,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            Intent nextIntent = new Intent();
            nextIntent.setAction(ACTION_NEXT);
            PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getBaseContext(),ACTION_PLAY_REQUEST_CODE,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            Intent previousIntent = new Intent();
            previousIntent.setAction(ACTION_PREVIOUS);
            PendingIntent previousPendingIntent = PendingIntent.getBroadcast(getBaseContext(),ACTION_PLAY_REQUEST_CODE,previousIntent,PendingIntent.FLAG_UPDATE_CURRENT);


            configureNotificationRemoteView(notificationLayout,playPendingIntent,nextPendingIntent,previousPendingIntent);

            Notification notification = buildNotification(notificationLayout);

            startForeground(NOTIFICATION_REQUEST_CODE,notification);

        }
    }

    private Notification buildNotification(RemoteViews notificationLayout){

        Intent intent = new Intent(getBaseContext(),PlayingMusic.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),"Krawist")
                .setStyle(new android.support.v4.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .setShowWhen(false);


        if(isPlaying){
            builder.setSmallIcon(R.drawable.ic_play_arrow_notification);
            builder.setOngoing(true);
        }else{
            builder.setSmallIcon(R.drawable.ic_pause_black_notification);
            builder.setOngoing(false);
        }

        return builder.build();
    }

    private void configureNotificationRemoteView(RemoteViews notificationLayout,PendingIntent playPendingIntent, PendingIntent nextPendingIntent, PendingIntent previousPendingIntent){
        notificationLayout.setTextViewText(R.id.textview_notification_song_artist, playingMusic.getArtistName());
        notificationLayout.setTextViewText(R.id.textview_notification_song_title, playingMusic.getMusicTitle());
        Bitmap bitmap;
        if(playingMusic.getPochette().equals(Helper.DEFAULT_ALBUM_ART_STRING)){
            bitmap = Helper.decodeSampleBitmap(getResources(),R.drawable.default_background,600,600);
        }else{
            bitmap = Helper.decodeSampleBitmap(playingMusic.getPochette(),600,600);
        }
        notificationLayout.setImageViewBitmap(R.id.imageview_notification_song_pochette,bitmap);
        if(mediaPlayer.isPlaying()){
            notificationLayout.setImageViewResource(R.id.imageview_notification_play,R.drawable.ic_pause_black_notification);
        }else{
            notificationLayout.setImageViewResource(R.id.imageview_notification_play,R.drawable.ic_play_arrow_notification);
        }

        if(playPendingIntent!=null){
            notificationLayout.setOnClickPendingIntent(R.id.imageview_notification_play,playPendingIntent);
        }

        if(nextPendingIntent!=null){
            notificationLayout.setOnClickPendingIntent(R.id.imageview_notification_next,nextPendingIntent);
        }

        if(previousPendingIntent!=null){
            notificationLayout.setOnClickPendingIntent(R.id.imageview_notification_previous,previousPendingIntent);
        }
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "chanel_name";
            String description ="channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Krawist",name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    Thread playingMusicThread = new Thread(new Runnable() {
        @Override
        public void run() {


            /* on recherche la position du song choisi dans la liste des songs, pour
            savoir quel song vient avant lui et quel autre apres
             */
            for(int k=0;k<listOfSong.size();k++){
                if(playingMusic.getMusicId()==listOfSong.get(k).getMusicId()){
                    currentSongPosition = k;
                    updatePosition(currentSongPosition);
                    break;
                }
            }

            loadAndPlayMusic(currentSongPosition);
        }
    });

    private void loadAndPlayMusic(int position){
        playingMusic = listOfSong.get(position);
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(getBaseContext(),Uri.parse(playingMusic.getMusicPath()));
            if(isPlaying){
                mediaPlayer.start();
                isPlaying = true;
            }
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setLooping(getRepeatMode());
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    playNextSong();

                    return true;
                }
            });
        }
    }

    public class LocalBinder extends Binder{

        public PlayerService getService(){
            return PlayerService.this;
        }
    }

    public int getMusicPosition(){
        int currentPosition=0;
        if(isPlaying){
            currentPosition = mediaPlayer.getCurrentPosition();
        }

        return currentPosition;
    }

    public void setMusicPosition(int position){
        mediaPlayer.seekTo(position);
    }

    public int setMusicPause(){
        int state = 0;
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            state = SONG_IN_PAUSE;
            isPlaying = false;
            countDownTimer.cancel();
            countDownTimer.start();
        }else{
            mediaPlayer.start();
            state = SONG_IS_PLAYING;
            isPlaying = true;
            countDownTimer.cancel();
        }

        startNotificationForeground();

        return state;
    }

    CountDownTimer countDownTimer = new CountDownTimer(10,10) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            stopForeground(false);
            stopSelf();
        }
    };


    public int setMusicRepeatMode(){
        if(looping==SONG_IS_NOT_LOOPING){
            mediaPlayer.setLooping(false);
            looping = SONG_IS_LOOPING_ALL;
        }else if(looping == SONG_IS_LOOPING_ALL){
            mediaPlayer.setLooping(true);
            looping = SONG_IS_LOOPING_ONE;
        }else if(looping == SONG_IS_LOOPING_ONE){
            looping = SONG_IS_NOT_LOOPING;
            mediaPlayer.setLooping(false);
        }

        return looping;
    }

    private boolean getRepeatMode(){
        if(looping==SONG_IS_LOOPING_ONE){
            return true;
        }else{
            return false;
        }
    }

    public int getLooping(){
        return looping;
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public void playNextSong(){
        if(listOfSong.size()>0){

            updatePosition(nextSongPosition);

            loadAndPlayMusic(currentSongPosition);

            startNotificationForeground();
        }

    }

    public void playPreviousSong(){
        if(listOfSong.size()>0){

            if(mediaPlayer.getCurrentPosition()<2000){
                updatePosition(previousSongPosition);
            }

            loadAndPlayMusic(currentSongPosition);

            startNotificationForeground();
        }
    }

    public int setRandomMode(){
        if(randomMode==RANDOM_PLAYING_DISABLED) {
            randomMode = RANDOM_PLAYING_ENABLED;
            updatePosition(currentSongPosition);
        } else{
            randomMode = RANDOM_PLAYING_DISABLED;
            updatePosition(currentSongPosition);
            listOfPlayedSongPosition.clear();
        }


       return randomMode;
    }

    public int getRandomPlaying(){
        return randomMode;
    }

    public class ServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(PlayerService.ACTION_PLAY)){
                if(playingMusic !=null){
                    setMusicPause();
                }
            }
            if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                startService(new Intent(getBaseContext(),PlayerService.class));
            }
            if(intent.getAction().equals(ACTION_NEXT)){
                playNextSong();
            }
            if(intent.getAction().equals(ACTION_PREVIOUS)){
                playPreviousSong();
            }
        }


    }
}
