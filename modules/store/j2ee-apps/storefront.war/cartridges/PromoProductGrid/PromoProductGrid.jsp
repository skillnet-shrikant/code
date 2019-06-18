<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />
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
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" value="${contentItem.active}"/>
		<dsp:oparam name="true">
			<section id="${contentItem.anchorTag}" class="promo-product-grid-container ${mobileHideClass} ${desktopHideClass}">
				<c:if test="${not empty contentItem.sectionTitle}">
					<div class="section-title" ${customTitleStyle}>
						<h2>${contentItem.sectionTitle}</h2>
					</div>
				</c:if>
				<c:if test="${not empty contentItem.records}">
					 <c:if test="${bvEnabled}">
						<%--<dsp:include otherContext="/bv" page="/productListing/common/bv_plp_script.jsp">
							<dsp:param name="records" value="${contentItem.records}" />
						</dsp:include>
						<c:set var="contextPath" value="${currentContext}" />
						
						--%>
						<c:choose>
							<c:when test="${not empty bvRecords}">
								<dsp:droplet name="/com/mff/droplet/MergeLists">
									<dsp:param name="list1" value="${bvRecords}"/>
									<dsp:param name="list2" value="${contentItem.records}"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="bvRecords" param="finalList" scope="request"/>
									</dsp:oparam>
								</dsp:droplet>
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="bvRecords" value="${contentItem.records}" scope="request"/>
							</c:otherwise>
						</c:choose>
					</c:if>
					<div class="section-row" ${customContentStyle}>
						<div class="section-content">
							<ul class="promo-product-grid">
								<c:forEach var="record" items="${contentItem.records}">
									<li>
										<dsp:include page="/browse/includes/productTile.jsp">
											<dsp:param name="productId" value="${record.attributes['product.repositoryId']}"/>
											<dsp:param name="gridType" value="grid4"/>
											<dsp:param name="idImgPrefix" value="${contentItem.idImgTag}"/>
											<dsp:param name="idNamePrefix" value="${contentItem.idNameTag}"/>
										</dsp:include>
									</li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</c:if>
			</section>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
