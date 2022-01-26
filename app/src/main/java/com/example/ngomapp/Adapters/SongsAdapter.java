package com.example.ngomapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngomapp.Models.Song;
import com.example.ngomapp.R;
import com.example.ngomapp.ui.home.HomeFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class SongsAdapter  extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    Context context;
    List<Song> songs;
    HomeFragment homeFragment;

    public SongsAdapter(Context context, List<Song> songs, HomeFragment homeFragment) {
        this.context = context;
        this.songs = songs;
        this.homeFragment= homeFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(songs.get(position).getName());
        holder.song_name=songs.get(position).getName();
        holder.isPlaying= songs.get(position).isPlaying();
        holder.data= songs.get(position).getData();
        holder.position=holder.getAdapterPosition();

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView imgmore;
        String data, song_name;
        boolean isPlaying;
        Integer position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            name= itemView.findViewById(R.id.txt_song_name);
            imgmore= itemView.findViewById(R.id.img_more);

            imgmore.setOnClickListener(v -> {

                // TODO: Create Bottom Dialog with Song Options

                Snackbar.make(v, "Options: Coming Soon", Snackbar.LENGTH_LONG).show();
                homeFragment.goTodetails(songs.get(getAdapterPosition()), getAdapterPosition());


            });

            itemView.setOnClickListener(v -> {

//            if(isPlaying){
//                homeFragment.stopSong();
//                Snackbar.make(v,"stopped", Snackbar.LENGTH_LONG).show();
//                isPlaying= false;
//            }
//            else {
                homeFragment.playSong(data, song_name, getAdapterPosition());
                Snackbar.make(v,"Playing", Snackbar.LENGTH_LONG).show();
                isPlaying = true;

//            }

            });

        }
    }


}
