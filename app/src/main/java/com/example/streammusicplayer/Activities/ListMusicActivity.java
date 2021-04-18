package com.example.streammusicplayer.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.streammusicplayer.MainActivity;
import com.example.streammusicplayer.R;

import java.util.ArrayList;

// cette activité est dédiée a l'affichage des chansons existantes
public class ListMusicActivity extends AppCompatActivity {


    private ListView list_view ;
    private String ip = "192.168.43.107";

    ArrayList <String> Titre = new ArrayList<String>() ;
    ArrayList <String> Chanteur = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_music_activity);
        list_view = (ListView) findViewById(R.id.listview);


        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize())
        {
            com.zeroc.Ice.ObjectPrx base1 = communicator.stringToProxy("Streamer:default -h "+ip+" -p 10001");
            StreamServerSlice.StreamIntPrx stream = StreamServerSlice.StreamIntPrx.checkedCast(base1);

            if(stream == null)
            {
                throw new Error("Invalid proxy");
            }

            // recuperation de toute les chansons
             ArrayList<StreamServerSlice.Song> songs = (ArrayList) stream.getListSongs();

            for(int i = 0 ; i < songs.size() ; i++) {

                Titre.add(songs.get(i).titre);
                Chanteur.add(songs.get(i).chanteur);

            }

        }catch (Exception e){

            e.printStackTrace();
        }

        CostumListViewAdapter  clvAdapter = new CostumListViewAdapter();
        list_view.setAdapter(clvAdapter);
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ListMusicActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    
    class CostumListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Titre.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.list_view_item,null);
            ImageView imv = (ImageView) convertView.findViewById(R.id.imageView3);
            TextView txt = (TextView)convertView.findViewById(R.id.textView);
            TextView txt3 = (TextView)convertView.findViewById(R.id.textView3);


            txt.setText(Titre.get(position));
            txt3.setText( Chanteur.get(position));
            return convertView;
        }
    }

}
