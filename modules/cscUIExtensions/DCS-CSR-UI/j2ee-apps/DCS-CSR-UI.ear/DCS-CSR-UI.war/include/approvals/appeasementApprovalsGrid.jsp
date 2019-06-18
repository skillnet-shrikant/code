<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/appeasementApprovalsGrid.jsp#1 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
<dsp:page xml="true">

<%-- promote to request scope for availability to JSP includes from configuration --%>
<dsp:importbean var="appeasementGridConfig" bean="/atg/commerce/custsvc/ui/tables/approvals/AppeasementApprovalsGrid" />


<%-- promote to request scope for availability to JSP includes from configuration --%>
<dsp:getvalueof var="appeasementGridConfig" param="appeasementGridConfig" scope="request"/>
<c:if test="${!empty appeasementGridConfig.detailFormId}">
  <c:set var="detailFormId" value="${appeasementGridConfig.detailFormId}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.formHandlerPath}">
  <c:set var="formHandlerPath" value="${appeasementGridConfig.formHandlerPath}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.gridHeight}">
  <c:set var="gridHeight" value="${appeasementGridConfig.gridHeight}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.gridInstanceId}">
  <c:set var="gridInstanceId" value="${appeasementGridConfig.gridInstanceId}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.gridPath}">
  <c:set var="gridPath" value="${appeasementGridConfig.gridPath}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.gridWidgetId}">
  <c:set var="gridWidgetId" value="${appeasementGridConfig.gridWidgetId}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.imageClosed}">
  <c:set var="imageClosed" value="${appeasementGridConfig.imageClosed}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.imageOpen}">
  <c:set var="imageOpen" value="${appeasementGridConfig.imageOpen}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.imagePath}">
  <c:set var="imagePath" value="${appeasementGridConfig.imagePath}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.pageBaseOffset}">
  <c:set var="pageBaseOffset" value="${appeasementGridConfig.pageBaseOffset}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.pageIndexElementName}">
  <c:set var="pageIndexElementName" value="${appeasementGridConfig.pageIndexElementName}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.progressNodeId}">
  <c:set var="progressNodeId" value="${appeasementGridConfig.progressNodeId}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.rowsPerPage}">
  <c:set var="rowsPerPage" value="${appeasementGridConfig.rowsPerPage}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.searchFormId}">
  <c:set var="searchFormId" value="${appeasementGridConfig.searchFormId}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.selectLink}">
  <c:set var="selectLink" value="${appeasementGridConfig.selectLink}" scope="request"/>
</c:if>
<c:if test="${!empty appeasementGridConfig.viewLink}">
  <c:set var="viewLink" value="${appeasementGridConfig.viewLink}" scope="request"/>
</c:if>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">


<script type="text/javascript">
dojo.provide("atg.commerce.csr.approvals");
dojo.provide("atg.svc.agent.approvals");

${gridInstanceId} = null;

atg.commerce.csr.approvals.appeasementRefreshSearchResults = function() {
    try {
    console.debug("Refresh Search Results for ${gridInstanceId}");

    var _inst = atg.commerce.csr.approvals.appeasementSetupResultsGrid();
    if (!_inst) {
      console.debug("no instance, setting up a new one");
      ${gridInstanceId} = atg.commerce.csr.approvals.appeasementSetupResultsGrid();
      _inst = ${gridInstanceId};
    }

    _inst.getDataModel().clearData();
    _inst.render();
     console.debug("rendered");
    
    ${gridInstanceId} = _inst;
    } catch (ex) {
      console.debug("EXCEPTION!" + ex);
    }
  };

  atg.commerce.csr.approvals.appeasementSetupResultsGrid = function () {
      var _inst = ${gridInstanceId};
      if (_inst) { console.debug("grid already set up"); return _inst;}

      if (dojo.trim("${selectLink}")) { selectLink = "${selectLink}"; }
      if (dojo.trim("${viewLink}")) { viewLink = "${viewLink}"; }
      var params = {};
      params.formId = "${searchFormId}";
      params.gridWidgetId = "${gridWidgetId}";
      params.progressNodeId = "${progressNodeId}";
      
      
      <c:set var="dataModelPage" value="${appeasementGridConfig.dataModelPage}"/>
      params.url = "<c:url value="${fn:trim(dataModelPage.URL)}" context="${fn:trim(dataModelPage.servletContext)}"
        ><c:param name="_windowid">${windowId}</c:param
        ><c:param name="gp">${gridPath}</c:param
        ></c:url>";
      params.rowsPerPage = ${rowsPerPage};
      <c:if test="${!empty pageBaseOffset}">params.pageBaseOffset = ${pageBaseOffset};</c:if>

      params.currentPageElementName = "${pageIndexElementName}";
      params.messages = { inProgress:"<fmt:message key='search-in-progress'/>",
                          noResultsFound:"<fmt:message key='no-appeasement-approvals'/>",
                          resultsFound:"<fmt:message key='appeasement-approvals'/>"};
      params.dataModel = new atg.data.VirtualGridData(params.formId, params.url, params.rowsPerPage);

      var _inst = new atg.data.grid.VirtualGridInstance(params);

      var columns = [
      <c:forEach var="column" items="${appeasementGridConfig.columns}" varStatus="colStatus">
      <c:if test="${fn:trim(column.isVisible) == 'true'}">{<c:choose
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
      
      console.debug("view : " + view);

      _inst.setStructure(structure);

      return _inst;

    };


    _container_.onLoadDeferred.addCallback(function () {
      dojo.connect(this, "resize", dojo.hitch(dijit.byId("${gridWidgetId}"), "update"));
      atg.commerce.csr.approvals.appeasementRefreshSearchResults();
    });
    _container_.onUnloadDeferred.addCallback(function () {
      //dojo.disconnect(this, "resize", dojo.hitch(dijit.byId("${gridWidgetId}"), "update"));
    });

</script>


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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/appeasementApprovalsGrid.jsp#1 $$Change: 1179550 $--%>
