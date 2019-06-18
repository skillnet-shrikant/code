<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<dsp:getvalueof var="sectionTitle" value="${fn:replace(contentItem.sectionTitle, '&', '-')}"/>
	<dsp:getvalueof var="groupName" value="${fn:replace(sectionTitle, ' ', '-')}"/>

	<%-- facet sidebar --%>
	<div class="facet-sidebar">
		<div class="filters" data-filters>
			<div class="category-facet-menu facet-menu">
				<div class="accordion" role="tablist" aria-multiselectable="true" data-accordion>
					<div class="accordion-container">
						<div class="facet-title accordion-title active" role="tab" aria-controls="${groupName}-facet-body" id="${groupName}-facet-title" aria-expanded="true">
							 ${contentItem.sectionTitle} <span class="icon icon-arrow-down"></span>
						</div>
						<div class="facet-body accordion-body" aria-labelledby="${groupName}-facet-title" role="tabpanel" id="${groupName}-facet-body" aria-expanded="true" style="display:block;">
							<div class="accordion-body-content">
								<div class="facet-list links">
									<c:forEach var="element" items="${contentItem.UtilityLinks}">
										<div class="link-facet-items facet-item" >
											<dsp:renderContentItem contentItem="${element}" />
										</div>
									</c:forEach>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

</dsp:page>
