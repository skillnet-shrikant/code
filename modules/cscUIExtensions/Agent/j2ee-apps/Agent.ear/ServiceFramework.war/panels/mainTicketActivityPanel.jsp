<!-- ticketActivityPanel.jsp -->
<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/mainTicketActivityPanel.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>

<dspel:page xml="true">

  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
  
  <div dojoType="dijit.layout.LayoutContainer" style="height: 100%;">
  
  <div dojoType="dojox.layout.ContentPane" scriptHasHooks="true" parseOnLoad="true" layoutAlign="top" style="padding: 3px; height:100%">
  
  <div id="resultBoxTopPane" style="display: inline-block; overflow:scroll; ; height:100%" parseWidgets="false">
    <dspel:form style="display:none" action="#" id="mainTicketActivityListForm" formid="mainTicketActivityListForm">
      <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.list"/>
      <dspel:input type="hidden" name="operation" value="refresh" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.operation"/>
      <dspel:input type="hidden" name="treeTableId" value="mainTicketActivityTable" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.treeTableId"/>
      <dspel:input type="hidden" name="parameters" value="" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.parameters"/>
      <dspel:input type="hidden" name="state" value="" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.state"/>
      <dspel:input type="hidden" name="activityType" value="" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.activityType"/>
    </dspel:form>
    <dspel:importbean scope="request"
      var="ticketActivityListFormHandler" 
      bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler" />

    <svc-ui:insertControlBar controlBarId="mainTicketActivityListControlBar" 
      treeTableId="mainTicketActivityTable"/>

    <%-- Task list table --%>
    <svc-ui:insertTreeTable 
      actionId="mainTicketActivityList"
      hasPaging="true"
      hasHeader="true"
      overflow="visible"
      pageSize="10"
      stateSavingMethod="server"
      treeTableBean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler"
      treeTableId="mainTicketActivityTable"
      width="100%">
    </svc-ui:insertTreeTable>

    <script type="text/javascript">
      <svc-ui:executeOperation operationName="refresh" treeTableId="mainTicketActivityTable"/>
    </script>
    </div>
    
    </div>
    
    </div>
    
  </dspel:layeredBundle>
</dspel:page>
<!-- end ticketActivityPanel.jsp -->
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/mainTicketActivityPanel.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/mainTicketActivityPanel.jsp#1 $$Change: 946917 $--%>
