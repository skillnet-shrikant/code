/* =========================================================
 * kp.quantify.js
 * =========================================================

 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

	var Quantify = function Quantify(element, options) {
				this.init ('quantify', element, options);
			},
			CONSTANTS = global[namespace].constants,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug;

	function updateStates(quantify, val) {
		quantify.$minus.removeClass('inactive').attr('tabindex', '0').attr('aria-disabled', false);
		quantify.$plus.removeClass('inactive').attr('tabindex', '0').attr('aria-disabled', false);
		if (val <= quantify.options.min_quantity){
			quantify.$minus.addClass('inactive').attr('tabindex', '-1').attr('aria-disabled', true);
		}
		if (val >= quantify.options.max_quantity){
			quantify.$plus.addClass('inactive').attr('tabindex', '-1').attr('aria-disabled', true);
		}
	}

	//PUBLIC
	Quantify.prototype = {
		constructor: Quantify,
		init: function init(type, element, options) {
			if (loggingDebug) {
				console.debug('init quantify with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}

			var self = this,
					minQuantity,
					maxQuantity,
					dataFree;

			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);
			this.$plus = this.$element.find('.plus-icon');
			this.$minus = this.$element.find('.minus-icon');
			this.$counter = this.$element.find('.counter');
			this.$totalQty = this.$element.find('#totalQuantity');
			this.$freeQty = this.$element.find('#freeQuantity');

			maxQuantity = this.$element.attr('data-max');
			if (typeof maxQuantity != 'undefined') {
				this.options.max_quantity = parseInt(maxQuantity);
			}
			
			// 2414 Adding this to handle free gift quantity
			// separate from manually added skus of the gift
			dataFree = this.$element.attr('data-free');
			if (typeof dataFree != 'undefined') {
				this.options.data_free = parseInt(dataFree);
			}
			
			minQuantity = this.$element.attr('data-min');
			if (typeof minQuantity != 'undefined') {
				this.options.min_quantity = parseInt(minQuantity);
			}

			if (isNaN(parseInt(self.$counter.val()))) {
				self.$counter.val(self.options.min_quantity);
			}

			this.$element.on('click', '.plus-icon', function(){
				self.increment();
			});

			this.$element.on('click', '.minus-icon', function(){
				if (isNaN(parseInt(self.$counter.val()))) {
					self.$counter.val(self.options.min_quantity);
					return;
				}
				self.decrement();
			});

			//prevent non numbers
			this.$counter.keypress(function(e) {
				var key_codes = [48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 0, 8];

				if ($.inArray(e.which, key_codes) == -1) {
					e.preventDefault();
				}
			});

			this.$counter.keyup(function(e) {
				var currentValue = parseInt(self.$counter.val());
				var totalQty = currentValue;
				if(parseInt(self.$freeQty.val()) > 0) {
					totalQty = currentValue + parseInt(self.$freeQty.val());
				}

				if (currentValue > self.options.max_quantity) {
					//no!
					self.$counter.val(self.options.max_quantity).change();
					totalQty = self.options.max_quantity - parseInt(self.options.data_free);
				}

				if (currentValue < self.options.min_quantity) {
					//no!
					self.$counter.val(self.options.min_quantity).change();
					totalQty = self.options.min_quantity + parseInt(self.options.data_free);
				}

				if (isNaN(currentValue)) {
					//no!
					self.$counter.val(self.options.min_quantity).change();
					totalQty = self.options.min_quantity + parseInt(self.options.data_free);
				}
				self.$totalQty.val(totalQty);
				updateStates(self, currentValue);
			}).keyup();

		},

		increment : function (){
			if (isNaN(this.$counter.val()) || this.$counter.val() === '') {
				this.$counter.val(0);
			}
			//
			var newQuantity =  parseInt(this.$counter.val())  + 1;
			var totalQty = parseInt(this.$totalQty.val()) + 1;
			//var totalQty = newQuantity + parseInt(this.options.data_free);
			if (newQuantity > this.options.max_quantity){
				return;
			}
			this.$counter.val(newQuantity).change();
			this.$totalQty.val(totalQty);
			updateStates(this, newQuantity);
			this.$element.trigger('increment');
		},
		decrement : function (){
			if (isNaN(this.$counter.val()) || this.$counter.val() === '') {
				this.$counter.val(0);
			}
			// + parseInt(this.options.data_free)
			var newQuantity =  parseInt(this.$counter.val())  - 1;
			//var totalQty = newQuantity + parseInt(this.options.data_free);
			var totalQty = parseInt(this.$totalQty.val()) - 1;
			
			if (newQuantity < this.options.min_quantity){
				return;
			}
			this.$counter.val(newQuantity).change();
			this.$totalQty.val(totalQty);
			updateStates(this, newQuantity);
			this.$element.trigger('decrement');
		}
	};

	$.fn.quantify = function quantify(option) {
		var el = this,
				options = $.extend({}, $.fn.quantify.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'quantify');
			if (!data) {
				$.data(this, 'quantify', (data = new Quantify(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.quantify.defaults = {
		max_quantity: 99,
		min_quantity: 0,
		data_free: 0
	};

	$.fn.quantify.Constructor = Quantify;

	$(function () {
		$('[data-quantify]').quantify();
	});

}(this, window.jQuery, "KP"));
