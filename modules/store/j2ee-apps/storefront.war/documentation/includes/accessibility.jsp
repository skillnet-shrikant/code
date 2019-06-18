<section id="accessibility-docs" class="docs-section">
	<h1>Accessibility</h1>
	<p>The <code>sr-only</code> and <code>sr-only-focusable</code> classes are used to let screen-readers to grab elements on the page and read them when the user's accessibility setting is switched on.</p>

	<p>Accessibility classes should <em>only</em> be used for accessibilty and do not appear visible to users in the browser.</p>

	<h4>HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<div class="search-icon">
				<span class="icon-search"></span>
				// add your sr-only class as a span after the element your referring to
				<span class="sr-only">Search</span>
			</div>
		</jsp:attribute>
	</format:prettyPrint>

	<h4>SASS</h4>
	<pre class="prettyprint">
// the mixin is declared in _utility.scss
@mixin sr-only {
	border: 0 none;
	clip: rect(0px, 0px, 0px, 0px);
	height: 1px;
	margin: -1px;
	overflow: hidden;
	padding: 0;
	position: absolute;
	width: 1px;
}
@mixin sr-only-focusable {
	&:active, &:focus{
		clip: auto;
		height: auto;
		margin: 0;
		overflow: visible;
		position: static;
		width: auto;
	}
}
// the class is defined in _base.scss
.sr-only {
	@include sr-only;
}
.sr-only-focusable {
	@include sr-only-focusable;
}
	</pre>


</section>
