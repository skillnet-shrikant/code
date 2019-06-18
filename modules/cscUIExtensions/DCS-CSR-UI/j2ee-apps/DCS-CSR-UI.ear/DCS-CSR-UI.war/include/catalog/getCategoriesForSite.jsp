<%--
 This page generates JSON containing categories for site
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/getCategoriesForSite.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %> 
<dsp:page>
<dsp:getvalueof var="siteId" param="siteId"/> 
<dsp:importbean bean="/atg/commerce/custsvc/multisite/GetCatalogDroplet"/>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:droplet name="GetCatalogDroplet">
    <dsp:param name="siteId" param="siteId" />
    <dsp:setvalue param="catalog" paramvalue="element"/>
    <dsp:oparam name="output">
       <json:object prettyPrint="${UIConfig.prettyPrintResponses}">
        <dsp:getvalueof param="catalog.rootCategories" var="rootCategories"/>
        <json:array name="categories" items="${rootCategories}" var="category">
        <dsp:tomap var="category" value="${category}" />
          <json:object>
            <json:property name="id" value="${category.id}"/>
            <json:property name="name" value="${category.displayName}"/>
          </json:object>
        </json:array> 
      </json:object>
    </dsp:oparam>
    <dsp:oparam name="empty">
    </dsp:oparam>
  </dsp:droplet>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/getCategoriesForSite.jsp#1 $$Change: 946917 $--%> 