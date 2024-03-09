package net.auva.tv;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.BuildConfig;



public class WelcomeActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        button = findViewById(R.id.btnNext);
        textView = findViewById(R.id.app_version);
        textView.setText("Version " + BuildConfig.VERSION_NAME);
        button.setOnClickListener(view ->startSplashScreen());
    }

    private void startSplashScreen() {
        Intent intent = new Intent(WelcomeActivity.this, SendOTPActivity.class);
        startActivity(intent);
    }


}