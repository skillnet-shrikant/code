<%--
  - File Name: interaction.jsp
  - Author(s): KnowledgePath Solutions UX Team
  - Copyright Notice:
  - Description: This page documents basic javascript driven UX modules in the framework
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
					<c:param name="navSection" value="interaction"/>
				</c:import>
			</div>

			<div class="small-9 columns">
				<h1>Interaction Components</h1>

				<c:import url="/documentation/includes/loader.jsp"/>

				<c:import url="/documentation/includes/modals.jsp"/>

				<c:import url="/documentation/includes/dropdown.jsp"/>

				<c:import url="/documentation/includes/accordion.jsp"/>

				<c:import url="/documentation/includes/tabs.jsp"/>

				<c:import url="/documentation/includes/tooltip.jsp"/>

				<c:import url="/documentation/includes/slider.jsp"/>


			</div>
		</div>
	</jsp:body>
</layout:documentation>


