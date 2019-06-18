<section id="sassGlobals">
	<h2>Sass Globals and Usage</h2>
	<p>To be used for reference how to use the mixins and variables served the scss globals directory.</p>
	<p>Default properties are set in the mixin itself and then used as @includes to render CSS in the component files.</p>
	<hr/>

	<h3>Font Size</h3>
	<p><strong>Declaration:</strong></p>
	<pre class="prettyprint" style="display:block;">
	@function calculateRem($size) {
		$remSize: $size / 16px;
		@return $remSize * 1rem;
	}
	@mixin font-size($size) {
		font-size: $size;  // Will be overridden if browser supports rem
		font-size: calculateRem($size);
	}
	</pre>

	<p><strong>Usage:</strong></p>
	<pre class="prettyprint" style="display:block;">
	.title-header {
		@include font-size(20px);
	}
	</pre>
	<p>This will set the font size for the element selected. The function in the declaration determines the rem number and the mixin applies it to whatever class is stated. In the CSS, it will output a px then a rem which will override the former if the browser supports rem.</p>
	<br />

	<h3>Transitions</h3>
	<p><strong>Declaration:</strong></p>
	<pre class="prettyprint" style="display:block;">
	@mixin transition($property: all, $duration: 1s, $easing: ease-in-out) {
		transition: $property, $duration, $easing;
	}
	</pre>

	<p><strong>Usage:</strong></p>
	<pre class="prettyprint" style="display:block;">
	button {
		@include transition($property $duration $easing);
	}
	</pre>
	<p>This will take <code>$property</code>, <code>$duration</code>, and <code>$easing</code> directly from the declarations in the mixin. Include this mixin on any class or id you would like a smooth transition from one state to another.</p>
	<br />

	<h3>Drop Shadows</h3>
	<p><strong>Declaration:</strong></p>
	<pre class="prettyprint" style="display:block;">
	@mixin box-shadow($top, $left, $blur, $color, $inset: false) {
		box-shadow: $top $left $blur $color;
	}
	</pre>

	<p><strong>Usage:</strong></p>
	<pre class="prettyprint" style="display:block;">
	.product_tile {
		@include box-shadow(1px 3px 10px $medium-gray);
	}
	</pre>
	<p>This will add a drop shadow to the DOM element. Use <a href="http://css3gen.com/box-shadow/">this site</a> to get an idea of how your drop shadow numbers will render. Set inset to true if you'd like the shadow to be inset.</p>
	<br />

	<h3>Rounded corners</h3>
	<p><strong>Declaration:</strong></p>
	<pre class="prettyprint" style="display:block;">
	@mixin rounded($radius: 0.5em) {
		border-radius: $radius;
	}
	</pre>

	<p><strong>Usage:</strong></p>
	<pre class="prettyprint" style="display:block;">
	.product_tile {
		@include rounded($radius);
	}
	</pre>
	<p>This will take <code>$radius</code> directly from the declaration in the mixin.</p>
	<br />

	<h3>Text Shadows</h3>
	<p><strong>Declaration:</strong></p>
	<pre class="prettyprint" style="display:block;">
	@mixin text-shadow($top: 0, $left: 1px, $blur: 1px, $color: rgba(0,0,0,.3)) {
		text-shadow: $top $left $blur $color;
	}
	</pre>

	<p><strong>Usage:</strong></p>
	<pre class="prettyprint" style="display:block;">
	.main-header {
		@include text-shadow($top $left $blur $color);
	}
	</pre>
	<p>Text shadow is like box shadow, but for text. Use <a href="http://css3gen.com/text-shadow/">this site</a> to get an idea of how your text shadow numbers will render. This will take <code>$top</code>, <code>$left</code>, <code>$blur</code> and <code>$color</code> directly from the declarations in the mixin.</p>
	<br />

	<h3>Text Field</h3>
	<p><strong>Declaration:</strong></p>
	<pre class="prettyprint" style="display:block;">
	@mixin text-field {
		display: inline-block;
		text-decoration: none;
		font: $form-font-family;
		padding: .5em;
		@include rounded;
	}
	</pre>

	<p><strong>Usage:</strong></p>
	<pre class="prettyprint" style="display:block;">
	form {
		@include text-field;
	}
	</pre>

</section>
