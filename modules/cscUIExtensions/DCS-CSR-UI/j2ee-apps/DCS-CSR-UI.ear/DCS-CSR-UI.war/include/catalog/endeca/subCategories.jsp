<%--
  A page fragment that renders subcategories for given root category for fly-out

  input parameters:
    rootCategoryId    root category id for which to generate content with it's subcategories for the fly-out
    categoryNumber    root category number to use in the dom element id ('category'+categoryNumber)

  @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/subCategories.jsp#1 $
  @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">

  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean var="displaySubcategoriesFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/DisplaySubcategories"/>
  <dsp:getvalueof var="rootCategoryId" param="rootCategoryId"/>
  <dsp:getvalueof var="categoryNumber" param="categoryNumber"/>

  <dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
    <dsp:param name="id" value="${rootCategoryId}"/>
    <dsp:param name="elementName" value="category"/>
    <dsp:oparam name="output">
      <dsp:getvalueof param="category.childCategories" var="childCategories"/>
      <c:if test="${not empty childCategories}">
        <div id="atg_commerce_csr_catalog_endeca_category${categoryNumber}" style="display:none">
          <c:set var="categoriesCount" value="${0}" scope="request"/>
          <dsp:include src="${displaySubcategoriesFragment.URL}" otherContext="${displaySubcategoriesFragment.servletContext}">
            <dsp:param name="childCategories" value="${childCategories}"/>
            <dsp:param name="ancestorRepositoryIds" value=""/>
            <dsp:param name="deepcount" value="3"/>
            <dsp:param name="currentlevel" value="0"/>
          </dsp:include>
          <input type="hidden" value="${categoriesCount}" />
        </div>
      </c:if>
    </dsp:oparam>
  </dsp:droplet>
    
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/subCategories.jsp#1 $$Change: 946917 $--%>
