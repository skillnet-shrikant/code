<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<div class="mobile-refinements">
		<div class="section-title">
			<h2>Refinements</h2>
		</div>
		<div class="hide-refinements">
			<span class="icon icon-close" aria-hidden="true"></span>
			<span class="sr-only">close</span>
		</div>
		<button class="button secondary hide-refinements">Close Refinements</button>
	</div>

	<%-- facet sidebar --%>
	<div class="facet-sidebar">
		<div class="filters" data-filters>
			<div class="category-facet-menu facet-menu">
				<div class="accordion" role="tablist" aria-multiselectable="true" data-accordion>
					<c:forEach var="element" items="${contentItem.navigation}">
						<c:choose>
							<c:when test="${element['dimensionName']=='product.bvReviewsGroup' }">
								<c:if test="${bvEnabled}">
									<dsp:renderContentItem contentItem="${element}" />
								</c:if>
							</c:when>
							<c:otherwise>
								<dsp:renderContentItem contentItem="${element}" />
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</div>
			</div>
		</div>
	</div>
	<%-- show refinements on small screens --%>
	<button class="button secondary show-refinements">Refine</button>

</dsp:page>
