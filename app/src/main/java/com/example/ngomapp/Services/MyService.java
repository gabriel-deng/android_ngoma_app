package com.example.ngomapp.Services;

import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.example.ngomapp.Models.Song;
import com.example.ngomapp.R;
import com.example.ngomapp.ui.home.HomeFragment;
import com.example.ngomapp.utils.MessageManager;
import com.example.ngomapp.utils.StorageUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MyService extends Service implements
     MediaPlayer.OnCompletionListener,
     MediaPlayer.OnPreparedListener,
     MediaPlayer.OnErrorListener,
     MediaPlayer.OnSeekCompleteListener,
     MediaPlayer.OnInfoListener,
     MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {
    // ACTION CONSTANTS
    private static final String ACTION0N_PLAY= "com.example.ngomapp.ACTION_PLAY";
    private static final String ACTION_PAUSE= "com.example.ngomapp.ACTION_PAUSE";
    private static final String ACTION_PREVIOUS= "com.example.ngomapp.ACTION_PREVIOUS";
    private static final String ACTION_NEXT= "com.example.ngomapp.ACTION_NEXT";
    private static final String ACTION_STOP= "com.example.ngomapp.ACTION_STOP";

    // Notification ID
    private static final int NOTIFICATION_ID= 101;

    //Media Session Variable
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat mediaControllerCompat;
    private  MediaControllerCompat.TransportControls transportControls;



    MediaPlayer mediaPlayer;
    String mediaFile;
    private int resumePosition;
     private AudioManager audioManager;
     //List of available audio file

    private ArrayList<Song> audioList;
    private  int audioIndex= -1;
    private  Song activeAudio;

     public class  localBinder extends Binder{
         // here we define a method that returns an instance of the mediaplayer service we have created

         public MyService getService(){
             return  MyService.this;
         }
     }

     private  final  IBinder iBinder= new localBinder();
    public MyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Listen for the new Audio to play -- BroadcastReceiver
        register_playNewAudio();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
          try{
      //        mediaFile= intent.getStringExtra("DATA");
              StorageUtils storage= new StorageUtils(getApplicationContext());
              audioList=storage.loadAudio();
              audioIndex=storage.loadAudioIndex();

              if(audioIndex != -1 && audioIndex< audioList.size()){
                  activeAudio= audioList.get(audioIndex);
                  mediaFile= activeAudio.getData();

              }
              else {
                  stopSelf();
              }

          }
          catch (NullPointerException e){
              stopSelf();
          }
          if(!requestFocus()){
              stopSelf();
          }
          if(mediaSessionManager==null){
              try{
                  initMediaSession();
                  initMediaplayer();
              }catch (RemoteException e){
                  e.printStackTrace();
                  stopSelf();
              }
              buildNotification(true);
          }
          //Handle intent action from MediaSession.TransportControls
          handleIncomingActions(intent);


        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
            return iBinder;
    //    throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_GAIN:
                //resume playback
                if(mediaPlayer==null) initMediaplayer();
                else if(!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f,1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                //lost focus for an amount of time: stop playing
                if(mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer= null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                //lost but we are able to tell for how long we stop playback but keep media player because playback is likely to resume

                if(mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.setVolume(0.1f,0.1f);
                }
                break;
        }

    }

    private  boolean requestFocus(){
        audioManager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int result=audioManager.requestAudioFocus(
                    this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
            );
        if(result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            return  true;
        }
        return false;
    }

    private boolean removeAudioFocus(){
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED== audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        pauseMedia();

        stopSelf();

    }

    @Override
    public boolean onError(MediaPlayer mp, int error, int extra) {
        switch (error){
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MEDIA", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK: "+ extra);
                break;

            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MEDIA", "MEDIA ERROR SERVER DIED: "+extra);
                break;

            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MEDIA", "MEDIA ERROR UNKNOWN: "+extra);
                break;


        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        playmedia();

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
    private  void initMediaplayer(){
        mediaPlayer= new MediaPlayer();

        // set up the mediaplayer event listeners

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);

        // here we reset the media player so that it is not pointing to another data source(song/audio)
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try{
            mediaPlayer.setDataSource(activeAudio.getData());

        }catch (IOException e){
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    public   void playmedia(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    public void stopmedia(){
        //if the media player had not been initialised we return which exists the function immediately
        if(mediaPlayer==null) return;
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            //we save the current paused position
            resumePosition= mediaPlayer.getCurrentPosition();
        }
    }

    private  void  resumeMedia(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }
    public void pauseMedia(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    private void skipToPrevious(){
        if(audioIndex==0){
            //if first in playlist
            //set the index of the last in audiolist
            audioIndex=audioList.size()-1;
            activeAudio= audioList.get(audioIndex);
        }
        else {
            activeAudio=audioList.get(--audioIndex);
        }

        new StorageUtils(getApplicationContext())
                .storeAudioIndex(audioIndex);
        stopmedia();

        mediaPlayer.reset();
        initMediaplayer();
    }

    private void skipToNext(){
        if(audioIndex==audioList.size()-1){
            //if last in playlist

            audioIndex=0;
            activeAudio=audioList.get(audioIndex);
        }else{
            //get next in playlist
            activeAudio=audioList.get(++audioIndex);
        }

        new StorageUtils(getApplicationContext())
                .storeAudioIndex(audioIndex);
        stopmedia();

        mediaPlayer.reset();
        initMediaplayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mediaPlayer !=null){
            stopmedia();
            mediaPlayer.release();
        }

        removeAudioFocus();
        removeNotification();

        unregisterReceiver(playNewAudio);

        new StorageUtils(getApplicationContext()).clearCachedAudioPlaylist();
    }

    private BroadcastReceiver playNewAudio= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            audioIndex= new StorageUtils(getApplicationContext())
                    .loadAudioIndex();

            if(audioIndex != -1 && audioIndex<audioList.size()){
                //Index is in a valid range
                activeAudio= audioList.get(audioIndex);
                mediaFile= activeAudio.getData();
            }
            else{
                stopSelf();
            }

            stopmedia();
            mediaPlayer.reset();
            initMediaplayer();

            //TODO: Show Notification

        }
    };

    private void register_playNewAudio(){
        // Here we register the receiver using an intent filter
        //the saME WE REGISTER OUR Launcher activity using intent filters

        IntentFilter filter= new IntentFilter(HomeFragment.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    /**
     * media session and notification actions
     */

    private void removeNotification(){
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    //function to get pending intent for the different playback actions

    private PendingIntent playbackActions(int actionNumber) {
        Intent playbackAction = new Intent(this, MyService.class);

        switch (actionNumber) {
            case 0:
                playbackAction.setAction(ACTION0N_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);

            case 1:
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);

            case 2:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);

            case 3:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);

            case 4:
                playbackAction.setAction(ACTION_STOP);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;

        }
        return  null;
    }
    private void buildNotification(Boolean playbackStatus){
        /**
         * Notification Actions=> playbackAction()
         * 0->play
         * 1->pause
         * 2->next
         * 3->previous
         */

        int notificationButtonIcon= android.R.drawable.ic_media_pause;
        PendingIntent play_pauseAction= null;

        if(playbackStatus){
            notificationButtonIcon= android.R.drawable.ic_media_pause;
            //create the pause action intent
            play_pauseAction=playbackActions(1);

        }
        else {
            notificationButtonIcon= android.R.drawable.ic_media_play;
            play_pauseAction=playbackActions(0);
        }

        Bitmap largeIcon= BitmapFactory.decodeResource(getResources(),R.drawable.onboard);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this, MessageManager.CHANNEL_ID)
                .setShowWhen(false)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0,1,2))
                .setSmallIcon(android.R.drawable.ic_media_pause)
                .setLargeIcon(largeIcon)
                .setContentText(activeAudio.getName())
                .setColor(getResources().getColor(R.color.brown))
                .setContentTitle(activeAudio.getArtist())
                .setContentInfo(activeAudio.getAlbum())
                .addAction(android.R.drawable.ic_media_previous,"previous",playbackActions(3))
                .addAction(notificationButtonIcon,"paused",play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next",playbackActions(2));


        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                  .notify(NOTIFICATION_ID, builder.build());
    }

    private void initMediaSession() throws RemoteException{
        if(mediaSessionManager !=null) return; //media session manager

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);

        //Create new mediasession
        mediaSession= new MediaSessionCompat(getApplicationContext(),"AudioPlayer");

        //Get controls from the session to help control the media

        transportControls= mediaSession.getController().getTransportControls();

        //set media session ready to receive media commands
        mediaSession.setActive(true);

        //Indicate that the mediasession handles the transport controls through its mediasessionCompact.Callback
        mediaSession.setFlags(FLAG_HANDLES_TRANSPORT_CONTROLS);

        //set mediaSession's Metadata
        updateMetaData();

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();

                resumeMedia();
                buildNotification(true);
            }

            @Override
            public void onPause() {
                super.onPause();

                pauseMedia();
                buildNotification(false);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();

                skipToNext();
                updateMetaData();
                buildNotification(true);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateMetaData();
                buildNotification(true);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                stopSelf();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }
        });
    }

    private void updateMetaData(){
        Bitmap albumArt=  BitmapFactory.decodeResource(getResources(),R.drawable.onboard);

        mediaSession.setMetadata((new MediaMetadataCompat.Builder().putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getName()).build()));
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);

    }

    private void handleIncomingActions(Intent playbackAction){
        if(playbackAction==null || playbackAction.getAction()==null)return;

        String actionString=playbackAction.getAction();

        if(actionString.equalsIgnoreCase(ACTION0N_PLAY)){
            transportControls.play();
        }
        else if(actionString.equalsIgnoreCase(ACTION_PAUSE)){
            transportControls.pause();
        }
        else if(actionString.equalsIgnoreCase(ACTION_NEXT)){
            transportControls.skipToNext();

        }
        else if(actionString.equalsIgnoreCase(ACTION_PREVIOUS)){
            transportControls.skipToPrevious();
        }
        else if(actionString.equalsIgnoreCase(ACTION_STOP)){
            transportControls.stop();
        }
    }
}