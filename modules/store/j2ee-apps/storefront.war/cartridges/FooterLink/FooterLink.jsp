<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:if test="${not empty contentItem.linkText}">
		<li>
			<c:choose>
				<c:when test="${not empty contentItem.linkText && contentItem.externalLink}">
					<a href="${contentItem.linkURL}" rel="nofollow" target="_blank">${contentItem.linkText}</a>
				</c:when>
				<c:when test="${not empty contentItem.linkText}">
					<a href="${contentItem.linkURL}">${contentItem.linkText}</a>
				</c:when>
				<c:otherwise>
					${contentItem.linkText}
				</c:otherwise>
			</c:choose>
		</li>

	</c:if>
	
</dsp:page>