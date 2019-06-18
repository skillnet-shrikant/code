<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/mff/commerce/catalog/PrimaryNavDroplet"/>
	<dsp:importbean bean="/atg/commerce/endeca/cache/DimensionValueCacheDroplet"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<dsp:getvalueof var="rootCategoryId" bean="PrimaryNavDroplet.rootCategoryId"/>
	<c:set var="contextPath"  value="${pageContext.servletContext.contextPath}" scope="request" />
	<dsp:getvalueof var="searchTerm"  param="Ntt"/>

	<%-- breadcrumbs --%>
	<ul aria-label="breadcrumbs" role="navigation">
		<li><a href="${contextPath}/" class="crumb">Home</a></li>
		<c:choose>
			<c:when test="${not empty searchTerm}">
			<%-- Search Page Treatment --%>
				<li><span class="crumb active">Search : ${searchTerm}</span></li>
			</c:when>
			<c:when test="${not empty brandURIName}">
			<%-- brand page treatment --%>
				<li><a href="${contentItem.removeAllAction.contentPath}/${brandURIName}/_/N-${contentItem.refinementCrumbs[0].properties['dimVal']}" class="crumb">Brand: ${contentItem.refinementCrumbs[0].label}</a></li>
				<c:set var="brandName" value="${contentItem.refinementCrumbs[0].label}" scope="request"/>
				<c:set var="brandDim" value="${contentItem.refinementCrumbs[0].properties['dimVal']}" />
				<c:forEach var="refinementCrumb" items="${contentItem.refinementCrumbs}" varStatus="crumbIndex">
				<c:set var="bc" value="${refinementCrumb.properties['DGraph.Spec']}"/>
				<c:set var="pdpCrumb" value="${fn:replace(bc,'.', '|')}" scope="request"/>
				<c:set var="brandCrumb" value="${brandDim}" scope="request"/>
				<c:if test="${crumbIndex.index > '0'}">
					<c:forEach var="refinementCrumbAncestor" items="${refinementCrumb.ancestors}">
						<c:if test="${not empty refinementCrumbAncestor.properties['category.repositoryId'] && refinementCrumbAncestor.properties['category.repositoryId'] ne rootCategoryId}">
							<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
								<dsp:param name="id" value="${refinementCrumbAncestor.properties['category.repositoryId']}"/>
								<dsp:param name="elementName" value="category"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="catName" param="category.displayName" />
										<dsp:droplet name="DimensionValueCacheDroplet">
											<dsp:param name="repositoryId" value="${refinementCrumbAncestor.properties['category.repositoryId']}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="categoryCacheEntry" param="dimensionValueCacheEntry" />
											</dsp:oparam>
											</dsp:droplet>
											<c:set var="catCacheURI" value="${fn:substringBefore(categoryCacheEntry, '/_/')}" />
											<c:set var="catPath" value="${fn:substringAfter(catCacheURI, '/category')}" />
									<li><a href="/brand/${brandURIName}${catPath}/_/N-${brandDim}+${categoryCacheEntry.dimvalId}" class="crumb"><dsp:valueof param="category.displayName"/></a></li>
								</dsp:oparam>
							</dsp:droplet>
						</c:if>
					</c:forEach>
						<li><span class="crumb active">${refinementCrumb.label}</span></li>
					</c:if>
				</c:forEach>
			</c:when>
			<%-- end brand page treatment --%>
			<c:otherwise>
			<%-- Regular Category page Treatment --%>
				<c:forEach var="refinementCrumb" items="${contentItem.refinementCrumbs}">
				<c:set var="bc" value="${refinementCrumb.properties['DGraph.Spec']}"/>
				<c:set var="pdpCrumb" value="${fn:replace(bc,'.', '|')}" scope="request"/>
					<c:forEach var="refinementCrumbAncestor" items="${refinementCrumb.ancestors}">
						<c:if test="${not empty refinementCrumbAncestor.properties['category.repositoryId'] && refinementCrumbAncestor.properties['category.repositoryId'] ne rootCategoryId}">
							<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
								<dsp:param name="id" value="${refinementCrumbAncestor.properties['category.repositoryId']}"/>
								<dsp:param name="elementName" value="category"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="catName" param="category.displayName" />
										<dsp:droplet name="DimensionValueCacheDroplet">
											<dsp:param name="repositoryId" value="${refinementCrumbAncestor.properties['category.repositoryId']}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="categoryCacheEntry" param="dimensionValueCacheEntry" />
											</dsp:oparam>
											</dsp:droplet>
									<li><a href="${categoryCacheEntry.url}" class="crumb"><dsp:valueof param="category.displayName"/></a></li>
								</dsp:oparam>
							</dsp:droplet>
						</c:if>
					</c:forEach>
					<li><span class="crumb active">${refinementCrumb.label}</span></li>
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</ul>
	<%-- applied facets --%>
	<dsp:include page="/browse/includes/appliedFacets.jsp">
		<dsp:param name="contentItem" value="${contentItem}"/>
	</dsp:include>

</dsp:page>
