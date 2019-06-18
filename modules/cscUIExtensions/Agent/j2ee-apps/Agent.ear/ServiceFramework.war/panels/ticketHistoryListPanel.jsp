<%-- 
  Displays a ticket history list 
	
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketHistoryListPanel.jsp#1 $ $Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $ $Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">

<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
  <dspel:form style="display:none" action="#" id="ticketHistoryListForm" formid="ticketHistoryListForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.search"/>
    <dspel:input type="hidden" name="id" value="" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.parameterMap.id"/>
    <dspel:input type="hidden" name="operation" value="refresh" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.operation"/>
    <dspel:input type="hidden" name="treeTableId" value="ticketHistoryListTable" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.treeTableId"/> 
    <dspel:input type="hidden" name="parameters" value="" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.parameters"/> 
    <dspel:input type="hidden" name="state" value="" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.state"/> 
    <dspel:input type="hidden" name="status" value="" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler.parameterMap.status"/>
  </dspel:form> 
  <dspel:form style="display:none" action="#" id="ticketHistoryResultsViewTicketForm" formid="ticketHistoryResultsViewTicketForm">
    <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket"/>
    <dspel:input type="hidden" value="" name="ticketId" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
    <dspel:input type="hidden" name="id" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap.id"/>
    <dspel:input type="hidden" name="status" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap.status"/>
  </dspel:form>
  <svc-ui:insertControlBar controlBarId="ticketHistoryListControlBar" 
    treeTableId="ticketHistoryListTable"/>
                           
   <%-- Task list table --%>
  <svc-ui:insertTreeTable 
    actionId="ticketHistoryList"
    hasPaging="true"
    hasHeader="true"
    overflow="visible"
    pageSize="10"
    stateSavingMethod="server"
    treeTableBean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler"
    treeTableId="ticketHistoryListTable"
    width="100%">
  </svc-ui:insertTreeTable>

  <script type="text/javascript">
    <svc-ui:executeOperation operationName="refresh" treeTableId="ticketHistoryListTable"/>
  </script>

</dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketHistoryListPanel.jsp#1 $ $Change: 946917 $ $DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketHistoryListPanel.jsp#1 $$Change: 946917 $--%>
