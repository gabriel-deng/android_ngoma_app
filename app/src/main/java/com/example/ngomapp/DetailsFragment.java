package com.example.ngomapp;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngomapp.Models.Song;
import com.example.ngomapp.ui.home.HomeViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;


public class DetailsFragment extends Fragment {

ImageView imgplaying, imgnext, imgprevious;
MediaPlayer mediaPlayer;
boolean isPlaying=false;
TextView title, description;
private HomeViewModel homeViewModel;
Song song;

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

        imgplaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaySong();

            }
        });

        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               homeViewModel.getNext()
                       .observe(requireActivity(), song1 ->{
                         song = song1;
                         changeSong();

               });
            }
        });
        imgprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.getPrevious()
                        .observe(requireActivity(), song1 ->{
                            song = song1;
                            changeSong();

                        });
            }
        });
        mediaPlayer= new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()

        );
        homeViewModel= new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        homeViewModel.getSongs().observe(requireActivity(), songs -> {
            Toast.makeText(getActivity(), "there are"+ songs.size()+ "songs", Toast.LENGTH_SHORT).show();
        });

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
            try {

                mediaPlayer.setDataSource(getActivity().getApplicationContext(), Uri.parse(song.getData()));
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

            mediaPlayer.stop();
            mediaPlayer.release();
        }

    public  void  PlaySong(){
        if(isPlaying){
          StopSong();
        }
        else {
            mediaPlayer.start();
            isPlaying= true;
            imgplaying.setImageResource(R.drawable.ic_pause);
        }
    }

    public void  StopSong(){
        mediaPlayer.pause();
        isPlaying=false;
        imgplaying.setImageResource(R.drawable.ic_playing);
    }
    public  void changeSong(){
        if(mediaPlayer.isPlaying()){
            StopSong();
            mediaPlayer.stop();

        }
        try {
            mediaPlayer.reset();

        }catch (Exception e){
            e.printStackTrace();
        }
        setupsong();
        PlaySong();
    }

}