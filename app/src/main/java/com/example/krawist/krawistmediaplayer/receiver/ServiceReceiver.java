package com.example.krawist.krawistmediaplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.krawist.krawistmediaplayer.models.Musique;
import com.example.krawist.krawistmediaplayer.service.PlayerService;

public class ServiceReceiver extends BroadcastReceiver {

    PlayerService playerService;
    Musique musique = null;
    Context context = null;
    Intent intent = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent pleayerServiceIntent = new Intent(context,PlayerService.class);
        context.startService(new Intent(context,PlayerService.class));
        context.bindService(pleayerServiceIntent,connection,Context.BIND_AUTO_CREATE);

        this.context = context;
        this.intent = intent;

    }

    private void startVerification(){
        if(intent.getAction().equals(PlayerService.ACTION_PLAY)){
            if(musique!=null){
                playerService.setMusicPause();
            }
        }
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            context.startService(new Intent(context,PlayerService.class));
        }
        if(intent.getAction().equals(PlayerService.ACTION_NEXT)){
            playerService.playNextSong();
        }
        if(intent.getAction().equals(PlayerService.ACTION_PREVIOUS)){
            playerService.playPreviousSong();
        }
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder)service;
            playerService = binder.getService();
            musique = playerService.getPlayingMusique();
            startVerification();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
