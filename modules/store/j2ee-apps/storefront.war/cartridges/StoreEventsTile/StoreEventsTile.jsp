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
			<dsp:getvalueof var="activeStoreEventsList" param="storeItem.activeStoreEvents"/>
			<li class="store-event-section ${mobileHideClass} ${desktopHideClass}">
				<div class="text-left">
					<h2><dsp:valueof param="storeItem.pageStoreEventsHeader"/></h2>
					<ul>
						<c:choose>
							<c:when test="${empty activeStoreEventsList}">
								<li><dsp:valueof param="storeItem.pageStoreNoEventsMessage"/></li>
							</c:when>
							<c:otherwise>
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="storeItem.activeStoreEvents" />
									<dsp:param name="elementName" value="storeEvents" />
									<dsp:param name="sortProperties" value="+eventStartTime"/>
									<dsp:oparam name="output">
										<li>
											<div class="event-card">
												<div class="event-date">
													<p class="event-day"><dsp:valueof param="storeEvents.eventDate" date="EEEEEEEE"/></p>
													<strong><dsp:valueof param="storeEvents.eventDate" date="MM/dd"/></strong>
												</div>
												<div  class="event-details">
													<strong><dsp:valueof param="storeEvents.eventDescription"/></strong><br>
													<span class="store-time"><dsp:valueof param="storeEvents.eventStartTime" date="h:mmaa"/> - <dsp:valueof param="storeEvents.eventEndTime" date="h:mmaa"/></span><br>
		   											<div class="view-store-details">
														<dsp:a class="view-details modal-trigger" href="/sitewide/ajax/storeEventDescriptionModal.jsp" data-target="discontinued-item-policy-modal" data-size="small"><dsp:valueof param="storeEvents.eventShortDetail"/>
															<dsp:param name="msg" param="storeEvents.eventLongDetail"/>
															<dsp:param name="title" param="storeEvents.eventDescription"/>
														</dsp:a>
													</div>
												</div>
											</div>
											<hr class="divider">
										</li>
									</dsp:oparam>
								</dsp:droplet>
							</c:otherwise>
							
						</c:choose>
						
					</ul>
				</div>
			</li>
			<hr>
		</dsp:oparam>
	</dsp:droplet>
	
</dsp:page>