<%--
Display the appropriate details for the payment group.

Expected params
paymentGroup : The payment group.
currencyCode : The order.priceInfo.currencyCode value.

@version $Id:
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />

  <dsp:getvalueof var="pgType" param="paymentGroup.paymentGroupClassType" />
  <dsp:getvalueof var="pgTypeConfig"  bean="CSRConfigurator.paymentGroupTypeConfigurationsAsMap.${pgType}" />
  <c:choose>
    <c:when test="${pgTypeConfig != null && pgTypeConfig.displayPageFragment != null}">
      <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
      otherContext="${pgTypeConfig.displayPageFragment.servletContext}" />
    </c:when>
    <c:otherwise>
      <dsp:getvalueof var="pgTypeConfig" bean="/atg/commerce/custsvc/ui/UnknownPaymentGroupConfiguration" />
        <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
        otherContext="${pgTypeConfig.displayPageFragment.servletContext}" />
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/finishOrderBillingLineItem.jsp#1 $$Change: 946917 $--%>