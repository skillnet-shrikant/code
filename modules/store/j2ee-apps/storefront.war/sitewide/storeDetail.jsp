<%--
  - File Name: storeDetail.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the store details page
  --%>

<%@ page import="atg.core.util.StringUtils" %>

<dsp:page>

	<dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach"/>
	<dsp:importbean bean="/com/mff/content/droplet/MFFContentDisplayDroplet"/>
	<dsp:importbean bean="/atg/targeting/RepositoryLookup"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
		<dsp:param name="key" value="${requestScope['articleItemId']}" />
		<dsp:param name="defaultPageTitle" value="Store Detail" />
		<dsp:param name="defaultMetaDescription" value="Store Detail" />
		<dsp:param name="defaultCanonicalURL" value="" />
		<dsp:param name="defaultRobotsIndex" value="index" />
		<dsp:param name="defaultRobotsFollow" value="follow" />
	</dsp:include>

	<layout:default>
		<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
		<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
		<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">home</jsp:attribute>
		<jsp:attribute name="pageType">storeDetail</jsp:attribute>
		<jsp:attribute name="bodyClass">store-detail</jsp:attribute>

		<dsp:setvalue param="articleItem" value="${requestScope['articleItem']}" />
		<dsp:setvalue param="storeId" value="${requestScope['locationId']}" />
		<dsp:setvalue param="articleId" value="${requestScope['articleItemId']}" />

		<%-- breadcrumbs --%>
		<section class="breadcrumbs">
			<ul aria-label="breadcrumbs" role="navigation">
				<li><a href="${contextPath}/" class="crumb">Home</a></li>
				<li><span class="crumb active">Store Detail</span></li>
			</ul>
		</section>

		<div class="two-column-container">
			<div class="section-title">
				<h1>Store Detail</h1>
			</div>
			<div class="two-column-left">

				<div class="mobile-refinements">
					<div class="section-title">
						<h2>Choose a store</h2>
					</div>
					<div class="hide-refinements">
						<span class="icon icon-close" aria-hidden="true"></span>
						<span class="sr-only">close</span>
					</div>
					<button class="button secondary expand hide-sidebar">Back to Content</button>
				</div> <!-- /mobile-refinements -->

				<div class="section-row">

					<h3>
						<dsp:a href="/visit-stores">Visit Stores</dsp:a>
					</h3>

					<dsp:droplet name="RQLQueryForEach">
						<dsp:param name="queryRQL" value="ALL"/>
						<dsp:param name="repository" value="/atg/commerce/locations/LocationRepository"/>
						<dsp:param name="itemDescriptor" value="store"/>
						<dsp:param name="sortProperties" value="+city"/>
						<dsp:oparam name="output">
							<ul class="store-details-nav-list">
								<dsp:getvalueof var="landingPageId"  param="element.storeLandingPage.id" />
								<dsp:getvalueof var="storeId" param="element.locationId"/>
								<dsp:getvalueof var="city" param="element.city"/>
								<c:set var="cityHyphen" value="${fn:replace(city, ' ', '-')}"/>
								<dsp:getvalueof var="state" param="element.stateAddress"/>
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
											<dsp:getvalueof var="website" param="element.website" />
											<c:if test="${not empty childStoreName}">
												<dsp:a href="${website}/department/${fn:toLowerCase(childStoreName)}">
													<dsp:valueof param="page.name"/>
												</dsp:a>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>
								</li>
							</ul>
						</dsp:oparam>
					</dsp:droplet>
				</div> <!-- /section-row -->
			</div>	<!-- /store-locator-left -->

			<dsp:getvalueof var="storeId" param="storeId"/>
			<dsp:getvalueof var="mainImageURL" param="articleItem.mainImage.url" />
			<dsp:getvalueof var="articleHeadline" param="articleItem.headline" />
			<dsp:getvalueof var="body" param="articleItem.body" />

			<dsp:droplet name="RepositoryLookup">
				 <dsp:param name="id" param="storeId"/>
				 <dsp:param name="itemDescriptor" value="store"/>
				 <dsp:param name="repository" bean="/atg/commerce/locations/LocationRepository"/>
				 <dsp:oparam name="output">
					<dsp:getvalueof var="storeItem" param="element" />
				 </dsp:oparam>
			</dsp:droplet>

			<dsp:setvalue param="parentStore" value="${storeItem}"/>
			<dsp:getvalueof var="stateAddress" param="parentStore.stateAddress"/>
			<dsp:getvalueof var="storeId" param="parentStore.locationId"/>
			<dsp:getvalueof var="storeCity" param="parentStore.city"/>
			<dsp:getvalueof var="storePhoneNumber" param="parentStore.phoneNumber"/>

			<div class="two-column-right">
					<%-- show refinements on small screens --%>
					<button class="button secondary expand show-sidebar">Show Stores</button>
				<div class="section-row">

					<div class="store-output" itemscope itemtype="http://schema.org/LocalBusiness">
						<dsp:getvalueof var="homeStore" vartype="java.lang.Object" bean="Profile.myHomeStore"/>
						<c:set var="homeStoreId" value="${fn:replace(homeStore, 'store:', '')}"/>
						<c:if test="${not empty storeCity}">
							<!-- Store landing Information - Start -->
							<div>
							<h2 class="title">The <dsp:valueof param="parentStore.city"/>, <dsp:valueof param="parentStore.stateAddress"/> Fleet Farm</h2>
							<div class="home-store-section">
								<c:choose>
									<c:when test="${homeStoreId eq storeId}">
										<h3><span class="icon icon-locator" aria-hidden="true"></span> MY STORE</h3>
									</c:when>
									<c:otherwise>
										<div class="actions"><button class="button primary make-this-store" data-store-id="${storeId}">MAKE THIS MY STORE</button></div>
									</c:otherwise>
								</c:choose>
							</div>
							</div>
							<c:if test="${empty mainImageURL}">
								<c:set var="mainImageURL" value="//www.fleetfarm.com/content/category/ankeny-ia-fleet-farm.jpg" scope="request" />
							</c:if>
							<img src="${mainImageURL}" alt="Fleet Farm" itemprop="image" />

							<div class="store-detail-section store-name">
								<h3>${articleHeadline}</h3>
							</div>
							<div class="store-detail-section store-name-text">
								${body}
							</div>
							<div class="store-detail-section store-address">
								<span itemprop="name">
									<dsp:valueof param="parentStore.name"/>
								</span>
								<br />
								<div itemprop="address" itemscope itemtype="http://schema.org/PostalAddress">
									<span itemprop="streetAddress">
										<dsp:valueof param="parentStore.address1"/>
										<dsp:valueof param="parentStore.address2"/>
									</span>
									<br />
									<span itemprop="addressLocality"><dsp:valueof param="parentStore.city"/></span>,
									<span itemprop="addressRegion"><dsp:valueof param="parentStore.stateAddress"/></span>
									<span itemprop="postalCode"><dsp:valueof param="parentStore.postalCode"/></span>
									<br />
									<%
										String phoneNumberLink = (String) pageContext.findAttribute("storePhoneNumber");
										phoneNumberLink = "tel:+1" + StringUtils.alphaNumericOnly(phoneNumberLink);
										pageContext.setAttribute("phoneNumberLink", phoneNumberLink);
									%>
									<span itemprop="telephone">
										<a href="${phoneNumberLink}"><dsp:valueof param="parentStore.phoneNumber"/></a>
									</span>
								</div>
								<a href="${contextPath}/sitewide/storeLocator.jsp?locationId=${storeId}">Map &amp; Store Hours</a>
							</div>
							<!-- Store landing Information - End -->
						</c:if>

						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="articleItem.relatedArticles" />
							   <dsp:setvalue param="rltdArticle" paramvalue="element"/>
							   <dsp:oparam name="output">
									<dsp:getvalueof var="mediaURL" param="rltdArticle.mainImage.url" />
									<dsp:getvalueof var="mediaType" param="rltdArticle.mainImage.mediaType" />
									<dsp:getvalueof var="headline" param="rltdArticle.headline" />
									<dsp:getvalueof var="body" param="rltdArticle.body" />
									<!-- First display the headline if it is defined-->
									<c:if test="${not empty headline}">
										<div class="store-detail-section store-name">
											<h3>${headline}</h3>
										</div>
									</c:if>
									<c:if test="${not empty mediaURL}">
										<c:if test="${mediaType eq 'Other'}">
											<div class="store-detail-section store-video">
												<iframe width="100%" height="315" src="${mediaURL}" frameborder="0" allowfullscreen></iframe>
											</div>
										</c:if>
										<c:if test="${mediaType eq 'Image'}">
											<img src="${mediaURL}" />
										</c:if>
									</c:if>
									<!-- Display the body if it is defined-->
									<div class="store-detail-section store-info">
										<c:if test="${not empty body}">
											${body}
										</c:if>
									</div>
									<br />
							</dsp:oparam>
						</dsp:droplet>
					</div>

				</div> <!-- /section-row -->
			</div> <!-- /store-detail-right -->
		</div> <!-- /two-column-container -->
		<%-- <dsp:form id="home-store-form" formid="home-store-form" action="${requestURL}" method="post">
		 	<dsp:input bean="ProfileFormHandler.homeStoreChosen" name="homestore" type="hidden" id="homestore"/>
		 	<!-- <dsp:input bean="ProfileFormHandler.updateMyHomeStore" type="submit" value="Set Home Store" iclass="hide"/> -->
		 	<dsp:input type="hidden" bean="ProfileFormHandler.updateHomeStoreSuccessURL" value="${contextPath}/sitewide/json/updateMyHomeStoreSuccess.jsp" />
			<dsp:input type="hidden" bean="ProfileFormHandler.updateHomeStoreErrorURL" value="${contextPath}/account/json/profileError.jsp" />
		 	<dsp:input type="hidden" bean="ProfileFormHandler.updateMyHomeStore" id="choose-store" name="choose-store" value="Set Home Store" />
		</dsp:form> --%>
	</layout:default>
</dsp:page>
