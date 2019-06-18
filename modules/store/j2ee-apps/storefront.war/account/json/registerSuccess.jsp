<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<dsp:getvalueof param="originalURL" var="originalURL" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<c:choose>
		<c:when test="${not empty originalURL}">
			<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
				<dsp:param name="inUrl" value="${originalURL}"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="redirectUrl" scope="request" param="secureUrl"/>
				</dsp:oparam>
			</dsp:droplet>
		</c:when>
		<c:otherwise>
			<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
				<dsp:param name="inUrl" value="${contextPath}/account/account.jsp?new=true"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="redirectUrl" scope="request" param="secureUrl"/>
				</dsp:oparam>
			</dsp:droplet>
		</c:otherwise>
	</c:choose>

	<json:object>
		<json:property name="success">true</json:property>
		<json:property name="url">${redirectUrl}</json:property>
		<json:property name="wishlistId"><dsp:valueof bean="Profile.wishlist.id" /></json:property>
	</json:object>

</dsp:page>
