package Model;

public class Data {

	
	private String action_type ;
	
	private String song ;


	public Data() {
		super();
		
	}

	public Data(String action_type, String song) {
		super();
		this.action_type = action_type;
		this.song = song;
	}

	public String getAction_type() {
		return action_type;
	}

	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}
	
	
	
}