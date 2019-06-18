<%--
 A page fragment that displays the add product button. Workgs with displayProductAddQuantity.dsp to provide the input controls for adding the product
 to the cart. 

 @param product - The product
 @param productSiteId - The best match site for this product.
 @param currentSiteId - the current site context
 @param inputIdModifier - The value is prepended to the id of the input tag used to capture the quantity. 
 This value is optional to distinguish the same product input appearing multiple time on the page.
 
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductAddAction.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:getvalueof param="product" var="product"/>
<dsp:getvalueof param="productSiteId" var="siteId"/>
<dsp:getvalueof param="currentSiteId" var="currentSiteId"/>
<dsp:getvalueof param="inputIdModifier" var="inputIdModifier"/>

<dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="shoppingCart"/>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

<%-- This flag is used to disable the add to cart controls when the current order is not modifiable --%>
<dsp:droplet name="/atg/commerce/custsvc/order/OrderIsModifiable">
<dsp:param name="order" value="${shoppingCart.originalOrder}"/>
<dsp:oparam name="true">
  <c:set var="orderIsModifiable" value="true"/>
</dsp:oparam>
</dsp:droplet>

<c:choose>
<c:when test="${orderIsModifiable}">
  <c:set var="orderIsModifiableDisableAttribute" value=""/>
</c:when>
<c:otherwise>
  <c:set var="orderIsModifiableDisableAttribute" value=" disabled='disabled' "/>
</c:otherwise>
</c:choose>

<dsp:tomap value="${product.childSKUs}" var="childSKUs"/>
<dsp:tomap value="${childSKUs.baseList[0]}" var="singleSKU"/>
<fmt:message key="catalogBrowse.searchResults.productAddedToOrder.js" var="addToOrderMsg"><fmt:param value="${fn:escapeXml(product.displayName)}"/></fmt:message>
<c:set var="addToOrderMsg_search" value="&#039;"/>
<c:set var="addToOrderMsg_replace" value="&#092;&#039;"/>
<c:set var="addToOrderMsg" value="${fn:replace(addToOrderMsg,addToOrderMsg_search,addToOrderMsg_replace)}"/>
<fmt:message key="genericRenderer.invalidQuantityMessage" var="addToOrderInvalidQuantityMsg"/>
<c:set var="addToOrderInvalidQuantityMsg_search" value="&#039;"/>
<c:set var="addToOrderInvalidQuantityMsg_replace" value="&#092;&#039;"/>
<c:set var="addToOrderInvalidQuantityMsg" value="${fn:replace(addToOrderInvalidQuantityMsg,addToOrderInvalidQuantityMsg_search,addToOrderInvalidQuantityMsg_replace)}"/>

<fmt:message key='catalogBrowse.searchResults.buy' var="buyButtonLabel"/>

  <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
    <dsp:param name="product" value="${product.id}"/>
    <dsp:param name="sku" value="${singleSKU.id}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="isFractional" param="fractional"/>
    </dsp:oparam>
  </dsp:droplet>

  <input id="itemQuantity${inputIdModifier}${fn:escapeXml(product.id)}_button" type="button" name="submit" value="${fn:escapeXml(buyButtonLabel)}" onclick="atg.commerce.csr.catalog.addItemToOrder('${inputIdModifier}${fn:escapeXml(product.id)}', '${fn:escapeXml(singleSKU.id)}', '${fn:escapeXml(product.id)}', '${addToOrderMsg}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(isFractional)}', '${addToOrderInvalidQuantityMsg}');" <c:out value="${orderIsModifiableDisableAttribute}"/>/>

</dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductAddAction.jsp#2 $$Change: 1179550 $--%>
