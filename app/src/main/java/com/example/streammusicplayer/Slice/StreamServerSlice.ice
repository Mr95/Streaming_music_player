module  StreamServerSlice
{


   struct Song {

     int id ;

	 string titre ;

	 string chanteur ;

	 string album ;

	};

["java:type:java.util.ArrayList<Song>"]
	sequence<Song> SongSeq;

	interface StreamInt {

	SongSeq getListSongs();

	Song getSongUrl(string identifier);

	Song next(int currentMusic) ;

	Song previous(int currentMusic) ;


	};


};