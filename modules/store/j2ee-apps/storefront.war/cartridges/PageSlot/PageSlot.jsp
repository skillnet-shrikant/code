<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/OriginatingRequest" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	
	<dsp:droplet name="Switch">
		<dsp:param name="value" value="${contentItem['endeca:contentPath']}"/>
		
		<dsp:oparam name="/home">
			<dsp:getvalueof var="section" value="home"/>
			<dsp:getvalueof var="pageType" value="home"/>
			<dsp:getvalueof var="bodyClass" value="home"/>
			<%-- sets SEO title & desc into request vars that are used later in the file --%>
			<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
				<dsp:param name="key" value="home" />
				<dsp:param name="defaultPageTitle" value="Fleet Farm" />
				<dsp:param name="defaultMetaDescription" value="Fleet Farm" />
				<dsp:param name="defaultCanonicalURL" value="" />
				<dsp:param name="defaultRobotsIndex" value="index" />
				<dsp:param name="defaultRobotsFollow" value="follow" />
			</dsp:include>
			
			<layout:default>
		
				<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
				<jsp:attribute name="section">${section}</jsp:attribute>
				<jsp:attribute name="pageType">${pageType}</jsp:attribute>
				<jsp:attribute name="bodyClass">${bodyClass}</jsp:attribute>
				<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
				<jsp:attribute name="metaKeywords"></jsp:attribute>
				<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
				<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
				<jsp:attribute name="lastModified"></jsp:attribute>
				<jsp:body>
					<c:forEach var="element" items="${contentItem.contents}">
						<dsp:renderContentItem contentItem="${element}" />
					</c:forEach>
				</jsp:body>
			</layout:default>			
		</dsp:oparam>
		
		<dsp:oparam name="/category">

			<%-- Fetch SEOTags --%>
			<c:set var="topLevelCategory" value="${contentItem.contents[0].SecondaryContent[0].refinementCrumbs[0].ancestors[0].label}" scope="request" />
			<c:set var="currentCategory" value="${contentItem.contents[0].SecondaryContent[0].refinementCrumbs[0].label}" scope="request" />
			<c:set var="currentCategoryId" value="${contentItem.contents[0].SecondaryContent[0].refinementCrumbs[0].properties['category.repositoryId']}" scope="request" />
			<c:choose>
				<c:when test="${empty topLevelCategory}">
					<c:set var="defaultPageTitle" scope="request">${currentCategory} at Fleet Farm</c:set>
					<c:set var="defaultMetaDescription" scope="request">
						Find a large selection of ${currentCategory} at low Fleet Farm prices.
					</c:set>
				</c:when>
				<c:otherwise>
					<c:set var="defaultPageTitle" scope="request">${currentCategory} - ${topLevelCategory} at Fleet Farm</c:set>
					<c:set var="defaultMetaDescription" scope="request">
						Find a large selection of ${currentCategory} in the ${topLevelCategory} department at low Fleet Farm prices.
					</c:set>
				</c:otherwise>
			</c:choose>	

			<%-- sets SEO title & desc into request vars that are used later in the file --%>
			<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
				<dsp:param name="key" value="${currentCategoryId}" />
				<dsp:param name="defaultPageTitle" value="${defaultPageTitle}" />
				<dsp:param name="defaultMetaDescription" value="${defaultMetaDescription}" />
				<dsp:param name="defaultCanonicalURL" value="${contentItem['endeca:siteState'].contentPath}" />
				<dsp:param name="defaultRobotsIndex" value="index" />
				<dsp:param name="defaultRobotsFollow" value="follow" />
			</dsp:include>	
		
			
			<dsp:getvalueof var="section" value="browse"/>
			<dsp:getvalueof var="pageType" value="category"/>
			<dsp:getvalueof var="bodyClass" value="category"/>
			
			<layout:default>
		
				<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
				<jsp:attribute name="section">${section}</jsp:attribute>
				<jsp:attribute name="pageType">${pageType}</jsp:attribute>
				<jsp:attribute name="bodyClass">${bodyClass}</jsp:attribute>
				<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
				<jsp:attribute name="metaKeywords"></jsp:attribute>
				<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
				<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
				<jsp:attribute name="lastModified"></jsp:attribute>
				<jsp:body>
					<c:forEach var="element" items="${contentItem.contents}">
						<dsp:renderContentItem contentItem="${element}" />
					</c:forEach>
				</jsp:body>
			</layout:default>		
		</dsp:oparam>
		<dsp:oparam name="/brand">
			<dsp:getvalueof var="section" value="browse"/>
			<dsp:getvalueof var="pageType" value="category"/>
			<dsp:getvalueof var="bodyClass" value="category brand"/>
			<dsp:getvalueof var="brandURIParts" value="${fn:split(contentItem['endeca:siteState'].contentPath, '/')}" />
			<dsp:getvalueof var="brandURIName" value="${brandURIParts[1]}" scope="request" />
			<c:set var="formattedBrandName" value=""/>
			<c:forEach var="word" items="${fn:split(fn:toLowerCase(brandURIName),'-')}">
			<c:choose>
				<c:when test="${empty formattedBrandName}">
					<c:set var="formattedBrandName" value="${fn:toUpperCase(fn:substring(word,0,1))}${fn:toLowerCase(fn:substring(word,1,fn:length(word)))}" />
				</c:when>
				<c:otherwise>
					<c:set var="formattedBrandName" value="${formattedBrandName} ${fn:toUpperCase(fn:substring(word,0,1))}${fn:toLowerCase(fn:substring(word,1,fn:length(word)))}" />
				</c:otherwise>
			</c:choose>
		        
		    </c:forEach>
			<c:set var="defaultPageTitle" scope="request">${formattedBrandName} at Fleet Farm</c:set>
			<c:set var="defaultMetaDescription" scope="request">
				Find a large selection of ${formattedBrandName} at low Fleet Farm prices.
			</c:set>
			<%-- sets SEO title & desc into request vars that are used later in the file --%>
			<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
				<dsp:param name="key" value="${fn:toLowerCase(brandURIName)}" />
				<dsp:param name="defaultPageTitle" value="${defaultPageTitle}" />
				<dsp:param name="defaultMetaDescription" value="${defaultMetaDescription}" />
				<dsp:param name="defaultCanonicalURL" value="${contentItem['endeca:siteState'].contentPath}" />
				<dsp:param name="defaultRobotsIndex" value="index" />
				<dsp:param name="defaultRobotsFollow" value="follow" />
			</dsp:include>	
			
			<layout:default>
		
				<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
				<jsp:attribute name="section">${section}</jsp:attribute>
				<jsp:attribute name="pageType">${pageType}</jsp:attribute>
				<jsp:attribute name="bodyClass">${bodyClass}</jsp:attribute>
				<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
				<jsp:attribute name="metaKeywords"></jsp:attribute>
				<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
				<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
				<jsp:attribute name="lastModified"></jsp:attribute>
				<jsp:body>
					<c:forEach var="element" items="${contentItem.contents}">
						<dsp:renderContentItem contentItem="${element}" />
					</c:forEach>
				</jsp:body>
			</layout:default>		
		</dsp:oparam>
		<dsp:oparam name="/search">
			 <c:set var="redirect" value="false" />
			<%-- <dsp:droplet name="/com/mff/droplet/IsStringNumeric">
				<dsp:param name="string" value="${param.Ntt}"/>
				<dsp:oparam name="true">--%>
					<c:forEach var="mainContent" items="${contentItem.contents[0].MainContent}">
						<c:choose>
							<c:when test="${mainContent['@type']=='SearchAdjustments'}">
								<c:forEach var="terms" items="${mainContent.originalTerms}">
									<c:set var="searchTerm" value="${terms}" scope="request" />
								</c:forEach>
							</c:when>
							<c:when test="${mainContent['@type']=='ContentSlot-Main'}">
								<c:forEach var="contents" items="${mainContent.contents}">
									<c:set var="totalNumRecs" value="${contents.totalNumRecs}" scope="request" />
									<c:if test="${totalNumRecs eq 1 && not empty contents.records  }">
										<dsp:getvalueof var="singleRecord" value="${contents.records[0]}" scope="request"/>
										<c:set var="productId">${singleRecord.attributes['product.repositoryId']}</c:set>
										 <dsp:droplet name="/com/mff/droplet/ProductUrlGeneratorDroplet">
											<dsp:param name="productId" value="${productId}"/>
											<dsp:oparam name="output">
												<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
													<dsp:param name="inUrl" param="url"/>
													<dsp:oparam name="output">
														<c:set var="productUrl"><dsp:valueof param="secureUrl"/>?Ntt=${param.Ntt}</c:set>
														 <c:set var="redirect" value="true" />
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
									</c:if>
								</c:forEach>
							</c:when>
						</c:choose>
						
					</c:forEach>
				<%--</dsp:oparam>
			</dsp:droplet>--%>		
			
			<c:choose>
				<c:when test="${ redirect && not empty productUrl}">
					 <dsp:droplet name="/atg/dynamo/droplet/Redirect">
					  <dsp:param name="url" value="${productUrl}"/>
					</dsp:droplet>
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="pageTitle" value="Search Results"/>
					<dsp:getvalueof var="section" value="browse"/>
					<dsp:getvalueof var="pageType" value="search"/>
					<dsp:getvalueof var="bodyClass" value="search"/>
					<dsp:getvalueof var="seoCanonicalURL" value="${contentItem['endeca:siteState'].contentPath}"/>
					<layout:default>
				
						<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
						<jsp:attribute name="section">${section}</jsp:attribute>
						<jsp:attribute name="pageType">${pageType}</jsp:attribute>
						<jsp:attribute name="bodyClass">${bodyClass}</jsp:attribute>
						<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
						<jsp:attribute name="metaKeywords"></jsp:attribute>
						<jsp:attribute name="seoCanonicalURL">${seoCanonicalURL}</jsp:attribute>
						<jsp:attribute name="seoRobots"></jsp:attribute>
						<jsp:attribute name="lastModified"></jsp:attribute>
						<jsp:body>
							<c:forEach var="element" items="${contentItem.contents}">
								<dsp:renderContentItem contentItem="${element}" />
							</c:forEach>
						</jsp:body>
					</layout:default>
				
				</c:otherwise>
			</c:choose>
						
		</dsp:oparam>
		<dsp:oparam name="default">
			<dsp:getvalueof var="key" value="${contentItem['endeca:siteState'].contentPath}" scope="request"/> 
			<c:forEach var="element" items="${contentItem.contents}">
				<dsp:renderContentItem contentItem="${element}" />
			</c:forEach>
		</dsp:oparam>
	</dsp:droplet>

	

</dsp:page>
