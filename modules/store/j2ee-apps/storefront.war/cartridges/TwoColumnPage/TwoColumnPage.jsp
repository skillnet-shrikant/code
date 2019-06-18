<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/atg/endeca/assembler/droplet/InvokeAssembler"/>
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />
	<c:if test="${bvEnabled}">
		<dsp:include otherContext="/bv" page="/common/bv_common_script.jsp" />
		<c:set var="contextPath" value="${currentContext}" />
	</c:if>
	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:forEach var="elementSecondaryContent" items="${contentItem.SecondaryContent}">
		<c:if test="${elementSecondaryContent['@type']=='Breadcrumbs'}">
			<dsp:getvalueof var="breadcrumbsContentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${elementSecondaryContent}" />
		</c:if>
	</c:forEach>

	<c:if test="${not empty breadcrumbsContentItem && empty breadcrumbsContentItem['@error']}">
		<section class="breadcrumbs">
			<dsp:renderContentItem contentItem="${breadcrumbsContentItem}" />
		</section>
	</c:if>

	<section class="two-column-container">

		<%-- search results info --%>
		<c:forEach var="mainContent" items="${contentItem.MainContent}">
			<c:choose>
				<c:when test="${mainContent['@type']=='SearchAdjustments'}">
					<c:forEach var="terms" items="${mainContent.originalTerms}">
						<c:set var="searchTerm" value="${terms}" scope="request" />
					</c:forEach>
				</c:when>
				<c:when test="${mainContent['@type']=='ContentSlot-Main'}">
					<c:forEach var="contents" items="${mainContent.contents}">
						<c:set var="totalNumRecs" value="${contents.totalNumRecs}" scope="request" />
					</c:forEach>
				</c:when>
			</c:choose>
		</c:forEach>
		<c:if test="${not empty searchTerm}">
			<div class="section-content search-results-bar">
				Search results: <span>${searchTerm} (<span id="total-num-recs">${totalNumRecs}</span> results)</span>
			</div>
		</c:if>

		<div class="two-column-left">
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
			<c:forEach var="elementMainContent" items="${contentItem.MainContent}">
				<dsp:renderContentItem contentItem="${elementMainContent}" />
			</c:forEach>
		</div>
	</section>
	<c:if test="${bvEnabled && not empty bvRecords && not isAjax}">
		<dsp:include otherContext="/bv" page="/productListing/common/bv_plp_script.jsp">
			<dsp:param name="records" value="${bvRecords}" />
		</dsp:include>
		<c:set var="contextPath" value="${currentContext}" />
	</c:if>
</dsp:page>
