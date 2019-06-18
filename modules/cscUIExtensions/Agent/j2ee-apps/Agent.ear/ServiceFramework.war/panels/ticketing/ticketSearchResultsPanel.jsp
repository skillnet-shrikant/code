<!-- ticketSearchResultsPanel.jsp -->
<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/ticketSearchResultsPanel.jsp#2 $$Change: 953229 $
    @updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
  <dspel:importbean scope="request" var="ticketSearchFormHandler" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler" /> 
  <dspel:form style="display:none" action="#" id="ticketSearchResultsViewTicketForm" formid="ticketSearchResultsViewTicketForm">
    <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket"/>
    <dspel:input type="hidden" value="" name="ticketId" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
  </dspel:form>
  <dspel:form style="display:none" action="#" id="ticketSearchResultsViewCustomerForm" formid="ticketSearchResultsViewCustomerForm">
    <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.changeUser"/>
    <dspel:input type="hidden" name="customerId" value="" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.customerId"/>
    <dspel:input type="hidden" name="viewMode" value="" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.viewMode"/>
  </dspel:form>
  <dspel:form style="display:none" formid="ticketSearchResultsTableForm" id="ticketSearchResultsTableForm" action="#">
    <dspel:input type="hidden" value="" priority="-10" name="action" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.action"/>
    <dspel:input type="hidden" name="currentPage" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.currentPage"/>
    <dspel:input type="hidden" name="sortProperty" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.sortField"/>
    <dspel:input type="hidden" name="sortDirection" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.sortDirection"/>
  </dspel:form>

<script type="text/javascript">
  dojo.provide("atg.service.ticketing.search");
  atg.service.ticketing.search.ticketFields = [
      { name: '<fmt:message key="table.tickets.ticketId"/>', canSort: true, property: "id" },
      { name: '<fmt:message key="table.tickets.description"/>', canSort: true, property: "description" },
      { name: '<fmt:message key="table.tickets.created"/>', canSort: true, property: "creationTime" },
      { name: '<fmt:message key="table.tickets.age"/>', canSort: true, property: "age" },
      { name: '<fmt:message key="table.tickets.status"/>', canSort: true, property: "subStatus" },
      { name: '<fmt:message key="table.options.workOn"/>', canSort: false, property: "id" }
  ];

  atg.service.ticketing.search.onBeforeRow = function(inDataIndex, inRow) {
    //console.debug("in onBeforeRow index " + inDataIndex);
    var table = dijit.byId("atg_service_ticketing_search_ticketResultsTable");
    if (table) {
      if (table.expandedRows) {
        //console.debug("old hidden value is: " + table.expandedRows[inDataIndex]);
        var oldHidden = (table.expandedRows[inDataIndex] == true || table.expandedRows[inDataIndex] == "true");
        inRow[1].hidden = !oldHidden;
        //console.debug("new hidden value is " + inRow[1].hidden);
      } else {
        inRow[1].hidden = true;
      }
    } else {
      inRow[1].hidden = true;
    }
  };

atg.service.ticketing.search.getCheckImgTag = "<img src=\"image/theImagePath\" onclick=\"atg.service.ticketing.search.toggle('inRowIndex', 'theShowValue')\" height=\"12\" width=\"12\">";

// this has to go before the layout, since the layout refers to it
atg.service.ticketing.search.getCheck = function (inRow, inCell) {
  //console.debug("in getCheck");
  var table = dijit.byId("atg_service_ticketing_search_ticketResultsTable");
  if (table) {
    var oldExpandedValue = table.expandedRows[inRow] == true || table.expandedRows[inRow] == "true";
    //console.debug("old expanded value is " + oldExpandedValue);
    var images = (oldExpandedValue ? 'nav/nav-arrow-down.gif' : 'nav/nav-arrow-closed.gif');
    var shouldShowNextTime = (oldExpandedValue ? false : true);
    //console.debug ("shouldShowNextTime is : " + shouldShowNextTime);
    var imgTagValue = atg.service.ticketing.search.getCheckImgTag.replace(/theImagePath/, images);
    imgTagValue = imgTagValue.replace(/inRowIndex/, inRow);
    imgTagValue = imgTagValue.replace(/theShowValue/, shouldShowNextTime);
    return imgTagValue;
  }
  //console.debug("returning blank from getCheck");
  return "";
};

atg.service.ticketing.search.toggle = function(inIndex, inShow) {
  //console.debug("toggling " + inIndex + " to " + inShow);
  var table = dijit.byId("atg_service_ticketing_search_ticketResultsTable");
  table.expandedRows[inIndex] = inShow;
  //console.debug("expandedRows is : " + table.expandedRows.toSource());
  table.updateRow(inIndex);
};

// this has to go before the layout, too
atg.service.ticketing.search.getDetail = function(inRow, inCell) {
  //console.debug("in getDetail for row " + inRow.toSource());
  var table = dijit.byId("atg_service_ticketing_search_ticketResultsTable");
  if (table.expandedRows[inRow]) {
    var newNodeId = "atg_service_ticketing_search_detail" + inRow;
    dojo.xhrGet({
      url: "/agent/panels/ticketing/ticketSearchResultsDetails.jsp",
      mimetype: "text/html",
      encoding: "utf-8",
      timeout: atgXhrTimeout,
      content: { 
        ticketId: table.model.getDatum(inRow, 6), // the id value from the data rows
        _windowid: window.windowId
      }, 
      load: function (response, ioArgs) {
        dojo.byId(newNodeId).innerHTML = response;
      }
    });
    return '<div id="' + newNodeId + '" style="height:300px;overflow:auto;"><fmt:message key="loading"/></div>';
  }
  else {
    return '';
  }
};

  atg.service.ticketing.search.ticketView = 
    { onBeforeRow: atg.service.ticketing.search.onBeforeRow,
      cells: 
      [
        [
          { name: ' ', get: atg.service.ticketing.search.getCheck, styles: 'text-align:center;', width: "16px" },
          { name: '<fmt:message key="table.tickets.ticketId" />', width: "100px"},
          { name: '<fmt:message key="table.tickets.description" />', width: "40%"},
          { name: '<fmt:message key="table.tickets.created" />', width: "10em"},
          { name: '<fmt:message key="table.tickets.age" />', width: "80px"},
          { name: '<fmt:message key="table.tickets.status" />', width: "8em"},
          { name: ' ', width: "90px"}
        ],
        [ 
          { name: 'Detail', colSpan: 7, get: atg.service.ticketing.search.getDetail } 
        ]
      ]
    };
  atg.service.ticketing.search.ticketLayout = [atg.service.ticketing.search.ticketView];

  atg.service.ticketing.search.viewTicketString = "<a href=\"#\" class=\"blueU\" onclick=\"document.getElementById('ticketSearchResultsViewTicketForm').ticketId.value='theTicketId';viewTicket('ticketSearchResultsViewTicketForm');return false;\">theTicketId</a>";
  atg.service.ticketing.search.workTicketString = "<a href=\"#\" class=\"blueU\" onclick=\"workTicket('workTicketNoSwitchForm','theTicketId');\"><fmt:message key='table.options.workOn'/></a>";
  atg.service.ticketing.search.reopenTicketString = "<a href=\"#\" onclick=\"reopenTicket('workTicketNoSwitchForm','theTicketId');\"><fmt:message key='reopen-ticket' /></a>";

  atg.service.ticketing.search.ticketSetFormSortProperty = function (/*int*/columnIndex, /*bool*/sortDesc)  {
    var theForm = dojo.byId(atg.service.ticketing.search.ticketResultsModel.formId);
    if (columnIndex != -1) {
      theForm.sortProperty.value = atg.service.ticketing.search.ticketFields[columnIndex].property;
      theForm.sortDirection.value = sortDesc ? "desc" : "asc";
    }
    else
    {
      theForm.sortProperty.value = "id";
      theForm.sortDirection.value = "asc";
    }
  };

atg.service.ticketing.search.ticketRefreshGrid = function () {
  var table = dijit.byId("atg_service_ticketing_search_ticketResultsTable");
  if (table && dojo.byId("ticketSearchForm")) {
    dojo.byId('atg_service_ticketing_search_ticketCount').innerHTML = "<fmt:message key='searching-in-progress'/>";
    table.expandedRows = [];
    atg.service.ticketing.search.ticketResultsModel.fetchRowCount(
    {
      callback: function(inRowCount) {
        console.debug("ticketRefreshGrid called " + inRowCount.resultLength);
        table.editCell = null;
        atg.service.ticketing.search.ticketResultsModel.count = inRowCount.resultLength;
        table.rowCount = inRowCount.resultLength;
        atg.service.ticketing.search.ticketResultsModel.clearData();
        table.updateRowCount(inRowCount.resultLength);
        atg.service.ticketing.search.ticketSearchUpdateCount(inRowCount.resultLength);
        console.debug("completed ticketRefreshGrid callback");
      }
    });
  }
};

  atg.service.ticketing.search.ticketResultsModel = new atg.data.FormhandlerData(atg.service.ticketing.search.ticketFields,"/agent/panels/ticketing/ticketSearchEncodeSearchResults.jsp");
  atg.service.ticketing.search.ticketResultsModel.formId = 'ticketSearchForm';
  atg.service.ticketing.search.ticketResultsModel.formCurrentPageField = "currentPage";
  <dspel:getvalueof var="rowsPerPage" bean="TicketSearchFormHandler.resultsPerPage"/>
  atg.service.ticketing.search.ticketResultsModel.rowsPerPage = ${rowsPerPage};
  atg.service.ticketing.search.ticketResultsModel.rows = function(inRowIndex, inData) {
    var ticketIdString;
    var ticketSelectString;
    for (var i=0, l=inData.results.length; i<l; i++) {
      var value = inData.results[i].id;
      ticketIdString = atg.service.ticketing.search.viewTicketString.replace(/theTicketId/g, value);
      if (inData.results[i].workOrReopen == 'work') {
        ticketSelectString = atg.service.ticketing.search.workTicketString.replace(/theTicketId/g, value);
      }
      else {
        ticketSelectString = atg.service.ticketing.search.reopenTicketString.replace(/theTicketId/g, value);
      }
      var newRow = [
        ticketIdString,
        inData.results[i].description,
        inData.results[i].creationTime,
        inData.results[i].age,
        inData.results[i].status,
        ticketSelectString,
        inData.results[i].id
      ];
      this.setRow(newRow, inRowIndex + i);
    }
  };
</script>
<script type="text/javascript">
_container_.onLoadDeferred.addCallback(function() {
  console.debug("*** ticketsSearchResults.jsp addOnLoad called ***");
  atg.service.ticketing.search.ticketRefreshGrid();
  var table = dijit.byId("atg_service_ticketing_search_ticketResultsTable");
  table.expandedRows = []; // this needs to be before the call to setStructure
  table.setStructure(atg.service.ticketing.search.ticketLayout);
  atg.service.ticketing.search.ticketResizeListener = dojo.hitch(table, "update");
  ticketSearchResultsHandle = dojo.connect(_container_, "resize", atg.service.ticketing.search.ticketResizeListener);
  //table.autoHeight = false;
  var h = Number(300);
  dojo.contentBox(table.domNode, {h: h});
  table.update(); 
});
_container_.onUnloadDeferred.addCallback(function () {
  console.debug("*** ticketsSearchResults.jsp addOnunload called ***");
  dojo.disconnect(ticketSearchResultsHandle);
});

</script>
<script type="text/javascript">
atg.service.ticketing.search.ticketSearchUpdateCount = function(count) {
  if (count !== 0) {
    var matchingStr = "<fmt:message key='matching-tickets'/>";
    dojo.byId('atg_service_ticketing_search_ticketCount').innerHTML = matchingStr.replace(/\{0\}/g, count);
  }
  else {
    dojo.byId('atg_service_ticketing_search_ticketCount').innerHTML = "<fmt:message key='no-matching-tickets'/>";
  }
};

</script>

<span class="atg_resultTotal" id="atg_service_ticketing_search_ticketCount"></span><input type="button" onclick="atg.service.ticketing.search.ticketRefreshGrid();" value="<fmt:message key='table.tickets.refreshButton'/>" style="font-size: 0.9em;"></input>

<div id="atg_service_ticketing_search_ticketResultsTableContainer">
  <div id="atg_service_ticketing_search_ticketResultsTable" 
    dojoType="dojox.Grid" 
    model="atg.service.ticketing.search.ticketResultsModel"
    autoWidth="false"
    autoHeight="false"
    onMouseOverRow="atg.noop()"
    onRowClick="atg.noop()"
    onCellClick="atg.noop()">
  </div>
</div>

  </dspel:layeredBundle>  
</dspel:page>
<!-- end ticketResultsPanel.jsp -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/ticketSearchResultsPanel.jsp#2 $$Change: 953229 $--%>
