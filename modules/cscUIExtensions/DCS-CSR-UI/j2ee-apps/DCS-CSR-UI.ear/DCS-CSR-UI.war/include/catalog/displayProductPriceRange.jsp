<%--
 A page fragment that displays the product price range or, when there's only
 one sku, the price of the sku. 

 @param productToPrice - The product item
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/displayProductPriceRange.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:getvalueof var="productToPrice" param="productToPrice"/>
  <dsp:tomap var="productToPriceMap" value="${productToPrice}"/>
  <c:choose>
    <c:when test="${1 == fn:length(productToPriceMap.childSKUs)}">
      <dsp:include src="/include/catalog/displaySkuPrice.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="product" value="${productToPrice}"/>
        <dsp:param name="sku" value="${productToPriceMap.childSKUs[0]}"/>
      </dsp:include>
    </c:when>
    <c:otherwise>
      <dsp:include src="/include/catalog/productPriceRange.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="product" value="${productToPrice}"/>
      </dsp:include>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/displayProductPriceRange.jsp#1 $$Change: 946917 $--%>
