<%--
 Available promotions JSON
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/walletDataModel.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>

<dsp:page>
	<dsp:getvalueof var="gridPath" param="gp"/>
	<dsp:importbean var="gridConfig" bean="${gridPath}"/>

  <dsp:importbean bean="/atg/commerce/custsvc/promotion/PromotionWalletFormHandler" var="promotionWallet"/>
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <json:object prettyPrint="${UIConfig.prettyPrintResponses}" escapeXml="false">
      <json:property name="resultLength" value="${promotionWallet.totalItemCount}"/>
      <json:property name="currentPage" value="${promotionWallet.currentPage}"/>
      <json:array name="results" items="${promotionWallet.searchResults}" var="state">
        <dsp:tomap var="promotionMap" value="${state.promotion}"/>
        <json:object>
          <c:forEach var="column" items="${gridConfig.columns}" varStatus="colStatus">
            <c:if test="${!empty fn:trim(column.field) && !empty fn:trim(column.dataRendererPage)}">
              <dsp:include src="${fn:trim(column.dataRendererPage.URL)}" 
                           otherContext="${fn:trim(column.dataRendererPage.servletContext)}">
                <dsp:param name="field" value="${fn:trim(column.field)}"/>
                <dsp:param name="colIndex" value="${colStatus.index}"/>
                <dsp:param name="isComma" value="${isComma}"/>
                <dsp:param name="promotionMap" value="${promotionMap}"/>
                <dsp:param name="promotionState" value="${state}"/>
                <dsp:param name="order" value="${promotionWallet.order}"/>
                <dsp:param name="promotionItem" value="${state.promotion}"/>
              </dsp:include>
            </c:if>
          </c:forEach>
        </json:object>
      </json:array>
    </json:object>
  </dsp:layeredBundle>
</dsp:page>

<%-- Version: $Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/walletDataModel.jsp#1 $$Change: 946917 $--%>
