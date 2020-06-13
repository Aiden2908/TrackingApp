package locationrestfulservice;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.Collection;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
        buffer.append("<users uri=").append(QUOTE).append(context.getAbsolutePath()).append(QUOTE).append(">");
        Collection<Location> allStock = locationBean.getLocationList();

        for (Location stock : allStock) {
            buffer.append(stock.getXMLString());
        }

        buffer.append("</users>");

        return buffer.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("{percent}")
    public String getPercentageStock(@PathParam("percent") String percent) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<stock uri=").append(QUOTE).append(context.getAbsolutePath()).append(QUOTE).append(">");

        Collection<Location> allStock = locationBean.getLocationList();

        for (Location stock : allStock) {
        }

        buffer.append("</stock>");

        return buffer.toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String registerUser(String XML) {
        Location location;
        try {
            String attr[] = XML.split(",");
            location = new Location(attr[0], attr[1], attr[2], attr[3]);
            String sqlException = locationBean.registerNewUser(location);
            if (sqlException != null) {
                return "<error><reason>" + sqlException + "</reason></error>";
            }
        } catch (Exception e) {
            return "<error><reason>Invalid content</reason></error>";
        }
        return location.getXMLString();

    };
    
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("{percent}")
    public String loginUser(@PathParam("percent") String percent, String XML) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<stock uri=").append(QUOTE).append(context.getAbsolutePath()).append(QUOTE).append(">");

        if (percent.equals("positive")) {
            Location location;
            try {
                String attr[] = XML.split(",");
                location = new Location(attr[0], attr[1], attr[2], attr[3]);
                String sqlException = locationBean.userStates(location, 1);
                if (sqlException != null) {
                    return "<error><reason>" + sqlException + "</reason></error>";
                }
            } catch (Exception e) {
                return "<error><reason>Invalid content</reason></error>";
            }
            return location.getXMLString();
        } else if (percent.equals("negative")) {
            Location location;
            try {
                String attr[] = XML.split(",");
                location = new Location(attr[0], attr[1], attr[2], attr[3]);
                String sqlException = locationBean.userStates(location, 0);
                if (sqlException != null) {
                    return "<error><reason>" + sqlException + "</reason></error>";
                }
            } catch (Exception e) {
                return "<error><reason>Invalid content</reason></error>";
            }
            return location.getXMLString();
        }else if (percent.equals("longlat")) {
            Location location;
            try {
                String attr[] = XML.split(",");
                location = new Location(attr[0], attr[1], attr[2], attr[3]);
                String sqlException = locationBean.updateUserLocation(location);
                if (sqlException != null) {
                    return "<error><reason>" + sqlException + "</reason></error>";
                }
            } catch (Exception e) {
                return "<error><reason>Invalid content</reason></error>";
            }
            return location.getXMLString();
            
        }
        buffer.append("</stock>");

        return buffer.toString();
    }

}
