<%--
 In-Store Pickup Locations
 This page displays the proximity search inputs
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/pickupLocationsResults.jsp#3 $$Change: 1179550 $
 @updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler" var="storeLocatorFormHandler" />
<dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler"/>
<dsp:importbean bean="/atg/commerce/inventory/GetInventoryInfoStockLevelDroplet"/>

<dsp:getvalueof var="productId" param="productId"/>
<dsp:getvalueof var="skuId" param="skuId"/>
<dsp:getvalueof var="quantity" param="quantity"/>
<dsp:getvalueof var="allItems" param="allItems"/>

<c:choose>
  <c:when test="${!empty storeLocatorFormHandler.locationResults}">
    <table border="0" cellpadding="0" cellspacing="0" class="atg-csc-in-store-locations-container">
      <thead>
        <tr style="background-color:#EFEFEF">
          <th colspan="2" align="right" class="atg-csc-in-store-locations-th1"><h3>${skuId}</h3></th>
          <th colspan="2" align="left" class="atg-csc-in-store-locations-th2"><img src="${CSRConfigurator.contextRoot}/images/icons/qty-left.png"><span class="atg-csc-in-store-locations-quantity-span">${quantity}</span><img src="${CSRConfigurator.contextRoot}/images/icons/qty-right.png"> <fmt:message key='productViewRenderer.numberRequested' /></th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${storeLocatorFormHandler.locationResults}" var="item" varStatus="status">
          <c:if test="${allItems || (!allItems && status.index < 10)}">
          <dsp:tomap value="${item}" var="item"/>
          <tr>
            <td valign="top" class="atg-csc-in-store-locations-distance">
              <c:out value="${item.distance}" />
            </td>
            <td class="atg-csc-in-store-locations-td">
              <span style="font-weight:bold">
                <c:out value="${item.name}" />
              </span>
              <br/>
              <c:out value="${item.address1}" />
              <br/>
              <c:if test="${!empty item.address2}">
                <c:out value="${item.address2}" />
                <br/>
              </c:if>
              <c:out value="${item.city}" /> <c:out value="${item.stateAddress}" />, <c:out value="${item.postalCode}" />
              <br/>
              <c:out value="${item.phoneNumber}" />
            </td>
            <td class="atg-csc-in-store-locations-td2">
              <dsp:droplet name="InventoryLookup">
                <dsp:param name="itemId" value="${skuId}"/>
                <dsp:param name="useCache" value="true" /> 
                <dsp:param name="locationId" value="${item.repositoryId}"/>
                <dsp:param name="useCache" value="true"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof param="inventoryInfo" var="inventoryInfo"/>
                  <dsp:droplet name="GetInventoryInfoStockLevelDroplet">
                    <dsp:param name="item" param="inventoryInfo"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="stockLevel" param="stockLevel"/>
                    </dsp:oparam>
                  </dsp:droplet>
                  <c:set var="buttonDisabled" value="" />
                    <c:choose>
                      <c:when test="${stockLevel == 0}">
                        <img src="${CSRConfigurator.contextRoot}/images/icons/qty-left.png" /><span class="atg-csc-in-store-locations-quantity-span">
                        0
                        </span><img src="${CSRConfigurator.contextRoot}/images/icons/qty-right.png" />
                        <c:set var="buttonDisabled" value="disabled='disabled'" />
                      </c:when>
                      <c:when test="${stockLevel > 0}">
                        <img src="${CSRConfigurator.contextRoot}/images/icons/qty-left.png" /><span class="atg-csc-in-store-locations-quantity-span">
                        ${stockLevel}
                        </span><img src="${CSRConfigurator.contextRoot}/images/icons/qty-right.png" />
                      </c:when>
                      <c:otherwise>
                        <c:set var="buttonDisabled" value="disabled='disabled'" />
                      </c:otherwise>
                    </c:choose>
                  <dsp:droplet name="Switch">
                    <dsp:param name="value" param="inventoryInfo.availabilityStatus"/>
                    <dsp:oparam name="1001">
                      <c:choose>
                        <c:when test="${stockLevel != 0}">
                          <fmt:message key="global.product.availabilityStatus.outOfStock" />
                        </c:when>
                        <c:otherwise>
                          <fmt:message key="global.product.availabilityStatus.inStock" />
                        </c:otherwise>
                      </c:choose>
                    </dsp:oparam>
                    <dsp:oparam name="1002">
                      <fmt:message key="global.product.availabilityStatus.preorder" />
                    </dsp:oparam>
                    <dsp:oparam name="1003">
                      <fmt:message key="global.product.availabilityStatus.backorder" />
                    </dsp:oparam>
                    <dsp:oparam name="1000">
                      <c:if test="${stockLevel > 0}">
                        <fmt:message key="global.product.availabilityStatus.inStock"/>
                      </c:if>
                    </dsp:oparam>
                  </dsp:droplet>
                  <dsp:droplet name="IsNull">
                    <dsp:param name="value" param="inventoryInfo.availableToPromise"/>
                    <dsp:oparam name="false">
                      <dsp:droplet name="ForEach">
                        <dsp:param name="array" param="inventoryInfo.availableToPromise.availabilityDates" />
                        <dsp:param name="elementName" value="atp" />
                        <dsp:oparam name="output">
                          <br />+
                          <dsp:valueof param="atp.quantity">0</dsp:valueof>
                           <fmt:message key="global.product.availabilityStatus.atp.on" /> <dsp:valueof param="atp.date" date="MM/dd/yy"><fmt:message key="global.product.availabilityStatus.atp.noDate" /></dsp:valueof>
                        </dsp:oparam>
                      </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>
                  
                </dsp:oparam>
              </dsp:droplet>
            </td>
            <dsp:droplet name="/atg/commerce/custsvc/catalog/SKULookup">
              <dsp:param name="id" value="${skuId}"/>
              <dsp:oparam name="output">
               <dsp:getvalueof var="sku" param="element"/>
               <dsp:getvalueof var="displayName" param="element.displayName"/>
              </dsp:oparam>
            </dsp:droplet>
            <td align="right" class="atg-csc-in-store-locations-button-container">
              <c:choose>
                <c:when test="${stockLevel} < ${quantity}">
                  <fmt:message key="catalogBrowse.searchResults.productNotAddedToOrder.js" var="notAddToOrderMsg"><fmt:param value="${quantity - stockLevel} - ${fn:escapeXml(displayName)}(${skuId})"/></fmt:message>
                  <c:set var="infoMessage" value="${notAddToOrderMsg}" />
                  <fmt:message key="catalogBrowse.searchResults.productAddedToOrder.js" var="addToOrderMsg"><fmt:param value="${stockLevel} - ${fn:escapeXml(displayName)}(${skuId})"/></fmt:message>
                </c:when>
                <c:otherwise>
                  <c:set var="infoMessage" value="" />
                  <fmt:message key="catalogBrowse.searchResults.productAddedToOrder.js" var="addToOrderMsg"><fmt:param value="${quantity} - ${fn:escapeXml(displayName)}(${skuId})"/></fmt:message>
                </c:otherwise>
              </c:choose>
              
              <input type="button" value="<fmt:message key='productViewRenderer.addToCart' />" onclick="atg.commerce.csr.catalog.pickupInStoreAddToCart('${item.repositoryId}', '${addToOrderMsg}', '${infoMessage}', ${stockLevel});return false;" ${buttonDisabled} />
            </td>
          </tr>
          </c:if>
          <c:if test="${!allItems && status.index == 10}">
            <tr id="loadMoreStoresButton" onclick="atg.commerce.csr.catalog.pickupInStoreSearchStores('${productId}', '${skuId}', ${quantity}, true)">
              <td colspan="4">
                <fmt:message key="catalogBrowse.inStorePickup.loadMore" />
              </td>
            </tr>
          </c:if>
        </c:forEach>
      </tbody>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/pickupLocationsResults.jsp#3 $$Change: 1179550 $--%>