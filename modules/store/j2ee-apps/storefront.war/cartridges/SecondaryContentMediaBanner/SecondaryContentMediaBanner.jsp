<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:if test="${not empty contentItem.imageURL}">
		<div class="managed-content-slot">
			<div class="managed-content-image">
				<c:choose>
					<c:when test="${not empty contentItem.link && not empty contentItem.link.path }">
						<a href="${contentItem.link.path}"><img src="${contentItem.imageURL}" /></a>
					</c:when>
					<c:otherwise>
						<img src="${contentItem.imageURL}" />
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</c:if>
</dsp:page>