<%--

This file is for prompting before ticket close.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/mergeTicketPrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%@  include file="../top.jspf" %>
<dspel:page xml="true">
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />

  <dspel:importbean var="ticketingFormHandler" bean="/atg/svc/ui/formhandlers/TicketingFormHandler"/>  
  <dspel:form action="#" id="mergeTicketForm" formid="mergeTicketForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.mergeTicket"/>
    <dspel:input type="hidden" name="ticketId" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
    <div class="popupwindow">
      <h4><fmt:message key="merge-ticket"/></h4>
      <input type="text" name="mergeTicketId" id="mergeTicketId"/>
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="mergeTicketOk"
           onclick="window.ticketId=document.getElementById('mergeTicketForm').mergeTicketId.value;mergeTicket(); atg.service.ticketing.cancelMergeTicketPrompt(); return false;">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall" 
           id="mergeTicketCancel" 
           onclick="atg.service.ticketing.cancelMergeTicketPrompt();return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/mergeTicketPrompt.jsp#1 $$Change: 946917 $--%>
