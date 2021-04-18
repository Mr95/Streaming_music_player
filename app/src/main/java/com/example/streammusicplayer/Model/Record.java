package com.example.streammusicplayer.Model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.io.Serializable;

public class Record {

    MediaRecorder mr ;

    public Record(){
      //  mr = new MediaRecorder();
    }

    public void StartRecord(String outputFile){

        mr = new MediaRecorder();
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);

        mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        mr.setOutputFile(outputFile);

        try {

            mr.prepare();
            mr.start();

        } catch (IllegalStateException ise) {
        } catch (IOException io) {}

    }

    public void StopRecord(){

        mr.stop();
        mr.release();
        mr = null;

    }

}