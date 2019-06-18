<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	
	<c:choose>
		<c:when test="${contentItem.linkURL.linkType == 'ABSOLUTE'}">
			<a href="${contentItem.linkURL.path}">${contentItem.linkText}</a>
		</c:when>
		<c:when test="${contentItem.linkURL.linkType == 'RELATIVE_NAV'}">
			<a href="${contentItem.linkURL.path}?${contentItem.linkURL.queryString}">${contentItem.linkText}</a>
		</c:when>
	</c:choose>
	
</dsp:page>