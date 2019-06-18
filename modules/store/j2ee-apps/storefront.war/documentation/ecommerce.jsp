<%--
  - File Name: presentation.jsp
  - Author(s): KnowledgePath Solutions UX Team
  - Copyright Notice:
  - Description: This page documents ecommerce elements in the MFF Rapid Prototype
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
					<c:param name="navSection" value="ecommerce"/>
				</c:import>
			</div>

			<div class="small-9 columns">

				<h1>eCommerce Components</h1>

				<c:import url="/documentation/includes/priceTreatment.jsp"/>

				<c:import url="/documentation/includes/totals.jsp"/>

				<c:import url="/documentation/includes/changeQuantity.jsp"/>

				<c:import url="/documentation/includes/productTile.jsp"/>

				<c:import url="/documentation/includes/promoCode.jsp"/>

				<c:import url="/documentation/includes/addedToCartModal.jsp"/>

				<c:import url="/documentation/includes/miniCart.jsp"/>

				<c:import url="/documentation/includes/cartPage.jsp"/>

			</div>
		</div>
	</jsp:body>
</layout:documentation>

