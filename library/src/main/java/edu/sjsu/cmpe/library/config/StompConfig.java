package edu.sjsu.cmpe.library.config;

public class StompConfig {

	
	public static String getUser(){
		String user= (String) env("APOLLO_USER", "admin");
		return user; 
	}

	public static String getPassword(){
		String password = env("APOLLO_PASSWORD", "password");
		return password;
		
	}
	public static String getHost(){
		String host = env("APOLLO_HOST", "54.193.56.218");
		return host;
	}
	
	public static  int getPort(){
		int port = Integer.parseInt(env("APOLLO_PORT", "61613"));
		return port;
	}
	
	public static String getQueue(){
		String queue="/queue/03879.book.orders";
		
		return queue;
	}
	
	public static String getDestination(String []args){
		String destination = arg(args, 0, getQueue());
		return destination;
	}
	
	private static String env(String key, String defaultValue) {
		// TODO Auto-generated method stub
		String rc = System.getenv(key);
		if( rc== null ) {
		    return defaultValue;
		}
		return rc;
	    }
	 private static String arg(String []args, int index, String defaultValue) {
			if( index < args.length ) {
			    return args[index];
			} else {
			    return defaultValue;
			}
		    }
		
	}
	


