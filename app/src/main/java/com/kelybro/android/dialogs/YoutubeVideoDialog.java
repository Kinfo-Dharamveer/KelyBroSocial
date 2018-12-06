package com.kelybro.android.dialogs;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kelybro.android.R;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;


/**
 * Created by krishna on 09/04/18.
 */

public class YoutubeVideoDialog extends AppCompatActivity {
    String youtubelink;
    YouTubePlayerView youTubePlayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_youtube);
        youtubelink = getIntent().getStringExtra("link");
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        String videoId = youtubelink.replace("https://www.youtube.com/watch?v=","");
                        videoId = videoId.replace("https://youtu.be/","");
                        initializedYouTubePlayer.loadVideo(videoId, 0);
                        initializedYouTubePlayer.play();
                    }
                });
            }
        }, true);
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen(){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            @Override
            public void onYouTubePlayerExitFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(youTubePlayerView.isFullScreen())
            youTubePlayerView.exitFullScreen();
        //else
            //youTubePlayerView.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        youTubePlayerView.release();
    }
}
