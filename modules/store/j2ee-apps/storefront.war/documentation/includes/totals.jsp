<section id="totals-docs" class="docs-section">

	<h2>Totals</h2>
	<p>The total section is a reusable treatment for order totals. This can be used in the mini-cart, cart page or any other order summary.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<div class="demo-total-wrapper">
      <c:import url="/documentation/includes/totalsContent.jspf"/>
		</div>
	</div>

<h4 >HTML</h4>
<format:prettyPrint>
  <jsp:attribute name="htmlString">
    <c:import url="/documentation/includes/totalsContent.jspf"/>
  </jsp:attribute>
</format:prettyPrint>


<h4 >Sass</h4>
<pre class="prettyprint">
.totals {
  font-weight: $bold;
  display:table;
  width:100%;

  .total-row {
    display:table-row;
    @include font-size(14px);
    @include rem-size(line-height, 16px);
  }
  .total-label, .total-amount {
    display:table-cell;
    text-align:right;
    vertical-align:top;
    padding: 4px 10px;

  }
  .note {
    font-weight: $regular;
    color: lighten($black, 40);
    display:block;
    margin:0;
  }

  .savings {
    @include font-size(18px);
  }
  .total {
    background: $deep-gray;
    color: $base-white;
    @include font-size(18px);
  }
}
</pre>


</section>
