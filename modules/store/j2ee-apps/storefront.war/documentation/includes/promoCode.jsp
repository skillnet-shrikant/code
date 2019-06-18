<section id="promo-code-docs" class="docs-section">

	<h2>Promo Code Entry</h2>
	<p></p>


	<div class="docs-example">
		<h4>Example</h4>
		<div class="promo-code-container">
			<c:import url="/checkout/includes/promoCode.jsp"/>
		</div>
	</div>

	<h4 >HTML</h4>
	<format:prettyPrint>
    <jsp:attribute name="htmlString">
      <c:import url="/checkout/includes/promoCode.jsp"/>
    </jsp:attribute>
  </format:prettyPrint>


	<h4 >SASS</h4>
<pre class="prettyprint">
.promo-code {
	width: 60%;
	border: 1px solid lighten($black, 30);
	background: lighten($black,80);
	.add-promo {
		padding: 10px;
	}
	.promo-code-title {
		@include font-size(14px);
		margin: 5px 0 0 10px;
		cursor: pointer;
		float: left;
		img {
			width: 20px;
			margin-left: 5px;
		}
		.plus, .minus {
			width: 15px;
			float: left;
		}
	}
	.promo-code-msg {
		display: inline-block;
		padding: 3px 20px 3px 5px;
		margin: 0 10px;
		color: lighten($black, 50);
	}
	.promo-applied-area {
		margin: 0 10px;
	}
	.view-details {
		margin: 0 10px;
		text-decoration: none;
	}
	.icon-remove {
		cursor: pointer;
		vertical-align: middle;
	}
	.too-many-promos-msg {
		display: none;
		padding: 10px;
		@include font-size (16px);
		font-weight: $bold;
	}
	.estimate-shipping-area {
		padding: 10px;
		border-top: 1px solid lighten($black, 30);
		.estimate-shipping {
			padding: 0 10px;
			font-weight: $bold;
			@include font-size(20px);
		}
	}
	.already-applied-msg {
		color: darkred;
	}
}
</pre>

<h4 >JavaScript</h4>
<pre class="prettyprint">
See plugin: kp.promocode.js
</pre>
</section>
