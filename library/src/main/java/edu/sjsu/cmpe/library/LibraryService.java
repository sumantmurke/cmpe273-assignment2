package edu.sjsu.cmpe.library;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.config.StompConfig;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;

public class LibraryService extends Service<LibraryServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static String libraryName = "";
    
    public static void main(String[] args) throws Exception {
	new LibraryService().run(args);
	
    }
    

    @Override
    public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap) {
	bootstrap.setName("library-service");
	bootstrap.addBundle(new ViewBundle());
	bootstrap.addBundle(new AssetsBundle());
    }
  //1st change

    public static void LostBook(Long lostisbn) throws JMSException{
    	LibraryServiceConfiguration configuration= new LibraryServiceConfiguration();
    	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
    	factory.setBrokerURI("tcp://" + StompConfig.getHost() + ":" + StompConfig.getPort());

    	Connection connection = factory.createConnection(StompConfig.getUser(), StompConfig.getPassword());
    	connection.start();
    	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    	Destination dest = new StompJmsDestination(StompConfig.getQueue());
    	MessageProducer producer = session.createProducer(dest);
    	producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    	System.out.println("Sending messages to " + StompConfig.getQueue() + "...");
    	String data = libraryName +":"+ lostisbn.toString();
    	TextMessage msg = session.createTextMessage(data);
    	msg.setLongProperty("id", System.currentTimeMillis());
    	producer.send(msg);
    	System.out.println(configuration.getLibraryName()+":"+ lostisbn.toString());
    	//producer.send(session.createTextMessage("SHUTDOWN"));
    	connection.close();
    	}

    //change finish    	

    @Override
    public void run(LibraryServiceConfiguration configuration,
	    Environment environment) throws Exception {
	// This is how you pull the configurations from library_x_config.yml
	String queueName = configuration.getStompQueueName();
	String topicName = configuration.getStompTopicName();
	log.debug("{} - Queue name is {}. Topic name is {}",
		configuration.getLibraryName(), queueName,
		topicName);
	String libraryName = configuration.getLibraryName();
	// TODO: Apollo STOMP Broker URL and login

			
	
	/** Root API */
	environment.addResource(RootResource.class);
	/** Books APIs */
	BookRepositoryInterface bookRepository = new BookRepository();
	environment.addResource(new BookResource(bookRepository));

	/** UI Resources */
	environment.addResource(new HomeResource(bookRepository));
    }
}
