<%-- In-Store Pickup Locations This popup displays a list of in-store pickup locations, based on a proximity search @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/shippingPickupLocations.jsp#2 $$Change: 953229 $ @updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $--%><%@ include file="/include/top.jspf" %><dsp:page xml="true"><dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">  <dsp:getvalueof var="productId" param="productId"/>  <dsp:getvalueof var="skuId" param="skuId"/>  <dsp:getvalueof var="quantity" param="quantity"/>    <dsp:importbean bean="/atg/commerce/locations/RQLStoreLookupDroplet"/>  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>  <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler"/>    <dsp:include src="/include/catalog/shippingPickupLocations.jsp" otherContext="${CSRConfigurator.contextRoot}">    <dsp:param name="productId" value="${productId}" />    <dsp:param name="skuId" value="${skuId}" />    <dsp:param name="quantity" value="${quantity}" />  </dsp:include>    <div id="storesSearchResults">  </div></dsp:layeredBundle></dsp:page><%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/shippingPickupLocations.jsp#2 $$Change: 953229 $--%>