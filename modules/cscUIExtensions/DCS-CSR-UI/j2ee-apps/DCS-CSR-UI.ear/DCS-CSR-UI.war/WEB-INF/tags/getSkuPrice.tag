<%@ tag language="java" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
 
<%@ attribute name="output" required="true" rtexprvalue="false" %>
<%@ variable name-from-attribute="output"
    variable-class="java.lang.Double"
    alias="priceOutput" scope="AT_END" %>

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

<c:choose>
  <c:when test="${configurator.usingPriceLists}">
    <dsp:droplet name="PriceDroplet">
      <dsp:param name="priceList" bean="CSREnvironmentTools.currentPriceList"/>
      <dsp:param name="product" value="${product}"/>
      <dsp:param name="sku" value="${sku}"/>
      <dsp:oparam name="empty">
      </dsp:oparam>
      <dsp:oparam name="error">
      </dsp:oparam>
      <dsp:oparam name="output">
        <dsp:getvalueof var="priceOutput" param="price.listPrice"/>
      </dsp:oparam>
    </dsp:droplet>
  </c:when>
  <c:otherwise>
    <dsp:tomap var="sku" value="${sku}"/>
    <c:set var="priceOutput" value="${sku.listPrice}"/>
  </c:otherwise>
</c:choose>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/getSkuPrice.tag#1 $$Change: 946917 $--%>
