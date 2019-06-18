<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

	<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
		<dsp:param name="inUrl" value="${contextPath}/checkout/cart.jsp"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="cartUrl" scope="request" param="nonSecureUrl"/>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:getvalueof var="currentOrder" bean="ShoppingCart.current" />
	<c:if test="${currentOrder !=null && currentOrder.totalCommerceItemCount > 0}">
		<%-- reprice order --%>
		<dsp:droplet name="/atg/commerce/order/purchase/RepriceOrderDroplet">
			<dsp:param value="ORDER_TOTAL" name="pricingOp"/>
		</dsp:droplet>
	</c:if>
	
	<div class="side-cart-content">
		<dsp:droplet name="IsEmpty">
			<dsp:param name="value" bean="ShoppingCart.current.commerceItems"/>
			<dsp:oparam name="true">
				<div class="side-cart-header">
					<span class="count-number">0</span> items in your cart.
				</div>
			</dsp:oparam>
			<dsp:oparam name="false">
				
				<dsp:getvalueof var="totalItems" bean="ShoppingCart.current.totalCommerceItemCount" />
				<div class="side-cart-header">
					<span class="count-number">${totalItems}</span> items in your cart.
				</div>

				<dsp:droplet name="ForEach">
					<dsp:param name="array" bean="ShoppingCart.current.commerceItems"/>
					<dsp:param name="elementName" value="commerceItem"/>
					<dsp:oparam name="outputStart">
						<div class="side-cart-items">
					</dsp:oparam>
					<dsp:oparam name="output">
						<%@ include file="/browse/fragments/sideCartItem.jspf"%>
					</dsp:oparam>
					<dsp:oparam name="outputEnd">
						</div>
					</dsp:oparam>
				</dsp:droplet>

				<div class="side-cart-footer">
					<%@ include file="/browse/fragments/sideCartTotals.jspf" %>
				</div>

			</dsp:oparam>
		</dsp:droplet>
	</div>

	<%-- close button --%>
	<span class="icon icon-close exit-off-canvas"></span>

	<%-- listrak: cart --%>
	<%@ include file="/sitewide/third_party/listrak_cart.jspf" %>

</dsp:page>
