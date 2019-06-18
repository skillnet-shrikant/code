<dsp:page>

	<%-- Imports --%>
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
	
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" value="${contentItem.active}"/>
		<dsp:oparam name="true">
			<dsp:getvalueof var="isWeeklyEnabled" param="storeItem.pageWeeklyAdEnabled"/>
			<dsp:getvalueof var="isTextPromoEnabled" param="storeItem.pageTextPromotionsEnabled"/>
			<dsp:getvalueof var="isEmailPromoEnabled" param="storeItem.pageEmailPromotionsEnabled"/>
			<dsp:getvalueof var="isFBEnabled" param="storeItem.pageConnectOnFBEnabled"/>
			<dsp:getvalueof var="isCatalogSignupEnabled" param="storeItem.pageCatalogSignupEnabled"/>
			
			<c:if test="${isWeeklyEnabled or isTextPromoEnabled or isEmailPromoEnabled or isFBEnabled or isCatalogSignupEnabled}">
				<li class="store-promo-section ${mobileHideClass} ${desktopHideClass}">
					<div class="text-left">
						<ul>
							<c:if test="${isWeeklyEnabled}">
								<li>
									<span class="icon icon-weekly-ad" aria-hidden="true"></span>
									<div class="store-detail-card">
										<div class="location-details">
											<h2><dsp:valueof param="storeItem.pageWeeklyAdHeader"/></h2>
											<dsp:valueof param="storeItem.pageWeeklyAdBody" valueishtml="true"/>
										</div>
									</div>
								</li>
							</c:if>
							<c:if test="${isTextPromoEnabled}">
								<li>
									<img src="/images/static/mobile-phone-icon.png" alt="text promotion"/>
									<div class="store-detail-card">
										<div  class="location-details">
											<h2><dsp:valueof param="storeItem.pageTextPromotionsHeader"/></h2>
											<dsp:valueof param="storeItem.pageTextPromotionsBody" valueishtml="true"/>
										</div>
									</div>
								</li>
							</c:if>
							<c:if test="${isEmailPromoEnabled}">
								<li>
									<img src="/images/static/email-icon.png" alt="email promotion"/>
									<div class="store-detail-card">
										<div class="location-details">
											<h2><dsp:valueof param="storeItem.pageEmailPromotionsHeader"/></h2>
											<dsp:valueof param="storeItem.pageEmailPromotionsBody" valueishtml="true"/>
										</div>
									</div>
								</li>
							</c:if>
							<c:if test="${isFBEnabled}">
								<li>
									<span class="icon icon-facebook" aria-hidden="true"></span>
									<div class="store-detail-card">
										<div  class="location-details">
											<h2><dsp:valueof param="storeItem.pageConnectOnFBHeader"/></h2>
											<dsp:valueof param="storeItem.pageConnectOnFBBody" valueishtml="true"/>
										</div>
									</div>
								</li>
							</c:if>
							<c:if test="${isCatalogSignupEnabled}">
								<li>
									<span class="icon icon-email" aria-hidden="true"></span>
									<div class="store-detail-card">
										<div  class="location-details">
											<h2><dsp:valueof param="storeItem.pageCatalogSignupHeader"/></h2>
											<dsp:valueof param="storeItem.pageCatalogSignupBody" valueishtml="true"/>
										</div>
									</div>
								</li>
							</c:if>
						</ul>
					</div>
				</li>
				<hr>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
	
</dsp:page>