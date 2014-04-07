package edu.sjsu.cmpe.procurement.api.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import edu.sjsu.cmpe.procurement.ProcurementService;
import edu.sjsu.cmpe.procurement.domain.Book;
import edu.sjsu.cmpe.procurement.domain.PublisherData;
import edu.sjsu.cmpe.procurement.domain.shipped_books;

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
		System.out.println("the string isbn is : "+ string);

		lst.add(Integer.parseInt(string));
	//	lst.add(Integer.parseInt(string));
	}
	//pubdata.setOrder_book_isbns(lst);
	
//    ObjectMapper objmapper = new ObjectMapper();
 //   String str = objmapper.writeValueAsString(pubdata);
  //  System.out.println("json data is "+ str);
	String input = "{\"id\":\"03879\",\"order_book_isbns\":" + lst + "}";
	System.out.println("json data is "+input);
	Client client =  Client.create();
	WebResource webResource = client.resource("http://54.193.56.218:9000/orders");
	ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);

	if (response.getStatus() != 200) {
		throw new RuntimeException
		("The order wasnt able to be post at http://54.193.56.218:9000/orders " + response.getStatus());
	}
	else
		System.out.println(response.toString());

	ProcurementService.SendPubToTop(RetrivePub()); 
	}
	catch(Exception e)
	{
	System.out.println("Exception at sending POST: "+e.getMessage());
	}
		
}
private static List<Book> RetrivePub() throws Exception {

	String url = "http://54.193.56.218:9000/orders/03879";
	shipped_books book = null;
	try{
	Client client = Client.create();
	WebResource webResource = client.resource(url);

	ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

	if (response.getStatus() != 200) {
	   throw new RuntimeException("There was no book recieved from publisher as status: "+response.getStatus());
	}

		String output = response.getEntity(String.class);
		try {
		ObjectMapper jsontojackson = new ObjectMapper();
		book = jsontojackson.readValue(output,shipped_books.class);
	}
	catch(Exception e)	{
		System.out.println("Error While parsing the jsontojackson in receiveget" + e.getMessage());
	}
	}
	catch(Exception e){
		System.out.println("Exception in ReceiveGetFromPubliser");
	}
	return book.getShipped_books();


}

	
}
