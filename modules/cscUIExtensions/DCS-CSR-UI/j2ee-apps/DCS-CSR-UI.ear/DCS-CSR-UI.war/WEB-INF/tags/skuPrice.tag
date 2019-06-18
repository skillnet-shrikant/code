<%@ tag language="java" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="csr" tagdir="/WEB-INF/tags" %>

<%@ attribute name="sku" required="true" type="java.lang.Object" %>
<%@ attribute name="product" required="false" 
  type="java.lang.Object" %>

<dsp:importbean var="configurator" 
  bean="/atg/commerce/custsvc/util/CSRConfigurator" />
<dsp:importbean
  bean="/atg/commerce/custsvc/environment/CSREnvironmentTools"/>
<dsp:importbean var="agentTools"
  bean="/atg/commerce/custsvc/util/CSRAgentTools" />
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <csr:getCurrencyCode>
   <c:set var="currencyCode" value="${currencyCode}" scope="request" />
  </csr:getCurrencyCode> 

  <c:choose>
    <c:when test="${configurator.usingPriceLists}">
      <dsp:droplet name="PriceDroplet">
        <dsp:param name="priceList" bean="CSREnvironmentTools.currentPriceList"/>
        <dsp:param name="product" value="${product}"/>
        <dsp:param name="sku" value="${sku}"/>
        <dsp:oparam name="empty">
          <fmt:message key="skuPriceTag.noPrice"/>
        </dsp:oparam>
        <dsp:oparam name="error">
          <fmt:message key="skuPriceTag.pricingError"/>
        </dsp:oparam>
        <dsp:oparam name="output">
          <csr:formatNumber param="price.listPrice" type="currency" currencyCode="${currencyCode}"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:when>
    <c:otherwise>
      <dsp:tomap var="sku" value="${sku}"/>
      <csr:formatNumber value="${sku.listPrice}" type="currency" currencyCode="${currencyCode}"/>
    </c:otherwise>
  </c:choose>
</dsp:layeredBundle>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/skuPrice.tag#2 $$Change: 1179550 $--%>
