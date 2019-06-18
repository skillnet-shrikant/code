<%--
  A page fragment that recursively displays the subcategory tree navigation

  @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displaySubcategories.jsp#1 $
  @updated $DateTime: 2015/01/26 17:26:27 $

  params:
    childCategories         subcategories to iterate and display
    ancestorRepositoryIds   ancestor repository ids. for top-level can be empty
    deepcount               how deeply to go
    currentlevel            current level for styling

  output param:
    categoriesCount         variable in request scope with subcategory count
--%>


<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/endeca/cache/DimensionValueCacheDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>
  <dsp:importbean var="displaySubcategoriesFragment" bean="/atg/commerce/custsvc/ui/fragments/catalog/endeca/DisplaySubcategories"/>

  <dsp:getvalueof var="searchResultPageURL" bean="ContentRequestURLDroplet.searchResultPageURL"/>
  <dsp:getvalueof var="childCategories" param="childCategories"/>
  <dsp:getvalueof var="ancestorRepositoryIds" param="ancestorRepositoryIds"/>
  <dsp:getvalueof var="deepcount" param="deepcount"/>
  <dsp:getvalueof var="currentlevel" param="currentlevel"/>

  <c:forEach items="${childCategories}" var="childCategory">
    <dsp:droplet name="DimensionValueCacheDroplet">
      <dsp:param name="repositoryId" value="${childCategory['id']}"/>
      <dsp:param name="ancestors" value="${ancestorRepositoryIds}"/>
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
    <%-- Name for subcategory of that level --%>
    <c:set var="categoriesCount" value="${categoriesCount + 1}" scope="request"/>
    <div class="sub-category">
      <a class="level${currentlevel}" href="#" onclick="atgSubmitAction({url: '${contentURL}'});return false;"><c:out value="${childCategory['displayName']}"/></a>
    </div>
    <c:if test="${deepcount > 1 and not empty childCategory['childCategories']}">
      <dsp:include src="${displaySubcategoriesFragment.URL}" otherContext="${displaySubcategoriesFragment.servletContext}">
        <dsp:param name="childCategories" value="${childCategory['childCategories']}"/>
        <dsp:param name="ancestorRepositoryIds" value="${categoryEntry.ancestorRepositoryIds}"/>
        <dsp:param name="deepcount" value="${deepcount - 1}"/>
        <dsp:param name="currentlevel" value="${currentlevel + 1}"/>
      </dsp:include>
    </c:if>
  </c:forEach>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displaySubcategories.jsp#1 $$Change: 946917 $--%>
