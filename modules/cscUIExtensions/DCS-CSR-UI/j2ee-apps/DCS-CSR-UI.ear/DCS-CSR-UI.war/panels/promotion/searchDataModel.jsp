<%--
Promotion search JSON
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/searchDataModel.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:getvalueof var="gridPath" param="gp"/>
  <dsp:importbean var="gridConfig" bean="${gridPath}"/>

  <dsp:importbean bean="/atg/commerce/custsvc/promotion/PromotionWalletFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/promotion/PromotionSearch" var="promotionSearch"/>
  <dsp:getvalueof bean="/atg/commerce/custsvc/order/ShoppingCart.current" var="currentOrder"/>
  
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <json:object prettyPrint="${UIConfig.prettyPrintResponses}" escapeXml="false">
      <json:property name="resultLength" value="${promotionSearch.totalItemCount}"/>
      <json:property name="currentPage" value="${promotionSearch.currentPage}"/>
      <json:array name="results" items="${promotionSearch.searchResults}" var="searchResult">
        <dsp:tomap var="promotionMap" value="${searchResult}"/>
        <json:object>
          <c:forEach var="column" items="${gridConfig.columns}" varStatus="colStatus">
            <c:if test="${!empty fn:trim(column.field) && !empty fn:trim(column.dataRendererPage)}">
              <dsp:include src="${fn:trim(column.dataRendererPage.URL)}" 
                           otherContext="${fn:trim(column.dataRendererPage.servletContext)}">
                <dsp:param name="field" value="${fn:trim(column.field)}"/>
                <dsp:param name="colIndex" value="${colStatus.index}"/>
                <dsp:param name="promotionMap" value="${promotionMap}"/>
                <dsp:param name="order" value="${currentOrder}"/>
                <dsp:param name="promotionItem" value="${searchResult}"/>
              </dsp:include>
            </c:if>
          </c:forEach>
        </json:object>
      </json:array>
    </json:object>
	</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/searchDataModel.jsp#1 $$Change: 946917 $--%>
