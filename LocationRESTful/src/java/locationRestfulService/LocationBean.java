package locationRestfulService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

/**
 *
 * @author Rob
 */
public class LocationBean {
    private ArrayList<Location> locationList;
    
    @PostConstruct
    public void initaliseStockCollection() {
        locationList = new ArrayList();
        addLocationFromDatabase();
    }
    
    public ArrayList<Location> getLocationList() {
        return locationList;
    }
    
    // Method which pulls the data from the database and then saves it in the locationList
    public void addLocationFromDatabase() {
        try {
            String DB_URL = "jdbc:mysql://raptor2.aut.ac.nz:3306/testUnrestricted";
            Connection conn = DriverManager.getConnection(DB_URL, "student", "fpn871");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select * from LocationAssignment");
            System.out.println(rs.getMetaData().getColumnCount());
            
            while(rs.next()) {
                String email = rs.getString(1);
                String password = rs.getString(2);
                String longitude = rs.getString(3);
                String latitude = rs.getString(4);
                
                locationList.add(new Location(email, password, longitude, latitude));
            }
        } catch(SQLException ex) {
            Logger.getLogger(LocationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
