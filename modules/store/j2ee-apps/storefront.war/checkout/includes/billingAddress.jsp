<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="isBopis" bean="ShoppingCart.current.bopisOrder" vartype="boolean" />
	<dsp:getvalueof var="fflOrder" bean="ShoppingCart.current.fflOrder" vartype="boolean"/>

	<h3>Billing Address</h3>
	<div class="required-note">* Required</div>

	<c:set var="baClass" value="" scope="request"/>
	<c:choose>
		<c:when test="${(not isBopis) and (not fflOrder)}">
			<div class="field-group">
				<div class="checkbox">
					<label for="same-as-shipping">
						<dsp:input type="checkbox" bean="PaymentGroupFormHandler.sameAddressAsShipping" id="same-as-shipping" name="same-as-shipping" checked="true" /> Same as shipping
					</label>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<c:set var="baClass" value="ffl-order" scope="request"/>
		</c:otherwise>
	</c:choose>

	<div class="billing-address ${baClass}">
		<dsp:include page="/checkout/includes/addressForm.jsp">
			<dsp:param name="billing" value="true" />
		</dsp:include>
	</div>
</dsp:page>
