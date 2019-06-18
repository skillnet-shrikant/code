<%@ tag language="java"%>
<%@ attribute name="creditCard" required="true"  type="atg.commerce.order.CreditCard"%>

<%@ taglib prefix="dsp"
  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <fmt:message key="${fn:toLowerCase(creditCard.creditCardType)}" />&nbsp;
    <fmt:message key="common.hyphen" />&nbsp;
  </dsp:layeredBundle>
    <dsp:droplet  name="/atg/commerce/custsvc/events/ViewCreditCardEventDroplet">
    <dsp:param name="profile" bean="/atg/userprofiling/ActiveCustomerProfile" />
    <dsp:param name="creditCardNumber" value="${creditCard.creditCardNumber}" />
    </dsp:droplet>
    <c:if test="${!empty creditCard && !empty creditCard.creditCardNumber}">
      <c:set var="cardNumberIndex" value="${fn:length(creditCard.creditCardNumber) - 4}"/>
      <c:out value="${fn:substring(creditCard.creditCardNumber, cardNumberIndex, -1)}"/>
    </c:if>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/displayCreditCardType.tag#1 $$Change: 946917 $--%>
