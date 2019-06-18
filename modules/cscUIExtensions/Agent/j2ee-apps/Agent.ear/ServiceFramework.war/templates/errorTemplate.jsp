 <%--
 
 This page defines the error panel template
 
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/errorTemplate.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf" %>

<dspel:page xml="true">
  <dspel:getvalueof var="panelStackId" param="panelStackId"/>
  <dspel:getvalueof var="panelId" param="panelId"/>
  <dspel:getvalueof var="ticketId" bean="/atg/svc/ticketing/TicketHolder.currentTicket.id"/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

      <div class="panelContent" id="errorPanelContent">
      </div>

  </dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/errorTemplate.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/errorTemplate.jsp#1 $$Change: 946917 $--%>
