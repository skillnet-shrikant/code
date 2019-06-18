<%--
This file is for prompting before assigning to another agent.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/assignTicketToAgentPrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="../top.jspf" %>
<dspel:page xml="true">
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />
  <dspel:importbean var="ticketingTools" bean="/atg/svc/agent/ticketing/TicketingTools"/>  
  <dspel:importbean var="reasonManager" bean="/atg/ticketing/ActivityReasonManager"/>
  <dspel:form action="#" id="assignTicketToAgentForm" formid="assignTicketToAgentForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/ReassignTicket.changeEnvironment"/>
    <dspel:input type="hidden" value="reassign" name="tdoption" bean="/atg/svc/agent/ui/formhandlers/ReassignTicket.ticketDispositionOptions.dispositionOption"/>
    <dspel:input type="hidden" name="reasonCode" value="" bean="/atg/svc/agent/ui/formhandlers/ReassignTicket.ticketDispositionOptions.reasonCode"/>
    <dspel:input type="hidden" value="" name="noteText" bean="/atg/svc/agent/ui/formhandlers/ReassignTicket.ticketDispositionOptions.ticketNote"/>
    <dspel:input type="hidden" value="" name="share" bean="/atg/svc/agent/ui/formhandlers/ReassignTicket.ticketDispositionOptions.publicNote"/>
    <dspel:input type="hidden" name="reassignAgent" value="" bean="/atg/svc/agent/ui/formhandlers/ReassignTicket.ticketDispositionOptions.inputParameters.reassignAgent"/>
  <dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />
  <div class="popupwindow">
      <h4><fmt:message key="reassign-agent"/></h4>
      <select name="agentSelect">
        <c:forEach items="${ticketingTools.allAgents}" var="agent">
          <dspel:tomap var="agentMap" value="${agent}"/>
          <c:set var="login" value=""/>
          <c:set var="firstName" value=""/>
          <c:set var="lastName" value=""/>
          <c:if test="${agentMap.login != null}"><c:set var="login" value="${agentMap.login}"/></c:if>
          <c:if test="${agentMap.firstName != null}"><c:set var="firstName" value="${agentMap.firstName}"/></c:if>
          <c:if test="${agentMap.lastName != null}"><c:set var="lastName" value="${agentMap.lastName}"/></c:if>
          <c:choose>
          <c:when test="${(lastName != null && not empty lastName) || (firstName != null && not empty firstName)}">
            <fmt:message var="agentName" key="agent-full-name">
              <fmt:param value="${login}"/>
              <fmt:param value="${firstName}"/>
              <fmt:param value="${lastName}"/>
            </fmt:message>
          </c:when>
          <c:otherwise>
            <c:set var="agentName" value="${login}"/>
          </c:otherwise>
          </c:choose>
          <option value="<c:out value='${agentMap.id}'/>">
            <c:out value="${agentName}" escapeXml="false"/>
          </option>
        </c:forEach>
      </select>
      <h4><fmt:message key="reason-code"/></h4>
      <select id="assignTicketToAgentReasonCode" >
      <c:set var="reasonCodes" value="${reasonManager.contextNameToReasonNamesMap}"/>
        <c:forEach items="${reasonCodes['Ticketing.ReassignPrompt']}" var="reason">
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
      <textarea name="assignTicketToAgentNote" id="assignTicketToAgentNote" cols="50" rows="5" style="height:70px;"></textarea>
<%@  include file="noteTable.jspf" %>
 <div>
        <input type="checkbox" id="assignTicketToAgentShare" value="share" /><label for="assignTicketToAgentShare"><fmt:message key="share-with-customer-label"/></label>
      </div>
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="assignAgentOk">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall" 
           id="assignTicketToAgentCancel" 
           onclick="atg.service.ticketing.cancelAssignTicketToAgentPrompt();return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/assignTicketToAgentPrompt.jsp#1 $$Change: 946917 $--%>
