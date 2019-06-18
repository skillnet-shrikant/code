<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/atg/endeca/assembler/droplet/InvokeAssembler"/>
	<dsp:importbean bean="/mff/MFFEnvironment" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:if test="${ empty key }">
		<dsp:getvalueof var="key" value="${contentItem['endeca:siteState'].contentPath}" scope="request"/>
	</c:if>
	
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />
	<c:if test="${bvEnabled}">
		<dsp:include otherContext="/bv" page="/common/bv_common_script.jsp" />
		<c:set var="contextPath" value="${currentContext}" />
	</c:if>
	
	<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
		<dsp:param name="key" value="${fn:toLowerCase(key)}" />
		<dsp:param name="defaultPageTitle" value="Promo Page" />
		<dsp:param name="defaultMetaDescription" value="" />
		<dsp:param name="defaultCanonicalURL" value="${contentItem['endeca:siteState'].contentPath}" />
		<dsp:param name="defaultRobotsIndex" value="index" />
		<dsp:param name="defaultRobotsFollow" value="follow" />
	</dsp:include>	
	
	<layout:default>
		<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
		<jsp:attribute name="section">content</jsp:attribute>
		<jsp:attribute name="pageType">promo</jsp:attribute>
		<jsp:attribute name="bodyClass">promo</jsp:attribute>
		<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
		<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:body>
		
		<c:forEach var="elementMainContent" items="${contentItem.MainContent}">
			<dsp:renderContentItem contentItem="${elementMainContent}" />
		</c:forEach>
		
		<c:if test="${bvEnabled && not empty bvRecords && not isAjax}">
			<dsp:include otherContext="/bv" page="/productListing/common/bv_plp_script.jsp">
				<dsp:param name="records" value="${bvRecords}" />
			</dsp:include>
			<c:set var="contextPath" value="${currentContext}" />
		</c:if>

		</jsp:body>
	</layout:default>

</dsp:page>
