/* =========================================================
 * kp.dropdown.js
 * =========================================================

 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

	var Dropdown = function Dropdown(element, options) {
			this.init ('dropdown', element, options);
		},
		CONSTANTS = global[namespace].constants,
		loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
		maxPageWidth = global[namespace].config.maxWidth;

	function tabOutOfDropdown() {
		$('body').on('keyup', function(e){
			if ($(e.target).parents('.dropdown.active').length === 0) {
				$('.active .dropdown-toggle').trigger('mouseleave').trigger('click');
				$('body').off('keyup');
			}
		});
	}

	function clickOutOfDropdown() {
		$('body').on('click', function(e){
			if ($(e.target).parents('.dropdown.active').length === 0) {
				$('.active .dropdown-toggle').trigger('mouseleave').trigger('click');
				$('body').off('click');
			}
		});
	}

	//PUBLIC
	Dropdown.prototype = {
		constructor: Dropdown,
		init: function init(type, element, options) {
			if (loggingDebug) {
				console.debug('init dropdown with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}

			var self = this;
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);
			this.$toggle = this.$element.find('.dropdown-toggle');
			this.$menu = this.$element.find('.dropdown-menu');
			this.url = this.$element.attr('data-url');
			this.nocache = this.$element.attr('data-nocache');
			this.isLoaded = false;

			// pass is_hover through data attributes
			this.is_hover = this.$element.attr('data-is-hover');
			if (typeof this.is_hover !== 'undefined' && this.is_hover) {
				self.options.is_hover = true;
			}

			if (Modernizr.touchevents) {
				self.options.is_hover = false;
			}

			/* Event binding */
			if (self.options.is_hover) {
				this.$toggle
					.off('dropdown')
					.on('mouseenter.' + namespace + '.dropdown', function () {
						clearTimeout(self.timeout);
						self.timeout = setTimeout(function () {
							/* small delay before opening the menu means that if you briefly mouse-over another trigger on your
							 way to the menu contents, you won't close this menu and open a neighboring menu */
							self.open.call(self);
						},250);
					})
					.on('mouseleave.' + namespace + '.dropdown', function () {
						clearTimeout(self.timeout);
						self.timeout = setTimeout(function () {
							self.close.call(self);
						}.bind(this), 150);
					});
				this.$menu
					.on('mouseenter.' + namespace + '.dropdown', function () {
						clearTimeout(self.timeout);
					})
					.on('mouseleave.' + namespace + '.dropdown', function () {
						clearTimeout(self.timeout);
						self.timeout = setTimeout(function () {
							self.close.call(self);
						}.bind(this), 150);
					});
			} else {
				this.$toggle
					.off('dropdown')
					.on('click.' + namespace + '.dropdown', function(e){
						self.toggle();
						e.preventDefault();
					});
				$('body').off('dropdown').on('click.' + namespace + '.dropdown, touchstart.' + namespace + '.dropdown', function (e) {
					//Will close any open item if the click does not originate from this menu
					if ($(e.target).closest(element).length > 0) {
						return;
					}
					self.close();
				});

				/* clicks in the menu-list will close the dropdown */
				this.$menu.off('dropdown').on('click.' + namespace + '.dropdown', '.menu-list', function(e){
					self.close();
				});
			}

			// make keyboard accessible
			this.$toggle.on('keydown', function(e){
				var keycode = (e.keyCode ? e.keyCode : e.which);
				// enter and spacebar trigger accordion
				if (keycode == 13 || keycode == 32) {
					e.preventDefault();
					e.stopPropagation();
					self.toggle(this);
				}
			});

			this.$menu.on('keydown', 'a', function(e){
				var keycode = (e.keyCode ? e.keyCode : e.which);
				// enter and spacebar trigger accordion
				if (keycode == 13 || keycode == 32) {
					e.preventDefault();
					e.stopPropagation();
					$(this).click();
				}
			});

		},
		toggle: function () {
			// hasClass('active') is false
			if (this.$element.hasClass('active')) {
				this.close.call(this);
			} else {
				// in touch screen, touch primary nav gets you here
				this.open.call(this);
			}
		},
		close : function (){
			this.$toggle.attr('aria-expanded', false);
			this.$menu.hide().attr('aria-expanded', false);
			this.$element.removeClass('active').trigger('closed');
		},
		open : function (){
			// close open dropdowns before opening another.
			if ($('.dropdown.active').length > 0) {
				$('body').off('keyup').off('click');
				$('.dropdown.active .dropdown-toggle').attr('aria-expanded', false);
				$('.dropdown.active .dropdown-menu').hide().attr('aria-expanded', false);
				$('.dropdown.active').removeClass('active').trigger('closed');
			}

			var self = this,
				maxWidth = maxPageWidth,
				windowWidth = window.innerWidth,
				toggleLeft = this.$toggle.offset().left,
				menuWidth = this.$menu.outerWidth(),
				forceLeft = false,
				leftPos, rightPos;

			function insertContent(response){
				self.$menu.html(response);
				self.isLoaded = true;

				// trigger picturefill for ajax requests in browsers that don't support <picture>
				// if (!window.HTMLPictureElement && $(response).find('picture').length > 0) {
				// 	picturefill();
				// }
			}

			//clear out css values
			this.$menu.css("left","").css("right","");

			/* force open left / edge detection */
			if (toggleLeft + menuWidth > windowWidth || toggleLeft + menuWidth > maxWidth || this.$toggle.hasClass('force-left')) {
				forceLeft = true;
			}

			if (!forceLeft) {
				leftPos = '0px';
				rightPos = 'auto';
			}
			else {
				leftPos = 'auto';
				rightPos = '0px';
			}

			// trigger is bigger than menu, lets make the menu at least as wide as the trigger
			if (this.$toggle.outerWidth() >= menuWidth) {
				leftPos = '0px';
				rightPos = '0px';
			}

			if (this.url !== undefined && (this.isLoaded === false || this.nocache !== undefined)) {
				if (loggingDebug) {
					console.log('making ajax request');
				}

				$.ajax({
					url: self.url,
					dataType: 'html',
					success: insertContent
				});
			}
			this.$toggle.attr('aria-expanded', true);
			this.$menu.css({"left" : leftPos, "right" : rightPos}).show().attr('aria-expanded', true);
			this.$element.addClass('active').trigger('opened');

			tabOutOfDropdown();
			clickOutOfDropdown();
		}
	};

	$.fn.dropdown = function dropdown(option) {
		var el = this,
			options = $.extend({}, $.fn.dropdown.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'dropdown');
			if (!data) {
				$.data(this, 'dropdown', (data = new Dropdown(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.dropdown.defaults = {
		is_hover : false
	};

	$.fn.dropdown.Constructor = Dropdown;

	$(function () {
		// accessibility
		$('.dropdown-toggle').attr('tabindex', '0');
		$('.dropdown-toggle a').attr('tabindex', '-1');
		$('[data-is-hover="true"] .dropdown-toggle').on('focus', function(){
			$(this).trigger('mouseenter');
		});

		// init dropdowns
		$('[data-dropdown]').dropdown();
	});


}(this, window.jQuery, "KP"));
