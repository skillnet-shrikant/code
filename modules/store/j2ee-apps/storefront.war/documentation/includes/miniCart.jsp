<section id="mini-cart-docs" class="docs-section">

	<h2>Mini Cart</h2>
	<p>The mini cart is a cart summary that is accessed from the bag button in the header. It contains a small version of the cart item list and the estimated totals.</p>

	<div class="docs-example">
	<h4>Example</h4>
		<div class="mini-cart demo">
			<div class="mini-cart-header">
				<a href="#">
					<span aria-hidden="true" class="icon icon-cart mini-cart-icon"></span>
					<span class="mini-cart-count">1</span>
					Cart
				</a>
			</div>
			<div class="mini-cart-expanded">
				<div class="mini-cart-item-count"><span class="count-number">##</span> items in your cart.</div>
				<div class="mini-cart-item-list">
					<c:forEach begin="0" end="2" varStatus="loop">
						<div>
							<%@ include file="/documentation/fragments/miniOrderItem.jspf" %>
						</div>
					</c:forEach>
				</div>
				<%-- end mini cart item list--%>

				<div class="mini-cart-footer">
					<div class="mini-cart-adspace">
						<img src="http://placehold.it/140x150">
					</div>
					<%@ include file="/documentation/fragments/miniCartTotals.jspf" %>
				</div>
			</div>
		</div>
	</div>


  <h4 >HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<div class="mini-cart-header">
				<a href="#">
					<span aria-hidden="true" class="icon icon-cart mini-cart-icon"></span>
					<span class="mini-cart-count">1</span>
					Cart
				</a>
			</div>
			<!-- the minicart dropdown content -->
			<div class="mini-cart-expanded">
				<div class="mini-cart-item-count"><span class="count-number">##</span> items in your cart.</div>
				<div class="mini-cart-item-list">
					<!-- include your miniOrderItems.jspf fragment file here -->
				</div>

				<div class="mini-cart-footer">
					<div class="mini-cart-adspace">
						<img src="http://placehold.it/140x150">
					</div>
					<div class="mini-cart-totals">
						<!-- include your miniOrderTotals.jspf fragment file here -->
						<!-- See Totals area -->
						<div class="mini-cart-buttons">
			        <button class="button primary">View cart &amp; checkout</button>
			      </div>
					</div>
				</div>
			</div>
		</jsp:attribute>
	</format:prettyPrint>

<h4 >Sass</h4>
<pre class="prettyprint">
// Use the components directory to construct the meat of your CSS.
// Categorize your styles here based on element in the file name.

// Mini cart definitions
.mini-cart {
  cursor: pointer;
  position:relative;
}

.mini-cart-header &gt; a {
  @include font-size(12px);
  @include rem-size(line-height, 32px);
  border-color: transparent;
  border-width: 1px 1px 0px 1px;
  border-style: solid;
  display:inline-block;
  position:relative;
  padding: 0 1.5rem 0 2.5rem;
  text-decoration: none;
  color: $black;
  text-align:center;
  z-index:200;
}

.mini-cart-icon, .cart-count {
  display: inline-block;
}
.mini-cart-icon {
  width: 30px;
  height: 30px;
  position: absolute;
  top: 0px;
  left: 0;
}
.cart-count {
  color: $base-white;
  position: absolute;
  border: none;
  top: 0px;
  left: 15px;
  @include rem-size(font-size, 13px);
  @include rem-size(line-height, 20px);
  background: $medium-gray;
  border-radius: 50px;
  text-align:center;
  height: 20px;
  width: 20px;
}

.mini-cart-expanded {
  display: none;
  position: absolute;
  right:0;
  background: $base-white;
  border:solid 1px $base-gray;
  width: 520px;
  margin-top:-1px;
}

.mini-cart:hover .mini-cart-expanded{
  display: block;
  z-index:100;
}
.mini-cart:hover .mini-cart-header &gt; a {
  background-color:$white;
  border-color:$base-gray;
}

// the border after mini-cart item count //
.mini-cart-item-count {
  @include rem-size(font-size, 14px);
  @include rem-size(line-height, 40px);
  border-bottom: 1px solid $base-gray;
  padding:0 20px 5px;
}
.mini-cart-item-list {
  max-height: 250px;
  overflow: auto;
  padding:0 20px;
}
.mini-cart-item {
  @include grid-row();
  padding: 0.5rem 0;

  // customized _orderItems classes in mini-cart
  .item-image {
    width:30%;
    padding-left:0; //override padding from grid-column mixin
  }
  .item-details {
    width: 35%;
    li {
      @include font-size(12px);
      @include rem-size (line-height, 16px);
    }

    .item-name {
      @include font-size(14px);
    }
  }

  .item-price-subtotal {
    @include font-size(14px);
    width: 35%;
    padding-right:0; //override padding from grid-column mixin

    .total {
      color: $black;
    }

    a {
      color: darken($primary-color, 15);
      text-decoration: none;
      &:hover {
        @include transition;
        color: darken($primary-color, 30);
      }
    }
  }

}
// the border between mini cart items //
.mini-cart-item + .mini-cart-item {
  border-top: 1px solid $base-gray;
}

.mini-cart-footer {
  @include grid-row();
  border-top: 1px solid $base-gray;
  padding: 10px 0;

  .mini-cart-adspace {
    @include grid-column(3.6);
  }

  .mini-cart-totals {
    @include grid-column(8.4);
  }

  // customized from _totals.scss to fit container
  .totals {
    background:none;
    @include font-size(13px);
    @include rem-size(line-height, 16px);
    dl {
      padding:0;
    }
    dt {
      padding: 5px 0;
    }
    .savings {
      @include font-size(16px);
    }
    .total {
      padding: 5px 10px 6px;
      @include font-size(16px);
    }

  }
}

.mini-cart-buttons {
  border-top: 1px solid $base-gray;
  padding-top: 5px;
  margin-top: 10px;
  .button {
    margin: 5px auto;
  }
}
</pre>




</section>
