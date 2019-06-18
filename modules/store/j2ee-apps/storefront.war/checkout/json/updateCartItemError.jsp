<%--
	- File Name: updateCartItemError.jsp
	- Author(s): jjensen
	- Copyright Notice:
	- Description: Creates a json error message after quantity update.
	- Parameters:
	--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:param name="formhandler" bean="CartModifierFormHandler" />

	<json:object>
		<%@ include file="/sitewide/includes/errors/jsonErrors.jspf" %>
		<json:array name="quantities">
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" bean="ShoppingCart.current.commerceItems"/>
				<dsp:oparam name="output">
					<json:object>
						<json:property name="itemId">
							<dsp:valueof param="element.id"/>
						</json:property>
						<json:property name="qty">
							<dsp:valueof param="element.quantity"/>
						</json:property>
					</json:object>
				</dsp:oparam>
			</dsp:droplet>
		</json:array>		
	</json:object>

</dsp:page>
