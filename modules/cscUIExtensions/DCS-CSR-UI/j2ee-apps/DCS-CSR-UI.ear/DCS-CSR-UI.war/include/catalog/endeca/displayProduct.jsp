<%--
 A page fragment that displays a product 

 @param contentItem - The content item that references the product
 @param productId - The product id
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProduct.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:getvalueof param="contentItem" var="contentItem"/>
<dsp:getvalueof param="objectType" var="objectType"/>
<dsp:getvalueof param="resultObject" var="productItem"/>
<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>

<dsp:importbean bean="/atg/multisite/Site"/> 
<dsp:getvalueof var="currentSiteId" bean="Site.id"/>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

<c:set var="resultType" value="${contentItem['@type']}"/>


<c:if test="${objectType == 'RepositoryItem'}">
  <dsp:getvalueof var="productId" value="${productItem.repositoryId}"/>
</c:if>

<c:if test="${objectType == 'Record'}">
  <dsp:getvalueof var="productId"  value="${productItem.attributes[endecaConfig.recordProductIdPropertyName]}"/>
</c:if>

<dsp:droplet name="/atg/commerce/custsvc/catalog/CSRProductLookup">
<dsp:param name="id" value="${productId}" />
<dsp:oparam name="output">
  <dsp:getvalueof var="product" param="element" />
</dsp:oparam>
</dsp:droplet>

<dsp:droplet name="/atg/commerce/multisite/SiteIdForCatalogItem">
<dsp:param name="item" value="${product}"/>
<dsp:param name="currentSiteFirst" value="true"/>
<dsp:oparam name="output">
  <dsp:getvalueof param="siteId" var="productSiteId"/>
</dsp:oparam>
</dsp:droplet>
  
<div class="column-site">
  <div class="first-cell-div">
      <dsp:include src="/include/catalog/endeca/displayProductSite.jsp">
      <dsp:param name="product" value="${product}"/>
      <dsp:param name="productSiteId" value="${productSiteId}"/>
     </dsp:include>
  </div>
</div>
<div class="column-image">
  <div class="second-cell-div">
    <dsp:include src="/include/catalog/endeca/displayProductImage.jsp">
    <dsp:param name="product" value="${product}"/>
    <dsp:param name="currentSiteId" value="${currentSiteId}"/>
    <dsp:param name="productSiteId" value="${productSiteId}"/>
   </dsp:include>
  </div>
</div>
<div class="column-name">
  <dsp:include src="/include/catalog/endeca/displayProductDisplayName.jsp">
  <dsp:param name="product" value="${product}"/>
  <dsp:param name="currentSiteId" value="${currentSiteId}"/>
  <dsp:param name="productSiteId" value="${productSiteId}"/>
  </dsp:include>
  <span><c:out value="${fn:escapeXml(product.id)}"/></span>
</div>
<div class="column-price"> 
  <dsp:include src="/include/catalog/endeca/displayProductPrice.jsp">
  <dsp:param name="product" value="${product}"/>
  </dsp:include>
</div>
  <c:choose>
    <%-- show add to cart when only one sku --%>
    <c:when test="${1 == fn:length(product.childSKUs)}">
      <div class="column-action">
        <dsp:include src="/include/catalog/endeca/displayProductAddAction.jsp">
        <dsp:param name="product" value="${product}"/>
        <dsp:param name="currentSiteId" value="${currentSiteId}"/>
        <dsp:param name="productSiteId" value="${productSiteId}"/>
        <dsp:param name="inputIdModifier" value="${resultType}"/>
        </dsp:include>
      </div>
    <div class="column-qty">
      <dsp:include src="/include/catalog/endeca/displayProductAddQuantity.jsp">
      <dsp:param name="product" value="${product}"/>
      <dsp:param name="inputIdModifier" value="${resultType}"/>
      </dsp:include>
    </div>
  </c:when>
  <c:otherwise>
    <div class="column-action">
      <dsp:include src="/include/catalog/endeca/displayProductViewAction.jsp">
      <dsp:param name="product" value="${product}"/>
      <dsp:param name="currentSiteId" value="${currentSiteId}"/>
      <dsp:param name="productSiteId" value="${productSiteId}"/>
      </dsp:include>
    </div>
    <div class="column-qty"></div>
  </c:otherwise>  
  </c:choose>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProduct.jsp#1 $$Change: 946917 $--%>
