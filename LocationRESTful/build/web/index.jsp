<%-- 
    Document   : index
    Created on : 20/05/2020, 9:53:12 AM
    Author     : Rob
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Location Restful</title>
    </head>
    <body>
        <h1>Location Restful Web Service</h1>
        
        <a style="text-decoration: none; text-transform: uppercase" href="<%= response.encodeURL(request.getContextPath())%>/locationservice/location">Get all logged user data</a></br>
        <a href="<%= response.encodeURL(request.getContextPath())%>/locationservice/location/positive">Log user</a></br>
        <a href="<%= response.encodeURL(request.getContextPath())%>/locationservice/location/negative">Logout user</a></br>
        <a href="<%= response.encodeURL(request.getContextPath())%>/locationservice/location/longlat">Update longlat user</a></br>
        
    </body>
</html>
