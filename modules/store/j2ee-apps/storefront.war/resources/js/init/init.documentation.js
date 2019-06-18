/*!
 * Documentation Init
 */
(function (global, $, namespace) {
	// "use strict";

	var loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug;

	var documentation = {
		init : function () {
			// for the menu scroll feature (only preventDefault on nav menu links okay??)
			$('a[href*="#"]:not(".docs-section a")').click(function(e) {
				e.preventDefault();
				var target = this.hash;
				$target = $(target);

				$('html, body').stop().animate({
					'scrollTop': $target.offset().top - 20
				}, 900, 'swing', function () {
					window.location.hash = target;
				});
			});


			// to highlight the selected section in the side nav
			$(window).scroll(function() {
				var windscroll = $(window).scrollTop(),
						anchor = "";
				//loop through sections and test if on screen. if on screen set anchor to this id and break from loop
				$('.doc-wrapper	.docs-section').each(function(i) {
					var $this = $(this);
					anchor = "#" + $this.attr('id');
					if ($(this).position().top + $this.height() > windscroll) {
						return false;
					}
				});
				// anchor set, highlight nav.
				if (anchor !== '') {
					$('.side-nav a').removeClass('sidenav-active');
					$('.side-nav a[href$=' + anchor + ']').addClass('sidenav-active');
				}

			}).scroll();


			// Facets
			$('.link-facet-items, .facet-swatch, .tile-facet-items').click(function(){
				$(this).toggleClass('active');
			});

			//pagination click listener
			$('body').on('click', '.page-num', function(){
				$('.page-num').removeClass('active');
				$(this).addClass('active');
			});

			//swatch click listener
			$('body').on('click', '.swatch', function(event){
				// do not toggle if swatch is already seleceted
				if ( $(this).hasClass('active') ) {
					return;
				} else {
					$(this).removeClass('active').siblings().removeClass('active');
					$(this).toggleClass('active');
				}
			});

			// cart update quantity functionality
			$('.counter').on('change', function(){
				var clickedUnit, unitPrice, qty, calcPrice, calcPriceFixed;
				clickedUnit = $(this).parents('.order-item-section').siblings().find($('.unit-price-line'));
				unitPrice = parseFloat($(clickedUnit).text(),10);
				qty = $(this).val();
				calcPrice = unitPrice * qty;
				// make sure 2 decimal places
				calcPriceFixed = calcPrice.toFixed(2);
				$(this).parents('.order-item-section').siblings().find($('.calculated-price')).text(calcPriceFixed);
			});

			//Slick init
			$('.product-tile-slider').slick({
				dots: false,
				infinite: true,
				slidesToShow: 3,
				slidesToScroll: 3,
				responsive: [
					{
						breakpoint: global[namespace].config.largeMin,
						settings: {
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			/* Modal Examples */
			// HTML
			// Using an outside file to add modal HTML content
			$('.launch-example-modal').click(function(){
				var $modalTarget = document.getElementById('modal-example') ? $('#modal-example') : global[namespace].utilities.createModal('modal-example');
				$modalTarget.modal({
					'url': 'exampleModal.jsp',
				});
			});

			// Javascript
			// Using 'content' object to add modal html content
			$('.launch-jscontent-modal').click(function(){
				var $modalTarget = document.getElementById('modal-example') ? $('#modal-example') : global[namespace].utilities.createModal('modal-example');
				$modalTarget.modal({
					'content': '<div class="modal-header"><div class="title-bar"><h2 class="title">I\'m a Javascript Modal!</h2></div></div><div class="modal-body"><p>This content is added via a Javascript object.</p></div><div class="modal-footer"><a href="" data-dismiss="modal" class="button secondary">Close</a></div>'
				});
			});
			// Using open, close & toggle to open an existing modal
			$('.launch-toggle-modal').click(function(){
				var $modalTarget = document.getElementById('modal-example') ? $('#modal-example') : global[namespace].utilities.createModal('modal-example');
				$modalTarget.modal('toggle');
			});


			/* Loader Example */
			$('.launch-example-loader').click(function(){
				global[namespace].loader.showLoader();
				var loaderTimeout = setTimeout(function () {
					global[namespace].loader.hideLoader();
				}, 3000);
			});

			/* Accordion Example */

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

		}
	};

	global[namespace] = global[namespace] || {};

	global[namespace].documentation = documentation;

})(this, window.jQuery, "KP");
