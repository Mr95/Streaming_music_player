package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

import com.zeroc.Ice.Current;

import StreamServerSlice.Song;


//Cette class représente l'implementation de l'interface spécifié par le language slice

public class StreamServerImpl implements StreamServerSlice.StreamInt {
	
	
	//Cette methode retourne une List contenant tout les chansons existantes dans le serveur de streaming en envoyant une requette http vers la route /all   
	@Override
	public ArrayList<Song> getListSongs(Current current) {
		
		
		ArrayList<Song> AllSongs =  new ArrayList<Song>(); 
		
		JSONArray json_array = null;
		
		try {
				
			URL url = new URL("http://localhost:3000/all");
			HttpURLConnection	c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			while ((inputLine = in.readLine()) != null) {
				
				response.append(inputLine);
		
			}
			
			in.close();

			
			json_array = new JSONArray(response.toString()) ;
			
			for(int i = 0 ; i < json_array.length() ; i++) {
				
				Song s = new Song(json_array.getJSONObject(i).getInt("id"),
						json_array.getJSONObject(i).getString("titre"),
						json_array.getJSONObject(i).getString("chanteur"), 
						json_array.getJSONObject(i).getString("album"));
				
				AllSongs.add(s);
					
			}
			
						
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 	AllSongs;
	}
	
	
	//Cette methode elle permet de retourner les informations d'une chanson id,titre,album,chanteur en passant dans les paramettres le titre de la chanson 
	@Override
	public Song getSongUrl(String Identifiant, Current current) {
		
		//System.out.println("sami"+Identifiant.substring(1));
		
		String Link = "empty" ;
		
		JSONObject json_object = null;
		Song s = null ;
		
		try {
		 
			URL	url = new URL("http://localhost:3000/getSongUrl?identifiant="+Identifiant.substring(1));
		
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");	
	
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
		
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			json_object = new JSONObject(response.toString()) ;
			
			s = new Song(json_object.getInt("id"),
					json_object.getString("titre"),
					json_object.getString("chanteur"), 
					json_object.getString("album"));
			
			
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		
		return s ;
	}

	
	//cette methode permet de recuperer les informations de la chanson suivante dans la liste en passant dans les parametres l'id de la chanson en cours d'ecoute
	@Override
	public Song next(int currentMusic , Current current) {
		
		JSONObject json_object = null;
		Song s = null ;
		
		try {
		 
			URL	url = new URL("http://localhost:3000/next?current="+currentMusic);
		
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");	
	
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
		
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			json_object = new JSONObject(response.toString()) ;
			
			s = new Song(json_object.getInt("id"),
					json_object.getString("titre"),
					json_object.getString("chanteur"), 
					json_object.getString("album"));
			
		//	Link =  response.toString() ;
			
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		//return Link;
		return s ;
		
	}

	
	//cette methode permet de recuperer les informations de la chanson precedante dans la liste en passant dans les parametres l'id de la chanson en cours d'ecoute
	@Override
	public Song previous(int currentMusic , Current current) {
	
		JSONObject json_object = null;
		Song s = null ;
		
		try {
		 
			URL	url = new URL("http://localhost:3000/previous?current="+currentMusic);
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");	
	
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			json_object = new JSONObject(response.toString()) ;
			
			s = new Song(json_object.getInt("id"),
					json_object.getString("titre"),
					json_object.getString("chanteur"), 
					json_object.getString("album"));
				
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		return s ;
	}

	
	
	
	
}