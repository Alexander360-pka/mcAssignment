package com.example.mcassignment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FindMatch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Button btnFindMatch = findViewById(R.id.btnFindMatch);
        btnFindMatch.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }
}
