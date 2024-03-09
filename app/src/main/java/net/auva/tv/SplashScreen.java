package net.auva.tv;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        videoView = findViewById(R.id.video_view);
        String path = "android.resource://net.auva.tv/" + R.raw.auva_promo;
        Uri uri = Uri.parse(path);
        videoView.setVideoURI(uri);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
           /* FirebaseUser prevUser = FirebaseAuth.getInstance().getCurrentUser();
            if (prevUser != null) {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }*/
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
        videoView.setOnPreparedListener(MediaPlayer::start);
    }
}
