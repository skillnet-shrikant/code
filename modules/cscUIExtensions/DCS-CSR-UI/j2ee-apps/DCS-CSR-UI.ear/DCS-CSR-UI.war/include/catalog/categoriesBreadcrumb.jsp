<%--
 This page shows the categories breadcrumb
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/categoriesBreadcrumb.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
    <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
    <dsp:importbean bean="/atg/commerce/custsvc/catalog/ProductSearch" var="productSearch"/>
    <dsp:importbean bean="/atg/commerce/custsvc/catalog/CustomCatalogProductSearch" var="customCatalogProductSearch"/>
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:getvalueof param="path" var="path"/>
      <dsp:getvalueof param="whole" var="whole"/>

      <c:if test="${CSRConfigurator.customCatalogs}">
        <c:set var="productSearch" value="${customCatalogProductSearch}"/>
      </c:if>
  
      <c:if test="${!empty path}">
        <c:set var="ancestorsString" value=""/>
        <c:set var="wholebreadcrumbs" value=""/>
        <c:set var="do_onclick" value=""/>
        <fmt:message var="greaterSign" key="text.greater"/>
        <c:forTokens items="${path}" delims="," var="catId" varStatus="status">
          <dsp:droplet name="CategoryLookup">
            <dsp:param bean="RequestLocale.locale" name="repositoryKey"/>
            <dsp:param name="id" value="${catId}"/>
            <dsp:param name="elementName" value="category"/>
            <dsp:oparam name="output">
              <dsp:tomap param="category" var="category"/>
              <c:choose>
                <c:when test="${ !whole}">
                  <c:if test="${!(status.last && status.first)}">
                    <b>
                    <c:choose>
                      <c:when test="${!status.last}">
                          <a href="#" onclick="atg.commerce.csr.catalog.nodeClicked('<c:out value="${category.id}" />', '<c:out value="${ancestorsString}" />', '<c:out value="${CSRConfigurator.customCatalogs}"/>');return false;">
                            <c:out value="${category.displayName}"/>
                          </a>
                          <c:out value="${greaterSign}"/>&nbsp;
                      </c:when>
                      <c:otherwise>
                        <c:out value="${category.displayName}"/>
                      </c:otherwise>
                    </c:choose>
                    </b>
                  </c:if>
                </c:when>
                <c:otherwise>
                    <c:choose>
                      <c:when test="${status.last}">
                        <div dojoType="dijit.MenuItem"
                             id="atg_commerce_csr_catalog_continueCategory<c:out value='${category.id}'/>" 
                             onClick="atg.commerce.csr.catalog.showProductCatalog('<c:out value="${category.id}"/>', '<c:out value="${ancestorsString}"/>', '<c:out value="${CSRConfigurator.customCatalogs}"/>')"  
                        >
                          <span><c:out value="${wholebreadcrumbs}"/> <c:out value="${category.displayName}"/></span>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <c:set var="wholebreadcrumbs" value="${wholebreadcrumbs} ${category.displayName}${greaterSign}"/>
                      </c:otherwise>
                    </c:choose>
                </c:otherwise>
              </c:choose>
              <c:set var="ancestorsString" value="${ancestorsString}$div$category_div_${category.id}"/>
            </dsp:oparam>
          </dsp:droplet>
        </c:forTokens>
      </c:if>
    </dsp:layeredBundle>

  </dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/categoriesBreadcrumb.jsp#1 $$Change: 946917 $--%>
