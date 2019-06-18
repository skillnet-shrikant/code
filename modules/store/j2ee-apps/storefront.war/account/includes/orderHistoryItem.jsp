<dsp:page>
	
	<dsp:getvalueof var="orderNumber" param="order.orderNumber"/>
	
	<div class="order-item">
		<div class="item-date order-item-section">
			<span class="label">Date:</span><dsp:valueof param="order.submittedDate" converter="date" date="MM/dd/yyyy"/>
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
			<span class="label">Order Total:</span>
			<dsp:getvalueof var="amount" param="order.amount" />
			<dsp:getvalueof var="tax" param="order.tax" />
			<dsp:getvalueof var="shipping" param="order.shipping" />
			 <dsp:valueof value="${amount + tax + shipping}" converter="currency"/>
		</div>
		<div class="item-status order-item-section">
			<span class="label">Status:</span>
			<dsp:include page="/sitewide/includes/orderState.jsp">
				<dsp:param name="order" param="order"/>
			</dsp:include>
		</div>
		<div class="item-action-links order-item-section">
			<dsp:a href="${contextPath}/account/orderDetails.jsp" class="button primary view-order-details">
				View Details
				<dsp:param name="orderId" param="order.id"/>
				<dsp:param name="lgc" param="order.legacyOrder"/>
			</dsp:a>
		</div>
	</div>
</dsp:page>
