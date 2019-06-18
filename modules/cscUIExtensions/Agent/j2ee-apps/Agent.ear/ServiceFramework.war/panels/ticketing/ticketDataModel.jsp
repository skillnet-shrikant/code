<%--
 Customer Ticket History JSON Object Creation
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/ticketDataModel.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>

<dspel:page>

  <dspel:getvalueof var="gridPath" param="gp"/>
  <dspel:importbean var="gridConfig" bean="${gridPath}"/>

  <dspel:importbean var="ticketHistoryFormHandler" bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler"/>

{
  "resultLength":${ticketHistoryFormHandler.totalItemCount},
  "currentPage":${ticketHistoryFormHandler.currentPage},
  "results":[<c:forEach var="ticketItem" items="${ticketHistoryFormHandler.searchResults}" varStatus="status"
    >${status.index > 0 ? ',' : ''}
    {
      <c:set var="isComma" value="${false}"/><c:forEach var="column" items="${gridConfig.columns}" varStatus="colStatus">
        <c:if test="${!empty fn:trim(column.field) && !empty fn:trim(column.dataRendererPage)}">${isComma ? ',' : '' }
          <dspel:include src="${fn:trim(column.dataRendererPage.URL)}" otherContext="${fn:trim(column.dataRendererPage.servletContext)}">
            <dspel:param name="field" value="${fn:trim(column.field)}"/>
            <dspel:param name="colIndex" value="${colStatus.index}"/>
            <dspel:param name="ticketItem" value="${ticketItem}"/>
          </dspel:include><c:set var="isComma" value="${true}"/>
        </c:if>
      </c:forEach><c:remove var="isComma"/>
    }</c:forEach>
  ]
}
</dspel:page>

<%-- Version: $Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/ticketDataModel.jsp#1 $$Change: 946917 $--%>
