<%--
 In-Store Pickup Locations
 This page displays the proximity search inputs
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/shippingLocationsSearchResults.jsp#2 $$Change: 1179550 $
 @updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler" var="storeLocatorFormHandler" />
<dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler"/>
<dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" />
<dsp:importbean bean="/atg/commerce/custsvc/order/ApplicableShippingGroups"/>
<dsp:importbean var="container" bean="/atg/commerce/custsvc/order/ShippingGroupContainerService"/>

<dsp:getvalueof var="productId" param="productId"/>
<dsp:getvalueof var="skuId" param="skuId"/>
<dsp:getvalueof var="quantity" param="quantity"/>

<c:set var="order" value="${cart.current}" />
<dsp:droplet name="ApplicableShippingGroups">
  <dsp:param name="order" value="${order}"/>
  <dsp:param name="sgMapContainer" value="${container}"/>
  <dsp:param name="cisiContainer" value="${container}"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="commonShippingGroupTypes" param="commonShippingGroupTypes"/>
    <dsp:getvalueof var="allShippingGroupTypes" param="allShippingGroupTypes"/>
    <dsp:getvalueof var="currentShippingGroups" param="shippingGroups"/>
  </dsp:oparam>
</dsp:droplet>

<c:choose>
  <c:when test="${!empty storeLocatorFormHandler.locationResults}">
    <c:forEach items="${storeLocatorFormHandler.locationResults}" var="item">
      <dsp:tomap value="${item}" var="item"/>
      <c:set var="storeAdded" value="${false}" />
      <c:set var="cssClass" value="" />
      <c:forEach items="${currentShippingGroups}" var="shippingGroup" varStatus="shippingGroupIndex">
        <c:set var="shippingGroupObject" value="${shippingGroup.value}" />
        <c:if test="${shippingGroupObject.shippingGroupClassType == 'inStorePickupShippingGroup' && shippingGroupObject.locationId == item.locationId}">
          <c:set var="storeAdded" value="${true}" />
          <c:set var="cssClass" value="gray" />
        </c:if>
      </c:forEach>
      <div class="atg_commerce_csr_addressView">
        <ul class="atg_svc_shipAddress addressSelect ${cssClass}" id="shipping_atg_commerce_csr_neworder_ShippingAddressHome">
          <li class="atg-csc-base-bold">
            <c:out value="${item.name}" />
          </li>
          <li>
            <c:out value="${item.address1}" />
          </li>
          <c:if test="${!empty item.address2}">
            <li>
              ${item.address2}
            </li>
          </c:if>
          <li>
            <c:out value="${item.city}" /> <c:out value="${item.stateAddress}" />, <c:out value="${item.postalCode}" />
          </li>
          <li>
            <c:out value="${item.country}" />
          </li>
          <li>
            <c:out value="${item.phoneNumber}" />
          </li>
          <c:choose>
            <c:when test="${storeAdded}">
              <fmt:message var="resourceAdded" key="newOrderSingleShipping.button.added"/>
              <li class="atg_commerce_csr_shippingControls">
                <input type="button" value="${resourceAdded}" name="shipToButton" onclick="atg.commerce.csr.catalog.createInStorePickupShippingGroupForm('${item.locationId}');" disabled="disabled"/>
              </li>
            </c:when>
            <c:otherwise>
              <fmt:message var="resourceAddStore" key="newOrderSingleShipping.button.addStore"/>
              <li class="atg_commerce_csr_shippingControls">
                <input type="button" value="${resourceAddStore}" name="shipToButton" onclick="atg.commerce.csr.catalog.createInStorePickupShippingGroupForm('${item.locationId}');"/>
              </li>
            </c:otherwise>
          </c:choose>
        </ul>
      </div>  
    </c:forEach>
    </table>
    <c:set target="${storeLocatorFormHandler}" property="latitude" value="0"/>
    <c:set target="${storeLocatorFormHandler}" property="longitude" value="0"/>
    <c:set target="${storeLocatorFormHandler}" property="city" value="${null}"/>
  </c:when>
  <c:otherwise>
    <b><fmt:message key="catalogBrowse.inStorePickup.searchResults.noStoresFound" /></b>
    <br /><fmt:message key="catalogBrowse.inStorePickup.searchResults.modifyYourSearch" />
  </c:otherwise>
</c:choose>

</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/shippingLocationsSearchResults.jsp#2 $$Change: 1179550 $--%>