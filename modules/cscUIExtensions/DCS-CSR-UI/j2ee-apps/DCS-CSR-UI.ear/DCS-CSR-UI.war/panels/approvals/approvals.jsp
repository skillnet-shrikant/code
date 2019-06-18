 <%--
 This page defines the approvals panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/approvals/approvals.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean var="gridConfig" bean="/atg/commerce/custsvc/ui/tables/approvals/ApprovalsGrid" />
  
  <dsp:getvalueof var="gridConfig" param="gridConfig" scope="request"/>
  
  <c:if test="${!empty gridConfig.gridHeight}">
    <c:set var="gridHeight" value="${gridConfig.gridHeight}" scope="request"/>
  </c:if>
  
  <c:if test="${!empty gridConfig.progressNodeId}">
    <c:set var="progressNodeId" value="${gridConfig.progressNodeId}" scope="request"/>
  </c:if>
  
  <c:if test="${!empty gridConfig.gridWidgetId}">
    <c:set var="gridWidgetId" value="${gridConfig.gridWidgetId}" scope="request"/>
  </c:if>
  
  <c:if test="${!empty gridConfig.rowsPerPage}">
    <c:set var="rowsPerPage" value="${gridConfig.rowsPerPage}" scope="request"/>
  </c:if>
  
  <dsp:importbean bean="/atg/svc/agent/ui/OriginatingPage"/>
  <dsp:setvalue bean="OriginatingPage.pageName" value="approvals"/>

  <dsp:form style="display:none" formid="approvalsDisplayForm" id="approvalsDisplayForm" action="#">
      <dsp:input type="hidden" value="" priority="-10" name="action" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalRepositoryQueryFormHandler.search"/>
      <dsp:input type="hidden" name="currentPage" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalRepositoryQueryFormHandler.currentPage"/>
      <dsp:input type="hidden" name="sortProperty" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalRepositoryQueryFormHandler.sortField" value="${gridConfig.defaultSortField}"/>
      <dsp:input type="hidden" name="sortDirection" bean="/atg/commerce/custsvc/approvals/order/OrderApprovalRepositoryQueryFormHandler.sortDirection" value="${gridConfig.defaultSortDirection}"/>
  </dsp:form>
  
  <div class="atg_resultTotal" id="${progressNodeId}"><fmt:message key='no-search'/></div>
      
  <div style="width: 100%; height: ${!empty gridHeight ? gridHeight : '450px'}">
    <div id="${gridWidgetId}" dojoType="dojox.Grid" rowCount="10" autoHeight="true" rowsPerPage="${rowsPerPage}" style="width:100%;"
      onMouseOverRow="atg.noop()" onRowClick="atg.noop()" onCellClick="atg.noop()">
    </div>
  </div>
  
  <dsp:include src="/include/approvals/approvalsGrid.jsp" otherContext="${CSRConfigurator.contextRoot}">
    <dsp:param name="gp" bean="/atg/commerce/custsvc/ui/tables/approvals/ApprovalsGrid"/>
  </dsp:include>
  
  <dsp:include src="/include/approvals/approvalsAction.jsp" otherContext="${CSRConfigurator.contextRoot}"/>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/approvals/approvals.jsp#1 $$Change: 946917 $--%>