<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:if test="${ empty key }">
		<dsp:getvalueof var="key" value="${contentItem['endeca:siteState'].contentPath}" scope="request"/>
	</c:if>
	<c:if test="${not empty contentItem.storeId }">
		<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
			<dsp:param name="key" value="${fn:toLowerCase(key)}" />
			<dsp:param name="defaultPageTitle" value="Store Location Page" />
			<dsp:param name="defaultMetaDescription" value="" />
			<dsp:param name="defaultCanonicalURL" value="${contentItem['endeca:siteState'].contentPath}" />
			<dsp:param name="defaultRobotsIndex" value="index" />
			<dsp:param name="defaultRobotsFollow" value="follow" />
		</dsp:include>
		<dsp:droplet name="/atg/commerce/locations/StoreLookupDroplet">
		    <dsp:param name="id" value="${contentItem.storeId}"/>
		    <dsp:param name="elementName" value="storeItem"/>
		    <dsp:oparam name="output">
				<layout:default>
					<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
					<jsp:attribute name="section">home</jsp:attribute>
					<jsp:attribute name="pageType">storeDetail</jsp:attribute>
					<jsp:attribute name="bodyClass">store-detail</jsp:attribute>
					<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
					<jsp:attribute name="metaKeywords"></jsp:attribute>
					<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
					<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
					<jsp:attribute name="lastModified"></jsp:attribute>
					<jsp:body>
					<c:forEach var="elementMainContent" items="${contentItem.MainContent}">
						<dsp:renderContentItem contentItem="${elementMainContent}">
							<dsp:param name="storeItem" param="storeItem"/>
						</dsp:renderContentItem>
					</c:forEach>
					
			
					</jsp:body>
				</layout:default>
		   </dsp:oparam>
		</dsp:droplet>
	</c:if>
	
	
</dsp:page>