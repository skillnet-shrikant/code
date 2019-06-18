<%--
  - File Name: taxExemptionSuccess.jsp
  - Author(s): jjensen
  - Copyright Notice:
  - Description: Creates a json success message after successfully applying tax exemption to order
  - Parameters:
  --%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
	<json:object>
		<json:property name="success">true</json:property>
	</json:object>
</dsp:page>
