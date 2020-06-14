

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author Rob
 */

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup",
        propertyValue = "jms/LocationQueue")
    ,
    @ActivationConfigProperty(propertyName = "destinationType",
        propertyValue = "javax.jms.Queue")
    })

public class LocationMessageBean implements MessageListener {
    @Resource
    private MessageDrivenContext mdc;
    
    private static final String DB_URL = "jdbc:mysql://raptor2.aut.ac.nz:3306/testUnrestricted";
    
    public LocationMessageBean() {
    }

    private Connection getConn() throws SQLException {
        return DriverManager.getConnection(DB_URL, "student", "fpn871");
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            if(message instanceof TextMessage) {
                String str = ((TextMessage) message).getText();
                String[] arr = str.split(",");
                
                String sql = "INSERT INTO testUnrestricted.LocationAssignment (email,password,longitude,latitude,isLoggedIn) VALUES (?,?,?,?,?);";
                PreparedStatement pstmt = getConn().prepareStatement(sql);
                pstmt.setString(1, arr[1]);
                pstmt.setString(2, arr[0]);
                pstmt.setString(3, arr[2]);
                pstmt.setString(4, arr[3]);
                pstmt.setInt(5, 1);

                pstmt.executeUpdate();
                System.out.println(str);
            } else {
                System.out.println("error");
            }
        } catch(JMSException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
