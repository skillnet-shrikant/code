<%--
This file is for prompting before releasing a ticket back to the queue.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/releaseTicketPrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="../top.jspf" %>
<dspel:page xml="true">
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />
  <dspel:importbean var="reasonManager" bean="/atg/ticketing/ActivityReasonManager"/>
  <dspel:form action="#" id="releaseTicketForm" formid="releaseTicketForm">
    <dspel:input type="hidden" priority="-10" value="" name="release" bean="/atg/svc/agent/ui/formhandlers/ReleaseTicket.changeEnvironment"/>
    <dspel:input type="hidden" value="release" name="tdoption" bean="/atg/svc/agent/ui/formhandlers/ReleaseTicket.ticketDispositionOptions.dispositionOption"/>
    <dspel:input type="hidden" name="reasonCode" value="" bean="/atg/svc/agent/ui/formhandlers/ReleaseTicket.ticketDispositionOptions.reasonCode"/>
    <dspel:input type="hidden" value="" name="noteText" bean="/atg/svc/agent/ui/formhandlers/ReleaseTicket.ticketDispositionOptions.ticketNote"/>
    <dspel:input type="hidden" value="" name="share" bean="/atg/svc/agent/ui/formhandlers/ReleaseTicket.ticketDispositionOptions.publicNote"/>
    <div class="popupwindow">
      <h4><fmt:message key="reason-code"/></h4>
      <select id="releaseReasonCode" >
      <c:set var="reasonCodes" value="${reasonManager.contextNameToReasonNamesMap}"/>
        <c:forEach items="${reasonCodes['Ticketing.ReleasePrompt']}" var="reason">
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
      <textarea name="releaseTicketNote" id="releaseTicketNote" cols="50" rows="5" style="height:70px;"></textarea>
<%@  include file="noteTable.jspf" %>
  <div>
        <input type="checkbox" id="releaseTicketShare" value="share"/><label for="releaseTicketShare"><fmt:message key="share-with-customer-label"/></label>
      </div>
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="releaseTicketOk">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall" 
           id="releaseCancel" 
           onclick="atg.service.ticketing.cancelReleaseTicketPrompt();return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/releaseTicketPrompt.jsp#1 $$Change: 946917 $--%>
