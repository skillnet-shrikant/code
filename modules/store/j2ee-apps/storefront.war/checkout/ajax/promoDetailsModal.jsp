<%--
  - File Name: promoDetailsModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal contains details about the promo that was applied to the order
  --%>

<dsp:importbean bean="/com/mff/commerce/order/purchase/MFFPaymentStackDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<layout:ajax>
	<jsp:attribute name="section">checkout</jsp:attribute>
	<jsp:attribute name="pageType">promoDetailsModal</jsp:attribute>
	<jsp:body>

	<dsp:getvalueof var="isOrderConfirmation" param="isOrderConfirmation" />
	<dsp:param name="lastOrder" bean="ShoppingCart.last" />
	<dsp:param name="currentOrder" bean="ShoppingCart.current" />

		<div class="promo-details-modal">

			<div class="modal-header">
				<h2>APPLIED DISCOUNTS</h2>
			</div>
			<div class="modal-body">
				<dsp:droplet name="MFFPaymentStackDroplet">
					<c:choose>
						<c:when test="${isOrderConfirmation}">
							<dsp:param name="order" param="lastOrder"/>
						</c:when>
						<c:otherwise>
							<dsp:param name="order" param="currentOrder"/>
						</c:otherwise>
					</c:choose>
					<dsp:param name="activePromotions" bean="Profile.activePromotions" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="globalDiscountMap" param="globalDiscountAmount" vartype="java.util.Map" />
						<dsp:getvalueof var="shippingPromos" param="shippingPromos" />
						<dsp:getvalueof var="shippingPromoToDiscMap" param="shippingPromoToDiscMap" vartype="java.util.Map" />
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="globalPromotions"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="couponCode" param="key" idtype="java.lang.String" />
								<div class="total-row">
									<div class="total-label">
										${couponCode}
									</div>
									<div class="total-amount savings">
										- <dsp:valueof value="${globalDiscountMap[couponCode]}" converter="currency"/>
									</div>
								</div>
								<div class="total-row total">
									<div class="total-label">
										<%-- BZ 2393 - Removing the tooltip --%>
										<dsp:valueof param="element"/><span data-tooltip class="" title="<dsp:valueof param="element"/>"><span class="sr-only"><dsp:valueof param="element"/></span></span>
									</div>
								</div>
							</dsp:oparam>
						</dsp:droplet>
						
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="orderAppliedPromotions" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="discountType" param="element.discountType"/>
								<dsp:getvalueof var="couponDiscount" param="element.discountAmount" idtype="java.lang.Double" />
									<c:if test="${not empty couponDiscount && couponDiscount > 0 }">
										<div class="total-row">
											<div class="total-label">
												<dsp:valueof param="element.promoName"/>
											</div>
											<div class="total-amount savings">
												- <dsp:valueof param="element.discountAmount" converter="currency"/>
											</div>
										</div>
										<div class="total-row total">
											<div class="total-label">
												<%-- BZ 2393 - Removing the tooltip --%>
												<dsp:valueof param="element.promoShortDesc"/><span data-tooltip class="" title="<dsp:valueof param="element"/>"><span class="sr-only"><dsp:valueof param="element"/></span></span><br>
												with code <strong><dsp:valueof param="element.couponCode"/></strong>
											</div>
										</div>
									</c:if>
							</dsp:oparam>
						</dsp:droplet>

						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="shippingPromos" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="shipPromoName" param="key" idtype="java.lang.String" />
								<dsp:getvalueof var="shipPromoDesc" param="element" idtype="java.lang.String" />

								<div class="total-row">
									<div class="total-label">
										${shipPromoName}
									</div>
									<div class="total-amount savings">
									- <dsp:valueof value="${shippingPromoToDiscMap[shipPromoName]}" converter="currency"/>
									</div>
								</div>
								<div class="total-row total">
									<div class="total-label">
										<%-- BZ 2393 - Removing the tooltip --%>
										${shipPromoDesc}<span data-tooltip class="" title="<dsp:valueof param="element"/>"><span class="sr-only"><dsp:valueof param="element"/></span></span>
									</div>
								</div>
							</dsp:oparam>
						</dsp:droplet>
					
					</dsp:oparam>
				</dsp:droplet>

			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
