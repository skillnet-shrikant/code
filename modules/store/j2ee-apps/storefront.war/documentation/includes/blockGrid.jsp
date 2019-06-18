<section id="block-grid-docs"  class="docs-section">
	<h2>Block Grid</h2>

	<p>We are using Foundation's block grid system. Block grids are a way to evenly split contents of a list within a grid
		structure and are best if a row of images or paragraphs needs to be evenly spaced for no matter what screen size. </p>
	<p>Again, although the base block grid styles are available, you should make use of the mixins so that the class names are
		semantic. The base styles are intended for use in managed content where the authors cannot make use of mixins.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<ul class="custom-block-grid">
			<li><img src="http://placehold.it/250x250"></li>
			<li><img src="http://placehold.it/250x250"></li>
			<li><img src="http://placehold.it/250x250"></li>
			<li><img src="http://placehold.it/250x250"></li>
			<li><img src="http://placehold.it/250x250"></li>
			<li><img src="http://placehold.it/250x250"></li>
			<li><img src="http://placehold.it/250x250"></li>
			<li><img src="http://placehold.it/250x250"></li>
		</ul>
	</div>


	<h4 >HTML</h4>
<format:prettyPrint>
	<jsp:attribute name="htmlString">
		<ul class="custom-block-grid">
			<li>...</li>
			<li>...</li>
			<li>...</li>
			<li>...</li>
			<li>...</li>
		</ul>
	</jsp:attribute>
</format:prettyPrint>


	<h4 >Sass</h4>
<pre class="prettyprint">
.custom-block-grid {
  @include block-grid(
    $per-row: 2,
    $spacing: 20px,
    $base-style: true
  );
  @media &#35;{$medium-up} {
    @include block-grid(
      $per-row: 4,
      $spacing: 20px,
      $base-style: false
    );
  }
  @media &#35;{$large-up} {
    @include block-grid(
      $per-row: 5,
      $spacing: 15px,
      $base-style: false
    );
  }
}
</pre>

</section>