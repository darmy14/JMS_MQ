package com.ibm.mq.samples.jms;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

public class JmsPutGet {

	// System exit status value (assume unset value to be 1)
	private static int status = 1;

	// Create variables for connection to MQ

	private static final String HOST = "qm-bfe9.qm.us-south.mq.appdomain.cloud"; // Host or IP address
	private static final int PORT = 30177; // Listener MQGR
	private static final String CHANNEL = "CLOUD.ADMIN.SVRCONN"; // connection channel app
	private static final String QMGR = "QM"; // QMGR Name
	private static final String APP_USER = "dherrera1"; // User name that application user to connect to MQ
	private static final String APP_PASSWORD = "kim6Pz-j1HiI2F3lX8q5AKWOyYgtJfD24-djvRBTUCdQ"; // my pass
	private static final String QUEUE_NAME = "DEV.QUEUE.1";

	
	public static void main(String[] args) {

		// TODO Auto-generated method stub

		// Variables
		JMSContext context = null;
		Destination destination = null;
		JMSProducer producer = null;
		JMSConsumer consumer = null;

		try {
			// Create a connection factory
			JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
			JmsConnectionFactory cf = ff.createConnectionFactory();

			// set the properties

			cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
			cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
			cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
			cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
			cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JMS Darmy's APP (put & get)");
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
			cf.setStringProperty(WMQConstants.USERID, APP_USER);
			cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
			
			//create JMS objects
			context = cf.createContext();
			destination = context.createQueue("queue:///"+ QUEUE_NAME);
			
			long uniqueNumber = System.currentTimeMillis() %1000;
			TextMessage message = context.createTextMessage("Su numero de suerte hoy es "+ uniqueNumber);
			
			producer = context.createProducer();
			producer.send(destination, message);
			System.out.println("Enviar Mensaje:\n"+message);
			
			consumer = context.createConsumer(destination); //atoclosable
			String receivedMessage = consumer.receiveBody(String.class, 15000); //in ms or 15 seconds
			
			System.out.println("\nMensaje Recibido:\n" + receivedMessage);
	
	       recordSuccess();
		} catch (JMSException jmsex) {
			recordFailure(jmsex);
		}

		System.exit(status);

	} // end Main ()

	/*
	 * Record this run as successful.
	 */

	private static void recordSuccess() {
		System.out.println("SUCCESS");
		status = 0;
		return;
	}

	/*
	 * Record this run as failure.
	 * 
	 * @param ex
	 */
	private static void recordFailure(Exception ex) {
		if (ex != null) {
			if (ex instanceof JMSException) {
				processJMSException((JMSException) ex);
			} else {
				System.out.println(ex);
			}
		}
		System.out.println("FAILURE");
		status = -1;
		return;
	}

	/*
	 * Process a JMSException and any associates inner exceptions
	 */

	private static void processJMSException(JMSException jmsex) {
		System.out.println(jmsex);
		Throwable innerException = jmsex.getLinkedException();
		if (innerException != null) {
			System.out.println("Inner exception(s): ");
		}
		while (innerException != null) {
			System.out.println(innerException);
			innerException = innerException.getCause();
		}
		return;
	}
}
