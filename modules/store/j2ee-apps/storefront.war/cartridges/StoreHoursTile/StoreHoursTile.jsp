<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
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
			<dsp:getvalueof var="isComingSoon" param="storeItem.pageComingSoonEnabled"/>
			
			<li class="store-hours-section ${mobileHideClass} ${desktopHideClass}">
				<div class="text-left">
					<h2><dsp:valueof param="storeItem.pageStoreHoursHeader"/></h2>
					<ul>
						<c:choose>
							<c:when test="${isComingSoon}">
								<li><span>Location coming soon!</span></li>
							</c:when>
							<c:otherwise>
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="storeItem.standardStoreHoursList" />
									<dsp:param name="elementName" value="storeHours" />
									<dsp:oparam name="output">
										<li><span><dsp:valueof param="storeHours.dayType"/></span>:&nbsp;&nbsp;<span class="store-time"><dsp:valueof param="storeHours.openingTime" date="haa"/>&nbsp;-&nbsp;<span><dsp:valueof param="storeHours.closingTime" date="haa"/></li>
									</dsp:oparam>
								</dsp:droplet>
								<dsp:getvalueof var="holidayList" param="storeItem.holidayStoreHoursList" />
								<c:if test="${not empty holidayList}">
									<li class="text-left store-holidays-section">
										<div id="product-info-accordion" class="accordion" role="tablist" aria-multiselectable="true" data-accordion>
											<div class="accordion-container">
												<div class="accordion-title left" role="tab" aria-controls="panel1" id="tab1">
													<p class="holiday-header">Holiday Hours</p>&nbsp;<span class="icon icon-plus" aria-hidden="true"></span>
												</div>
												<div class="accordion-body" aria-labelledby="tab1" role="tabpanel" id="panel1">
													<div class="accordion-body-content">
														<dsp:droplet name="ForEach">
															<dsp:param name="array" param="storeItem.holidayStoreHoursList" />
																<dsp:param name="elementName" value="holidayHours" />
																<dsp:oparam name="output">
																	<dsp:getvalueof param="holidayHours.isClosed" var="isClosed"/>
																	<c:choose>
																		<c:when test="${isClosed}">
																			<p><span><dsp:valueof param="holidayHours.holidayDate" date="MM/dd" /></span>&nbsp;&nbsp;<dsp:valueof param="holidayHours.holidayDescription" valueishtml="true"/>:&nbsp;&nbsp;<span>Closed</span></p>
																		</c:when>
																		<c:otherwise>
																			<p><span><dsp:valueof param="holidayHours.holidayDate" date="MM/dd" /></span>&nbsp;&nbsp;<dsp:valueof param="holidayHours.holidayDescription" valueishtml="true"/>:&nbsp;&nbsp;<span class="store-time"><dsp:valueof param="holidayHours.openingTime" date="haa"/>&nbsp;-&nbsp;<span class="store-time"><dsp:valueof param="holidayHours.closingTime" date="haa"/></p>
																		</c:otherwise>
																	</c:choose>
																</dsp:oparam>
														</dsp:droplet>
													</div>
												</div>
											</div>
										</div>
									</li>
								</c:if>
							</c:otherwise>
						</c:choose>
					</ul>
				</div>
			</li>
			<hr>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>