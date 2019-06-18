<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>

	<c:forEach var="element" items="${contentItem.contents}">
      <dsp:renderContentItem contentItem="${element}"/>
    </c:forEach>
	
</dsp:page>