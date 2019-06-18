<section id="globals-docs" class="docs-section">
	<h1>Sass Globals</h1>
	<p>Globals are saved in the <code>/resources/scss/globals</code> folder. These files to not contain any sass that would generate css - they are strictly for variable declaration, functions and mixins that can be reused by the sass (for components and pages) that generates css.</p>
	<p>These files create default styling for assets used over the entire site. This includes setting variables for default font face, link colors, assets such	as transitions, border radius, box shadows, and rem calculation. Color declarations and typography will also be stated here.</p>

	<h3>Font Size</h3>

	<p>This will set the font size in rem units. Since IE8 does not support rem units, the pixel value will be used in the IE8 stylesheet. </p>



	<div class="docs-example">
		<h4>Example</h4>
		<p class="big-font-example">Font size is 1.375rem (22px)</p>
		<p class="small-font-example">Font size is 0.625rem (10px)</p>

	</div>

	<h4>SASS</h4>
<pre class="prettyprint">
.big-font-example {
  @include font-size(22px);
}
.small-font-example {
  @include font-size(10px);
}
</pre>

	<h3>Rem Size</h3>

	<p>Pass this mixin a property name and pixel value, and it will set the property using rem units. Again, for IE8, the pixel value will be passed through to the stylesheet instead of the rem unit. You only need to specify rem units for elements that relate to type. For instance you don't have to use rem units for height declarations, but you would use rem units for line-height.</p>

		<div class="docs-example">
		<h4>Example</h4>
		<p class="line-height-example">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id nulla pulvinar, condimentum massa at, porta felis. Quisque tincidunt justo erat, vitae pulvinar tortor vestibulum sed. Quisque maximus volutpat mauris. Duis id odio nec ex consectetur blandit. Aenean sed iaculis metus. Fusce varius ipsum arcu, sed sodales nisl feugiat vel. Etiam quis turpis accumsan augue finibus scelerisque. Suspendisse potenti. Cras rutrum sem at augue mattis commodo. Pellentesque arcu nisl, consequat eu nunc sed, rutrum mattis mi. Vivamus scelerisque nisi vitae urna mattis iaculis. </p>

	</div>

	<h4>SASS</h4>
<pre class="prettyprint">
.line-height-example {
	@include rem-size(line-height, 16px);
}
</pre>


<h3>Transitions</h3>
<p>This will take <code>$property</code>, <code>$duration</code>, and <code>$easing</code> directly from the declarations in the mixin. Include this mixin on any class or id you would like a smooth transition from one state to another.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<div class="transition-example"><span>hover me</span></div>
	</div>
	<h4>SASS</h4>
<pre class="prettyprint">
.transition-example {
	@include transition(background-color, 0.5s, ease-in-out);
	background-color: red;
	&:hover {
		background-color: orange;
	}
}
</pre>

	<h3>Drop Shadows</h3>
	<p>This will add a drop shadow to the DOM element. Use <a href="http://css3gen.com/box-shadow/" target="_blank">this site</a> to get an idea of how your drop shadow numbers will render. Set inset to true if you'd like the shadow to be inset.</p>
	<div class="docs-example">
		<h4>Example</h4>
		<div class="row">
			<div class="column small-6"><div class="box-shadow-example outset"> </div></div>
			<div class="column small-6"><div class="box-shadow-example inset"> </div></div>
		</div>


	</div>
	<h4>SASS</h4>
<pre class="prettyprint">
.outset {
  @include box-shadow(1px, 3px, 10px, $medium-gray);
}
.inset {
  @include box-shadow(1px, 3px, 10px, $deepest-gray, true);
}
</pre>

<h3>Rounded corners</h3>
<p>This will take <code>$radius</code> directly from the declaration in the mixin.</p>
	<div class="docs-example">
		<h4>Example</h4>
		<div class="rounded-example "> </div>
	</div>
	<h4>SASS</h4>
<pre class="prettyprint">
.rounded-example {
  @include rounded(20px);
}
</pre>

<h3>Text Shadows</h3>
<p>Text shadow is like box shadow, but for text. Use <a href="http://css3gen.com/text-shadow/" target="_blank">this site</a> to get an idea of how your text shadow numbers will render. This will take <code>$top</code>, <code>$left</code>, <code>$blur</code> and <code>$color</code> directly from the declarations in the mixin.</p>
	<div class="docs-example">
		<h4>Example</h4>
		<p class="text-shadow-example">This text has a shadow!</p>
	</div>
	<h4>SASS</h4>
	<pre class="prettyprint" style="display:block;">
.text-shadow-example {
  @include text-shadow(1px, 1px, 3px, rgba(150, 150, 150, .5));
}
</pre>
</section>
