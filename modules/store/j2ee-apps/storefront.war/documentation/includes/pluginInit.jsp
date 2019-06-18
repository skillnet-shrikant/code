<section id="plugin-init-docs" class="docs-section">
	<h2>Plugin Initialization</h2>

	<p>For widely used functional elements, they're likely to be included using custom plugins. Plugins currently in use in this framework:
	<ul>
		<li><a href="interaction.jsp#accordion-docs">Accordion</a></li>
		<li>Back to Top</li>
		<li><a href="interaction.jsp#slider-docs">Carousel/Slider</a></li>
		<li><a href="interaction.jsp#dropdown-docs">Dropdown</a></li>
		<li><a href="navigation.jsp#facet-docs">Facets/Filters</a></li>
		<li>Image Viewer</li>
		<li><a href="interaction.jsp#loader-docs">Loader</a></li>
		<li><a href="interaction.jsp#modal-docs">Modal</a></li>
		<li>Off Canvas</li>
		<li><a href="navigation.jsp#primary-nav-docs">Primary Nav</a></li>
		<li><a href="ecommerce.jsp#promo-code-docs">Promo Code</a></li>
		<li>Profile</li>
		<li><a href="ecommerce.jsp#change-quantity-docs">Quantity picker</a></li>
		<li><a href="interaction.jsp#tabs-docs">Tabs</a></li>
		<li>Typeahead</li>
		<li><a href="presentation.jsp#form-validation-docs">Validate</a></li>
	</ul>

	<p>Note: plugins are different than JavaScript incorporated elements in that they are self-contained in their own files and are self-initialized. There are a couple different ways to initialize them.</p>

	<p>Each plugin has an internal code snippet: <code>$('[data-myplugin]').myplugin();</code> which allows you to initialize it either in the markup or with JavaScript.</p>

	<p>In your JS, initialize the plugin by adding the following code:</p>

	<pre class="prettyprint">
$('.my-plugin').myplugin();
	</pre>

	<p>Or in the markup, add a <code>data-</code> element to the item's top level container:</p>

	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<div class="my-plugin" data-myplugin>
				<!-- ... your page component code ... -->
			</div>
		</jsp:attribute>
	</format:prettyPrint>


</section>