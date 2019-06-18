<!-- ticketActivityPanel.jsp -->
<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketActivityPanel.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">

  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">

    <dspel:importbean bean="/atg/svc/agent/ui/OriginatingPage"/>
    <dspel:setvalue bean="OriginatingPage.pageName" value=""/>

    <dspel:importbean scope="request"
      var="ticketActivityListFormHandler" 
      bean="/atg/svc/ui/formhandlers/TicketActivityListFormHandler" />

    <svc-ui:insertControlBar controlBarId="ticketActivityListControlBar" 
      treeTableId="ticketActivityTable"/>
                             
    <%-- Task list table --%>
    <svc-ui:insertTreeTable 
      actionId="ticketActivityList"
      hasPaging="true"
      hasHeader="true"
      overflow="visible"
      pageSize="10"
      stateSavingMethod="server"
      treeTableBean="/atg/svc/ui/formhandlers/TicketActivityListFormHandler"
      treeTableId="ticketActivityTable"
      width="100%">
    </svc-ui:insertTreeTable>

    <script type="text/javascript">
      <svc-ui:executeOperation operationName="refresh" treeTableId="ticketActivityTable"/>
    </script>

  </dspel:layeredBundle>
</dspel:page>
<!-- end ticketActivityPanel.jsp -->
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketActivityPanel.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketActivityPanel.jsp#1 $$Change: 946917 $--%>
