<%--
 In-Store Pickup Locations
 This popup displays a list of in-store pickup locations, based on a proximity search
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/pickupLocations.jsp#3 $$Change: 1179550 $
 @updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="skuId" param="skuId"/>
  <dsp:getvalueof var="quantity" param="quantity"/>
  
  <dsp:importbean bean="/atg/commerce/locations/RQLStoreLookupDroplet"/>
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler"/>
  
  <dsp:include src="/include/catalog/pickupLocations.jsp" otherContext="${CSRConfigurator.contextRoot}">
    <dsp:param name="productId" value="${productId}" />
    <dsp:param name="skuId" value="${skuId}" />
    <dsp:param name="quantity" value="${quantity}" />
  </dsp:include>

  <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
    <dsp:param name="product" value="${productId}"/>
    <dsp:param name="sku" value="${skuId}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="isFractional" param="fractional"/>
    </dsp:oparam>
  </dsp:droplet>

  <c:set var="calcQuantity" value="${quantity}"/>
  <c:set var="calcQuantityWithFraction" value="0.0"/>
  <c:if test="${isFractional}">
    <c:set var="calcQuantity" value="0"/>
    <c:set var="calcQuantityWithFraction" value="${quantity}"/>
  </c:if>

  <svc-ui:frameworkUrl var="url" splitChar="|"  panelStacks="${renderInfo.pageOptions.successPanelStacks}" /> 
  <dsp:form method="post" id="inStorePickupForm" name="inStorePickupForm">
    <dsp:input type="hidden" value="${url}" bean="CartModifierFormHandler.addItemToOrderSuccessURL" />
    <dsp:input type="hidden" value="${url}" bean="CartModifierFormHandler.addItemToOrderErrorURL" />
    <dsp:input type="hidden" value="${productId}" name="productId" bean="CartModifierFormHandler.productId" />
    <dsp:input type="hidden" value="${skuId}" name="skuId" bean="CartModifierFormHandler.catalogRefIds" />
    <dsp:input type="hidden" value="" name="locationId" bean="CartModifierFormHandler.locationId"/>
    <dsp:input type="hidden" value="${calcQuantity}" name="quantity" bean="CartModifierFormHandler.quantity"/>
    <dsp:input type="hidden" value="${calcQuantityWithFraction}" name="quantityWithFraction" bean="CartModifierFormHandler.quantityWithFraction"/>
    <dsp:input type="hidden" value="dummy" priority="-100" bean="CartModifierFormHandler.addItemToOrder"/>
    <input id="atg.successMessage" name="atg.successMessage" type="hidden" value=""/> 
  </dsp:form>
  <div id="inStorePickupResults">
  </div>

</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/pickupLocations.jsp#3 $$Change: 1179550 $--%>