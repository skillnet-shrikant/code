<%--
- Author(s):
- Copyright Notice:
- Description: Creates a json for submit Order Success
- Parameters:
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
	<json:object>
		<json:property name="success">true</json:property>
		<json:property name="continueToConfirmation">true</json:property>
		<json:property name="url">${contextPath}/checkout/orderConfirmation.jsp</json:property>
	</json:object>
</dsp:page>
