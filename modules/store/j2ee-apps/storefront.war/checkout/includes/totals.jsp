<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/com/mff/commerce/order/purchase/MFFPaymentStackDroplet"/>
	<dsp:importbean bean="/com/mff/commerce/order/purchase/MFFMerchandiseTotalDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/multisite/Site" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="isCart" param="isCart" />
	<dsp:getvalueof var="isOrderConfirmation" param="isOrderConfirmation" />
	
	<dsp:getvalueof var="isCheckout" param="isCheckout" />
	<dsp:getvalueof var="taxExemptions" vartype="java.lang.Object" bean="Profile.taxExemptions"/>
	<dsp:getvalueof var="isLoggedIn" vartype="java.lang.Boolean" bean="Profile.hardLoggedIn" />
	<dsp:getvalueof var="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	<dsp:getvalueof bean="Site.isEnableMillsMoney" var="millsMoney"/>
	
	<c:set var="gcHeaderText">Gift Cards</c:set>
	<c:if test="${millsMoney}">
		<c:set var="gcHeaderText">Gift Card / Bonus Bucks</c:set>
	</c:if>
	<c:choose>
		<c:when test="${isCart or isCheckout}">
			<dsp:setvalue param="order" beanvalue="ShoppingCart.current"/>
		</c:when>
		<c:otherwise>
			<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
				<dsp:param name="value" param="order"/>
				<dsp:oparam name="true">
					<dsp:setvalue param="order" beanvalue="ShoppingCart.last"/>
				</dsp:oparam>
			</dsp:droplet>
		</c:otherwise>
	</c:choose>

	<div class="totals">
		<div class="total-row subtotal">
			<div class="total-label">Merchandise Total :</div>
			<div class="total-amount">
				<dsp:droplet name="MFFMerchandiseTotalDroplet">
					<dsp:param name="order" param="order"/>
					<dsp:oparam name="output">
						<dsp:valueof param="merchandiseTotal" converter="currency"/>
						<dsp:getvalueof var="totalSavings" param="totalSavings" scope="request"/>
					</dsp:oparam>
				</dsp:droplet>
			</div>
		</div>
		
		<%-- order level promotions --%>
		<%-- only show order discounts when order level discounts are applied to order --%>
		<dsp:getvalueof param="order.priceInfo.discountAmount" var="discountAmount"/>
		<dsp:droplet name="MFFPaymentStackDroplet">
			<dsp:param name="order" param="order" />
			<dsp:param name="activePromotions" bean="Profile.activePromotions" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="isGlobalPromoExists" param="isGlobalPromoExists" scope="request"/>
				<%-- Any markup changes to this section need to be applied to the appliedPromotions mustache template as well --%>
				<c:if test="${totalSavings gt 0 && isGlobalPromoExists eq 'true'}">
					<div class="total-row subtotal">
						<div class="total-label">
							Discounts <a class="view-details modal-trigger"
								href="${contextPath}/checkout/ajax/promoDetailsModal.jsp?isOrderConfirmation=${isOrderConfirmation}"
								data-target="promo-details-modal" data-size="small"><span
								class="icon icon-info"></span></a> :
						</div>
						<div class="total-amount savings">
							- <dsp:valueof value="${totalSavings}" converter="currency" />
						</div>
					</div>
				</c:if>
				
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="globalPromotions" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="couponCode" param="key" idtype="java.lang.String" />
						<div class="total-row">
							<div class="total-label">
								<span class="total-promo">${couponCode}</span>
							</div>
						</div>
					</dsp:oparam>
				</dsp:droplet>
				
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="orderAppliedPromotions" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="discountType" param="element.discountType" />
						<dsp:getvalueof var="couponDiscount" param="element.discountAmount" idtype="java.lang.Double" />
						<c:if test="${not empty couponDiscount && couponDiscount > 0}">
							<c:if test="${not empty isGlobalPromoExists && isGlobalPromoExists eq 'false'}">
								<div class="total-row subtotal">
									<div class="total-label">
										Discounts <a class="view-details modal-trigger"
											href="${contextPath}/checkout/ajax/promoDetailsModal.jsp?isOrderConfirmation=${isOrderConfirmation}"
											data-target="promo-details-modal" data-size="small"><span
											class="icon icon-info"></span></a> :
									</div>
									<div class="total-amount savings">
										-
										<dsp:valueof value="${totalSavings}" converter="currency" />
									</div>
								</div>
							</c:if>
							
							<c:choose>
								<c:when test="${discountType ne 'shipping'}">
									<div class="total-row">
										<div class="total-label">
											<span class="total-promo"><dsp:valueof param="element.promoName" /></span>
										</div>
									</div>
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="shippingCouponDiscount" param="element.discountAmount" idtype="java.lang.Double" />
									<dsp:getvalueof var="shippingCouponName" param="element.promoName" />
								</c:otherwise>
							</c:choose>
							
						</c:if>
					</dsp:oparam>
				</dsp:droplet>

				<div class="total-row shipping">
					<div class="total-label">
						<c:choose>
							<c:when test="${isCart}">
								Estimated Shipping* :
							</c:when>
							<c:otherwise>
								Shipping :
							</c:otherwise>
						</c:choose>

						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="shippingPromos" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="shipPromoName" param="key"
									idtype="java.lang.String" />
								<div>
									<span class="total-promo">${shipPromoName}</span>
								</div>
							</dsp:oparam>
						</dsp:droplet>
							
						<c:if test="${not empty shippingCouponDiscount && shippingCouponDiscount > 0 }">
							<div>
								<span class="total-promo">${shippingCouponName}</span>
							</div>
						</c:if>
					</div>

					<%--2393 & 2505. Do not show FREE for shipping when it is a BOPIS order. $0.00 to be shown instead --%>
					<dsp:getvalueof var="isBopisOrder" param="order.bopisOrder" />
					<dsp:getvalueof var="shippingCharge" param="order.priceInfo.shipping" />
					<c:choose>
						<c:when test="${!isBopisOrder && shippingCharge le 0 }">
							<div class="total-amount savings">FREE</div>
						</c:when>
						<c:otherwise>
							<div class="total-amount">
								<dsp:valueof param="order.priceInfo.shipping" converter="currency" />
							</div>
						</c:otherwise>
					</c:choose>
				</div>
				
			</dsp:oparam>
		</dsp:droplet>
				
		<div class="total-row tax">
			<div class="total-label">
				Tax<c:if test="${isCart}">*</c:if> :
			</div>
			<div class="total-amount"><dsp:valueof param="order.priceInfo.tax" converter="currency"/></div>
		</div>
		
		<dsp:getvalueof var="gcTotal" param="order.priceInfo.giftCardPaymentTotal" />
		<c:if test="${not isCart and gcTotal gt 0}">
			<div class="total-row gift-cards">
				<div class="total-label">${gcHeaderText} :</div>
				<div class="total-amount">- <dsp:valueof param="order.priceInfo.giftCardPaymentTotal" converter="currency"/></div>
			</div>
		</c:if>

		<div class="total-row total">
			<c:choose>
				<c:when test="${isCart}">
					<div class="total-label">
						Estimated Total :
					</div>
					<div class="total-amount"><dsp:valueof param="order.priceInfo.total" converter="currency"/></div>
				</c:when>
				<c:otherwise>
					<div class="total-label">
						Total :
					</div>
					<div class="total-amount"><dsp:valueof param="order.priceInfo.orderChargeAmount" converter="currency"/></div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<%-- note about tax and tax exemption --%>
	<c:if test="${isCart}">
		<div class="totals-note">
			<p class="note">
				* The total above does not include state sales tax. Shipping charges above are only an estimate.
			</p>
			<%-- authenticated with saved tax exemptions --%>
			<c:if test="${not userIsAuthenticated || empty taxExemptions}">
				<%-- anonymous / soft-logged-in : show tax exempt modal --%>
				<a href="${contextPath}/sitewide/ajax/taxExemptModal.jsp" class="modal-trigger" data-target="tax-exempt-modal" data-size="medium">Are you Tax Exempt?</a>
			</c:if>
		</div>
	</c:if>

</dsp:page>
