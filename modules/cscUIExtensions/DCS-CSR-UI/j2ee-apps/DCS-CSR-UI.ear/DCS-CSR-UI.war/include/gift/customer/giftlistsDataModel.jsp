<%--

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/customer/giftlistsDataModel.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:getvalueof var="gridPath" param="gp"/>
  <dsp:importbean var="gridConfig" bean="${gridPath}"/>
  <dsp:getvalueof var="giftlistTableFormHandler" bean="/atg/commerce/custsvc/gifts/GiftlistTableFormHandler" />

{
  "resultLength":${giftlistTableFormHandler.totalItemCount},
  "currentPage":${giftlistTableFormHandler.currentPage},
  "results":[<c:forEach var="giftlistItem" items="${giftlistTableFormHandler.searchResults}" varStatus="status"
    ><dsp:tomap var="giftlistItemMap" value="${giftlistItem}"/>${status.index > 0 ? ',' : ''}
    {
      <c:set var="isComma" value="${false}"/><c:forEach var="column" items="${gridConfig.columns}" varStatus="colStatus">
        <c:if test="${!empty fn:trim(column.field) && !empty fn:trim(column.dataRendererPage)}">${isComma ? ',' : '' }
          <dsp:include src="${fn:trim(column.dataRendererPage.URL)}" otherContext="${fn:trim(column.dataRendererPage.servletContext)}">
            <dsp:param name="field" value="${fn:trim(column.field)}"/>
            <dsp:param name="colIndex" value="${colStatus.index}"/>
            <dsp:param name="giftlistItemMap" value="${giftlistItemMap}" />
          </dsp:include><c:set var="isComma" value="${true}"/>
        </c:if>
      </c:forEach><c:remove var="isComma"/>
    }</c:forEach>
  ]
}
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/customer/giftlistsDataModel.jsp#1 $$Change: 946917 $--%>