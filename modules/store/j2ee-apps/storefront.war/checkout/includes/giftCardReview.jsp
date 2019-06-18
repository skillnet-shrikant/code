<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>

	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof bean="Site.isEnableMillsMoney" var="millsMoney"/>
	<c:set var="gcHeaderText">Applied Gift Cards</c:set>
	<c:if test="${millsMoney}">
		<c:set var="gcHeaderText">Applied Gift Cards / Bonus Bucks</c:set>
	</c:if>
	
	<dsp:droplet name="ForEach">
		<dsp:param name="array" bean="ShoppingCart.current.paymentGroups"/>
		<dsp:param name="elementName" value="paymentGroup"/>
		<dsp:oparam name="outputStart">
			<h3>${gcHeaderText}</h3>
			<div class="applied-gift-cards">
		</dsp:oparam>
		<dsp:oparam name="output">
			<dsp:getvalueof var="paymentClassType" param="paymentGroup.paymentGroupClassType" />
			<c:if test="${paymentClassType == 'giftCard'}">
				<dsp:getvalueof var="giftCardId" param="paymentGroup.cardNumber"/>
				<div class="applied-gift-card">
					<div class="gift-card-number-applied">${giftCardId}</div>
					<a href="#" class="gift-card-remove" data-number="${giftCardId}">remove</a>
					<div class="gift-card-amount-applied amount-${giftCardId}"><dsp:valueof param="paymentGroup.amount" converter="currency" /></div>
				</div>
			</c:if>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
			</div>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
