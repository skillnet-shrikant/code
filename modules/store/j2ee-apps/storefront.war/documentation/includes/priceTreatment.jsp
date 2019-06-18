<section id="price-treatment-docs" class="docs-section">
	<h2>Price treatment</h2>
	<p>There are two types of item prices, sale and non-sale. If an item is on sale we will show the original price,
		usually with a strike through before the sale price. Depending on the retailer, there may be a "compare at" or
		savings line that would show the savings versus full retail price.</p>

	<div class="docs-example">
		<h4>Non-Sale Example</h4>
		<c:import url="/documentation/includes/priceTreatmentNonSaleContent.jspf"/>
	</div>
	<div class="docs-example">
		<h4>Sale Example (with savings)</h4>
		<c:import url="/documentation/includes/priceTreatmentSaleContent.jspf"/>
	</div>

	<h4 >HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<!-- Non-sale example -->
			<c:import url="/documentation/includes/priceTreatmentNonSaleContent.jspf"/>
		</jsp:attribute>
	</format:prettyPrint>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<!-- Sale example -->
			<c:import url="/documentation/includes/priceTreatmentSaleContent.jspf"/>
		</jsp:attribute>
	</format:prettyPrint>

	<h4 >Sass</h4>
	<pre class="prettyprint">
.regular-price {
  color: $black;
}
.compare-price {
  font-weight: $bold;
  color: $black;
}
.original-price {
  text-decoration: line-through;
  color: lighten($black, 30);
}
.sale-price {
  color: $sale-color;
  @include rem-size(padding-left, 8px);
  font-weight: $bold;
}
.savings {
  color: $sale-color;
}
</pre>

</section>
