<%--
  Copyright (c) 2002 by Phil Hanna
  All rights reserved.
  
  You may study, use, modify, and distribute this
  software for any purpose provided that this
  copyright notice appears in all copies.
  
  This software is provided without warranty
  either expressed or implied.
--%>
<dsp:page>
<%@ page import="java.util.*" %>
<dsp:importbean bean="/atg/dynamo/Configuration" />
<html>
   <head>
      <title>Echo</title>
      <style>
      </style>
   </head>
   <body>
      <h1>HTTP Request Headers Received</h1>
      <table border="1" cellpadding="4" cellspacing="0">
      <%
         Enumeration eNames = request.getHeaderNames();
         while (eNames.hasMoreElements()) {
            String name = (String) eNames.nextElement();
            String value = normalize(request.getHeader(name));
      %>
         <tr><td><%= name %></td><td><%= value %></td></tr>
      <%
         }
      %>
      <tr>
     	 	<td>
     	 		Host:
      		</td>
      		<td>
      			<dsp:valueof bean="Configuration.thisHostname" />
      		</td>
      </tr>
      <tr>
     	 	<td>
     	 		Atg http Port:
      		</td>
      		<td>
      			<dsp:valueof bean="Configuration.httpPort" />
      		</td>
      </tr>
      <tr>
      		<td>
      			Instance name:
      		</td>
      		<td>
      			<%= System.getProperty("weblogic.Name") %>
      		</td>
      		
      </tr>
      
      </table>
   </body>
</html>
<%!
   private String normalize(String value)
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         sb.append(c);
         if (c == ';')
            sb.append("<br>");
      }
      return sb.toString();
   }
%>
</dsp:page>