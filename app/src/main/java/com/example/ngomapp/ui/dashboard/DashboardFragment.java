package com.example.ngomapp.ui.dashboard;

import static java.time.LocalDateTime.now;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.ngomapp.Adapters.PlaylistAdapter;
import com.example.ngomapp.Models.Playlist;
import com.example.ngomapp.R;
import com.example.ngomapp.databinding.FragmentDashboardBinding;
import com.example.ngomapp.utils.ObjectBox;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class DashboardFragment extends Fragment {
    PlaylistAdapter playlistAdapter;
    List<Playlist> playlists= new ArrayList<>();
    Box<Playlist> playlistBox= ObjectBox.get().boxFor(Playlist.class);
    LinearLayout layoutEmptyState;
    RecyclerView PlaylistRecyclerview;

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        PlaylistRecyclerview= binding.playlistsRecyclerview;
        PlaylistRecyclerview.setNestedScrollingEnabled(true);
        PlaylistRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(),2));

        playlists.add(new Playlist("smooth Jams"));
        playlists.add(new Playlist("Rocks"));
        playlistAdapter= new PlaylistAdapter(getActivity(), playlists);
        PlaylistRecyclerview.setAdapter(playlistAdapter);

         layoutEmptyState= binding.layoutEmptyState;
//        layoutEmptyState.setVisibility(View.GONE);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        playlists.addAll(playlistBox.getAll());

//        playlists.add(new Playlist("smooth Jams"));
//        playlists.add(new Playlist("Rocks"));
//        playlistAdapter.notifyDataSetChanged();


        if(playlists.size() ==0){
            layoutEmptyState.setVisibility(View.VISIBLE);
            PlaylistRecyclerview.setVisibility(View.GONE);
        }
        else {
            layoutEmptyState.setVisibility(View.GONE);
            PlaylistRecyclerview.setVisibility(View.VISIBLE);
            playlistAdapter.notifyDataSetChanged();

        }
    }
}