<%--
- File Name: giftCardSuccess.jsp
- Author(s): jjensen
- Copyright Notice:
- Description: Creates a json success message when a user adds a gift card to their order
- Parameters:
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CheckoutManager" />
	<dsp:importbean bean="/com/mff/commerce/order/purchase/MFFMerchandiseTotalDroplet"/>
	<dsp:importbean bean="/com/mff/commerce/order/purchase/MFFPaymentStackDroplet"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	
	<%-- Page Variables --%>
	<dsp:getvalueof var="isBopisOrder" bean="ShoppingCart.current.bopisOrder" />
	<dsp:setvalue param="order" beanvalue="ShoppingCart.current"/>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" bean="CheckoutManager.orderRequiresCreditCard"/>
		<dsp:oparam name="false">
			<c:set var="isOrderCovered" value="true" />
		</dsp:oparam>
		<dsp:oparam name="true">
			<c:set var="isOrderCovered" value="false" />
		</dsp:oparam>
	</dsp:droplet>

	<dsp:getvalueof bean="Site.isEnableMillsMoney" var="millsMoney"/>
	
	<c:set var="gcHeaderText">Gift Cards</c:set>
	<c:set var="gcAppliedHeaderText">Applied Gift Cards</c:set>
	<c:if test="${millsMoney}">
		<c:set var="gcHeaderText">Gift Cards / Bonus Bucks</c:set>
		<c:set var="gcAppliedHeaderText">Applied Gift Cards / Bonus Bucks</c:set>
	</c:if>
	
	<%-- reprice order --%>
	<dsp:droplet name="/atg/commerce/order/purchase/RepriceOrderDroplet">
		<dsp:param value="ORDER_TOTAL" name="pricingOp"/>
	</dsp:droplet>

	<json:object>

		<json:property name="success">true</json:property>
		<json:property name="orderCovered">${isOrderCovered}</json:property>
		<json:property name="gcHeaderText">${gcHeaderText}</json:property>
		<%-- applied gift cards --%>
		<json:array name="appliedGiftCards">
			<json:property name="gcAppliedHeaderText">${gcAppliedHeaderText}</json:property>
			<dsp:droplet name="ForEach">
			<dsp:param name="array" bean="ShoppingCart.current.paymentGroups"/>
			<dsp:param name="elementName" value="paymentGroup"/>
			<dsp:oparam name="output">
			<dsp:getvalueof var="paymentClassType" param="paymentGroup.paymentGroupClassType" />
			<c:if test="${paymentClassType == 'giftCard'}">
				<json:object>
					<json:property name="number"><dsp:valueof param="paymentGroup.cardNumber"/></json:property>
					<json:property name="amount"><dsp:valueof param="paymentGroup.amount" converter="currency" /></json:property>
				</json:object>
			</c:if>
			</dsp:oparam>
			</dsp:droplet>
		</json:array>

		<%-- merchandise total and total savings on order--%>
		<dsp:droplet name="MFFMerchandiseTotalDroplet">
			<dsp:param name="order" bean="ShoppingCart.current"/>
			<dsp:oparam name="output">
				<json:property name="orderSubtotal">
					<dsp:valueof param="merchandiseTotal" converter="currency"/>
				</json:property>
				<json:property name="totalSavings">
					<dsp:valueof param="totalSavings" converter="currency"/>
				</json:property>
			</dsp:oparam>
		</dsp:droplet>

		<dsp:droplet name="MFFPaymentStackDroplet">
			<dsp:param name="order" bean="ShoppingCart.current"/>
			<dsp:param name="activePromotions" bean="Profile.activePromotions" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="globalDiscountMap" param="globalDiscountAmount" idtype="java.util.Map" />
				<dsp:getvalueof var="shippingPromos" param="shippingPromos" />
				<%-- discounts --%>
				<json:array name="orderDiscount">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="globalPromotions"/>
						<dsp:oparam name="output">
							<json:object>
								<json:property name="discountType">
									global
								</json:property>
								<json:property name="promoDispName">
									<dsp:valueof param="key"/>
									<dsp:getvalueof var="promo" param="key"/>
								</json:property>
								<json:property name="couponDetails">
									<dsp:valueof param="element"/>
								</json:property>
								<json:property name="discountAmount">
									<dsp:valueof value="${globalDiscountMap[promo]}" converter="currency"/>
								</json:property>
							</json:object>
						</dsp:oparam>
					</dsp:droplet>
		
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="orderAppliedPromotions" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="discountType" param="element.discountType"/>
							<dsp:getvalueof var="couponDiscount" param="element.discountAmount" idtype="java.lang.Double" />
							<c:if test="${not empty couponDiscount && couponDiscount > 0.0 }">
								<c:choose>
									<c:when test="${discountType ne 'shipping'}">
										<json:object>
											<json:property name="discountType">
												coupon
											</json:property>
											<json:property name="couponCode">
												<dsp:valueof param="element.couponCode"/>
											</json:property>
											<json:property name="couponPromoShortDesc">
												<dsp:valueof param="element.promoShortDesc"/>
											</json:property>
											<json:property name="promoDispName">
												<dsp:valueof param="element.promoName"/>
											</json:property>
											<json:property name="couponDetails">
												<dsp:valueof param="element.description"/>
											</json:property>
											<json:property name="discountAmount">
												<dsp:valueof param="element.discountAmount" converter="currency"/>
											</json:property>
										</json:object>
									</c:when>
									<c:otherwise>
										<json:object>
											<json:property name="discountType">
												shippingcoupon
											</json:property>
										</json:object>
									</c:otherwise>
								</c:choose>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</json:array>

			<%-- applied gift cards --%>
			<dsp:getvalueof var="gcTotal" param="order.priceInfo.giftCardPaymentTotal" />
			<c:if test="${gcTotal gt 0}">
				<json:property name="giftCardTotal">
					<dsp:valueof param="order.priceInfo.giftCardPaymentTotal" converter="currency"/>
				</json:property>
				<json:property name="gcHeaderText">
					${gcHeaderText}
				</json:property>
			</c:if>

			<%-- shipping --%>
			<%--2393 & 2505. Do not show FREE for shipping when it is a BOPIS order. $0.00 to be shown instead --%>
			
			<dsp:getvalueof var="orderShipping" param="order.priceInfo.shipping" />
			<c:choose>
				<c:when test="${!isBopisOrder && orderShipping le 0 }">
					<json:property name="isFreeShipping">
						true
					</json:property>
					<json:property name="orderShipping">
						FREE
					</json:property>
				</c:when>
				<c:otherwise>
					<json:property name="orderShipping">
						<dsp:valueof param="order.priceInfo.shipping" converter="currency"/>
					</json:property>
				</c:otherwise>
			</c:choose>
		
			<json:array name="orderShippingPromos">
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="shippingPromos" />
					<dsp:oparam name="output">
					<dsp:getvalueof var="shipPromoName" param="key" idtype="java.lang.String" />
						<json:object>
							<json:property name="shipPromoName">
								${shipPromoName}
							</json:property>
						</json:object>
					</dsp:oparam>
				</dsp:droplet>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="orderAppliedPromotions" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="discountType" param="element.discountType"/>
							<c:if test="${discountType eq 'shipping'}">
								<json:object>
									<json:property name="shipPromoName">
										<dsp:valueof param="element.promoName"/>
									</json:property>
								</json:object>
							</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</json:array>

			<json:array name="appliedCouponPromos">
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="orderAppliedPromotions" />
					<dsp:oparam name="output">
						<json:object>
							<json:property name="discountType">
								coupon
							</json:property>
							<json:property name="couponCode">
								<dsp:valueof param="element.couponCode"/>
							</json:property>
							<json:property name="couponPromoShortDesc">
								<dsp:valueof param="element.promoShortDesc"/>
							</json:property>
							<json:property name="couponDetails">
								<dsp:valueof param="element.description"/>
							</json:property>
						</json:object>
					</dsp:oparam>
				</dsp:droplet>
			</json:array>
	
			</dsp:oparam>
		</dsp:droplet>
		<%-- tax --%>
		<json:property name="orderTax">
			<dsp:valueof param="order.priceInfo.tax" converter="currency"/>
		</json:property>

		<%-- subtotal --%>
		<json:property name="orderTotal">
			<dsp:valueof param="order.priceInfo.orderChargeAmount" converter="currency"/>
		</json:property>

	</json:object>

</dsp:page>