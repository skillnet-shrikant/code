<%--
 A page fragment that displays the category tree navigation

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayCategoryTree.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/endeca/SearchState"/>
  <dsp:importbean var="rootCategoriesFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/RootCategories"/>

  <dsp:getvalueof bean="SearchState.siteScope" var="siteScope"/>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <%-- Browse should be available only for "Current" site only. [CSC-EndecaCatalogSearch-50] --%>
  <script type="text/javascript">
    function showHideMainCategoriesStopEvent(e) {
      if(!e) var e = window.event;
      //e.cancelBubble is supported by IE -
      // this will kill the bubbling process.
      e.cancelBubble = true;
      e.returnValue = false;
      //e.stopPropagation works only in Firefox.
      if ( e.stopPropagation ) e.stopPropagation();
      if ( e.preventDefault ) e.preventDefault();
      return false;
    }
  </script>
  <c:if test="${siteScope == 'current'}">  
    <li style="float:none" class="browse-row" onmouseover="this.className='browse-row-selected'" onmouseout="this.className='browse-row'" 
      onclick="atg.commerce.csr.catalog.endeca.search.showHideMainCategories('/${rootCategoriesFragment.servletContext}${rootCategoriesFragment.URL}'); return showHideMainCategoriesStopEvent(event);"><fmt:message key="endeca.browse"/><div class="icon-browse"></div>
    </li>
    <li style="float:none">
      <div id="atg_commerce_csr_catalog_endeca_categories" class="categories"></div>
      <div id="atg_commerce_csr_catalog_endeca_sub-categories" class="sub-categories"></div>
      <div id="atg_commerce_csr_catalog_endeca_subcategoryFlyOutContentCache"></div>
    </li>
  </c:if>
  </dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayCategoryTree.jsp#1 $$Change: 946917 $--%>
