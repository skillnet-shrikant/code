<%--
 A page fragment that renders root category list for browse pop-up

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/rootCategories.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
  <dsp:importbean bean="/atg/commerce/endeca/cache/DimensionValueCacheDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean var="subCategoriesFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/SubCategories"/>
  <dsp:getvalueof var="searchResultPageURL" bean="ContentRequestURLDroplet.searchResultPageURL"/>
  
  
  <dsp:getvalueof var="currentCatalogId" bean="CSREnvironmentTools.currentCatalog.id"/>
  
  <c:set var="rootCategoryFromConfig" value="${endecaConfig.catalogRootCategoryMap[currentCatalogId]}"/>
  <c:set var="rootCategoriesConfigured" value="${not empty rootCategoryFromConfig}"/>
  
  <dsp:getvalueof var="rootCategories" bean="CSREnvironmentTools.currentCatalog.rootCategories"/>
  <c:if test="${rootCategoriesConfigured}">
    <dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
      <dsp:param name="id" value="${rootCategoryFromConfig}"/>
      <dsp:param name="elementName" value="category"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="rootCategories" param="category.childCategories"/>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>

  <ul>
    <c:forEach var="rootCategory" items="${rootCategories}" varStatus="varStatus">
      <dsp:setvalue param="rootCategory" value="${rootCategory}"/>
      <dsp:getvalueof param="rootCategory.childCategories" var="childCategories"/>
      <li>
        <dsp:getvalueof param="rootCategory.id" var="rootCategoryId"/>
        <c:set var="showHideSubCategoriesJs" 
          value="atg.commerce.csr.catalog.endeca.search.showHideSubCategories(${varStatus.count}, '${rootCategoryId}', '/${subCategoriesFragment.servletContext}${subCategoriesFragment.URL}');"/>
        <c:choose>
          <c:when test="${rootCategoriesConfigured}">
            <dsp:droplet name="DimensionValueCacheDroplet">
              <dsp:param name="repositoryId" value="${rootCategoryId}"/>
              <dsp:param name="ancestors" value=""/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="categoryEntry" param="dimensionValueCacheEntry" />
                  <dsp:droplet name="ContentRequestURLDroplet">
                    <dsp:param name="url" value="${UIConfig.contextRoot}${searchResultPageURL}"/>
                    <dsp:param name="dimensionId" value="${categoryEntry.dimvalId}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="contentURL" bean="ContentRequestURLDroplet.url"/>
                    </dsp:oparam>
                  </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
            <a href="#"
                onmouseover="${empty childCategories? '' : showHideSubCategoriesJs }"
                onclick="atgSubmitAction({url: '${contentURL}'});return false;"
                class="${not empty childCategories? '' : 'empty'}">
              <dsp:valueof param="rootCategory.displayName"/>
            </a>
          </c:when>
          <c:otherwise>
            <a href="#"
                onmouseover="${empty childCategories? '' : showHideSubCategoriesJs }"
                class="${not empty childCategories? '' : 'empty'}">
              <dsp:valueof param="rootCategory.displayName"/>
            </a>
          </c:otherwise>
        </c:choose>
      </li>
    </c:forEach>
  </ul>
      
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/rootCategories.jsp#1 $$Change: 946917 $--%>
