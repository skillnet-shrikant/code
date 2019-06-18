<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/atg/endeca/assembler/droplet/InvokeAssembler"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:forEach var="elementSecondaryContent" items="${contentItem.SecondaryContent}">
		<c:if test="${elementSecondaryContent['@type']=='Breadcrumbs'}">
			<dsp:getvalueof var="breadcrumbsContentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${elementSecondaryContent}" />
		</c:if>
	</c:forEach>

	<%-- sets SEO title & desc into request vars that are used later in the file --%>
	<c:if test="${ empty key }">
		<dsp:getvalueof var="key" value="${contentItem['endeca:siteState'].contentPath}" scope="request"/>
	</c:if>
	<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
		<dsp:param name="key" value="${fn:toLowerCase(key)}" />
		<dsp:param name="defaultPageTitle" value="Garden Center" />
		<dsp:param name="defaultMetaDescription" value="" />
		<dsp:param name="defaultCanonicalURL" value="${contentItem['endeca:siteState'].contentPath}" />
		<dsp:param name="defaultRobotsIndex" value="index" />
		<dsp:param name="defaultRobotsFollow" value="follow" />
	</dsp:include>	
			
	<layout:default>
		<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
		<jsp:attribute name="section">content</jsp:attribute>
		<jsp:attribute name="pageType">gardenCenter</jsp:attribute>
		<jsp:attribute name="bodyClass">garden-center</jsp:attribute>
		<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
		<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:body>

			<c:if test="${not empty breadcrumbsContentItem && empty breadcrumbsContentItem['@error']}">
				<section class="breadcrumbs">
					<dsp:renderContentItem contentItem="${breadcrumbsContentItem}" />
				</section>
			</c:if>

			<section class="two-column-container">

				<div class="two-column-left">

					<%-- small screen garden center nav --%>
					<div class="mobile-refinements">
						<div class="section-title">
							<h2>Garden Center Links</h2>
						</div>
						<div class="hide-sidebar">
							<span class="icon icon-close" aria-hidden="true"></span>
							<span class="sr-only">close</span>
						</div>
						<button class="button secondary expand hide-sidebar">Hide Garden Center Links</button>
					</div>

					<c:forEach var="elementSecondaryContent" items="${contentItem.SecondaryContent}">
						<c:if test="${elementSecondaryContent['@type']!='Breadcrumbs'}">
							<c:choose>
								<c:when test="${elementSecondaryContent['@type']=='ContentSlot-Secondary'}">
									<c:choose>
										<c:when test="${not empty elementSecondaryContent.contents}">
											<c:forEach var="element" items="${elementSecondaryContent.contents}">
												<dsp:renderContentItem contentItem="${element}" />
											</c:forEach>
										</c:when>
										<c:otherwise>
											<c:forEach var="element" items="${elementSecondaryContent.contentPaths}">
												<dsp:droplet name="InvokeAssembler">
													<dsp:param name="includePath" value=""/>
													<dsp:param name="contentCollection" value="${element}"/>
													<dsp:oparam name="output">
														<dsp:getvalueof var="contentSlotContent" vartype="com.endeca.infront.assembler.ContentItem" param="contentItem" />
														<c:forEach var="contentElement" items="${contentSlotContent.contents}">
															<dsp:renderContentItem contentItem="${contentElement}" />
														</c:forEach>
													</dsp:oparam>
												</dsp:droplet>
											</c:forEach>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<dsp:renderContentItem contentItem="${elementSecondaryContent}" />
								</c:otherwise>
							</c:choose>
						</c:if>
					</c:forEach>
				</div>

				<div class="two-column-right">
					<%-- small screen garden center nav toggle --%>
					<button class="button secondary expand show-sidebar">Garden Center Links</button>

					<c:forEach var="elementMainContent" items="${contentItem.MainContent}">
						<dsp:renderContentItem contentItem="${elementMainContent}" />
					</c:forEach>
				</div>

			</section>

		</jsp:body>
	</layout:default>

</dsp:page>
