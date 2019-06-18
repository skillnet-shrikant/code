<%--
  - File Name: upsellMessageModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal shows upsell instructions.
  --%>

<dsp:getvalueof var="qualName" param="qualName" />
<dsp:getvalueof var="qualInstructions" param="qualInstructions" />

<layout:ajax>
	<jsp:attribute name="section">checkout</jsp:attribute>
	<jsp:attribute name="pageType">promoDetailsModal</jsp:attribute>
	<jsp:body>

		<div class="shipping-surcharge-modal">

			<div class="modal-header">
				<h2>${qualName}</h2>
			</div>

			<div class="modal-body">
				<p>${qualInstructions}</p>
			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
