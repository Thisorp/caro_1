package com.example.caro_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlayingMusic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton buttonOnePlayer = findViewById(R.id.button_one_player);
        ImageButton buttonTwoPlayers = findViewById(R.id.button_two_players);
        ImageButton buttonExit = findViewById(R.id.button_exit);
        ImageButton buttonToggleMusic = findViewById(R.id.button_toggle_music);

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.bg_sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        buttonOnePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AIActivity.class);
                startActivity(intent);
            }
        });

        buttonTwoPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mediaPlayer != null) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                }
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        buttonToggleMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayingMusic) {
                    mediaPlayer.pause();
                    buttonToggleMusic.setImageResource(R.drawable.sound_off);
                } else {
                    mediaPlayer.start();
                    buttonToggleMusic.setImageResource(R.drawable.sound_on);
                }
                isPlayingMusic = !isPlayingMusic;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
