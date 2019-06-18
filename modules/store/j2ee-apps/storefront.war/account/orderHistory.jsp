<%--
	- File Name: orderHistory.jsp
	- Author(s): KnowledgePath Solutions UX Team
	- Copyright Notice:
	- Description: Order history page that contains all a users orders
	- Parameters:
	-
	--%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/mff/account/order/droplet/MFFOrderHistoryDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="currentPage" param="pageNumber" scope="request" />
	<c:if test="${empty currentPage}">
		<dsp:getvalueof var="currentPage" value="1" scope="request" />
	</c:if>

	<layout:default>
		<jsp:attribute name="pageTitle">My Orders</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">orderHistory</jsp:attribute>
		<jsp:attribute name="bodyClass">account orderHistory</jsp:attribute>

		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><span class="crumb active">My Orders</span></li>
				</ul>
			</section>

			<section>
				<div class="section-title">
					<h1>My Orders</h1>
				</div>

				<%-- errors --%>
				<div class="error-container">
					<div class="error-messages"></div>
				</div>
				<dsp:droplet name="MFFOrderHistoryDroplet">
					<dsp:param name="userId" bean="Profile.id"/>
					<dsp:param name="currentPage" value="${currentPage}"/>
					<dsp:oparam name="empty">
						<%-- no orders message --%>
						<div class="order-messages">
							<div class="order-message">
								<p>You don't have any orders yet. Start shopping now to score some great deals!</p>
								<a href="${contextPath}/" class="button primary">Shop Now</a>
							</div>
						</div>
					</dsp:oparam>
					<dsp:oparam name="output">
						<dsp:getvalueof var="numberOfPages" param="numberOfPages" scope="request" />

						<%-- order messages --%>
						<div class="order-messages">
							<div class="order-message">
								Review your order history here. Your most recent orders are listed first. Select
								VIEW DETAILS to review additional information about previous orders.
							</div>
						</div>

						<%-- top pagination --%>
						<c:if test="${numberOfPages > 1}">
							<div class="pagination-container">
								<dsp:include page="/account/includes/pagination.jsp">
									<dsp:param name="currentPage" value="${currentPage}" />
									<dsp:param name="totalPages" value="${numberOfPages}" />
									<dsp:param name="baseUrl" value="${contextPath}/account/orderHistory.jsp" />
								</dsp:include>
							</div>
						</c:if>

						<%-- orders header --%>
						<div class="order-items-header">
							<div class="order-items-header-orders">Orders</div>
							<div class="order-items-header-date">Date</div>
							<div class="order-items-header-order-number">Order Number</div>
							<div class="order-items-header-order-total">Order Total</div>
							<div class="order-items-header-status">Status</div>
						</div>

						<div class="order-items order-history-items">
							<%-- order history items --%>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" param="orders"/>
								<dsp:param name="elementName" value="order" />
								<dsp:oparam name="output">
									<dsp:include page="${contextPath}/account/includes/orderHistoryItem.jsp">
										<dsp:param name="order" param="order" />
									</dsp:include>
								</dsp:oparam>
							</dsp:droplet>
						</div>

						<%-- bottom pagination --%>
						<c:if test="${numberOfPages > 1}">
							<div class="pagination-container">
								<dsp:include page="/account/includes/pagination.jsp">
									<dsp:param name="currentPage" value="${currentPage}" />
									<dsp:param name="totalPages" value="${numberOfPages}" />
									<dsp:param name="baseUrl" value="${contextPath}/account/orderHistory.jsp" />
								</dsp:include>
							</div>
						</c:if>

						<div class="order-history-actions">
							<div class="order-history-links">
								<a href="${contextPath}/static/online-returns" class="return-policy-link">Return Policy</a>
							</div>
						</div>

					</dsp:oparam>
				</dsp:droplet>

			</section>

		</jsp:body>
	</layout:default>

</dsp:page>
