<%--
  - File Name: autoRemoveItemModal.jsp.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the modal that appears to ensure the user wants to change their order from
  					BOPIS to shipped, even though items will be removed from the order due to inventory
  					issues.
  --%>

<layout:ajax>
	<jsp:attribute name="section">checkout</jsp:attribute>
	<jsp:attribute name="pageType">autoRemoveItemModal</jsp:attribute>
	<jsp:body>
		<div class="auto-remove-item-modal">
			<div class="modal-header">
				<h2>Ship Order Instead Confirmation</h2>
			</div>
			<div class="modal-body">
				<c:choose>
					<c:when test="${not empty param.bopisItemsOnly}">
						<p>
							None of the items in your cart can be shipped.
							If you choose to ship your order, all items will be removed from your cart.
							Do you want to proceed?
						</p>
					</c:when>
					<c:otherwise>
						<p>
							Some items in your cart cannot be shipped.
							If you choose to ship your order, these items will be removed from your cart.
							Do you want to proceed?
						</p>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary cancel-button">Cancel</a><a href="#" class="button primary ship-my-order">Ship My Order</a>
			</div>
		</div>
	</jsp:body>
</layout:ajax>
