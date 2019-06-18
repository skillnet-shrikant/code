<%--

Next steps

--%>

<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
  <dspel:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>
  <dspel:getvalueof var="isTransient" bean="/atg/svc/ticketing/TicketHolder.currentTicket.transient"/>

  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
    <dspel:tomap var="currentTicket" bean="/atg/svc/ticketing/TicketHolder.currentTicket"/>
    <dspel:tomap var="viewTicket" bean="/atg/svc/ticketing/ViewTicketHolder.currentTicket"/>
    <dspel:getvalueof var="isTransient" bean="/atg/svc/ticketing/TicketHolder.currentTicket.transient"/>
    <dspel:getvalueof var="isViewTicketTransient" bean="/atg/svc/ticketing/ViewTicketHolder.currentTicket.transient"/>
    <dspel:getvalueof var="isViewTicketClosed" bean="/atg/svc/ticketing/ViewTicketHolder.currentTicketIsClosed"/>
    <dspel:getvalueof var="agentId" bean="/atg/userprofiling/Profile.id"/>
    <dspel:getvalueof var="ticketAgentId" bean="/atg/svc/ticketing/TicketHolder.currentTicket.agentAssignmentActivity.owningAgent.id"/>
    <dspel:getvalueof var="viewTicketSiteId" bean="/atg/svc/ticketing/ViewTicketHolder.currentTicket.siteId"/>
  
    <fmt:message var="createText" key="create-prompt-message"/>
    <fmt:message var="createTitle" key="create-prompt-title"/>

    <%-- New Ticket --%>   
      <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
				<dspel:param name="actionJavaScript" value="atg.service.ticketing.createNewTicket('createTicketNextStepForm');"/>
        <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_createTicket.gif"/>
        <dspel:param name="labelKey" value="nextSteps.createTicket.label"/>
      </dspel:include>

    <%-- Escalate Ticket --%>
    <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="actionJavaScript" value="atg.service.ticketing.escalateTicketPrompt(atg.service.ticketing.escalateTicketFunc);"/>
      <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_escalate.gif"/>
      <dspel:param name="labelKey" value="nextSteps.escalateTicket.label"/>
    </dspel:include>
                              
    <%-- Work Ticket --%>
    <c:choose>
      <c:when test ="${envTools.siteAccessControlOn == 'true' }">
        <dspel:droplet name="IsSiteAccessibleDroplet">
          <dspel:param name="siteId" value="${viewTicketSiteId}"/>
          <dspel:oparam name="true">
            <c:if test="${(currentTicket.id != viewTicket.id) && (!isViewTicketTransient) && !isViewTicketClosed}">
              <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
                <dspel:param name="actionJavaScript" value="atg.service.ticketing.workViewingTicket('workTicketNoSwitchForm','${viewTicket.id}');"/>
                <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_claimTicket.gif"/>
                <dspel:param name="labelKey" value="nextSteps.workTicket.label"/>
              </dspel:include>
            </c:if>
          </dspel:oparam>
          <dspel:oparam name="false">
            <!-- Show "Work on Ticket"link for legacy tickets (null site id) as this will initially fail  -->
            <c:if test="${(currentTicket.id != viewTicket.id) && (!isViewTicketTransient) && !isViewTicketClosed && viewTicketSiteId==null}">
              <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
                <dspel:param name="actionJavaScript" value="atg.service.ticketing.workViewingTicket('workTicketNoSwitchForm','${viewTicket.id}');"/>
                <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_claimTicket.gif"/>
                <dspel:param name="labelKey" value="nextSteps.workTicket.label"/>
              </dspel:include>
            </c:if>
          </dspel:oparam>
        </dspel:droplet>
      </c:when>
      <c:otherwise>
        <c:if test="${(currentTicket.id != viewTicket.id) && (!isViewTicketTransient) && !isViewTicketClosed}">
          <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
            <dspel:param name="actionJavaScript" value="atg.service.ticketing.workViewingTicket('workTicketNoSwitchForm','${viewTicket.id}');"/>
            <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_claimTicket.gif"/>
            <dspel:param name="labelKey" value="nextSteps.workTicket.label"/>
          </dspel:include>
        </c:if>
      </c:otherwise>
    </c:choose>
                              
    <%-- Reopen Ticket --%>
    <c:if test="${isViewTicketClosed}">
      <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
        <dspel:param name="actionJavaScript" value="atg.service.ticketing.reopenViewingTicket('workTicketNoSwitchForm','${viewTicket.id}');"/>
        <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_reopenTicket.gif"/>
        <dspel:param name="labelKey" value="nextSteps.reopenTicket.label"/>
      </dspel:include>
    </c:if>

    <%-- Close Ticket --%>
    <c:if test="${not empty currentTicket && !isTransient && !ticketHolder.currentTicketIsClosed}">
      <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
        <dspel:param name="actionJavaScript" value="atg.service.ticketing.closeTicketPrompt(atg.service.ticketing.closeTicketFunc);"/>
        <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_closeTicket.gif"/>
        <dspel:param name="labelKey" value="nextSteps.closeTicket.label"/>
      </dspel:include>
    </c:if>

    <%-- Release Ticket --%>
    <c:if test="${not empty currentTicket && !isTransient && (ticketAgentId == agentId)}">
      <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
        <dspel:param name="actionJavaScript" value="atg.service.ticketing.releaseTicketPrompt(atg.service.ticketing.releaseTicketFunc);"/>
        <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_releaseTicketQueue.gif"/>
        <dspel:param name="labelKey" value="nextSteps.releaseTicket.label"/>
      </dspel:include>
    </c:if>

    <%-- Defer Ticket --%>
    <c:if test="${not empty currentTicket && !isTransient}">
      <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
        <dspel:param name="actionJavaScript" value="atg.service.ticketing.deferTicketPrompt(atg.service.ticketing.deferTicketFunc);"/>
        <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_deferTicket.gif"/>
        <dspel:param name="labelKey" value="nextSteps.deferTicket.label"/>
      </dspel:include>
    </c:if>

    <%-- Reassign Ticket --%>
    <c:if test="${not empty currentTicket && !isTransient}">
      <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
        <dspel:param name="actionJavaScript" value="atg.service.ticketing.assignTicketToAgentPrompt(atg.service.ticketing.assignTicketFunc);"/>
        <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_reassignTicket.gif"/>
        <dspel:param name="labelKey" value="nextSteps.reassignTicket.label"/>
      </dspel:include>
    </c:if>

    <%-- Send Ticket to Group --%>
    <c:if test="${not empty currentTicket && !isTransient}">
      <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
        <dspel:param name="actionJavaScript" value="atg.service.ticketing.sendTicketPrompt(atg.service.ticketing.sendTicketFunc);"/>
        <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_pushToGroups.gif"/>
        <dspel:param name="labelKey" value="nextSteps.sendTicket.label"/>
      </dspel:include>
    </c:if>

    <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="isSeparator" value="${true}"/>
    </dspel:include>
    
    <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="actionId" value="printTicket"/>
      <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/toolbar/icon_print.gif"/>
      <dspel:param name="labelKey" value="nextSteps.printTicket.label"/>
      <dspel:param name="formhandler" value="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.transform"/>
    </dspel:include>
    
    <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="isSeparator" value="${true}"/>
    </dspel:include>
        
    <%-- Back to ticket search --%>
    <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="actionId" value="backToTicketSearch"/>
      <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_backToFindTicket.gif"/>
      <dspel:param name="labelKey" value="nextSteps.backToTicketSearch.label"/>
      <dspel:param name="formhandler" value="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.transform"/>
    </dspel:include>

  </dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/nextSteps/ticketViewNextSteps.jsp#2 $$Change: 953229 $$DateTime: 2015/02/26 10:47:28 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/nextSteps/ticketViewNextSteps.jsp#2 $$Change: 953229 $--%>
