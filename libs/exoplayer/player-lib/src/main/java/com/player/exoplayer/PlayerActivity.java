package com.player.exoplayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;



public class PlayerActivity extends AppCompatActivity {
    private static final String TAG = "PlayerActivity";
    PlayerView playerView;
    ImageView fullScreen;
    boolean isFullScreen = false;
    SimpleExoPlayer player;
    ProgressBar progressBar;
    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector trackSelector;

    String[] speed = {"360","480","720"};
    String CHANNEL_NAME="";
    String CHANNEL_URL="";
    String CHANNEL_URL360="";
    String CHANNEL_URL480="";
    String CHANNEL_URL720="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
//        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        if (getSupportActionBar() != null) {
//                        getSupportActionBar().hide();
//                    }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);


        Intent intent = getIntent();
        CHANNEL_URL = intent.getStringExtra("channel_url");
        CHANNEL_URL360 = intent.getStringExtra("channel_url360");
        CHANNEL_URL480 = intent.getStringExtra("channel_url480");
        CHANNEL_URL720 = intent.getStringExtra("channel_url720");
        CHANNEL_NAME = intent.getStringExtra("channel_name");

        TextView channelName =findViewById(R.id.txt_name);
        channelName.setText(CHANNEL_NAME);

        ImageButton buBack = findViewById(R.id.bu_back);
        buBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        trackSelector = new DefaultTrackSelector(this);

        player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        playerView = findViewById(R.id.playerView);
        playerView.setPlayer(player);

//        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory();
//        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).
//                createMediaSource(MediaItem.fromUri(CHANNEL_URL720));
        player.setMediaItem(MediaItem.fromUri(CHANNEL_URL720));
        player.prepare();
        player.setPlayWhenReady(true);
//        System.out.println("==================================================================");

//System.out.println(player.getVideoSize().pixelWidthHeightRatio);

        TextView speedTxt = playerView.findViewById(R.id.speed);
        speedTxt.setText("720");
//        TextView txtName = playerView.findViewById(R.id.txt_name);
//        txtName.setText(CHANNEL_NAME);
        playerView = findViewById(R.id.playerView);
        fullScreen = playerView.findViewById(R.id.exo_fullscreen_button);
        ImageView speedBtn = playerView.findViewById(R.id.exo_playback_speed);
//        ImageView backBtn = playerView.findViewById(R.id.bu_back);
        progressBar = findViewById(R.id.progressBar);
//        speedBtn.setVisibility(View.GONE);
        speedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
                builder.setTitle("الجودة");
                builder.setItems(speed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        if (which==0){

                            speedTxt.setVisibility(View.VISIBLE);
//                            speedTxt.setVisibility(View.GONE);
                            speedTxt.setText("360");
                            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory();
                            HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).
                                    createMediaSource(MediaItem.fromUri(CHANNEL_URL360));
                            player.setMediaSource(mediaSource);
                            player.prepare();
                            player.setPlayWhenReady(true);


                        }
                        if (which==1){
                            speedTxt.setVisibility(View.VISIBLE);
//                            speedTxt.setVisibility(View.GONE);
                            speedTxt.setText("480");
                            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory();
                            HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).
                                    createMediaSource(MediaItem.fromUri(CHANNEL_URL480));
                            player.setMediaSource(mediaSource);
                            player.prepare();
                            player.setPlayWhenReady(true);


                        }
                        if (which==2){
                            speedTxt.setVisibility(View.VISIBLE);
//                            speedTxt.setVisibility(View.GONE);
                            speedTxt.setText("720");
                            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory();
                            HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).
                                    createMediaSource(MediaItem.fromUri(CHANNEL_URL720));
                            player.setMediaSource(mediaSource);
                            player.prepare();
                            player.setPlayWhenReady(true);

                        }
//                        if (which==3){
//
//                            speedTxt.setVisibility(View.VISIBLE);
////                            speedTxt.setVisibility(View.GONE);
//                            speedTxt.setText("1080");
//
//                            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory();
//                            HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).
//                                    createMediaSource(MediaItem.fromUri(CHANNEL_URL));
//                            player.setMediaSource(mediaSource);
//                            player.prepare();
//                            player.setPlayWhenReady(true);
//
//
//
//                        }



                    }
                });
                builder.show();





            }
        });

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                if (isFullScreen) {
//                    System.out.println("+++++++++");
//                    System.out.println(isFullScreen);
//                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);


//                    if (getSupportActionBar() != null) {
//                        getSupportActionBar().hide();
//                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
//                    params.width = params.MATCH_PARENT;
//                    params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
//
//                    playerView.setLayoutParams(params);
//                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

//                    Toast.makeText(MainActivity.this, "We are Now going back to normal mode.", Toast.LENGTH_SHORT).show();
                    isFullScreen = false;
                } else {
//                    System.out.println("--------");
//                    System.out.println(isFullScreen);
//                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
//                    if (getSupportActionBar() != null) {
//                        getSupportActionBar().hide();
//                    }



//                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
//                    params.width = params.MATCH_PARENT;
//                    params.height = params.MATCH_PARENT;
//                    playerView.setLayoutParams(params);

//                    playerView.setPlayer(player);
//                    player.retry();

//                    Toast.makeText(MainActivity.this, "We are going to FullScreen Mode.", Toast.LENGTH_SHORT).show();
                    isFullScreen = true;
                }
            }
        });
        playerView = findViewById(R.id.playerView);
//        ImageView setting = playerView.findViewById(R.id.exo_track_selection_view);
//        setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isShowingTrackSelectionDialog
//                        && TrackSelectionDialog.willHaveContent(trackSelector)) {
//                    isShowingTrackSelectionDialog = true;
//                    TrackSelectionDialog trackSelectionDialog =
//                            TrackSelectionDialog.createForTrackSelector(
//                                    trackSelector,
//                                    /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
//                    trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
//
//
//                }
//
//
//            }
//        });



        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                } else if (state == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                    playerView.setKeepScreenOn(true);
                } else {
                    progressBar.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);

                }
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Log.d(TAG, "onTimelineChanged: "+timeline.getPeriodCount() + reason);
                Player.Listener.super.onTimelineChanged(timeline, reason);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                switch (error.type) {
                    case ExoPlaybackException.TYPE_SOURCE:
                        Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().toString());
                        //TODO solve problem and delete prepare and when ready
//                        player.seekToDefaultPosition();
                        player.setPlayWhenReady(true);
                        player.prepare();
                        break;

                    case ExoPlaybackException.TYPE_RENDERER:
                        Log.e(TAG, "TYPE_RENDERER: " + error.getRendererException().getMessage());
                        break;

                    case ExoPlaybackException.TYPE_UNEXPECTED:
                        Log.e(TAG, "TYPE_UNEXPECTED: " + error.getUnexpectedException().getMessage());
                        break;
                }
            }
        });
        hideSystemUi();
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);}


    @Override
    protected void onResume() {
        super.onResume();
        player.seekToDefaultPosition();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        player.setPlayWhenReady(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }



}