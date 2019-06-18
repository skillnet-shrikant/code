<%--
 This page defines the product catalog panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/endeca/productCatalogSearch.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
    <dsp:importbean var="refinementsDisplayFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/Refinements"/>
    <dsp:importbean var="breadcrumbsDisplayFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/Breadcrumbs"/>
    <dsp:importbean var="searchTermInputDisplayFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/SearchTermInput"/>
    <dsp:importbean var="siteScopeDisplayFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/SiteScope"/>
    <dsp:importbean var="categoryTreeDisplayFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/CategoryTree"/>
    <dsp:importbean var="resultsDisplayFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/Results"/>
    <dsp:importbean var="searchState" bean="/atg/commerce/custsvc/catalog/endeca/SearchState"/>
    <dsp:getvalueof var="endecaContentURI" param="contentURI"/>

    <%-- determines the contentURI to used based on the incoming parameters and cached value --%>
    <c:if test="${empty endecaContentURI}">
      <c:set var="endecaContentURI" value="${searchState.lastContentURI}"/>
      <c:if test="${empty endecaContentURI}">
        <c:set var="endecaContentURI" value="${endecaConfig.defaultContentURI}"/>
      </c:if>
    </c:if>

    <dsp:setvalue bean="/atg/commerce/custsvc/catalog/endeca/SearchState.lastContentURI" value="${endecaContentURI}"/>

    <div class="endeca-catalog">
      <table class="layout" style="height:100%">
      <tr>
      <td valign="top">
      <div id="catalog-nav">

        <dsp:droplet name="/atg/commerce/custsvc/catalog/endeca/InvokeAssembler">
          <dsp:param name="includePath" value="${endecaContentURI}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="contentItemResult" param="contentItem"/>
            <dsp:getvalueof var="contentItemMap" param="contentItemMap"/>
          </dsp:oparam>
        </dsp:droplet>

        <div class="search">
          <ul>
              <dsp:include src="${searchTermInputDisplayFragment.URL}" otherContext="${searchTermInputDisplayFragment.servletContext}"/>
              <dsp:include src="${categoryTreeDisplayFragment.URL}" otherContext="${categoryTreeDisplayFragment.servletContext}"/>
          </ul>
        </div>

        <div class="selections">
          <ul>
            <li class="sub-heading"><fmt:message key="endeca.selectionsTitle"/></li>

            <dsp:droplet name="/atg/dynamo/droplet/multisite/SharingSitesDroplet">
            <dsp:param name="excludeInputSite" value="true"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="sites" param="sites"/>
            </dsp:oparam>
            </dsp:droplet>
           <c:if test="${isMultiSiteEnabled == 'true' && !empty sites}">
              <%-- Site Scope (Current site name or 'All sites') --%>
              <dsp:include src="${siteScopeDisplayFragment.URL}" otherContext="${siteScopeDisplayFragment.servletContext}"/>
            </c:if>
            <%-- Breadcrumbs (Search crumb, refinement crumbs, 'clear all') --%>
            <dsp:include src="${breadcrumbsDisplayFragment.URL}" otherContext="${breadcrumbsDisplayFragment.servletContext}">
              <dsp:param name="contentItemMap" value="${contentItemMap}"/>
              <dsp:param name="contentItemResult" value="${contentItemResult}"/>
            </dsp:include>
          </ul>
        </div>

        <dsp:include src="${refinementsDisplayFragment.URL}" otherContext="${refinementsDisplayFragment.servletContext}">
          <dsp:param name="contentItemMap" value="${contentItemMap}"/>
          <dsp:param name="contentItemResult" value="${contentItemResult}"/>
        </dsp:include>

      </div>
      </td>
      <td valign="top" style="width:100%;height:100%">
        <div id="catalog-results">
          <dsp:include src="${resultsDisplayFragment.URL}" otherContext="${resultsDisplayFragment.servletContext}">
          <dsp:param name="contentItemMap" value="${contentItemMap}"/>
          <dsp:param name="contentItemResult" value="${contentItemResult}"/>
          </dsp:include>
        </div>
      </td>
      </tr>
      </table>

    </div>

  </dsp:layeredBundle>
  <script type="text/javascript">
    atg.progress.update('cmcCatalogPS');
  </script>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/endeca/productCatalogSearch.jsp#1 $$Change: 946917 $--%>
