<%--
- File Name: avsTaxExemptionSuccess.jsp
- Author(s):
- Copyright Notice:
- Description: Creates a json message for tax exemption address avs modal
- Parameters:
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

	<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
		<dsp:param name="inUrl" value="${contextPath}/account/taxExemptions.jsp"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="redirectUrl" scope="request" param="secureUrl"/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof var="addressMatched" bean="ProfileFormHandler.addressMatched" />
	<dsp:getvalueof var="suggestedAddresses" bean="ProfileFormHandler.suggestedAddresses" />

	<json:object>

		<%-- avs called successfully --%>
		<json:property name="success">true</json:property>

		<c:choose>
			<c:when test="${addressMatched}">
				<%-- address passed avs --%>
				<json:property name="addressMatched">true</json:property>
				<json:property name="url">${redirectUrl}</json:property>
			</c:when>
			<c:otherwise>

				<%-- address failed avs --%>
				<json:property name="showModal">true</json:property>
				<json:property name="submitId">#tax-exemption-submit</json:property>

				<dsp:droplet name="ForEach">
					<dsp:param name="array" bean="ProfileFormHandler.suggestedAddresses"/>
					<dsp:param name="elementName" value="addressInfo"/>
					<dsp:oparam name="output">
						<dsp:getvalueof param="key" var="addressType"/>
						<c:if test="${addressType eq 'suggestedAddress'}">
							<c:set var="isSuggestedAddress" value="true" scope="request" />
							<json:object name="suggestedAddress">
								<json:property name="address1"><dsp:valueof param="addressInfo.address1"/></json:property>
								<json:property name="address2"><dsp:valueof param="addressInfo.address2"/></json:property>
								<json:property name="city"><dsp:valueof param="addressInfo.city"/></json:property>
								<json:property name="state"><dsp:valueof param="addressInfo.state"/></json:property>
								<json:property name="postalCode"><dsp:valueof param="addressInfo.postalCode"/></json:property>
							</json:object>
						</c:if>
						<c:if test="${addressType eq 'enteredAddress'}">
							<json:object name="enteredAddress">
								<json:property name="address1"><dsp:valueof param="addressInfo.address1"/></json:property>
								<json:property name="address2"><dsp:valueof param="addressInfo.address2"/></json:property>
								<json:property name="city"><dsp:valueof param="addressInfo.city"/></json:property>
								<json:property name="state"><dsp:valueof param="addressInfo.state"/></json:property>
								<json:property name="postalCode"><dsp:valueof param="addressInfo.postalCode"/></json:property>
							</json:object>
						</c:if>
					</dsp:oparam>
			   </dsp:droplet>
			</c:otherwise>
		</c:choose>

		<%-- if there's not suggested address let them edit the address they entered if they want to --%>
		<c:if test="${not isSuggestedAddress}">
			<json:property name="noMatch">true</json:property>
		</c:if>

	</json:object>

</dsp:page>
