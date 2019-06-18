<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	
	<dsp:getvalueof var="rawSubtotal" param="order.rawSubtotal" />
	<dsp:getvalueof var="merchandiseTotal" param="order.merchandiseTotal" />
	<dsp:getvalueof var="tax" param="order.tax" />
	<dsp:getvalueof var="shipping" param="order.shipping" />

	<dsp:getvalueof bean="Site.isEnableMillsMoney" var="millsMoney"/>
	
	<c:set var="gcHeaderText">Gift Cards</c:set>
	<c:set var="gcAppliedHeaderText">Applied Gift Cards</c:set>
	<c:if test="${millsMoney}">
		<c:set var="gcHeaderText">Gift Cards / Bonus Bucks</c:set>
	</c:if>
							
	<div class="totals">
		<div class="total-row subtotal">
			<div class="total-label">Merchandise Total :</div>
			<div class="total-amount"><dsp:valueof value="${merchandiseTotal}" converter="currency"/></div>
		</div>

		<dsp:getvalueof var="discountAmount" param="order.discountAmount" />
		<c:if test="${not empty discountAmount && discountAmount ne 0.0}">
			<div class="total-row savings">
				<div class="total-label">
						Discounts :
				</div>
				<div class="total-amount">
					- <dsp:valueof param="order.discountAmount" converter="currency"/>
				</div>
			</div>
		</c:if>

		<div class="total-row shipping">
			<div class="total-label">
				Shipping :
			</div>
			<div class="total-amount"><dsp:valueof value="${shipping}" converter="currency"/></div>
		</div>
		<div class="total-row tax">
			<div class="total-label">
				Tax :
			</div>
			<div class="total-amount"><dsp:valueof value="${tax}" converter="currency"/></div>
		</div>
		<dsp:getvalueof var="gcTotal" param="order.giftCardPaymentTotal" />
		<c:if test="${gcTotal gt 0}">
			<div class="total-row tax">
				<div class="total-label">${gcHeaderText} :</div>
				<div class="total-amount">- <dsp:valueof param="order.giftCardPaymentTotal" converter="currency"/></div>
			</div>
		</c:if>
		<div class="total-row total">
			<div class="total-label">
				Total :
			</div>
			<div class="total-amount"><dsp:valueof param="order.orderChargeAmount" converter="currency"/></div>
		</div>
	</div>

</dsp:page>
