<%--

This file is for prompting before escalating to a group.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/escalateTicketPrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%@  include file="../top.jspf" %>
<dspel:page xml="true">
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />

  <dspel:importbean var="reasonManager" bean="/atg/ticketing/ActivityReasonManager"/>
  <dspel:importbean var="ticketingTools" bean="/atg/svc/agent/ticketing/TicketingTools"/>
  <dspel:form action="#" formid="escalateTicketForm" id="escalateTicketForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/EscalateTicket.changeEnvironment"/>
    <dspel:input type="hidden" value="escalate" name="tdoption" bean="/atg/svc/agent/ui/formhandlers/EscalateTicket.ticketDispositionOptions.dispositionOption"/>
    <dspel:input type="hidden" value="" name="reasonCode" bean="/atg/svc/agent/ui/formhandlers/EscalateTicket.ticketDispositionOptions.reasonCode"/>
    <dspel:input type="hidden" value="" name="noteText" bean="/atg/svc/agent/ui/formhandlers/EscalateTicket.ticketDispositionOptions.ticketNote"/>
    <dspel:input type="hidden" value="" name="share" bean="/atg/svc/agent/ui/formhandlers/EscalateTicket.ticketDispositionOptions.publicNote"/>
    <dspel:input type="hidden" name="escalationLevel" value="" bean="/atg/svc/agent/ui/formhandlers/EscalateTicket.ticketDispositionOptions.inputParameters.escalationLevel"/>
    <dspel:input type="hidden" name="group" value="" bean="/atg/svc/agent/ui/formhandlers/EscalateTicket.ticketDispositionOptions.inputParameters.group"/>
    <div class="popupwindow">
      <h4><fmt:message key="group"/></h4>
      <select name="escalateGroupSelect">
        <c:forEach items="${ticketingTools.ticketQueues}" var="group">
          <dspel:tomap var="groupMap" value="${group}"/>
<c:if test="${ticketingTools.ticketingManager.defaultEscalationTicketQueueName == groupMap.name}">
          <option value="<c:out value='${groupMap.id}'/>" selected="selected">
            <c:out value="${groupMap.name}" />
          </option>
</c:if>
<c:if test="${ticketingTools.ticketingManager.defaultEscalationTicketQueueName != groupMap.name}">
          <option value="<c:out value='${groupMap.id}'/>">
            <c:out value="${groupMap.name}" />
          </option>
</c:if>
        </c:forEach>
      </select>
      <h4><fmt:message key="new-escalation-level"/></h4>
      <select name="escalatePromptSelect" style="width:25%;">
        <option value="${ticketingTools.ticketingManager.defaultEscalatedTicketEscalationLevel}" selected="selected"><fmt:message key="default"/></option>z
        <dspel:droplet name="/atg/dynamo/droplet/PossibleValues">
      	  <dspel:param name="repository" value="${ticketingTools.ticketingManager.ticketingRepository}" />
      	  <dspel:param name="itemDescriptorName" value="ticket" />
          <dspel:param name="propertyName" value="escalationLevel" />
          <dspel:param name="returnValueObjects" value="true" />
          <dspel:oparam name="output">
            <dspel:getvalueof var="values" param="values" />
            <c:forEach items="${values}" var="option" begin="${ticketingTools.nextHigherEscalationLevelForCurrentTicket}">
              <option value="${option.settableValue}">${fn:escapeXml(option.localizedLabel)}</option>
            </c:forEach>
          </dspel:oparam>
        </dspel:droplet>
      </select>
      
      <h4><fmt:message key="reason-code"/></h4>
      <select name="escalateReasonCode" id="escalateReasonCode" >
      <c:set var="reasonCodes" value="${reasonManager.contextNameToReasonNamesMap}"/>
        <c:forEach items="${reasonCodes['Ticketing.EscalatePrompt']}" var="reason">
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
      <textarea name="escalateTicketNote" id="escalateTicketNote" cols="50" rows="5" style="height:70px;"></textarea>
<%@  include file="noteTable.jspf" %>
<div><input type="checkbox" id="escalateTicketShare" value="share"/><label for="escalateTicketShare"><fmt:message key="share-with-customer-label"/></label></div>
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="escalateTicketOk">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall" 
           id="escalateTicketCancel" 
           onclick="atg.service.ticketing.cancelEscalateTicketPrompt();return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/escalateTicketPrompt.jsp#1 $$Change: 946917 $--%>
