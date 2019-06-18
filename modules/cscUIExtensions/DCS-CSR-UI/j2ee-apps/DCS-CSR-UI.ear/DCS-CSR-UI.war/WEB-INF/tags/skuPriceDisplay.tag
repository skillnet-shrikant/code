<%--
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/skuPriceDisplay.tag#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ tag language="java" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<%@ attribute name="sku" required="true" type="java.lang.Object" %>
<%@ attribute name="product" required="false" 
  type="java.lang.Object" %>

<%@ attribute name="salePrice" required="true" rtexprvalue="false" %>
<%@ variable name-from-attribute="salePrice"
    variable-class="java.lang.Double"
    alias="displaySalePrice" scope="AT_END" %>
<%@ attribute name="listPrice" required="true" rtexprvalue="false" %>
<%@ variable name-from-attribute="listPrice"
    variable-class="java.lang.Double"
    alias="displayListPrice" scope="AT_END" %>

<dsp:importbean var="configurator" 
  bean="/atg/commerce/custsvc/util/CSRConfigurator" />
<dsp:importbean
  bean="/atg/commerce/custsvc/environment/CSREnvironmentTools"/>
<dsp:importbean var="agentTools"
  bean="/atg/commerce/custsvc/util/CSRAgentTools" />
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>

  <c:if test="${configurator.usingPriceLists}">
    <dsp:droplet name="PriceDroplet">
      <dsp:param name="priceList" bean="CSREnvironmentTools.currentPriceList"/>
      <dsp:param name="product" value="${product}"/>
      <dsp:param name="sku" value="${sku}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="displayListPrice" param="price.listPrice"/>
      </dsp:oparam>
    </dsp:droplet>
    <c:if test="${configurator.usingSalePriceLists}">
      <dsp:droplet name="PriceDroplet">
        <dsp:param name="priceList" bean="CSREnvironmentTools.currentSalePriceList"/>
        <dsp:param name="product" value="${product}"/>
        <dsp:param name="sku" value="${sku}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="displaySalePrice" param="price.listPrice"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
  </c:if>
  <c:if test="${configurator.usingPriceLists == false}">
    <dsp:tomap var="skuMap" value="${sku}"/>
    <c:if test="${skuMap.onSale == true}">
      <c:set var="displaySalePrice" value="${skuMap.salePrice}" /> 
    </c:if>
    <c:set var="displayListPrice" value="${skuMap.listPrice}" /> 
  </c:if>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/skuPriceDisplay.tag#1 $$Change: 946917 $--%>
