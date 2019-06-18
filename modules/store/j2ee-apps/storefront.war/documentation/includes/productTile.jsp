<section id="product-tile-docs" class="docs-section">

	<h2>Product Tile</h2>
	<p>The product tile is the box that contains a product image and key product details. It is used for product display
		on category and search pages as well as in cross sell-type blocks.</p>

	<div class="docs-example">
		<h4>Example</h4>
    <c:import url="/documentation/includes/productTileContent.jspf"/>
	</div>

	<h4 >HTML</h4>
  <format:prettyPrint>
    <jsp:attribute name="htmlString">
      <c:import url="/documentation/includes/productTileContent.jspf"/>
    </jsp:attribute>
  </format:prettyPrint>

<h4 >Sass</h4>
<pre class="prettyprint">
.product-tile {
  height: auto !important;
  border: 1px solid lighten(black, 20);
  overflow: hidden;
  position: relative;
  @include transition;
  &:hover {
    @include box-shadow (0, 0, 4px, lighten(black, 20));
  }
  .button-quickview {
    width: 65%;
    background: rgba(255,255,255,0.85);
    border: 2px solid rgba(255,255,255,0.95);
    text-align: center;
    color: $medium-gray;
    @include font-size(12px);
    padding: 5px 15px;
    position: absolute;
    left: 0;  // left/right added to allow use of abs positioning to center btn
    right: 0;
    margin: 0 auto;
    bottom: 20px;
    cursor: pointer;
    text-transform: uppercase;
    @include opacity(0);
    @include font-size(16px);
    @include transition;
    &:hover {
      background: rgba(255,255,255,0.95);
    }
  }
  .product-image {
    position:relative;
    img {
      margin: 0 auto;
      display: block;
    }
    &:hover .button-quickview {
      @include opacity(100);
      bottom: 10px;
    }
  }
  .swatch-group {
    margin: 10px;
  }
  .unavailable {
    opacity: 0.2;
    pointer-events: none;
    cursor: default;
  }
  .active {
    border: 2px solid $black;
  }
  .product-details-container {
    padding: 0 10px;
    color: $black;
    @include rem-size(line-height, 20px);
  }
  .product-name {
    @include font-size(16px);
    font-weight: $bold;
  }
  .product-brand {
    @include font-size(14px);
    margin-bottom:.5rem;
  }
  .price {
    @include font-size(14px);
    @include rem-size(line-height, 20px);
    margin-bottom:.5rem;
  }
  .compare-price, .savings  {
    @include font-size(12px);
  }
  .ratings-container {
    padding: 0 0 10px 10px;
    div {
      display: inline-block;
      @include font-size(12px);
    }
  }
}
</pre>

</section>
