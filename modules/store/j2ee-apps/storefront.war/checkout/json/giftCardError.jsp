<%--
- File Name: avsShippingSuccess.jsp
- Author(s):
- Copyright Notice:
- Description: Creates a json billing error message
- Parameters:
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
	<dsp:param name="formhandler" bean="PaymentGroupFormHandler" />
	<json:object>
	<json:property name="success">false</json:property>
		<%@ include file="/sitewide/includes/errors/jsonErrors.jspf" %>
	</json:object>
</dsp:page>
