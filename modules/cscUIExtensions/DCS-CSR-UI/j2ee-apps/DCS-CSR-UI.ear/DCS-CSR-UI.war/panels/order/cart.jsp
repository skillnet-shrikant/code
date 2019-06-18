<%--
 This page defines the shopping cart panel

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/cart.jsp#3 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf"%>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:importbean var="changeGiftLinkFragment" bean="/atg/commerce/custsvc/ui/fragments/gwp/ChangeGiftLinkPageFragment" />
      <dsp:importbean var="selectGiftLinkFragment" bean="/atg/commerce/custsvc/ui/fragments/gwp/SelectGiftLinkPageFragment" />
      <dsp:importbean bean="/atg/commerce/custsvc/promotion/CouponFormHandler" />
      <dsp:importbean bean="/atg/svc/droplet/FrameworkUrlDroplet" />
      <dsp:importbean bean="/atg/dynamo/droplet/PossibleValues" />
      <dsp:importbean bean="/atg/svc/security/droplet/HasAccessRight" />
      <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler" />
      <dsp:importbean var="mafh" bean="/atg/commerce/custsvc/order/ManualAdjustmentsFormHandler" />
      <dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" />
      <dsp:importbean bean="/atg/commerce/multisite/SiteIdForCatalogItem" />
      <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator" />
      <dsp:importbean bean="/atg/commerce/custsvc/util/CSRAgentTools" var="agentTools" />
      <dsp:importbean bean="/atg/commerce/custsvc/order/GetTotalAppeasementsForOrderDroplet" />
      <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet"/>
      <dsp:importbean bean="/atg/commerce/custsvc/ui/tables/promotion/AvailablePromotionsGrid" var="walletGridConfig"/>
      <dsp:importbean bean="/atg/commerce/locations/RQLStoreLookupDroplet"/>
      <dsp:importbean bean="/atg/commerce/custsvc/order/HardgoodShippingDisplayListDefinition" var="hardgoodShippingDisplayListDefinition"/>
      <dsp:importbean bean="/atg/commerce/custsvc/order/InstoreShippingDisplayListDefinition" var="instoreShippingDisplayListDefinition"/>
      <dsp:importbean bean="/atg/commerce/custsvc/order/ElectronicShippingDisplayListDefinition" var="electronicShippingDisplayListDefinition"/>
      <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
      <dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>
  	  <dsp:importbean bean="/atg/commerce/custsvc/order/IsHighlightedState"/>
  	  <dsp:importbean bean="/atg/commerce/custsvc/order/CommerceItemStateDescriptions"/>
  	  <dsp:importbean bean="/atg/commerce/pricing/calculators/ShippingUpChargeCalculator" />
  	  <dsp:getvalueof var="signatureRequiredFee" bean="ShippingUpChargeCalculator.upCharges.SignatureRequired" />
      <dsp:getvalueof var="isActiveOrderOwnerChangeble" bean="/atg/commerce/custsvc/environment/CSREnvironmentTools.activeOrderOwnerChangeble" />
	  <dsp:getvalueof var="customerProfile" bean="/atg/userprofiling/ActiveCustomerProfile"/>
	  <dsp:tomap var="customerProfileMap" value="${customerProfile}"/>
  	  <dsp:getvalueof var="isProfileTransient" value="${customerProfile['transient']}"/>

      <dsp:importbean bean="/atg/multisite/Site" />
      <dsp:getvalueof var="currentSiteId" bean="Site.id" />

      <script type="text/javascript">
        dojo.require("dijit.form.NumberTextBox");
      </script>

      <c:set var="fractionalUnitDecimalPlaces"  value="${CSRConfigurator.fractionalUnitsConfiguration.numberOfDecimalPlaces}" />
      <c:set var="fractionalUnitPattern"        value="${CSRConfigurator.fractionalUnitsConfiguration.validationPattern}" />
      <c:set var="fractionalValidationMessage"  value="${CSRConfigurator.fractionalUnitsConfiguration.invalidMessage}" />
	  <c:set var="signatureRequired" value="0" />
	  
      <%-- Select Customer Panel --%>
      <c:if test="${isActiveOrderOwnerChangeble}">
        <div class="atg_commerce_csr_cartSelectCustomerPanel" style="background-color:#F5F5F5;margin-bottom:4px;padding-bottom:0px"><dsp:include src="/panels/order/customer.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="order" value="${order}" />
        </dsp:include></div>
      </c:if>

      <c:set var="order" value="${cart.current}" />
      <dsp:droplet name="HasAccessRight">
        <dsp:param name="accessRight" value="commerce-custsvc-adjust-price-privilege" />
        <dsp:oparam name="accessGranted">
          <c:set var="adjustPricePriv" value="true" />
        </dsp:oparam>
        <dsp:oparam name="accessDenied">
          <c:set var="adjustPricePriv" value="false" />
        </dsp:oparam>
      </dsp:droplet>
      <dsp:droplet name="FrameworkUrlDroplet">
        <dsp:param name="panelStacks" value="cmcShoppingCartPS" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="thisURL" bean="FrameworkUrlDroplet.url" />
        </dsp:oparam>
      </dsp:droplet>

      <csr:getCurrencyCode order="${order}">
        <c:set var="currencyCode" value="${currencyCode}" scope="request" />
      </csr:getCurrencyCode>


      <c:choose>
        <c:when test="${empty order.commerceItems}">
          <div id="atg_commerce_csr_emptyCartMessage"><fmt:message key="cart.emptyCart" /><a href="#" onclick="atg.commerce.csr.openPanelStack('cmcCatalogPS');return false;"><fmt:message key="cart.emptyCartLink" /></a><br>
          <dsp:include src="${selectGiftLinkFragment.URL}" otherContext="${selectGiftLinkFragment.servletContext}">
          <dsp:param name="order" value="${order}" />
          </dsp:include>
          </div>
        </c:when>
        <c:otherwise>
          <script type="text/javascript">
        if (!dijit.byId("atg_commerce_csr_catalog_productQuickViewPopup")) {
          new dojox.Dialog({ id: "atg_commerce_csr_catalog_productQuickViewPopup",
                             cacheContent: "false",
                             executeScripts: "true",
                             scriptHasHooks: "true",
                             duration: 100,
                             "class": "atg_commerce_csr_popup"});
        }
      </script>

          <dsp:form method="post" id="itemsForm" formid="itemsForm">
            <svc-ui:frameworkUrl var="mtpiSuccessURL" panelStacks="cmcShippingAddressPS" init="true" />
            <dsp:input type="hidden" value="false" id="persistOrder" bean="CartModifierFormHandler.persistOrder" />
            <dsp:input type="hidden" value="${mtpiSuccessURL}" bean="CartModifierFormHandler.moveToPurchaseInfoSuccessURL" />
            <dsp:input type="hidden" value="${thisURL}" bean="CartModifierFormHandler.moveToPurchaseInfoErrorURL" />
            <dsp:input type="hidden" value="${mtpiSuccessURL}" bean="CartModifierFormHandler.moveToPurchaseInfoByRelIdSuccessURL" />
            <dsp:input type="hidden" value="${thisURL}" bean="CartModifierFormHandler.moveToPurchaseInfoByRelIdErrorURL" />
            <table class="atg_dataTable atg_commerce_csr_innerTable" border="0" cellspacing="0" cellpadding="0" summary="item details">
              <thead>
                <tr>
                  <%-- Site Icon Heading --%>
                  <c:if test="${isMultiSiteEnabled == true}">
                    <th class="atg_commerce_csr_siteIcon"></th>
                  </c:if>
                  <th><fmt:message key="cart.items.itemDescription" /></th>
                  <th><fmt:message key="cart.items.inventoryStatus" /></th>
                  <th><fmt:message key="cart.items.qty" /></th>
                  <th class="atg_numberValue"><fmt:message key="cart.items.priceEach" /></th>
                  <th class="atg_numberValue atg_commerce_csr_totalPrice"><fmt:message key="cart.items.totalPrice" /></th>
                  <c:if test="${adjustPricePriv}">
                    <th class="atg_commerce_csr_finalPrice atg_numberValue"><fmt:message key="cart.items.finalPrice" /></th>
                  </c:if>
                  <th abbr="edit" scope="down"></th>
                  <th abbr="remove" scope="down"></th>
                </tr>
              </thead>
              <tbody>
                <c:set var="shippingGroupCount" value="0" />
                  <c:forEach items="${order.shippingGroups}" var="shippingGroup" varStatus="shippingGroupIndex">
                    <c:set var="shippingGroupCount" value="${shippingGroupCount + 1}" />
                  </c:forEach>
                  <c:choose>
                  <c:when test="${shippingGroupCount > 1}">
                    <dsp:droplet name="/atg/commerce/locations/RQLStoreLookupDroplet">
                      <dsp:oparam name="output">
                        <dsp:getvalueof param="items" var="stores"/>
                      </dsp:oparam>
                    </dsp:droplet>
                    <c:forEach items="${order.commerceItems}" var="item" varStatus="vs">
                      <dsp:tomap var="sku" value="${item.auxiliaryData.catalogRef}" />
                      <dsp:tomap var="product" value="${item.auxiliaryData.productRef}" />

                      <dsp:droplet name="/atg/commerce/custsvc/order/GetCommerceItemQuantityDroplet">
                        <dsp:param name="item" value="${item}"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="itemQuantity" param="quantity"/>
                        </dsp:oparam>
                      </dsp:droplet>

                      <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
                        <dsp:param name="item" value="${item}"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="isItemFractional" param="fractional"/>
                        </dsp:oparam>
                      </dsp:droplet>

                      <tr style="height:20px;">
                        <td colspan="9"></td>
                      </tr>
                      <tr class="${((vs.index % 2)==0) ? '' : 'atg_dataTable_altRow'}">
                        <c:if test="${isMultiSiteEnabled == true}">
                          <c:set var="siteId" value="${item.auxiliaryData.siteId}" />
                          <td class="atg_commerce_csr_siteIcon"><csr:siteIcon siteId="${siteId}" /></td>
                        </c:if>

                        <%-- Item Description --%>
                        <td>
                          <c:set var="cartShareableSite" value="${true}" />
                          <dsp:droplet name="/atg/dynamo/droplet/multisite/SitesShareShareableDroplet">
                            <dsp:param name="siteId" value="${currentSiteId}" />
                            <dsp:param name="otherSiteId" value="${siteId}" />
                            <dsp:param name="shareableTypeId" value="${CSRConfigurator.cartShareableTypeId}" />
                            <dsp:oparam name="false">
                              <c:set var="cartShareableSite" value="${false}" />
                            </dsp:oparam>
                           </dsp:droplet>

                        <ul class="atg_commerce_csr_itemDesc">
                          <li>
                          <%-- If the current site and item site do not share shopping cart, then display the item Description without a link --%>
                          <c:choose>
                            <c:when test="${cartShareableSite == false}">
                              ${fn:escapeXml(sku.displayName)}
                            </c:when>
                            <c:when test="${(isMultiSiteEnabled == true) && (isSiteDeleted != true)}">
                              <a onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(currentSiteId)}');
                                    return false;" href="#">${fn:escapeXml(sku.displayName)}</a>
                            </c:when>
                            <c:otherwise>
                              <a onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}');
                                    return false;" href="#">${fn:escapeXml(sku.displayName)}</a>
                            </c:otherwise>
                          </c:choose>

                          <dsp:include src="${changeGiftLinkFragment.URL}" otherContext="${changeGiftLinkFragment.servletContext}">
                            <dsp:param name="item" value="${item}" />
                            <dsp:param name="order" value="${order}" />
                          </dsp:include>

                          </li>
                          <li>${fn:escapeXml(item.catalogRefId)}</li>
                          
                          <c:if test="${not empty product.fulfillmentMethod && product.fulfillmentMethod == 7}">
							<li><span style="font-weight:bold"><font  color="0000FF">BOPIS ONLY FULFILLMENT</font></span></li>
			              </c:if>
                          
                           <c:if test="${not empty product.minimumAge }">
                           	<c:set var="signatureRequired" value="1" />
							<li><<span style="font-weight:bold">font  color="FF0000">YOU MUST BE AT LEAST ${product.minimumAge} YEARS OLD TO PURCHASE THIS ITEM.</font></span></li>
			              </c:if>
                        </ul>
                        </td>
                        <%-- Inventory status --%>
                        <td class="atg_commerce_csr_inventoryStatus"></td>
                        <%-- Quantity --%>
                        <td class="atg_commerce_csr_quatity">
                          <web-ui:formatNumber value="${itemQuantity}"/>
                        </td>
                        <%-- Price Each --%>
                        <td class="atg_numberValue atg_commerce_csr_priceEach">
				  <c:if test="${item.priceInfo.salePrice < item.priceInfo.listPrice}">
					  <span class="atg_commerce_csr_common_content_strikethrough">
					    <csr:formatNumber value="${item.priceInfo.listPrice}" type="currency" currencyCode="${currencyCode}" />
					  </span>
					  <csr:formatNumber value="${item.priceInfo.salePrice}" type="currency" currencyCode="${currencyCode}" />
				  </c:if>
				  <c:if test="${item.priceInfo.salePrice == item.priceInfo.listPrice}">
					  <csr:formatNumber value="${item.priceInfo.listPrice}" type="currency" currencyCode="${currencyCode}" />
 				  </c:if>
 			</td>
                        <%-- Total Price --%>
                        <td class="atg_numberValue atg_commerce_csr_totalPrice"><c:set var="pi" value="${item.priceInfo}" />
                          <csr:formatNumber value="${pi.amount}" type="currency" currencyCode="${currencyCode}" /></td>
                        <%-- Final Price (price override) --%>
                        <c:if test="${adjustPricePriv}">
                          <td class="atg_numberValue atg_commerce_csr_finalPrice">
                            <csr:formatNumber var="finalAmount" value="${item.priceInfo.amountIsFinal ? item.priceInfo.amount : '' }" type="currency" currencyCode="${currencyCode}" />
                            <input type="text" size="10" maxlength="12" class="atg_numberValue" id="IPO:${item.id}" name="IPO:${item.id}" value="${finalAmount}" /></td>
                        </c:if>
                        <%-- Edit line item --%>

                        <dsp:droplet name="/atg/commerce/gifts/GiftlistShoppingCartQuantityDroplet">
                          <dsp:param name="commerceItemId" value="${item.id}" />
                          <dsp:param name="order" value="${order}" />
                          <dsp:oparam name="empty">
                            <td class="atg_iconCell"><dsp:tomap var="product" value="${item.auxiliaryData.productRef}" /> <c:if test="${fn:length(product.childSKUs) > 1}">
                              <svc-ui:frameworkPopupUrl var="popupUrl" value="/include/order/editProductSKU.jsp" context="${CSRConfigurator.contextRoot}" windowId="${windowId}" mode="apply" commerceItemId="${item.id}" />
                              <a id="edit_${sku.id}" class="atg_tableIcon atg_propertyEdit" title="<fmt:message key='cart.items.edit'/>" href="#"
                                onclick="atg.commerce.csr.common.showPopupWithReturn({
                                  popupPaneId: 'atg_commerce_csr_catalog_productQuickViewPopup',
                                  title: '${fn:escapeXml(sku.displayName)}',
                                  url: '${popupUrl}',
                                  onClose: function( args ) {
                                    if ( args.result == 'ok' ) {
                                      atgSubmitAction({
                                        panels : ['cmcShoppingCartP'],
                                        panelStack : 'cmcShoppingCartPS',
                                        form : document.getElementById('transformForm')
                                      });
                                    }
                                  }});return false;">
                              <fmt:message key="cart.items.edit" /> </a>
                            </c:if></td>
                          </dsp:oparam>
                          <dsp:oparam name="output">
                            <td class="atg_iconCell"></td>
                          </dsp:oparam>
                        </dsp:droplet>

                        <%-- Delete line item --%>
                        <td class="atg_iconCell">
                          <a class="atg_tableIcon atg_propertyClear" title="<fmt:message key='cart.items.delete'/>" href="#" onclick="atg.commerce.csr.cart.deleteCartItem('${item.id}');return false;"> <fmt:message
                          key="cart.items.delete" /></a>
                        </td>
                      </tr>
                      <c:forEach items="${order.shippingGroups}" var="shippingGroup" varStatus="shippingGroupIndex">
                        <c:forEach items="${shippingGroup.commerceItemRelationships}" var="ciRelationship">
                          <dsp:droplet name="/atg/commerce/custsvc/order/GetCommerceItemRelationshipQuantityDroplet">
                            <dsp:param name="itemRelationship" value="${ciRelationship}"/>
                            <dsp:oparam name="output">
                              <dsp:getvalueof var="ciRelationshipQuantity" param="quantity"/>
                            </dsp:oparam>
                          </dsp:droplet>
                          <c:choose>
                            <c:when test="${sku.id == ciRelationship.commerceItem.auxiliaryData.catalogRef.repositoryId}">
                              <tr>
                                <c:if test="${isMultiSiteEnabled == true}">
                                 <td></td>
                                 <td>
                                </c:if>
                                <c:if test="${isMultiSiteEnabled == false}">
                                  <td>
                                </c:if>
                                <c:choose>
                                  <c:when test="${shippingGroup.shippingGroupClassType == 'hardgoodShippingGroup'}">
                                    <span class="atg-csc-base-bold">
                                      <c:choose>
                                        <c:when test="${!empty hardgoodShippingDisplayListDefinition.intro}">
                                          <fmt:message key="${hardgoodShippingDisplayListDefinition.intro}" var="hardgoodShippingIntro"/>
                                        </c:when>
                                        <c:otherwise>
                                          <fmt:message key="inStorePickup.shippingGroup.deliver" var="hardgoodShippingIntro"/>
                                        </c:otherwise>
                                      </c:choose>
                                      ${hardgoodShippingIntro}
                                    </span>
                                    <c:if test="${!empty shippingGroup.shippingAddress}">
                                      <span id="address_${ciRelationship.id}">
                                        <c:set var="showTooltip" value="${false}" />
                                        <c:forEach items="${hardgoodShippingDisplayListDefinition.items}" var="listItem" varStatus="status">
                                          <c:if test="${!empty shippingGroup.shippingAddress[listItem]}">
                                            <c:set var="showTooltip" value="${true}" />
                                            ${shippingGroup.shippingAddress[listItem]}
                                            <c:if test="${!status.last}">
                                            ,
                                            </c:if>
                                          </c:if>
                                        </c:forEach>
                                        <c:if test="${showTooltip}">
                                          <span class="atg-csc-in-store-more"></span>
                                        </c:if>
                                      </span>
                                      <c:if test="${showTooltip}">
                                        <div dojoType="dijit.Tooltip" connectId="address_${ciRelationship.id}">
                                          ${shippingGroup.shippingAddress.address1}
                                          <br />
                                          <c:if test="${!empty shippingGroup.shippingAddress.address2}">
                                            ${shippingGroup.shippingAddress.address2}
                                            <br />
                                          </c:if>
                                          ${shippingGroup.shippingAddress.city}
                                          <c:if test="${!empty shippingGroup.shippingAddress.state}">
                                            , ${shippingGroup.shippingAddress.state}
                                          </c:if>
                                          <br />
                                          ${shippingGroup.shippingAddress.country}
                                          <br />
                                          ${shippingGroup.shippingAddress.phoneNumber}
                                        </div>
                                      </c:if>
                                    </c:if>
                                    <br />
                                  </c:when>
                                  <c:when test="${shippingGroup.shippingGroupClassType == 'inStorePickupShippingGroup'}">
                                    <style>
                                      #edit_${sku.id} {
                                        display:none;
                                      }
                                    </style>

                                    <span class="atg-csc-base-bold">
                                      <c:choose>
                                        <c:when test="${!empty instoreShippingDisplayListDefinition.intro}">
                                          <fmt:message key="${instoreShippingDisplayListDefinition.intro}" var="instoreShippingIntro"/>
                                        </c:when>
                                        <c:otherwise>
                                          <fmt:message key="inStorePickup.shippingGroup.deliver" var="instoreShippingIntro"/>
                                        </c:otherwise>
                                      </c:choose>
                                      ${instoreShippingIntro}
                                    </span>
                                    <c:forEach items="${stores}" var="store">
                                      <dsp:tomap value="${store}" var="store"/>
                                      <c:if test="${shippingGroup.locationId == store.locationId}">
                                        <span id="store_${ciRelationship.id}">
                                          <c:forEach items="${instoreShippingDisplayListDefinition.items}" var="listItem" varStatus="status">
                                            <c:if test="${!empty store[listItem]}">
                                              ${store[listItem]}
                                              <c:if test="${!status.last}">
                                              ,
                                              </c:if>
                                            </c:if>
                                          </c:forEach>
                                          <span class="atg-csc-in-store-more"></span>
                                        </span>
                                        <div dojoType="dijit.Tooltip" connectId="store_${ciRelationship.id}">
                                          <b>${store.name}</b>
                                          <br />
                                          ${store.address1}
                                          <br />
                                          <c:if test="${!empty store.address2}">
                                            ${store.address2}
                                            <br />
                                          </c:if>
                                          ${store.city} ${store.stateAddress}, ${store.postalCode}
                                          <br />
                                          ${store.country}
                                          <br />
                                          ${store.phoneNumber}
                                        </div>
                                      </c:if>
                                    </c:forEach>
                                    <br />
                                  </c:when>
                                  <c:when test="${shippingGroup.shippingGroupClassType == 'electronicShippingGroup'}">
                                    <span class="atg-csc-base-bold">
                                      <c:choose>
                                        <c:when test="${!empty electronicShippingDisplayListDefinition.intro}">
                                          <fmt:message key="${electronicShippingDisplayListDefinition.intro}" var="electronicShippingIntro"/>
                                        </c:when>
                                        <c:otherwise>
                                          <fmt:message key="inStorePickup.shippingGroup.deliver" var="electronicShippingIntro"/>
                                        </c:otherwise>
                                      </c:choose>
                                      ${electronicShippingIntro}
                                    </span>
                                    <c:forEach items="${electronicShippingDisplayListDefinition.items}" var="listItem" varStatus="status">
                                      <c:if test="${!empty shippingGroup[listItem]}">
                                        ${shippingGroup[listItem]}
                                        <c:if test="${!status.last}">
                                        ,
                                        </c:if>
                                      </c:if>
                                    </c:forEach>
                                    <br />
                                  </c:when>
                                </c:choose>
                                </td>
                                <td><csr:inventoryStatus commerceItemId="${item.catalogRefId}" /></td>
                                <td class="atg_commerce_csr_quatity">
                                  <input value="${ciRelationshipQuantity}" type="text" size="5" maxlength="5" name="${item.id}" id="${ciRelationship.id}" class="ciRelationship" />
                                  <input value="${ciRelationshipQuantity}" type="hidden" id="${ciRelationship.id}_quantity" />
                                  <a class="atg_tableIcon atg_propertyClear" title="<fmt:message key='cart.items.delete'/>" href="#" onclick="atg.commerce.csr.cart.deleteCartItemByRelationalshipId('${ciRelationship.id}');return false;"> <fmt:message key="cart.items.delete" /></a>
                                </td>
                              </tr>
                            </c:when>
                          </c:choose>
                        </c:forEach>
                      </c:forEach>

                      <%-- Display breakdown if commerce item is associated with a giftlist --%>
                      <c:set var="customerTotalQuantity" value="${itemQuantity}" />
                      <c:set var="giftlistTotalQuantity" value="0" />
                      <dsp:droplet name="/atg/commerce/gifts/GiftlistShoppingCartQuantityDroplet">
                        <dsp:param name="commerceItemId" value="${item.id}" />
                        <dsp:param name="order" value="${order}" />
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="key" param="key" />
                          <dsp:getvalueof var="value" param="element" />
                            <c:set var="customerTotalQuantity" value="${customerTotalQuantity - value }" />
                            <c:set var="giftlistTotalQuantity" value="${giftlistTotalQuantity + value }" />
                        </dsp:oparam>
                        <dsp:getvalueof var="numberOfGiftRecipients" param="size" />
                      </dsp:droplet><%-- End GiftlistShoppingCartQuantityDroplet --%>
                      <c:if test="${customerTotalQuantity > '0' && giftlistTotalQuantity > '0'}">
                        <tr>
                          <c:if test="${isMultiSiteEnabled == true}">
                           <td></td>
                           <td>
                          </c:if>
                          <c:if test="${isMultiSiteEnabled == false}">
                            <td>
                          </c:if>
                          <ul class="atg_commerce_csr_itemDesc">
                            <span class="atg-csc-in-store-sub-item"></span><li class="atg_commerce_csr_giftwishListName"><fmt:message key="cart.customer.currentCustomer.label" /><%-- Display Current Customer label --%></li>
                          </ul>
                          </td>
                          <td></td>
                          <td>
                            <web-ui:formatNumber value="${customerTotalQuantity}"/> <%-- Display Customer Quantity --%>
                          </td>
                        </tr>
                      </c:if>
                      <%-- Multiple Gift Recipients detected, So disable Quantity box for this particular SKU --%>
                      <c:if test="${numberOfGiftRecipients > 1}">
                        <c:choose>
                          <c:when test="${true eq isItemFractional}">
                            <input value="${itemQuantity}" type="hidden" size="9" maxlength="9" name="${item.id}" id="hidden_${item.id}"
                                   dojoType="dijit.form.NumberTextBox"
                                   constraints="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"
                                   invalidMessage="${fractionalValidationMessage}" />
                          </c:when>
                          <c:otherwise>
                            <input value="${itemQuantity}" type="hidden" size="5" maxlength="9" name="${item.id}" id="hidden_${item.id}"
                                   dojoType="dijit.form.NumberTextBox"
                                   constraints="{places:0, pattern: '#####'}"
                                   invalidMessage="Please enter a valid value."/>
                          </c:otherwise>
                        </c:choose>
                        <script type="text/javascript">
                          dojo.byId("${item.id}").disabled = true;
                        </script>
                      </c:if>
                      <%-- Gift Recipient along with current customer purchasing sku for themself, so disable Quantity box for this particular SKU --%>
                      <c:if test="${numberOfGiftRecipients > '0' && customerTotalQuantity > '0'}">
                        <c:choose>
                          <c:when test="${true eq isItemFractional}">
                            <input value="${itemQuantity}" type="hidden" size="9" maxlength="9" name="${item.id}" id="hidden_${item.id}"
                                   dojoType="dijit.form.NumberTextBox"
                                   constraints="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"
                                   invalidMessage="${fractionalValidationMessage}" />
                          </c:when>
                          <c:otherwise>
                            <input value="${itemQuantity}" type="hidden" size="5" maxlength="5" name="${item.id}" id="hidden_${item.id}"
                                    dojoType="dijit.form.NumberTextBox"
                                    constraints="{places:0, pattern: '#####'}"
                                    invalidMessage="Please enter a valid value."/>
                          </c:otherwise>
                        </c:choose>
                        <script type="text/javascript">
                          dojo.byId("${item.id}").disabled = true;
                        </script>
                      </c:if>
                      <dsp:droplet name="/atg/commerce/gifts/GiftlistShoppingCartQuantityDroplet">
                        <dsp:param name="commerceItemId" value="${item.id}" />
                        <dsp:param name="order" value="${order}" />
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="key" param="key" />
                          <dsp:getvalueof var="value" param="element" />
                          <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
                            <dsp:param name="id" param="key" />
                            <dsp:oparam name="output">
                              <dsp:setvalue paramvalue="element" param="giftlist" />
                              <tr>
                                <c:if test="${isMultiSiteEnabled == true}">
                                  <td></td>
                                  <td>
                                </c:if>
                                <c:if test="${isMultiSiteEnabled == false}">
                                  <td>
                                </c:if>
                                <ul class="atg_commerce_csr_itemDesc">
                                  <span class="atg-csc-in-store-sub-item"></span><li class="atg_commerce_csr_giftwishListName">
                                  <dsp:getvalueof var="eventName" vartype="java.lang.String" param="giftlist.eventName" />
                                  <a href="#" class="blueU" onclick="atg.commerce.csr.order.gift.giftlistBuyFrom('${key}');return false;">
                                  <dsp:valueof param="giftlist.owner.firstName" />&nbsp;
                                  <dsp:valueof param="giftlist.owner.lastName" />,
                                  <c:out value="${eventName}" />
                                  </a><%-- Display Gift Recipient Name and Event Name --%></li>
                                </ul>
                                </td>
                                <td></td>
                                <td>
                                  <web-ui:formatNumber value="${value}"/> <%-- Display Gift Recipient Quantity --%>
                                </td>
                              </tr>
                            </dsp:oparam>
                          </dsp:droplet><%-- End GiftlistLookupDroplet --%>
                        </dsp:oparam>
                      </dsp:droplet><%-- End GiftlistShoppingCartQuantityDroplet --%>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <c:forEach items="${order.commerceItems}" var="item" varStatus="vs">
                      <dsp:tomap var="sku" value="${item.auxiliaryData.catalogRef}" />
                      <dsp:tomap var="product" value="${item.auxiliaryData.productRef}" />

                      <dsp:droplet name="/atg/commerce/custsvc/order/GetCommerceItemQuantityDroplet">
                        <dsp:param name="item" value="${item}"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="itemQuantity" param="quantity"/>
                        </dsp:oparam>
                      </dsp:droplet>

                      <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
                        <dsp:param name="item" value="${item}"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="isItemFractional" param="fractional"/>
                        </dsp:oparam>
                      </dsp:droplet>

                      <tr class="${((vs.index % 2)==0) ? '' : 'atg_dataTable_altRow'}">
                        <c:if test="${isMultiSiteEnabled == true}">
                          <c:set var="siteId" value="${item.auxiliaryData.siteId}" />
                          <td class="atg_commerce_csr_siteIcon"><csr:siteIcon siteId="${siteId}" /></td>
                        </c:if>

                        <%-- Item Description --%>
                        <td>
                          <c:set var="cartShareableSite" value="${true}" />
                          <dsp:droplet name="/atg/dynamo/droplet/multisite/SitesShareShareableDroplet">
                            <dsp:param name="siteId" value="${currentSiteId}" />
                            <dsp:param name="otherSiteId" value="${siteId}" />
                            <dsp:param name="shareableTypeId" value="${CSRConfigurator.cartShareableTypeId}" />
                            <dsp:oparam name="false">
                              <c:set var="cartShareableSite" value="${false}" />
                            </dsp:oparam>
                           </dsp:droplet>
                        
                        <ul class="atg_commerce_csr_itemDesc">
                          <li>
                          <%-- If the current site and item site do not share shopping cart, then display the item Description without a link --%>
                          <c:choose>
                            <c:when test="${cartShareableSite == false}">
                              ${fn:escapeXml(sku.displayName)}
                            </c:when>
                            <c:when test="${(isMultiSiteEnabled == true) && (isSiteDeleted != true)}">
                              <a onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}', '${fn:escapeXml(siteId)}', '${fn:escapeXml(currentSiteId)}');
                                    return false;" href="#">${fn:escapeXml(product.displayName)}</a>
                            </c:when>
                            <c:otherwise>
                              <a onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${fn:escapeXml(product.id)}');
                                    return false;" href="#">${fn:escapeXml(product.displayName)}</a>
                            </c:otherwise>
                          </c:choose>
                          
                          <c:choose>
	                          <c:when test ="${envTools.siteAccessControlOn =='true' }">
	                            <c:set var="siteId" value="${item.auxiliaryData.siteId}"/>   
	                            <dsp:droplet name="IsSiteAccessibleDroplet">
	                              <dsp:param name="siteId" value="${siteId}"/>
	                              <dsp:oparam name="true">
	                                <dsp:include src="${changeGiftLinkFragment.URL}" otherContext="${changeGiftLinkFragment.servletContext}">
                                    <dsp:param name="item" value="${item}" />
                                    <dsp:param name="order" value="${order}" />
                                  </dsp:include>
	                              </dsp:oparam>
	                              <dsp:oparam name="false">
	                                &nbsp;
	                              </dsp:oparam>
	                            </dsp:droplet>
	                          </c:when>
	                          <c:otherwise>
	                            <dsp:include src="${changeGiftLinkFragment.URL}" otherContext="${changeGiftLinkFragment.servletContext}">
                                <dsp:param name="item" value="${item}" />
                                <dsp:param name="order" value="${order}" />
                              </dsp:include>
	                          </c:otherwise>
	                        </c:choose>

                          </li>
                          <li>SKU: ${fn:escapeXml(item.catalogRefId)}</li>
                          <c:if test="${not empty product.fulfillmentMethod && product.fulfillmentMethod == 7}">
							<li><span style="font-weight:bold"><font  color="0000FF">BOPIS ONLY FULFILLMENT</font></span></li>
			              </c:if>
                          
                           <c:if test="${not empty product.minimumAge}">
                           	<c:set var="signatureRequired" value="1" />
							<li><span style="font-weight:bold"><font  color="FF0000">YOU MUST BE AT LEAST ${product.minimumAge} YEARS OLD TO PURCHASE THIS ITEM.</font></span></li>
			              </c:if>
                          <li>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array"  value="${item.auxiliaryData.productRef.dynamicAttributes}"/>
								<dsp:oparam name="outputStart">
									<ul style="padding-left: 10px;">
								</dsp:oparam>
								<dsp:oparam name="output">
									<dsp:setvalue param="skuDynamicAttributes" value="${sku.dynamicAttributes}"/>
									<dsp:getvalueof var="key" param="key"/>
									<li><dsp:valueof param="element"/>: <dsp:valueof param="skuDynamicAttributes.${key}"/></li>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
									</ul>
								</dsp:oparam>
		  					</dsp:droplet>
                          </li>
                        </ul>
                        </td>
                        <%-- Inventory status --%>
                        <td class="atg_commerce_csr_inventoryStatus">
							<dsp:droplet name="CommerceItemStateDescriptions">
								<dsp:param name="state" value="${item.stateAsString}"/>
								<dsp:param name="elementName" value="stateDescription"/>
								<dsp:oparam name="output">
									<dsp:droplet name="IsHighlightedState">
										<dsp:param name="obj" value="${item}"/>
										<dsp:oparam name="true">
											<span class="atg_commerce_csr_dataHighlight"><dsp:valueof param="stateDescription"></dsp:valueof></span>
										</dsp:oparam>        
										<dsp:oparam name="false">
											<dsp:valueof param="stateDescription"></dsp:valueof>
										</dsp:oparam>        
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
                        </td>
                        <%-- Quantity --%>
                        <c:choose>
                          <c:when test ="${envTools.siteAccessControlOn =='true' }">
                            <c:set var="siteId" value="${item.auxiliaryData.siteId}"/>   
                            <dsp:droplet name="IsSiteAccessibleDroplet">
                              <dsp:param name="siteId" value="${siteId}"/>
                              <dsp:oparam name="true">
                                <td class="atg_commerce_csr_quatity">
                                  <c:choose>
                                    <c:when test="${true eq isItemFractional}">
                                      <input value="${itemQuantity}" type="text" size="9" maxlength="9" name="${item.id}" id="${item.id}"
                                             dojoType="dijit.form.NumberTextBox"
                                             constraints="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"
                                             invalidMessage="${fractionalValidationMessage}" />
                                    </c:when>
                                    <c:otherwise>
                                      <input value="${itemQuantity}" type="text" size="5" maxlength="5" name="${item.id}" id="${item.id}"
                                             dojoType="dijit.form.NumberTextBox"
                                             constraints="{places:0, pattern: '#####'}"
                                             invalidMessage="Please enter a valid value."/>
                                    </c:otherwise>
                                  </c:choose>
                                </td>
                              </dsp:oparam>
                              <dsp:oparam name="false">
                                <td class="atg_commerce_csr_quatity"/>
                              </dsp:oparam>
                            </dsp:droplet>
                          </c:when>
                          <c:otherwise>
                            <td class="atg_commerce_csr_quatity">
                              <c:choose>
                                <c:when test="${true eq isItemFractional}">
                                  <input value="${itemQuantity}" type="text" size="9" maxlength="9" name="${item.id}" id="${item.id}"
                                         dojoType="dijit.form.NumberTextBox"
                                         constraints="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"
                                         invalidMessage="${fractionalValidationMessage}" />
                                </c:when>
                                <c:otherwise>
                                  <dsp:getvalueof var="giftCardProduct" bean="/atg/commerce/catalog/CatalogTools.giftCardProductId"/>
                                  <c:choose>
        							<c:when test="${item.auxiliaryData.productRef.id == giftCardProduct}">
        							<input value="${itemQuantity}" type="hidden" name="${item.id}" id="${item.id}"/>&nbsp;&nbsp;${itemQuantity}
            						</c:when>
    								<c:otherwise>
										<input value="${itemQuantity}" type="text" size="5" maxlength="5" name="${item.id}" id="${item.id}"
                                         dojoType="dijit.form.NumberTextBox"
                                         constraints="{places:0, pattern: '#####'}"
                                         invalidMessage="Please enter a valid value."/>
									</c:otherwise>
								  </c:choose>
                                </c:otherwise>
                              </c:choose>
                            </td>
                          </c:otherwise>
                        </c:choose>
                        <%-- Price Each --%>
                        <td class="atg_numberValue atg_commerce_csr_priceEach">
                        <c:if test="${item.priceInfo.onSale == true}">
                          <c:if test="${item.priceInfo.salePrice < item.priceInfo.listPrice}">
				  <span class="atg_commerce_csr_common_content_strikethrough"> <csr:formatNumber value="${item.priceInfo.listPrice}" type="currency" currencyCode="${currencyCode}" /> </span>
				  <csr:formatNumber value="${item.priceInfo.salePrice}" type="currency" currencyCode="${currencyCode}" />
                          </c:if>
 			  <c:if test="${item.priceInfo.salePrice == null ||  item.priceInfo.salePrice == item.priceInfo.listPrice}">
 	                         <csr:formatNumber value="${item.priceInfo.listPrice}" type="currency" currencyCode="${currencyCode}" />
                          </c:if>                        
                        </c:if> 
                        <c:if test="${item.priceInfo.onSale == false}">
                          <csr:formatNumber value="${item.priceInfo.listPrice}" type="currency" currencyCode="${currencyCode}" />
                        </c:if></td>
                        <%-- Total Price --%>
                        <td class="atg_numberValue atg_commerce_csr_totalPrice"><c:set var="pi" value="${item.priceInfo}" /> <csr:formatNumber value="${pi.amount}" type="currency" currencyCode="${currencyCode}" /></td>
                        <%-- Final Price (price override) --%>
                        <c:if test="${adjustPricePriv}">
                        <c:choose>
                          <c:when test ="${envTools.siteAccessControlOn =='true' }">
                            <c:set var="siteId" value="${item.auxiliaryData.siteId}"/>   
                            <dsp:droplet name="IsSiteAccessibleDroplet">
                              <dsp:param name="siteId" value="${siteId}"/>
                              <dsp:oparam name="true">
                                <td class="atg_numberValue atg_commerce_csr_finalPrice">
                                  <csr:formatNumber var="finalAmount" value="${item.priceInfo.amountIsFinal ? item.priceInfo.amount : '' }" type="currency" currencyCode="${currencyCode}" />
                                  <input type="text" size="10" maxlength="12" class="atg_numberValue" id="IPO:${item.id}" name="IPO:${item.id}" value="${finalAmount}" />
                                </td>
                              </dsp:oparam>
                              <dsp:oparam name="false">
                                <td class="atg_numberValue atg_commerce_csr_finalPrice"/>
                              </dsp:oparam>
                            </dsp:droplet>
                          </c:when>
                          <c:otherwise>
                            <td class="atg_numberValue atg_commerce_csr_finalPrice">
                              <csr:formatNumber var="finalAmount" value="${item.priceInfo.amountIsFinal ? item.priceInfo.amount : '' }" type="currency" currencyCode="${currencyCode}" />
                              <input type="text" size="10" maxlength="12" class="atg_numberValue" id="IPO:${item.id}" name="IPO:${item.id}" value="${finalAmount}" />
                            </td>
                          </c:otherwise>
                        </c:choose>
                        </c:if>
                        <%-- Edit line item --%>
                        <dsp:droplet name="/atg/commerce/gifts/GiftlistShoppingCartQuantityDroplet">
                          <dsp:param name="commerceItemId" value="${item.id}" />
                          <dsp:param name="order" value="${order}" />
                          <dsp:oparam name="empty">
                          <c:choose>
                          <c:when test ="${envTools.siteAccessControlOn =='true' }">
                            <c:set var="siteId" value="${item.auxiliaryData.siteId}"/>   
                            <dsp:droplet name="IsSiteAccessibleDroplet">
                              <dsp:param name="siteId" value="${siteId}"/>
                              <dsp:oparam name="true">
                                <td class="atg_iconCell"><dsp:tomap var="product" value="${item.auxiliaryData.productRef}" /> <c:if test="${fn:length(product.childSKUs) > 1}">
                              <svc-ui:frameworkPopupUrl var="popupUrl" value="/include/order/editProductSKU.jsp" context="${CSRConfigurator.contextRoot}" windowId="${windowId}" mode="apply" commerceItemId="${item.id}" />
                              <a class="atg_tableIcon atg_propertyEdit" title="<fmt:message key='cart.items.edit'/>" href="#"
                                onclick="atg.commerce.csr.common.showPopupWithReturn({
                                  popupPaneId: 'atg_commerce_csr_catalog_productQuickViewPopup',
                                  title: '${fn:escapeXml(sku.displayName)}',
                                  url: '${popupUrl}',
                                  onClose: function( args ) {
                                    if ( args.result == 'ok' ) {
                                      atgSubmitAction({
                                        panels : ['cmcShoppingCartP'],
                                        panelStack : 'cmcShoppingCartPS',
                                        form : document.getElementById('transformForm')
                                      });
                                    }
                                  }});return false;">
                              <fmt:message key="cart.items.edit" /> </a>
                            </c:if></td>
                              </dsp:oparam>
                              <dsp:oparam name="false">
                                <td class="atg_iconCell"/>
                              </dsp:oparam>
                            </dsp:droplet>
                          </c:when>
                          <c:otherwise>
                            <td class="atg_iconCell"><dsp:tomap var="product" value="${item.auxiliaryData.productRef}" /> <c:if test="${fn:length(product.childSKUs) > 1}">
                              <svc-ui:frameworkPopupUrl var="popupUrl" value="/include/order/editProductSKU.jsp" context="${CSRConfigurator.contextRoot}" windowId="${windowId}" mode="apply" commerceItemId="${item.id}" />
                              <a class="atg_tableIcon atg_propertyEdit" title="<fmt:message key='cart.items.edit'/>" href="#"
                                onclick="atg.commerce.csr.common.showPopupWithReturn({
                                  popupPaneId: 'atg_commerce_csr_catalog_productQuickViewPopup',
                                  title: '${fn:escapeXml(sku.displayName)}',
                                  url: '${popupUrl}',
                                  onClose: function( args ) {
                                    if ( args.result == 'ok' ) {
                                      atgSubmitAction({
                                        panels : ['cmcShoppingCartP'],
                                        panelStack : 'cmcShoppingCartPS',
                                        form : document.getElementById('transformForm')
                                      });
                                    }
                                  }});return false;">
                              <fmt:message key="cart.items.edit" /> </a>
                            </c:if></td>
                          </c:otherwise>
                        </c:choose>
                        </dsp:oparam>
                          <dsp:oparam name="output">
                            <td class="atg_iconCell"></td>
                          </dsp:oparam>
                        </dsp:droplet>

                        <%-- Delete line item --%>
                        <c:choose>
                          <c:when test ="${envTools.siteAccessControlOn =='true' }">
                            <c:set var="siteId" value="${item.auxiliaryData.siteId}"/>   
                            <dsp:droplet name="IsSiteAccessibleDroplet">
                              <dsp:param name="siteId" value="${siteId}"/>
                              <dsp:oparam name="true">
                                <td class="atg_iconCell">
                                  <a class="atg_tableIcon atg_propertyClear" title="<fmt:message key='cart.items.delete'/>" href="#" onclick="atg.commerce.csr.cart.deleteCartItem('${item.id}');return false;"> <fmt:message
                                    key="cart.items.delete" /></a>
                                </td>
                              </dsp:oparam>
                              <dsp:oparam name="false">
                                <td class="atg_iconCell"/>
                              </dsp:oparam>
                            </dsp:droplet>
                          </c:when>
                          <c:otherwise>
                            <td class="atg_iconCell">
                              <a class="atg_tableIcon atg_propertyClear" title="<fmt:message key='cart.items.delete'/>" href="#" onclick="atg.commerce.csr.cart.deleteCartItem('${item.id}');return false;"> <fmt:message
                                key="cart.items.delete" /></a>
                            </td>
                          </c:otherwise>
                        </c:choose>
                        
                      </tr>
                      
                      <%-- Display breakdown if commerce item is associated with a giftlist --%>
                      <c:set var="customerTotalQuantity" value="${itemQuantity}" />
                      <c:set var="giftlistTotalQuantity" value="0" />
                      <dsp:droplet name="/atg/commerce/gifts/GiftlistShoppingCartQuantityDroplet">
                        <dsp:param name="commerceItemId" value="${item.id}" />
                        <dsp:param name="order" value="${order}" />
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="key" param="key" />
                          <dsp:getvalueof var="value" param="element" />
                            <c:set var="customerTotalQuantity" value="${customerTotalQuantity - value }" />
                            <c:set var="giftlistTotalQuantity" value="${giftlistTotalQuantity + value }" />
                        </dsp:oparam>
                        <dsp:getvalueof var="numberOfGiftRecipients" param="size" />
                      </dsp:droplet><%-- End GiftlistShoppingCartQuantityDroplet --%>
                      <c:if test="${customerTotalQuantity > '0' && giftlistTotalQuantity > '0'}">
                        <tr>
                          <c:if test="${isMultiSiteEnabled == true}">
                           <td></td>
                           <td>
                          </c:if>
                          <c:if test="${isMultiSiteEnabled == false}">
                            <td>
                          </c:if>
                          <ul class="atg_commerce_csr_itemDesc">
                            <span class="atg-csc-in-store-sub-item"></span><li class="atg_commerce_csr_giftwishListName"><fmt:message key="cart.customer.currentCustomer.label" /><%-- Display Current Customer label --%></li>
                          </ul>
                          </td>
                          <td></td>
                          <td>
                            <web-ui:formatNumber value="${customerTotalQuantity}"/> <%-- Display Customer Quantity --%>
                          </td>
                        </tr>
                      </c:if>
                      <%-- Multiple Gift Recipients detected, So disable Quantity box for this particular SKU --%>
                      <c:if test="${numberOfGiftRecipients > 1}">
                        <c:choose>
                          <c:when test="${true eq isItemFractional}">
                            <input value="${itemQuantity}" type="hidden" type="text" size="9" maxlength="9" name="${item.id}" id="hidden_${item.id}"
                                   dojoType="dijit.form.NumberTextBox"
                                   constraints="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"
                                   invalidMessage="${fractionalValidationMessage}" />
                          </c:when>
                          <c:otherwise>
                            <input value="${itemQuantity}" type="hidden" type="text" size="5" maxlength="5" name="${item.id}" id="hidden_${item.id}"
                                   dojoType="dijit.form.NumberTextBox"
                                   constraints="{places:0, pattern: '#####'}"
                                   invalidMessage="Please enter a valid value."/>
                          </c:otherwise>
                        </c:choose>
                        <script type="text/javascript">         
                          dojo.byId("${item.id}").disabled = true;
                        </script>
                      </c:if>
                      <%-- Gift Recipient along with current customer purchasing sku for themself, so disable Quantity box for this particular SKU --%>
                      <c:if test="${numberOfGiftRecipients > '0' && customerTotalQuantity > '0'}">
                        <c:choose>
                          <c:when test="${true eq isItemFractional}">
                            <input value="${itemQuantity}" type="hidden" type="text" size="9" maxlength="9" name="${item.id}" id="hidden_${item.id}"
                                   dojoType="dijit.form.NumberTextBox"
                                   constraints="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"
                                   invalidMessage="${fractionalValidationMessage}" />
                          </c:when>
                          <c:otherwise>
                            <input value="${itemQuantity}" type="hidden" type="text" size="5" maxlength="5" name="${item.id}" id="hidden_${item.id}"
                                    dojoType="dijit.form.NumberTextBox"
                                    constraints="{places:0, pattern: '#####'}"
                                    invalidMessage="Please enter a valid value."/>
                          </c:otherwise>
                        </c:choose>
                        <script type="text/javascript">         
                          dojo.byId("${item.id}").disabled = true;
                        </script>
                      </c:if>
                      <dsp:droplet name="/atg/commerce/gifts/GiftlistShoppingCartQuantityDroplet">
                        <dsp:param name="commerceItemId" value="${item.id}" />
                        <dsp:param name="order" value="${order}" />
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="key" param="key" />
                          <dsp:getvalueof var="value" param="element" />
                          <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
                            <dsp:param name="id" param="key" />
                            <dsp:oparam name="output">
                              <dsp:setvalue paramvalue="element" param="giftlist" />
                              <tr>
                                <c:if test="${isMultiSiteEnabled == true}">
                                  <td></td>
                                  <td>
                                </c:if>
                                <c:if test="${isMultiSiteEnabled == false}">
                                  <td>
                                </c:if>
                                <ul class="atg_commerce_csr_itemDesc">
                                  <span class="atg-csc-in-store-sub-item"></span><li class="atg_commerce_csr_giftwishListName">
                                  <dsp:getvalueof var="eventName" vartype="java.lang.String" param="giftlist.eventName" />
                                  <a href="#" class="blueU" onclick="atg.commerce.csr.order.gift.giftlistBuyFrom('${key}');return false;">
                                  <dsp:valueof param="giftlist.owner.firstName" />&nbsp; 
                                  <dsp:valueof param="giftlist.owner.lastName" />, 
                                  <c:out value="${eventName}" /> 
                                  </a><%-- Display Gift Recipient Name and Event Name --%></li>
                                </ul>
                                </td>
                                <td></td>
                                <td>
                                  <web-ui:formatNumber value="${value}"/> <%-- Display Gift Recipient Quantity --%>
                                </td>
                              </tr>
                            </dsp:oparam>
                          </dsp:droplet><%-- End GiftlistLookupDroplet --%>
                        </dsp:oparam>
                      </dsp:droplet><%-- End GiftlistShoppingCartQuantityDroplet --%>
                    </c:forEach>
                  </c:otherwise>
                </c:choose>
                <tr><td></td>
                <c:if test="${(not empty signatureRequired) and (signatureRequired gt 0)}">
                	<td colspan="3" class="atg_commerce_csr_shoppingCart_items_underline">
						<p>
							Your order consists of an item(s) that requires signature upon delivery. 
							Your order will be charged an additional shipping fee of 
							<strong><fmt:formatNumber value="${signatureRequired * signatureRequiredFee}" type="currency" /></strong>.
						</p>
					</td>
				</c:if>
               
                <td colspan="3" class="atg_commerce_csr_shoppingCart_items_underline">
                  <dsp:include src="${selectGiftLinkFragment.URL}" otherContext="${selectGiftLinkFragment.servletContext}">
                  <dsp:param name="order" value="${order}" />
                  </dsp:include>
                </td></tr>
              </tbody>
            </table>

            <%-- Order Summary and Buttons --%>
            <div class="atg_commerce_csr_shoppingCartSummary">
              <div class="atg_commerce_csr_shoppingCartControls">
                <svc-ui:frameworkUrl var="setOrderErrorURL" panelStacks="globalPanels,cmcShoppingCartPS"/>
                <dsp:input type="hidden" name="errorURL" value="${setOrderErrorURL}" bean="CartModifierFormHandler.setOrderErrorURL" />
                <dsp:input type="hidden" value="ignored" priority="-10" id="modifyOrderSubmitter" bean="CartModifierFormHandler.setOrderByCommerceId" />
                <c:choose>
                  <c:when test="${shippingGroupCount > 1}">
                    <input type="button" id="updatePriceButton" value="<fmt:message key='cart.items.updatePrice'/>" onclick="atg.commerce.csr.cart.setOrderByRelationshipIdForm(); return false;" />
                    <dsp:input type="hidden" value="ignored" priority="-10" id="moveToPurchaseInfoSubmitter" bean="CartModifierFormHandler.moveToPurchaseInfoByRelId" />
                    <input type="button" class="atg_commerce_csr_activeButton atg_commerce_csr_shippingButton" dojoType="atg.widget.validation.SubmitButton" form="itemsForm" name="checkoutFooterNextButton" id="checkoutFooterNextButton"
                      value="<fmt:message key="cart.continueToShipping"/>" onclick="atg.commerce.csr.cart.submitNextAction(); return false;" />
                  </c:when>
                  <c:otherwise>
                    <input type="button" id="updatePriceButton" value="<fmt:message key='cart.items.updatePrice'/>" onclick="atg.commerce.csr.cart.updatePrice(); return false;" />
                    <dsp:input type="hidden" value="ignored" priority="-10" id="moveToPurchaseInfoSubmitter" bean="CartModifierFormHandler.moveToPurchaseInfoByCommerceId" />
                    <input type="button" class="atg_commerce_csr_activeButton atg_commerce_csr_shippingButton" dojoType="atg.widget.validation.SubmitButton" form="itemsForm" name="checkoutFooterNextButton" id="checkoutFooterNextButton"
                      value="<fmt:message key="cart.continueToShipping"/>" onclick="atg.commerce.csr.cart.submitNextAction(); return false;" />
                  </c:otherwise>
                </c:choose>
              </div>
              <div class="atg_commerce_csr_orderSummary"><csr:displayOrderSummary order="${order}" isShowHeader="${false}" /></div>

            <script type="text/javascript">
            _container_.onLoadDeferred.addCallback( function() {
              atg.keyboard.registerFormDefaultEnterKey("itemsForm", "updatePriceButton");
            });
            _container_.onUnloadDeferred.addCallback( function() {
              atg.keyboard.unRegisterFormDefaultEnterKey("itemsForm");
            });
          </script>
          </dsp:form>
          <c:if test="${!isProfileTransient && not empty customerProfile.taxExemptions}">
	          <dsp:getvalueof var="taxExemptions" vartype="java.lang.Object" value="${customerProfile.taxExemptions}"/>
	          <dsp:getvalueof var="taxExmpOnOrder" bean="ShoppingCart.current.taxExemptionName"/>
	          <div style="margin: 10px; width:210px; float:right;">
				<dsp:form method="post" formid="cartTaxExemptionForm" id="cartTaxExemptionForm" name="cartTaxExemptionForm">
					<dsp:input type="hidden" value="${thisUrl}" bean="CartModifierFormHandler.addTaxExemptionSuccessUrl"/>
					<dsp:input type="hidden" value="${thisUrl}" bean="CartModifierFormHandler.addTaxExemptionErrorUrl"/>
					<div><label for="tax-exemption">Tax Exemption</label></div>
					<dsp:select bean="CartModifierFormHandler.taxExempSelected" id="tax-exemption">
						<dsp:option value="">Please Select</dsp:option>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${taxExemptions}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="code" param="element.classificationCode" />
								<dsp:getvalueof var="displayName" param="element.nickName"/>
								<dsp:getvalueof var="exe" param="element"/>
								<c:choose>
									<c:when test="${code == taxExmpOnOrder}">
										<dsp:option selected="true" value="${code}">${displayName}</dsp:option>
									</c:when>
									<c:otherwise>
										<dsp:option value="${code}">${displayName}</dsp:option>
									</c:otherwise>
								</c:choose>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:select>
					<dsp:input type="hidden" value="dummy" bean="CartModifierFormHandler.applyTaxExemption"/>
					<input type="submit" value="Apply Tax Exemption"/>
				</dsp:form>
				<script type="text/javascript">
              		document.forms["cartTaxExemptionForm"].onsubmit = function() {
                	atgSubmitAction({
                        panels : ['cmcShoppingCartP'],
                        panelStack : 'cmcShoppingCartPS',
                        form : document.getElementById('cartTaxExemptionForm')});
                	return false;
              		};
            	</script>
	          </div>
		  </c:if>
        </c:otherwise>
      </c:choose>

      <c:set var="displayOrderModDiv" value="${true}"/>
      <%-- if we are doing an exchange and there are no promos in the exchange order, don't display the div --%>
      <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnActive">
      <dsp:oparam name="true">
        <dsp:droplet name="/atg/commerce/custsvc/promotion/PromotionViewDroplet">
        <dsp:param name="byType" value="${false}"/>
        <dsp:param name="order" value="${cart.current}"/>
        <dsp:oparam name="empty">
            <c:set var="displayOrderModDiv" value="${false}"/>
        </dsp:oparam>      
        </dsp:droplet>
      </dsp:oparam>      
      </dsp:droplet>

      <c:if test="${displayOrderModDiv}">
      <%-- Order Modification Container --%>
      <div class="atg_commerce_csr_orderModifications">
      
      
      <%-- Promotions Container --%>
        <div class="atg_commerce_csr_promotionsListing">
        
        <%-- Promotions Title and Browse Link --%>
        <h4><fmt:message key="cart.promotions" /> <%-- Show promotions browser if order not exchange/return, not submitted and agent has privilege --%> 
        <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnActive">
          <dsp:oparam name="false">
            <dsp:droplet name="/atg/commerce/custsvc/order/IsOrderSubmitted">
              <dsp:oparam name="false">
                <dsp:droplet name="HasAccessRight">
                  <dsp:param name="accessRight" value="commerce-custsvs-browse-promotions-privilege" />
                  <dsp:oparam name="accessGranted">
                    <c:set var="isShowPromotionsBrowser" value="${true}"/>
                    <a class="atg_commerce_csr_promoBrowser" href="#" onclick="atg.commerce.csr.promotion.openPromotionsBrowser(${walletGridConfig.gridWidgetId}_refreshSearchResults)"><fmt:message key="cart.promotions.browsePromotions" /></a>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet></h4>

        <%-- List of Promotions --%>
        <div class="atg_commerce_csr_promotionsListingInternal"><dsp:include src="/include/order/promotionQualificationSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
          <dsp:param name="order" value="${order}" />
        </dsp:include></div>
  
        <%-- Enter Coupon Code --%>
          <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnActive">
            <dsp:oparam name="false">
              <dsp:form method="post" id="couponForm" formid="couponForm">
                <ul class="atg_dataForm atg_commerce_csr_enterPromoForm">
                  <li><dsp:input type="hidden" value="${thisUrl}" bean="CouponFormHandler.claimCouponSuccessURL" /> <dsp:input type="hidden" value="${thisUrl}" bean="CouponFormHandler.claimCouponErrorURL" /> <fmt:message var="enterClaimCodeMsg"
                    key="cart.promotions.enterClaimCode" /> <dsp:input type="text" id="couponCode" size="26" value="${fn:escapeXml(enterClaimCodeMsg)}"
                    onclick="atg.commerce.csr.common.setIfValue(
                        'couponCode','${fn:escapeXml(enterClaimCodeMsg)}','')"
                    onblur="atg.commerce.csr.common.setIfValue(
                        'couponCode','','${fn:escapeXml(enterClaimCodeMsg)}')" bean="CouponFormHandler.couponClaimCode" /> <dsp:input type="hidden" value="dummy"
                    bean="CouponFormHandler.claimCoupon" /></li>
                  <li>
                    <input type="submit" value="<fmt:message key='cart.promotions.addCoupon'/>"/>
                   </li>
                </ul>
              </dsp:form>
              <script type="text/javascript">
              document.forms["couponForm"].onsubmit = function() {
                atgSubmitAction({
                        panels : ['cmcShoppingCartP'],
                        panelStack : 'cmcShoppingCartPS',
                        form : document.getElementById('couponForm')});
                return false;
              };
            </script>
            </dsp:oparam>
          </dsp:droplet> 
  
        <%-- Closing DIV for Promotions Container --%>
        </div>
        
        <%-- Site Access Control to display or disable Appeasment section --%>
        <%-- Remove appeasement section
         <c:choose>
	         <c:when test="${envTools.siteAccessControlOn == 'true'}">
	           <c:set var="allSitesAccessible" value="1"/>
	            <c:forEach items="${order.commerceItems}" var="item" varStatus="vs">             
	             <c:set var="siteId" value="${item.auxiliaryData.siteId}"/>   
	             <dsp:droplet name="IsSiteAccessibleDroplet">
	               <dsp:param name="siteId" value="${siteId}"/>
	               <dsp:oparam name="false">
	                 <c:set var="allSitesAccessible" value="0"/>
	               </dsp:oparam>
	             </dsp:droplet>
	           </c:forEach>
	           
	           <c:if test="${allSitesAccessible == '1'}">
						   <%@include file="orderAdjustmentTable.jspf" %>
	           </c:if>
	        </c:when>
	        <c:otherwise>
	          <%@include file="orderAdjustmentTable.jspf" %>
	        </c:otherwise>
        </c:choose>
         --%>
      </div>

      <%-- Closing DIV for Order Modifications Container --%>
      </div>
      </c:if> <%-- end if promo container should be included --%>

    <div style="display: none;" id="atg_commerce_csr_catalog_addToCartContainer"></div>
    </dsp:layeredBundle>
    <%-- These variables are used in validation script --%>
    <dsp:getvalueof var="minAdjAmount" bean="ManualAdjustmentsFormHandler.minimumAdjustmentAmount" />
    <dsp:getvalueof var="maxAdjAmount" bean="ManualAdjustmentsFormHandler.maximumAdjustmentAmount" />
    <script type="text/javascript">
      atg.progress.update('cmcShoppingCartPS');
      atg.commerce.csr.cart.isInRange = function() {
        var maxl = parseInt(this.maxlength);
        var minl = parseInt(this.minlength);
        var len = this.getValue().length;
        var inrange = len >= (isNaN(minl) ? 0 : minl) && len <= (isNaN(maxl) ? Infinity : maxl);
        if (inrange == true) {
          dojo.byId("adjustmentSubmitButton").disabled = false;
        }
        else {
          dojo.byId("adjustmentSubmitButton").disabled = true;
        }
        return inrange;
      }
      var validateAdjustmentsForm = function () {
        var disable = false;
        var amount = dijit.byId("amountTxt").getValue();
        // amount must be number in range form minAdjAmount to maxAdjAmount
        if (isNaN(amount) || amount < ${minAdjAmount} || amount > ${maxAdjAmount})  disable = true;  
        dojo.byId("adjustmentsForm").adjustmentSubmitButton.disabled = disable;
      }
      _container_.onLoadDeferred.addCallback(function () {
        var comments = dijit.byId("commentsTxt");
        if (comments) {
          comments.isInRange = atg.commerce.csr.cart.isInRange;
        }

        var theButton = dojo.byId("checkoutFooterNextButton");
        if (theButton != null) {
          theButton.focus();
        }

        // wrap long lines in Adjustment Comments
        dojo.query("div.atg_commerce_csr_orderAdjustmentComment").forEach(function(commentEl){
            commentEl.innerHTML = commentEl.innerHTML.replace(/([^\s]{10})(?=[^\s]{2,})/g,"$1<wbr/>");
        });
        
        // validation of adjustmentsForm.
        validateAdjustmentsForm();
        atg.service.form.watchInputs('adjustmentsForm', validateAdjustmentsForm);
        
      });
      _container_.onUnloadDeferred.addCallback(function() {
        atg.service.form.unWatchInputs('adjustmentsForm');
      });
      
      <c:if test="${!empty isShowPromotionsBrowser && isShowPromotionsBrowser == true}"
        ><dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources"
          ><dsp:droplet name="SharingSitesDroplet"
          ><dsp:oparam name="output"
            ><dsp:getvalueof var="sites" param="sites"
          /></dsp:oparam
        ></dsp:droplet
        >atg.commerce.csr.promotion.sites = [];
        atg.commerce.csr.promotion.sites.push({value:'',name:'<fmt:message key="promotion.allSites"/>'});
        <c:forEach var="siteConfig" items="${sites}"
          ><dsp:tomap var="siteConfigMap" value="${siteConfig}"
          />atg.commerce.csr.promotion.sites.push({value:'${siteConfigMap.id}',name:'${siteConfigMap.name}'});
        </c:forEach
        ><c:remove var="isShowPromotionsBrowser"
      /></dsp:layeredBundle></c:if>
    </script>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
    Exception ee = (Exception) pageContext.getAttribute("exception");
    ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/cart.jsp#3 $$Change: 1179550 $--%>
