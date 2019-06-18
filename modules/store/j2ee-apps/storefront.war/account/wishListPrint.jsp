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
		<jsp:attribute name="pageType">wishListPrint</jsp:attribute>
		<jsp:attribute name="bodyClass">account wish-list wish-list-print</jsp:attribute>
		<jsp:body>

			<div class="section-title">
				<h1>Wish List</h1>
			</div>

			<div class="wish-list-content">

				<%-- wish list header --%>
				<div class="order-items-header">
					<div class="order-items-header-detail">Item Details</div>
					<div class="order-items-header-price">Price</div>
					<div class="order-items-header-links">&nbsp;</div>
				</div>

				<%-- wish list items --%>
				<div class="order-items">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="wishlist.giftlistItems"/>
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

			</div>

		</jsp:body>
	</layout:default>

</dsp:page>
