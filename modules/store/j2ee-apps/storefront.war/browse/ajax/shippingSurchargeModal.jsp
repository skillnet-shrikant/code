<%--
  - File Name: shippingSurchargeModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal contains details shipping surcharge.
  --%>

<layout:ajax>
	<jsp:attribute name="pageType">shippingSurchargeModal</jsp:attribute>
	<jsp:body>

		<div class="shipping-surcharge-modal">

			<div class="modal-header">
				<h2>Additional Shipping Charges</h2>
			</div>

			<div class="modal-body">
				<p>Based on the size or weight, certain items may incur a shipping surcharge based on the quantity added to your cart. </p> 
				<p>Shipping surcharges will be added to the total shipping cost, and can be reviewed upon adding the item to your cart.</p>
			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
