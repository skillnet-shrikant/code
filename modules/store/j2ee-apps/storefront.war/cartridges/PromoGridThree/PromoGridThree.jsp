<!-- promo : triple -->
<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:choose>
		<c:when test="${contentItem.noOverLayEnabled1}">
			<dsp:getvalueof var="backgroundClass1" value="${contentItem.overlay1} ${contentItem.overlayVerticalAlignment1} ${contentItem.overlayHorizontalAlignment1} no-overlay"/>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="backgroundClass1" value="${contentItem.overlay1} ${contentItem.overlayVerticalAlignment1} ${contentItem.overlayHorizontalAlignment1}"/>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${contentItem.noOverLayEnabled2}">
			<dsp:getvalueof var="backgroundClass2" value="${contentItem.overlay2} ${contentItem.overlayVerticalAlignment2} ${contentItem.overlayHorizontalAlignment2} no-overlay"/>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="backgroundClass2" value="${contentItem.overlay2} ${contentItem.overlayVerticalAlignment2} ${contentItem.overlayHorizontalAlignment2}"/>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${contentItem.noOverLayEnabled3}">
			<dsp:getvalueof var="backgroundClass3" value="${contentItem.overlay3} ${contentItem.overlayVerticalAlignment3} ${contentItem.overlayHorizontalAlignment3} no-overlay"/>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="backgroundClass3" value="${contentItem.overlay3} ${contentItem.overlayVerticalAlignment3} ${contentItem.overlayHorizontalAlignment3}"/>
		</c:otherwise>
	</c:choose>
	<section>
		<c:if test="${not empty contentItem.sectionTitle}">
			<div class="section-title">
				<h2>${contentItem.sectionTitle}</h2>
			</div>
		</c:if>
		<div class="section-row">
			<div class="section-content">
				<ul class="promo-grid promo-grid-three">
					<li class="promo-tile">
						<a href="${contentItem.imageButtonHref1}" data-tag-title="${contentItem.sectionTitle}" id="${contentItem.idTag1}">
							<c:if test="${not empty contentItem.imageURL1}">
								<img src="${contentItem.imageURL1}" alt="${contentItem.imageAltText1}" />
							</c:if>
							<c:if test="${not empty contentItem.imageH2Text1 || not empty contentItem.imageParaText1 || not empty contentItem.imageButtonLabel1}">
								<div class="promo-tile-content ${backgroundClass1}">
									<c:if test="${not empty contentItem.imageH2Text1}">
										<h2>${contentItem.imageH2Text1}</h2>
									</c:if>
									<c:if test="${not empty contentItem.imageParaText1}">
										<p>${contentItem.imageParaText1}</p>
									</c:if>
									<c:if test="${contentItem.buttonCustomWidthEnabled1}">
										<dsp:getvalueof var="customStyle1" value='style="width: ${contentItem.buttonCustomWidth1}%" ' />
									</c:if>
									<c:if test="${not empty contentItem.imageButtonLabel1}">
										<span class="button primary" ${customStyle1}>${contentItem.imageButtonLabel1}</span>
									</c:if>
								</div>
							</c:if>
						</a>
					</li>
					<li class="promo-tile">
						<a href="${contentItem.imageButtonHref2}" data-tag-title="${contentItem.sectionTitle}" id="${contentItem.idTag2}">
							<c:if test="${not empty contentItem.imageURL2}">
								<img src="${contentItem.imageURL2}" alt="${contentItem.imageAltText2}" />
							</c:if>
							<c:if test="${not empty contentItem.imageH2Text2 || not empty contentItem.imageParaText2 || not empty contentItem.imageButtonLabel2}">
								<div class="promo-tile-content right ${backgroundClass2}">
									<c:if test="${not empty contentItem.imageH2Text2}">
										<h2>${contentItem.imageH2Text2}</h2>
									</c:if>
									<c:if test="${not empty contentItem.imageParaText2}">
										<p>${contentItem.imageParaText2}</p>
									</c:if>
									<c:if test="${contentItem.buttonCustomWidthEnabled2}">
										<dsp:getvalueof var="customStyle2" value='style="width: ${contentItem.buttonCustomWidth2}%" ' />
									</c:if>
									<c:if test="${not empty contentItem.imageButtonLabel2}">
										<span class="button primary" ${customStyle2}>${contentItem.imageButtonLabel2}</span>
									</c:if>
								</div>
							</c:if>
						</a>
					</li>
					<li class="promo-tile">
						<a href="${contentItem.imageButtonHref3}" data-tag-title="${contentItem.sectionTitle}" id="${contentItem.idTag3}">
							<c:if test="${not empty contentItem.imageURL3}">
								<img src="${contentItem.imageURL3}" alt="${contentItem.imageAltText3}" />
							</c:if>
							<c:if test="${not empty contentItem.imageH2Text3 || not empty contentItem.imageParaText3 || not empty contentItem.imageButtonLabel3}">
								<div class="promo-tile-content right ${backgroundClass3}">
									<c:if test="${not empty contentItem.imageH2Text3}">
										<h2>${contentItem.imageH2Text3}</h2>
									</c:if>
									<c:if test="${not empty contentItem.imageParaText3}">
										<p>${contentItem.imageParaText3}</p>
									</c:if>
									<c:if test="${contentItem.buttonCustomWidthEnabled3}">
										<dsp:getvalueof var="customStyle3" value='style="width: ${contentItem.buttonCustomWidth3}%" ' />
									</c:if>
									<c:if test="${not empty contentItem.imageButtonLabel3}">
										<span class="button primary" ${customStyle3}>${contentItem.imageButtonLabel3}</span>
									</c:if>
								</div>
							</c:if>
						</a>
					</li>
				</ul>
	    </div>
		</div>
	</section>
</dsp:page>
