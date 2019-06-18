<%--
 A page fragment that displays a product 

 @param product - The product
 @param productSiteId - The best match site for this product.
 @param currentSiteId - the current site context
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductDisplayName.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:getvalueof param="product" var="product"/>
<dsp:getvalueof param="productSiteId" var="siteId"/>
<dsp:getvalueof param="currentSiteId" var="currentSiteId"/>

<a  onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(currentSiteId)}');return false;" href="#">${fn:escapeXml(product.displayName)}</a></br>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductDisplayName.jsp#1 $$Change: 946917 $--%>
