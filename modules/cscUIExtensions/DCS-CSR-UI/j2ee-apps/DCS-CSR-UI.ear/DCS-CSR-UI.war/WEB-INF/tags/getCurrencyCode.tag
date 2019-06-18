<%@ tag language="java"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dsp"	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<%@ attribute name="order" required="false" type="atg.commerce.order.Order"%>
<%@ attribute name="orderItem" required="false" type="atg.repository.RepositoryItem"%>
<%@ variable name-given="currencyCode" scope="NESTED"%>

<%--
  This tag gets a currency code. If the order is passed in and contains the currency code, that 
  currency code is set. Otherwise the default currency code is set.
--%>

<dsp:importbean var="agentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools" />

<c:choose>
	<c:when test="${not empty order}">
		<c:choose>
			<c:when test="${not empty order.priceInfo.currencyCode}">
    <c:set var="currencyCode" value="${order.priceInfo.currencyCode}" />
			</c:when>
			<c:otherwise>
     <c:set var="currencyCode" value="${agentTools.activeCustomerCurrencyCode}" />
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:when test="${not empty orderItem}">
        <dsp:tomap var="orderItemMap" value="${orderItem}"/> 
        <dsp:tomap var="orderPriceInfoMap" value="${orderItemMap.priceInfo}"/> 
		<c:choose>
			<c:when test="${not empty orderPriceInfoMap && not empty orderPriceInfoMap.currencyCode}">
    <c:set var="currencyCode" value="${orderPriceInfoMap.currencyCode}" />
			</c:when>
			<c:otherwise>
     <c:set var="currencyCode" value="${agentTools.activeCustomerCurrencyCode}" />
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="currencyCode"	value="${agentTools.activeCustomerCurrencyCode}" />
	</c:otherwise>
</c:choose>

<jsp:doBody />
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/getCurrencyCode.tag#1 $$Change: 946917 $--%>
