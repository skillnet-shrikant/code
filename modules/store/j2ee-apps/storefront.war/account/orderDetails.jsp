<%--
	- File Name: orderDetails.jsp
	- Author(s): KnowledgePath Solutions UX Team
	- Copyright Notice:
	- Description: Order details page that contains all information about a specific order
	- Parameters:
	-
	--%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/com/mff/commerce/order/MFFOrderDetailLookup"/>
	<dsp:importbean bean="/atg/commerce/payment/CreditCardTools"/>
	<dsp:importbean bean="/com/mff/commerce/order/MFFReturnStatusDroplet"/>
	<dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnStateDescriptions"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	
	<!-- Bazaar voice enable check -->
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="bvOrderTransactionsEnabled" bean="MFFEnvironment.bvOrderTransactionsEnabled"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />
	
	<dsp:getvalueof bean="Site.isEnableMillsMoney" var="millsMoney"/>

	<c:set var="gcHeaderText">Gift Card </c:set>
	<c:if test="${millsMoney}">
		<c:set var="gcHeaderText">Gift Card / Bonus Bucks</c:set>
	</c:if>
	<layout:default>
		<jsp:attribute name="pageTitle">Order Details</jsp:attribute>
		<jsp:attribute name="metaDescription"></jsp:attribute>
		<jsp:attribute name="metaKeywords"></jsp:attribute>
		<jsp:attribute name="seoCanonicalURL"></jsp:attribute>
		<jsp:attribute name="seoRobots"></jsp:attribute>
		<jsp:attribute name="lastModified"></jsp:attribute>
		<jsp:attribute name="section">account</jsp:attribute>
		<jsp:attribute name="pageType">orderDetails</jsp:attribute>
		<jsp:attribute name="bodyClass">account orderDetails</jsp:attribute>
		<jsp:body>

			<!-- Bazaar voice display common script -->
			<c:if test="${bvEnabled}">
				<dsp:include otherContext="/bv" page="/common/bv_common_script.jsp" />
					<c:set var="contextPath" value="${currentContext}" />
			</c:if>
			
			<%-- breadcrumbs --%>
			<section class="breadcrumbs">
				<ul aria-label="breadcrumbs" role="navigation">
					<li><a href="${contextPath}/" class="crumb">Home</a></li>
					<li><a class="crumb" href="${contextPath}/account/account.jsp">My Account</a></li>
					<li><a class="crumb" href="${contextPath}/account/orderHistory.jsp">My Orders</a></li>
					<li><span class="crumb active">Order Details</span></li>
				</ul>
			</section>

			<section>
				<div class="section-title">
					<h1>Order Details</h1>
				</div>

				<%-- errors --%>
				<div class="error-container">
					<div class="error-messages"></div>
				</div>

				<%-- order header --%>
				<div class="order-items-header">
					<div class="order-items-header-orders">Order Info</div>
					<div class="order-items-header-date">Date</div>
					<div class="order-items-header-order-number">Order Number</div>
					<div class="order-items-header-order-total">Order Total</div>
					<div class="order-items-header-status">Status</div>
				</div>

					<dsp:droplet name="MFFOrderDetailLookup">
						<dsp:param name="orderId" param="orderId"/>
						<dsp:oparam name="output">
							<dsp:getvalueof param="result" var="order"/>
							<dsp:getvalueof var="orderNumber" param="result.orderNumber"/>
						</dsp:oparam>
					</dsp:droplet>
					<dsp:param name="order" value="${order}"/>
				<%--</c:if>--%>

				<%-- is bopis? --%>
				<dsp:getvalueof var="bopis" param="order.bopisOrder"/>

				<dsp:getvalueof var="returnState" value=""/>

				<dsp:droplet name="MFFReturnStatusDroplet">
					<dsp:param name="orderId" param="orderId"/>
					<dsp:param name="resultName" value="relatedReturnRequests"/>
					<dsp:oparam name="output">
					<dsp:getvalueof var="relatedReturnRequests" param="relatedReturnRequests"/>

					<dsp:droplet name="ForEach" array="${relatedReturnRequests}" elementName="return">
                    <dsp:oparam name="output">

                      <dsp:getvalueof var="returnState" param="return.state"/>


                    </dsp:oparam>
                  </dsp:droplet>

					</dsp:oparam>
				</dsp:droplet>

				<%-- order information --%>
				<div class="order-items order-history-items">
					<div class="order-item">
						<div class="item-date order-item-section">
							<span class="label">Date:</span> <dsp:valueof param="order.submittedDate" converter="date" date="MM/dd/yyyy"/>
						</div>
						<div class="item-order-number order-item-section">
							<span class="label">Order Number:</span>
							<c:choose>
								<c:when test="${not empty orderNumber}">
									${orderNumber}
								</c:when>
								<c:otherwise>
									<dsp:valueof param="order.id"/>
								</c:otherwise>
							</c:choose>
						</div>

						<div class="item-order-total order-item-section">
							<span class="label">Order Total:</span> <dsp:valueof param="order.orderTotal" converter="currency"/>
						</div>
						<div class="item-status order-item-section">
							<span class="label">Status:</span>
							<dsp:include page="/sitewide/includes/orderState.jsp">
								<dsp:param name="order" param="order"/>
							</dsp:include>
							<c:if test="${not empty returnState}">
										<dsp:droplet name="ReturnStateDescriptions">
										<dsp:param name="state" value="${returnState}"/>
										<dsp:param name="elementName" value="returnStateDescription"/>
										<dsp:oparam name="output">
											(<dsp:valueof param="returnStateDescription"><fmt:message key="common.returnDefaultState"/></dsp:valueof>)
										</dsp:oparam>
									  </dsp:droplet>
								</c:if>
						</div>
					</div>
				</div>

				<%-- shipping/billing information header --%>
				<div class="shipping-billing-info">
					<div class="shipped-to">
						<c:choose>
							<c:when test="${bopis}">
								<dsp:droplet name="/com/mff/locator/droplet/StoreLookupDroplet">
									<dsp:param name="id" param="order.bopisStore"/>
									<dsp:param name="elementName" value="store"/>
									<dsp:oparam name="output">
										<div class="shipping-billing-header">
											Pick-Up Location
										</div>
										<div class="shipped-to-info">
											<p class="title"><dsp:valueof param="store.city"/>,&nbsp;<dsp:valueof param="store.stateAddress"/></p>
											<p><dsp:valueof param="store.address1"/></p>
											<p><dsp:valueof param="store.city"/>,&nbsp;<dsp:valueof param="store.stateAddress"/>&nbsp;<dsp:valueof param="store.postalCode"/></p>
											<p><dsp:valueof param="store.phoneNumber" /></p>
										</div>
									</dsp:oparam>
								</dsp:droplet>
							</c:when>
							<c:otherwise>
								<div class="shipping-billing-header">
									Shipped To
								</div>
								<div class="shipped-to-info">
									<p><dsp:valueof param="order.shippingInfo.firstName"/>&nbsp;<dsp:valueof param="order.shippingInfo.lastName"/></p>
									<dsp:droplet name="IsEmpty">
										<dsp:param name="value" param="order.attention"/>
										<dsp:oparam name="false">
											 <p><span class="label">Attention:</span><dsp:valueof param="order.attention"/></p>
										</dsp:oparam>
									</dsp:droplet>
									<p><dsp:valueof param="order.shippingInfo.address1"/></p>
									<p><dsp:valueof param="order.shippingInfo.address2"/></p>
									<p><dsp:valueof param="order.shippingInfo.city"/>,&nbsp;<dsp:valueof param="order.shippingInfo.state"/>&nbsp;<dsp:valueof param="order.shippingInfo.postalCode"/></p>
									<p>
										<dsp:getvalueof var="phone" param="order.shippingInfo.phoneNumber" />
										<c:out value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, 10)}" />
									</p>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="shipping-method">
						<c:choose>
							<c:when test="${bopis}">
								<div class="shipping-billing-header">
									Selected Pick-Up Person
								</div>
								<div class="shipped-to-info">
									<p><strong>Name:</strong> <dsp:valueof param="order.bopisPerson"/></p>
									<p><strong>Email:</strong> <dsp:valueof param="order.bopisEmail"/></p>
								</div>
							</c:when>
							<c:otherwise>
								<div class="shipping-billing-header">
									Shipping Method
								</div>
								<div class="shipping-method-info">
									<dsp:valueof param="order.shippingMethod"/>

									<dsp:droplet name="Switch">
										<dsp:param name="value" param="order.isSaturdayDelivery"/>
										<dsp:oparam name="true">
											<p>Saturday Delivery</p>
										</dsp:oparam>
									</dsp:droplet>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="billed-to">

						<div class="shipping-billing-header">
							Billed To
						</div>
						<div class="billed-to-info">
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="order.paymentGroups"/>
								<dsp:param name="elementName" value="paymentGroup"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="paymentClassType" param="paymentGroup.paymentGroupClassType" />
									<c:choose>
										<c:when test="${paymentClassType == 'creditCard'}">

										<dsp:getvalueof var="creditCardType" param="paymentGroup.creditCardType"/>
										<dsp:getvalueof var="reverseCardCodeMap" bean="CreditCardTools.reverseCardCodeMap"/>

											<dsp:getvalueof var="billingName" param="paymentGroup.nameOnCard" scope="request" />
											<div class="billed-to-info-block">
												<h3>Credit Card</h3>
												<p>${billingName}</p>
												<p><c:out value="${reverseCardCodeMap[creditCardType]}"/></p>
												<p>
												<dsp:getvalueof var="isLegacy" param="order.legacyOrder"/>
												<c:if test="${isLegacy eq 'false'}">
													XXXX&nbsp;XXXX&nbsp;XXXX
												</c:if>
												<dsp:valueof param="paymentGroup.creditCardNumber" groupingsize="4" converter="CreditCard" numcharsunmasked="4"/></p>
												<p><strong>Exp:</strong>&nbsp;<dsp:valueof param="paymentGroup.expirationMonth"/>/<dsp:valueof param="paymentGroup.expirationYear"/></p>
												<p><strong>Amount:</strong> <dsp:valueof param="paymentGroup.amount" converter="currency" /></p>
											</div>
										</c:when>
										<c:when test="${paymentClassType == 'giftCard'}">
											<div class="billed-to-info-block">
												<h3>${gcHeaderText}</h3>
												<p><dsp:valueof param="paymentGroup.cardNumber"/></p>
												<p><strong>Amount:</strong> <dsp:valueof param="paymentGroup.amount" converter="currency" /></p>
											</div>
										</c:when>
										<c:when test="${paymentClassType == 'giftCertificate'}">
											<div class="billed-to-info-block">
												<h3>Gift Card</h3>
												<p><dsp:valueof param="paymentGroup.giftCertificateNumber"/></p>
												<p><strong>Amount:</strong> <dsp:valueof param="paymentGroup.amount" converter="currency" /></p>
											</div>
										</c:when>
									</c:choose>
								</dsp:oparam>
							</dsp:droplet>
						</div>
					</div>
				</div>

				<!-- BazaarVoice inline script for product Ids -->
				<c:if test="${bvEnabled}">
					<dsp:include otherContext="/bv" page="/productListing/common/bv_plp_order_detail_script.jsp">
						<dsp:param name="commerceItems" param="order.commerceItems" />
					</dsp:include>
					<c:set var="contextPath" value="${currentContext}" />
				</c:if>

				<%-- cart header --%>
				<c:if test="${bopis}">
					<c:set var="bopisClass" value="bopis" />
				</c:if>
				<div class="order-items-header">
					<div class="order-items-header-detail ${bopisClass}">Item Details</div>
					<c:if test="${not bopis}"><div class="order-items-header-tracking">Tracking #</div></c:if>
					<div class="order-items-header-total ${bopisClass}">Total</div>
					<div class="order-items-header-links">&nbsp;</div>
				</div>
				<dsp:getvalueof var="trackingNumber" param="order.trackingNumber"/>
				<%-- cart items --%>
				<div class="order-items order-details-items">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="order.commerceItems"/>
						<dsp:param name="elementName" value="commerceItem"/>
						<dsp:oparam name="output">
							<dsp:include src="/account/includes/orderDetailsItem.jsp" flush="true">
								<dsp:param name="commerceItem" param="commerceItem"/>
								<dsp:param name="isLegacy" param="order.legacyOrder"/>
								<dsp:param name="trackingNumber" param="order.trackingNumber"/>
								<dsp:param name="bopis" value="${bopis}" />
								<dsp:param name="bopisClass" value="${bopisClass}" />
							</dsp:include>
						</dsp:oparam>
					</dsp:droplet>
				</div>
				
				

				<%-- promo form / financial stack --%>
				<div class="promo-and-totals">
					<div class="totals-container">
						<dsp:include page="/account/includes/ordertotals.jsp">
							<dsp:param name="order" param="order"/>
						</dsp:include>
					</div>
				</div>

			</section>
			
			<%-- Bazaar voice transaction pixel for write a review request --%>
			<c:if test="${bvEnabled and bvOrderTransactionsEnabled}">
				<!--I can see this part</h1> -->
				<dsp:include otherContext="/bv" page="/pixel/common/bv_pixel_common_script.jsp" />
				<dsp:include otherContext="/bv" page="/pixel/transaction/bv_pixel_transaction_event_myaccount.jsp">
					<dsp:param name="repositoryId" param="orderId"/>
				</dsp:include>
				<c:set var="contextPath" value="${currentContext}" />
			</c:if>

		</jsp:body>
	</layout:default>

</dsp:page>
