package com.example.ngomapp.ui.home;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
import androidx.core.app.NotificationManagerCompat;
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
import com.example.ngomapp.Services.MyService;
import com.example.ngomapp.databinding.FragmentHomeBinding;
import com.example.ngomapp.utils.MessageManager;
import com.example.ngomapp.utils.StorageUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    ArrayList<Song> songs= new ArrayList<>();
    Context context= getContext();
    SongsAdapter songsAdapter;
    RecyclerView songsRecyclerview;
    TextInputEditText inputsearch;
    MessageManager messageManager;
    NotificationManagerCompat notificationManager;
    MediaPlayer mediaPlayer;
    View root;
    boolean serviceBound=false;


    MyService player;
    public static final String Broadcast_PLAY_NEW_AUDIO= "com.example.ngomapp.PlayNewAudio";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(getArguments()!=null){
            serviceBound=getArguments().getBoolean("SERVICE");
        }
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

         root = inflater.inflate(R.layout.fragment_home, container, false);
        inputsearch= root.findViewById(R.id.input_search);
        songsRecyclerview= root.findViewById(R.id.songs_recyclerview);
        songsRecyclerview.setNestedScrollingEnabled(true);
        songsRecyclerview.setLayoutManager(new LinearLayoutManager(context));
//        mediaPlayer= new MediaPlayer();
//        mediaPlayer.setAudioAttributes(
//                new AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .build()

 //       );
        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verifyPermissions();

        homeViewModel.getSongs().observe(requireActivity(), songs1 -> {
            songs.clear();
            songs.addAll(songs1);
        });
        songsAdapter = new SongsAdapter(context, songs, this);
        songsRecyclerview.setAdapter(songsAdapter);

        messageManager= new MessageManager(getActivity(), R.drawable.ic_music);
        messageManager.createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(getActivity());


    }

    @Override
    public void onStart() {
        super.onStart();

  //      mediaPlayer = mediaPlayer.create(getActivity().getApplicationContext(), R.raw.song1);
     //   mediaPlayer.start();

        verifyPermissions();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

//    public void playSong(String data, String name){
//        try {
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(getActivity().getApplicationContext(), Uri.parse(data));
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//
//            Bitmap bitmap= BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.onboard);
//      //      notificationManager.notify(407, messageManager.setupNotification("playing", name).build());
//            notificationManager.notify(407, messageManager
//            .setupNotifications("playing", name, bitmap).build());
//        }
//        catch (Exception e){
//
//            e.printStackTrace();
//            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
//            Log.d("TEST::", "playSong: "+e.getMessage());
//        }
//
//
//    }

    public  void  playSong(String data, String name, int index){
        if(!serviceBound){
            StorageUtils storage= new StorageUtils(getActivity().getApplicationContext());
            storage.storeAudio(songs);
            storage.storeAudioIndex(index);

            Intent playerIntent= new Intent(getActivity(),MyService.class);
     //       playerIntent.putExtra("DATA", data);

            requireActivity().startService(playerIntent);
            requireActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else {
            StorageUtils storage= new StorageUtils(getActivity().getApplicationContext());
            storage.storeAudioIndex(index);

            //Service is active so we need a broadcast to it
            Intent broadcastIntent= new Intent(Broadcast_PLAY_NEW_AUDIO);
            requireActivity().sendBroadcast(broadcastIntent);
        }
//        notificationManager.notify(407, messageManager.setupNotification("Now Playing...",name).build());
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
        Bundle bundle= new Bundle();
        bundle.putBoolean("SERVICE", serviceBound);
        Navigation.findNavController(root).navigate(R.id.action_fragment_details, bundle);
    }

    public void  verifyPermissions(){
        String[] permissions= {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MEDIA_CONTENT_CONTROL,Manifest.permission.READ_PHONE_STATE};

        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permissions[0])== PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permissions[1])== PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permissions[2])== PackageManager.PERMISSION_GRANTED

        ){

        }
        else{
            ActivityCompat.requestPermissions(getActivity(),permissions,123);
        }
    }

    private ServiceConnection serviceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.localBinder binder= (MyService.localBinder) service;
            player=binder.getService();

            serviceBound=true;

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