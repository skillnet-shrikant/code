<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />
	<dsp:getvalueof var="seoReviews" param="seoReviews" />
	<dsp:getvalueof var="seoRatings" param="seoRatings" />

	<%-- product info accordion --%>
	<div id="product-info-accordion" class="accordion" role="tablist" aria-multiselectable="true" data-accordion>

		<%-- product overview --%>
		<dsp:droplet name="IsEmpty">
			<dsp:param name="value" param="productItem.longDescription"/>
			<dsp:oparam name="false">
				<div class="accordion-container">
					<div class="accordion-title" role="tab" aria-controls="panel1" id="tab1">
						Product Overview <span class="icon icon-plus" aria-hidden="true"></span>
					</div>
					<div class="accordion-body" aria-labelledby="tab1" role="tabpanel" id="panel1">
						<div class="accordion-body-content">
							<p><span itemprop="description"><dsp:valueof param="productItem.longDescription" valueishtml="true"/></span></p>
						</div>
					</div>
				</div>
			</dsp:oparam>
		</dsp:droplet>

		<%-- specifications --%>
		<dsp:getvalueof var="sellingPoints" param="productItem.sellingPoints" />
		<dsp:getvalueof var="specs" value="${fn:split(sellingPoints, '^;')}" />
		<dsp:getvalueof var="isFromQuickView" param="isFromQuickView" />
		<dsp:droplet name="IsEmpty">
			<dsp:param name="value" value="${isFromQuickView}"/>
			<dsp:oparam name="true">
				<c:set var="isFromQuickView" value="false" />
			</dsp:oparam>
			<dsp:oparam name="false">
				<c:set var="isFromQuickView" value="true" />
			</dsp:oparam>

		</dsp:droplet>

		<c:set var="showSpecs" value="false" scope="request" />
		<dsp:droplet name="IsEmpty">
			<dsp:param name="value" value="${specs}"/>
			<dsp:oparam name="false">
				<c:set var="showSpecs" value="true" scope="request" />
			</dsp:oparam>
			<dsp:oparam name="true">
				<dsp:droplet name="Switch">
					<dsp:param name="value" param="productItem.madeInUsa"/>
					<dsp:oparam name="true">
						<c:set var="showSpecs" value="true" scope="request" />
					</dsp:oparam>
					<dsp:oparam name="false">
						<dsp:droplet name="IsEmpty">
							<dsp:param name="value" param="productItem.chokingHazards"/>
							<dsp:oparam name="false">
								<c:set var="showSpecs" value="true" scope="request" />
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>

		<c:if test="${showSpecs}">
			<div class="accordion-container">
				<div class="accordion-title" role="tab" aria-controls="panel2" id="tab2">
					Specifications <span class="icon icon-plus" aria-hidden="true"></span>
				</div>
				<div class="accordion-body" aria-labelledby="tab2" role="tabpanel" id="panel2">
					<div class="accordion-body-content">
						<ul>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" value="${specs}"/>
								<dsp:param name="elementName" value="spec"/>
								<dsp:oparam name="output">
									<li><dsp:valueof param="spec" /></li>
								</dsp:oparam>
							</dsp:droplet>
						</ul>

						<%-- product spec images --%>
						<div class="product-spec-images">

							<%-- made in usa --%>
							<dsp:droplet name="Switch">
								<dsp:param name="value" param="productItem.madeInUsa"/>
								<dsp:oparam name="true">
									<img src="${contextPath}/resources/images/made-in-usa.jpg" class="product-spec-image" alt="Made In USA"/>
								</dsp:oparam>
							</dsp:droplet>

							<%-- choking hazards --%>
							<dsp:getvalueof var="chokingHazards" param="productItem.chokingHazards" />
							<dsp:getvalueof var="hazards" value="${fn:split(chokingHazards, '^')}" />
							<dsp:droplet name="ForEach">
								<dsp:param name="array" value="${hazards}"/>
								<dsp:param name="elementName" value="hazard"/>
								<dsp:oparam name="output">
									<!--hazard <dsp:valueof param="index" />: <dsp:valueof param="hazard" />-->
									<dsp:droplet name="Switch">
										<dsp:param name="value" param="hazard"/>
										<dsp:oparam name="1">
											<img src="${contextPath}/resources/images/choking-hazard-1.jpg" class="product-spec-image" alt="Choking Hazard" />
										</dsp:oparam>
										<dsp:oparam name="2">
											<img src="${contextPath}/resources/images/choking-hazard-2.jpg" class="product-spec-image" alt="Choking Hazard" />
										</dsp:oparam>
										<dsp:oparam name="3">
											<img src="${contextPath}/resources/images/choking-hazard-3.jpg" class="product-spec-image" alt="Choking Hazard" />
										</dsp:oparam>
										<dsp:oparam name="4">
											<img src="${contextPath}/resources/images/choking-hazard-4.jpg" class="product-spec-image" alt="Choking Hazard" />
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>

						</div>

					</div>
				</div>
			</div>
		</c:if>

		<%-- more info --%>
		<div class="accordion-container">
			<div class="accordion-title" role="tab" aria-controls="panel3" id="tab3">
				More Info <span class="icon icon-plus" aria-hidden="true"></span>
			</div>
			<div class="accordion-body" aria-labelledby="tab3" role="tabpanel" id="panel3">
				<div class="accordion-body-content">
					<dsp:a href="${contextPath}/browse/ajax/emailProductModal.jsp" class="modal-trigger" data-target="email-product-modal">Send to a Friend
						<dsp:param name="productId" param="productItem.id"/>
					</dsp:a><br>
					<a href="${contextPath}/sitewide/ajax/returnPolicyModal.jsp" class="modal-trigger" data-target="return-policy-modal" data-size="medium">Return policy</a><br>
					<a href="${contextPath}/sitewide/ajax/warrantyModal.jsp" class="modal-trigger" data-target="warranty-modal" data-size="small">Warranty Info</a><br>
					<a href="${contextPath}/sitewide/ajax/taxExemptModal.jsp" class="modal-trigger" data-target="tax-exempt-modal" data-size="medium">Are you Tax Exempt?</a><br>
				</div>
			</div>
		</div>

		<%-- bazaarvoice reviews --%>
		<dsp:droplet name="Switch">
			<dsp:param name="value" bean="MFFEnvironment.bvEnabled"/>
			<dsp:oparam name="true">
				<c:if test="${!isFromQuickView}">
					<div class="accordion-container">
						<div class="accordion-title" role="tab" aria-controls="panel4" id="tab4">
							Reviews <span class="icon icon-plus" aria-hidden="true"></span>
						</div>
						<div class="accordion-body" aria-labelledby="tab4" role="tabpanel" id="panel4">
							<div class="accordion-body-content">
								<dsp:include otherContext="/bv" page="/productDisplay/reviews/bv_reviews_container.jsp">
									<dsp:param name="seoReviews" value="${seoReviews}" />
								</dsp:include>
								<c:set var="contextPath" value="${currentContext}" />
							</div>
						</div>
					</div>
				</c:if>
			</dsp:oparam>
		</dsp:droplet>

	</div>

</dsp:page>
