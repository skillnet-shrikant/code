<section id="button-docs" class="docs-section">

	<h2>Buttons</h2>
	<p>There are two button styles, primary and secondary (feel free to create more if needed). Button colors can be adjusted/added in the <code>_colors.scss</code> file. Appearance can be customized in the <code>_buttons.scss</code> file.</p>
	<div class="docs-example">
		<h4>Example</h4>
		<button class="button primary">Primary</button>
		<button class="button secondary">Secondary</button>
	</div>

	<h4 >HTML</h4>

<format:prettyPrint>
	<jsp:attribute name="htmlString">
		<button class="button primary">Primary</button>
		<button class="button secondary">Secondary</button>
	</jsp:attribute>
</format:prettyPrint>


	<p>There are additional modifiers for the button classes for larger or smaller appearance.</p>
	<div class="docs-example">
		<h4>Example</h4>
		<button class="button primary small">Small Button</button>
		<button class="button primary medium">Medium Button</button>
		<button class="button primary">Normal Button</button>
	</div>

	<h4 >HTML</h4>

<format:prettyPrint>
	<jsp:attribute name="htmlString">
		<button class="button primary small">Small Button</button>
		<button class="button primary medium">Medium Button</button>
		<button class="button primary">Normal Button</button>
	</jsp:attribute>
</format:prettyPrint>

</section>