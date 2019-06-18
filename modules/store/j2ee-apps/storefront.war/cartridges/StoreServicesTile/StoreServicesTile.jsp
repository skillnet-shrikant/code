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
			<dsp:getvalueof var="pageStoreServiceHeader" param="storeItem.pageStoreServiceHeader"/>
			<dsp:getvalueof var="isGasMartEnabled" param="storeItem.pageGasMartEnabled"/>
			<dsp:getvalueof var="isCStoreEnabled" param="storeItem.pageCStoreEnabled"/>
			<dsp:getvalueof var="isCarWashEnabled" param="storeItem.pageCarWashEnabled"/>
			<dsp:getvalueof var="isAutoServiceEnabled" param="storeItem.pageAutoServiceCenterEnabled"/>
			<dsp:getvalueof var="isGardenCenterEnabled" param="storeItem.pageGardenCenterEnabled"/>
			
			<li class="store-service-section ${mobileHideClass} ${desktopHideClass}">
				<div class="text-left">
					<h2><dsp:valueof param="storeItem.pageStoreServiceHeader"/></h2>
					<ul>
						<c:if test="${isGasMartEnabled}">
							<li>
								<img src="/images/static/gas-pump-icon.png" alt="gas mart"/>
								<div class="store-detail-card">
									<div  class="location-details">
										<strong><dsp:valueof param="storeItem.pageGasMartHeader"/></strong><br>
										<dsp:valueof param="storeItem.pageGasMartBody" valueishtml="true"/>
									</div>
								</div>
							</li>
						</c:if>
						<c:if test="${isCStoreEnabled}">
							<li>
								<img src="/images/static/c-store-icon.png" alt="c store"/>
								<div class="store-detail-card">
									<div  class="location-details">
										<strong><dsp:valueof param="storeItem.pageCStoreHeader"/></strong><br>
										<dsp:valueof param="storeItem.pageCStoreBody" valueishtml="true"/>
									</div>
								</div>
							</li>
						</c:if>
						<c:if test="${isCarWashEnabled}">
							<li>
								<img src="/images/static/car-wash-icon.png" alt="car wash"/>
								<div class="store-detail-card">
									<div  class="location-details">
										<strong><dsp:valueof param="storeItem.pageCarWashHeader"/></strong><br>
										<dsp:valueof param="storeItem.pageCarWashBody" valueishtml="true"/>
									</div>
								</div>
							</li>
						</c:if>
						<c:if test="${isAutoServiceEnabled}">
							<li>
								<img src="/images/static/auto-service-icon.png" alt="auto service"/>
								<div class="store-detail-card">
									<div  class="location-details">
										<strong><dsp:valueof param="storeItem.pageAutoServiceCenterHeader"/></strong><br>
										<dsp:valueof param="storeItem.pageAutoServiceCenterBody" valueishtml="true"/>
									</div>
								</div>
							</li>
						</c:if>
						<c:if test="${isGardenCenterEnabled}">
							<li>
								<img src="/images/static/watering-can-icon.png" alt="garden center"/>
								<div class="store-detail-card">
									<div  class="location-details">
										<strong><dsp:valueof param="storeItem.pageGardenCenterHeader"/></strong><br>
										<dsp:valueof param="storeItem.pageGardenCenterBody" valueishtml="true"/>
									</div>
								</div>
							</li>
						</c:if>
					</ul>
				</div>
			</li>
			<hr>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>