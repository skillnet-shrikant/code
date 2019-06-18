/* =========================================================
 * kp.backtotop.js
 * =========================================================
 * Sets a "back to top" button, fixed on page for user to click
 * and scroll back to top of window.
 *
 * Requires jquery.throttle plugin
 * Requires Modernizr
 * ========================================================= */
(function (global, $, namespace) {

	"use strict";

	var BackToTop = function (element, options) {
		this.init ('backToTop', element, options);
	};

	BackToTop.prototype = {
		constructor: BackToTop,
		init: function init(type, element, options) {
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);
			this.$scroller = $(this.options.scroll_parent);

			var self = this;

			this.$scroller.scroll($.throttle( 250, function(){
				self.toggleDisplay();
			}));

			this.$element.on('click', this.scrollToTop);
		},
		toggleDisplay: function toggleDisplay() {
			if (this.displayTest()) {
				this.show();
			} else {
				this.hide();
			}
		},
		scrollToTop: function scrollToTop() {
			$("html, body").animate({
				scrollTop: 0
			}, 400);
		},
		show: function show() {
			this.$element.fadeIn();
		},
		hide: function hide() {
			this.$element.fadeOut();
		},
		displayTest: function displayTest() {
			//optionally set responsive rules for showing this element here. (use modernizr match media)

			return this.$scroller.scrollTop() > 90;
		}
	};


	$.fn.backToTop = function backToTop(option) {
		var el = this,
				options = $.extend({}, $.fn.backToTop.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'backToTop');
			if (!data) {
				$.data(this, 'backToTop', (data = new BackToTop(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.backToTop.defaults = {
		scroll_parent: window
	};

	$.fn.backToTop.Constructor = BackToTop;


	$(function () {
		$('[data-backtotop]').backToTop();
	});


}(this, window.jQuery, "KP"));