<%--
  Tag for default page wrapper. Responsible for wrapping contents of page with html, head, header and footer.
--%>
<%@ include file="/sitewide/fragments/tags.jspf" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="section"%>
<%@ attribute name="pageType"%>
<%@ attribute name="bodyClass"%>

<%-- Set page vars --%>
<c:set var="jsController" value="${section}" scope="request"/>
<c:set var="jsAction" value="${pageType}" scope="request"/>

<!doctype html>
<%-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ --%>
<!--[if lt IE 7 ]> <html class="no-js ie6 ${deviceClass}" lang="en"> <![endif]-->
<!--[if IE 7 ]>    <html class="no-js ie7 ${deviceClass}" lang="en"> <![endif]-->
<!--[if IE 8 ]>    <html class="no-js ie8 ${deviceClass}" lang="en"> <![endif]-->
<!--[if IE 9 ]>    <html class="no-js ie9 ${deviceClass}" lang="en"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--><html class="no-js ${deviceClass}" lang="en"><!--<![endif]-->

	<head>
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
		
	</head>

	<body class="${bodyClass}" data-controller="proxy" data-action="${jsAction}">

		<%-- Body content --%>
		<div id="proxyContent" class="site-wrapper">
			<jsp:doBody />
		</div>

		<%-- Javascript --%>
		<script	type="text/javascript" src="${contextPath}/sitewide/jsConstants.jsp?ver=${assetVersion}"></script>
		<script	type="text/javascript" src="${assetPath}/js/lib.kp.js?ver=${assetVersion}"></script>

	</body>
</html>
