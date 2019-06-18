<dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach"/>
<dsp:importbean bean="/atg/targeting/RepositoryLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<!-- facet- classes added for accordion appearance consistency  -->
<div class="facet-menu">
	<div id="staticContentAccordion" class="accordion" role="tablist" aria-multiselectable="true" data-accordion>
	<!-- sections -->
	<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
			<dsp:param name="queryRQL" value="all"/>
			<dsp:param name="repository" value="/com/mff/content/repository/MFFContentRepository"/>
			<dsp:param name="itemDescriptor" value="mffStaticLeftNav"/>
			<dsp:param name="sortProperties" value="+displayOrder"/>
			<dsp:param name="elementName" value="leftNavItem"/>

			<dsp:oparam name="output">
				<dsp:getvalueof var="mainCounter" param="count"/>
				<div class="accordion-container top-level">
				<div class="facet-title accordion-title" role="tab" aria-controls="accordion-content-${mainCounter}" id="accordion-title-${mainCounter}"></span><dsp:valueof param="leftNavItem.title"/> <span class="icon icon-arrow-down"></span></div>

					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="leftNavItem.leftNavLinks" />
						<dsp:param name="elementName" value="element"/>
						<dsp:oparam name="outputStart">
							<div class="facet-body accordion-body nest-target" aria-labelledby="accordion-title-${mainCounter}" role="tabpanel" id="accordion-content-${mainCounter}">
								<div class="accordion-body-content">
									<div class="facet-list links">
										<ul>
						</dsp:oparam>
						<dsp:oparam name="output">
							<dsp:getvalueof var="childLinks" param="element.relatedLinks"/>
							<dsp:getvalueof var="landingPageId" param="element.contentId"/>
							<dsp:getvalueof var="linkName" param="element.displayName"/>
							<dsp:getvalueof var="linkRedirectUrl" param="element.redirectUrl"/>
							<c:choose>
								<c:when test="${empty childLinks}">
									<li data-articleid="${landingPageId}">
										<dsp:a href="/static/${linkRedirectUrl}">${linkName}</dsp:a>

									</li>
								</c:when>
								<c:otherwise>
									<!-- nested accordion -->
									<li class="has-submenu accordion-container">
										<div class="accordion-title" role="tab" aria-expanded="false" aria-controls="content-menu-return-exchange" id="content-title-return-exchange"><dsp:valueof param="element.displayName"/><span class="icon icon-arrow-down white" aria-hidden="true"></span></div>

									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="element.relatedLinks" />
										<dsp:param name="elementName" value="element"/>
										<dsp:oparam name="outputStart">
											<div class="facet-body accordion-body nest-target" aria-labelledby="content-title-return-exchange" role="tabpanel" id="content-menu-return-exchange">
											<div class="accordion-body-content">
												<div class="facet-list links">
													<ul aria-hidden="true" id="subcategories-return-exchange" class="accordion" role="tablist" aria-multiselectable="false" data-accordion>
										</dsp:oparam>
										<dsp:oparam name="output">
										<dsp:getvalueof var="subLinkName" param="element.displayName"/>
										<dsp:getvalueof var="subLandingPageId" param="element.contentId"/>
										<dsp:getvalueof var="subLinkRedirectUrl" param="element.redirectUrl"/>
											<li data-articleid="${subLandingPageId}">
												<dsp:a href="/static/${subLinkRedirectUrl}">${subLinkName}</dsp:a>
											</li>
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
											</ul></div></div></div></li>
										</dsp:oparam>
									</dsp:droplet>

								</c:otherwise>
							</c:choose>

						</dsp:oparam>
						<dsp:oparam name="outputEnd">
							</ul></div></div></div>
						</dsp:oparam>
					</dsp:droplet>
				</div>
			</dsp:oparam>
		</dsp:droplet>

	</div>
</div>
