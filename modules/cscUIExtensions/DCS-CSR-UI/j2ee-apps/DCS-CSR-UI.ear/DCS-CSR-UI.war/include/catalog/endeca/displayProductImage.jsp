<%--
 A page fragment that displays a product 

 @param product - The product
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductImage.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">


<dsp:getvalueof param="productSiteId" var="siteId"/>
<dsp:getvalueof param="currentSiteId" var="currentSiteId"/>
<dsp:getvalueof param="product" var="product"/>

<c:choose>
<c:when test="${!empty product.smallImage.url}">
  <a class="product-image" href="#" onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(currentSiteId)}');return false;" href="#"><img src="${fn:escapeXml(product.smallImage.url)}" alt="${fn:escapeXml(product.displayName)}" /></a>
</c:when>
<c:otherwise>
  <c:url context='/agent' value='/images/icon_confirmationLarge.gif' var="defaultImageURL"/>
  <a href="#" onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(currentSiteId)}');return false;" href="#"><img src="${defaultImageURL}" alt="${fn:escapeXml(product.displayName)}" /></a>
</c:otherwise>
</c:choose>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductImage.jsp#1 $$Change: 946917 $--%>
