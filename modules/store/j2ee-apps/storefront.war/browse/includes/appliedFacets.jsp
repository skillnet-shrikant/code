<%--

--%>
<dsp:page>

	<%-- Page Variables --%>
	<dsp:getvalueof param="contentItem" var="content"/>

	<div id="applied-facet-breadcrumbs">
		<div class="applied-facets">

			<%-- Facets --%>
			<c:forEach var="dimCrumb" varStatus="counter" items="${content.refinementCrumbs}">
				<c:if test="${dimCrumb.dimensionName != 'product.category' || (dimCrumb.dimensionName == 'product.category' && pageType == 'search')}">
					<dsp:getvalueof var="facetNoProduct" value="${fn:replace(dimCrumb.dimensionName, 'product.', '')}"/>
					<dsp:getvalueof var="facetName" value="${fn:replace(facetNoProduct, '.', '-')}"/>
					<c:set var="facetSelected" value="yes"/>
					<c:set var="name" value="${dimCrumb.label}"/>
					<c:set var="dataId" value="${fn:replace(dimCrumb.label, ' ', '-')}-${facetName}"/>
					<div class="applied-facet ${counter.last ? 'last':''}" data-nstate="${dimCrumb.removeAction.contentPath}${dimCrumb.removeAction.navigationState}" data-id="${dataId}" data-count="${dimCrumb.count}" data-dim="${facetName}">
						<div class="applied-facet-item">
							${name}<span class="icon icon-close close-facet" role="button" tabindex="0"><span class="sr-only">Remove ${name}</span></span>
						</div>
						<div class="applied-facet-${facetName}">
							<div class="facet" data-id="${dataId}" data-cat="${facetName}" data-nstate="${dimCrumb.removeAction.contentPath}${dimCrumb.removeAction.navigationState}">
								<input type="checkbox" id="${dataId}" checked>
								<label for="${dataId}">${name}<span class="ref-count"> (${dimCrumb.count})</span></label>
							</div>
						</div>
					</div>
				</c:if>
			</c:forEach>

			<%-- Search within results term --%>
			<c:if test="${pageType != 'search'}">
				<c:forEach var="searchCrumb" varStatus="counter" items="${content.searchCrumbs}">
					<c:set var="facetSelected" value="yes"/>
					<div class="applied-facet ${counter.last? 'last':''}" data-nstate="${searchCrumb.removeAction.contentPath}${searchCrumb.removeAction.navigationState}">
						<div class="applied-facet-item">
							${fn:escapeXml(searchCrumb.terms)}<span class="icon icon-close close-facet" role="button" tabindex="0"><span class="sr-only">Remove ${fn:escapeXml(searchCrumb.terms)}</span></span>
						</div>
					</div>
				</c:forEach>
			</c:if>

			<%-- if there are facets selected, show 'clear all filters' link --%>
			<c:if test="${facetSelected eq 'yes'}">

				<c:set var="clearAllLink" value="${contextPath}${content.removeAllAction.navigationState}" scope="request"/>

				<c:if test="${pageType == 'search' && fn:length(content.searchCrumbs) > 0}">
					<c:forEach var="searchCrumb" varStatus="counter" items="${content.searchCrumbs}">
						<c:set var="clearAllLink" value="${contextPath}/search/?Ntt=${searchCrumb.terms}" scope="request"/>
					</c:forEach>
				</c:if>

				<%-- the right option clear all link --%>
				<span class="clear-all-link" data-id="clearallfilters">
					<a href="${clearAllLink}">Clear All Filters</a>
				</span>

			</c:if>

		</div>
	</div>
	<!-- end Facet-Breadcrumbs -->

</dsp:page>
