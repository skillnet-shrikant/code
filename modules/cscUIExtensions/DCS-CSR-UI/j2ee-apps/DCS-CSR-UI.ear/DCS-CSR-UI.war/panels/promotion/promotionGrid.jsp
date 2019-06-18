<%--
 Initializes the promotion grid using the following input parameters:

 gridConfig - the grid configuration component containing all the possible 
   configuration parameters.

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/promotionGrid.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
<dsp:page xml="true">
<%-- promote to request scope for availability to JSP includes from configuration --%>
<dsp:getvalueof var="gridConfig" param="gridConfig" scope="request"/>
<dsp:getvalueof var="allowSort" param="allowSort" scope="request"/>
<dsp:getvalueof var="resultsMessage" param="resultsMessage" scope="request"/>
<dsp:getvalueof var="noResultsMessage" param="noResultsMessage" scope="request"/>

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

<dsp:layeredBundle basename="atg.commerce.csr.Messages">
<script type="text/javascript">
dojo.provide("atg.commerce.csr.promotion");
${gridWidgetId}_refreshSearchResults = function(observer) {
  try {
    var _inst = ${gridInstanceId};
    if (!_inst) {
      console.debug("no instance, setting up a new one");
      ${gridInstanceId} = ${gridWidgetId}_setupGrid();
      _inst = ${gridInstanceId};
      if (observer && _inst.dataModel) {
        _inst.dataModel.observers.push(observer);
      }
    }
    else { // non-standard grid implementation for promotions browser - only hitch methods, not model!
      console.debug("existing instance, reattaching methods to the grid");
      // just hitch methods since model will be stale
      // (setting stale model results in flashing of the stale checkbox statesbefore they refresh)
      _inst.hitchGridMethods();
    }

    _inst.getDataModel().clearData();
    _inst.render();
  }
  catch (ex) {
    console.debug(ex);
  }
};

${gridWidgetId}_setupGrid = function () {
  var _inst = ${gridInstanceId};
  if (_inst) { console.debug("grid already set up"); return _inst;}

  var params = {};
  params.formId = "${searchFormId}";
  params.gridWidgetId = "${gridWidgetId}";
  params.progressNodeId = "${progressNodeId}";

  <c:set var="dataModelPage" value="${gridConfig.dataModelPage}"/>
  params.url = "<c:url value="${fn:trim(dataModelPage.URL)}" context="${fn:trim(dataModelPage.servletContext)}"
    ><c:param name="_windowid">${windowId}</c:param
    ><c:param name="gp">${gridPath}</c:param
    ></c:url>";
  params.rowsPerPage = ${rowsPerPage};
  <c:if test="${!empty pageBaseOffset}">params.pageBaseOffset = ${pageBaseOffset};</c:if>
  params.currentPageElementName = "${pageIndexElementName}";
  params.messages = { inProgress:"<fmt:message key='search-in-progress'/>",
                      noResultsFound:"<fmt:message key='${noResultsMessage}'/>",
                      resultsFound:"<fmt:message key='${resultsMessage}'/>"};

  params.dataModel = new atg.data.VirtualGridData(params.formId, params.url, params.rowsPerPage);

  var _inst = new atg.data.grid.VirtualGridInstance(params);

  var columns = [
  <c:forEach var="column" items="${gridConfig.columns}" varStatus="colStatus"
    ><c:if test="${fn:trim(column.isVisible) == 'true' && (column.field != 'site' || isMultiSiteEnabled)}">{<c:choose
      ><c:when test="${!empty fn:trim(column.resourceBundle) && !empty fn:trim(column.resourceKey)}"
      >
      name: "<dsp:layeredBundle basename="${fn:trim(column.resourceBundle)}"><fmt:message key="${fn:trim(column.resourceKey)}"/></dsp:layeredBundle>"</c:when
      ><c:otherwise
      >
      name: " "</c:otherwise
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
      ><c:if test="${!empty fn:trim(column.sortField) && allowSort}"
      >,
      sortField: "${fn:trim(column.sortField)}"</c:if
      ><c:if test="${!empty fn:trim(column.styles)}"
      >,
      styles: "${fn:trim(column.styles)}"</c:if
      ><c:if test="${!empty fn:trim(column.width)}"
      >,
      width: "${fn:trim(column.width)}"</c:if
      ><c:if test="${!empty fn:trim(column.defaultSort) && allowSort}"
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
</script>

<div style="width: 100%; height: ${!empty gridHeight ? gridHeight : '250px'}">
  <div id="${gridWidgetId}"
       dojoType="dojox.Grid"
       rowCount="10"
       rowsPerPage="${rowsPerPage}"
       style="width:100%;"
       autoHeight="false"
       onMouseOverRow="atg.noop()"
       onRowClick="atg.noop()"
       onCellClick="atg.noop()">
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/promotionGrid.jsp#1 $$Change: 946917 $--%>
