package com.example.streammusicplayer.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.streammusicplayer.Model.MySharedPrefrences;
import com.example.streammusicplayer.Model.Record;
import com.example.streammusicplayer.R;
import com.example.streammusicplayer.Services.SpeechService;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class PlayMusicActivity extends AppCompatActivity {

    private ImageView imageview ;
    private SeekBar seekbar ;
    private TextView tempsecoule , tempsrestant ;
    private Button  Pause ,start , stop;
    private ImageButton commande ;
    private /*static*/ MediaPlayer mediaplayer = new MediaPlayer();  ;
    private int tempstotal ;
     Record   r ;
    private String outputFile ;
    private static int media_player_pausetime ;
    private SpeechService speechservice ;
    AudioManager am ;
    private MySharedPrefrences sh ;

    private BroadcastReceiver broadcastReceiver  ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.play_music_activity);


            seekbar = (SeekBar) findViewById(R.id.seekbar);
            imageview = (ImageView) findViewById(R.id.imageview);
            tempsecoule = (TextView) findViewById(R.id.tempsecoule);
            tempsrestant = (TextView) findViewById(R.id.tempsrestant);
            //mediaplayer = new MediaPlayer();
            start = (Button) findViewById(R.id.start);
            stop = (Button) findViewById(R.id.stop);
            r = new Record();
            sh = new MySharedPrefrences(this);
            outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.amr";

        /************Broadcast reciever pour recevoir une notification afin de faire pause ou reprendre***********/

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                String action = intent.getStringExtra("action");

                 if(action.equals("pause")){
                        pause();
                 }else if(action.equals("reprendre")){
                        restart();
                 }
                }
            };

            IntentFilter filter = new IntentFilter();
            filter.addAction("com.example.streammusicplayer.PlayMusicActivity");
            this.registerReceiver(broadcastReceiver,filter);

        /**********************************************************************************************************/

            speechservice = new SpeechService(this);
            //jouer un morceau de musique
            play();

            mediaplayer.seekTo(0);
            mediaplayer.setVolume(0.5f, 0.5f);
            tempstotal = mediaplayer.getDuration();

        seekbar.setMax(tempstotal);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    if (fromUser) {
                        mediaplayer.seekTo(progress);
                        seekbar.setProgress(progress);
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });



        // Thread pour la mise a jour du positionBar et le timeLabel
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaplayer != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mediaplayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        }).start();

        // demarer l'enregistrement
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setEnabled(false);
                stop.setEnabled(true);
                pause();
                System.out.println("paused.............................");
                r.StartRecord(outputFile);

            }
        });

        //arreter l'enregistrement
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 r.StopRecord();
                 String a = speechservice.uploadFileToNodeJs(outputFile,"");
                 restart();
                 start.setEnabled(true);
                 stop.setEnabled(false);

            }
        });

    }

// handler created for setting progress
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            seekbar.setProgress(currentPosition);
            // Update Labels.
           String elapsedTime = createTimeLabel(currentPosition);
            tempsecoule.setText(elapsedTime);
           String remainingTime = createTimeLabel(tempstotal-currentPosition);
            tempsrestant.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time){

        String timeLabel = "" ;
        int min = time / 1000 / 60 ;
        int sec  = time / 1000 % 60 ;

        timeLabel = min + ":" ;
        if(sec < 10 ) timeLabel += "0" ;

        timeLabel += sec ;

        return timeLabel ;
    }

    //cette methode recupere l'id de la chanson a partir du sharedprefrences ensuite
    // elle envoi une requete http pour jouer le morceau avec l'id passer en parametre
      public void play(){

        // String id_music = sh.getSharedData();
         SharedPreferences sh = getSharedPreferences("music", Context.MODE_PRIVATE);
         String music_id = sh.getString("music_id","");
         System.out.println("music id is "+ music_id);

         String url = "http://192.168.43.107:3000/music/"+music_id;

          mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
          try {

              mediaplayer.setDataSource(url);
              mediaplayer.prepare();
              mediaplayer.start();

          } catch (IOException e) {
              e.printStackTrace();
          }

      }

   // methode executé lorsque l'activité est fermé elle a pour role de stoper le media player pour eviter le probleme de lancer plusieurs morceau
    // au meme temps
    @Override
    protected void onDestroy() {

        super.onDestroy();
        stop();
    }

    // permet de stopper le mediaplayer
    public  void stop(){

        if(mediaplayer != null) {
            if (mediaplayer.isPlaying()) {
                System.out.println("////////////////////////////////////////MediaPlayer is playing//////////////////////////////////////////////////");

                try {
                    mediaplayer.reset();
                    mediaplayer.prepare();
                    mediaplayer.stop();
                    mediaplayer.release();
                    mediaplayer = null;
                    System.out.println("////////////////////////////////////////MediaPlayer null value assined//////////////////////////////////////////////////");

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }

        }
      }



    //permet de suspendre un morceau de musique
    public /*static*/ void pause(){

        if( mediaplayer.isPlaying()){

            mediaplayer.pause();
            media_player_pausetime = mediaplayer.getCurrentPosition();

        }

      }
    //permet de reprendre un morceau de musique qui ete en pause
    public /*static*/ void restart() {

           if(!mediaplayer.isPlaying()){

               mediaplayer.seekTo(media_player_pausetime);
               mediaplayer.start();

            }

       }


}