package com.example.ngomapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngomapp.Models.Playlist;
import com.example.ngomapp.Models.Song;
import com.example.ngomapp.R;

import java.util.List;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context context;
    List<Playlist> songs;

    public PlaylistAdapter(Context context, List<Playlist> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return  new PlaylistAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Playlist playlist= songs.get(position);
        holder.txtname.setText(playlist.getName());
        holder.txtTracks.setText(playlist.getTracks()+ "Tracks");
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtname, txtTracks;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtname= itemView.findViewById(R.id.txt_playlist_name);
            txtTracks= itemView.findViewById(R.id.txt_playlist_tracks);
        }
    }
}
