<section id="slider-docs" class="docs-section">

	<h2>Carousel Slider</h2>

	<p>The slider is a left/right scrollable UX component that allows users to view a selection of elements without having
		to leave the page or expand a section. The slider is responsive, and can show a different number of items per
		breakpoint.</p>

	<p>Slider styles are in <code>_sliders.scss</code>. Additionally, the slider plugin styles are found in <code>vendor/_slick.scss</code>. Use the <code>_slider.scss</code> file to override slick styles contextually. Only update <code>_slick.scss</code> with styles you want to apply to the sliders globally. </p>

	<p>Sliders can be used for products (cross sells, recently viewed, etc), or for marketing content controlled by the BCC or XM.</p>
	<p><a href="http://kenwheeler.github.io/slick/">Slick plugin documentation</a></p>
	<div class="docs-example">
		<h4>Example</h4>

		<div class="slider-container">
			<div class="section-title">
				<h2>You May Also Like</h2>
			</div>
			<div class="slider-content">
				<div class="product-tile-slider">
					<c:forEach begin="0" end="10" varStatus="loop">
						<div>
							<%-- extra wrapper needed for slick slider --%>
							<%@ include file="productTileMiniContent.jspf" %>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>


	</div>
	<h4 >HTML</h4>
	<format:prettyPrint>
    <jsp:attribute name="htmlString">
			<div class="slider-container">
			  <div class="section-title">
					<h2>You May Also Like</h2>
			  </div>
			  <div class="slider-content">
			    <div class="product-tile-slider">
		        <div>
		          <!-- Include productTileMiniContent.jspf file(s) here -->
		          <!-- See Product tile -->
		        </div>
			    </div>
			  </div>
			</div>
    </jsp:attribute>
  </format:prettyPrint>
	<pre class="prettyprint">
</pre>

	<h4 >JavaScript</h4>
	<pre class="prettyprint">
$('.product-tile-slider').slick({
  dots: false,
  infinite: true,
  slidesToShow: 3,
  slidesToScroll: 3,
  responsive: [
    {
      breakpoint: global[namespace].config.largeMin,
      settings: {
        slidesToShow: 2,
        slidesToScroll: 2
      }
    }
  ]
});
</pre>


</section>
