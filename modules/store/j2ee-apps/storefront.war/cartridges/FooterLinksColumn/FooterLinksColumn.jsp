<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<div class="footer-links-group">
		<c:if test="${not empty contentItem.columnTitle}">
				<h3>${contentItem.columnTitle} <span class="icon icon-arrow-down" aria-hidden="true"></span></h3>
		</c:if>
		<ul>
			<c:forEach var="element" items="${contentItem.FooterLinks}">
				<dsp:renderContentItem contentItem="${element}" />
			</c:forEach>
		</ul>
	</div>
	
</dsp:page>	