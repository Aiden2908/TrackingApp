package locationRestfulService;

import java.util.Collection;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;


/**
 *
 * @author Rob
 */

@Named
@Path("/location")
public class LocationResource {
    @EJB
    private LocationBean locationBean;
    @Context
    private UriInfo context;
    private static final char QUOTE = '\"';
    
    public LocationResource() {
        
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getAllStock() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<stock uri=").append(QUOTE).append(context.getAbsolutePath()).append(QUOTE).append(">");

        Collection<Location> allLocation = locationBean.getLocationList();

        for(Location location : allLocation) {
            buffer.append(location.getXMLString());
        }

        buffer.append("</stock>");

        return buffer.toString();
    }
    
    // Method which gets valid email and passwords
    
    // Method which registers new user
    
    // Method which sets location
}
