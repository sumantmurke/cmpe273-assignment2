package edu.sjsu.cmpe.procurement.api.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import edu.sjsu.cmpe.procurement.domain.PublisherData;

public class MessagePublisher {
/*
 * Method to publish data (message) to publisher
 */
public static void msgpub(HashMap<Integer,String> messageList){	

	try{

	PublisherData pubdata = new PublisherData();
	pubdata.setId("03879");
	List<Integer> lst = new ArrayList<Integer>();
	for(int i=0;i<messageList.size();++i){
		String string = messageList.get(i);
		if(string!=null && string.contains(":"))
			string = string.split(":")[1];
		else
			string="0";
		lst.add(Integer.parseInt(string));
	}
	pubdata.setOrder_book_isbns(lst);

	ObjectMapper jacktojson = new ObjectMapper();
	String s = jacktojson.writeValueAsString(pubdata);

	Client client =  Client.create();
	WebResource webResource = client.resource("http://54.219.156.168:9000/orders");
	ClientResponse response = webResource.type("application/json").post(ClientResponse.class, s);

	if (response.getStatus() != 200) {
		throw new RuntimeException
		("Couldnt Post the order to http://54.219.156.168:9000/orders" + response.getStatus());
	}
	else
		System.out.println(response.toString());

	//ProcurementService.PublisherToTopic(ReceiveGetFromPubliser()); 
	}
	catch(Exception e)
	{
	System.out.println("Exception at sending POST: "+e.getMessage());
	}
		
}
	
	
}
