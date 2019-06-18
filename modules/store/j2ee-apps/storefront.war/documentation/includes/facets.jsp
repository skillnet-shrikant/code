<section id="facet-docs" class="docs-section">

	<h2>Facets</h2>

	<div class="docs-example facet-example">
		<h4>Example</h4>
		<c:import url="/documentation/includes/facets.jsp" />

	</div>


	<h4 >HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">

			<div class="facet-menu filters" data-filters>
				<div class="accordion" role="tablist" aria-multiselectable="true" data-accordion>
					<div class="accordion-container">
						<div class="facet-title accordion-title" role="tab" aria-controls="category-facet-body" id="category-facet-title">
							 Refine by Category <span class="icon icon-arrow-down"></span>
						</div>
						<div class="facet-body accordion-body" aria-labelledby="category-facet-title" role="tabpanel" id="category-facet-body">
							<div class="accordion-body-content">
								<div class="facet-list links">
									<div class="clear-filter" data-cat="category-cat">
										Clear filters
									</div>
									<div class="link-facet-items facet-item" data-id="catfacet1" data-nstate="catnstate1" data-cat="category-cat">
										Category One
									</div>
									<div class="link-facet-items facet-item" data-id="catfacet2" data-nstate="catnstate2" data-cat="category-cat">
										Category Two
									</div>
									<!-- truncated to save space -->
								</div>
							</div>
						</div>
					</div>

					<div class="accordion-container">
						<div class="facet-title accordion-title" role="tab" aria-controls="style-facet-body" id="style-facet-title">
							 Refine by Style <span class="icon icon-arrow-down"></span>
						</div>
						<div class="facet-body accordion-body" aria-labelledby="style-facet-title" role="tabpanel" id="style-facet-body">
							<div class="accordion-body-content">
								<div class="facet-list checkboxes">
									<div class="clear-filter" data-cat="style-cat">
										Clear filters
									</div>
									<div class="checkbox-facet-items facet-item" data-id="stylefacet1" data-nstate="stylenstate1" data-cat="style-cat" >
										<label><input id="sfacet1" type="checkbox" name="style" value="sone"/>One</label>
									</div>
									<div class="checkbox-facet-items facet-item" data-id="stylefacet2" data-nstate="stylenstate2" data-cat="style-cat" >
										<label><input id="sfacet2" type="checkbox" name="style" value="stwo"/>Two</label>
									</div>
									<!-- truncated to save space -->
								</div>
							</div>
						</div>
					</div>

					<div class="accordion-container">
						<div class="facet-title accordion-title" role="tab" aria-controls="color-facet-body" id="color-facet-title">
							Refine by Color <span class="icon icon-arrow-down"></span>
						</div>
						<div class="facet-body accordion-body" aria-labelledby="color-facet-title" role="tabpanel" id="color-facet-body">
							<div class="accordion-body-content">
								<div class="facet-list swatches">
									<div class="clear-filter" data-cat="color-cat">
										Clear filters
									</div>
									<div class="facet-swatch facet-item" data-id="colorfacet1" data-nstate="colornstate1" data-cat="color-cat"  title="Wood">
										<img src="http://goo.gl/8Jo4u9">
									</div>
									<div class="facet-swatch facet-item" data-id="colorfacet2" data-nstate="colornstate2" data-cat="color-cat"  title="Leather">
										<img src="http://goo.gl/0BJ9bW">
									</div>
									<div class="facet-swatch facet-item unavailable" data-id="colorfacet3" data-nstate="colornstate3" data-cat="color-cat"  title="Tan (Unavailable)">
										<img src="http://goo.gl/UF4uhN">
									</div>
									<!-- truncated to save space -->
								</div>
							</div>
						</div>
					</div>

					<div class="accordion-container">
						<div class="facet-title accordion-title" role="tab" aria-controls="size-facet-body" id="size-facet-title">
							Refine by Size <span class="icon icon-arrow-down"></span>
						</div>
						<div class="facet-body accordion-body" aria-labelledby="size-facet-title" role="tabpanel" id="size-facet-body">
							<div class="accordion-body-content">
								<div class="facet-list tiles">
									<div class="clear-filter" data-cat="size-cat">
										Clear filters
									</div>
									<div class="tile-facet-items facet-item" data-id="sizefacet1" data-nstate="sizenstate1" data-cat="size-cat">
										5
									</div>
									<div class="tile-facet-items facet-item" data-id="sizefacet2" data-nstate="sizenstate2" data-cat="size-cat">
										5.5
									</div>
									<div class="tile-facet-items facet-item unavailable" data-id="sizefacet3" data-nstate="sizenstate3" data-cat="size-cat">
										6
									</div>
									<!-- truncated to save space -->
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</jsp:attribute>
	</format:prettyPrint>


	<h4 >JavaScript</h4>
	<pre class="prettyprint">
// call with the filters plugin
$('.facet-menu').filters();
	</pre>

	<h4 >Sass</h4>
	<pre class="prettyprint">
.facet-menu {
	width: 300px;
	border: 1px solid $border-color;
	.facet-title {
		padding: 10px;
		@include font-size(15px);
		cursor: pointer;
	}
	.facet-body {
		padding-bottom: 1px;
	}
}
.facet-list {
	padding:10px;
	@include clearfix;
	.clear-filter {
		@include font-size(12px);
		text-align: right;
		color: lighten($black, 50);
		padding: 0px 5px;
		cursor: pointer;
	}
	input[type="checkbox"] {
		vertical-align: baseline;
		margin-right: 5px;
	}
	&.links {
		.link-facet-items {
			cursor: pointer;
			@include font-size(12px);
			padding: 0 15px;
			margin: 5px 0;
			&:hover {
				background: lighten($black, 70);
			}
		}
		.active {
			background: lighten($black, 30);
			color: $base-white;
			&:hover {
				background: lighten($black, 30);
			}
		}
		.unavailable {
			opacity: 0.2;
			pointer-events: none;
			cursor: default;
		}
	}
	&.swatches {
		.facet-swatch {
			width: 38px;
			height: 38px;
		}
	}
	&.tiles {
		.tile-facet-items {
			cursor: pointer;
			float: left;
			width: 22%;
			text-align: center;
			margin: 0 2% 2% 0;
			padding: 10px 0;
			color: lighten($black, 40);
			border: 1px solid $border-color;
			@include font-size(14px);
			font-weight: $bold;
			&:hover {
				background: lighten($black, 90);
				border: 1px solid transparent;	 		}
		}
		.active {
			background: lighten($black, 40);
			color: $base-white;
			&:hover {
				background: lighten($black, 40);
			}
		}
		.unavailable {
			opacity: 0.2;
			pointer-events: none;
			cursor: default;
		}
	}
}
.applied-facets {
	.clear-all-link {
		padding: 5px 10px;
	}
}
.clear-filter {
	display:none;
}
	</pre>


</section>
