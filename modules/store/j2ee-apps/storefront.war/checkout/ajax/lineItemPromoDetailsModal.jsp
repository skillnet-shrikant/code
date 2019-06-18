<%--
  - File Name: lineItemPromoDetailsModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal contains details about the promo that was applied to the line item.
  --%>

<dsp:getvalueof var="promo" param="p" />
<dsp:getvalueof var="description" param="d" />

<layout:ajax>
	<jsp:attribute name="section">checkout</jsp:attribute>
	<jsp:attribute name="pageType">promoDetailsModal</jsp:attribute>
	<jsp:body>

		<div class="promo-details-modal">

			<div class="modal-header">
				<h2>promotion details</h2>
			</div>

			<div class="modal-body">
				<p><strong>Name:</strong> ${promo}</p>
				<p><strong>Description:</strong> ${description}</p>
			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
