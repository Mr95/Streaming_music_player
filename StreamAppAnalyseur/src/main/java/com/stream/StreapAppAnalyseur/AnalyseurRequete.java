package com.stream.StreapAppAnalyseur;

import java.util.List ;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.json.*;
import Model.Data;


@Path("/Analyse")
public class AnalyseurRequete {

	
	String Action [] = {"ecouter" ,"écouter","entendre" ,"augmenter","diminuer","précédent",
						"précédente", "suivant","suivante","consulter","liste","pause","reprendre","quitter"};
	
	
	private Pattern pattern;
	private Matcher matcher ;
		
	  	
		@GET
	    @Produces(MediaType.TEXT_PLAIN)
	    public String geteaction() {
	     
	  		return "salut";
	    
	  	}
	
	
		@POST
		@Produces(MediaType.APPLICATION_JSON)
		public Data getPost(@FormParam("param") String param) {
			
			Data data = new Data();
			
			for(int i = 0 ; i < Action.length ; i++) {
				
				pattern = Pattern.compile("(?<="+Action[i]+").*");
		        matcher = pattern.matcher(param);
				
		        if(matcher.find()) {
		        	
		        	data.setAction_type(Action[i]);
		       
		        	String s = matcher.group(0);
		        	
			  		data.setSong(s);
		        	
		        }
		        
			}
			

			return data ;
		}
		
	
}