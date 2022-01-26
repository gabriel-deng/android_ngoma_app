package com.example.ngomapp;

import static com.example.ngomapp.ui.home.HomeFragment.Broadcast_PLAY_NEW_AUDIO;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngomapp.Adapters.SongsAdapter;
import com.example.ngomapp.Models.Song;
import com.example.ngomapp.Services.MyService;
import com.example.ngomapp.ui.home.HomeViewModel;
import com.example.ngomapp.utils.MessageManager;
import com.example.ngomapp.utils.StorageUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DetailsFragment extends Fragment {

ImageView imgplaying, imgnext, imgprevious;
MediaPlayer mediaPlayer;
boolean isPlaying=false;
TextView title, description;
boolean serviceBound=false;
private HomeViewModel homeViewModel;
Song song;
MessageManager messageManager;
NotificationManagerCompat notificationManager;
MyService player;

ArrayList<Song> songs = new ArrayList<>();


 int index=0;



    public DetailsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        title= view.findViewById(R.id.txt_song_title);
        description= view.findViewById(R.id.txt_song_details);
        imgplaying = view.findViewById(R.id.img_playing);
        imgnext = view.findViewById(R.id.img_next);
        imgprevious = view.findViewById(R.id.img_previous);

//      messageManager= new MessageManager(getActivity(), R.drawable.ic_music);
//      messageManager.creatnewNotificationChannel();
//      notificationManager= NotificationManagerCompat.from(getActivity());
//

//        imgplaying.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                try{
//                    notificationManager.notify(123, messageManager
//                            .setupNotificationForPlayButton("Now Playing").build());
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                PlaySong();
//
//
//            }
//        });

        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               homeViewModel.getNext()
                       .observe(requireActivity(), song1 ->{
                         song = song1;
                       setupsong();

               });
            }
        });
        imgprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.getPrevious()
                        .observe(requireActivity(), song1 ->{
                            song = song1;
                           setupsong();

                        });
            }
        });
//        mediaPlayer= new MediaPlayer();
//        mediaPlayer.setAudioAttributes(
//                new AudioAttributes.Builder()
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                        .setUsage(AudioAttributes.USAGE_MEDIA)
//                        .build()

//        );
        homeViewModel= new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        return view;

    }
        @Override
        public void onStart(){
            super.onStart();

//            loadSongInfo();

            homeViewModel.getSelected().observe(requireActivity(), song1 -> {
             song=song1;
             setupsong();
            });


        }

        private  void setupsong(){


        homeViewModel.getIndex().observe(requireActivity(),position->{
            index = position;
        });
        if(serviceBound){
            StorageUtils storage= new StorageUtils(getActivity().getApplicationContext());
            storage.storeAudioIndex(index);

            //Service is active so we need a broadcast to it
            Intent broadcastIntent= new Intent(Broadcast_PLAY_NEW_AUDIO);
            requireActivity().sendBroadcast(broadcastIntent);
        }else {
            homeViewModel.getSongs().observe(requireActivity(), songs1 -> {
                        songs.addAll(songs1);
                StorageUtils storage= new StorageUtils(getActivity().getApplicationContext());
                storage.storeAudio(songs);
                storage.storeAudioIndex(index);

                Intent playerIntent= new Intent(getActivity(),MyService.class);
                //       playerIntent.putExtra("DATA", data);

                requireActivity().startService(playerIntent);
                requireActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                    });


        }
            isPlaying= true;
            imgplaying.setImageResource(R.drawable.ic_pause);
//            try {
//
//
//                mediaPlayer.setDataSource(getActivity().getApplicationContext(), Uri.parse(song.getData()));
//                mediaPlayer.prepare();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            title.setText(song.getName());
            title.setText(song.getArtist()+","+song.getAlbum());
        }

//    private void loadSongInfo() {
//        if(getArguments().containsKey("DATA")){
//
//            try {
//                mediaPlayer.reset();
//                mediaPlayer.setDataSource(getActivity().getApplicationContext(), Uri.parse(getArguments().getString("DATA")));
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//
//        }
//
//        if(getArguments().containsKey("NAME")){
//            title.setText(getArguments().getString("NAME"));
//        }
//
//        if(getArguments().containsKey("ARTIST")){
//            description.setText(getArguments().getString("ARTIST"));
//        }
//    }



    @Override
        public void onStop(){
            super.onStop();

//            mediaPlayer.stop();
//            mediaPlayer.release();
        }

    public  void  PlaySong(){
        if(isPlaying){
            player.pauseMedia();
            isPlaying=true;
            imgplaying.setImageResource(R.drawable.ic_playing);

        }
        else {

            player.playmedia();
            isPlaying=false;
            imgplaying.setImageResource(R.drawable.ic_pause);

        }
    }

//    public void  StopSong(){
//        mediaPlayer.pause();
//        isPlaying=false;
//        imgplaying.setImageResource(R.drawable.ic_playing);
//    }
    public  void changeSong(){
//        if(mediaPlayer.isPlaying()){
//            StopSong();
//            mediaPlayer.stop();
//
//        }
//        try {
//            mediaPlayer.reset();
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        setupsong();
   //     PlaySong();
    }

    private ServiceConnection serviceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.localBinder binder = (MyService.localBinder) service;
            player = binder.getService();

            serviceBound = true;

            Toast.makeText(getActivity(), "Service Bound", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound=false;

        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(serviceBound){
            requireActivity().unbindService(serviceConnection);
            player.stopSelf();
        }
    }
}