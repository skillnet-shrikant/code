<%--
- File Name: submitOrderInventoryError.jsp
- Author(s):
- Copyright Notice:
- Description: Creates a json message for submit order page
- Parameters:
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
	<json:object>
		<json:property name="success">true</json:property>
		<json:property name="url">${contextPath}/checkout/cart.jsp</json:property>
	</json:object>
</dsp:page>
