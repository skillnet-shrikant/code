<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
		
	<c:choose>
		<c:when test="${not empty contentItem.sectionHref}">
			<a href="${contentItem.sectionHref}">
				<div class="img-container">
					<c:if test="${not empty contentItem.imageURL}">
						<img src="${contentItem.imageURL}" alt="${contentItem.imageAltText}" />
					</c:if>
					<c:if test="${not empty contentItem.sectionTitle}">
						<div class="title-container">${contentItem.sectionTitle}</div>
					</c:if>
				</div>
			</a>
		</c:when>
		<c:otherwise>
			<div class="img-container">
				<c:if test="${not empty contentItem.imageURL}">
					<img src="${contentItem.imageURL}" alt="${contentItem.imageAltText}" />
				</c:if>
				<c:if test="${not empty contentItem.sectionTitle}">
					<div class="title-container">${contentItem.sectionTitle}</div>
				</c:if>
			</div>
		</c:otherwise>
	</c:choose>
	
</dsp:page>