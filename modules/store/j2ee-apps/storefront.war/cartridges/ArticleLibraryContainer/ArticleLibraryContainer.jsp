<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<!-- three columns of text links w titles  -->
	<c:if test="${not empty contentItem.sectionTitle }">
		<div class="section-row">
			<div class="headline-title">
				<h1>${contentItem.sectionTitle}</h1>
			</div>
		</div>
	</c:if>
	<div class="section-row">
		<c:if test="${not empty contentItem.ArticleLibraryColumns }">
			<div class="three-col-link-list">
				<ul class="cols">
					<c:forEach var="element" items="${contentItem.ArticleLibraryColumns}">
						<li>
							<dsp:renderContentItem contentItem="${element}" />
						</li>
					</c:forEach>
				</ul>
			</div>
		</c:if>
	</div>


</dsp:page>
