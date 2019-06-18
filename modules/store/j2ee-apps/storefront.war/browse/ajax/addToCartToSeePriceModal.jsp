<%--
  - File Name: addToCartForPriceModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal displays info related to MAP pricing.
  --%>

<layout:ajax>
	<jsp:attribute name="pageType">addToCartForPriceModal</jsp:attribute>
	<jsp:body>

		<div class="shipping-surcharge-modal">

			<div class="modal-header">
				<h2>Add To Cart for Price</h2>
			</div>

			<div class="modal-body">
				<p>Our price on this item is so good we can't show it here. To see our low price, just add the item to your shopping cart. Don't worry - if you decide you don't want to purchase the item, you can easily remove it from your cart.</p>
			</div>

			<div class="modal-footer">
				<a href="#" data-dismiss="modal" class="button secondary expand">Close</a>
			</div>

		</div>

	</jsp:body>
</layout:ajax>
