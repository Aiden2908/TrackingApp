package com.example.mapapp;

import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

//:: A class to handle sending request to REST-API and receiving data :://
public class XML_Request implements Runnable {
    public static String API="http://10.0.2.2:8080/LocationRESTful/locationservice/location/";
    public static String LOGIN_URL="http://10.0.2.2:8080/LocationRESTful/locationservice/location/positive";
    private String url;
    private String method;
    private ArrayList<Location> locations;
    private boolean isSuccess=false;
    XML_Request(String url,String method){
        this.url=url;
        this.method=method;
        Log.i(this.getClass().getName(),"CREATE ");

    }
    @Override
    public void run() {
        if(method.equals("GET")) {
            Log.i(this.getClass().getName(),"START ");
            try {
                Document document = requestXML();
                parseXML(document);
                onResponse();
            } catch (ParserConfigurationException | XmlPullParserException | TransformerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
    }

    //:: Overridable method to be used on response from REST-API :://
    void onResponse(){

    }

    ArrayList<Location> getResponse(){
        return locations;
    }

    //:: A method to handle sending request to REST-API :://
    private Document requestXML()  throws ParserConfigurationException, MalformedURLException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new URL(url).openStream());
        if(doc==null){
            Log.i(this.getClass().getName(),"IS NULL CUNT ");
        }
        return doc;
    }

    //:: A method to handle parsing XML response :://
    private void parseXML(Document doc) throws XmlPullParserException, TransformerException, IOException {
        if(doc==null) {
            Log.d("TAGG", "IS NULLLLL");
        }else {
            Log.d("TAGG", "IS NOT NULL");
        }

        XmlPullParserFactory pullParserFactory= XmlPullParserFactory.newInstance();
        XmlPullParser parser= pullParserFactory.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(doc);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

        parser.setInput(is,null);
        processParsing(parser);
    }
    private void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<Location> locations=new ArrayList<>();
        int eventType=parser.getEventType();
        Location currentLocation=null;

        while (eventType!= XmlPullParser.END_DOCUMENT){
            String eltName=null;

            switch (eventType){
                case XmlPullParser.START_TAG:
                    eltName=parser.getName();
                    if("user".equals(eltName)){
                        currentLocation=new Location();
                        locations.add(currentLocation);
                    }else if (currentLocation!=null){
                        if("email".equals(eltName)){
                            currentLocation.email=parser.nextText();
                        }
                        else if("password".equals(eltName)){
                            currentLocation.password=parser.nextText();
                        }
                        else if("longitude".equals(eltName)){
                            currentLocation.longitude=parser.nextText();
                        }
                        else if("latitude".equals(eltName)){
                            currentLocation.latitude=parser.nextText();
                        }
                    }
                    break;

            }
            eventType=parser.next();
        }
//        locations.remove(0);
        this.locations =locations;
    }

    private HttpURLConnection postRequest(String URL) throws IOException {
        URL url = new URL(URL);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST"); // PUT is another valid option
        http.setDoOutput(true);

        return http;
    }
    String registerUser(String email,String password) throws IOException {
        HttpURLConnection http=postRequest(API);
        String data=email+","+password+",0,0";
        byte[] out = data.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                return inputLine;
            }
            in.close();
        }
        return null;
    }
     String loginUser(String email,String password) throws IOException {
         HttpURLConnection http=postRequest(LOGIN_URL);
         String data=email+","+password+",0,0";
         byte[] out = data.getBytes(StandardCharsets.UTF_8);
         int length = out.length;

         http.setFixedLengthStreamingMode(length);
         http.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
         http.connect();
         try (OutputStream os = http.getOutputStream()) {
             os.write(out);

             //System.out.println(http.getInputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
             String inputLine;
             while ((inputLine = in.readLine()) != null){
                 return inputLine;
             }
             in.close();
         }
         return null;
    }


}
