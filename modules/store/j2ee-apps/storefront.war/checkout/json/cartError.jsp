<%--
	- File Name: cartError.jsp
	- Author(s):
	- Copyright Notice:
	- Description: Displays add to cart error messages (json format).
	- Parameters:
	-
	--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>

<dsp:page>
	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:param name="formhandler" bean="CartModifierFormHandler" />
	<json:object>
		<%@ include file="/sitewide/includes/errors/jsonErrors.jspf" %>
		<json:array name="maxQuantities">
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" bean="CartModifierFormHandler.maxQuantities"/>
				<dsp:oparam name="output">
					<json:object>
						<json:property name="skuId">
							<dsp:valueof param="key"/>
						</json:property>
						<json:property name="maxQuantity">
							<dsp:valueof param="element"/>
						</json:property>
					</json:object>
				</dsp:oparam>
			</dsp:droplet>
		</json:array>
	</json:object>

</dsp:page>
