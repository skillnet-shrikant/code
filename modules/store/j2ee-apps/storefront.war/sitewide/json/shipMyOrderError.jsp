<%--
- File Name: shipMyOrderErrorCart.jsp
- Author(s): jjensen
- Copyright Notice:
- Description: Creates a json error message for ship my order in cart and PDP
- Parameters:
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:param name="formhandler" bean="CartModifierFormHandler" />
	<json:object>
	<json:property name="success">false</json:property>
		<%@ include file="/sitewide/includes/errors/jsonErrors.jspf" %>
	</json:object>
</dsp:page>
