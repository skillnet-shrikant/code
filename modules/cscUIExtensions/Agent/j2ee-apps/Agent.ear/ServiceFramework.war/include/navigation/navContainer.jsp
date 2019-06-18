<%--
 Generic Navigation Container Renderer
 This file iterates over the nav items stored inside the nav container and calls out to render each nav item individually
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navContainer.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">

<dspel:importbean bean="/atg/dynamo/droplet/ForEach" />
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<dspel:getvalueof var="navContainer" param="navContainer"/>

<%-- Set up droplet to iterate over nav items within nav container --%>
<dspel:droplet name="ForEach">
  <dspel:param name="array" param="navContainer.allNavItems"/>
  <dspel:param name="sortProperties" value="+sortPriority"/>
  <dspel:setvalue param="navItem" paramvalue="element"/>
    
  <%-- Extract nav item and pass it to JSP for rendering --%>
  <dspel:oparam name="output">
    <dspel:include src="/include/navigation/navItem.jsp">
      <dspel:param name="navItem" param="navItem" />
    </dspel:include>
  </dspel:oparam>

</dspel:droplet>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navContainer.jsp#1 $$Change: 946917 $--%>
