<section id="category-page-docs"  class="docs-section">
	<h2>Category Page</h2>

	<p>The category page is a display of inline product tiles that extends from end to end.</p>

	<p>This is added here primarily to demonstrate the use of block grids. Block grids are a way to evenly split contents of a list within a grid structure and are best if a row of images or paragraphs needs to be evenly spaced for no matter what screen size.</p>

	<div class="docs-example">
		<h4>Example</h4>
	<div class="categoryPage">
		<ul class="product-grid">
		  <li><%@ include file="productTileContent.jspf" %></li>
		  <li><%@ include file="productTileContent.jspf" %></li>
		  <li><%@ include file="productTileContent.jspf" %></li>
		  <li><%@ include file="productTileContent.jspf" %></li>
		  <li><%@ include file="productTileContent.jspf" %></li>
		  <li><%@ include file="productTileContent.jspf" %></li>
		  <li><%@ include file="productTileContent.jspf" %></li>
		  <li><%@ include file="productTileContent.jspf" %></li>
		</ul>
	</div>
	</div>

<h4 >HTML</h4>
<format:prettyPrint>
	<jsp:attribute name="htmlString">
		<div class="categoryPage">
			<ul class="product-grid">
				<li>...</li>
				<li>...</li>
				<li>...</li>
			</ul>
		</div>
	</jsp:attribute>
</format:prettyPrint>

<h4 >Sass</h4>
<pre class="prettyprint">
.category-grid {
	@include block-grid(
		$per-row: 1,
		$spacing: 20px,
		$base-style: true
	);
	@media &#35;{$medium-up} {
		@include block-grid(
			$per-row: 2,
			$spacing: 20px,
			$base-style: false
		);
	}
	@media &#35;{$large-up} {
		@include block-grid(
			$per-row: 4,
			$spacing: 15px,
			$base-style: false
		);
	}
}
</pre>

</section>