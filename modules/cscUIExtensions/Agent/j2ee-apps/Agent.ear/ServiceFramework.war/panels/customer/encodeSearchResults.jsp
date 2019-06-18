<%--
 This page encodes the search results as JSON
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/encodeSearchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dspel:page>

<dspel:importbean var="formHandler" bean="/atg/svc/agent/ui/formhandlers/CustomerSearchTreeQueryFormHandler"/>
<dspel:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>

<dspel:getvalueof var="searchResponse" value="${formHandler.searchResponse}"/>
<dspel:getvalueof var="items" value="${searchResponse.items}"/>

<%
//is there a taglib to convert int to String?
java.util.ArrayList items = 
  (java.util.ArrayList)pageContext.getAttribute("items");
  
String size = Integer.toString(items.size());

pageContext.setAttribute("size", size);

%>

<json:object prettyPrint="${UIConfig.prettyPrintResponses}">


  <json:property name="resultLength" value="${size}"/>
  <json:property name="currentPage" value="${searchResponse.pageNum}"/>
  
  <json:array name="results" items="${searchResponse.items}" var="customerItem">
  
    <dspel:droplet name="/atg/targeting/RepositoryLookup">  
      <dspel:param name="url" value="${customerItem.id}"/>
      <dspel:oparam name="output">
        <dspel:tomap var="currentCustomer" param="element"/>
      </dspel:oparam>
    </dspel:droplet>
    
    <json:object>
       <json:property name="login" value="${currentCustomer.login}"/>
       <json:property name="id" value="${currentCustomer.id}"/>
       <json:property name="lastName" value="${currentCustomer.lastname}"/>
       <json:property name="firstName" value="${currentCustomer.firstname}"/>
       <dspel:tomap var="address" value="${currentCustomer[agentUIConfig.customerAddressPropertyName]}"/>
       <json:property name="postal" value="${address.postalCode}"/>
       <json:object name="address">
          <json:property name="address1" value="${address.address1}"/>
          <json:property name="city" value="${address.city}"/>
          <json:property name="state" value="${address.state}"/>
          <json:property name="postal" value="${address.postalCode}"/>
       </json:object>
       <json:property name="email" value="${currentCustomer.email}"/>
       <json:property name="phone" value="${address.phoneNumber}"/>
    </json:object>
    
  </json:array>
</json:object>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/encodeSearchResults.jsp#1 $$Change: 946917 $--%>
