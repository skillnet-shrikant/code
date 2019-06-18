<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CheckoutManager" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>

	<%-- Page Variables --%>
	<dsp:getvalueof param="isCheckout" var="isCheckout" />
	<c:choose>
		<c:when test="${isCheckout}">
			<dsp:getvalueof var="successUrl" value="${contextPath}/checkout/json/updateCartItemSuccess.jsp?isCheckout=true" />
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="successUrl" value="${contextPath}/checkout/json/updateCartItemSuccess.jsp" />
		</c:otherwise>
	</c:choose>

	<%-- get applied coupons array --%>
	<c:set var="formClass" value="" scope="request" />
	<c:set var="messageClass" value="hide" scope="request" />
	<dsp:droplet name="/com/mff/commerce/order/purchase/OrderAppliedPromotionCouponsDroplet">
		<dsp:param name="order" bean="ShoppingCart.current" />
		<dsp:param name="activePromotions" bean="Profile.activePromotions" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="activePromotions" param="result" scope="request" />
			<dsp:getvalueof var="couponDiscount" param="couponDiscount" idtype="java.lang.Double"/>
			<dsp:getvalueof var="hasItemShipDiscount" param="hasItemShipDiscount" idtype="java.lang.Boolean"/>
			
			<dsp:test var="promoTest" value="${activePromotions}" />
			<%--2564: Hide the promo form when there a free item shipping --%>
			<c:if test="${couponDiscount gt 0 or hasItemShipDiscount}">
				<c:set var="formClass" value="hide" scope="request" />
				<c:set var="messageClass" value="" scope="request" />
			</c:if>
		</dsp:oparam>
	</dsp:droplet>

	<div class="promo-code" data-promocode>

		<dsp:form formid="promo-remove-form" id="promo-remove-form" name="promo-remove-form" method="post">
			<dsp:input type="hidden" bean="CartModifierFormHandler.fromCheckout" value="${isCheckout}" id="fromCheckout" name="fromCheckout" />
			<dsp:input type="hidden" bean="CartModifierFormHandler.removeCouponSuccessURL" value="${successUrl}" />
			<dsp:input type="hidden" bean="CartModifierFormHandler.removeCouponErrorURL" value="${contextPath}/checkout/json/promoError.jsp" />
			<dsp:input type="submit" bean="CartModifierFormHandler.removeCoupon" id="promo-remove-submit" iclass="hide" value="Remove"/>
		</dsp:form>

		<dsp:form formid="promo-form" id="promo-form" name="promo-form" iclass="promo-form" method="post">
			<div class="field-group">
				<label for="promo-code-field">
					Add promo or coupon code (1 per order)
				</label>
				<div class="promo-form-fields ${formClass}">
					<dsp:input type="text" bean="CartModifierFormHandler.couponCode" id="promo-code-field" name="promo-code-field" iclass="promo-code-field" data-validation="" data-fieldname="Promo Code" placeholder="Promo or coupon code"/>
					<dsp:input type="hidden" bean="CartModifierFormHandler.fromCheckout" value="${isCheckout}" id="fromCheckout" name="fromCheckout" />
					<dsp:input type="hidden" bean="CartModifierFormHandler.applyCouponSuccessURL" value="${successUrl}" id="apply-success" name="apply-success" />
					<dsp:input type="hidden" bean="CartModifierFormHandler.applyCouponErrorURL" value="${contextPath}/checkout/json/promoError.jsp" id="apply-error" name="apply-error"/>
					<dsp:input type="submit" bean="CartModifierFormHandler.applyCoupon" id="promo-code-submit" name="promo-code-submit" iclass="button primary apply-promo-btn" value="Apply"/>
				</div>
			</div>
		</dsp:form>
		<div class="promo-code-msg">
			<%-- Display any coupon messages here --%>
			<dsp:getvalueof var="couponMessages" bean="CheckoutManager.couponMessages" />
			<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
				<dsp:param name="value" value="${couponMessages}"/>
				<dsp:oparam name="false">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" value="${couponMessages}"/>
						<dsp:param name="elementName" value="message"/>
						<dsp:oparam name="output">
							<div class="alert-box info">
								<p><dsp:valueof param="message" /></p><span class="icon icon-close" role="button" tabindex="0"><span class="sr-only">Close</span></span>
							</div>
						</dsp:oparam>
					</dsp:droplet>
					<%-- Clear the message in session after display --%>
					<dsp:setvalue bean="CheckoutManager.clearCouponMessages" value="true" />
					
				</dsp:oparam>
			</dsp:droplet>
		</div>
		<div class="promo-applied-area">

			<%-- applied coupons --%>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" value="${activePromotions}" />
				<dsp:oparam name="output">
				
					<dsp:getvalueof var="couponCode" param="element.couponCode" idtype="java.lang.String" />
					<dsp:getvalueof var="couponPromoName" param="element.promoName" idtype="java.lang.String" />
					<dsp:getvalueof var="couponPromoShortDesc" param="element.promoShortDesc" idtype="java.lang.String" />
					<dsp:getvalueof var="couponDiscount" param="element.discountAmount" idtype="java.lang.Double" />
					<dsp:getvalueof var="itemShipping" param="element.itemShipping" idtype="java.lang.Boolean" />
					<%-- Any markup changes to this section need to be applied to the appliedPromotions mustache template as well --%>
					<%-- 2564: Free item ship coupons have no $ adjustment. But if applied, display the coupon --%>
					<c:if test="${(not empty couponDiscount && couponDiscount > 0) or itemShipping}">  
						<div class="promo-applied">
							<span class="coupon-code">${couponCode}</span>
							<c:url value="${contextPath}/checkout/ajax/lineItemPromoDetailsModal.jsp" var="url">
								<c:param name="p" value="${couponPromoName}" />
								<c:param name="d" value="${couponPromoShortDesc}" />
							</c:url>
							<a class="view-details modal-trigger" href="${url}" data-target="promo-details-modal" data-size="small">view details</a>
							<a href="#" class="remove-link"><span class="icon icon-remove"></span>remove</a>
						</div>
					</c:if>  
				</dsp:oparam>
			</dsp:droplet>

		</div>
	</div>

</dsp:page>
