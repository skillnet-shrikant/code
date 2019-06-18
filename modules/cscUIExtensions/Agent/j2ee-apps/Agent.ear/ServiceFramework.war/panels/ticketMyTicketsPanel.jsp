<%--
 my tickets search results panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketMyTicketsPanel.jsp#2 $
 @updated $DateTime: 2015/02/26 10:47:28 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <dspel:importbean var="activeTicketsSearchFormHandler" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler" scope="request" />

<script type="text/javascript">
  atg.service.ticketing.myTicketSearchFields = [
      { name: ' ', canSort: false, property: "imageHtml" },
      { name: 'Id', canSort: true, property: "id" },
      { name: 'Description', canSort: true, property: "description" },
      { name: 'Created', canSort: true, property: "creationTime" },
      { name: 'Age', canSort: true, property: "age" },
      { name: 'Status', canSort: true, property: "subStatus" },
      { name: 'Work&nbsp;On', canSort: false, property: "id" }
  ];

  atg.service.ticketing.myTicketSearchLayout = [
    { cells: [[
      { name: '', width: "26px"},               
      { name: '<fmt:message key="table.tickets.ticketId" />', width: "6em"},
      { name: '<fmt:message key="table.tickets.description" />', width: "40%"},
      { name: '<fmt:message key="table.tickets.created" />', width: "10em"},
      { name: '<fmt:message key="table.tickets.age" />', width: "6em"},
      { name: '<fmt:message key="table.tickets.status" />', width: "8em"},
      { name: '<fmt:message key="table.tickets.workOn" />', width: "7em"}
    ]]}
  ];

  atg.service.ticketing.myChangeTicketString = "<a href=\"#\" class=\"blueU\" onclick=\"dojo.byId('myTicketsViewTicketForm').ticketId.value='theTicketId';viewTicket('myTicketsViewTicketForm');\">theTicketId</a>";

  atg.service.ticketing.myWorkTicketString = "<a href=\"#\" class=\"blueU\" onclick=\"workActiveTicket('workActiveTicketForm','theTicketId');return false;\"><fmt:message key="table.options.workOn" /></a>";

  atg.service.ticketing.myTicketSearchSetFormSortProperty = function (/*int*/columnIndex, /*bool*/sortDesc)  {
    var theForm = dojo.byId(atg.service.ticketing.myTicketSearchResultsModel.formId);
    if (columnIndex != -1) {
      theForm.sortProperty.value = atg.service.ticketing.myTicketSearchProperties[columnIndex].property;
      theForm.sortDirection.value = sortDesc ? "desc" : "asc";
    }
    else
    {
      theForm.sortProperty.value = "id";
      theForm.sortDirection.value = "asc";
    }
  }

atg.service.ticketing.myTicketSearchRefreshGrid = function () {
  atg.service.ticketing.myTicketSearchResultsModel.fetchRowCount(
  {
    callback: function(inRowCount) {
      console.debug("myTicketSearchRefreshGrid called " + inRowCount.resultLength);
      dijit.byId("atg_service_ticketing_myTicketSearchResultsTable").editCell = null;
      atg.service.ticketing.myTicketSearchResultsModel.clearData();
      atg.service.ticketing.myTicketSearchResultsModel.count = inRowCount.resultLength;
      dijit.byId("atg_service_ticketing_myTicketSearchResultsTable").rowCount = inRowCount.resultLength;
      dijit.byId("atg_service_ticketing_myTicketSearchResultsTable").updateRowCount(inRowCount.resultLength);
      atg.service.ticketing.myTicketsUpdateCount(inRowCount.resultLength);
      console.debug("completed myTicketSearchRefreshGrid callback");
    }
  });
};

  atg.service.ticketing.myTicketSearchResultsModel = new atg.data.FormhandlerData(atg.service.ticketing.myTicketSearchFields,"/agent/panels/ticketing/activeTicketsEncodeSearchResults.jsp");
  atg.service.ticketing.myTicketSearchResultsModel.formId = 'myTicketsSearchForm';
  atg.service.ticketing.myTicketSearchResultsModel.formCurrentPageField = "currentPage";
  <dspel:getvalueof var="rowsPerPage" bean="SearchAgentTicketsFormHandler.resultsPerPage"/>
  atg.service.ticketing.myTicketSearchResultsModel.rowsPerPage = ${rowsPerPage};
  atg.service.ticketing.myTicketSearchResultsModel.rows = function(inRowIndex, inData) {
    var ticketIdString;
    for (var i=0, l=inData.results.length; i<l; i++) {
      var value = inData.results[i].id;
      if (atg.service.ticketing.currentTicket != value) {
        ticketIdString = atg.service.ticketing.myChangeTicketString.replace(/theTicketId/g, value);
      }
      else {
        ticketIdString = "<strong>" + value + "</strong>";
      }
      var newRow = [
        inData.results[i].imageHtml,
        ticketIdString,
        inData.results[i].description,
        inData.results[i].creationTime,
        inData.results[i].age,
        inData.results[i].subStatus,
        atg.service.ticketing.myWorkTicketString.replace(/theTicketId/g, inData.results[i].id)
      ];
      this.setRow(newRow, inRowIndex + i);
    }
  };

  atg.service.ticketing.myTicketsUpdateCount = function(count) {
    dojo.byId('atg_service_ticketing_myTicketCount').innerHTML = count + ' ' + '<fmt:message key="table.tickets.ticketsCount" />';
  };

</script>
<script type="text/javascript">
_container_.onLoadDeferred.addCallback(function () {
  console.debug("*** ticketMyTicketsPanel.jsp addOnLoad called ***");
  dijit.byId("atg_service_ticketing_myTicketSearchResultsTable").setModel(atg.service.ticketing.myTicketSearchResultsModel);
  atg.service.ticketing.myTicketSearchRefreshGrid();
  dijit.byId("atg_service_ticketing_myTicketSearchResultsTable").setStructure(atg.service.ticketing.myTicketSearchLayout);
  dijit.byId("atg_service_ticketing_myTicketSearchResultsTable").update();
  ticketMyTicketsHandle = dojo.connect(_container_, "resize", dijit.byId("atg_service_ticketing_myTicketSearchResultsTable"), "update");
});
_container_.onUnloadDeferred.addCallback(function () {
  console.debug("*** ticketMyTicketsPanel.jsp addOnUnload called ***");
  dojo.disconnect(ticketMyTicketsHandle);
});
</script>

  <span class="atg_resultTotal" id="atg_service_ticketing_myTicketCount"></span><input type="button" onclick="atg.service.ticketing.myTicketSearchRefreshGrid();" value="<fmt:message key='table.tickets.refreshButton'/>" style="font-size: 0.9em;"></input>
   <div style="height:350px">
    <div id="atg_service_ticketing_myTicketSearchResultsTable"
         dojoType="dojox.Grid"
         onMouseOverRow="atg.noop()"
         autoHeight="true"
         onRowClick="atg.noop()"
         onCellClick="atg.noop()">
    </div>
  </div>
  <dspel:form action="#" style="display:none" id="myTicketsSearchForm" formid="myTicketsSearchForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler.search"/>
    <dspel:input type="hidden" name="currentPage" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler.currentPage"/>
    <dspel:input type="hidden" name="sortProperty" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler.sortField"/>
    <dspel:input type="hidden" name="sortDirection" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler.sortDirection"/>
  </dspel:form>
  <dspel:form style="display:none" action="#" id="myTicketsViewTicketForm" formid="myTicketsViewTicketForm">
    <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket"/>
    <dspel:input type="hidden" value="" name="ticketId" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
  </dspel:form>
  </dspel:layeredBundle>
</dspel:page>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketMyTicketsPanel.jsp#2 $$Change: 953229 $--%>