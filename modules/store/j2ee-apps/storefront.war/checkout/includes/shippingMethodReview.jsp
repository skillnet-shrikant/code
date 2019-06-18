<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />

	<%-- Page Parameters --%>
	<dsp:param name="shippingGroup" bean="ShoppingCart.current.shippingGroups[0]"/>

	<h3>Shipping Method</h3>
	<p><dsp:valueof param="shippingGroup.shippingMethod" /></p>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" param="shippingGroup.saturdayDelivery"/>
		<dsp:oparam name="true">
			<p>Saturday Delivery</p>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
