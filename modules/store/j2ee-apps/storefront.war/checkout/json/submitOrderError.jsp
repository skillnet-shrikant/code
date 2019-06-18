<%--
- File Name: avsShippingSuccess.jsp
- Author(s):
- Copyright Notice:
- Description: Creates a json submit order error message
- Parameters:
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:param name="formhandler" bean="CommitOrderFormHandler" />

	<dsp:droplet name="/atg/commerce/order/purchase/RepriceOrderDroplet">
		<dsp:param value="ORDER_TOTAL" name="pricingOp"/>
	</dsp:droplet>

	<json:object>
		<json:property name="success">false</json:property>
		<%@ include file="/sitewide/includes/errors/jsonErrors.jspf" %>
	</json:object>
</dsp:page>
