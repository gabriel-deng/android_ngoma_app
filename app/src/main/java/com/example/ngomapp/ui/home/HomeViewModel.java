package com.example.ngomapp.ui.home;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.hardware.lights.LightState;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ngomapp.Models.Song;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Integer> selectedposition= new MutableLiveData<>();
    private  MutableLiveData<List<Song>>songs;
    private  MutableLiveData<Song> selected= new MutableLiveData<Song>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Song>> getSongs(){
        if(songs==null){
            songs= new MutableLiveData<List<Song>>();
            loadSongs();
        }

        return songs;
    }

    private  void loadSongs(){
        // This where we get the songs from a content provider
        ContentResolver contentResolver=getApplication().getApplicationContext().getContentResolver();
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String Selection= MediaStore.Audio.Media.IS_MUSIC + "!=0";

        String sortOder= MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor= contentResolver.query(uri, null, Selection, null, sortOder);

        if(cursor !=null && cursor.getCount()>0){
            List<Song> oursongs= new ArrayList<>();
            while ((cursor.moveToNext())){
                String title= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String data= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String album= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                oursongs.add(new Song(title,data,album,artist));
            }
            songs.setValue(oursongs);
        }
        cursor.close();
    }
    public  void select(Song song, int position){
        selected.setValue(song);
        selectedposition.setValue(position);
    }
    public LiveData<Integer> getIndex(){return selectedposition;}

    public  LiveData<Song> getSelected(){
        return selected;
    }

    public LiveData<Song> getNext(){
        int position;
        if(selectedposition.getValue() > getSongs().getValue().size()){
            position=0;
        }
        else {
            position=selectedposition.getValue()+1;
        }

     Song song=getSongs().getValue().get(position);
     select(song, position);
     return  getSelected();
    }

    public LiveData<Song> getPrevious(){
        int position;
        if(selectedposition.getValue() ==0){
            position=0;
        }
        else {
            position=selectedposition.getValue()-1;
        }

        Song song=getSongs().getValue().get(position);
        select(song, position);
        return  getSelected();
    }
    }

