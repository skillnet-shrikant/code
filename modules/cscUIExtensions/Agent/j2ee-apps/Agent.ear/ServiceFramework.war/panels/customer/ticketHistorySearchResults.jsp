<%--
 Customer Ticket History Search Results Panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/ticketHistorySearchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
    <dspel:importbean bean="/atg/svc/agent/ui/tables/ticket/CustomerTicketGrid" var="gridConfig"/>
    <dspel:importbean var="ticketHistoryFormHandler" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler" scope="request" />
    <dspel:form style="display:none" action="#" id="ticketHistoryListForm" formid="ticketHistoryListForm">
      <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.search"/>
      <dspel:input type="hidden" name="currentPage" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.currentPage"/>
      <dspel:input type="hidden" name="sortProperty" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.sortField"/>
      <dspel:input type="hidden" name="sortDirection" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.sortDirection"/>
    </dspel:form> 
    <dspel:form style="display:none" action="#" id="ticketHistoryResultsViewTicketForm" formid="ticketHistoryResultsViewTicketForm">
      <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket"/>
      <dspel:input type="hidden" value="" name="ticketId" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
    </dspel:form>

    <dspel:include src="${gridConfig.gridPage.URL}" otherContext="${gridConfig.gridPage.servletContext}">
      <dspel:param name="gridConfig" value="${gridConfig}"/>
    </dspel:include>
  </dspel:layeredBundle>
</dspel:page>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/ticketHistorySearchResults.jsp#1 $$Change: 946917 $--%>