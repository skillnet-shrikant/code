<%--
 Order JSON Object Creation
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderDataModel.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>

<dsp:page>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<dsp:getvalueof var="gridPath" param="gp"/>
<dsp:importbean var="gridConfig" bean="${gridPath}"/>

<dsp:getvalueof var="formHandlerPath" param="fhp"/>
<dsp:importbean var="formHandler" bean="${formHandlerPath}"/>

{
  "resultLength":${formHandler.totalItemCount},
  "currentPage":${formHandler.currentPage},
  "results":[<c:forEach var="resultWrapper" items="${formHandler.searchResults}" varStatus="status"
    ><dsp:tomap var="orderItemMap" value="${resultWrapper.repositoryItem}"/>${status.index > 0 ? ',' : ''}
    {
      <c:set var="isComma" value="${false}"/><c:forEach var="column" items="${gridConfig.columns}" varStatus="colStatus">
        <c:if test="${!empty fn:trim(column.field) && !empty fn:trim(column.dataRendererPage)}">${isComma ? ',' : '' }
          <dsp:include src="${fn:trim(column.dataRendererPage.URL)}" otherContext="${fn:trim(column.dataRendererPage.servletContext)}">
            <dsp:param name="field" value="${fn:trim(column.field)}"/>
            <dsp:param name="colIndex" value="${colStatus.index}"/>
            <dsp:param name="orderItem" value="${resultWrapper.repositoryItem}"/>
            <dsp:param name="orderItemMap" value="${orderItemMap}"/>
          </dsp:include><c:set var="isComma" value="${true}"/>
        </c:if>
      </c:forEach><c:remove var="isComma"/>
    }</c:forEach>
  ]
}
</dsp:layeredBundle>
</dsp:page>

<%-- Version: $Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderDataModel.jsp#1 $$Change: 946917 $--%>
