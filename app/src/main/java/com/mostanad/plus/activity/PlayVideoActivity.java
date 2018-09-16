package com.mostanad.plus.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.longtailvideo.jwplayer.JWPlayerFragment;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;

import com.mostanad.plus.R;

public class PlayVideoActivity extends AppCompatActivity {


    String videoUrl = "";
    private JWPlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        Bundle extras = getIntent().getExtras();

        videoUrl = extras.getString("url");

        // Get a handle to the JWPlayerFragment
        JWPlayerFragment fragment = (JWPlayerFragment) getFragmentManager().findFragmentById(R.id.fm_jwplayer);
        // Get a handle to the JWPlayerView
        playerView = fragment.getPlayer();
        // Create a PlaylistItem
        PlaylistItem video = new PlaylistItem(videoUrl);

        // Load a stream into the player
        playerView.load(video);
        playerView.play();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
