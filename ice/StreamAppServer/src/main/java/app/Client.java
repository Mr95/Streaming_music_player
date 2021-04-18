package app;

import java.io.UnsupportedEncodingException;
import java.util.List;

import StreamServerSlice.Song;


// cette class represente le client elle est utilisé juste pour que je test le fonctionnement de l'implémentation afin de l'utilisé dans l'application mobile 
public class Client {

	public static void main(String[] args) throws UnsupportedEncodingException {

		
		try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args))
        {
			//recupération du proxy de type com.zeroc.Ice.ObjectPrx
            com.zeroc.Ice.ObjectPrx base1 = communicator.stringToProxy("Streamer:tcp -p 10001");
            
            //faire le casting de com.zeroc.Ice.ObjectPrx a StreamInt Interface
            StreamServerSlice.StreamIntPrx stream = StreamServerSlice.StreamIntPrx.checkedCast(base1);
             
            
            if(stream == null)
            {
                throw new Error("Invalid proxy");
            }
            
            Song s = stream.getSongUrl("hello");
            System.out.println(s.id + " " + s.chanteur + " " + new String(s.titre.getBytes("ISO-8859-1"), "UTF-8"));
             
            
            
             
             Song s1 = stream.getSongUrl(" ");
             System.out.println(s1.id + " " + s1.chanteur + " " +s1.titre);
             
             System.out.println("=========================================");
           
            
            List<Song>  songs =  stream.getListSongs();
            
            for(int i = 0 ; i < songs.size() ; i++) {
            	
            	System.out.println(songs.get(i).album + "  " + songs.get(i).chanteur);
            	
            }
            
            System.out.println("=========================================");
            
           Song s2 = stream.next(4);        
           System.out.println(s2.id + " " + s2.chanteur + " " +s2.titre);
           
           
            Song s3 = stream.previous(1); 
           System.out.println(s3.id + " " + s3.chanteur + " " +s3.titre);
            
        }

	}

}
