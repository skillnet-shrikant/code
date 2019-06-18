	<%--
  - File Name: index.jsp
  - Author(s): KnowledgePath Solutions UX Team
  - Copyright Notice:
  - Description: This is the main documentation page for the UX framework
  - Parameters:
  -
  --%>
<layout:documentation>
	<jsp:attribute name="pageTitle">MFF Rapid Prototype Documentation</jsp:attribute>
	<jsp:attribute name="metaDescription"></jsp:attribute>
	<jsp:attribute name="metaKeywords"></jsp:attribute>
	<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
	<jsp:attribute name="seoRobots"></jsp:attribute>
	<jsp:attribute name="lastModified"></jsp:attribute>
	<jsp:attribute name="section">documentation</jsp:attribute>
	<jsp:attribute name="pageType"></jsp:attribute>
	<jsp:attribute name="bodyClass">documentation</jsp:attribute>
	<jsp:body>
		<div class="row doc-wrapper">
			<div class="small-3 columns">
				<c:import url="/documentation/includes/documentationNav.jsp">
					<c:param name="navSection" value="setup"/>
				</c:import>
			</div>

			<div class="small-9 columns">

			<h1>MFF Rapid Prototype</h1>
			<p>The MFF Rapid Prototype consists of common eCommerce components that are used in many of our projects. This platform is based on SASS so it can be easily customized for each implementation. Obviously each client's business requirements are different &#8212; this framework is meant to be the base starting point for an implementation and should be customized for the client's needs. </p>

			<c:import url="/documentation/includes/gettingStarted.jsp"/>

			<c:import url="/documentation/includes/base.jsp"/>

			<c:import url="/documentation/includes/partials.jsp"/>

			<c:import url="/documentation/includes/utilityClasses.jsp"/>

			<c:import url="/documentation/includes/globals.jsp"/>

			<c:import url="/documentation/includes/mediaQueries.jsp"/>

			<c:import url="/documentation/includes/pluginInit.jsp"/>

			<c:import url="/documentation/includes/pageTemplates.jsp"/>

			<c:import url="/documentation/includes/accessibility.jsp"/>

			</div>
		</div>

	</jsp:body>
</layout:documentation>
