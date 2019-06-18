<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:if test="${not empty contentItem.sectionTitle }">
		<div class="subtitle article-column">${contentItem.sectionTitle}</div>
	</c:if>
	<c:if test="${not empty contentItem.UtilityLinks }">
	<ul>
		<c:forEach var="element" items="${contentItem.UtilityLinks}">
			<li>
				<dsp:renderContentItem contentItem="${element}" />
			</li>
		</c:forEach>
	</ul>
	</c:if>

</dsp:page>
