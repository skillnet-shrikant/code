<%--
 This page fragment renders the quanity box for the product add to cart on the catalog display 

 @param product - The product
 @param inputIdModifier - The value is prepended to the id of the input tag used to capture the quantity. This value is optional to distinguish the same product input appearing on multiple
 areas of the display. otherwise, only the product id is prepended to make it unique. 
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductAddQuantity.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:getvalueof param="product" var="product"/>
<dsp:getvalueof param="inputIdModifier" var="inputIdModifier"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="shoppingCart"/>

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

<dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
  <dsp:param name="product" value="${product.id}"/>
  <dsp:param name="sku" value="${singleSKU.id}"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="isFractional" param="fractional"/>
  </dsp:oparam>
</dsp:droplet>

  <c:set var="maxLength" value="5"/>
  <c:set var="size" value="5"/>
  <c:if test="${isFractional eq true}">
    <c:set var="maxLength" value="9"/>
  </c:if>

<input maxlength="${maxLength}" size="${size}" type="text" onkeyup="if (event.keyCode==13){dojo.byId('itemQuantity${inputIdModifier}${fn:escapeXml(product.id)}_button').click();}" id="itemQuantity${inputIdModifier}${fn:escapeXml(product.id)}" <c:out value="${orderIsModifiableDisableAttribute}"/>/>


</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displayProductAddQuantity.jsp#2 $$Change: 1179550 $--%>
