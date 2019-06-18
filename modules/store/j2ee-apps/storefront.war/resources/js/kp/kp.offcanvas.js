/* =========================================================
 * kp.offcanvas.js
 * =========================================================
 * This is based off of the Foundation offcanvas plugin, but
 * stripped down to the essentials for this implementation.
 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

	var Offcanvas = function Offcanvas(element, options) {
				this.init ('offcanvas', element, options);
			},
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			$menu = $('.off-canvas-menu'),
			$wrap = $('.off-canvas-wrap'),
			$window = $(window);

	function setOffCanvasHeight(isSideCart){
		// prevent the page from scrolling past the bottom of the off-canvas nav when it's open
		var offCanvasHeight = 0;
		if (isSideCart) {
			offCanvasHeight = $window.height();
			$('#side-cart').css('height', offCanvasHeight);
			$('.side-cart-items').css('height', offCanvasHeight - 128);
		}
		else {
			$menu.children('ul').each(function () {
				offCanvasHeight += $(this).outerHeight(true);
			});
		}
		$wrap.css('minHeight', $window.height()).css('height', offCanvasHeight);
	}

	function removeOffCanvasHeight(){
		$wrap.css('height', 'auto');
		if ($('#side-cart').length > 0) {
			$('#side-cart').removeAttr('style');
			$('.side-cart-items').removeAttr('style');
		}
	}

	//PUBLIC
	Offcanvas.prototype = {
		constructor: Offcanvas,
		init: function init(type, element, options) {

			var self = this,
					move_class = '',
					side_cart_class = 'side-cart-overlap';
			if (loggingDebug) {
				console.debug('init offcanvas with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);

			if (this.options.open_method === 'move') {
				move_class = 'move-right';
			} else if (this.options.open_method === 'overlap_single') {
				move_class = 'offcanvas-overlap-right';
			} else if (this.options.open_method === 'overlap') {
				move_class = 'offcanvas-overlap';
			}

			$('body').off('.offcanvas')
					.on('click.' + namespace + '.offcanvas', '.off-canvas-toggle', function (e) {
						// toggle the offcanvas menu
						self.toggle(move_class, this);
					})
					.on('click.' + namespace + '.offcanvas', '.side-cart-toggle', function (e) {
						// toggle the side cart
						self.toggle(side_cart_class, this);
					})
					.on('click.' + namespace + '.offcanvas', '.off-canvas-menu a', function (e) {
						var $parent = $(this).parent();
						if(self.options.close_on_click && !$parent.hasClass('has-submenu')){
							// close the menu
							self.hide.call(self, move_class);
						} else if ($parent.hasClass('has-submenu')) {
							e.preventDefault();
						}
					})
					.on('click.' + namespace + '.offcanvas', '.exit-off-canvas', function (e) {
						self.hide(move_class);
						self.hide(side_cart_class);
					});

			// when there's a click in the off-canvas-menu, re-calculate the off-canvas-menu height
			$menu.on('click', '.accordion-title', function(){
				// we need to wait for the accordion to open/close
				setTimeout(function(){
					setOffCanvasHeight();
				}, 500);
			});

		},
		toggle: function(class_name, trigger) {
			if (this.$element.is('.' + class_name)) {
				this.hide(class_name, trigger);
			} else {
				this.show(class_name, trigger);
			}
		},
		show: function(class_name, trigger) {
			this.$element.trigger('open').trigger('open.' + namespace + '.offcanvas');
			this.$element.addClass(class_name);
			if (trigger) {
				trigger.setAttribute('aria-expanded', 'true');
			} else {
				this.$element.find('.off-canvas-toggle').attr('aria-expanded', 'true');
			}
			if (class_name == 'side-cart-overlap') {
				setOffCanvasHeight(true);
			}
			else {
				setOffCanvasHeight();
			}
		},
		hide: function(class_name, trigger) {
			this.$element.trigger('close').trigger('close.' + namespace + '.offcanvas');
			this.$element.removeClass(class_name);
			if (trigger) {
				trigger.setAttribute('aria-expanded', 'false');
			} else {
				this.$element.find('.off-canvas-toggle').attr('aria-expanded', 'false');
			}
			removeOffCanvasHeight();
		}
	};

	$.fn.offcanvas = function offcanvas(option) {
		var el = this,
				options = $.extend({}, $.fn.offcanvas.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'offcanvas');
			if (!data) {
				$.data(this, 'offcanvas', (data = new Offcanvas(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.offcanvas.defaults = {
		open_method : 'overlap',
		close_on_click : true
	};

	$.fn.offcanvas.Constructor = Offcanvas;


	$(function () {
		$('[data-offcanvas]').offcanvas();
	});


}(this, window.jQuery, "KP"));
