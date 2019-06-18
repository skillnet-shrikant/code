<section id="added-to-cart-modal-docs" class="docs-section">

	<h2>Added to Cart Modal</h2>
	<p></p>
	<div class="docs-example">
		<h4>Example</h4>
		<div class="modal-window fade in">
			<div class="modal-content fade in">
				<div class="cart-success">
					<h2 class="cart-success-header">Item added to your cart</h2>
					<div class="cart-success-item">
						<div class="item-image">
							<img src="http://placehold.it/130x130">
						</div>
						<div class="item-details">
							<div class="product-name">&lt;Product Display Name&gt;</div>
							<div class="product-brand">&lt;brand&gt;</div>
							<div class="product-selections">
								<div class="variant"><span class="label">size:</span> 5</div>
								<div class="variant"><span class="label">color:</span> blue</div>
								<div class="quantity"><span class="label">qty:</span> 1</div>
							</div>
						</div>

						<!-- taken from Sale Price in priceTreatment.jsp -->
						<div class="item-price-subtotal">
							<span class="original-price">$&lt;xx.xx&gt;</span><span class="sale-price">$&lt;xx.xx&gt;</span>
							<div class="compare-price">Compare at $&lt;xx.xx&gt;</div>
							<div class="savings">you saved &lt;$ or %&gt;</div>
						</div>
						<!-- end priceTreatment area-->
					</div>
					<div class="cart-success-buttons">
						<button class="button secondary">Continue shopping</button>
						<button class="button primary">View cart &amp; checkout</button>
					</div>
					<div class="crosssell">
						<div class="slider-container">
							<div class="section-title">
								<h2>You May Also Like</h2>
							</div>
							<div class="slider-content">
								<div class="product-tile-slider">
									<c:forEach begin="0" end="2" varStatus="loop">
										<div><%-- extra wrapper needed for slick slider --%>
											<%@ include file="productTileMiniContent.jspf" %>
										</div>
									</c:forEach>
								</div>
							</div>
						</div>
					</div>
				</div>

			</div>
			<div class="modal-close" data-dismiss="modal"><span class="icon icon-close" aria-hidden="true"></span><span class="sr-only">close</span></div>
		</div>
	</div>

	<h4>HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<div class="modal-window fade in">
				<div class="modal-content fade in">
					<div class="cart-success">
						<h2 class="cart-success-header">Item added to your cart</h2>
						<div class="cart-success-item">
							<div class="item-image">
								<img src="http://placehold.it/130x130">
							</div>
							<div class="item-details">
								<div class="product-name">Product Display Name</div>
								<div class="product-brand">brand</div>
								<div class="product-selections">
									<div class="variant"><span class="label">size:</span> 5</div>
									<div class="variant"><span class="label">color:</span> blue</div>
									<div class="quantity"><span class="label">qty:</span> 1</div>
								</div>
							</div>

							<!-- taken from Sale Price in priceTreatment.jsp -->
							<div class="item-price-subtotal">
								<span class="original-price">$xx.xx</span><span class="sale-price">$xx.xx</span>
								<div class="compare-price">Compare at $xx.xx</div>
								<div class="savings">you saved $ or %</div>
							</div>
							<!-- end priceTreatment area-->
						</div>
						<div class="cart-success-buttons">
							<button class="button secondary">Continue shopping</button>
							<button class="button primary">View cart &amp; checkout</button>
						</div>
						<div class="crosssell">
							<div class="slider-container">
								<div class="section-title">
									<h2>You May Also Like</h2>
								</div>
								<div class="slider-content">
									<div class="product-tile-slider">
										<!-- include slider with productTileMiniContent.jspf here -->
									</div>
								</div>
							</div>
						</div>
					</div>

				</div>
				<div class="modal-close" data-dismiss="modal"><span class="icon icon-close" aria-hidden="true"></span><span class="sr-only">close</span></div>
			</div>
		</jsp:attribute>
	</format:prettyPrint>

	<h4 >SASS</h4>
<pre class="prettyprint">
.cart-success {
  .crosssell {
    margin: 10px 0;
  }
  .crosssell-header {
    @include font-size(20px);
  }
}
.cart-success-header {
  @include font-size(20px);
  border-bottom: 1px solid lighten($black, 60);
}
.cart-success-item {
  @include grid-row();
  padding: 0.5rem 0;

  /* conditionally style orderItem components */
  .item-image {
    padding-left: 0;
    width: 28%;
  }
  .item-details {
     width:39%
  }
  .item-price-subtotal {
    padding-right: 0;
    width: 33%;
  }
}
.cart-success-item + .cart-success-item{
  border-top: 1px solid lighten($black, 60);
}
.cart-success-buttons {
  text-align: center;
  padding-bottom: 10px;
  border-bottom: 1px solid lighten($black, 60);
}
</pre>

</section>
