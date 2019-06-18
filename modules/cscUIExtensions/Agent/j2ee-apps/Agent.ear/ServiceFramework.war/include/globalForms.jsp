
<%
/* 
 * This file is used to hold global form definitions that are rendered only once when main.jsp is first rendered. It is 
 * included in main.jsp through configuration of /atg/svc/agent/ui/AgentUIConfiguration. These forms are global as opposed to 
 * a form that's included in a panel definition and rendered every time the panel is rendered.
 *
 * Note that forms in this file will be loaded at login and hence, degrade login performance.
 */
%>


<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">

<dspel:form formid="tabsForm" id="tabsForm" action="#">
<dspel:input id="changeTab" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.changeTab"/>
<dspel:input type="hidden" converter="map" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.parameterMap"/>
</dspel:form>

<dspel:form style="display:none" action="#" id="globalViewTicketForm" formid="globalViewTicketForm">
  <dspel:input type="hidden" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket" priority="-10"/>
  <dspel:input type="hidden" value="" name="ticketId" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
</dspel:form>

<dspel:form style="display:none" action="#" id="globalViewCustomerForm" formid="globalViewCustomerForm">
  <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.changeUser"/>
  <dspel:input type="hidden" name="customerId" value="" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.customerId"/>
  <dspel:input type="hidden" name="viewMode" value="" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.viewMode"/>
</dspel:form>

<dspel:form style="display:none" action="#" id="globalSaveTicketForm" formid="globalSaveTicketForm">
 <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.endEditTicket"/>
 <dspel:input type="hidden" value="" converter="map" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap"/>
</dspel:form>


</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/globalForms.jsp#1 $$Change: 946917 $--%>
