package com.example.streammusicplayer.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public class MySharedPrefrences {

    AppCompatActivity app ;

    public MySharedPrefrences(AppCompatActivity app){

        app = app;

    }

    public void shareData(String data){

        SharedPreferences sh = app.getSharedPreferences("music", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sh.edit() ;
        editor.putString("music_id",data);
        editor.commit() ;

    }


    public String getSharedData(){

        SharedPreferences sh = app.getSharedPreferences("music", Context.MODE_PRIVATE);
        String music_id = sh.getString("music_id","");
        return music_id ;

    }




}
