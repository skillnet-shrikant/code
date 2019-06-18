<%--
 A page fragment that displays the SKU price

 @param product - The product item
 @param sku - The SKU item belonging to the product
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/displaySkuPrice.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRAgentTools" var="csrAgentTools"/>
  <dsp:getvalueof var="sku" param="sku"/>
  <dsp:getvalueof var="product" param="product"/>
  <csr:skuPriceDisplay salePrice="displaySalePrice" listPrice="displayListPrice" product="${product}" sku="${sku}"/>
  <csr:getCurrencyCode>
   <c:set var="currencyCode" value="${currencyCode}" scope="request" />
  </csr:getCurrencyCode> 


           <c:if test="${displaySalePrice < displayListPrice}">
		    <span class="atg_commerce_csr_common_content_strikethrough">
		      <csr:formatNumber value="${displayListPrice}" type="currency" currencyCode="${currencyCode}"/>
		    </span>
		    &nbsp;<csr:formatNumber value="${displaySalePrice}" type="currency" currencyCode="${currencyCode}"/>
           </c:if>
           <c:if test="${displaySalePrice == displayListPrice}">
  		   <csr:formatNumber value="${displayListPrice}" type="currency" currencyCode="${currencyCode}"/>
           </c:if> 
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/displaySkuPrice.jsp#2 $$Change: 1179550 $--%>
