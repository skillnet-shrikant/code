<%--
 A page fragment that displays the product price range or, when there's only
 one sku, the price of the sku. 

 @param productToPrice - The product item
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/productPriceRange.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:getvalueof var="rangeProduct" param="product"/>
<dsp:importbean bean="/atg/commerce/custsvc/util/CSRAgentTools" var="csrAgentTools"/>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <dsp:tomap var="rangeProductMap" value="${rangeProduct}"/>
    <csr:priceRange lowPrice="lowestPrice" highPrice="highestPrice" productId="${rangeProductMap.id}"/>

    <csr:getCurrencyCode>
     <c:set var="currencyCode" value="${currencyCode}" scope="request" />
    </csr:getCurrencyCode> 
    
    <c:choose>
    <c:when test="${lowestPrice eq highestPrice and empty lowestPrice}">
      <fmt:message key="catalogBrowse.searchResults.noPrice"/>
    </c:when>
    <c:when test="${lowestPrice eq highestPrice and !empty lowestPrice}">
      <csr:formatNumber value="${lowestPrice}" type="currency" currencyCode="${currencyCode}"/>
    </c:when>
    <c:otherwise>
      <csr:formatNumber value="${lowestPrice}" type="currency" currencyCode="${currencyCode}"/>
      ${empty rangeSeparator ? "-" : rangeSeparator}
      <csr:formatNumber value="${highestPrice}" type="currency" currencyCode="${currencyCode}"/>
    </c:otherwise>
  </c:choose>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/productPriceRange.jsp#2 $$Change: 1179550 $--%>
