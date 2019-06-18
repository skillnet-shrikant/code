<%--
  - File Name: presentation.jsp
  - Author(s): KnowledgePath Solutions UX Team
  - Copyright Notice:
  - Description: This page documents basic structural and presentation elements in the MFF Rapid Prototype
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
					<c:param name="navSection" value="presentation"/>
				</c:import>
			</div>

			<div class="small-9 columns">

				<h1>Presentation Components</h1>

				<c:import url="/documentation/includes/grid.jsp"/>

				<c:import url="/documentation/includes/blockGrid.jsp"/>

				<c:import url="/documentation/includes/buttons.jsp"/>

				<c:import url="/documentation/includes/forms.jsp"/>

				<c:import url="/documentation/includes/formValidation.jsp"/>

				<c:import url="/documentation/includes/alerts.jsp"/>

				<c:import url="/documentation/includes/sprites.jsp"/>

			</div>
		</div>
	</jsp:body>
</layout:documentation>

