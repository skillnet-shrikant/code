/* =========================================================
 /* =========================================================
 * kp.promocode.js
 * Created by KnowledgePath Solutions
 * ==========================================================
 * Utility to control promo code behavior. The promo code widget
 * allows users to enter promo codes for item discounts applied
 * at checkout. When the user adds their code it will appear in
 * applied promo area under the form and give an option to view
 * details about the code in a modal.
 * You will have the ability to set how many promo codes users
 * are allowed to add in defaults.
 * ========================================================= */

(function (global, $, namespace) {
	// "use strict";

	var CONSTANTS = global[namespace].constants;
	var Promocode = function Promocode(element, options) {
				this.init ('promo-code', element, options);
			};

	function refreshTotalsPromo(data) {
		var updatedTotals,
				updatedAppliedPromos,
				currentCartItemCIDs = [];

		// loop through cart items and see if we need to delete any items
		$.each($('.order-item'), function(){
			currentCartItemCIDs.push($(this).data('ciid'));
		});

		// loop through cart items
		$.each(data.cartItems, function(index){
			var $orderItem = $('.order-item[data-ciid="' + this.commerceItemId + '"]');
			if ($orderItem.length > 0) {
				// item is on page, update item totals
				$orderItem.find('.item-total').html(this.itemSubtotal);
				$orderItem.find('.promo-line-item-msg').html(Mustache.render(global[namespace].templates.lineItemPromotions, data.cartItems[index]));

				// remove the item from the array of original items
				currentCartItemCIDs.pop(this.commerceItemId);
			}
			else {
				// item is new, refresh page to get new cart items
				window.location.reload();
			}
		});

		// if the original items array still has values, that means something was deleted.
		if (currentCartItemCIDs.length > 0) {
			window.location.reload();
		}

		// update the order totals
		updatedTotals = Mustache.render(global[namespace].templates.orderTotals, data);
		$('.totals').html(updatedTotals);

		// update gift card totals in checkout payment sections
		$.each(data.appliedGiftCards, function(){
			$('.amount-' + this.number).html(this.amount);
		});

		// remove all global promos from the data so we only show coupons in the coupon area
		if (typeof data.appliedCouponPromos !== 'undefined' && data.appliedCouponPromos.length > 0) {
			var newOrderDiscount = [];
			for (var j=0; j<data.appliedCouponPromos.length; j++) {
				if (data.appliedCouponPromos[j].discountType == 'coupon') {
					data.appliedCouponPromos[j].couponDetails = encodeURIComponent(data.appliedCouponPromos[j].couponDetails);
					newOrderDiscount.push(data.appliedCouponPromos[j]);
				}
			}
			data.appliedCouponPromos = newOrderDiscount;
			updatedAppliedPromos = Mustache.render(global[namespace].templates.appliedCoupons, data);
			$('.promo-applied-area').html(updatedAppliedPromos);
		}

		global[namespace].utilities.hideLoader();
	}

	Promocode.prototype = {
		constructor: Promocode,
		init : function init(type, element, options){
			var self = this;

			this.options = $.extend({}, $.fn.promocode.defaults, options);
			this.$element = $(element);

			// initialize apply promo listener
			self.applyPromo();

			// initialize remove promo listener
			self.removeApplied();

		},

		applyPromo : function(){
			var couponUpdateOptions = {
				type: 'post',
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					global[namespace].utilities.hideLoader();
					if (statusText == 'success') {
						var pageSource = "Shopping Cart";
						if (responseText.success == 'true') {
							$('#promo-code-field').val('').removeClass('error').blur();
							$('.promo-code-msg').empty();
							$('.promo-form-fields').addClass('hide');
							refreshTotalsPromo(responseText);
							$('.shipping-method-fields').load(CONSTANTS.contextPath + '/checkout/includes/shippingMethodAJAX.jsp');
							
							// Fire tag to log coupon apply status.
							pageSource = $("#fromCheckout").val() == 'true' ? "Checkout" : "Shopping Cart";
							KP.analytics.trackEvent(pageSource, pageSource, 'Coupon Apply', $(".promo-applied .coupon-code").text().trim());
						}
						else {
							$('#promo-code-field').addClass('error');
							global[namespace].utilities.form.showErrors($form, responseText, undefined, global[namespace].templates.errorPromoMessageTemplate);
							// Fire tag to log coupon error status.
							pageSource = $("#fromCheckout").val() == 'true' ? "Checkout" : "Shopping Cart";
							KP.analytics.trackEvent(pageSource, pageSource, 'Coupon Error', $("#promo-code-field").val());
						}
					}
					else {
						console.log('Malformed JSON : missing statusText parameter:');
						global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
					}
				},
				error: function(xhr, statusText, exception, $form) {
					console.log('AJAX Error:');
					global[namespace].utilities.hideLoader();
				//	global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
				}
			};
			$('#promo-form').ajaxForm(couponUpdateOptions);
		},
		removeApplied : function(){
			var couponRemoveOptions = {
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					global[namespace].utilities.hideLoader();
					if (statusText == 'success') {
						if (responseText.success == 'true') {
							refreshTotalsPromo(responseText);
							$('.shipping-method-fields').load(CONSTANTS.contextPath + '/checkout/includes/shippingMethodAJAX.jsp');
							$('.promo-applied-area').empty();
							$('.promo-form-fields').removeClass('hide');
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.showErrors($form, responseText);
						}
					}
					else {
						console.log('Malformed JSON : missing statusText parameter:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
					}
				},
				error: function(xhr, statusText, exception, $form) {
					console.log('AJAX Error:');
					global[namespace].utilities.hideLoader();
					global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
				}
			};
			$('#promo-remove-form').ajaxForm(couponRemoveOptions);

			$('body').on('click', '.remove-link', function() {
				$('#promo-remove-submit').trigger('click');
			});
		},
	};

	$.fn.promocode = function promocode(option) {
		var el = this,
				options = $.extend({}, $.fn.promocode.defaults, typeof option === 'object' && option),
				args = Array.prototype.slice.call( arguments, 1 );
		return el.each(function () {
			var data = $.data(this, 'promocode');
			if (!data) {
				$.data(this, 'promocode', (data = new Promocode(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				} else if (typeof option == 'string') {
					data[option].apply(data, args);
				}
			}
		});
	};

	$.fn.promocode.Constructor = Promocode;

	$(function () {
		$('[data-promocode]').promocode();
	});

}(this, window.jQuery, "KP"));
