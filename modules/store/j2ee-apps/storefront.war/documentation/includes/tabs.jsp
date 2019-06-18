<section id="tabs-docs" class="docs-section">

	<h2>Tabs</h2>
	<p>Tabs, like accordions, are text/html content areas that maximize content on a page without the need for extra real estate. They stack up behind one another and are navigateable by small title 'tabs' at the top of the container that show regardless of what tab your viewing. Anything can be added to a tab but normally contain product info or reviews.</p>
	<p>You can customize the tab classes in <code>_tabs.scss</code>.</p>

	<div class="docs-example">
		<h4>Example</h4>
		<p>Tabs are clicked to reveal the individual tab content underneath.</p>
		<div class="tabs" data-tabs>
			<ul class="tabs-container">
				<li id="tab1" class="tab-title active" aria-controls="tab1" role="tab">Tab 1</li>
				<li id="tab2" class="tab-title" aria-controls="tab2" role="tab">Tab 2</li>
				<li id="tab3" class="tab-title" aria-controls="tab3" role="tab">Tab 3</li>
			</ul>
			<div class="tabs-content">
				<div class="tab-body active" aria-labelledby="tab1panel" role="tabpanel" id="tab1panel">
					<div class="tab-body-content">
						<p>This is the first panel of the basic tab example. You can place all sorts of content here including a grid.</p>
					</div>
				</div>
				<div class="tab-body" aria-labelledby="tab2panel" role="tabpanel" id="tab2panel">
					<div class="tab-body-content">
						<p>This is the second panel of the basic tab example.</p>
					</div>
				</div>
				<div class="tab-body" aria-labelledby="tab3panel" role="tabpanel" id="tab3panel">
					<div class="tab-body-content">
						<p>This is the third panel of the basic tab example.</p>
					</div>
				</div>
			</div>
		</div>
	</div>

	<h4 >HTML</h4>
	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<div class="tabs" data-tabs>
				<ul class="tabs-container">
					<li id="tab1" class="tab-title active" aria-controls="panel1" role="tab">Tab 1</li>
					<li id="tab2" class="tab-title" aria-controls="panel2" role="tab">Tab 2</li>
					<li id="tab3" class="tab-title" aria-controls="panel2" role="tab">Tab 3</li>
				</ul>
				<div class="tabs-content">
					<div class="tab-body active" aria-labelledby="panel1" role="tabpanel" id="tab1panel">
						<div class="tab-body-content">
							<p>This is the first panel of the basic tab example. You can place all sorts of content here including a grid.</p>
						</div>
					</div>
					<div class="tab-body" aria-labelledby="panel2" role="tabpanel" id="tab2panel">
						<div class="tab-body-content">
							<p>This is the second panel of the basic tab example.</p>
						</div>
					</div>
					<div class="tab-body" aria-labelledby="panel3" role="tabpanel" id="tab3panel">
						<div class="tab-body-content">
							<p>This is the third panel of the basic tab example.</p>
						</div>
					</div>
				</div>
			</div>
		</jsp:attribute>
	</format:prettyPrint>


	<h4 >Javascript</h4>
<pre class="prettyprint">
See plugin: kp.tabs.js
</pre>
</section>
