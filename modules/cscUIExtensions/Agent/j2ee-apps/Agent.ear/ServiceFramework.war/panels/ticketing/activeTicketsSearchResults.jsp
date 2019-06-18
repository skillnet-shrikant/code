<%--
 Active tickets search results panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/activeTicketsSearchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">

  <dspel:importbean var="activeTicketsSearchFormHandler" bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler" scope="request" />

<script type="text/javascript">
  atg.service.ticketing.ticketSearchFields = [
      { name: '<fmt:message key="table-id-label"/>', canSort: true, property: "id" },
      { name: '<fmt:message key="table-activity-label"/>', canSort: false, property: "activities" },
      { name: '<fmt:message key="table-description-label"/>', canSort: true, property: "description" },
      { name: '<fmt:message key="table-group-label"/>', canSort: false, property: "group" },
      { name: '<fmt:message key="table-escalation-label"/>', canSort: true, property: "localizedEscalationLevel" },
      { name: '<fmt:message key="table-status-label"/>', canSort: true, property: "subStatus" },
      { name: '<fmt:message key="table-duedate-label"/>', canSort: true, property: "dueTime" }
  ];

  atg.service.ticketing.ticketSearchLayout = [
    { cells: [[
      { name: '<fmt:message key="table-id-label"/>', width: "8em"},
      { name: '<fmt:message key="table-activity-label"/>', width: "5em;text-align:right" },
      { name: '<fmt:message key="table-description-label"/>', width: "60%"},
      { name: '<fmt:message key="table-group-label"/>', width: "20%"},
      { name: '<fmt:message key="table-escalation-label"/>', width: "20%"},
      { name: '<fmt:message key="table-status-label"/>', width: "10em"},
      { name: '<fmt:message key="table-duedate-label"/>', width: "10em"}
    ]]}
  ];

  atg.service.ticketing.changeTicketString = "<a href=\"#\" class=\"blueU\" onclick=\"workActiveTicket('workActiveTicketForm','theTicketId');dijit.byId('activeTicketsPopup').hide();return false;\">theTicketId</a>";

  atg.service.ticketing.ticketSearchSetFormSortProperty = function (/*int*/columnIndex, /*bool*/sortDesc)  {
    var theForm = dojo.byId(atg.service.ticketing.ticketSearchResultsModel.formId);
    if (columnIndex != -1) {
      theForm.sortProperty.value = atg.service.ticketing.ticketSearchFields[columnIndex].property;
      theForm.sortDirection.value = sortDesc ? "desc" : "asc";
    }
    else
    {
      theForm.sortProperty.value = "id";
      theForm.sortDirection.value = "asc";
    }
  }

atg.service.ticketing.ticketSearchRefreshGrid = function () {
  atg.service.ticketing.ticketSearchResultsModel.fetchRowCount(
  {
    callback: function(inRowCount) {
      console.debug("ticketSearchRefreshGrid called " + inRowCount.resultLength);
      dijit.byId("atg_service_ticketing_ticketSearchResultsTable").editCell = null;
      atg.service.ticketing.ticketSearchResultsModel.clearData();
      atg.service.ticketing.ticketSearchResultsModel.count = inRowCount.resultLength;
      dijit.byId("atg_service_ticketing_ticketSearchResultsTable").rowCount = inRowCount.resultLength;
      dijit.byId("atg_service_ticketing_ticketSearchResultsTable").updateRowCount(inRowCount.resultLength);
      atg.service.ticketing.activeTicketsUpdateCount(inRowCount.resultLength);
      console.debug("completed ticketSearchRefreshGrid callback");
    } 
  });
};

  atg.service.ticketing.ticketSearchResultsModel = new atg.data.FormhandlerData(atg.service.ticketing.ticketSearchFields,"/agent/panels/ticketing/activeTicketsEncodeSearchResults.jsp");
  atg.service.ticketing.ticketSearchResultsModel.formId = 'activeTicketsSearchForm';
  atg.service.ticketing.ticketSearchResultsModel.formCurrentPageField = "currentPage";
  <dspel:getvalueof var="rowsPerPage" bean="SearchAgentTicketsFormHandler.resultsPerPage"/>
  atg.service.ticketing.ticketSearchResultsModel.rowsPerPage = ${rowsPerPage};
  atg.service.ticketing.ticketSearchResultsModel.rows = function(inRowIndex, inData) {
    var ticketIdString;
    for (var i=0, l=inData.results.length; i<l; i++) {
      var value = inData.results[i].id;
      if (atg.service.ticketing.currentTicket != value) {
        ticketIdString = atg.service.ticketing.changeTicketString.replace(/theTicketId/g, value);
      }
      else {
        ticketIdString = "<strong>" + value + "</strong>";
        // make the current selection selected
        dijit.byId("atg_service_ticketing_ticketSearchResultsTable").selection.select(i);
      }
      var newRow = [
        ticketIdString,
        inData.results[i].activities,
        inData.results[i].description,
        inData.results[i].group,
        inData.results[i].localizedEscalationLevel,
        inData.results[i].subStatus,
        inData.results[i].dueTime
      ];
      this.setRow(newRow, inRowIndex + i);
    }
    dijit.byId("atg_service_ticketing_ticketSearchResultsTable").selection.multiSelect = false;
    dijit.byId("atg_service_ticketing_ticketSearchResultsTable").onCellClick = function(e) {};
  };

atg.service.ticketing.activeTicketsUpdateCount = function(count) {
  var ticketCount = '<fmt:message key="active-tickets-count"/>';
  dojo.byId('atg_service_ticketing_activeTicketCount').innerHTML = ticketCount.replace(/XXX/gi, count);
}

</script>
<script type="text/javascript">
try {
  _container_.onLoadDeferred.addCallback(function () {
    console.debug("*** activeTicketsSearchResults.jsp addOnLoad called ***");
    atg.service.ticketing.ticketSearchRefreshGrid();
    dijit.byId("atg_service_ticketing_ticketSearchResultsTable").setStructure(atg.service.ticketing.ticketSearchLayout);
    dijit.byId("atg_service_ticketing_ticketSearchResultsTable").update();
    atg.service.ticketing.ticketSearchResizeListener = dojo.hitch(dijit.byId("atg_service_ticketing_ticketSearchResultsTable"), "update");
    activeTicketsSearchResultsHandle = dojo.connect(dijit.byId("globalTicketsContent"), "resize", atg.service.ticketing.ticketSearchResizeListener);
  });
  _container_.onUnloadDeferred.addCallback(function () {
    console.debug("*** activeTicketsSearchResults.jsp addOnunload called ***");
    dojo.disconnect(activeTicketsSearchResultsHandle);
  });
}
catch (e) {
 // do nothing 
}
</script>

<div dojoType="dijit.layout.LayoutContainer" style="width:750px;height:400px;">
  <div dojoType="dojox.layout.ContentPane" scriptHasHooks="true" parseOnLoad="true" layoutAlign="client" style="padding: 3px">

    <span class="atg_resultTotal" id="atg_service_ticketing_activeTicketCount"></span>

    <div style="height:350px">
      <div id="atg_service_ticketing_ticketSearchResultsTable" 
        dojoType="dojox.Grid" 
        model="atg.service.ticketing.ticketSearchResultsModel"
        onMouseOverRow="atg.noop()"
        autoHeight="false"
        onRowClick="atg.noop()"
        onCellClick="atg.noop()">
      </div>
    </div>
    
    <div style="background:white;">
      <input type="button" onclick="dijit.byId('activeTicketsPopup').hide();" value="<fmt:message key='table.tickets.closeButton'/>" style="float:right"/>
      <input type="button" onclick="atg.service.ticketing.ticketSearchRefreshGrid();" value="<fmt:message key='table.tickets.refreshButton'/>" style="float:right;margin-right:8px"/>
    </div>

  </div>
</div>

  </dspel:layeredBundle>
</dspel:page>


<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/activeTicketsSearchResults.jsp#1 $$Change: 946917 $--%>