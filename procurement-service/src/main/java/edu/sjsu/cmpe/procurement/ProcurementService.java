package edu.sjsu.cmpe.procurement;

import java.util.HashMap;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.client.JerseyClientBuilder;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import de.spinscale.dropwizard.jobs.JobsBundle;
import edu.sjsu.cmpe.procurement.api.resources.MessagePublisher;
import edu.sjsu.cmpe.procurement.api.resources.RootResource;
import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.config.StompConfig;

public class ProcurementService extends Service<ProcurementServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static HashMap<Integer,String> messageList = new HashMap<Integer,String>();
    static Integer counter=0;
    
    
    
    /**
     * FIXME: THIS IS A HACK!
     */
    public static Client jerseyClient;

    public static void main(String[] args) throws Exception {
	new ProcurementService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ProcurementServiceConfiguration> bootstrap) {
	bootstrap.setName("procurement-service");
	/**
	 * NOTE: All jobs must be placed under edu.sjsu.cmpe.procurement.jobs
	 * package
	 */
	bootstrap.addBundle(new JobsBundle("edu.sjsu.cmpe.procurement.jobs"));
    }

    @Override
    public void run(ProcurementServiceConfiguration configuration,
	    Environment environment) throws Exception {
	jerseyClient = new JerseyClientBuilder()
	.using(configuration.getJerseyClientConfiguration())
	.using(environment).build();

	/**
	 * Root API - Without RootResource, Dropwizard will throw this
	 * exception:
	 * 
	 * ERROR [2013-10-31 23:01:24,489]
	 * com.sun.jersey.server.impl.application.RootResourceUriRules: The
	 * ResourceConfig instance does not contain any root resource classes.
	 */
	environment.addResource(RootResource.class);

	String queueName = configuration.getStompQueueName();
	String topicName = configuration.getStompTopicPrefix();
	log.debug("Queue name is {}. Topic is {}", queueName, topicName);
	// TODO: Apollo STOMP Broker URL and login

    }
    //changes
    public static void retriveQueue() throws JMSException{
    	
    	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
    	factory.setBrokerURI("tcp://" + StompConfig.getHost() + ":" + StompConfig.getPort());

    	Connection connection = factory.createConnection(StompConfig.getUser(), StompConfig.getPassword());
    	connection.start();
    	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    	Destination dest = new StompJmsDestination(StompConfig.getQueue());

    	MessageConsumer consumer = session.createConsumer(dest);
    	System.out.println("Waiting for messages from " + StompConfig.getQueue() + "...");
    	long waitUntil = 5000; // wait for 5 sec
    	while(true) {
    	    Message msg = consumer.receive(waitUntil);
    	    if( msg instanceof  TextMessage ) {
    		String body = ((TextMessage) msg).getText();
    		messageList.put(counter,body);
             counter+=1;
    		System.out.println("Received message = " + body);

    	    } else if (msg == null) {
    	          System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
    	          break;
    	    } else {
    	         System.out.println("Unexpected message type: " + msg.getClass());
    	    }
    	} // end while loop
    	if(messageList.size() > 0)
        	MessagePublisher.msgpub(messageList);
    	connection.close();
    	System.out.println("Done");
        }

    	
    //changes done	
    
}
