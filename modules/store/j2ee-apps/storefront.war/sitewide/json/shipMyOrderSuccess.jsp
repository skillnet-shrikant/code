<%--
- File Name: shipMyOrderSuccess.jsp
- Author(s): jjensen
- Copyright Notice:
- Description: Creates a json message for ship my order success on PDP
- Parameters:
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
	<json:object>
		<json:property name="success">true</json:property>
	</json:object>
</dsp:page>
