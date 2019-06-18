<section id="media-queries-docs" class="docs-section">
	<h1>Media Queries</h1>
	<p>We are using a mobile-first approach in coding the sass. That is, the base styles you define for a class should be
		for small screens (and up), with media query overrides for the medium and/or large screens. To support this we have
		several media query variables. </p>
	<p>In order to support ie8, we've created a media query mixin that will 'flatten' the media queries on a class by
		applying the small-up, medium-up, and large-up/large-only content to the class without the media query wrappers. </p>


	<h3>Setting breakpoints</h3>

	<p>We have three screen sizes that we support: small, medium, and large. But additional sizes (x-large) can be added
		as needed. The media query variables are held in the declarations partial. To adjust the breakpoints, change the
		ranges for the small, medium and large variables. The row-width is the maximum width the site will stretch to (in
		the largest breakpoint). </p>

	<h4>SASS</h4>
<pre class="prettyprint">
// max width for site
$row-width: calculateRem(960px) !default;
...
// Media Query Ranges
$small-range: (0, 47.49em) !default;
$medium-range: (47.5em, 59.99em) !default;
$large-range: (60em, 99999999em) !default;
</pre>

	<p>In addition, there are corresponding settings for the javascript held in the core/config javascript</p>

	<h4>JAVASCRIPT</h4>
<pre class="prettyprint">
var smallRange =  [0, 759],
    mediumRange =  [760, 959],
    largeRange = [960, 1599999984],
    ...
    config = {
        ...
        maxWidth: 960,
        ...
    }
</pre>

	<h3>Media Query Mixin</h3>

	<p>We are using a media query mixin so that we can automatically generate an ie8 stylesheet that applies all the styles
		for the large breakpoint to each class. Use the following values for media queries</p>
	<dl>
		<dt>small-only</dt>
		<dd>Only applies to screens within the small breakpoint.</dd>
		<dt>medium-only</dt>
		<dd>Only applies to screens within the medium breakpoint.</dd>
		<dt>medium-up</dt>
		<dd>Applies to screens above the medium breakpoint.</dd>
		<dt>large-only</dt>
		<dd>Only applies to screens within the large breakpoint. Really only useful if you have the x-large breakpoint in
			play.</dd>
		<dt>large-up</dt>
		<dd>Applies to screens above the large breakpoint. Use this to target large - if we add a x-large breakpoint
			during future development, it will inherit these styles.</dd>
	</dl>

	<div class="docs-example">
		<h4>Example</h4>
		<div class="mq-demo-box">
			<p>See how I change when you resize the browser.</p>
		</div>
	</div>

	<h4>SASS</h4>
<pre class="prettyprint">
.mq-demo-box {
  //small and up styles
  color: $white;
  background-color: red;

  //medium and up styles
  @include media(medium-up) {
    background-color: blue;
  }

  //large and up styles
  @include media(large-up) {
    color: $black;
    background-color: yellow;
  }
}
</pre>

</section>
