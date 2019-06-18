<%--
  - File Name: notFFLModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal tells the user this is NOT an FFL item but the current cart is an FFL
  								order and verifies they want to add it to their cart.
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

	<layout:ajax>
		<jsp:attribute name="section">modal</jsp:attribute>
		<jsp:attribute name="pageType">fflModal</jsp:attribute>
		<jsp:body>

			<div class="not-ffl-modal">

				<div class="modal-header">
					<h2>FFL Requirements</h2>
				</div>

				<div class="modal-body">
					<p>
						This item is a non-FFL item and you currently have items that must ship to an FFL
						dealer. An order cannot contain both FFL and non-FFL items. Would you like to continue
						adding the non-FFL items to your cart and remove the FFL items?
					</p>
					<p><a href="${contextPath}/static/faq#restrictions-for-purchasing-firearms-online">Click here for more information</a></p>
				</div>

				<div class="modal-footer">
					<a href="#" data-dismiss="modal" class="button secondary cancel-button">Cancel</a>
					<a href="#" class="button primary add-ffl-to-cart-submit">Confirm</a>
				</div>

			</div>

		</jsp:body>
	</layout:ajax>

</dsp:page>
