package locationrestfulservice;

import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author Rob
 */
public class LocationMessageSender {
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    
    @Resource(mappedName = "jms/LocationConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(mappedName = "jms/LocationQueue")
    private static Queue queue;
    
    public LocationMessageSender() {
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);
        } catch(JMSException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMessage(ArrayList<String> data) {
        try {
            TextMessage message = session.createTextMessage();
            
            message.setText(data.get(0) + "," + data.get(1) + "," + data.get(2) + "," + data.get(3));
            System.out.println("Message: " + message);
            messageProducer.send(message);
        } catch(JMSException e) {
            e.printStackTrace();
        }
    }
    
    public void closeConnection() {
        try {
            if(session != null) {
                session.close();
            }
            
            if(connection != null) {
                connection.close();
            }
        } catch(JMSException e) {
            e.printStackTrace();
        }
    }
}
