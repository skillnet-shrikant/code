<%--
  - File Name: fflModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal tells the user this is an FFL item and verifies they want to add it to their cart.
  --%>
<dsp:page>
<layout:ajax>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<jsp:attribute name="section">modal</jsp:attribute>
	<jsp:attribute name="pageType">fflModal</jsp:attribute>
	<jsp:body>

		<div class="ffl-modal">

			<div class="modal-header">
				<h2>FFL Requirements</h2>
			</div>

			<div class="modal-body">
				<p class="mixed-cart-note hide">
					This item is an FFL item and must ship to an FFL dealer. You currently have items that are
					non-FFL items. An order cannot contain both FFL and non-FFL items. Would you like to
					continue adding the FFL items to your cart and remove the non-FFL items?
				</p>
				<p>Confirm that you understand this item must be shipped to an authorized FFL dealer.</p>
				<p>The FFL dealer # and FFL contact information must be provided at checkout.</p>
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
