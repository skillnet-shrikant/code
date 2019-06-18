<%--
  - File Name: removeItemModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is the modal that appears to ensure the user wants to delete an address
  --%>

<layout:ajax>
	<jsp:attribute name="section">account</jsp:attribute>
	<jsp:attribute name="pageType">removeItemModal</jsp:attribute>
	<jsp:body>

		<div class="remove-item-modal">

			<div class="modal-header">
				<h2>Remove Item Confirmation</h2>
			</div>

			<div class="modal-body">
				<p>Are you sure you want to remove this item from your cart?</p>
			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary cancel-button">Cancel</a><a href="#" class="button primary delete-button item-remove" data-ciid="${param.ciId}">Remove</a>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
