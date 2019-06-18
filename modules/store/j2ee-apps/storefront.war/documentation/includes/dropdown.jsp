<section id="dropdown-docs" class="docs-section">

	<h2>Dropdowns</h2>
	<p>The dropdown plugin provides a simple (one-level) menu that can be triggered with hover or click. Click is the default (and recommended) behavior.</p>
	<p>Dropdown styles are stored in <code>_dropdowns.scss</code>. For dropdown styling it's usually better to leave the default styling to be very basic and then enhance the style by using a modified class or with contextual selectors. For example you may use <code>.utility-nav .dropdown-toggle</code> to style the dropdown header in the utility nav without affecting the dropdown style else in the site.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<div class="dropdown" data-dropdown>
			<div class="button primary dropdown-toggle" id="dropdownMenu1">
				Click for menu
			</div>
			<div class="dropdown-menu">
				<ul class="menu-list" role="menu" aria-labelledby="dropdownMenu1">
					<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Golden Gate Bridge</a></li>
					<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Alcatraz</a></li>
					<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Ferry Building</a></li>
					<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Fisherman's Wharf</a></li>
				</ul>
			</div>
		</div>
	</div>

	<h4 >HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<div class="dropdown" data-dropdown>
				<div class="button primary dropdown-toggle" id="dropdownMenu1">
					Click for menu
				</div>
				<div class="dropdown-menu">
					<ul class="menu-list" role="menu" aria-labelledby="dropdownMenu1">
						<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Golden Gate Bridge</a></li>
						<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Alcatraz</a></li>
						<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Ferry Building</a></li>
						<li role="presentation"><a role="menuitem" tabindex="-1" href="#">Fisherman's Wharf</a></li>
					</ul>
				</div>
			</div>
		</jsp:attribute>
	</format:prettyPrint>

	<p>The dropdown behavior is automatically applied to elements with the data-dropdown attribute. Otherwise you can initialize calling the jQuery plugin.</p>
	<h4>Javascript</h4>
	<pre class="prettyprint">
$('#myDropdown').dropdown(/*{options}*/);
//  Defaults
//  $.fn.dropdown.defaults = {
//    is_hover : false
//  };
</pre>
</section>