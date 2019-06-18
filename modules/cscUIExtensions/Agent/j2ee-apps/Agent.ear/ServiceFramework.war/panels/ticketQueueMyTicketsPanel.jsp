<!-- ticketQueueMyTicketsPanel.jsp -->
<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketQueueMyTicketsPanel.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true"> 
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
<div id="ticketQueue" class="tabcontent"> 
  <table class="w98p"  border="0" cellspacing="0" cellpadding="0"> 
    <tr> 
      <td colspan="3" class="headerLabel">
        <fmt:message key="my-tickets"/> 
        <hr /> 
      </td> 
    </tr> 
    <tr>   
      <%-- Find active form handler either browse or search --%>
      <dspel:importbean scope="request"
                        var="ticketSearchFormHandler"
                        bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler" />

      <%-- Results panel container - hide while loading --%>

      <div id="searchResultsPanelContainer">

        <%-- Search results top control bar --%>

        <svc-ui:insertControlBar controlBarId="ticketMyTicketsResultsControlBar"
                             treeTableId="ticketMyTicketsResultsTable"/>

        <%-- Loading screen for table --%>

        <div class="loading"
             id="searchResultsTableLoading"
             style="display:none;">
          <dspel:img src="${imageLocation}/loading.gif"/>
        </div>

        <%-- Results table container - hide while paging --%>

        <div id="ticketMyTicketsResultsTableContainer">

        <%-- Search results table --%>

        <svc-ui:insertTreeTable actionId="previousSearchResults"
                              hasPaging="true"
                              hasHeader="true"
                              initialUrl="/include/noSearch.jsp"
                              pageSize="10"
                              stateSavingMethod="server"
                              treeTableBean="/atg/svc/ui/formhandlers/TicketSearchFormHandler"
                              treeTableId="ticketMyTicketsResultsTable"
                              width="100%">
          <svc-ui:initialSort defaultSortDirection="ascending" sortField="id"/>
          <svc-ui:insertBody items="${ticketSearchFormHandler.myItems}" varItem="result" itemKey="${result.id}"/>
        </svc-ui:insertTreeTable>

      </div>
    </tr>
  </table>
</div>
</dspel:layeredBundle>
</dspel:page>
<!-- end ticketQueueMyTicketsPanel.jsp -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketQueueMyTicketsPanel.jsp#1 $$Change: 946917 $--%>
