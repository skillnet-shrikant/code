<%--
  - File Name: orderTracking.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This page has the form users can use to track an order
  --%>

<dsp:page>

	<layout:default>
		<jsp:attribute name="pageTitle">Order Tracking</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">orderTracking</jsp:attribute>
		<jsp:attribute name="bodyClass">account orderTracking</jsp:attribute>
		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><span class="crumb active">Order Tracking</span></li>
				</ul>
			</section>

			<section>
				<div class="section-title">
					<h1>Order Tracking</h1>
				</div>
				<div class="section-row">
					<div class="order-tracking-form">
						<p>
							Order tracking is available for orders placed online within the last 30 days. To track
							your order, enter the order number below. Once your order is displayed, click on your
							highlighted order number to access order tracking. Please note that tracking information
							will not be provided until after midnight CST of the day your package ships. If your
							order number is not highlighted, tracking information is not yet available.
						</p>
						<dsp:form id="order-tracking-form" method="post" data-validate>
							<dsp:include page="/account/includes/orderTrackingForm.jsp" />
						</dsp:form>
					</div>
					<div class="order-tracking-links">
						<a href="${contextPath}/account/orderHistory.jsp">View your complete Order History</a>
					</div>
				</div>
			</section>

		</jsp:body>
	</layout:default>

</dsp:page>
