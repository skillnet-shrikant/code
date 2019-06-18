<%--
 In-Store Pickup Shipping Group
 This page displays the in-store pickup shipping group
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/displayInStorePickupShippingGroup.jsp#2 $$Change: 953229 $
 @updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsHighlightedState"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShippingGroupStateDescriptions"/>
  <dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/InstoreShippingDisplayListDefinition" var="instoreShippingDisplayListDefinition"/>
  <dsp:importbean var="shippingGroupFormHandler" bean="atg/commerce/custsvc/order/ShippingGroupFormHandler" />
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
  
  <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
  <dsp:getvalueof var="propertyName" param="propertyName"/>
  <dsp:getvalueof var="displayValue" param="displayValue"/>
  <dsp:getvalueof var="displayHeading" param="displayHeading"/>
  <dsp:getvalueof var="shippingGroupIndex" param="shippingGroupIndex"/>
  <dsp:getvalueof var="displaySelectButton" param="displaySelectButton"/>
  <dsp:getvalueof var="displayAuthorizedForm" param="displayAuthorizedForm"/>
  <dsp:getvalueof var="shortDisplay" param="shortDisplay"/>
  <dsp:getvalueof var="displayAuthorizedReceiver" param="displayAuthorizedReceiver"/>
  <dsp:getvalueof var="displayStatus" param="displayStatus"/>
  
  <c:set var="order" value="${cart.current}" />
  
  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>
  <dsp:droplet name="/atg/commerce/locations/RQLStoreLookupDroplet">
    <dsp:oparam name="output">
      <dsp:getvalueof param="items" var="stores"/>
    </dsp:oparam>
  </dsp:droplet>
  <c:forEach items="${stores}" var="store">
    <dsp:tomap value="${store}" var="store"/>
    <c:if test="${shippingGroup.locationId == store.locationId}">
      <c:set var="currentStore" value="${store}" />
    </c:if>
  </c:forEach>
  <c:set var="selectButtonOk" value="${true}" />
  
  <c:choose>
    <c:when test="${shortDisplay}">
      <fmt:message key="${instoreShippingDisplayListDefinition.intro}" />
      <c:forEach items="${instoreShippingDisplayListDefinition.items}" var="listItem" varStatus="status">
        <c:if test="${!empty currentStore[listItem]}">
          ${currentStore[listItem]}
          <c:if test="${!status.last}">
          ,
          </c:if>
        </c:if>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <c:if test="${displayAuthorizedForm}">
        <div>
          <div>
            <fmt:message key="inStorePickup.shippingMethod.authorizedRecipient" />
          </div>
          <table>
            <tr>
              <td>
                <c:if test="${shippingGroupFormHandler.authorizedRecipientForInStorePickupRequired}">
                  <span class="red">
                    <fmt:message key="inStorePickup.shippingMethod.required" />
                  </span>
                </c:if>
                <fmt:message key="inStorePickup.shippingMethod.firstName" />
              </td>
              <td>
                <dsp:input type="text" bean="ShoppingCart.current.ShippingGroups[param:shippingGroupIndex].firstName" />
              </td>
            </tr>
              <td>
                <c:if test="${shippingGroupFormHandler.authorizedRecipientForInStorePickupRequired}">
                  <span class="red">
                    <fmt:message key="inStorePickup.shippingMethod.required" />
                  </span>
                </c:if>
                <fmt:message key="inStorePickup.shippingMethod.lastName" />
              </td>
              <td>
                <dsp:input type="text" bean="ShoppingCart.current.ShippingGroups[param:shippingGroupIndex].lastName" />
              </td>
            </tr>
          </table>
        </div>
        <div class="atg_commerce_csr_addressView" style="float:left;width:300px;margin-left:30px">
          <fmt:message key="inStorePickup.shippingMethod.pickupLocation" />
      </c:if>
      <div class="atg_commerce_csr_addressView">
        <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
          <li class="atg-csc-base-bold">
            ${currentStore.name}
          </li>
          <li>
            ${currentStore.address1}
          </li>
          <c:if test="${!empty currentStore.address2 }">
            <li>
               ${currentStore.address2}
            </li>
          </c:if>
          <li>
            ${currentStore.city} ${currentStore.stateAddress}, ${currentStore.postalCode}
          </li>
          <li>
            ${currentStore.country}
          </li>
          <li>
            ${currentStore.phoneNumber}
          </li>
          <c:if test="${displaySelectButton != false}">
            <c:set var="canBePickupUpInStore" value="true" />
            <c:forEach items="${order.commerceItems}" var="item" varStatus="vs">
              <dsp:tomap var="sku" value="${item.auxiliaryData.catalogRef}" />
              <dsp:tomap var="product" value="${item.auxiliaryData.productRef}" />
              <c:set var="skuObj" value="${item.auxiliaryData.catalogRef}" />
              <c:set var="productObj" value="${item.auxiliaryData.productRef}" />
              <dsp:droplet name="InventoryLookup">
                <dsp:param name="itemId" value="${sku.id}"/>
                <dsp:param name="useCache" value="true" /> 
                <dsp:param name="locationId" value="${currentStore.locationId}"/>
                <dsp:param name="useCache" value="true"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof param="inventoryInfo" var="inventoryInfo"/>
                  <dsp:getvalueof param="inventoryInfo.stockLevel" var="stockLevel"/>
                  <dsp:droplet name="/atg/commerce/catalog/OnlineOnlyDroplet">
                    <dsp:param name="product" value="${productObj}"/>
                    <dsp:param name="sku" value="${skuObj}"/>
                    <dsp:oparam name="true">
                      <c:set var="canBePickupUpInStore" value="false" />
                    </dsp:oparam>
                  </dsp:droplet> 
                  <c:if test="${empty stockLevel || stockLevel <= 0}">
                    <c:set var="canBePickupUpInStore" value="false" />
                  </c:if>
                </dsp:oparam>
              </dsp:droplet>
            </c:forEach>

            <c:if test="${canBePickupUpInStore}">
              <li class="atg_commerce_csr_shippingControls">
                <fmt:message var="resourceSelect" key="newOrderSingleShipping.button.select"/>
                <input type="button" value="${resourceSelect}" onclick="atg.commerce.csr.order.shipping.shipToAddress('${currentStore.locationId}');return false;">
              </li>
            </c:if>
          </c:if>
        </ul>
      </div>
      <c:if test="${displayAuthorizedReceiver && (!empty shippingGroup.firstName || !empty shippingGroup.lastName)}">
        <br />
        <div class="atg_commerce_csr_addressView">
          <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
            <li style="margin-top: -27px;margin-bottom: 15px;">
              <fmt:message key="shippingSummary.inStorePickup.authorizedReceiver" />
            </li>
            <li>
              ${shippingGroup.firstName} ${shippingGroup.lastName}
            </li>
          </ul>
        </div>
      </c:if>
      <c:if test="${displayStatus}">
        <div class="atg_commerce_csr_addressView">
          <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
            <li style="margin-top: -27px;margin-bottom: 15px;">
              <fmt:message key="shippingSummary.inStorePickup.status" />
            </li>
            <li>
              <fmt:message key="shippingSummary.inStorePickup.goodsAreReady" />
            </li>
          </ul>
        </div>
      </c:if>
    </c:otherwise>
  </c:choose>
  
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/displayInStorePickupShippingGroup.jsp#2 $$Change: 953229 $--%>