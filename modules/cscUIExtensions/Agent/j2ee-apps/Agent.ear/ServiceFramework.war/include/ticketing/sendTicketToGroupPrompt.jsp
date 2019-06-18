<%--
This file is for prompting before assigning to another agent.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/sendTicketToGroupPrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="../top.jspf" %>
<dspel:page xml="true">
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />
  <dspel:importbean var="reasonManager" bean="/atg/ticketing/ActivityReasonManager"/>
  <dspel:importbean var="ticketingTools" bean="/atg/svc/agent/ticketing/TicketingTools"/>
  <dspel:form action="#" id="sendTicketForm" formid="sendTicketForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/SendToGroup.changeEnvironment"/>
    <dspel:input type="hidden" value="sendToGroup" name="tdoption" bean="/atg/svc/agent/ui/formhandlers/SendToGroup.ticketDispositionOptions.dispositionOption"/>
    <dspel:input type="hidden" value="" name="reasonCode" bean="/atg/svc/agent/ui/formhandlers/SendToGroup.ticketDispositionOptions.reasonCode"/>
    <dspel:input type="hidden" value="" name="noteText" bean="/atg/svc/agent/ui/formhandlers/SendToGroup.ticketDispositionOptions.ticketNote"/>
    <dspel:input type="hidden" value="" name="share" bean="/atg/svc/agent/ui/formhandlers/SendToGroup.ticketDispositionOptions.publicNote"/>
    <dspel:input type="hidden" name="sendToGroup" value="" bean="/atg/svc/agent/ui/formhandlers/SendToGroup.ticketDispositionOptions.inputParameters.group"/>
    <div class="popupwindow"> 
      <h4><fmt:message key="group"/></h4>
      <select name="groupSelect">
        <c:forEach items="${ticketingTools.ticketQueues}" var="group">
          <dspel:tomap var="groupMap" value="${group}"/>
          <option value="<c:out value='${groupMap.id}'/>">
            <c:out value="${groupMap.name}" />
          </option>
        </c:forEach>
      </select>
      <h4><fmt:message key="reason-code"/></h4>
      <select id="sendTicketReasonCode">
      <c:set var="reasonCodes" value="${reasonManager.contextNameToReasonNamesMap}"/>
        <c:forEach items="${reasonCodes['Ticketing.SendToQueuePrompt']}" var="reason">
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
      <textarea name="sendTicketNote" id="sendTicketNote" cols="50" rows="5" style="height:70px;"></textarea>
<%@  include file="noteTable.jspf" %>
  <div>  
        <input type="checkbox" id="sendTicketShare" value="share"/><label for="sendTicketShare"><fmt:message key="share-with-customer-label"/></label>
      </div>
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="sendTicketOk">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall" 
           id="sendTicketCancel" 
           onclick="atg.service.ticketing.cancelSendTicketPrompt();return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/sendTicketToGroupPrompt.jsp#1 $$Change: 946917 $--%>
