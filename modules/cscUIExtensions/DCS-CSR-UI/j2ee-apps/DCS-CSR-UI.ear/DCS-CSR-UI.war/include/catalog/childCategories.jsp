<%--
 This page shows the child categories of selected category
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/childCategories.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
  <dsp:page xml="true">

    <dsp:getvalueof param="category" var="category"/>
    <dsp:getvalueof param="path" var="path"/>

    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <c:if test="${!empty category.childCategories}">
        <c:set var="fullPath" value=""/>
        <c:forTokens items="${path}" delims="," var="catId" varStatus="status">
          <c:set var="fullPath" value="${fullPath}$div$category_div_${catId}"/>
        </c:forTokens>
        <dsp:tomap param="category" var="category"/>
        <div class="atg_commerce_csr_relatedCategories">
          <b>
            <fmt:message key="catalogBrowse.searchResults.relatedCategories"/>
          </b>
          <ul>
            <c:forEach items="${category.childCategories}" var="childCategory">
              <dsp:tomap value="${childCategory}" var="childCategory"/>
              <c:set var="categoryId" value="${childCategory.id}"/>
              <li>
                <a href="#" onclick="atg.commerce.csr.catalog.nodeClicked('<c:out value="${categoryId}" />', '<c:out value="${fullPath}" />', '<c:out value="${CSRConfigurator.customCatalogs}"/>');return false;">
                  <img width="14" height="13" border="0" src="<c:url context='/agent' value='/images/icons/folder.gif'/>" alt="<c:out value='${childCategory.displayName}'/>"/>
                  <span class="propertydisplay">
                    <c:out value="${childCategory.displayName}"/>
                  </span>
                </a>
              </li>
              <c:remove var="ancestorsString"/>
            </c:forEach>
          </ul>
        </div>
      </c:if>
    </dsp:layeredBundle>
  </dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/childCategories.jsp#1 $$Change: 946917 $--%>
