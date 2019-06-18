<dsp:page>

	<%-- Page Variables --%>
	<dsp:getvalueof var="firstStep" param="firstStep" vartype="boolean" />

	<%-- is order bopis only --%>
	<dsp:droplet name="/com/mff/commerce/order/purchase/IsItemRemovalRequired">
		<dsp:oparam name="output">
			<dsp:getvalueof var="isItemRemovalRequired" param="isItemRemovalRequired" scope="request"/>
		</dsp:oparam>
	</dsp:droplet>
	
	<dsp:droplet name="/com/mff/locator/droplet/StoreLookupDroplet">
		<dsp:param name="id" param="storeId"/>
		<dsp:param name="elementName" value="store"/>
		<dsp:oparam name="output">
			<div class="bopis-review-panel">
				<c:if test="${firstStep}">
					<div class="shipping-method-review">
						<h3>Pick Up In Store</h3>
						<a href="${contextPath}/checkout/cart.jsp?changeStore=true">Change Store</a>
						
						<%-- 
							Requested in 2505. Show ship my order modal consistently
							on PDP, cart & checkout. Changes to this section should also be made
							on cart & PDP
						--%>
						<c:choose>
							<c:when test="${isItemRemovalRequired}">
								<a href="/checkout/ajax/autoRemoveItemModal.jsp" class="auto-remove-item-trigger modal-trigger" data-target="auto-remove-item-modal" data-size="small">Ship My Order Instead</a>
							</c:when>
							<c:otherwise>
								<a href="#" class="disabled ship-my-order">Ship My Order Instead</a>
							</c:otherwise>
						</c:choose>							
					</div>
				</c:if>
				<div class="shipping-address-review">
					<h3><dsp:valueof param="store.city"/>,&nbsp;<dsp:valueof param="store.stateAddress"/></h3>
					<p><dsp:valueof param="store.address1"/></p>
					<p><dsp:valueof param="store.city"/>,&nbsp;<dsp:valueof param="store.stateAddress"/>&nbsp;<dsp:valueof param="store.postalCode"/></p>
					<p><dsp:valueof param="store.phoneNumber" /></p>
				</div>
				<c:if test="${not firstStep}">
					<div class="bopis-person-review">
						<dsp:include page="/checkout/includes/bopisPersonReview.jsp" />
					</div>
				</c:if>
			</div>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
