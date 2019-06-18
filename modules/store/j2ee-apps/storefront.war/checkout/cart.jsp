<%--
	- File Name: cart.jsp
	- Author(s): KnowledgePath Solutions UX Team
	- Copyright Notice:
	- Description: Main shopping cart page
	- Parameters:
	-
	--%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ProtocolChange"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CheckoutManager"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/ExpressCheckoutFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/ValidateExpressCheckout"/>
	<dsp:importbean bean="/com/mff/account/order/droplet/LTLOrderCheckDroplet"/>
	<dsp:importbean bean="/com/mff/account/order/droplet/HasShippingSurchargeBySkuDroplet"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="bopis" bean="ShoppingCart.current.bopisOrder" />
	<dsp:getvalueof var="bopisStore" bean="ShoppingCart.current.bopisStore" />
	<dsp:getvalueof var="expressCheckout" bean="Profile.expressCheckout" />
	<dsp:getvalueof var="fflOrder" bean="ShoppingCart.current.fflOrder" />
	<dsp:getvalueof var="taxExemptions" vartype="java.lang.Object" bean="Profile.taxExemptions"/>
	<dsp:getvalueof var="cartURL" scope="request" value="${contextPath}/checkout/cart.jsp"/>

	<%-- Authenticated? --%>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param bean="Profile.hardLoggedIn" name="value"/>
		<dsp:oparam name="true">
			<%-- authenticated / continue to checkout --%>
			<c:set var="userIsAuthenticated" value="true" scope="request" />
			<dsp:getvalueof var="nextUrl" scope="request" value="${contextPath}/checkout/checkout.jsp"/>
		</dsp:oparam>
		<dsp:oparam name="false">
			<%-- anonymous / redirect to login page --%>
			<c:set var="userIsAuthenticated" value="false" scope="request" />
			<dsp:getvalueof var="nextUrl" scope="request" value="${contextPath}/checkout/login.jsp"/>
		</dsp:oparam>
	</dsp:droplet>

	<layout:default>
		<jsp:attribute name="pageTitle">Cart Page</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:attribute name="pageType">cart</jsp:attribute>
		<jsp:attribute name="bodyClass">cart</jsp:attribute>

		<jsp:body>

			<dsp:droplet name="ShippingGroupDroplet">
				<dsp:param name="clearShippingInfos" value="true"/>
				<dsp:param name="clearShippingGroups" value="true"/>
				<dsp:param name="shippingGroupTypes" value="hardgoodShippingGroup"/>
				<dsp:param name="initShippingGroups" value="true"/>
				<dsp:param name="initBasedOnOrder" value="true"/>
				<dsp:oparam name="output"/>
			</dsp:droplet>

			<dsp:droplet name="/com/mff/commerce/order/purchase/InitCartPageDroplet">
				<dsp:oparam name="output">
					<dsp:getvalueof var="stockLevelForSkusInOrder" param="stockLevelForSkusInOrder" vartype="java.util.Map" scope="request"/>
					<dsp:getvalueof var="isItemRemovalRequired" param="isItemRemovalRequired" scope="request"/>
					<dsp:getvalueof var="bopisItemsOnly" param="bopisItemsOnly" scope="request"/>
				</dsp:oparam>
			</dsp:droplet>

			<section>
				<div class="section-title">
					<h1><fmt:message key="cart.header"/></h1>
				</div>

				<div class="cart-content">

					<dsp:droplet name="IsEmpty">
						<dsp:param name="value" bean="ShoppingCart.current.commerceItems"/>
						<dsp:oparam name="true">
							<%-- cart empty --%>
							<div class="error-container">
								<div class="error-messages">
									<dsp:include page="/sitewide/includes/errors/formErrors.jsp">
										<dsp:param name="errorType" value="warning"/>
										<dsp:param name="formhandler" bean="CartModifierFormHandler"/>
									</dsp:include>
								</div>
							</div>
							<%@ include file="fragments/cartEmpty.jspf"%>
						</dsp:oparam>
						<dsp:oparam name="false">

							<%-- errors --%>
							<div class="error-container">
								<div class="error-messages">

									<%-- Display cart messages --%>
									<dsp:droplet name="IsEmpty">
										<dsp:param name="value" bean="CheckoutManager.cartMessage"/>
										<dsp:oparam name="false">
											<div class="alert-box warning" role="alert">
												<p><dsp:valueof bean="CheckoutManager.cartMessage" valueishtml="true"/></p>
											</div>
										</dsp:oparam>
									</dsp:droplet>

									<%-- cart errors --%>
									<dsp:include page="/sitewide/includes/errors/formErrors.jsp">
										<dsp:param name="errorType" value="warning"/>
										<dsp:param name="formhandler" bean="CartModifierFormHandler"/>
									</dsp:include>

									<%-- express checkout errors --%>
									<dsp:include page="/sitewide/includes/errors/formErrors.jsp">
										<dsp:param name="formhandler" bean="ExpressCheckoutFormHandler"/>
									</dsp:include>

									<%-- commit order errors --%>
									<dsp:include page="/sitewide/includes/errors/formErrors.jsp">
										<dsp:param name="formhandler" bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
									</dsp:include>

									<%-- hidden form for removing an item --%>
									<dsp:form name="cartRemoveForm" action="${cartURL}" method="post" id="cartRemoveForm" formid="cartRemoveForm">
										<dsp:input type="hidden" id="removeCommerceIds" bean="CartModifierFormHandler.removalCommerceIds" value=""/>
										<dsp:input type="hidden" bean="CartModifierFormHandler.removeItemFromOrderSuccessURL" value="${contextPath}/checkout/json/updateCartItemSuccess.jsp" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.removeItemFromOrderErrorURL" value="${contextPath}/checkout/json/updateCartItemError.jsp" />
										<dsp:input type="hidden" id="removeSubmit" bean="CartModifierFormHandler.removeItemFromOrder" name="removeSubmit" value="submit" />
									</dsp:form>

									<%-- hidden form for moving an item to wish list--%>
									<dsp:form name="cartItemMoveToWishList" action="${cartURL}" method="post" id="cartItemMoveToWishList" formid="cartItemMoveToWishList">
										<dsp:input type="hidden" id="moveItemId" bean="CartModifierFormHandler.removalCommerceIds" value="" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.wishListId" name="wishListId" beanvalue="Profile.wishlist.id" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.addItemToGiftlistSuccessURL" value="${contextPath}/checkout/json/wishListSuccess.jsp" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.addItemToGiftlistErrorURL" value="${contextPath}/checkout/json/wishListError.jsp" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.moveItemToWishlist" id="moveToWishListSubmit" name="moveToWishListSubmit" value="submit" />
									</dsp:form>

									<%-- bopis form --%>
									<dsp:form name="select-bopis-store" id="select-bopis-store" method="post" formid="select-bopis-store">
										<input type="hidden" id="bopis-change-store" value="false" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.chooseStoreSuccessURL" value="${contextPath}/checkout/json/cartSuccess.jsp" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.chooseStoreErrorURL" value="${contextPath}/checkout/json/cartError.jsp" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.bopisStore" id="bopis-store-id" name="bopis-store-id" value="${bopisStore}" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.fromProduct" id="bopis-from-product" name="bopis-from-product" value="false" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.chooseBopisStore" id="choose-this-store" name="choose-this-store" value="Submit" />
									</dsp:form>

									<%-- hidden form for switching to shipMyOrder instead of store pickup--%>
									<dsp:form formid="ship-my-order-form" id="ship-my-order-form" name="ship-my-order-form" action="${contextPath}/checkout/cart.jsp" method="post">
										<dsp:input type="hidden" bean="CartModifierFormHandler.shipMyOrderSuccessURL" value="${contextPath}/sitewide/json/shipMyOrderSuccess.jsp" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.shipMyOrderErrorURL" value="${contextPath}/sitewide/json/shipMyOrderError.jsp" />
										<dsp:input type="hidden" bean="CartModifierFormHandler.shipMyOrder" id="ship-my-order" name="ship-my-order" value="submit" />
									</dsp:form>

								</div>
							</div>

							<%-- BOPIS --%>
							<c:if test="${bopis}">
								<input type="hidden" id="is-bopis-order" value="true" />
								<dsp:droplet name="/com/mff/locator/droplet/StoreLookupDroplet">
									<dsp:param name="id" value="${bopisStore}"/>
									<dsp:param name="elementName" value="store"/>
									<dsp:oparam name="output">
										<div class="bopis-container">
											<div class="bopis-info">
												<div class="card">
													<div class="card-title">Store Pick Up Information</div>
													<div class="card-content">
														<h3><dsp:valueof param="store.city"/>,&nbsp;<dsp:valueof param="store.stateAddress"/></h3>
														<p><dsp:valueof param="store.address1"/></p>
														<p><dsp:valueof param="store.city"/>,&nbsp;<dsp:valueof param="store.stateAddress"/>&nbsp;<dsp:valueof param="store.postalCode"/></p>
														<p><dsp:valueof param="store.phoneNumber" /></p>
													</div>
													<div class="card-links">
														<a href="#" class="disabled change-store">Change Store</a>
														<c:choose>
															<c:when test="${bopisItemsOnly}">
																<a href="ajax/autoRemoveItemModal.jsp?bopisItemsOnly=true" class="disabled auto-remove-item-trigger modal-trigger" data-target="auto-remove-item-modal" data-size="small">Ship My Order Instead</a>
															</c:when>
															<c:when test="${isItemRemovalRequired}">
																<a href="ajax/autoRemoveItemModal.jsp" class="disabled auto-remove-item-trigger modal-trigger" data-target="auto-remove-item-modal" data-size="small">Ship My Order Instead</a>
															</c:when>
															<c:otherwise>
																<a href="#" class="disabled ship-my-order">Ship My Order Instead</a>
															</c:otherwise>
														</c:choose>
													</div>
												</div>
											</div>
										</div>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>

							<%-- cart header --%>
							<div class="order-items-header">
								<div class="order-items-header-detail">Item Details</div>
								<div class="order-items-header-price">Price</div>
								<div class="order-items-header-quantity">Quantity</div>
								<div class="order-items-header-total">Total</div>
							</div>

							<dsp:droplet name="LTLOrderCheckDroplet">
								<dsp:param name="items" bean="ShoppingCart.current.commerceItems"/>
								<dsp:param name="order" bean="ShoppingCart.current"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="isLTLOrder" value="true" />
									<input type="hidden" id="isLTLOrder" value="true" />
									<input type="hidden" id="totalLTLWeight" value="<dsp:valueof param='totalLTLWeight'/>" />
									<input type="hidden" id="rangeLow" value="<dsp:valueof param='rangeLow'/>" />
									<input type="hidden" id="rangeHigh" value="<dsp:valueof param='rangeHigh'/>" />
									<input type="hidden" id="ltlShippingCharges" value="<dsp:valueof param='ltlShippingCharges'/>" />
									<input type="hidden" id="hasSurcharge" value="false" />
									<input type="hidden" id="totalSurcharge" value="0.0" />
								</dsp:oparam>
								<dsp:oparam name="empty">
									<dsp:getvalueof var="isLTLOrder" value="false" />
									<input type="hidden" id="isLTLOrder" value="false" />

									<dsp:droplet name="HasShippingSurchargeBySkuDroplet">
										<dsp:param name="items" bean="ShoppingCart.current.commerceItems"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="hasSurcharge" value="true" />
											<input type="hidden" id="hasSurcharge" value="true" />
											<input type="hidden" id="totalSurcharge" value="<dsp:valueof param='totalSurcharge'/>" />
										</dsp:oparam>
										<dsp:oparam name="empty">
											<dsp:getvalueof var="hasSurcharge" value="false" />
											<input type="hidden" id="hasSurcharge" value="false" />
										</dsp:oparam>
									</dsp:droplet>

								</dsp:oparam>
							</dsp:droplet>
							<dsp:setvalue param="isLTLOrder" value="${isLTLOrder}"/>
							<dsp:setvalue param="hasSurcharge" value="${hasSurcharge}"/>

							<%-- cart items --%>
							<div class="order-items">
								<dsp:form name="cartUpdateForm" action="${cartURL}" method="post" id="cartUpdateForm" formid="cartUpdateForm" role="grid" aria-readonly="true">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" bean="ShoppingCart.current.commerceItems"/>
										<dsp:param name="elementName" value="commerceItem"/>
										<dsp:param name="sortProperties" value="+minimumAge"/>
										<dsp:oparam name="outputStart">
											<c:set var="signatureRequired" value="0" scope="request" />
											<c:set var="longLight" value="0" scope="request" />
											<c:set var="isOversize" value="0" scope="request" />
										</dsp:oparam>
										<dsp:oparam name="output">

											<%-- 2414 related changes
											GWP sku is automatically added to the cart when the qualifier is met by the order
											User can also added the same SKU manually. These two, behind the scenes, are the same
											commerce item unless the GWP is a gift card. But on the card, the gift & manually added items
											should be displayed as separate lines.. with the manual one having edit/remove actions
											--%>
											<dsp:getvalueof var="giftCard" param="commerceItem.giftCard" />
											<dsp:getvalueof var="gwp" param="commerceItem.gwp" />
											<dsp:getvalueof var="totalQty" param="commerceItem.quantity" />
											<c:choose>
												<%-- if gift, then determine the manual qty & gift qty and display on separate lines --%>
												<c:when test="${gwp}">
													<dsp:getvalueof var="totalQty" param="commerceItem.quantity" />
													<c:set var="freeQty" value="0" />
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="commerceItem.priceInfo.adjustments"/>
														<dsp:param name="elementName" value="adjustment"/>
														<dsp:oparam name="output">
															<dsp:getvalueof var="adjDesc" param="adjustment.adjustmentDescription" />
															<c:if test="${adjDesc eq 'Item Discount'}">
																<dsp:getvalueof var="tmpFreeQty" param="adjustment.quantityAdjusted" />
																<c:set var="freeQty" value="${tmpFreeQty+freeQty}" />
															</c:if>
														</dsp:oparam>
													</dsp:droplet>
													<c:choose>
														<%-- cart has auto-added gift sku and manually added sku --%>
														<c:when test="${(totalQty - freeQty) > 0 }">
															<dsp:param name="displayQty" value="${freeQty}" />
															<dsp:param name="freeGift" value="true" />
															<dsp:param name="justGift" value="false" />
															<dsp:param name="pricingModel" param="adjustment.pricingModel" />
															<%-- changed to dsp include to avoid size limit errors from static includes --%>
															<dsp:include page="includes/cartOrderItem.jsp" />

															<dsp:param name="displayQty" value="${totalQty - freeQty}" />
															<dsp:param name="freeGift" value="false" />
															<dsp:param name="justGift" value="false" />

															<%-- changed to dsp include to avoid size limit errors from static includes --%>
															<dsp:include page="includes/cartOrderItem.jsp" />
														</c:when>
														<c:otherwise>
															<dsp:param name="displayQty" value="${totalQty}" />
															<dsp:param name="freeGift" value="true" />
															<dsp:param name="justGift" value="true" />
															<dsp:param name="pricingModel" param="adjustment.pricingModel" />

															<%-- changed to dsp include to avoid size limit errors from static includes --%>
															<dsp:include page="includes/cartOrderItem.jsp" />
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<%-- item is not a gift --%>
													<%-- changed to dsp include to avoid size limit errors from static includes --%>
													<dsp:param name="displayQty" value="${totalQty}" />
													<dsp:param name="freeGift" value="false" />
													<dsp:param name="justGift" value="false" />
													<dsp:include page="includes/cartOrderItem.jsp" />

												</c:otherwise>
											</c:choose>

										</dsp:oparam>
										<dsp:oparam name="outputEnd">
											<input type="hidden" id="signatureRequired" value="${signatureRequired}" />
											<input type="hidden" id="longLight" value="${longLight}" />
											<input type="hidden" id="isOversize" value="${isOversize}" />
										</dsp:oparam>
									</dsp:droplet>
									<dsp:input bean="CartModifierFormHandler.setOrderSuccessURL" type="hidden" id="" value="${contextPath}/checkout/json/updateCartItemSuccess.jsp"/>
									<dsp:input bean="CartModifierFormHandler.setOrderErrorURL" type="hidden"  id="" value="${contextPath}/checkout/json/updateCartItemError.jsp"/>
									<dsp:input bean="CartModifierFormHandler.setOrderByCommerceId" type="hidden" value="Update" />
								</dsp:form>
							</div>

							<%-- promo form / financial stack --%>
							<div class="promo-and-totals">
								<div class="promo-code-container">
									<jsp:include page="/checkout/includes/promoCode.jsp" />
								</div>
								<div class="totals-container">

									<dsp:include page="/checkout/includes/totals.jsp">
										<dsp:param name="isCart" value="true" />
									</dsp:include>

									<dsp:getvalueof var="taxExmpOnOrder" bean="ShoppingCart.current.taxExemptionName"/>

									<c:if test="${userIsAuthenticated && not empty taxExemptions}">
										<dsp:form formid="tax-exemption-select-form" id="tax-exemption-select-form" name="tax-exemption-select-form" action="${requestURL}" method="post">
											<label for="tax-exemption">Tax Exemption</label>
											<dsp:select bean="CartModifierFormHandler.taxExempSelected" id="tax-exemption"  data-fieldname="Tax Exemption">
												<dsp:option value="">Please Select</dsp:option>
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" value="${taxExemptions}"/>
													<dsp:oparam name="output">
														<dsp:getvalueof var="code" param="element.classificationCode" />
														<dsp:getvalueof var="displayName" param="element.nickName"/>
														<c:choose>
															<c:when test="${code eq taxExmpOnOrder}">
																<dsp:option selected="true" value="${code}">${displayName}</dsp:option>
															</c:when>
															<c:otherwise>
																<dsp:option value="${code}">${displayName}</dsp:option>
															</c:otherwise>
														</c:choose>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:select>
											<dsp:input type="submit" bean="CartModifierFormHandler.applyTaxExemption" name="applyTaxExemption" class="hide apply-tax-exemption" value="Apply Tax Exemption" />
											<dsp:input type="hidden" bean="CartModifierFormHandler.taxExemptionErrorURL" name="taxExemptionErrorURL" value="${contextPath}/checkout/json/taxExemptionError.jsp"/>
											<dsp:input type="hidden" bean="CartModifierFormHandler.taxExemptionSuccessURL" name="taxExemptionSuccessURL"  value="${contextPath}/checkout/json/taxExemptionSuccess.jsp"/>
										</dsp:form>
									</c:if>

								</div>
							</div>

							<%-- cart actions --%>
							<div class="order-item-actions">
								<div class="cart-buttons">
									<dsp:form name="cartForm" action="${nextUrl}" method="post" formid="cartForm" id="cartForm" class="checkout-btn-container">
										<dsp:input type="hidden" bean="CartModifierFormHandler.checkForChangedQuantity" name="checkForChangedQuantity" value="false"/>
										<dsp:input type="hidden" bean="CartModifierFormHandler.moveToPurchaseInfoSuccessURL" name="moveToPurchaseInfoSuccessURL" id="moveToPurchaseInfoSuccessURL" value="${nextUrl}"/>
										<dsp:input type="hidden" bean="CartModifierFormHandler.moveToPurchaseInfoErrorURL" name="moveToPurchaseInfoErrorURL" id="moveToPurchaseInfoErrorURL" value="${cartURL}"/>
										<dsp:input type="submit" class="button primary to-checkout-btn" bean="CartModifierFormHandler.moveToPurchaseInfo" name="moveToPurchaseInfo" id="moveToPurchaseInfo" value="Proceed to checkout" />
									</dsp:form>
									<dsp:droplet name="ValidateExpressCheckout">
										<dsp:oparam name="output">
											<dsp:getvalueof var="isExpressCheckout" param="isExpressCheckout"/>
											<c:if test="${isExpressCheckout}">
												<dsp:form name="expressCheckoutForm" action="${nextUrl}" method="post" formid="expressCheckoutForm" id="expressCheckoutForm" class="checkout-btn-container">
													<dsp:input type="hidden" bean="ExpressCheckoutFormHandler.expressCheckoutSuccessURL" name="expressCheckoutSuccessURL" id="expressCheckoutSuccessURL" value="${nextUrl}"/>
													<dsp:input type="hidden" bean="ExpressCheckoutFormHandler.expressCheckoutErrorURL" name="expressCheckoutErrorURL" id="expressCheckoutErrorURL" value="${cartURL}"/>
													<dsp:input type="submit" class="button tertiary express-checkout-btn" bean="ExpressCheckoutFormHandler.expressCheckout" name="expressCheckout" id="expressCheckout" value="Express Checkout" />
												</dsp:form>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>
									<a href="${contextPath}/" class="button secondary keep-shopping-btn">Keep shopping</a>
								</div>
							</div>

						</dsp:oparam>
					</dsp:droplet>

					<%-- clear cart messages in session after display --%>
					<dsp:setvalue bean="CheckoutManager.cartMessage" value="" />

				</div>
			</section>

		</jsp:body>
	</layout:default>

</dsp:page>
