package com.example.streammusicplayer.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.streammusicplayer.Activities.ListMusicActivity;
import com.example.streammusicplayer.Activities.PlayMusicActivity;
import com.example.streammusicplayer.MainActivity;
import com.example.streammusicplayer.Model.MySharedPrefrences;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;


public class AnalyseurService  {

    private String ip = "192.168.43.107";
    private String  url  = "http://"+ip+":8080/StreamAppAnalyseur/webapi/Analyse";
    private RequestQueue requestQueue ;
    AppCompatActivity app ;
    String v ;
    String pause_reprendre = "" ;
    private MySharedPrefrences sh ;
    AudioManager am ;

        public AnalyseurService(AppCompatActivity app){

            this.app = app ;
            sh = new MySharedPrefrences(app);

        }


        //methode permettant d'envoyer la chaine de caractere en entrer vers l'analyseyr de requete et recupere le resultat
        // a l'interieure de la methode onReponse je fais appele a la methode reaction qui effectuer des operation en fonction des valeurs recus
        public void sendRequestoAnalyse(String TexToAnalyse){

          final String textoAnalyse = TexToAnalyse;

          requestQueue = Volley.newRequestQueue(app.getApplicationContext());

           StringRequest  stringRequest = new StringRequest(Request.Method.POST , url ,new Response.Listener<String>(){
              @Override
              public void onResponse(String response) {

               try {

                  JSONObject j = new JSONObject(response);

                 // System.out.println("Action type "+j.getString("action_type"));
                 // System.out.println("Song "+j.getString("song"));

                  String action = new String(j.getString("action_type").getBytes("ISO-8859-1"), "UTF-8") ;
                  String song = new String(j.getString("song").substring(0).replace(" ", "_").getBytes("ISO-8859-1"), "UTF-8") ;

                  reaction(action,song);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                   e.printStackTrace();
                }

             }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("cause ===  " + error.getCause());
                System.out.println("Network  ===  " + error.networkResponse);
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("param", textoAnalyse);
                return params;
            }

        };

        requestQueue.add(stringRequest) ;

    }

        // cette methode prend en parametre l'action et la chanson et en fonction des valeurs elle effectue soit un appelle distant soit des appelles telque augmenter ou bien diminuer le son
        public void reaction(String action , String song){

        if(action.equals("écouter") || action.equals("entendre") ){

            try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize())
            {
                com.zeroc.Ice.ObjectPrx base1 = communicator.stringToProxy("Streamer:default -h "+ip+" -p 10001");
                StreamServerSlice.StreamIntPrx stream = StreamServerSlice.StreamIntPrx.checkedCast(base1);
                if(stream == null)
                {
                    throw new Error("Invalid proxy");
                }
               // System.out.println( "sami"+new String(j.getString("song").substring(1).getBytes("ISO-8859-1")));
                StreamServerSlice.Song son = (StreamServerSlice.Song)stream.getSongUrl(song);

                //si la chanson existe donc id != -1 je met l'id dans sharredprefrences et je lance l'activité playMusic et je ferme l'activité en cours
                if(son.id != -1) {

                    SharedPreferences sh = app.getSharedPreferences("music", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sh.edit();
                    editor.putString("music_id", String.valueOf(son.id));
                    editor.commit();

                    Intent intent = new Intent(app.getApplicationContext(), PlayMusicActivity.class);
                    app.startActivity(intent);
                    app.finish();

                }else{

                    Toast.makeText(app.getApplicationContext(),"Song not found",Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){

                e.printStackTrace();
            }


        }else if(action.equals("augmenter")){

            Augmenter();

        }else if(action.equals("diminuer")){

            Diminuer();

        }else if(action.equals("suivante") || action.equals("suivant")){

            try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize())
            {

                com.zeroc.Ice.ObjectPrx base1 = communicator.stringToProxy("Streamer:default -h "+ip+" -p 10001");
                StreamServerSlice.StreamIntPrx stream = StreamServerSlice.StreamIntPrx.checkedCast(base1);
                if(stream == null)
                {
                    throw new Error("Invalid proxy");
                }

                SharedPreferences sh = app.getSharedPreferences("music", Context.MODE_PRIVATE);
                String music_id = sh.getString("music_id","");

                //appele distant a la methode next
                StreamServerSlice.Song son = (StreamServerSlice.Song)stream.next(Integer.valueOf(music_id));

                // sh.shareData(String.valueOf(son.id));
                //insertion de l'id dans un sharedprefrences
                SharedPreferences.Editor editor =sh.edit() ;
                editor.putString("music_id",String.valueOf(son.id));
                editor.commit() ;

                //lancement de l'activité play music
                Intent intent = new Intent(app.getApplicationContext(), PlayMusicActivity.class);
                app.startActivity(intent);
                app.finish();


            }catch (Exception e){

                e.printStackTrace();
            }

        }else if(action.equals("précédente") || action.equals("précédent")){

            try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize())
            {
                com.zeroc.Ice.ObjectPrx base1 = communicator.stringToProxy("Streamer:default -h "+ip+" -p 10001");
                StreamServerSlice.StreamIntPrx stream = StreamServerSlice.StreamIntPrx.checkedCast(base1);
                if(stream == null)
                {
                    throw new Error("Invalid proxy");
                }

                SharedPreferences sh = app.getSharedPreferences("music", Context.MODE_PRIVATE);
                String music_id = sh.getString("music_id","");

                //appele distant a la methode previous
                StreamServerSlice.Song son = (StreamServerSlice.Song)stream.previous(Integer.valueOf(music_id));

                //insertion de l'identifiant dans un sharedpreferences
                SharedPreferences.Editor editor =sh.edit() ;
                editor.putString("music_id",String.valueOf(son.id));
                editor.commit() ;

                //lancement de l'activité pour jouer le morceau precedent
                Intent intent = new Intent(app.getApplicationContext(), PlayMusicActivity.class);

                app.startActivity(intent);
                app.finish();


            }catch (Exception e){

                e.printStackTrace();
            }

       }else if(action.equals("consulter") || action.equals("liste")){

            //lancemecnt de l'activité qui permet d'afficher la liste des musiques existantes cette classe lancé fait un appelle distant
            Intent intent = new Intent(app.getApplicationContext(), ListMusicActivity.class);
            app.startActivity(intent);
            app.finish();

        }else if(action.equals("pause")){

           // PlayMusicActivity.pause();

            // broadcast reciever pour notifier l'application qui lis un morceau pour suspendre la musique
            Intent intent = new Intent("com.example.streammusicplayer.PlayMusicActivity");
            intent.putExtra("action", "pause");
            app.sendBroadcast(intent);

            //System.out.println("pause");

        }else if(action.equals("reprendre")){

           // PlayMusicActivity.restart();
            // broadcast reciever pour notifier l'application qui lis un morceau pour rependre la musique
            Intent intent = new Intent("com.example.streammusicplayer.PlayMusicActivity");
            intent.putExtra("action", "reprendre");
            app.sendBroadcast(intent);


        }else if(action.equals("quitter")){

            // quitter l'application
            app.finish();

        }


    }

        //cette methodepermet d'augmenter le son
        public void Augmenter(){

        am = (AudioManager) app.getSystemService(Context.AUDIO_SERVICE);
        int current_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int MAX_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

            int New_Volume = current_volume + 3 ;
            am.setStreamVolume(AudioManager.STREAM_MUSIC ,New_Volume ,0);

        System.out.println("Augmented");
    }

        //cette methode permet de diminuer le son
        public void Diminuer(){

        am = (AudioManager) app.getSystemService(Context.AUDIO_SERVICE);
        int current_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int New_Volume = current_volume - 3 ;
        am.setStreamVolume(AudioManager.STREAM_MUSIC ,New_Volume ,0);
        System.out.println("Diminuer");
    }

}