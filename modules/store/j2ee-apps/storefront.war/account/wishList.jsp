<%--
  - File Name: wishList.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the users Wish List
  --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Range"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
	<dsp:importbean bean="/com/mff/droplet/MFFPaginationDroplet"/>

	<%-- Page Variables --%>
	<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist"/>
	<dsp:getvalueof var="items" param="wishlist.giftlistItems"/>
	<dsp:setvalue paramvalue="wishlist.id" param="giftlistId"/>

	<layout:default>
		<jsp:attribute name="pageTitle">Wish List</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">wishList</jsp:attribute>
		<jsp:attribute name="bodyClass">account wish-list</jsp:attribute>
		<jsp:body>

			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><span class="crumb active">Wish List</span></li>
				</ul>
			</section>

			<div class="section-title">
				<h1>Wish List</h1>
				<c:if test="${not empty items}">
					<div class="title-buttons">
						<dsp:a href="${contextPath}/browse/ajax/emailWishListModal.jsp" class="modal-trigger" data-target="email-wish-list-modal" data-size="small"><dsp:param name="giftListId" param="giftlistId"/><span class="icon icon-email"></span></dsp:a>
						<a href="${contextPath}/account/wishListPrint.jsp" class="print-wish-list" target="_blank"><span class="icon icon-print"></span></a>
					</div>
				</c:if>
			</div>

			<div class="wish-list-content">
				<c:choose>
					<c:when test="${empty items}">
						<%-- wish list empty --%>
						<%@ include file="fragments/wishListEmpty.jspf"%>
					</c:when>
					<c:otherwise>

						<dsp:droplet name="MFFPaginationDroplet">
							<dsp:param name="items" value="${items}"/>
							<dsp:param name="pageNumber" param="pageNumber"/>
							<dsp:oparam name="output">

								<%-- top pagination --%>
								<dsp:getvalueof var="numPages" param="noOfPages" />
								<input type="hidden" id="wishlist-num-pages" value="${numPages}" />
								<c:if test="${numPages > 1}">
									<div class="pagination-container">
										<dsp:include page="${contextPath}/account/includes/pagination.jsp">
											<dsp:param name="currentPage" param="currentPage" />
											<dsp:param name="totalPages" param="noOfPages" />
											<dsp:param name="baseUrl" value="${contextPath}/account/wishList.jsp" />
										</dsp:include>
									</div>
								</c:if>

								<%-- wish list header --%>
								<div class="order-items-header">
									<div class="order-items-header-detail">Item Details</div>
									<div class="order-items-header-price">Price</div>
									<div class="order-items-header-links">&nbsp;</div>
								</div>

								<%-- wish list items --%>
								<div class="order-items">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="filteredItems"/>
										<dsp:oparam name="output">
											<dsp:setvalue param="giftItem" paramvalue="element"/>
											<dsp:droplet name="ProductLookup">
												<dsp:param name="id" param="giftItem.productId"/>
												<dsp:param name="filterByCatalog" value="false"/>
												<dsp:param name="filterBySite" value="false"/>
												<dsp:param name="elementName" value="giftProductItem"/>
												<dsp:oparam name="output">
													<dsp:droplet name="SKULookup">
														<dsp:param name="id" param="giftItem.catalogRefId"/>
														<dsp:param name="filterByCatalog" value="false"/>
														<dsp:param name="filterBySite" value="false"/>
														<dsp:param name="elementName" value="giftSkuItem"/>
														<dsp:oparam name="output">
															<%@ include file="/account/fragments/wishListOrderItem.jspf"%>
														</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:oparam>
									</dsp:droplet>
								</div>

								<%-- bottom pagination --%>
								<c:if test="${numPages > 1}">
									<div class="pagination-container">
										<dsp:include page="${contextPath}/account/includes/pagination.jsp">
											<dsp:param name="currentPage" param="currentPage" />
											<dsp:param name="totalPages" param="noOfPages" />
											<dsp:param name="baseUrl" value="${contextPath}/account/wishList.jsp" />
										</dsp:include>
									</div>
								</c:if>

							</dsp:oparam>
						</dsp:droplet>

						<%-- wish list actions --%>
						<div class="wish-list-actions">
							<div class="wish-list-buttons">
								<a href="${contextPath}/" class="button primary">Continue Shopping</a>
							</div>
						</div>

					</c:otherwise>
				</c:choose>
			</div>

		</jsp:body>
	</layout:default>

</dsp:page>
