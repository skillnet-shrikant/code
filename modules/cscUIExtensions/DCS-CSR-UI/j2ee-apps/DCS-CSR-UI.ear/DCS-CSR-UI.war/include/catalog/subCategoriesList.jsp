<%--
 This page shows subcategories for current selected category
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/subCategoriesList.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale" var="requestLocale"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet"/>
  
  <dsp:getvalueof param="categoryId" var="categoryId"/>
  <dsp:getvalueof param="path" var="path"/>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <c:if test="${!empty categoryId}">
      <div class="atg_commerce_csr_relatedCategories">
		      <dsp:droplet name="SharingSitesDroplet">
		      <dsp:oparam name="output">
		        <dsp:getvalueof var="sites" param="sites"/>
		      </dsp:oparam>
		      </dsp:droplet>
      
        <dsp:droplet name="CategoryLookup">
          <dsp:param name="repositoryKey" bean="RequestLocale.locale"/>
          <dsp:param name="id" value="${categoryId}"/>
          <dsp:param name="sites" value="${sites}"/>
          <dsp:param name="elementName" value="category"/>
          <dsp:oparam name="output">
            <dsp:tomap param="category" var="category"/>
            <dsp:droplet name="/atg/commerce/custsvc/catalog/CSRRecentlyViewedCatalogHistory">
              <dsp:param value="${path}" name="item"/>
            </dsp:droplet>
            <%--<dsp:include src="/include/catalog/childCategories.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="category" value="${category}"/>
              <dsp:param name="path" value="${path}"/>
              <dsp:param name="_windowid" value="${param['_windowid']}"/>
            </dsp:include>--%>
          </dsp:oparam>
        </dsp:droplet>
      </div>
      <br/>
    </c:if>
    <c:import url="/include/catalog/categoriesBreadcrumb.jsp">
      <c:param name="_windowid" value="${param['_windowid']}"/>
      <c:param name="path" value="${path}"/>
    </c:import>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/subCategoriesList.jsp#1 $$Change: 946917 $--%>
