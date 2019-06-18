<%--
	- File Name: head.jsp
	- Author(s): KnowledgePath Solutions UX Team
	- Copyright Notice:
	- Description: Outputs the contents of the <head>. Includes the meta tags, js and css includes.
	- Parameters (these are request scoped):
	-	 section - string for section the page falls in (ex browse)
	-	 pageType - string for the page type (ex product)
	-	 metaKeywords (optional) - Page keywords
	-	 metaDescription (optional) - Page Description
	-	 pageTitle (optional) - Page title that appears in the title bar of your browser.
	-
	- Facebook Open Graph Notes:
	-	Tags appear in several sections of this file, search for og: and fb: to find them.
	--%>
<dsp:page>

	<%-- For Endeca Preview --%>
	<dsp:getvalueof bean="/OriginatingRequest.contentItem" var="contentItem" />
	<dsp:getvalueof var="endecaPreviewEnabled" bean="/atg/endeca/assembler/cartridge/manager/AssemblerSettings.previewEnabled"/>

	<%-- Get Context Root --%>
	<dsp:getvalueof var="siteHttpServerName" bean="/mff/MFFEnvironment.siteHttpServerName" />

	<%-- Get Root Url --%>
	<dsp:droplet name="/atg/dynamo/droplet/ProtocolChange">
		<dsp:param name="inUrl" value="https://${siteHttpServerName}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="rootUrl" param="nonSecureUrl" vartype="java.lang.String" scope="request"/>
			<dsp:getvalueof var="secureRootUrl" param="secureUrl" vartype="java.lang.String" scope="request"/>
		</dsp:oparam>
	</dsp:droplet>

	<%-- static --%>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<meta charset="utf-8" />
	<%--viewport size for responsive --%>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<%-- google tag manager --%>
	<%@ include file="/sitewide/third_party/gtmHead.jspf" %>

	<c:if test="${pageType == 'search'}">
		<c:set var="seoRobots" value="noarchive,noindex"/>
	</c:if>

	<c:if test="${!empty seoRobots}">
		<meta name="robots" content="${seoRobots}" />
	</c:if>
	<c:if test="${!empty lastModified}">
		<meta http-equiv="last-modified" content="${lastModified}" />
	</c:if>
	<c:if test="${!empty metaDescription}">
		<meta name="description" content="${metaDescription}" />
	</c:if>
	<c:if test="${!empty metaKeywords}">
		<meta name="keywords" content="${metaKeywords}" />
	</c:if>

	<meta property="og:site_name" content="${siteDisplayName}" />


	<c:if test="${not empty encodedSeoCanonicalURL}">
		<dsp:droplet name="/com/mff/droplet/RemoveSessionIdDroplet">
			<dsp:param name="inputUrl" value="${secureRootUrl}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="modifiedSecureUrl" param="url" vartype="java.lang.String" />
				<c:set var="encodedSeoCanonicalURL" value="${modifiedSecureUrl}${encodedSeoCanonicalURL}" scope="request" />
			</dsp:oparam>
			<dsp:oparam name="empty">
				<c:set var="encodedSeoCanonicalURL" value="${secureRootUrl}${encodedSeoCanonicalURL}" scope="request" />
			</dsp:oparam>
		</dsp:droplet>
		
	</c:if>

	<c:choose>
		<c:when test="${!empty encodedSeoCanonicalURL}">
			<dsp:droplet name="/com/mff/droplet/RemoveSessionIdDroplet">
				<dsp:param name="inputUrl" value="${encodedSeoCanonicalURL}"/>
				<dsp:oparam name="output">
					
					<dsp:getvalueof var="canonicalUrlForSeo" param="url" vartype="java.lang.String" />
					
					<meta property="og:url" content="${canonicalUrlForSeo}">
				</dsp:oparam>
				<dsp:oparam name="empty">
					
					<meta property="og:url" content="${contextPath}/">
				</dsp:oparam>
			</dsp:droplet>
		</c:when>
		<c:when test="${section == 'home' && pageType == ''}"> <%-- setting og:url for home page is important for fb share icon to work --%>
			
			<meta property="og:url" content="${contextPath}/">
		</c:when>
		<c:when test="${section == 'browse' && pageType == 'product'}">
			<%-- if encodedSeoCanonicalURL is empty and we're on a product page --%>
			
			<meta property="og:url" content="https://www.fleetfarm.com/detail/p/${productId}">
		</c:when>
	</c:choose>

	<%--conditional content. Website when on non-product page, product on product page. See other types in FB documentation--%>
	<c:choose>
		<c:when test="${pageType == 'product'}">
			<meta property="og:type" content="product">
			<c:if test="${!empty pageTitle}">
				<meta property="og:title" content="${pageTitle}">
			</c:if>
			<c:if test="${!empty metaDescription}">
				<meta property="og:description" content="${metaDescription}">
			</c:if>
			<meta property="og:image" content="https://www.fleetfarm.com/images/product/${productId}/l/1.jpg">
		</c:when>
		<c:otherwise>
			<meta property="og:type" content="website">
		</c:otherwise>
	</c:choose>
	<%-- END FACEBOOK OPEN GRAPH TAGS --%>

	<%-- Favicon --%>
	<link rel="apple-touch-icon" sizes="180x180" href="${assetPath}/images/favicon/apple-touch-icon.png">
	<link rel="icon" type="image/png" href="${assetPath}/images/favicon/favicon-32x32.png" sizes="32x32">
	<link rel="icon" type="image/png" href="${assetPath}/images/favicon/favicon-16x16.png" sizes="16x16">
	<link rel="manifest" href="${assetPath}/images/favicon/manifest.json">
	<link rel="mask-icon" href="${assetPath}/images/favicon/safari-pinned-tab.svg" color="#fe6319">
	<link rel="shortcut icon" href="${assetPath}/images/favicon/favicon.ico">
	<meta name="msapplication-config" content="${assetPath}/images/favicon/browserconfig.xml">
	<meta name="theme-color" content="#ffffff">

	<c:if test="${!empty encodedSeoCanonicalURL}">
		<dsp:droplet name="/com/mff/droplet/RemoveSessionIdDroplet">
			<dsp:param name="inputUrl" value="${encodedSeoCanonicalURL}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="canonicalUrlForSeo" param="url" vartype="java.lang.String" />
				<link rel="canonical" href="${canonicalUrlForSeo}" />
			</dsp:oparam>
			<dsp:oparam name="empty">
				<dsp:getvalueof var="canonicalUrlForSeo" param="url" vartype="java.lang.String" />
			</dsp:oparam>
		</dsp:droplet>
		
	</c:if>

	<%-- For Endeca Preview --%>
	<c:if test="${endecaPreviewEnabled}">
			<c:if test="${not empty contentItem}">
			<!-- ENDECA PREVIEW TAG -->
			<endeca:pageHead rootContentItem="${contentItem}"/>
			<!-- END ENDECA PREVIEW TAG -->
		</c:if>
	</c:if>

	<%-- turn off auto-phone-number detection in ios (we do it manually) --%>
	<meta name="format-detection" content="telephone=no">
	
</dsp:page>
