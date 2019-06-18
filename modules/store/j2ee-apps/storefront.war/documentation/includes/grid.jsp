<section id="grid-docs"  class="docs-section">
	<h2>Grid</h2>

	<p>The responsive grid gives us the scaffolding for our site structure. Grids are fluid and stretch to 100% until they
		hit a max width. We're using Foundation's grid system, which includes basic grid class names and grid mixins.
		<strong>USE THE MIXINS ONLY!</strong> Although the base grid styles are available, you should make use of the mixins so that the class
		names are semantic. The base styles are intended for use in managed content where the authors cannot make use of mixins.
	</p>

	<div class="docs-example">
		<h4>Example</h4>
		<div class="grid-demo">
			<div class="grid-column-first">First Element</div>
			<div class="grid-column-second">Second Element</div>
			<div class="grid-column-third">Third Element</div>
		</div>
	</div>


	<h4 >HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<div class="grid-demo">
				<div class="grid-column-first">First Element</div>
				<div class="grid-column-second">Second Element</div>
				<div class="grid-column-third">Third Element</div>
			</div>
		</jsp:attribute>
	</format:prettyPrint>

	<p>There are two mixins for grid structure: <code>grid-row</code> for the wrapper element around the
		grid items, and <code>grid-column</code> for the items in the grid. To use with media queries, set the grid-columns
		for mobile-first (not wrapped in a media query). Then use the media queries to override the column widths. We are not
		restricted to a 12 columns grid. You can set the widths to any percentage you need for the layout.</p>

	<h4 >Sass</h4>
	<pre class="prettyprint">
.grid-demo {
  @include grid-row();
}
.grid-column-first {
  @include grid-column(12);
  @media &#35;{$medium-up}{
    width:50%
  }
  @media &#35;{$large-up}{
    width:33.333%
  }
}
.grid-column-second {
  @include grid-column(12);
  @media &#35;{$medium-up}{
    width:50%
  }
  @media &#35;{$large-up}{
    width:33.333%
  }
}
.grid-column-third {
  @include grid-column(12);
  @media &#35;{$large-up}{
    width:33.333%
  }
}
	</pre>

</section>