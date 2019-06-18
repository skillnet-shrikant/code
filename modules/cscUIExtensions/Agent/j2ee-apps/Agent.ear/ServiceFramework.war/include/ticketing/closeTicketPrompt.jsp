<%--

This file is for prompting before ticket close.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/closeTicketPrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%@  include file="../top.jspf" %>
<dspel:page xml="true">
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />

  <dspel:importbean var="reasonManager" bean="/atg/ticketing/ActivityReasonManager"/>
  <dspel:importbean var="ticketingManager" bean="/atg/ticketing/TicketingManager"/>
  <dspel:form id="closeTicketForm" formid="closeTicketForm" action="#">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/CloseTicket.changeEnvironment"/>
    <dspel:input type="hidden" value="close" name="tdoption" bean="/atg/svc/agent/ui/formhandlers/CloseTicket.ticketDispositionOptions.dispositionOption"/>
    <!-- this param isn't used but must be there forethe javascript function to work - shared with close as duplicate javascript -->
    <input type="hidden" value="" name="ticketId">
    <dspel:input type="hidden" value="" name="reasonCode" bean="/atg/svc/agent/ui/formhandlers/CloseTicket.ticketDispositionOptions.reasonCode"/>
    <dspel:input type="hidden" value="" name="noteText" bean="/atg/svc/agent/ui/formhandlers/CloseTicket.ticketDispositionOptions.ticketNote"/>
    <dspel:input type="hidden" value="${ticketingManager.ticketStatusManager.closedSubStatusName}" name="subStatus" bean="/atg/svc/agent/ui/formhandlers/CloseTicket.ticketDispositionOptions.subStatus"/>
    <dspel:input type="hidden" value="" name="share" bean="/atg/svc/agent/ui/formhandlers/CloseTicket.ticketDispositionOptions.publicNote"/>
    <div class="popupwindow">
      <h4><fmt:message key="reason-code"/></h4>
      <select id="closeReasonCode">
      <c:set var="reasonCodes" value="${reasonManager.contextNameToReasonNamesMap}"/>
        <c:forEach items="${reasonCodes['Ticketing.ClosePrompt']}" var="reason">
          <option value="<c:out value='${reason}'/>">
            <dspel:droplet name="/atg/ticketing/ActivityReasonDescription">
              <dspel:param name="descriptionId" value="${reason}"/>
              <dspel:param name="elementName" value="description"/>
              <dspel:oparam name="output">
                <dspel:getvalueof var="description" param="description"/>
                <c:out value="${description}"/>
              </dspel:oparam>
            </dspel:droplet>
          </option>
        </c:forEach>
      </select>
      <h4><fmt:message key="enter-note"/></h4>
      <textarea name="closeTicketNote" id="closeTicketNote" cols="50" rows="5" style="height:70px;"></textarea>
<%@  include file="noteTable.jspf" %>
     <div>
        <input type="checkbox" id="closeTicketShare" value="share" /><label for="closeTicketShare"><fmt:message key="share-with-customer-label"/></label>
      </div>
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="closeOk">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall" 
           id="closeCancel" 
           onclick="atg.service.ticketing.cancelCloseTicketPrompt();return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/closeTicketPrompt.jsp#1 $$Change: 946917 $--%>
