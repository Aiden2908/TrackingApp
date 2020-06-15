package locationrestfulservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 *
 * @author Rob
 */
@Singleton
public class LocationBean {

    private ArrayList<Location> locationList;
    private static final String DB_URL = "jdbc:mysql://raptor2.aut.ac.nz:3306/testUnrestricted";

    @PostConstruct
    public void initaliseStockCollection() {
        locationList = new ArrayList();
        addLocationFromDatabase();
    }

    public ArrayList<Location> getLocationList() {
        addLocationFromDatabase();
        return locationList;
    }

    private Connection getConn() throws SQLException {
        return DriverManager.getConnection(DB_URL, "student", "fpn871");
    }

    // Method which pulls the data from the database and then saves it in the locationList
    public void addLocationFromDatabase() {
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery("select * from LocationAssignment where isLoggedIn=1");
            System.out.println(rs.getMetaData().getColumnCount());
            locationList.clear();

            while (rs.next()) {
                String email = rs.getString(1);
                String password = rs.getString(2);
                String longitude = rs.getString(3);
                String latitude = rs.getString(4);

                locationList.add(new Location(email, password, longitude, latitude));
            }
        } catch (SQLException ex) {
            Logger.getLogger(LocationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Method which pulls the data from the database and then saves it in the locationList
    public String registerNewUser(Location location) {
        try {
            String sql = "INSERT INTO testUnrestricted.LocationAssignment (email,password,longitude,latitude,isLoggedIn) VALUES (?,?,?,?,?);";
            PreparedStatement pstmt = getConn().prepareStatement(sql);
            pstmt.setString(1, location.getEmail());
            pstmt.setString(2, location.getPassword());
            pstmt.setString(3, location.getLongitude());
            pstmt.setString(4, location.getLatitude());
            pstmt.setInt(5, 1);

            pstmt.executeUpdate();

        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                //duplicate primary key 
                return "userAlreadyExists";
            }
            return "SQLException";
        }
        return null;
    }

    //Method to handle setting user states to either logged logged in or logged out.
    public String userStates(Location location, int isLoggedIn) throws SQLException {
        if(isLoggedIn==1){
            int i=0;
            try {
                Statement st = getConn().createStatement();
                ResultSet rs = st.executeQuery("select * from LocationAssignment where email='" + location.getEmail() + "' and password='" + location.getPassword() + "';");
                while (rs.next()) {
                    String sql = "UPDATE testUnrestricted.LocationAssignment SET isLoggedIn = ? WHERE email='" + location.getEmail() + "' and password='" + location.getPassword() + "';";
                    PreparedStatement pstmt = getConn().prepareStatement(sql);
                    pstmt.setInt(1, isLoggedIn);
                    pstmt.executeUpdate();
                    i++;
                }
                if(i==0){
                    return "doesnt-exist";
                }
            } catch (SQLException ex) {
                return "SQLException " + ex;
            }
        }else{
            String sql = "UPDATE testUnrestricted.LocationAssignment SET isLoggedIn = ? WHERE email='" + location.getEmail()+"';";
            PreparedStatement pstmt = getConn().prepareStatement(sql);
            pstmt.setInt(1, isLoggedIn);
            pstmt.executeUpdate();
        }
        return null;
    }

    public String updateUserLocation(Location location) {
        try {
            String sql = "UPDATE testUnrestricted.LocationAssignment SET longitude = ?, latitude = ? WHERE email='" + location.getEmail()+ "';";
            PreparedStatement pstmt = getConn().prepareStatement(sql);
            pstmt.setString(1, location.getLongitude());
            pstmt.setString(2, location.getLatitude());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            return "SQLException " + ex;
        }
        return null;
    }
}
