/* =========================================================
 /* =========================================================
 * kp.tabs.js
 * Created by KnowledgePath Solutions.
 * ==========================================================
 * Utility to control tabs behavior. Tabs are a set of stacked
 * divs revealed by cliking title boxes at top.
 * ========================================================= */

$(function(){
	(function (global, $, namespace) {

		"use strict";

		var Tabs = function Tabs(element, options) {
					this.init ('tabs', element, options);
				};

		Tabs.prototype = {
			constructor: Tabs,
			init : function init(type, element, options){
				var self = this,
						elements = {},
						$element, $trigger, $target;

				if (element instanceof jQuery) {
					$element  = element;
				} else {
					$element = $(element);
				}

				this.options = $.extend({}, $.fn[type].defaults, options);
				this.$element = $(element);
				this.$trigger = this.$element.find('.tab-title');
				this.$target = this.$element.find('.tab-body');

				this.$element.on('click', '.tab-title', function(){
					self.changeTab(this);
				});
			},

			changeTab: function (el){
				this.$trigger.removeClass('active');
				this.$target.removeClass('active');

				var t = $(el).attr('aria-controls');
				$(el).addClass('active');
				$('#' + t).addClass('active');
			}
		};

		$.fn.tabs = function tabs(option) {
			var el = this,
					options = $.extend({}, $.fn.tabs.defaults, typeof option === 'object' && option),
					args = Array.prototype.slice.call( arguments, 1 );
			return el.each(function () {
				var data = $.data(this, 'tabs');
				if (!data) {
					$.data(this, 'tabs', (data = new Tabs(this, options)));
				} else {
					if (typeof option === 'object') {
						$.extend(data.options, option);
					} else if (typeof option == 'string') {
						data[option].apply(data, args);
					}
				}
			});
		};

		$.fn.tabs.Constructor = Tabs;


		$(function () {
			$('[data-tabs]').tabs();
		});

	}(this, window.jQuery, "KP"));
});

