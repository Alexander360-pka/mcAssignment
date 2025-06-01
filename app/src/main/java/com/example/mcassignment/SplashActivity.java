package com.example.mcassignment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        VideoView videoView = findViewById(R.id.videoView);
        Button btnFindMatch = findViewById(R.id.btnFindMatch);


        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.findmatch;
        videoView.setVideoURI(Uri.parse(videoPath));

        videoView.start();


        videoView.setOnCompletionListener(mp -> goToMainActivity());
        btnFindMatch.setOnClickListener(v -> goToMainActivity());
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}