<%--
 A page fragment that displays a product 

 @param product - The product
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductViewAction.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:getvalueof param="product" var="product"/>
<dsp:getvalueof param="productSiteId" var="siteId"/>
<dsp:getvalueof param="currentSiteId" var="currentSiteId"/>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<fmt:message key='catalogBrowse.searchResults.view' var="viewButtonLabel"/>
  <input type="button" name="submit" value="${fn:escapeXml(viewButtonLabel)}" onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(currentSiteId)}')"/>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductViewAction.jsp#1 $$Change: 946917 $--%>
