<%--
	- File Name: sitemap.jsp
	- Author(s): KnowledgePath Solutions
	- Copyright Notice:
	- Description: This is the sitemap page
	--%>

<dsp:page>

	<%@ page import="com.mff.commerce.catalog.PrimaryNavItem" %>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/endeca/cache/DimensionValueCacheDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach"/>
	<dsp:importbean bean="/mff/commerce/catalog/PrimaryNavDroplet"/>

	<layout:default>
		<jsp:attribute name="pageTitle">Sitemap</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">home</jsp:attribute>
		<jsp:attribute name="pageType">sitemap</jsp:attribute>
		<jsp:attribute name="bodyClass">sitemap</jsp:attribute>
		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><span class="crumb active">Sitemap</span></li>
				</ul>
			</section>

			<div class="sitemap-container">
				<div class="section-title">
					<h2>Sitemap</h2>
				</div>

				<div class="section-row">

					<dsp:droplet name="PrimaryNavDroplet">
						<dsp:getvalueof var="departments" param="departments" />
						<dsp:oparam name="output">

							<c:forEach var="department" items="${departments}" varStatus="status">
								<div class="sitemap-section">
									<div class="sitemap-title">
										<a href="${department.url}">${department.displayName}</a>
									</div>
									<div class="sitemap-section-container">
										<c:if test="${not empty department.subcategories}">
											<ul class="sitemap-list">
												<c:forEach var="category" items="${department.subcategories}" varStatus="status">
													<li>
														<h3 class="sitemap-list-title">
															<a href="${category.url}">${category.displayName}</a>
														</h3>
														<c:if test="${not empty category.subcategories}">
															<ul class="sitemap-list">
																<c:forEach var="subcategory" items="${category.subcategories}" varStatus="status">
																	<li>
																		<a href="${subcategory.url}">${subcategory.displayName}</a>
																	</li>
																</c:forEach>
															</ul>
														</c:if>
													</li>
												</c:forEach>
											</ul>
										</c:if>
									</div>
								</div>
							</c:forEach>

						</dsp:oparam>
					</dsp:droplet>

					<div class="sitemap-section">

						<!-- Static content urls - Start-->
						<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
							<dsp:param name="queryRQL" value="all"/>
							<dsp:param name="repository" value="/com/mff/content/repository/MFFContentRepository"/>
							<dsp:param name="itemDescriptor" value="mffStaticLeftNav"/>
							<dsp:param name="sortProperties" value="+displayOrder"/>
							<dsp:param name="elementName" value="leftNavItem"/>

								<dsp:oparam name="output">
									<div class="sitemap-title">
										<dsp:valueof param="leftNavItem.title"/>
									</div>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="leftNavItem.leftNavLinks" />
										<dsp:param name="elementName" value="element"/>
										<dsp:oparam name="outputStart">
											<div class="sitemap-section-container">
										</dsp:oparam>
										<dsp:oparam name="output">
											<dsp:getvalueof var="childLinks" param="element.relatedLinks"/>
											<dsp:getvalueof var="landingPageId" param="element.contentId"/>
											<dsp:getvalueof var="linkName" param="element.displayName"/>
											<dsp:getvalueof var="linkRedirectUrl" param="element.redirectUrl"/>

											<c:choose>
												<c:when test="${empty childLinks}">
													<ul class="sitemap-list">
														<li>
															<dsp:a href="/static/${linkRedirectUrl}">${linkName}</dsp:a>
														</li>
													</ul>
												</c:when>
												<c:otherwise>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="element.relatedLinks" />
														<dsp:param name="elementName" value="element"/>
														<dsp:oparam name="output">
															<dsp:getvalueof var="subLinkName" param="element.displayName"/>
															<dsp:getvalueof var="subLandingPageId" param="element.contentId"/>
															<dsp:getvalueof var="subLinkRedirectUrl" param="element.redirectUrl"/>
															<ul class="sitemap-list">
																<li>
																	<dsp:a href="/static/${subLinkRedirectUrl}">${subLinkName}</dsp:a>
																</li>
															</ul>
														</dsp:oparam>
													</dsp:droplet>
												</c:otherwise>
											</c:choose>
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
											</div>
										</dsp:oparam>

									</dsp:droplet>

								</dsp:oparam>
							</dsp:droplet>
							<!-- Static content urls - End-->

							<div class="sitemap-title">
								<dsp:a href="/visit-stores">VISIT STORES</dsp:a>
							</div>
							<dsp:droplet name="RQLQueryForEach">
								<dsp:param name="queryRQL" value="ALL"/>
								<dsp:param name="repository" value="/atg/commerce/locations/LocationRepository"/>
								<dsp:param name="itemDescriptor" value="store"/>
								<dsp:param name="sortProperties" value="+city"/>
								<dsp:oparam name="outputStart">
											<div class="sitemap-section-container visit-stores-sitemap">
								</dsp:oparam>
								<!-- <div class="visit-stores-sitemap" style="padding-bottom: 30px"> -->
								<dsp:oparam name="output">
										<dsp:getvalueof var="landingPageId"  param="element.storeLandingPage.id" />
										<dsp:getvalueof var="city" param="element.city"/>
										<dsp:getvalueof var="state" param="element.stateAddress"/>
										<ul class="sitemap-list">
											<li>
											<c:if test="${not empty landingPageId and not empty city and not empty state}">
													<dsp:getvalueof var="website" param="element.website" />
													<dsp:getvalueof var="storeName" param="element.name" />
													<dsp:a href="${website}">
														${storeName}
													</dsp:a><br>
											</c:if>
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="element.relatedArticles" />
												<dsp:param name="elementName" value="page"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="childStoreName" param="page.name"/>
													<c:if test="${not empty childStoreName}">
														<dsp:getvalueof var="website" param="element.website" />
														<dsp:a href="${website}/department/${fn:toLowerCase(childStoreName)}">
															<dsp:valueof param="page.name"/>
														</dsp:a>
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
										</li>
									</ul>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
									<!-- </div> -->
									</div>
								</dsp:oparam>
							</dsp:droplet>

					</div>
				</div>

			</div>
		</jsp:body>
	</layout:default>

</dsp:page>
