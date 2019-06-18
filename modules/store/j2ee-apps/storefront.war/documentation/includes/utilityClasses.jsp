<section id="utilities-docs" class="docs-section">
	<h2>Utility Classes</h2>
	<p><code>_base.scss</code> includes some useful utility classes. Most of these are meant for use in managed content as
		we have the mixins available to us when authoring the page sass.</p>

	<h3>Float Classes</h3>
	<p>Float an element to the left or right with a class. The class is intended for use in managed content.</p>

	<h4>HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
		  <div class="left">...</div>
		  <div class="right">...</div>
		</jsp:attribute>
	</format:prettyPrint>

	<h3>ClearFix</h3>
	<p>Clear floats inside of an element with the clearfix utility. You should use the mixin when possible. The class is intended for use in managed content.</p>

	<h4>HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
		  <div class="clearfix">...</div>
		</jsp:attribute>
	</format:prettyPrint>

	<h4>SASS</h4>
<pre class="prettyprint">
// using the clearfix mixin
.my-container-class {
  @include clearfix;
}
</pre>

	<h3>Hiding content</h3>
	<p>Force an element to be hidden (including for screen readers) with the use of the .hide class. Use the sr- prefixed sr-only to show an element for only screen readers. Feel fre to use the sr-only, sr-focusable in your markup. The elements these apply to ususally don't have unique classes to add the mixin to.</p>
	<h4>HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
		  <div class="hide">...</div>
		  <label class="sr-only">Keyword Search</label>
		  <a class="skip-to-main" href="#content">Skip to main content</a>
		</jsp:attribute>
	</format:prettyPrint>

	<h4>SASS</h4>
<pre class="prettyprint">
// Usage as a mixin
.skip-to-main{
  @include sr-only;
  @include sr-only-focusable;
}
</pre>
</section>