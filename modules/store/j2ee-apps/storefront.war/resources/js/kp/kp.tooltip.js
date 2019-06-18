/* =========================================================
 /* =========================================================
 * kp.tooltip.js
 * Created by KnowledgePath Solutions.
 * ==========================================================
 * Tooltips are lightweight utilities activated by hovering
 * over an icon to get more info without needing a modal.
 * ========================================================= */

(function (global, $, namespace) {
	"use strict";

	var Tooltip = function Tooltip(element, options) {
			this.init ('tooltip', element, options);
		};

	//PUBLIC
	Tooltip.prototype = {
		constructor: Tooltip,
		init: function init(type, element, options) {
			// if (loggingDebug) {
			// 	console.debug('init tooltip with options:');
			// 	console.debug(Array.prototype.slice.call(arguments));
			// }
			var self = this;
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);

			self.create(this.$element);

			// set timer for displaying the tooltip
			function _startShow(elt, $this, immediate) {
				if (elt.timer) {
					return;
				}

				if (immediate) {
					elt.timer = null;
					self.showTip($this);
				} else {
					elt.timer = setTimeout(function () {
						elt.timer = null;
						self.showTip($this);
					}.bind(elt), self.options.hover_delay);
				}
			}

			function _startHide(elt, $this) {
				if (elt.timer) {
					clearTimeout(elt.timer);
					elt.timer = null;
				}
				self.hide($this);
			}

			$('body').off('.tooltip')
				.on('mouseenter.' + namespace + '.tooltip mouseleave.' + namespace + '.tooltip touchstart.' + namespace + '.tooltip', '[data-tooltip]', function (e) {
					var $this = $(this),
						is_touch = false;

					if ($this.hasClass('open')) {
						if (Modernizr.touchevents) {
							e.preventDefault();
						}
//						self.hide($this);
					}
					else {
						if (/enter|over/i.test(e.type)) {
							_startShow(this, $this);
						} else if (e.type === 'mouseout' || e.type === 'mouseleave') {
							_startHide(this, $this);
						} else {
							_startShow(this, $this, true);
						}
					}
				})
				.on('mouseleave.' + namespace + '.tooltip touchstart.' + namespace + '.tooltip', '[data-tooltip].open', function (e) {
					_startHide(this, $(this));
				});
		},

		showTip : function ($target) {
			return this.show($target);
		},

		getTip : function ($target) {
			var selector = this.selector($target),
				tip = null;

			if (selector) {
				tip = $('span[data-selector="' + selector + '"]' + this.options.tooltip_class);
			}
			return (typeof tip === 'object') ? tip : false;
		},

		// add unique data-selector & aria-describedby attributes
		selector : function ($target) {
			var dataSelector = $target.attr('data-selector');

			if (typeof dataSelector != 'string') {
				dataSelector = 'tooltip-' + Math.random().toString().substring(2,8);
				$target
				.attr('data-selector', dataSelector)
				.attr('aria-describedby', dataSelector);
			}
			return dataSelector;
		},

		create : function ($target) {
			var self = this,
				tip_template = this.options.tip_template;

			if (typeof this.options.tip_template === 'string' && window.hasOwnProperty(this.options.tip_template)) {
				tip_template = window[this.options.tip_template];
			}
			// add the class names set in inheritable_classes function
			var $tip = $(tip_template(this.selector($target), $('<div></div>').html($target.attr('title')).html())),
				classes = this.inheritable_classes($target);

			// add the tooltip to body
			$tip.addClass(classes).appendTo(this.options.append_to);

			if (Modernizr.touchevents) {
				$tip.on('touchstart.' + namespace + '.tooltip', function (e) {
					self.hide($target);
				});
			}
			// remove the default title hover behavior
			$target.removeAttr('title').attr('title', '');
		},

		// set the position of the tooltip depending on classes set in markup
		reposition : function (target, tip, classes) {
			var width, nub, nubHeight, nubWidth, column, objPos;

			tip.css('visibility', 'hidden').show();

			width = target.data('width');
			nub = tip.children('.nub');
			nubHeight = nub.outerHeight();
			nubWidth = nub.outerHeight();

			if ($('html').hasClass('mobile')) {
				tip.css({'width' : '100%'});
			} else {
				tip.css({'width' : (width) ? width : 'auto'});
			}

			objPos = function (obj, top, right, bottom, left, width) {
				return obj.css({
				'top' : (top) ? top : 'auto',
				'bottom' : (bottom) ? bottom : 'auto',
				'left' : (left) ? left : 'auto',
				'right' : (right) ? right : 'auto'
				}).end();
			};
			objPos(tip, (target.offset().top + target.outerHeight() + 10), 'auto', 'auto', target.offset().left);

			if ($('html').hasClass('mobile')) {
				objPos(tip, (target.offset().top + target.outerHeight() + 10), 'auto', 'auto', 12.5, $(this.scope).width());
				tip.addClass('tip-override');
				objPos(nub, -nubHeight, 'auto', 'auto', target.offset().left);
			} else {
				var left = target.offset().left;

				objPos(tip, (target.offset().top + target.outerHeight() + 10), 'auto', 'auto', left);
				// reset nub from small styles, if they've been applied
				if (nub.attr('style')) {
					nub.removeAttr('style');
				}

				tip.removeClass('tip-override');
				if (classes && classes.indexOf('tip-top') > -1) {
					objPos(tip, (target.offset().top - tip.outerHeight()), 'auto', 'auto', left)
					.removeClass('tip-override');
				} else if (classes && classes.indexOf('tip-left') > -1) {
					objPos(tip, (target.offset().top + (target.outerHeight() / 2) - (tip.outerHeight() / 2)), 'auto', 'auto', (target.offset().left - tip.outerWidth() - nubHeight))
					.removeClass('tip-override');
					nub.removeClass('rtl');
				} else if (classes && classes.indexOf('tip-right') > -1) {
					objPos(tip, (target.offset().top + (target.outerHeight() / 2) - (tip.outerHeight() / 2)), 'auto', 'auto', (target.offset().left + target.outerWidth() + nubHeight))
					.removeClass('tip-override');
					nub.removeClass('rtl');
				}
			}

			tip.css('visibility', 'visible').hide();
		},
		//sets the class names to use in the markup
		inheritable_classes : function ($target) {
			var inheritables = ['tip-top', 'tip-left', 'tip-bottom', 'tip-right', 'radius', 'round'],
				classes = $target.attr('class'),
				filtered = classes ? $.map(classes.split(' '), function (el, i) {
					if ($.inArray(el, inheritables) !== -1) {
					return el;
					}
				}).join(' ') : '';
			return $.trim(filtered);
		},

		show : function ($target) {
			var $tip = this.getTip($target);
			this.reposition($target, $tip, $target.attr('class'));
			$target.addClass('open');
			$tip.fadeIn(150);
		},

		hide : function ($target) {
			var $tip = this.getTip($target);
			$tip.fadeOut(150, function () {
				$target.removeClass('open');
			});
		},

		off : function () {
			var self = this;
			this.off('.' + namespace + '.tooltip');
			$(this.options.tooltip_class).each(function (i) {
				$('[' + self.attr_name() + ']').eq(i).attr('title', $(this).text());
			}).remove();
		},
	};

	$.fn.tooltip = function tooltip(option) {
		var el = this,
			options = $.extend({}, $.fn.tooltip.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'tooltip');
			if (!data) {
				$.data(this, 'tooltip', (data = new Tooltip(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.tooltip.defaults = {
		tooltip_class : '.tooltip',
		append_to : 'body',
		hover_delay : 200,
		show_on : 'all',
		tip_template : function (selector, content) {
			return '<span data-selector="' + selector + '" id="' + selector + '" class="' + 'tooltip' + '" role="tooltip" tabindex="-1">' + content + '<span class="nub"></span></span>';
		}
	};

	$.fn.tooltip.Constructor = Tooltip;

	$(function () {
		$('[data-tooltip]').on('focus', function(){
			$(this).trigger('mouseenter');
		});
		$('[data-tooltip]').on('blur', function(){
			$(this).trigger('mouseleave');
		});
		$('[data-tooltip]').tooltip();
	});

}(this, window.jQuery, "KP"));
