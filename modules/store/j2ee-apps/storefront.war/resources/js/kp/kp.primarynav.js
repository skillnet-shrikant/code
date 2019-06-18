/* =========================================================
 * primaryNav.js
 * Handles a 3-level nav menu with the third level as a flyout.
 * Configurable to trigger on hover or click.
 * Requires modernizer for touch detection. (Recommend assuming touch and not using hover)
 * Created by KnowledgePath Solutions.
 * ========================================================= */

(function (global, $, Modernizr, namespace) {

	"use strict";

	var Primarynav = function (element, options) {
		this.init ('primarynav', element, options);
	};

	function elementHasClass(el, className) {
		if (el.classList) {
			return el.classList.contains(className);
		} else {
			return new RegExp('(^| )' + className + '( |$)', 'gi').test(el.className);
		}
	}

	function isMenuToggle(el) {
		// check if this link can toggle a menu
		if (!elementHasClass(el, 'locked')  && el.parentNode.querySelector('.primary-nav-menu') !== null) {
			return true;
		}
		return false;
	}

	Primarynav.prototype = {
		constructor : Primarynav,
		init: function init (type, element, options) {
			var self = this;
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);

			if (Modernizr.touchevents) {
				self.options.is_primary_hover = false;
			}

			/* Event binding */
			if (!self.options.is_primary_hover) {
				/* Primary nav click events */
				this.$element
						.off('primarynav')
						.on('click.' + namespace + '.primary-nav', '.primary-nav-button', function(e){
							if (!isMenuToggle(this)) {
								return;
							}
							var $menu = $(this).closest('.primary-nav-item');
							if ($menu.hasClass('active') && self.options.is_primary_button_clickable) {
								/* for touch devices, if already opened then follow link. */
								return;
							} else {
								self.toggle($menu);
								e.preventDefault();
								e.stopPropagation();
								return false;
							}
						});
				$('body').off('primarynav').on('click.' + namespace + '.primary-nav, touchstart.' + namespace + '.primary-nav', function (e) {
					//Will close any open item if the click does not originate from this menu
					if ($(e.target).closest(element).length > 0) {
						return;
					}
					self.close.call(self, self.$element.find('.primary-nav-item'));
				});
			} else {
				/* Primary nav hover events */
				this.$element
						.off('primarynav')
						.on('mouseenter.' + namespace + '.primary-nav', '.primary-nav-button', function (e) {
							if (!isMenuToggle(this)) {
								return;
							}
							var $menu;
							clearTimeout(self.timeout);
							$menu = $(this).closest('.primary-nav-item');
							self.timeout = setTimeout(function () {
								/* small delay before opening the menu means that if you briefly mouse-over another trigger on your
								 way to the menu contents, you won't close this menu and open a neighboring menu */
								self.close.call(self);
								self.open.call(self, $menu);
							},100);
							// close any open flyouts.

						})
						.on('mouseleave.' + namespace + '.primary-nav', function (e) {
							clearTimeout(self.timeout);
							self.timeout = setTimeout(function () {
								self.close.call(self);
							}.bind(this), 100);
						})
						.on('mouseenter.' + namespace + '.primary-nav', '.primary-nav-menu', function (e) {
							if (!isMenuToggle(this)) {
								return;
							}
							clearTimeout(self.timeout);
						})
						.on('mouseenter.' + namespace + '.primary-nav', '.keyword-search-form', function (e) {
							clearTimeout(self.timeout);
							self.timeout = setTimeout(function () {
								self.close.call(self);
							}.bind(this), 100);
						});
			}
		},
		toggle: function ($menu) {
			//return this[this.$element.hasClass('open') ? 'hide' : 'show'](1);
			this.close.call(this, this.$element.find('.primary-nav-item').not($menu));
			// hasClass('active') is false
			if ($menu.hasClass('active')) {
				this.close.call(this, $menu);
			} else {
				// in touch screen, touch primary nav gets you here
				this.open.call(this, $menu);
			}
		},
		close : function ($menu){
			if ($menu) {
				var $dropdown = $menu.find('.primary-nav-menu');
				$dropdown.hide();
				$menu.removeClass('active');
			} else {
				this.$element.find('.primary-nav-item').removeClass("active").find('.primary-nav-menu').hide();
			}
		},
		open : function ($menu){
			var $dropdown = $menu.find('.primary-nav-menu'),
					$topNavContainer = this.$element.find('nav'),
					menuLeft = $menu.offset().left,
					navLeft = $topNavContainer.offset().left,
					navWidth,
					leftPos, rightPos,
					forceLeft = false,
					subNavWidth;

			//clear out css values
			$dropdown.css("left","").css("right","");

			if (this.options.is_primary_full_width) {

				leftPos = -(menuLeft - navLeft) + 'px';
				rightPos = 'auto';

			} else {

				$topNavContainer = this.$element.find('nav');
				navWidth = $topNavContainer.width();
				subNavWidth = $dropdown.outerWidth();

				/* edge detection */
				if (navLeft + navWidth - menuLeft < subNavWidth) {
					forceLeft = true;
				}

				if (!forceLeft) {
					leftPos = '0px';
					rightPos = 'auto';
				} else {
					leftPos = 'auto';
					rightPos = '0px';
				}
			}

			$dropdown.css({"left" : leftPos, "right" : rightPos}).show();
			$menu.addClass('active');
		}
	};
	$.fn.primarynav = function (option) {
		return this.each(function () {
			var $this = $(this),
					data = $this.data('primarynav'),
					options = typeof option === 'object' && option;
			if (!data) {
				$this.data('primarynav', (data = new Primarynav(this, options)));
			} else {
				$.extend(data.options, options);
			}
			if (typeof option === 'string') {
				data[option]();
			}
		});
	};

	$.fn.primarynav.defaults = {
		is_primary_hover : true,
		is_primary_button_clickable: false,
		is_primary_full_width : false
	};

	$.fn.primarynav.Constructor = Primarynav;

	$(function () {
		$('[data-primarynav]').primarynav();
	});


}(this, window.jQuery, window.Modernizr, "KP"));

/* =========================================================
 * Flyout nav is a separate plugin. Makes it modular.
 * ========================================================= */

(function (global, $, Modernizr, namespace) {

	"use strict";

	var Flyoutnav = function (element, options) {
		this.init ('flyoutnav', element, options);
	};

	function elementHasClass(el, className) {
		if (el.classList) {
			return el.classList.contains(className);
		} else {
			return new RegExp('(^| )' + className + '( |$)', 'gi').test(el.className);
		}
	}

	Flyoutnav.prototype = {
		constructor : Flyoutnav,
		init: function init (type, element, options) {
			var self = this;
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);

			if (Modernizr.touchevents) {
				self.options.is_flyout_hover = false;
			}

			/* Event Binding */
			if (!self.options.is_flyout_hover) {
				/* flyout nav click events */
				this.$element
						.off('flyoutnav').on('click.' + namespace + '.flyoutnav', '.sub-nav-button', function(e){
							var flyout = $(this).parent().find('.sub-nav-menu');

							if (flyout.parent().hasClass('active') && self.options.is_flyout_button_clickable) {
								/* for touch devices, if already opened then follow link. */
								return;
							} else {
								self.toggle(flyout);
								e.preventDefault();
								e.stopPropagation();
								return false;
							}
						});

				$('body').off('flyoutnav').on('click.' + namespace + '.flyoutnav, touchstart.' + namespace + '.flyoutnav', function (e) {
					//Will close any open item if the click does not originate from this menu
					if ($(e.target).closest(element).length > 0) {
						return;
					}
					self.close.call(self);
				});

			} else {
				/* flyout nav hover events */
				this.$element
						.off('flyoutnav')
						.on('mouseenter.' + namespace + '.flyoutnav', '.sub-nav-button', function(e){
							var flyout = $(this).parent().find('.sub-nav-menu');
							clearTimeout(self.timeout);
							self.timeout = setTimeout(function () {
								/* small delay before opening the menu means that if you briefly mouse-over another trigger on your
								 way to the menu contents, you won't close this menu and open a neighboring menu */
								self.close.call(self);
								self.open.call(self, flyout);
							}, 50);
						}).on('mouseleave.' + namespace + '.flyoutnav', function (e) {
							clearTimeout(self.timeout);
							self.timeout = setTimeout(function () {
								self.close.call(self);
							}.bind(this), 50);
						}).on('mouseenter.' + namespace + '.flyoutnav', '.sub-nav-menu', function (e) {
							clearTimeout(self.timeout);
						});
			}

			//event listener

		},

		toggle: function(flyout){
			this.close.call(this);
			this.close.call(this, this.$element.find('.sub-nav-menu').not(flyout));
			if (flyout.parent().hasClass('active')) {
				this.close.call(this, flyout);
			} else {
				this.open.call(this,  flyout);
			}
		},
		open: function(flyout){
			var offset_parent = this.$element.offsetParent(),
					position = this.$element.offset(),
					left = 0,
					menuwidth = this.$element.width() + flyout.width(),
					menuheight = Math.max(flyout.outerHeight(), this.$element.outerHeight());
			position.top -= offset_parent.offset().top;
			position.left -= offset_parent.offset().left;
			left = position.left + this.$element.width();
			this.$element.css({ width: menuwidth, height: menuheight });
			flyout.attr('style', '').css({ top: 0, left: left, height: menuheight}).parent().addClass('active');
		},
		close: function(flyout){
			if (flyout) {
				this.$element.css({ width: '', height: '' });
				flyout.attr('style', '').parent().removeClass('active');
			} else {
				this.$element.css({ width: '', height: '' })
						.find('.sub-nav-menu').attr('style', '').parent().removeClass('active');
			}
		}
	};
	$.fn.flyoutnav = function (option) {
		return this.each(function () {
			var $this = $(this),
					data = $this.data('flyoutnav'),
					options = typeof option === 'object' && option;
			if (!data) {
				$this.data('flyoutnav', (data = new Flyoutnav(this, options)));
			} else {
				$.extend(data.options, options);
			}
			if (typeof option === 'string') {
				data[option]();
			}
		});
	};

	$.fn.flyoutnav.defaults = {
		is_flyout_hover : true,
		is_flyout_button_clickable: true
	};

	$.fn.flyoutnav.Constructor = Flyoutnav;

	$(function () {
		$('[data-flyoutnav]').flyoutnav();
	});


}(this, window.jQuery, window.Modernizr, "KP"));
