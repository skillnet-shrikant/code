<%--
This file is for prompting before ticket close.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/closeAsDuplicateTicketPrompt.jsp#2 $$Change: 1179550 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="../top.jspf" %>
<dspel:page xml="true">
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />
  <dspel:importbean var="ticketingManager" bean="/atg/ticketing/TicketingManager"/>

  <dspel:form id="closeAsDuplicateTicketForm" formid="closeAsDuplicateTicketForm" action="#"
    onsubmit="atg.service.ticketing.closeTicketAsDuplicateFromPopupIfValueSet();return false;">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/CloseTicketAsDuplicate.changeEnvironment"/>
    <dspel:input type="hidden" value="closeAsDuplicate" name="tdoption" bean="/atg/svc/agent/ui/formhandlers/CloseTicketAsDuplicate.ticketDispositionOptions.dispositionOption"/>
    <dspel:input type="hidden" value="" name="reasonCode" bean="/atg/svc/agent/ui/formhandlers/CloseTicketAsDuplicate.ticketDispositionOptions.reasonCode"/>
    <dspel:input type="hidden" value="" name="ticketId" bean="/atg/svc/agent/ui/formhandlers/CloseTicketAsDuplicate.ticketDispositionOptions.inputParameters.duplicateTicketId"/>
    <dspel:input type="hidden" value="" name="noteText" bean="//atg/svc/agent/ui/formhandlers/CloseTicketAsDuplicate.ticketDispositionOptions.ticketNote"/>
    <dspel:input type="hidden" value="${ticketingManager.ticketStatusManager.closedAsDuplicateSubStatusName}" name="subStatus" bean="/atg/svc/agent/ui/formhandlers/CloseTicketAsDuplicate.ticketDispositionOptions.subStatus"/>
    <dspel:input type="hidden" value="" name="share" bean="/atg/svc/agent/ui/formhandlers/CloseTicketAsDuplicate.ticketDispositionOptions.publicNote"/>
    <div class="popupwindow">
      <h4><fmt:message key="duplicate-of"/></h4>
      <input type="text" name="closeAsDuplicateTicketId" id="closeAsDuplicateTicketId" style="width:97%"/>
      <script type="text/javascript" charset="utf-8">
        dojo.connect(dojo.byId("closeAsDuplicateTicketId"), 'onkeypress', function(e) {
            // On IE you get the window passed in the 
            // param. Use the event in the window object
            var e  = e || e.event;
        		if (e.keyCode == dojo.keys.ENTER) {
        			atg.service.ticketing.closeTicketAsDuplicateFromPopupIfValueSet();
        			e.preventDefault();
        		  console.debug('Ticket Has been closed as Duplicate');
        		}
        	});
      </script>
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="closeAsDuplicateTicketOk"
           onclick="atg.service.ticketing.closeTicketAsDuplicateFromPopupIfValueSet();">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall" 
           id="closeAsDuplicateTicketCancel" 
           onclick="atg.service.ticketing.cancelCloseAsDuplicateTicketPrompt();return false;"
           onkeypress="atg.service.ticketing.cancelCloseAsDuplicateTicketPrompt(); return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/closeAsDuplicateTicketPrompt.jsp#2 $$Change: 1179550 $--%>
