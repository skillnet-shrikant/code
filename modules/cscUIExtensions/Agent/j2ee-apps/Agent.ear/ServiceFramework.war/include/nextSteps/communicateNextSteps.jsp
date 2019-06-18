<%--

Next steps

--%>

<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:getvalueof var="isTransient" bean="/atg/svc/ticketing/TicketHolder.currentTicket.transient"/>
  <dspel:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
  <dspel:importbean bean="/atg/svc/ticketing/TicketHolder"/>
  <dspel:getvalueof var="currentTicketItem" bean="/atg/svc/ticketing/TicketHolder.currentTicket"/>
  <dspel:tomap var="currentTicket" value="currentTicketItem"/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

    <%-- Discard Email --%>

    <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="actionId" value="discardEmail"/>
      <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_dicardChanges.gif"/>
      <dspel:param name="labelKey" value="nextSteps.discard.label"/>
      <dspel:param name="formhandler" value="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.discard"/>
    </dspel:include>

    <%-- Send Email --%>
    <dspel:form style="display:none" formid="sendEmailForm" id="sendEmailForm" action="#">
      <dspel:input priority="-10" id="send" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.send"/>
      <dspel:input id="htmlBody" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.htmlBody"/>
      <dspel:input id="textBody" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.textBody"/>
      <dspel:input id="subject" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.subject"/>
      <dspel:input id="to" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.to"/>
      <dspel:input id="cc" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.cc"/>
      <dspel:input id="bcc" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.bcc"/>
      <dspel:input id="textOnly" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.textOnly"/>
      <dspel:input id="channelId" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.channelId"/>
    </dspel:form>
    <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="actionId" value="sendEmail"/>
      <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_sendEmail.gif"/>
      <dspel:param name="labelKey" value="nextSteps.sendEmail.label"/>
    </dspel:include>


    <%-- Send Email and Close Ticket --%>
    <dspel:importbean var="ticketingManager" bean="/atg/ticketing/TicketingManager"/>
    <dspel:form style="display:none" formid="sendEmailCloseForm" id="sendEmailCloseForm" action="#">
      <dspel:input priority="-10" id="send" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.changeEnvironment"/>
      <dspel:input id="tdoption" type="hidden" value="close" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.ticketDispositionOptions.dispositionOption"/>
      <dspel:input id="subStatus" type="hidden" value="${ticketingManager.ticketStatusManager.closedSubStatusName}" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.ticketDispositionOptions.subStatus"/>
      <dspel:input id="htmlBody" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.htmlBody"/>
      <dspel:input id="textBody" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.textBody"/>
      <dspel:input id="subject" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.subject"/>
      <dspel:input id="to" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.to"/>
      <dspel:input id="cc" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.cc"/>
      <dspel:input id="bcc" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.bcc"/>
      <dspel:input id="textOnly" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.textOnly"/>
      <dspel:input id="channelId" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/SendAndCloseTicket.channelId"/>
    </dspel:form>
    <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="actionId" value="sendEmailClose"/>
      <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_sendAndClose.gif"/>
      <dspel:param name="labelKey" value="nextSteps.sendEmailClose.label"/>
    </dspel:include>

    <%-- If Response Management is installed render these extra ticketing operations --%>
    <svc-ui:isElementLicensed var="responseManagementLicense" name="ResponseManagementLicense"/>
    <c:if test="${responseManagementLicense}">
    
      <c:if test="${not empty currentTicket}">
        <%-- Separator --%>
        <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="isSeparator" value="${true}"/>
        </dspel:include>
      </c:if>

      <%-- Escalate Ticket --%>
      <c:if test="${not empty currentTicket}">
        <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="actionJavaScript" value="ResponseSaveState(); atg.service.ticketing.escalateTicketPrompt(atg.service.ticketing.respondEscalateTicketFunc);"/>
          <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_escalate.gif"/>
          <dspel:param name="labelKey" value="nextSteps.escalateTicket.label"/>
        </dspel:include>
      </c:if>

      <%-- Close Ticket --%>
      <c:if test="${not empty currentTicket && !isTransient}">
        <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="actionJavaScript" value="ResponseSaveState();atg.service.ticketing.closeTicketPrompt(atg.service.ticketing.respondCloseTicketFunc);"/>
          <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_closeTicket.gif"/>
          <dspel:param name="labelKey" value="nextSteps.closeTicket.label"/>
        </dspel:include>
      </c:if>

      <%-- Release Ticket --%>
      <c:if test="${not empty currentTicket && !isTransient}">
        <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="actionJavaScript" value="ResponseSaveState();atg.service.ticketing.releaseTicketPrompt(atg.service.ticketing.respondReleaseTicketFunc);"/>
          <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_releaseTicketQueue.gif"/>
          <dspel:param name="labelKey" value="nextSteps.releaseTicket.label"/>
        </dspel:include>
      </c:if>

      <%-- Defer Ticket --%>
      <c:if test="${not empty currentTicket && !isTransient}">
        <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="actionJavaScript" value="ResponseSaveState();atg.service.ticketing.deferTicketPrompt(atg.service.ticketing.respondDeferTicketFunc);"/>
          <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_deferTicket.gif"/>
          <dspel:param name="labelKey" value="nextSteps.deferTicket.label"/>
        </dspel:include>
      </c:if>

      <%-- Reassign Ticket --%>
      <c:if test="${not empty currentTicket && !isTransient}">
        <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="actionJavaScript" value="ResponseSaveState(); atg.service.ticketing.assignTicketToAgentPrompt(atg.service.ticketing.respondAssignTicketFunc);"/>
          <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_reassignTicket.gif"/>
          <dspel:param name="labelKey" value="nextSteps.reassignTicket.label"/>
        </dspel:include>
      </c:if>

      <%-- Send Ticket to Group --%>
      <c:if test="${not empty currentTicket && !isTransient}">
        <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="actionJavaScript" value="ResponseSaveState(); atg.service.ticketing.sendTicketPrompt(atg.service.ticketing.respondSendTicketFunc);"/>
          <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_pushToGroups.gif"/>
          <dspel:param name="labelKey" value="nextSteps.sendTicket.label"/>
        </dspel:include>
      </c:if>

    </c:if>

    <c:set var="hasResearchTab" value="${false}"/>
    <c:forEach items="${framework.frameworkInstance.tabIds}" var="currentTabId">
      <c:if test="${currentTabId eq 'researchTab'}">
        <c:set var="hasResearchTab" value="${true}"/>
      </c:if>
    </c:forEach>

    <c:if test="${hasResearchTab}">

      <%-- Separator --%>
      <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
        <dspel:param name="isSeparator" value="${true}"/>
      </dspel:include>

      <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
        <dspel:param name="actionId" value="researchTab"/>
        <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_backtoFind.gif"/>
        <dspel:param name="labelKey" value="nextSteps.backToFind.label"/>
      </dspel:include>

    </c:if>

  </dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/nextSteps/communicateNextSteps.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/nextSteps/communicateNextSteps.jsp#1 $$Change: 946917 $--%>
