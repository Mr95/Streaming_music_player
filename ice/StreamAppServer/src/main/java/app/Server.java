package app;



public class Server {

	public static void main(String[] args) {
		
		 try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args))
	        {
			    //adaptateur a l'ecoute des requetes sur le protocol (TCP/IP) et le port number 10001.
	            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Adapter", "default -p 10001");
	         
	            //create de l'objet a partager.
	            com.zeroc.Ice.Object object1 = new StreamServerImpl();
	            
	            //ajout de l'objet a partager dans l'adaptateur 
	            adapter.add(object1, com.zeroc.Ice.Util.stringToIdentity("Streamer"));
	            
	            System.out.println("start server.....");
	            
	            //activation de l'adaptateur 
	            adapter.activate();
	            
	            communicator.waitForShutdown();
	                    
	        }
				
	}

}
