<dsp:page>


	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="/OriginatingRequest.contentItem"/>
	<dsp:getvalueof var="facetNoProduct" value="${fn:replace(contentItem.dimensionName, 'product.', '')}"/>
	<dsp:getvalueof var="facetName" value="${fn:replace(facetNoProduct, '.', '-')}"/>
	<dsp:getvalueof var="totalFacets" value="${contentItem.refinements.size()}" />
	<c:choose>
		<c:when test="${facetName == 'category'}">
			<c:set var="divClass" value="links" scope="request" />
			<c:set var="lessLinkLabel" value="Show Less" scope="request" />
			<c:set var="lessURL" value="${contentItem.lessLink.navigationState}" scope="request" />
			<c:set var="moreLinkLabel" value="${contentItem.moreLink.label}" scope="request" />
			<c:set var="moreURL" value="${contentItem.moreLink.navigationState}" scope="request" />			
		</c:when>
		<c:when test="${facetName == 'color'}">
			<c:set var="divClass" value="swatches" scope="request" />
		</c:when>
		<c:when test="${facetName == 'brand'}">
			<c:set var="divClass" value="checkbox" scope="request" />
			<c:set var="lessLinkLabel" value="Show Less" scope="request" />
			<c:set var="lessURL" value="${contentItem.lessLink.navigationState}" scope="request" />
			<c:set var="moreLinkLabel" value="${contentItem.moreLink.label}" scope="request" />
			<c:set var="moreURL" value="${contentItem.moreLink.navigationState}" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="divClass" value="checkbox" scope="request" />
			<c:set var="lessLinkLabel" value="Show Less" scope="request" />
			<c:set var="lessURL" value="${contentItem.lessLink.navigationState}" scope="request" />
			<c:set var="moreLinkLabel" value="${contentItem.moreLink.label}" scope="request" />
			<c:set var="moreURL" value="${contentItem.moreLink.navigationState}" scope="request" />
		</c:otherwise>
	</c:choose>

	<div class="accordion-container">
		<div class="facet-title accordion-title active" role="tab" aria-controls="facet-body-${facetName}" id="facet-title-${facetName}" aria-expanded="true">
			${contentItem.name} <span class="icon icon-arrow-down"></span>
		</div>
		<c:set var="numInitialItems" value="10"/>
		<c:choose>
			<%-- collapsible - restrict height to show only the required number of items in the viewport--%>
			<c:when test="${not empty lessURL}">
				<%-- because each different facet might have different heights for each item --%>
				<c:choose>
					<c:when test="${facetName == 'brand'}">
						<c:set var="itemHeight" value="25" scope="request" />
					</c:when>
					<c:otherwise>
						<c:set var="itemHeight" value="25" scope="request" />
					</c:otherwise>
				</c:choose>
				<c:set var="accordionBodyStyle">
					style="display:block;
						   max-height:${numInitialItems*itemHeight}px;
						   overflow-y:auto;
						   margin-bottom:10px;"
				</c:set>
			</c:when>
			<%-- expandible - so don't restrict height to not show any scrollbars --%>
			<c:when test="${not empty moreURL}">
				<c:set var="accordionBodyStyle">style="display:block;"</c:set>
			</c:when>
			<%-- default - neither more nor less is available --%>
			<c:otherwise>
				<c:set var="accordionBodyStyle">style="display:block;"</c:set>
			</c:otherwise>
		</c:choose>
		<div class="facet-body accordion-body <c:if test='${totalFacets gt 10}'>scrollbar</c:if>" 
			 aria-labelledby="facet-title-${facetName}" 
			 role="tabpanel" id="facet-body-${facetName}" 
			 aria-expanded="true" 
			 ${accordionBodyStyle}
			 >
			<div class="accordion-body-content">
				<div class="facet-list ${divClass}" data-dim="${facetName}">
					<div class="clear-filter" data-cat="${facetName}">
						Clear filters 
					</div>
						
					<c:forEach var="refinement" items="${contentItem.refinements}" varStatus="status">
						<c:set var="name" value="${refinement.label}"/>
						<c:set var="dataId" value="${fn:replace(refinement.label, ' ', '-')}-${facetName}"/>
						<c:set var="count" value=" (${refinement.count})"/>
						<c:choose>
							<c:when test="${not empty brandURIName}">
								<c:set var="refinementContext" value="${refinement.contentPath}/${brandURIName}"/>
							</c:when>
							<c:otherwise>
								<c:set var="refinementContext" value="${refinement.contentPath}" />
							</c:otherwise>
						</c:choose>

						<c:choose>

							<%-- swatches (color) --%>
							<c:when test="${facetName == 'color'}">
								<div class="facet" data-id="${dataId}" data-cat="${facetName}" data-nstate="${refinementContext}${refinement.navigationState}">
									<%-- set tabindex to label because checkbox input is visibly hidden --%>
									<span class="swatch" style="background:${name}" tabindex="0"><span class="icon icon-check" aria-hidden="true"></span></span>
									<input data-label="${name}" type="checkbox" id="${dataId}" tabindex="-1" />
									<label for="${dataId}">
										<c:out value="${name}"/>
										<span class="ref-count">${count}</span>
									</label>
								</div>
							</c:when>

							<%-- links (category) --%>
							<c:when test="${facetName == 'category'}">
								<div class="facet" data-id="${dataId}" data-cat="${facetName}" data-nstate="${refinementContext}${refinement.navigationState}">
									<a data-label="${name}" href="${refinementContext}${refinement.navigationState}">
										<c:out value="${name}"/>
										<span class="ref-count">${count}</span>
									</a>
								</div>
								<c:set var="leafCategory" value="false" scope="request" />
							</c:when>

							<c:when test="${facetName == 'brand'}">
								<div class="facet" data-id="${dataId}" data-cat="${facetName}" data-nstate="${refinementContext}${refinement.navigationState}">
									<input data-label="${name}" type="checkbox" id="${dataId}" />
									<label for="${dataId}">
										<c:out value="${name}"/>
										<span class="ref-count">${count}</span>
									</label>
								</div>
							</c:when>
							<c:when test="${facetName == 'bvReviewsGroup'}">
								<div class="facet" data-id="${dataId}" data-cat="${facetName}" data-nstate="${refinementContext}${refinement.navigationState}">
									<input data-label="${name}" type="checkbox" id="${dataId}" />
									<label class="bvreviews-facet" for="${dataId}">
										<input type="hidden" name="bvFacetRatingGroupValue" id="bvFacetRatingGroupValue" value="${name}" />
										<c:forEach var="i" begin="1" end="5">
											<c:choose>
												<c:when test="${i<=name}">
													<span class="bvreviews-color-orange">&#x2605;</span>
												</c:when>
												<c:otherwise>
													<span class="bvreviews-color">&#x2605;</span>
												</c:otherwise>
											</c:choose>
										</c:forEach>
										<span class="ref-count">${count}</span>
									</label>
								</div>
							</c:when>
							
							<%-- checkboxes (everything else) --%>
							<c:otherwise>
								<div class="facet" data-id="${dataId}" data-cat="${facetName}" data-nstate="${refinementContext}${refinement.navigationState}">
									<input data-label="${name}" type="checkbox" id="${dataId}" />
									<label for="${dataId}">
										<c:out value="${name}"/>
										<span class="ref-count">${count}</span>
									</label>
								</div>
							</c:otherwise>
						</c:choose>
					</c:forEach>
<%--
					<div class="facet-expand-collapse-btn" data-cat="${facetName}">
						<c:choose>
							<c:when test="${not empty lessURL}">
								<a href="${contentItem.lessLink.contentPath}${lessURL}"><c:out value="${lessLinkLabel}" />&nbsp;[-]</a>
							</c:when>
							<c:when test="${not empty moreURL}">
								<a href="${contentItem.moreLink.contentPath}${moreURL}"><c:out value="${moreLinkLabel}" />&nbsp;[+]</a>
							</c:when>
						</c:choose>
					</div>					 
 --%>					
				</div>
			</div>
		</div>


	</div>

</dsp:page>
