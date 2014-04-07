package edu.sjsu.cmpe.library;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;

import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;

public class Listener {

	String topicName = "";
	Listener(LibraryServiceConfiguration configuration){
		topicName = configuration.getStompTopicName();
	}	

	public void listenService(BookRepositoryInterface bookRepository) {
		String receivedData = "";

		try {
			String user = "admin";
			String password = "password";
			String host = "54.193.56.218";
			int port = 61613;
			String destination = topicName; 

			StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
			factory.setBrokerURI("tcp://" + host + ":" + port);

			Connection connection = factory.createConnection(user, password);
			connection.start();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination dest = new StompJmsDestination(destination);

			MessageConsumer consumer = session.createConsumer(dest);
			System.currentTimeMillis();
			System.out.println("Waiting...");
			while (true) {
				Message msg = consumer.receive();

				if (msg instanceof TextMessage) {
					receivedData = ((TextMessage) msg).getText();
					if ("SHUTDOWN".equals(receivedData)) {
						break;
					}
					System.out.println("The Received Message is : " + receivedData);					


					String args1[] = receivedData.split(":");

						String isbn = args1[0];
						String title = args1[1];
						String category = args1[2];
						String coverImage = args1[3];
						Book receivedBook = new Book();

					receivedBook.setIsbn(Long.parseLong(isbn));					
					receivedBook.setTitle(title);
					receivedBook.setCategory(category);

					try {
						receivedBook.setCoverimage(new URL(coverImage));
					} catch (MalformedURLException e) {

					}
					bookRepository.updateLibraryAfterResponse(receivedBook);


				} else if (msg instanceof StompJmsMessage) {
					StompJmsMessage smsg = ((StompJmsMessage) msg);
					receivedData = smsg.getFrame().contentAsString();
					if ("SHUTDOWN".equals(receivedData)) {
						break;
					}
					System.out.println("The Received Message is " + receivedData);

				} else {
					System.out.println("Unexpected message type: "
							+ msg.getClass());
				}
			}
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
