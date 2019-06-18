<section id="partials-docs" class="docs-section">
	<h1>Sass Partials File Structure</h1>
	<p>Sass files need to be imported into the main SCSS file for them to compile to CSS.</p>

	<h3>Steps to create a SASS component</h3>
	<p>First create your partial file in a subdirectory of <code>/resources/scss/</code>. The subdirectory should relate to the use of the element. There are a few already built into this framework:</p>
	<ul>
		<li>/components - for individual component styles such as forms and modals.</li>
		<li>/globals - for variables, mixins and functions used sitewide. No css in this folder.</li>
		<li>/layout - for page layout elements like responsive grids and high-level selectors like html and a tags.</li>
		<li>/pages - to centralize the styles for full pages. Overrides page component styles.</li>
		<li>/vendor - for third party plugins.</li>
	</ul>

	 <p>Always use an underscore to start the filename for files in these directories: <code>_myPartial.scss</code>.</p>

	<p>Then import the partial into the primary SASS file at <code>/resources/scss/main.scss</code> as such, omitting the underscore in the filename when importing the partial:</p>

	<h4>SASS</h4>
	<pre class="prettyprint">
@import "components/myPartial.scss";
	</pre>

	<h3>Main.scss</h3>
	<p><code>main.scss</code> is the master list of imported SASS partials the compiler uses. When you create a new partial file for any modular element be sure to add it to this list with the above format. If the file is not imported here it will not run. Note: these files cascade, so any class will overwrite a duplicate class in files that came before.</p>

	<p>You can import from any folder in your project directory but best to keep them grouped in the <code>resources</code> directory.</p>

</section>
