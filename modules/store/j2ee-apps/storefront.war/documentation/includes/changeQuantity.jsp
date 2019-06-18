<section id="change-quantity-docs" class="docs-section">

	<h2>Change Quantity Clicker</h2>
	<p>This componenet allows the user to update the quantity of single items of a particular product. Can be included wherever quantity needs to be updated, such as:</p>

  <ul>
    <li>Mini-cart</li>
    <li>Shopping cart page</li>
    <li>Checkout</li>
    <li>Added to cart modal</li>
  </ul>

  <p>Will stop at zero going down and a pre-defined number going up.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<%@ include file="changeQuantityContent.jspf" %>
	</div>


	<h4 >HTML</h4>
	<format:prettyPrint>
	<jsp:attribute name="htmlString">
		<%@ include file="changeQuantityContent.jspf" %>
	</jsp:attribute>
	</format:prettyPrint>


	<h4 >SASS</h4>
<pre class="prettyprint">
.change-quantity {
	@include clearfix;

	.quantity-group {
		float:left;
		border: 2px solid lighten($black, 40);
		@include rounded;
		@include no-select;
	}

	.plus-icon, .minus-icon {
		@include font-size(20px);
		background: lighten($black, 80);
		cursor: pointer;
		float:left;
		padding: 5px 10px;
		@include rem-size(line-height, 24px);
	}
	.plus-icon {
		border-right: 1px solid lighten($black, 40);
	}
	.minus-icon {
		border-left: 1px solid lighten($black, 40);
	}
	.current-quantity {
		float:left;
	}
	.counter {
		border: none;
		@include rem-size(width, 56px);
		@include rem-size(height, 34px);
		text-align: center;
	}
	.inactive {
		opacity: 0.3;
    pointer-events: none;
    cursor: default;
	}
}
</pre>

  <h4 >JavaScript</h4>
<pre class="prettyprint">
See plugin: kp.quantify.js
</pre>

</section>
