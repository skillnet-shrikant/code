<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />

	<dsp:getvalueof var="fflOrder" bean="ShoppingCart.current.fflOrder" />
	<dsp:getvalueof var="bopisOrder" bean="ShoppingCart.current.bopisOrder" />

	<%-- section title --%>
	<h3>Shipping Address</h3>
	<div class="required-note">* Required</div>

	<c:choose>
		<c:when test="${not fflOrder && not bopisOrder && userIsAuthenticated && hasProfileAddress}">
			<dsp:include page="/checkout/includes/savedAddresses.jsp"></dsp:include>
		</c:when>
		<c:otherwise>
		<dsp:input type="hidden" id="shipping-address-new" name="shipping-address" bean="ShippingGroupFormHandler.addressId" value="0"/>
			<%@ include file="/checkout/includes/addressForm.jsp"%>
		</c:otherwise>
	</c:choose>

</dsp:page>
