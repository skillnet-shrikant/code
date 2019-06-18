<section id="cart-page-docs" class="docs-section">
	<h2>Cart Items</h2>
	<p>This is a list of commerce items for display in the cart page or order summary page.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<div class="shopping-cart">
			<div class="cart-header">
				<div class="cart-header-item">Item</div>
				<div class="cart-header-price">Price</div>
				<div class="cart-header-quantity">Quantity</div>
				<div class="cart-header-total">Total</div>
			</div>

			<div class="cart-item-list">
				<c:forEach begin="0" end="2" varStatus="loop">
					<c:import url="fragments/orderItem.jspf"/>
				</c:forEach>
			</div>
		</div>
	</div>

	<p>Individual order item templates are added via a fragment <code>orderItem.jspf</code>.</p>

	<h4>HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString"><c:import url="/documentation/fragments/orderItem.jspf"/></jsp:attribute>
	</format:prettyPrint>

</section>
