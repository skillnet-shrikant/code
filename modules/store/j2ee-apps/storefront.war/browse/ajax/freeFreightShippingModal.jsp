<%--
  - File Name: shippingSurchargeModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal contains details shipping surcharge.
  --%>

<layout:ajax>
	<jsp:attribute name="pageType">freeFreightShippingModal</jsp:attribute>
	<jsp:body>

		<div class="free-freight-shipping-modal">

			<div class="modal-header">
				<h2>Free Freight Shipping</h2>
			</div>

			<div class="modal-body">
				<p>This item ships free via LTL freight directly from the manufacturer. Please allow 1-2 additional days for processing. </p> 
				<p>When your item reaches the carrier, you will be contacted by phone to schedule delivery. To ensure prompt delivery, please ensure your contact information provided at checkout is accurate.</p>
			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
