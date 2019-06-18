<%--

Next steps

--%>

<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:getvalueof var="isTransient" bean="/atg/svc/ticketing/TicketHolder.currentTicket.transient"/>
  <dspel:importbean bean="/atg/svc/agent/ui/formhandlers/ChangePasswordFormHandler" scope="request"/>
  <dspel:importbean bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler" scope="request"/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<dspel:form action="#" name="saveUserPasswordForm" id="saveUserPasswordForm" formid="saveUserPasswordForm">
  <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/ChangePasswordFormHandler.changePassword"/>
  <dspel:input type="hidden" name="newPassword" value="" bean="/atg/svc/agent/ui/formhandlers/ChangePasswordFormHandler.newPassword"/>
  <dspel:input type="hidden" name="oldPassword" value="" bean="/atg/svc/agent/ui/formhandlers/ChangePasswordFormHandler.oldPassword"/>
  <dspel:input type="hidden" name="confirmPassword" value="" bean="/atg/svc/agent/ui/formhandlers/ChangePasswordFormHandler.confirmPassword"/>
</dspel:form>
<dspel:form action="#" name="saveUserPreferencesForm" id="saveUserPreferencesForm" formid="saveUserPreferencesForm">
  <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler.save"/>

<%--  Bug 153888  Hide Group Answers by Document option - it no longer applies in Search 9.0
  <dspel:input type="hidden" value="" name="AgentUserAnswerGrouping" bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler.options.AgentUserAnswerGrouping.value"/>
--%>

  <dspel:input type="hidden" value="" name="TrylogOut" bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler.options.TrylogOut.value"/>
  <dspel:input type="hidden" value="" name="AgentUserDefaultHomeTab" bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler.options.AgentUserDefaultHomeTab.value"/>
  <input id="atg.successMessage" name="atg.successMessage" type="hidden"
         value="<fmt:message key='success.savedPreferences'/>"/>
</dspel:form>

    <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="actionId" value="saveUserPreferences"/>
      <dspel:param name="actionJavaScript" value="saveUserPassword();saveWindowPreferences();"/>
      <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_save.gif"/>
      <dspel:param name="labelKey" value="nextSteps.savePreferences.label"/>
    </dspel:include>

    <%-- Always cancel back to the tab the agent was on previously --%>
    <fw-beans:tabDefinitionFindByTabId appId="workspace" tabId="${framework.frameworkInstance.currentTabId}" var="tabDefinition"/>
    <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="actionJavaScript" value="${tabDefinition.contents.actionJavaScript.body}"/>
      <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_cancel.gif"/>
      <dspel:param name="labelKey" value="nextSteps.cancelUserPreferences.label"/>
    </dspel:include>

  </dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/nextSteps/userPreferencesNextSteps.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/nextSteps/userPreferencesNextSteps.jsp#1 $$Change: 946917 $--%>
