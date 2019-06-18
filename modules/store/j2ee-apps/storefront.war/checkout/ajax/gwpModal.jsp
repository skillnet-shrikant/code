<%--
  - File Name: gwpModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal contains details about the promo that was applied to the order
  --%>



<layout:ajax>
	<jsp:attribute name="section">checkout</jsp:attribute>
	<jsp:attribute name="pageType">promoDetailsModal</jsp:attribute>
	<jsp:body>
	<div class="promo-details-modal">

		<div class="modal-header">
			<h2>FREE ITEM DETAILS</h2>
		</div>
		<div class="modal-body">
			<dsp:droplet name="/atg/commerce/promotion/PromotionLookup">
				<dsp:param param="p" name="id"/>
				<dsp:oparam name="output">
					<dsp:valueof param="element.description" />
				</dsp:oparam>
			</dsp:droplet>		
			
		</div>

		<div class="modal-footer">
			<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
		</div>

	</div>

	</jsp:body>
</layout:ajax>
