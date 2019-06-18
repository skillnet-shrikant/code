<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

	<%-- Page Parameters --%>
	<dsp:param name="shippingGroup" bean="ShoppingCart.current.shippingGroups[0]"/>

	<h3>Shipping Address</h3>
	<p><dsp:valueof param="shippingGroup.shippingAddress.firstName" />&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.lastName" /></p>
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" param="shippingGroup.specialInstructions"/>
		<dsp:oparam name="false">
			<p>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="shippingGroup.specialInstructions"/>
					<dsp:oparam name="output">
						<span class="label">Attention:</span><dsp:valueof param="element"/>
					</dsp:oparam>
				</dsp:droplet>
			</p>
		</dsp:oparam>
	</dsp:droplet>
	<p><dsp:valueof param="shippingGroup.shippingAddress.address1" /></p>
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" param="shippingGroup.shippingAddress.address2"/>
		<dsp:oparam name="false">
			<p><dsp:valueof param="shippingGroup.shippingAddress.address2" /></p>
		</dsp:oparam>
	</dsp:droplet>
	<p><dsp:valueof param="shippingGroup.shippingAddress.city" />,&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.state" />&nbsp;<dsp:valueof param="shippingGroup.shippingAddress.postalCode" /></p>
	<p>
		<dsp:getvalueof var="phone" param="shippingGroup.shippingAddress.phoneNumber" />
		<c:out value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, 10)}" />
	</p>

</dsp:page>
