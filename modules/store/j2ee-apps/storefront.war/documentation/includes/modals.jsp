<section id="modal-docs" class="docs-section">

	<h2>Modals</h2>
	<p>Modals are popup text/html areas used to give users information without having reload or navigate away from the page. Anything can be added to a modal area such as product info, confirmations, videos, etc.</p>
	<p>You can customize the modal classes in <code>_modals.scss</code>. The modal template is a variable in <code>kp.modal.js</code></p>

	<h3>Using HTML</h3>
	<div class="docs-example">
		<h4>Example</h4>
		<p>This modal is triggered using an <strong>HTML</strong> link.</p>
		<a href="exampleModal.jsp" class="modal-trigger button primary" data-target="modal-example">Gimme some modal</a>
	</div>

	<format:prettyPrint>
		<jsp:attribute name="htmlString">
			<div class="modal-launch">
				<a href="exampleModal.jsp" class="modal-trigger" data-target="modal-example">Gimme some modal</a>
			</div>
		</jsp:attribute>
	</format:prettyPrint>

	<h3>Using Javascript</h3>
	<div class="docs-example">
		<h4>Example</h4>
		<p>This modal is triggered via a <strong>Javascript</strong> click event and uses a separate file to display modal content.</p>
		<span class="button primary launch-example-modal">Show me the modal</span>
	</div>

<pre class="prettyprint">
$('.launch-example-modal').click(function(){
  var $modalTarget = document.getElementById('modal-example') ? $('#modal-example') : global[namespace].utilities.createModal('modal-example');
  $modalTarget.modal({'url': 'exampleModal.jsp'});
});
</pre>


<div class="docs-example">
	<h4>Example</h4>
	<p>This modal is triggered via a <strong>Javascript</strong> click event and uses a Javascript object display modal contenet.</p>
	<span class="button primary launch-jscontent-modal">Modalize me</span>
</div>

<pre class="prettyprint">
$('.launch-jscontent-modal').click(function(){
  var $modalTarget = document.getElementById('modal-example') ? $('#modal-example') : global[namespace].utilities.createModal('modal-example');
  $modalTarget.modal({'content': 'your content template here'});
});
</pre>

<div class="docs-example">
	<h4>Example</h4>
	<p>This will trigger an <em>existing</em> modal using Javascript and a 'toggle' method.</p>
	<span class="button primary launch-toggle-modal">Mo' modal mo' problems</span>
</div>

<p>The <code>toggle</code> method can be replaced with <code>open</code> or <code>close</code> if necessary. Note: content will come from cached version.</p>
<pre class="prettyprint">
$('.launch-toggle-modal').click(function(){
  var $modalTarget = document.getElementById('modal-example') ? $('#modal-example') : global[namespace].utilities.createModal('modal-example');
  $modalTarget.modal('toggle');
});
</pre>
</section>