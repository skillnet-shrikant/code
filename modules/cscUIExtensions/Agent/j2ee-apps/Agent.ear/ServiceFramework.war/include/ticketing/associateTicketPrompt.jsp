<%--
This file is for prompting before ticket close.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/associateTicketPrompt.jsp#2 $$Change: 1179550 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="../top.jspf" %>
<dspel:page xml="true">
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />
  <dspel:importbean var="ticketingFormHandler" bean="/atg/svc/ui/formhandlers/TicketingFormHandler"/>  
  <dspel:form action="#" id="associateTicketForm" formid="associateTicketForm"
    onsubmit="atg.service.ticketing.associateTicketFromPopupIfValueSet();return false;">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.associateTicket"/>
    <dspel:input type="hidden" name="ticketId" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
    <div class="popupwindow">
      <h4><fmt:message key="ticket-id"/></h4>
      <input type="text" name="associateTicketId" id="associateTicketId" />
      
      <script type="text/javascript" charset="utf-8">
        dojo.connect(dojo.byId("associateTicketId"), 'onkeypress', function(e) {
        	  // On IE you get the window passed in the 
        	  // param. Use the event in the window object
        	  var e  = e || e.event;
        		if (e.keyCode == dojo.keys.ENTER) {
        			atg.service.ticketing.associateTicketFromPopupIfValueSet();
        			e.preventDefault();
        		  //console.debug('Ticket Has been Associated');
        		}
        	});
      </script>
      
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="associateTicketOk"
           onclick="atg.service.ticketing.associateTicketFromPopupIfValueSet();">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall" 
           id="associateTicketCancel" 
           onclick="atg.service.ticketing.cancelAssociateTicketPrompt();return false;"
           onkeypress="atg.service.ticketing.cancelAssociateTicketPrompt(); return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/associateTicketPrompt.jsp#2 $$Change: 1179550 $--%>
