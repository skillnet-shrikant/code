<section id="accordion-docs" class="docs-section">
	<h2>Accordion</h2>
	<p>A generic slide toggling feature that lets you show and hide content on the page as you need it. </p>
	<p>Accordion styles are stored in <code>_accordion.scss</code>. For accordion styling it's usually better to leave the default styling to be very basic and then enhance the style by using a modified class or with contextual selectors. For example you may use <code>.off-canvas .accordion-container</code> to style the accordion in the offcanvas nav without affecting the accordion style elsewhere in the site.</p>


	<div class="docs-example">
		<h4>Example</h4>
		<c:import url="/documentation/fragments/demoAccordion1.jspf" />

	</div>

	<h4>HTML</h4>
<format:prettyPrint>
	<jsp:attribute name="htmlString"><c:import url="/documentation/fragments/demoAccordion1.jspf" /></jsp:attribute>
</format:prettyPrint>

	<p>The accordion behavior is automatically applied to elements with the data-accordion attribute. Otherwise you can
		initialize calling the jQuery plugin. Please note that the default for the multi-selectable behavior can be overridden
		in the html markup by setting <code>aria-multiselectable</code> on the accordion element to true. By default, it is
		set to false per the <a href="https://www.w3.org/TR/wai-aria/states_and_properties#aria-multiselectable" target="_blank">W3 spec</a>
	</p>
	<h4>JavaScript</h4>

	<pre class="prettyprint">
$('.myAccordion').accordion(/*{options}*/);

//  $.fn.accordion.defaults = {
//    allow_multi_expand: true,
//    trigger_class_name: '.section-title',
//    content_class_name: '.section-container',
//    accordion_class_name: '.accordion-section'
//  };
	</pre>

	<p>The open and close actions can be called programmatically on the accordion if you need to trigger them via
		javascript. In order to open an accordion you need to pass in the trigger (accordion-title), the content
		(accordion-body), or the wrapper (accordion-container) as a DOM or jQuery element</p>

	<div class="docs-example">
		<h4>Example</h4>

		<p>
			<span class="button secondary js-accordion-example-open">Open</span>
			<span class="button secondary js-accordion-example-close">Close</span>
			<span class="button secondary js-accordion-example-close-all">Close All</span>
		</p>

		<div class="accordion" id="js-accordion-example" role="tablist" aria-multiselectable="true">
			<div class="accordion-container">
				<div class="accordion-title" role="tab" aria-controls="accordion-content-1" id="accordion-title-1"><span class="icon icon-plus" aria-hidden="true"></span>Section 1</div>
				<div class="accordion-body" aria-labelledby="accordion-title-1" role="tabpanel" id="accordion-content-1">
					<div class="accordion-body-content">
						<p>Lorem ipsum dolor sit amet, mutat erroribus id his, sonet deseruisse quo id. Mei malis efficiantur te, in qui agam habeo, in tantas corpora pri.</p>
					</div>
				</div>
			</div>

			<div  class="accordion-container">
				<div class="accordion-title" role="tab" aria-controls="accordion-content-2" id="accordion-title-2"><span class="icon icon-plus" aria-hidden="true"></span>Section 2</div>
				<div class="accordion-body" aria-labelledby="accordion-title-2" role="tabpanel" id="accordion-content-2">
					<div class="accordion-body-content">
						<p>Lorem ipsum dolor sit amet, mutat erroribus id his, sonet deseruisse quo id. Mei malis efficiantur te, in qui agam habeo, in tantas corpora pri. Nihil ocurreret disputando id per, quo te dicit impedit.</p>

						<p>Pro bonorum legendos conclusionemque ut, quo albucius quaestio principes at. Has cu referrentur efficiantur neglegentur.</p>
					</div>
				</div>
			</div>

		</div>
	</div>

	<h4 >JavaScript</h4>
	<pre class="prettyprint">
// initialize
var $exAccordion = $('#js-accordion-example').accordion();

$('.js-accordion-example-open').click(function(){
   $exAccordion.accordion('open', $('#accordion-title-2'));
});
$('.js-accordion-example-close').click(function(){
   $exAccordion.accordion('close', $('#accordion-title-2'));
});
$('.js-accordion-example-close-all').click(function(){
   $exAccordion.accordion('closeAll');
});
	</pre>

</section>
