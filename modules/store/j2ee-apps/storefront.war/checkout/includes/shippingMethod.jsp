<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/pricing/AvailableShippingMethods"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/ExpressCheckoutFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler" />
	<dsp:importbean bean="/com/mff/commerce/order/purchase/SaturdayDeliveryDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>

	<div class="shipping-method-radios">
		<h3>Shipping Method</h3><a href="${contextPath}/checkout/ajax/shippingFAQModal.jsp" class="modal-trigger shipping-faq-link" data-target="shipping-faq-modal">Shipping FAQs</a>
		<p class="note">Orders placed after 12pm (Central Time) will be processed the next day.</p>
		<div class="field-group">

			<dsp:droplet name="AvailableShippingMethods">
				<dsp:param bean="ShippingGroupFormHandler.shippingGroup" name="shippingGroup"/>
				<dsp:param bean="ShoppingCart.current" name="order"/>
				<dsp:oparam name="output">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="availableShippingMethods" />
						<dsp:oparam name="output">
							<dsp:getvalueof param="key" var="shipMethod"/>
							<dsp:getvalueof param="element.shippingMethodName" var="shipMethodDisplayName"/>
							<dsp:getvalueof param="element.shippingMethodNote" var="shipMethodNote"/>
							<dsp:getvalueof param="index" var="index"/>
							<c:set var="isChecked" value="false" scope="request" />
							<c:if test="${index == 0}">
								<c:set var="isChecked" value="true" scope="request" />
								<c:set var="defaultShippingMethod" value="${shipMethod}" scope="request" />
							</c:if>
							<div class="radio">
								<label for="shipping-${index}">
									<dsp:input type="radio" value="${shipMethod}" id="shipping-${index}" bean="ShippingGroupFormHandler.shippingMethod" name="shipping" checked="${isChecked}" />
									<span id="method-name-${index}"><c:out value="${shipMethodDisplayName}"/></span> - <span id="method-amount-${index}"><dsp:valueof param="element.shippingMethodAmount" converter="currency"/></span>
								</label>
							</div>
							<c:if test="${not empty shipMethodNote}">
								<p class="ship-method-note"><dsp:valueof param="element.shippingMethodNote"/></p>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
			
			<c:set var="satDayDeliveryClass" value="hide" scope="request" />
			<dsp:droplet name="SaturdayDeliveryDroplet">
				<dsp:param name="shippingGroup" bean="ShippingGroupFormHandler.shippingGroup" />
				<dsp:param name="shippingMethod" value="${defaultShippingMethod}" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="isSatDayDelivery" param="isSatDayDelivery" vartype="boolean"/>
					<c:if test="${isSatDayDelivery}">
						<c:set var="satDayDeliveryClass" value="" scope="request" />
					</c:if>
				</dsp:oparam>
			</dsp:droplet>

			<div class="checkbox saturday-delivery ${satDayDeliveryClass}">
				<label for="saturday-delivery">
					<dsp:input type="checkbox" bean="ShippingGroupFormHandler.saturdayDelivery" id="saturday-delivery" name="saturday-delivery" /> Apply Saturday Delivery
				</label>
			</div>

		</div>
	</div>

</dsp:page>
