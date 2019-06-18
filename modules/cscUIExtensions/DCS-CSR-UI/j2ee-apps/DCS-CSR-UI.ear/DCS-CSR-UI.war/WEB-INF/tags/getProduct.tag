<%@ tag language="java" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<%@ attribute name="commerceItemId" required="false" %>
<%@ attribute name="productId" required="false" %>

<%@ variable name-given="product" scope="NESTED"
  variable-class="atg.adapter.gsa.GSAItem"%>
<%@ variable name-given="skuId" scope="NESTED"%>
<%@ variable name-given="commerceItem" scope="NESTED"%>

<%--

  Get the product repository item from the current order if commerceItemId has
  been provided, or otherwise by doing a ProductLookup with the product id

--%>

<dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup" />
<dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>

<c:choose>
 <c:when test="${not empty commerceItemId}">
   <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart"  
     var="cart" />
   <c:set var="order" value="${cart.current}"/>
   <c:forEach var="item" items="${order.commerceItems}">
     <c:if test="${commerceItemId == item.id}">
       <c:set var="product" value="${item.auxiliaryData.productRef}"/>
       <c:set var="commerceItem" value="${item}"/>
     </c:if>
   </c:forEach>
 </c:when>
 <c:otherwise>
 
   <%-- The purpose of this tag is to lookup a product. This tag is not used to 
      restrict the product lookup within a single or multiple catalogs.
      If you want to restrict the product lookup within a catalog or catalogs, then
      you need to use /atg/commerce/catalog/ProductLookup. This tag is used in new order 
      and existing order view pages. After the order is submitted, the product could be moved
      from the catalog or sites could be deleted or sites could be moved. Thus in order to make this 
      tag work in all cases, CSRProductLookup droplet is used.
   --%>
   <dsp:droplet name="CSRProductLookup">
     <dsp:param bean="RequestLocale.locale" name="repositoryKey"/>
     <dsp:param name="id" value="${productId}"/>
     <dsp:oparam name="noCatalog">
       <dsp:getvalueof var="product" param="element"/>
     </dsp:oparam>
     <dsp:oparam name="output">
       <dsp:getvalueof var="product" param="element"/>
     </dsp:oparam>
   </dsp:droplet>
 </c:otherwise>
</c:choose>

<jsp:doBody/>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/getProduct.tag#1 $$Change: 946917 $--%>
