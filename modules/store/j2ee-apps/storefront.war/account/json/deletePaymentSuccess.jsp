<%@ include file="/sitewide/fragments/content-type-json.jspf" %>

<dsp:page>

	<dsp:getvalueof param="originalURL" var="originalURL" />

	<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
		<dsp:param name="inUrl" value="${contextPath}/account/paymentMethods.jsp"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="redirectUrl" scope="request" param="secureUrl"/>
		</dsp:oparam>
	</dsp:droplet>

	<json:object>
		<json:property name="success">true</json:property>
		<json:property name="url">${redirectUrl}</json:property>
	</json:object>

</dsp:page>
