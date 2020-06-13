package locationrestfulservice;

import java.io.StringReader;
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
import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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

        Collection<Location> allStock = locationBean.getLocationList();

        for(Location stock : allStock) {
            buffer.append(stock.getXMLString());
        }

        buffer.append("</stock>");

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

        for(Location stock : allStock) {
        }

        buffer.append("</stock>");

        return buffer.toString();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String simpleTest(String XML) {
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><a><b></b><c></c></a>";  

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try {  
            builder = factory.newDocumentBuilder();  
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));  
            NodeList users = document.getElementsByTagName("user");
            System.out.println(">>>>>>>>>>.."+users.toString());
        } catch (Exception e) {  
            e.printStackTrace();  
            return "failed";
        } 
        return XML;
    
    };
    
}
