<%@ tag language="java"%>

<%@ taglib prefix="dsp"	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="value" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="currencyCode" required="true" %>

<%@ attribute name="var" required="false"%>

    <%--
    Get the number of decimal places for the currency code
    --%>
    <dsp:droplet name="/atg/commerce/pricing/CurrencyDecimalPlacesDroplet">
        <dsp:param name="currencyCode" value="${currencyCode}"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="currencyDecimalPlaces" param="currencyDecimalPlaces"/>
        </dsp:oparam>
    </dsp:droplet>

    <c:choose>
      <c:when test="${empty var}">
          <web-ui:formatNumber value="${value}" type="${type}" currencyCode="${currencyCode}"  maxFractionDigits="${currencyDecimalPlaces}" minFractionDigits="${currencyDecimalPlaces}"/>
      </c:when>
      <c:otherwise>
          <web-ui:formatNumber scope="request" var="${var}" value="${value}" type="${type}" currencyCode="${currencyCode}"  maxFractionDigits="${currencyDecimalPlaces}" minFractionDigits="${currencyDecimalPlaces}"/>
      </c:otherwise>
    </c:choose>

<jsp:doBody />
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/formatNumber.tag#1 $$Change: 1179550 $--%>