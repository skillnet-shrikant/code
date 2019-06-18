<section id="page-template-docs" class="docs-section">
	<h2>Page Templates</h2>
	<p>We are using template tags to automatically provide any page with template headers, footers and other custom elements. To use them, wrap the page content in <code>layout</code> tags. Can be customized to match page design or to add or remove features.</p>

	<pre class="prettyprint">
&lt;layout:yourPageName&gt;
	&lt;!-- ... your page code ... --&gt;
&lt;/layout:yourPageName&gt;
	</pre>

	<p>Tag prefixes are defined in <code>/sitewide/fragments/tags.jspf</code>.</p>

</section>