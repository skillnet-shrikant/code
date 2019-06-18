<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" value="${contentItem.mobileHide}"/>
		<dsp:oparam name="true">
			<dsp:getvalueof var="mobileHideClass" value="mobile-hide "/>
		</dsp:oparam>
		<dsp:oparam name="false">
			<dsp:getvalueof var="mobileHideClass" value=""/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" value="${contentItem.desktopHide}"/>
		<dsp:oparam name="true">
			<dsp:getvalueof var="desktopHideClass" value="desktop-hide "/>
		</dsp:oparam>
		<dsp:oparam name="false">
			<dsp:getvalueof var="desktopHideClass" value=""/>
		</dsp:oparam>
	</dsp:droplet>
	<c:if test="${contentItem.customPaddingTitleEnabled}">
		<dsp:getvalueof var="customTitleStyle" value='style="padding: ${contentItem.contentTitlePadding}px 0px" ' />
	</c:if>
	<c:if test="${contentItem.customPaddingRowEnabled}">
		<dsp:getvalueof var="customContentStyle" value='style="padding: ${contentItem.contentRowPadding}px 0px" ' />
	</c:if>
	
	<section id="${contentItem.anchorTag}" class="${mobileHideClass} ${desktopHideClass}">
		<div class="section-row" >
			<c:if test="${not empty contentItem.sectionTitle}">
					<div class="section-title" ${customTitleStyle}>
						<h2>${contentItem.sectionTitle}</h2>
					</div>
				</c:if>
			<c:if test="${not empty contentItem.FooterCommunicationContent}">
				<c:choose>
					<c:when test="${fn:length(contentItem.FooterCommunicationContent) == 1}">
						<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-one"/>
					</c:when>
					<c:when test="${fn:length(contentItem.FooterCommunicationContent) == 2}">
						<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-two"/>
					</c:when>
					<c:when test="${fn:length(contentItem.FooterCommunicationContent) == 3}">
						<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-three"/>
					</c:when>
					<c:when test="${fn:length(contentItem.FooterCommunicationContent) == 4}">
						<dsp:getvalueof var="gridClass" value="promo-grid promo-grid-four"/>
					</c:when>
					<c:otherwise>
						<dsp:getvalueof var="gridClass" value="promo-grid"/>
					</c:otherwise>
				</c:choose>
				
				<ul class="${gridClass}">
					<c:forEach var="element" items="${contentItem.FooterCommunicationContent}">
						<li><dsp:renderContentItem contentItem="${element}" /></li>
					</c:forEach>
				</ul>		
			</c:if>
		</div>
	</section>
</dsp:page>