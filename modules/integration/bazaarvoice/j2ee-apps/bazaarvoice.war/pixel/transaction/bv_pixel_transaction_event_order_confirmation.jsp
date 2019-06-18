<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/com/bv/configuration/BVConfiguration"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
	
	<dsp:param name="order" bean="ShoppingCart.last" />
	
	<dsp:getvalueof var="orderId" param="order.orderNumber" />
	<dsp:getvalueof var="orderTotal" param="order.priceInfo.total" />
	<dsp:getvalueof var="currency" bean="BVConfiguration.defaultCurrency" />
	<dsp:getvalueof var="orderTax" param="order.priceInfo.tax" />
	<dsp:getvalueof var="shippingPrice" param="order.priceInfo.shipping" />
	<dsp:getvalueof var="email" param="order.contactEmail" />
	<dsp:getvalueof var="firstName" param="order.shippingGroups[0].shippingAddress.firstName" />
	<dsp:getvalueof var="locale" bean="BVConfiguration.locale" />
	
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="order.commerceItems"/>
		<dsp:param name="elementName" value="commerceItem"/>
		<dsp:param name="sortProperties" value="+minimumAge"/>
		<dsp:oparam name="outputStart">
			<script type="text/javascript">
				BV.pixel.trackTransaction({
					"orderId":"${orderId}",
					"total":"${orderTotal}",
					"currency":"${currency}",
					"tax":"${orderTax}",
					"shipping":"${shippingPrice}",
					"items":[
					
		</dsp:oparam>
		<dsp:oparam name="output">
			<dsp:getvalueof var="size" param="size" />
			<dsp:getvalueof var="count" param="count" />
			<dsp:getvalueof var="quantity" param="commerceItem.quantity" />
			<dsp:getvalueof var="sku" param="commerceItem.auxiliaryData.productId"/>
			<dsp:droplet name="Compare">
				<dsp:param name="obj1" param="commerceItem.priceInfo.salePrice" />
				<dsp:param name="obj2" param="commerceItem.priceInfo.listPrice" />
				<dsp:oparam name="lessthan">
					<dsp:getvalueof param="commerceItem.priceInfo.salePrice" var="itemPrice" />
				</dsp:oparam>
				<dsp:oparam name="default">
						<dsp:getvalueof param="commerceItem.priceInfo.listPrice" var="itemPrice"/>
				</dsp:oparam>
			</dsp:droplet>
			<dsp:getvalueof var="productName" param="commerceItem.auxiliaryData.productRef.description" />
			{
				"name":"${productName}",
				"price":"${itemPrice}",
				"quantity":"${quantity}",
				"sku":"${sku}"
			}
			<c:if test="${size> count}">
				,
			</c:if>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
						],
						"email":"${email}",
						"locale":"${locale}",
						"nickname":"${firstName}"
					});
			</script>
		</dsp:oparam>
	</dsp:droplet>		
</dsp:page>