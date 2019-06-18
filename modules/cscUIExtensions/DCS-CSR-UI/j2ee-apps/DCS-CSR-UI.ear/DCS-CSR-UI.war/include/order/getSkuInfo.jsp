<%--
 This page encodes the SKU info as JSON
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/getSkuInfo.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ page errorPage="/error.jsp" %>
<%@ page contentType="text/javascript; charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="csr" tagdir="/WEB-INF/tags" %>

<c:catch var="exception">
  <dsp:page>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
    <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
    <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
    <dsp:importbean var="agentTools"  bean="/atg/commerce/custsvc/util/CSRAgentTools" />
    <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
    <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet"/>

    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:getvalueof param="skuId" var="skuId"/>
      <dsp:getvalueof param="productId" var="productId"/>

    <dsp:droplet name="SharingSitesDroplet">
    <dsp:oparam name="output">
      <dsp:getvalueof var="sites" param="sites"/>
    </dsp:oparam>
    </dsp:droplet>

     <csr:getCurrencyCode>
      <c:set var="currencyCode" value="${currencyCode}" scope="request" />
     </csr:getCurrencyCode> 

      <c:if test="${!empty productId}">
        <dsp:droplet name="ProductLookup">
          <dsp:param bean="RequestLocale.locale" name="repositoryKey"/>
          <dsp:param name="id" param="productId"/>
          <dsp:param name="sites" value="${sites}"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:oparam name="empty">
            <dsp:getvalueof value="" var="product"/>
          </dsp:oparam>
          <dsp:oparam name="noCatalog">
            <dsp:getvalueof var="productItem" param="product"/>
          </dsp:oparam>
          <dsp:oparam name="wrongCatalog">
            <dsp:getvalueof var="productItem" param="product"/>
          </dsp:oparam>
          <dsp:oparam name="output">
            <dsp:getvalueof var="productItem" param="product"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>

      <c:if test="${!empty skuId}">
        <dsp:droplet name="SKULookup">
          <dsp:param bean="RequestLocale.locale" name="repositoryKey"/>
          <dsp:param name="id" param="skuId"/>
          <dsp:param name="sites" value="${sites}"/>
          <dsp:param name="elementName" value="sku"/>
          <dsp:oparam name="empty">
            <dsp:getvalueof value="" var="product"/>
          </dsp:oparam>
          <dsp:oparam name="noCatalog">
            <dsp:getvalueof var="skuItem" param="sku"/>
            <dsp:tomap var="sku" param="sku"/>
          </dsp:oparam>
          <dsp:oparam name="output">
            <dsp:getvalueof var="skuItem" param="sku"/>
            <dsp:tomap var="sku" param="sku"/>
          </dsp:oparam>
          <dsp:oparam name="wrongCatalog">
            <dsp:getvalueof var="skuItem" param="sku"/>
            <dsp:tomap var="sku" param="sku"/>
          </dsp:oparam>
        </dsp:droplet>

        {
          skuId: '<dsp:valueof value="${sku.id}"/>',
          <dsp:droplet name="InventoryLookup">
            <dsp:param name="itemId" value="${sku.id}"/>
            <dsp:param name="useCache" value="true"/>
            <dsp:oparam name="output">
              <dsp:droplet name="Switch">
                <dsp:param name="value" param="inventoryInfo.availabilityStatus"/>
                <dsp:oparam name="1001">
                  status: '<fmt:message key="global.product.availabilityStatus.outOfStock"/>',
                </dsp:oparam>
                <dsp:oparam name="1002">
                  status: '<fmt:message key="global.product.availabilityStatus.preorder"/>',
                </dsp:oparam>
                <dsp:oparam name="1003">
                  status: '<fmt:message key="global.product.availabilityStatus.backorder"/>',
                </dsp:oparam>
                <dsp:oparam name="1000">
                  status: '<fmt:message key="global.product.availabilityStatus.inStock"/>',
                </dsp:oparam>
                <dsp:oparam name="unset">
                  status: '',
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
          <csr:skuPriceDisplay salePrice="displaySalePrice" listPrice="displayListPrice" product='${productItem}'  sku='${skuItem}'/>
          <c:if test="${displaySalePrice <  displayListPrice }">
            <csr:formatNumber var="originalPriceResult" value="${displayListPrice}" type="currency" currencyCode="${currencyCode}"/>
            <csr:formatNumber var="salePriceResult" value="${displaySalePrice}" type="currency" currencyCode="${currencyCode}"/>
            price: '<span class="atg_commerce_csr_common_content_strikethrough">${originalPriceResult}</span> ${salePriceResult}'
          </c:if>
          <c:if test="${empty displaySalePrice || (displaySalePrice ==  displayListPrice) }">
            <csr:formatNumber var="result" value="${displayListPrice}" type="currency" currencyCode="${currencyCode}"/>
            price: '${result}'
          </c:if>
        }
      </c:if>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/getSkuInfo.jsp#2 $$Change: 1179550 $--%>
