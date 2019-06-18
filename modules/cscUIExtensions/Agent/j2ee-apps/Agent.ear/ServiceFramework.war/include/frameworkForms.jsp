<%--

This file is used for including DSP forms for searching.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/frameworkForms.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:importbean scope="request"
                  var="frameworkFormHandler"
                  bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler" />
<dspel:getvalueof id="op" param="op" />
<dspel:getvalueof id="ticketId" param="ticketId" />

<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <dspel:form style="display:none" id="closeWindow" formid="closeWindow" action="#">
    <dspel:input name="closeWindow" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.closeWindow" priority="-10"/>
  </dspel:form>

  <dspel:form style="display:none" id="selectTabbedPanel" formid="selectTabbedPanel" action="#">
    <dspel:input name="selectTabbedPanel" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.selectTabbedPanel" priority="-10"/>
  </dspel:form>

  <dspel:form style="display:none" id="toggleCell" formid="toggleCell" action="#">
    <dspel:input name="toggleCell" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.toggleCell" priority="-10"/>
  </dspel:form>

  <dspel:form style="display:none" id="togglePanel" formid="togglePanel" action="#">
    <dspel:input name="togglePanel" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.togglePanel" priority="-10"/>
  </dspel:form>

  <dspel:form style="display:none" id="togglePanelContent" formid="togglePanelContent" action="#">
    <dspel:input name="togglePanelContent" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.togglePanelContent" priority="-10"/>
  </dspel:form>

  <dspel:form style="display:none" id="togglePanelsToTabs" formid="togglePanelsToTabs" action="#">
    <dspel:input name="togglePanelsToTabs" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.togglePanelsToTabs" priority="-10"/>
  </dspel:form>

  <dspel:form style="display:none" id="transformForm" formid="transformForm" action="#">
    <dspel:input name="transform" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.transform" priority="-10"/>
  </dspel:form>

  <%
    /* these two urls are used as success and error url placeholders for some of the following forms. They were added so the forms could 
    * could be used using atgSubmitAction and have their error and success urls provided through input paramters to atgSubmitAction. Otherwise, they don't 
    * do anything to change the landing page (that is specified by other javascript functions that submit these forms using atgSubmitAction with
    * panels, panelstacks and tab specified). 
    */
  %>
  <dspel:droplet name="/atg/svc/droplet/FrameworkUrlDroplet">
  <dspel:oparam name="output">
    <dspel:getvalueof var="noOpSuccessURL" bean="/atg/svc/droplet/FrameworkUrlDroplet.url" />
    <dspel:getvalueof var="noOpErrorURL" bean="/atg/svc/droplet/FrameworkUrlDroplet.url" />
  </dspel:oparam>
  </dspel:droplet>
  
  <%-- Chat needs this form to always be available --%>
  <dspel:form style="display:none" action="#" id="workTicketNoSwitchForm" formid="workTicketNoSwitchForm" >
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/WorkTicket.changeEnvironment"/>
    <dspel:input type="hidden" name="ticketId" value="" bean="/atg/svc/agent/ui/formhandlers/WorkTicket.inputParameters.ticketId"/>
    <dspel:input type="hidden" name="errorURL" value="${noOpErrorURL}" bean="/atg/svc/agent/ui/formhandlers/WorkTicket.errorURL"/>
    <dspel:input type="hidden" name="successURL" value="${noOpSuccessURL}" bean="/atg/svc/agent/ui/formhandlers/WorkTicket.successURL"/>
  </dspel:form> 

  <dspel:form style="display:none" action="#" id="workActiveTicketForm" formid="workActiveTicketForm" >
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/WorkActiveTicket.changeEnvironment"/>
    <dspel:input type="hidden" name="ticketId" value="" bean="/atg/svc/agent/ui/formhandlers/WorkActiveTicket.inputParameters.ticketId"/>
    <dspel:input type="hidden" name="doDispositionPrompt" value="false" bean="/atg/svc/agent/ui/formhandlers/WorkActiveTicket.doTicketDispositionPrompt"/>
    <dspel:input type="hidden" name="errorURL" value="${noOpErrorURL}" bean="/atg/svc/agent/ui/formhandlers/WorkActiveTicket.errorURL"/>
    <dspel:input type="hidden" name="successURL" value="${noOpSuccessURL}" bean="/atg/svc/agent/ui/formhandlers/WorkActiveTicket.successURL"/>
  </dspel:form> 

  <c:catch>
    <svc-ui:getOptionAsString var="newCallTab" optionName="NewCallTab" />  
    <svc-ui:getOptionAsString var="newCallPanelStack" optionName="NewCallPanelStack" />
  </c:catch>
  <dspel:droplet name="/atg/svc/droplet/FrameworkUrlDroplet">
  <dspel:param name="panelStacks" value="${empty newCallPanelStack ? 'customerSearchPanels' : newCallPanelStack},globalPanels" />
  <dspel:param name="tab" value="${empty newCallTab ? 'customerTab' : newCallTab}"/>
  <dspel:oparam name="output">
    <dspel:getvalueof var="callActionSuccessURL" bean="/atg/svc/droplet/FrameworkUrlDroplet.url" />
  </dspel:oparam>
  </dspel:droplet>

  <dspel:droplet name="/atg/svc/droplet/FrameworkUrlDroplet">
  <dspel:param name="panelStacks" value="globalPanels" />
  <dspel:oparam name="output">
    <dspel:getvalueof var="endCallActionSuccessURL" bean="/atg/svc/droplet/FrameworkUrlDroplet.url" />
  </dspel:oparam>
  </dspel:droplet>
  <dspel:droplet name="/atg/svc/droplet/FrameworkUrlDroplet">
  <dspel:param name="panelStacks" value="globalPanels" />
  <dspel:oparam name="output">
    <dspel:getvalueof var="callActionErrorURL" bean="/atg/svc/droplet/FrameworkUrlDroplet.url" />
  </dspel:oparam>
  </dspel:droplet>
  
  <dspel:droplet name="/atg/svc/droplet/FrameworkUrlDroplet">
  <dspel:param name="panelStacks" value="ticketPanels,globalPanels" />
  <dspel:param name="nextSteps" value="ticketViewNextSteps" />
  <dspel:param name="panels" value="nextStepsPanel,sideViewRecentTicketsPanel,ticketSummaryPanel" />
  <dspel:param name="tab" value="ticketsTab"/>
  <dspel:oparam name="output">
    <dspel:getvalueof var="createNewTicketSuccessURL" bean="/atg/svc/droplet/FrameworkUrlDroplet.url" />
  </dspel:oparam>
  </dspel:droplet>

  <dspel:form style="display:none" action="#" id="startCallForm" formid="startCallForm" >
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/StartNewCall.changeEnvironment"/>
    <dspel:input type="hidden" name="successURL" value="${callActionSuccessURL}" bean="/atg/svc/agent/ui/formhandlers/StartNewCall.successURL"/>
    <dspel:input type="hidden" name="errorURL" value="${callActionErrorURL}" bean="/atg/svc/agent/ui/formhandlers/StartNewCall.errorURL"/>
  </dspel:form> 
  
  <dspel:form style="display:none" action="#" id="endCallForm" formid="endCallForm" >
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/EndCall.changeEnvironment"/>
    <dspel:input type="hidden" name="successURL"  value="${endCallActionSuccessURL}" bean="/atg/svc/agent/ui/formhandlers/EndCall.successURL"/>
    <dspel:input type="hidden" name="errorURL" value="${callActionErrorURL}" bean="/atg/svc/agent/ui/formhandlers/EndCall.errorURL"/>
  </dspel:form> 

  <dspel:form style="display:none" action="#" id="endAndStartCallForm" formid="endAndStartCallForm" >
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/EndAndStartCall.changeEnvironment"/>
    <dspel:input type="hidden" name="successURL"  value="${callActionSuccessURL}" bean="/atg/svc/agent/ui/formhandlers/EndAndStartCall.successURL"/>
    <dspel:input type="hidden" name="errorURL" value="${callActionErrorURL}" bean="/atg/svc/agent/ui/formhandlers/EndAndStartCall.errorURL"/>
  </dspel:form> 

  <%-- polling for push tickets needs this form to be always available --%>
  <dspel:form action="#" style="display:none" id="activeTicketsSearchForm" formid="activeTicketsSearchForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler.search"/>
    <dspel:input type="hidden" name="currentPage" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler.currentPage"/>
    <dspel:input type="hidden" name="sortProperty" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler.sortField"/>
    <dspel:input type="hidden" name="sortDirection" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler.sortDirection"/>
<%--    <dspel:input type="hidden" name="successURL" value="panels/ticketing/activeTicketsEncodeSearchResults.jsp?_windowid=${windowId}" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler.successURL"/>
--%>
  </dspel:form>

  <dspel:form action="#" style="display:none" id="altTicketActivityListForm" formid="altTicketActivityListForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/TicketActivityListFormHandler.list"/>
    <dspel:input type="hidden" name="activityType" value="" bean="/atg/svc/ui/formhandlers/TicketActivityListFormHandler.activityType"/>
    <dspel:input type="hidden" name="operation" value="refresh" bean="/atg/svc/ui/formhandlers/TicketActivityListFormHandler.operation"/>
    <dspel:input type="hidden" name="treeTableId" value="ticketActivityTable" bean="/atg/svc/ui/formhandlers/TicketActivityListFormHandler.treeTableId"/>
    <dspel:input type="hidden" name="parameters" value="" bean="/atg/svc/ui/formhandlers/TicketActivityListFormHandler.parameters"/>
    <dspel:input type="hidden" name="state" value="" bean="/atg/svc/ui/formhandlers/TicketActivityListFormHandler.state"/>
  </dspel:form>

  <dspel:form style="display:none" action="#" id="createNewCustomer" formid="createNewCustomer" >
  <dspel:input type="hidden" value="newCustomer" bean="/atg/svc/agent/ui/formhandlers/ChangeCurrentCustomer.environmentChangeKey"/>
  <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/ChangeCurrentCustomer.changeEnvironment"/>
  <dspel:input type="hidden" name="errorURL" value="${noOpErrorURL}" bean="/atg/svc/agent/ui/formhandlers/ChangeCurrentCustomer.errorURL"/>
  <dspel:input type="hidden" name="successURL" value="${noOpSuccessURL}" bean="/atg/svc/agent/ui/formhandlers/ChangeCurrentCustomer.successURL"/>
  </dspel:form>

  <dspel:form style="display:none" action="#" id="showCurrentCustomer" formid="showCurrentCustomer">
  <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.changeUser"/>
  <dspel:input type="hidden" name="customerId" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.customerId"/>
  </dspel:form>

  <dspel:form style="display:none" action="#" id="syncCurrentCustomer" formid="syncCurrentCustomer">
  <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/agent/ui/formhandlers/SyncToCustomer.changeEnvironment"/>
  <dspel:input type="hidden" name="errorURL" value="${noOpErrorURL}" bean="/atg/svc/agent/ui/formhandlers/SyncToCustomer.errorURL"/>
  <dspel:input type="hidden" name="successURL" value="${noOpSuccessURL}" bean="/atg/svc/agent/ui/formhandlers/SyncToCustomer.successURL"/>
  </dspel:form>

  <dspel:form formid="logout" id="logout" method="post" style="display:none">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/agent/userprofiling/EnvironmentLogoutFormHandler.changeEnvironment" />
    <dspel:input type="hidden" name="doWarnings" bean="/atg/agent/userprofiling/EnvironmentLogoutFormHandler.doWarnings"/>
    <dspel:input type="hidden" name="doPrompt" bean="/atg/agent/userprofiling/EnvironmentLogoutFormHandler.doTicketDispositionPrompt"/>
  </dspel:form>
  
  <dspel:form style="display:none" action="#" id="createTicketNextStepForm" formid="createTicketNextStepForm" >
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/CreateNewTicket.changeEnvironment"/>
    <dspel:input type="hidden" name="successURL" value="${createNewTicketSuccessURL}" bean="/atg/svc/agent/ui/formhandlers/CreateNewTicket.successURL"/>
    <dspel:input type="hidden" name="errorURL" value="${noOpErrorURL}" bean="/atg/svc/agent/ui/formhandlers/CreateNewTicket.errorURL"/>
  </dspel:form>

  <dspel:form style="display:none" action="#" id="viewCustomerSelectForm"  formid="viewCustomerSelectForm">
   <dspel:input type="hidden" bean="/atg/svc/agent/ui/formhandlers/ChangeCurrentCustomer.inputParameters.changeProfileId"/>
   <dspel:input type="hidden" priority="-10" bean="/atg/svc/agent/ui/formhandlers/ChangeCurrentCustomer.changeEnvironment" value=""/>
  </dspel:form>

</dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/frameworkForms.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/frameworkForms.jsp#1 $$Change: 946917 $--%>
