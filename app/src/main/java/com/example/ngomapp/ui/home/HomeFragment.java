package com.example.ngomapp.ui.home;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngomapp.Adapters.SongsAdapter;
import com.example.ngomapp.Models.Song;
import com.example.ngomapp.R;
import com.example.ngomapp.databinding.FragmentHomeBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    List<Song> songs= new ArrayList<>();
    Context context= getContext();
    SongsAdapter songsAdapter;
    RecyclerView songsRecyclerview;
    TextInputEditText inputsearch;

    MediaPlayer mediaPlayer;

    View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

         root = inflater.inflate(R.layout.fragment_home, container, false);
        inputsearch= root.findViewById(R.id.input_search);
        songsRecyclerview= root.findViewById(R.id.songs_recyclerview);
        songsRecyclerview.setNestedScrollingEnabled(true);
        songsRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        mediaPlayer= new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()

        );
        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        homeViewModel.getSongs().observe(requireActivity(), songs1 -> {
            songs.clear();
            songs.addAll(songs1);
        });
        songsAdapter = new SongsAdapter(context, songs, this);
        songsRecyclerview.setAdapter(songsAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

  //      mediaPlayer = mediaPlayer.create(getActivity().getApplicationContext(), R.raw.song1);
     //   mediaPlayer.start();

        verifyPermissions();
    }

    @Override
    public void onStop() {
        super.onStop();
       try {
           mediaPlayer.stop();
       }catch (Exception e){
           e.printStackTrace();

       }
        mediaPlayer.release();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    public void playSong(String data){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getActivity().getApplicationContext(), Uri.parse(data));
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (Exception e){

            e.printStackTrace();
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            Log.d("TEST::", "playSong: "+e.getMessage());
        }


    }

    public void  stopSong(){

        try {
            mediaPlayer.pause();
        }catch (Exception e){
            e.printStackTrace();

        }

    }

    public void goTodetails(Song song, int position){
//        Bundle bundle= new Bundle();
//        bundle.putString("NAME", song.getName());
//        bundle.putString("DATA", song.getData());
//        bundle.putString("ALBUM", song.getAlbum());
//        bundle.putString("ARTIST", song.getArtist());


        homeViewModel.select(song, position);
        Navigation.findNavController(root).navigate(R.id.action_fragment_details);
    }

    public void  verifyPermissions(){
        String[] permissions= {Manifest.permission.READ_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permissions[0])== PackageManager.PERMISSION_GRANTED){

        }
        else{
            ActivityCompat.requestPermissions(getActivity(),permissions,123);
        }
    }



}