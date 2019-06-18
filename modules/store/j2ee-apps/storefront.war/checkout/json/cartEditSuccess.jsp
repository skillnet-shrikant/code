<%--
  - File Name: cartSuccess.jsp
  - Author(s): lthomason
  - Copyright Notice:
  - Description: Creates a json success message after successful add to cart.
  - Parameters:
  --%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/OriginatingRequest" var="originatingRequest" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="productId" bean="CartModifierFormHandler.productId" />
	
	<json:object>

		<json:property name="success">true</json:property>
		<json:property name="productId">${productId}</json:property>
		<json:property name="selectedQty"><dsp:valueof bean="CartModifierFormHandler.quantity"/></json:property>
		<json:property name="url">${contextPath}/checkout/cart.jsp</json:property>

		<dsp:droplet name="Switch">
			<dsp:param name="value" bean="/atg/commerce/ShoppingCart.current.bopisOrder"/>
			<dsp:oparam name="true">
				<dsp:droplet name="/atg/commerce/catalog/ProductLookup">
					<dsp:param name="id" value="${productId}"/>
					<dsp:param name="elementName" value="product"/>
					<dsp:oparam name="output">
						<%-- is product bopis only? --%>
						<dsp:droplet name="Switch">
							<dsp:param name="value" param="product.fulfillmentMethod" />
							<dsp:oparam name="7">
								<%-- 7: product is bopis only --%>
								<%-- does order contain only bopis items? --%>
								<dsp:droplet name="/com/mff/commerce/order/purchase/IsItemRemovalRequired">
									<dsp:oparam name="output">
										<dsp:droplet name="Switch">
											<dsp:param name="value" param="bopisItemsOnly" />
											<dsp:oparam name="true">
												<%-- order is bopis only --%>
												<json:property name="bopisOnly">true</json:property>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>

	</json:object>

</dsp:page>
