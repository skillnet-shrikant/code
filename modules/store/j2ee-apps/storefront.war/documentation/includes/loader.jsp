<section id="loader-docs" class="docs-section">

	<h2>Loading Animation</h2>
	<p>This is a simple utility to show a loading screen. This informs the user that a process is running and prevents them from interacting with the UI.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<p>Click the button to launch the loader. (Loader will hide automatically after timeout fires)</p>
		<span class="button primary launch-example-loader">Show me the loader</span>
	</div>

	<h4>HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString"><span class="button primary launch-example-loader">Show me the loader</span></jsp:attribute>
	</format:prettyPrint>


	<h4 >Javascript</h4>
<pre class="prettyprint">
$('.launch-example-loader').click(function(){
  /* use this to call the loader */
  global[namespace].loader.showLoader();
  /* hideLoader is wrapped in timeout just for this example */
  var loaderTimeout = setTimeout(function () {
    global[namespace].loader.hideLoader();
  }, 3000);
});

</pre>

	<p>Loader colors can be adjusted/added in the loader.scss file.</p>

	<h4>SASS</h4>
<pre class="prettyprint">
/* width and height of the loading graphic (images/loader.gif) */
$loading-graphic-width: 46px;
$loading-graphic-height: 46px;

/* background color of the loader graphic.
	(can be set to transparent if you have transparent graphic) */
$loading-graphic-background: $white;

/* padding around the graphic,
	useful if you have a non-transparent graphic */
$loading-graphic-padding: 7px;

/* color and opacity of the overlay */
$overlay-color: $light-gray;
$overlay-opacity: 0.8;
</pre>

</section>
