<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:if test="${not empty contentItem.FooterCommunicationContent}">
		<section class="footer-communication">
			<c:forEach var="element" items="${contentItem.FooterCommunicationContent}">
				<dsp:renderContentItem contentItem="${element}" />
			</c:forEach>		
		</section>
	</c:if>
	<c:if test="${not empty contentItem.FooterLinkContent}">		
		<section class="footer-links-container">
			<div class="footer-links">
				<c:forEach var="contentElement" items="${contentItem.FooterLinkContent}"> 
						<dsp:renderContentItem contentItem="${contentElement}" />
					</c:forEach> 
			</div>
		</section>
	</c:if>
</dsp:page>	