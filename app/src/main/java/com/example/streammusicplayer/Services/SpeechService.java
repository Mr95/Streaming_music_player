package com.example.streammusicplayer.Services;

import android.media.MediaRecorder;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
//import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue ;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class SpeechService {

   // private String Url ="https://speech.googleapis.com/v1/speech:recognize?key=AIzaSyB-knX4h8gGW7zcw54-XARGOVeR7p1Szrw";
    private RequestQueue requestQueue ;
    private AppCompatActivity app ;
    String ip ="192.168.43.107";
    String resultat_text = null;
    AnalyseurService analyseur ;

    public SpeechService(AppCompatActivity app){

      this.app = app ;
      analyseur = new AnalyseurService(app);
      requestQueue = Volley.newRequestQueue(app.getApplicationContext());

    }

//cette methode permet d'envoyer le fichier contenant l'enregistrement a transcrire vers le serveur intermediaire et ensuite recupere le resultat (un text)
// puis elle fait appelle a la methode qui envoie la requetes a l'analyseur des requetes
    public String uploadFileToNodeJs(String FilePath , String Url){

       SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, "http://"+ip+":3001/transcriptPost" ,

         new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {

              System.out.println(response);

                  if(!response.equals("")){

                      analyseur.sendRequestoAnalyse(response);

                   }
                       // resultat_text = response ;
              }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                      System.out.println("erorr sami erorr ");
                }
          });
          smr.addStringParam("string", "text");
          smr.addFile("file", FilePath);

        requestQueue.add(smr);

            return resultat_text ;
      }
}