<nav class="side-nav">
	<h4><a href="index.jsp">Setup</a></h4>
	<c:if test="${param.navSection == 'setup'}">
		<ul>
			<li><a href="index.jsp#getting-started-docs">Before you start</a></li>
			<li><a href="index.jsp#base-docs">Base CSS</a></li>
			<li><a href="index.jsp#partials-docs">Sass Partials</a></li>
			<li><a href="index.jsp#utilities-docs">Utility Classes</a></li>
			<li><a href="index.jsp#globals-docs">Sass Globals</a></li>
			<li><a href="index.jsp#media-queries-docs">Media Queries</a></li>
			<li><a href="index.jsp#plugin-init-docs">Plugin Initialization</a></li>
			<li><a href="index.jsp#page-template-docs">Page Templates</a></li>
			<li><a href="index.jsp#accessibility-docs">Accessibility</a></li>
		</ul>
	</c:if>

	<h4><a href="presentation.jsp">Presentation</a></h4>
	<c:if test="${param.navSection == 'presentation'}">
		<ul>
			<li><a href="presentation.jsp#grid-docs">Grid</a></li>
			<li><a href="presentation.jsp#block-grid-docs">Block Grid</a></li>
			<li><a href="presentation.jsp#button-docs">Buttons</a></li>
			<li><a href="presentation.jsp#forms-docs">Forms</a></li>
			<li><a href="presentation.jsp#form-validation-docs">Form Validation</a></li>
			<li><a href="presentation.jsp#alerts-docs">Alerts</a></li>
			<li><a href="presentation.jsp#sprites-docs">sprites</a></li>
		</ul>
	</c:if>
	<h4><a href="interaction.jsp">Interaction</a></h4>
	<c:if test="${param.navSection == 'interaction'}">
		<ul>
			<li><a href="interaction.jsp#loader-docs">Loader</a></li>
			<li><a href="interaction.jsp#modal-docs">Modals</a></li>
			<li><a href="interaction.jsp#dropdown-docs">Dropdown</a></li>
			<li><a href="interaction.jsp#accordion-docs">Accordion</a></li>
			<li><a href="interaction.jsp#tabs-docs">Tabs</a></li>
			<li><a href="interaction.jsp#tooltip-docs">Tooltip</a></li>
			<li><a href="interaction.jsp#slider-docs">Carousel Slider</a></li>
		</ul>
	</c:if>
	<h4><a href="navigation.jsp">Navigation</a></h4>
	<c:if test="${param.navSection == 'navigation'}">
		<ul>
			<li><a href="navigation.jsp#utility-nav-docs">Utility Nav</a></li>
			<li><a href="navigation.jsp#primary-nav-docs">Primary Nav</a></li>
			<li><a href="navigation.jsp#facet-docs">Facets</a></li>
			<li><a href="navigation.jsp#paginations-docs">Pagination</a></li>
		</ul>
	</c:if>
	<h4><a href="ecommerce.jsp">eCommerce</a></h4>
	<c:if test="${param.navSection == 'ecommerce'}">
		<ul>
			<li><a href="ecommerce.jsp#price-treatment-docs">Price Treatment</a></li>
			<li><a href="ecommerce.jsp#totals-docs">Totals area</a></li>
			<li><a href="ecommerce.jsp#change-quantity-docs">Change Quantity</a></li>
			<li><a href="ecommerce.jsp#product-tile-docs">Product Tile</a></li>
			<li><a href="ecommerce.jsp#promo-code-docs">Promo Code</a></li>
			<li><a href="ecommerce.jsp#added-to-cart-modal-docs">Added to Cart Modal</a></li>
			<li><a href="ecommerce.jsp#mini-cart-docs">Mini Cart</a></li>
			<li><a href="ecommerce.jsp#cart-page-docs">Cart Items</a></li>
		</ul>
	</c:if>
</nav>