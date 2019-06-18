/* =========================================================
 * kp.accordion.js
 * =========================================================
 * Accordion behavior for a series of panels with option to allow
 * multiple open at a time. In order to handle nested accordions, markup is strict.
 * <div data-accordion class="accordion">
 *   <div class="accordion-container">
 *     <div class="accordion-title" role="tab" aria-controls="panel1" id="tab1">Section 1></div>
 *     <div class="accordion-body" aria-labelledby="tab1" role="tabpanel" id="panel1">
 *       <div class="accordion-body-content">
 *         <p>Content here</p>
 *       </div>
 *     </div>
 *   </div>
 *   <div class="accordion-container">
 *     <div class="accordion-title" role="tab" aria-controls="panel2" id="tab2">Section 2</div>
 *     <div class="accordion-body" aria-labelledby="tab2" role="tabpanel" id="panel2">
 *       <div class="accordion-body-content">
 *         <p>Content Here</p>
 *       </div>
 *     </div>
 *   </div>
 * </div>
 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

	var Accordion = function Accordion(element, options) {
				this.init ('accordion', element, options);
			},
			CONSTANTS = global[namespace].constants,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			utilities = global[namespace].utilities;

	//private
	/* given an element, finds the accordion tab and panel. Accepts the accordion container, the accordion trigger or
	 the accordion body (panel). Element can be jquery or a dom element. */
	function getAccordionElements(accordion, element) {
		var elements = {},
				$element, $trigger, $target;

		if (element instanceof jQuery) {
			$element  = element;
		} else {
			$element = $(element);
		}

		if ($element.hasClass(accordion.options.trigger_class_name)) {
			$trigger = $element;
			$target = $('#' + $element.attr('aria-controls'));
		} else if ($element.hasClass(accordion.options.content_class_name)) {
			$target = $element;
			$trigger = $('#' + $element.attr('aria-labelledby'));
		} else if ($element.hasClass(accordion.options.accordion_class_name)) {

			/* Instead of enforcing a strict child relationship. The trigger and content just need to be
			 descendants of this element without being descendants of another accordion container. */
			$trigger = accordion.$triggers.filter(function(index) {
				return $(this).closest('.' + accordion.options.accordion_class_name).is($element);
			});
			$target =  accordion.$panels.filter(function(index) {
				return $(this).closest('.' + accordion.options.accordion_class_name).is($element);
			});
		}
		elements['trigger'] = $trigger;
		elements['target'] = $target;
		return elements;
	}

	function toggleAccordion (accordion, $trigger, $target){
		if ($trigger.hasClass('active')) {
			closeAccordion(accordion, $trigger, $target);
		} else {
			openAccordion(accordion, $trigger, $target);
		}
	}

	function openAccordion(accordion, $trigger, $target){
		if ($trigger.hasClass('active')) {
			return;
		}
		$trigger.addClass('active').attr('aria-expanded', true);
		$target.slideDown().attr('aria-expanded', true);
		$trigger.trigger('open.accordion');
		if (!accordion.options.allow_multi_expand) {
			closeAccordion(accordion, accordion.$triggers.not($trigger), accordion.$panels.not($target));
		}
	}

	function closeAccordion (accordion, $trigger, $target){
		if (!$trigger.hasClass('active')) {
			return;
		}
		$trigger.removeClass('active').attr('aria-expanded', false);
		$target.slideUp().attr('aria-expanded', false);
		$target.trigger('close.accordion');
	}


	//PUBLIC
	Accordion.prototype = {
		constructor: Accordion,
		init: function init(type, element, options) {
			var self = this,
					multiselectable;
			if (loggingDebug) {
				console.debug('init accordion with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);

			/* Instead of enforcing a strict child relationship. The accordion container and trigger just need to be
			 descendants of the accordion without being descendants of another accordion. */
			this.$accordions = this.$element.find('.' + self.options.accordion_class_name).filter(function(index) {
				return $(this).closest('.' + self.options.accordion_group_class_name).is(self.$element);
			});
			this.$triggers = this.$element.find('.' + self.options.trigger_class_name).filter(function(index) {
				return $(this).closest('.' + self.options.accordion_group_class_name).is(self.$element);
			});
			this.$panels =  this.$element.find('.' + self.options.content_class_name).filter(function(index) {
				return $(this).closest('.' + self.options.accordion_group_class_name).is(self.$element);
			});

			multiselectable = this.$element.attr('aria-multiselectable');
			if (multiselectable !== undefined) {
				this.options.allow_multi_expand = (multiselectable == 'true');
			}

			this.$triggers.on('click', function(e) {
				var $this = $(this);

				// if accordion is in off-canvas nav, make sure to only open when it has a submenu (not a link)
				if ($this.parents('.accordion').hasClass('off-canvas-list')) {
					if ($this.parent('.accordion-container').hasClass('has-submenu')) {
						e.preventDefault();
						self.toggle(this);
					}
				}
				else {
					e.preventDefault();
					self.toggle(this);
				}
			});
		},
		toggle : function toggle(element) {
			var els = getAccordionElements(this, element);
			if (els.trigger !== undefined && els.target !== undefined) {
				toggleAccordion(this, els.trigger, els.target);
			}
		},
		open : function open(element) {
			var els = getAccordionElements(this, element);
			if (els.trigger !== undefined && els.target !== undefined) {
				openAccordion(this, els.trigger, els.target);
			}
		},
		openAll : function closeAll() {
			var $accordion = this.$element;
			this.$triggers.addClass('active').attr('aria-expanded', true);
			this.$panels.slideDown(function(){
				if ($accordion.parents('.modal').length > 0) {
					$('.modal').modal('reposition');
				}
			}).attr('aria-expanded', true);

		},
		close : function close(element) {
			var els = getAccordionElements(this, element);
			if (els.trigger !== undefined && els.target !== undefined) {
				closeAccordion(this, els.trigger, els.target);
			}
		},
		closeAll : function closeAll() {
			this.$triggers.removeClass('active').attr('aria-expanded', false);
			this.$panels.slideUp().attr('aria-expanded', false);
		}
	};

	$.fn.accordion = function accordion(option) {
		var el = this,
				options = $.extend({}, $.fn.accordion.defaults, typeof option === 'object' && option),
				args = Array.prototype.slice.call( arguments, 1 );
		return el.each(function () {
			var data = $.data(this, 'accordion');
			if (!data) {
				$.data(this, 'accordion', (data = new Accordion(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				} else if (typeof option == 'string') {
					data[option].apply(data, args);
				}
			}
		});
	};

	$.fn.accordion.defaults = {
		allow_multi_expand: true,
		trigger_class_name: 'accordion-title',
		content_class_name: 'accordion-body',
		accordion_class_name: 'accordion-container',
		accordion_group_class_name: 'accordion'
	};

	$.fn.accordion.Constructor = Accordion;


	$(function () {
		$('[data-accordion]').accordion();
	});


}(this, window.jQuery, "KP"));


