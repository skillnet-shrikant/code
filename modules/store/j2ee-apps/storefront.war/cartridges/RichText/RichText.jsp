<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:if test="${not empty contentItem.richTextContent}">
		${contentItem.richTextContent}
	</c:if>
</dsp:page>