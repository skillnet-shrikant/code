<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<dsp:getvalueof var="listrakEnabled" bean="/mff/MFFEnvironment.listrakEnabled"/>

	<div class="footer-email-signup">
		<c:if test="${not empty contentItem.sectionTitle}">
			<h3>${contentItem.sectionTitle}</h3>
		</c:if>
		<c:if test="${not empty contentItem.paraText}">
			<p>${contentItem.paraText}</p>
		</c:if>
		<c:if test="${not empty contentItem.buttonText}">
			<c:choose>
				<c:when test="${contentItem.externalLink}" >
					<a href="${contentItem.buttonUrl}" class="button tertiary text-signup-btn" rel="nofollow" target="_blank">${contentItem.buttonText}</a>
				</c:when>
				<c:otherwise>
					<a href="${contentItem.buttonUrl}" class="button tertiary">${contentItem.buttonText}</a>
				</c:otherwise>
		</c:choose>
		</c:if>
		
	</div>
	
</dsp:page>