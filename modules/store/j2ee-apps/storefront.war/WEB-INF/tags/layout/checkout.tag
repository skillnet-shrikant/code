<%--
  Tag for checkout page wrapper. Responsible for wrapping contents of cart and checkout pages with html, head, header and footer.
--%>
<%@ include file="/sitewide/fragments/tags.jspf" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="pageTitle"%>
<%@ attribute name="metaDescription"%>
<%@ attribute name="metaKeywords"%>
<%@ attribute name="seoCanonicalURL"%>
<%@ attribute name="seoRobots"%>
<%@ attribute name="lastModified"%>
<%@ attribute name="section"%>
<%@ attribute name="pageType"%>
<%@ attribute name="bodyClass"%>

<%-- Set page vars --%>
<c:set var="siteDisplayName" value="Fleet Farm" scope="request"/>
<c:set var="encodedSeoCanonicalURL" scope="request"><c:url value="${seoCanonicalURL}"/></c:set>
<c:set var="jsController" value="${section}" scope="request"/>
<c:set var="jsAction" value="${pageType}" scope="request"/>
<c:set var="pageTitle" value="${pageTitle}" scope="request"/>
<c:set var="pageType" value="${pageType}" scope="request"/>

<!doctype html>
<%-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ --%>
<!--[if lt IE 7 ]> <html class="no-js ie6 ${deviceClass}" lang="en"> <![endif]-->
<!--[if IE 7 ]>    <html class="no-js ie7 ${deviceClass}" lang="en"> <![endif]-->
<!--[if IE 8 ]>    <html class="no-js ie8 ${deviceClass}" lang="en"> <![endif]-->
<!--[if IE 9 ]>    <html class="no-js ie9 ${deviceClass}" lang="en"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--><html class="no-js ${deviceClass}" lang="en"><!--<![endif]-->
	<head>
		<title>${pageTitle}</title>

		<%-- Meta stuff --%>
		<jsp:include page="/sitewide/head.jsp" />

		<%-- don't include css and js for ajax requests --%>
		<c:if test="${!isAjax}">
			<%-- CSS Imports --%>
			<!--[if lt IE 9]><link rel="stylesheet" href="${assetPath}/css/main-ie8.css?ver=${assetVersion}" type="text/css" charset="utf-8"/><![endif]-->
			<!--[if gt IE 8]><!--><link rel="stylesheet" href="${assetPath}/css/main.css?ver=${assetVersion}" type="text/css"/><!--<![endif]-->

			<%-- Modernizr: included in head so we don't render without it --%>
			<script type="text/javascript" src="${assetPath}/js/modernizr.min.js?ver=${assetVersion}"></script>
			
						
			<%-- Code for VWO A/B testing code snippet --%>
			<!-- Start Visual Website Optimizer Asynchronous Code -->
				<script type='text/javascript'>
					var _vwo_code=(function(){
					var account_id=364119,
					settings_tolerance=2000,
					library_tolerance=2500,
					use_existing_jquery=false,
					/* DO NOT EDIT BELOW THIS LINE */
					f=false,d=document;return{use_existing_jquery:function(){return use_existing_jquery;},library_tolerance:function(){return library_tolerance;},finish:function(){if(!f){f=true;var a=d.getElementById('_vis_opt_path_hides');if(a)a.parentNode.removeChild(a);}},finished:function(){return f;},load:function(a){var b=d.createElement('script');b.src=a;b.type='text/javascript';b.innerText;b.onerror=function(){_vwo_code.finish();};d.getElementsByTagName('head')[0].appendChild(b);},init:function(){settings_timer=setTimeout('_vwo_code.finish()',settings_tolerance);var a=d.createElement('style'),b='body{opacity:0 !important;filter:alpha(opacity=0) !important;background:none !important;}',h=d.getElementsByTagName('head')[0];a.setAttribute('id','_vis_opt_path_hides');a.setAttribute('type','text/css');if(a.styleSheet)a.styleSheet.cssText=b;else a.appendChild(d.createTextNode(b));h.appendChild(a);this.load('//dev.visualwebsiteoptimizer.com/j.php?a='+account_id+'&u='+encodeURIComponent(d.URL)+'&r='+Math.random());return settings_timer;}};}());_vwo_settings_timer=_vwo_code.init();
				</script>
			<!-- End Visual Website Optimizer Asynchronous Code -->
			
		</c:if>
	</head>

	<body class="${bodyClass}" data-controller="${jsController}" data-action="${jsAction}">

		<%-- google tag manager --%>
		<%@ include file="/sitewide/third_party/gtmBody.jspf" %>

		<%-- only show header and include wrapping divs for non ajax and non-iframe requests --%>
		<c:if test="${!isAjax && !isProxy}">
			<jsp:include page="/sitewide/analytics/analytics.jsp">
				<jsp:param name="title" value="${pageTitle}"/>
				<jsp:param name="analyticPageType" value="${pageType}"/>
			</jsp:include>
			<div class="off-canvas-wrap" data-offcanvas>
				<div class="inner-wrap">

					<%-- Show either the mobile header or the regular header --%>
					<c:choose>
						<c:when test="${isMobile == true}">
							<%-- For mobile we only include the mobile (small) header.
									The rendered file will have a smaller footprint for mobile users. --%>
							<c:import url="/sitewide/headerCheckoutMobile.jsp"/>
						</c:when>
						<c:otherwise>
							<%-- otheriwse include both the desktop/tablet (large) large header and the mobile (small) header --%>
							<c:import url="/sitewide/headerCheckout.jsp"/>
							<c:import url="/sitewide/headerCheckoutMobile.jsp"/>
						</c:otherwise>
					</c:choose>

					<%-- Body content --%>
					<div class="site-wrapper">
		</c:if>

						<jsp:doBody />

		<%-- only include wrapping divs for non ajax and non-iframe requests --%>
		<c:if test="${!isAjax && !isProxy}">
					</div>

					<%-- footer --%>
					<jsp:include page="/sitewide/footer.jsp">
						<jsp:param name="isCheckout" value="true" />
					</jsp:include>

					<%-- back to top --%>
					<%@ include file="/sitewide/fragments/backToTop.jspf" %>

					<a class="exit-off-canvas"><span class="sr-only">Close Navigation</span></a>
				</div><%-- /inner-wrap --%>
			</div><%-- /off-canvas-wrap --%>
		</c:if>

		<%-- listrak --%>
		<%@ include file="/sitewide/third_party/listrak_browse.jspf" %>
		<%@ include file="/sitewide/third_party/listrak_framework.jspf" %>

		<%-- iovation --%>
		<%@ include file="/sitewide/third_party/iovation.jspf" %>

		<c:choose>
			<c:when test="${!isAjax && !isProxy}">
				<%-- Javascript --%>
				<script	type="text/javascript" src="${contextPath}/sitewide/jsConstants.jsp?ver=${assetVersion}"></script>
				<script	type="text/javascript" src="${assetPath}/js/lib.kp.js?ver=${assetVersion}"></script>
				<jsp:include page="/sitewide/analytics/analytics_bottom.jsp"/>
			</c:when>
			<%-- proxy iframe may have additional javascript libraries --%>
			<c:when test="${isProxy}">
				<%-- Javascript --%>
				<script	type="text/javascript" src="${contextPath}/sitewide/jsConstants.jsp?ver=${assetVersion}"></script>
				<script	type="text/javascript" src="${assetPath}/js/lib.kp.js?ver=${assetVersion}"></script>
			</c:when>
		</c:choose>

	</body>
</html>
