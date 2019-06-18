<%--
 A page fragment that displays a product 

 @param product - The product
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductSite.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:getvalueof param="product" var="product"/>
<dsp:getvalueof param="productSiteId" var="siteId"/>

<csr:siteIcon siteId="${siteId}" />

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductSite.jsp#1 $$Change: 946917 $--%>
