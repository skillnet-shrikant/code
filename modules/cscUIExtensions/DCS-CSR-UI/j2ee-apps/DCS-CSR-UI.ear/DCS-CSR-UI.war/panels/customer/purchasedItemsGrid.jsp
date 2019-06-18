<%--
 Initializes the order search results grid using the following input parameters:

 gridConfig - the grid configuration component containing all the possible 
   configuration parameters.

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/purchasedItemsGrid.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
<dsp:page xml="true">
<dsp:getvalueof var="panelId" param="panelId"/>
<%-- promote to request scope for availability to JSP includes from configuration --%>
<dsp:getvalueof var="gridConfig" param="gridConfig" scope="request"/>

<c:if test="${!empty gridConfig.detailFormId}">
  <c:set var="detailFormId" value="${gridConfig.detailFormId}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.formHandlerPath}">
  <c:set var="formHandlerPath" value="${gridConfig.formHandlerPath}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.gridHeight}">
  <c:set var="gridHeight" value="${gridConfig.gridHeight}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.gridInstanceId}">
  <c:set var="gridInstanceId" value="${gridConfig.gridInstanceId}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.gridPath}">
  <c:set var="gridPath" value="${gridConfig.gridPath}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.gridWidgetId}">
  <c:set var="gridWidgetId" value="${gridConfig.gridWidgetId}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.imageClosed}">
  <c:set var="imageClosed" value="${gridConfig.imageClosed}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.imageOpen}">
  <c:set var="imageOpen" value="${gridConfig.imageOpen}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.imagePath}">
  <c:set var="imagePath" value="${gridConfig.imagePath}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.pageBaseOffset}">
  <c:set var="pageBaseOffset" value="${gridConfig.pageBaseOffset}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.pageIndexElementName}">
  <c:set var="pageIndexElementName" value="${gridConfig.pageIndexElementName}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.progressNodeId}">
  <c:set var="progressNodeId" value="${gridConfig.progressNodeId}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.rowsPerPage}">
  <c:set var="rowsPerPage" value="${gridConfig.rowsPerPage}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.searchFormId}">
  <c:set var="searchFormId" value="${gridConfig.searchFormId}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.selectLink}">
  <c:set var="selectLink" value="${gridConfig.selectLink}" scope="request"/>
</c:if>
<c:if test="${!empty gridConfig.viewLink}">
  <c:set var="viewLink" value="${gridConfig.viewLink}" scope="request"/>
</c:if>

<dsp:layeredBundle basename="atg.commerce.csr.Messages">
<script type="text/javascript">
dojo.provide("atg.commerce.csr.order");
atg.commerce.csr.order.refreshPurchasedItemsSearchResults = function() {
  try {
  console.debug("Refresh Search Results for ${gridInstanceId}");

  var _inst = ${gridInstanceId};
  if (!_inst) {
    console.debug("no instance, setting up a new one");
    ${gridInstanceId} = atg.commerce.csr.order.setupPurchasedItemsResultsGrid();
    _inst = ${gridInstanceId};
  }
  else { // instance exists, reattach to grid
    console.debug("existing instance, reattaching the model to the grid");
    _inst.hitchGridInstanceToWidget();
  }

  _inst.getDataModel().clearData();
  _inst.render();
  } catch (ex) {
    console.debug(ex);
  }
};

atg.commerce.csr.order.setupPurchasedItemsResultsGrid = function () {
  var _inst = ${gridInstanceId};
  if (_inst) { console.debug("grid already set up"); return _inst;}
  var params = {};
  params.formId = "${searchFormId}";
  params.gridWidgetId = "${gridWidgetId}";
  params.progressNodeId = "${progressNodeId}";

  <c:set var="dataModelPage" value="${gridConfig.dataModelPage}"/>
  params.url = "<c:url value="${fn:trim(dataModelPage.URL)}" context="${fn:trim(dataModelPage.servletContext)}"
    ><c:param name="_windowid">${windowId}</c:param
    ><c:param name="fhp">${formHandlerPath}</c:param
    ><c:param name="gp">${gridPath}</c:param
    ></c:url>";
  params.rowsPerPage = ${rowsPerPage};
  <dsp:getvalueof var="threshold" bean="/atg/commerce/custsvc/order/PurchasedItemsHistoryTableFormHandler.daysThreshold"/>
  
  <c:if test="${!empty pageBaseOffset}">params.pageBaseOffset = ${pageBaseOffset};</c:if>
  params.currentPageElementName = "${pageIndexElementName}";
  params.messages = { inProgress:"<fmt:message key='search-in-progress'/>",
                      noResultsFound:"<fmt:message key='purchasedHistory.noitems'><fmt:param value='${threshold}'/></fmt:message>",
                      resultsFound:"<fmt:message key='purchasedHistory.threshold'><fmt:param value='${threshold}'/></fmt:message>"};

  params.dataModel = new atg.data.VirtualGridData(params.formId, params.url, params.rowsPerPage);

  var _inst = new atg.data.grid.VirtualGridInstance(params);

  var columns = [
  <c:forEach var="column" items="${gridConfig.columns}" varStatus="colStatus"
    ><c:if test="${fn:trim(column.isVisible) == 'true'}">{<c:choose
      ><c:when test="${!empty fn:trim(column.resourceBundle) && !empty fn:trim(column.resourceKey)}"
      >
      <c:set var="ea" value=""/>
      <c:if test="${column.field == 'isReturnable'}">
        <c:set var="ea" value="ea_csc_purchased_isReturnable"/>
      </c:if>
      name: "<dsp:layeredBundle basename="${fn:trim(column.resourceBundle)}"><fmt:message key="${fn:trim(column.resourceKey)}"/></dsp:layeredBundle>" <c:if test="${!empty ea}"> + "<span id='${ea}'></span>"</c:if>
      </c:when
      ><c:otherwise
      >
      name: "   "</c:otherwise
      ></c:choose
      ><c:if test="${!empty fn:trim(column.cellRendererPage)}"
        >,
      get: <dsp:include src="${fn:trim(column.cellRendererPage.URL)}" 
        otherContext="${fn:trim(column.cellRendererPage.servletContext)}"
        ><dsp:param name="field" value="${fn:trim(column.field)}"
        /><dsp:param name="index" value="${colStatus.index}"
        /></dsp:include
      ></c:if
      ><c:if test="${!empty fn:trim(column.field)}"
      >,
      field: "${fn:trim(column.field)}"</c:if
      ><c:if test="${!empty fn:trim(column.sortField)}"
      >,
      sortField: "${fn:trim(column.sortField)}"</c:if
      ><c:if test="${!empty fn:trim(column.styles)}"
      >,
      styles: "${fn:trim(column.styles)}"</c:if
      ><c:if test="${!empty fn:trim(column.width)}"
      >,
      width: "${fn:trim(column.width)}"</c:if
      ><c:if test="${!empty fn:trim(column.defaultSort)}"
      >,
      defaultSort: "${fn:trim(column.defaultSort)}"</c:if
      >
  },</c:if>
  </c:forEach>
  ];

  var view = { cells: [ columns ] };
  var layout = [ view ];
  var structure = layout;

  _inst.setStructure(structure);

  return _inst;

};

_container_.onLoadDeferred.addCallback(function () {
  dojo.connect(this, "resize", dojo.hitch(dijit.byId("${gridWidgetId}"), "update"));
  dijit.byId("${gridWidgetId}").handlers = [];
  dijit.byId("${gridWidgetId}").handlers["/togglePanelContent"] = dojo.subscribe("/togglePanelContent", function (panelId) {if(panelId=="${panelId}"){dijit.byId("${gridWidgetId}").update();}});
  atg.commerce.csr.order.refreshPurchasedItemsSearchResults ();
});
_container_.onUnloadDeferred.addCallback(function () {
  //dojo.disconnect(this, "resize", dojo.hitch(dijit.byId("${gridWidgetId}"), "update"));
  dojo.unsubscribe(dijit.byId("${gridWidgetId}").handlers["/togglePanelContent"]);
});

if (!dijit.byId("productQuickViewPopup")) {
  new dojox.Dialog({ id: "productQuickViewPopup",
                     cacheContent: "false",
                     executeScripts: "true",
                     scriptHasHooks: "true",
                     duration: 100,
                     "class": "atg_commerce_csr_popup"});
}



</script>

<div class="atg_resultTotal" id="${progressNodeId}"><fmt:message key='no-search'/></div>

<div style="width:100%">
  <div id="${gridWidgetId}"
       dojoType="dojox.Grid"
       rowCount="10"
       autoHeight="8"
       rowsPerPage="${rowsPerPage}"
       style="width:100%;"
       onMouseOverRow="atg.noop()"
       onRowClick="atg.noop()"
       onCellClick="atg.noop()">
  </div>
</div>

</div>

</dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/purchasedItemsGrid.jsp#1 $$Change: 946917 $--%>
