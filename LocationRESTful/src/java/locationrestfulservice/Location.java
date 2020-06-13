package locationrestfulservice;

import java.io.StringReader;
import javax.ejb.Singleton;
import javax.xml.bind.JAXB;

/**
 *
 * @author Rob
 */
@Singleton
public class Location {
    private String email;
    private String password;
    private String longitude;
    private String latitude;
    
    public Location() {
        
    }
    
    public Location(String email, String password, String longitude, String latitude) {
        this.email = email;
        this.password = password;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    public String getXMLString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append("<user>");
        buffer.append("<email>").append(email).append("</email>");
        buffer.append("<password>").append(password).append("</password>");
        buffer.append("<longitude>").append(longitude).append("</longitude>");
        buffer.append("<latitude>").append(latitude).append("</latitude>");
        buffer.append("</user>");
        
        return buffer.toString();
    }
    
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setUsername(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    
}
