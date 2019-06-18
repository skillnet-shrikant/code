<%--
 A page fragment that displays a product 

 @param product - The product
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductPrice.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:getvalueof param="product" var="product"/>

<csr:getCurrencyCode>
  <c:set var="currencyCode" value="${currencyCode}" scope="request" />
</csr:getCurrencyCode> 

<c:choose>
<c:when test="${1 == fn:length(product.childSKUs)}">
  
  <csr:skuPriceDisplay salePrice="displaySalePrice" listPrice="displayListPrice" product="${element}" sku="${product.childSKUs[0]}"/>
  <c:if test="${displaySalePrice <  displayListPrice }">
      <span class="atg_commerce_csr_common_content_strikethrough">
        <csr:formatNumber value="${displayListPrice}" type="currency" currencyCode="${currencyCode}"/>
      </span>&nbsp;
      <csr:formatNumber value="${displaySalePrice}" type="currency" currencyCode="${currencyCode}"/>
  </c:if>
  <c:if test="${empty displaySalePrice || (displaySalePrice ==  displayListPrice) }">
      <csr:formatNumber value="${displayListPrice}" type="currency" currencyCode="${currencyCode}"/>
  </c:if>
</c:when>
<c:otherwise>

  <csr:priceRange lowPrice="lowestPrice" highPrice="highestPrice" productId='${product.repositoryId}'/>
  <c:choose>
    <c:when test="${lowestPrice eq highestPrice and empty lowestPrice}">
        <fmt:message key="catalogBrowse.searchResults.noPrice"/>
    </c:when>
    <c:when test="${lowestPrice eq highestPrice and !empty lowestPrice}">
        <csr:formatNumber value="${lowestPrice}" type="currency" currencyCode="${currencyCode}"/>
    </c:when>
    <c:otherwise>
        <csr:formatNumber value="${lowestPrice}" type="currency" currencyCode="${currencyCode}"/>${empty rangeSeparator ? "-" : rangeSeparator}<csr:formatNumber value="${highestPrice}" type="currency" currencyCode="${currencyCode}"/>
    </c:otherwise>
  </c:choose>
</c:otherwise>
</c:choose>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductPrice.jsp#2 $$Change: 1179550 $--%>
