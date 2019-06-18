<%--
  - File Name: discontinuedItemPolicyModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal contains details about the promo that was applied to the line item.
  --%>

<layout:ajax>
	<jsp:attribute name="pageType">discontinuedItemPolicyModal</jsp:attribute>
	<jsp:body>

		<div class="promo-details-modal">

			<div class="modal-header">
				<h2>Clearance Item Policy</h2>
			</div>

			<div class="modal-body">
				<p>This item is on clearance and is not guaranteed to be in stock. It may take longer to fulfill due to limited availability.</p>
				<p>You will not be charged for this item until it ships. Please note, if selecting an expedited shipping option, the order processing time may be delayed as we check availability on the item.</p>
				<p>If we are unable to fulfill the item, you will receive a cancellation notice. Any other items on your order will be fulfilled accordingly.</p>
			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
