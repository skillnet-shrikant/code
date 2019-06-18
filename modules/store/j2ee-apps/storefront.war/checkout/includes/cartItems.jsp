<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" bean="ShoppingCart.current.commerceItems"/>
		<dsp:param name="elementName" value="commerceItem"/>
		<dsp:param name="sortProperties" value="+minimumAge"/>
		<dsp:oparam name="output">
			<div class="order-items">
				<%@ include file="../fragments/checkoutOrderItem.jspf"%>
			</div>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>