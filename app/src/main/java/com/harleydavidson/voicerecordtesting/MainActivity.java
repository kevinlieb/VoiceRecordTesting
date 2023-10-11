package com.harleydavidson.voicerecordtesting;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "MainActivity";
    private static String fileName = null;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    AudioDeviceInfo[] adi;

    Button recordButton;
    Button playButton;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private boolean isPlaying = false;
    private boolean isRecording = false;

    TextView debugTextView;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);


        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.mp3";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(this);

        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        debugTextView = findViewById(R.id.debugTextView);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        adi = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        for(AudioDeviceInfo theDevice: adi) {
            debugTextView.append("\nAddress: " + theDevice.getAddress() + " type: " + theDevice.getType() + "\n");
        }

    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setPreferredDevice(adi[0]);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.recordButton) {
            if(isRecording) {
                recordButton.setText("RECORD");
                stopRecording();
                isRecording = false;
            }
            else {
                recordButton.setText("STOP RECORDING");
                startRecording();
                isRecording = true;

            }
        }
        if(v.getId() == R.id.playButton) {
            if(isPlaying) {
                playButton.setText("PLAY");
                stopPlaying();
                isPlaying = false;
            }
            else {
                playButton.setText("STOP PLAYING");
                startPlaying();
                isPlaying = true;
            }

        }
    }
}