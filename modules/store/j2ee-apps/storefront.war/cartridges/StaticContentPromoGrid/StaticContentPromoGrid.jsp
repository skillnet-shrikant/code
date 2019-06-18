<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<div class="section-row">
		<c:if test="${not empty contentItem.sectionTitle}">
			<div class="subtitle">
				${contentItem.sectionTitle}
			</div>
		</c:if>
		<div class="nine-img-grid">
			<ul class="cols">
				<c:forEach var="promo" items="${contentItem.StaticContentPromos}">
					<li>
						<dsp:renderContentItem contentItem="${promo}" />
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>

</dsp:page>
