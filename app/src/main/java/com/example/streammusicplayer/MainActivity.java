package com.example.streammusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences ;

import java.util.ArrayList;
import java.util.List;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.streammusicplayer.Activities.ListMusicActivity;
import com.example.streammusicplayer.Activities.PlayMusicActivity;

import com.example.streammusicplayer.Model.Record;
import com.example.streammusicplayer.Services.AnalyseurService;
import com.example.streammusicplayer.Services.SpeechService;
import com.zeroc.Ice.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button start , stop ;
   // private MediaRecorder mr  ;
    Record record = new Record();
    private String outputFile ;
    private SpeechService speechService ;
    private AnalyseurService analyseurService ;
    final int REQUEST_CODE_PERMISSION = 10;
    private SharedPreferences sharedpreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws UnsupportedEncodingException {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start  = findViewById(R.id.start);
        stop   = findViewById(R.id.stop);
        speechService = new SpeechService(this);
        analyseurService = new AnalyseurService(this) ;

        if(!checkPermissionFromDevice()){
            requestPermission();
        }else{ /* je vais empecher l'appli de se lancer  */ }

        start.setEnabled(true);
        stop.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.amr";

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                start.setEnabled(false);
                stop.setEnabled(true);
                record.StartRecord(outputFile);


            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              record.StopRecord();
            
              speechService.uploadFileToNodeJs(outputFile,"") ;

              start.setEnabled(true);
              stop.setEnabled(false);

            }
        });

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){

            case 10 : {

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    System.out.println("granted");
                }else{
                    System.out.println("denied");
                }
            }
            break ;
        }

    }

    private boolean checkPermissionFromDevice(){

        int exteral_storage_permisson = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ;
        int record_audio_permisson = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ;
        return  exteral_storage_permisson == PackageManager.PERMISSION_GRANTED
                && record_audio_permisson == PackageManager.PERMISSION_GRANTED ;

    }

}