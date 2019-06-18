/*!
 * Checkout Init
 */
(function (global, $, namespace) {
	//"use strict";

	var CONSTANTS = global[namespace].constants,
			TEMPLATES = global[namespace].templates,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			$body = $('body');

	var checkout = {
		init : function () {},
		login : function() {
			var profileOptions = {
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					if (statusText == 'success') {
						if (responseText.success == 'true') {
							// reset profile cookie on login
							global[namespace].profileController.resetProfileStatus();
							window.location = responseText.url;
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
			$('#login-form').ajaxForm(profileOptions);
		},
		cart : function() {

			// enable buttons
			$('.change-store, .ship-my-order, .auto-remove-item-trigger').removeClass('disabled');

			// LTL / Oversize / Additional Shipping / Long and Light Modal
			if ($('#is-bopis-order').val() !== 'true') {
				var signatureRequired = $('#signatureRequired').val(),
						longLight = $('#longLight').val(),
						isOversize = $('#isOversize').val(),
						isLTLOrder = $('#isLTLOrder').val(),
						totalLTLWeight = $('#totalLTLWeight').val(),
						rangeLow = $('#rangeLow').val(),
						rangeHigh = $('#rangeHigh').val(),
						ltlShippingCharges = $('#ltlShippingCharges').val(),
						hasSurcharge = $('#hasSurcharge').val(),
						totalSurcharge = $('#totalSurcharge').val();

				// 2427 - Do not pop-up modal if ltlOrder but no surcharge because of free freight shipping
				if ((typeof signatureRequired !== 'undefined' && signatureRequired !== '0') || (typeof longLight !== 'undefined' && longLight !== '0') || (typeof isOversize !== 'undefined' && isOversize !== '0') || (typeof isLTLOrder !== 'undefined' && isLTLOrder !== 'false'  && typeof ltlShippingCharges !== 'undefined' && ltlShippingCharges != '0.0') || (typeof hasSurcharge !== 'undefined' && hasSurcharge !== 'false')) {
					var $modalTarget = document.getElementById('additional-shipping-modal') ? $('#additional-shipping-modal') : global[namespace].utilities.createModal('additional-shipping-modal', 'small');
					$modalTarget.modal({'url': CONSTANTS.contextPath + '/checkout/ajax/additionalShippingModal.jsp?signatureRequired=' + signatureRequired + '&longLight=' + longLight + '&isOversize=' + isOversize + '&isLTLOrder=' + isLTLOrder + '&totalLTLWeight=' + totalLTLWeight + '&rangeLow=' + rangeLow + '&rangeHigh=' + rangeHigh + '&ltlShippingCharges=' + ltlShippingCharges + '&hasSurcharge=' + hasSurcharge + '&totalSurcharge=' + totalSurcharge});
				}
			}
			else {
				// show change store modal
				if (global[namespace].utilities.getURLParameter(window.location.href, 'changeStore') == 'true') {
					var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
					$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisModal.jsp'});
				}
			}

			// cart item form handlers
			var updateCartItemOptions = {
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					$('.modal').modal('hide');
					global[namespace].utilities.form.hideErrors($('.error-container'));
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					if (statusText == 'success') {
						if (responseText.success == 'true') {

							// hide ajax loader
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.hideErrors($form);

							// update the cart qty on page and in cookie
							global[namespace].profileController.getProfileStatus(true);
							
							//analytics for remove item from cart
							if($form.attr('id') == "cartRemoveForm"){
								var productId = "",
									ciId = "";
								$('.order-item').each(function(){
										ciId = $(this).attr('data-ciid'),
											matched = false;
									// loop thru cart items in json - look for match in cart
									for (var i=0; i<responseText.cartItems.length; i++) {
										if (ciId == responseText.cartItems[i].commerceItemId) {
											matched = true;
											break;
										}
									}
									// if not matched, remove
									if (!matched) {
										var $orderItem = $('.order-item[data-ciid="' + ciId + '"]');
										productId = $orderItem.attr('data-prodId');
									}
								});
															
								if(KP.analytics){
									if(productId!='')
									KP.analytics.sendRemoveProduct(productId);
								}
							}

							// update sidecart
							$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp');

							if (responseText.cartCount > 0) {

								// loop through cart items
								var currentCartItemCIDs = [];
								$.each(responseText.cartItems, function(index){
									var $orderItem = $('.order-item[data-ciid="' + this.commerceItemId + '"]');
									// Changes related to 2414. See cartItem.jspf for details
									var $freeItem = $('.order-item[data-ciid="' + this.commerceItemId + '-FREE"]');

									if ($orderItem.length > 0) {
										
										// item is on page, update item totals
										//console.log("Currently processing item " + this.commerceItemId);
										$orderItem.find('.promo-line-item-msg').html(Mustache.render(TEMPLATES.lineItemPromotions, responseText.cartItems[index]));
										$orderItem.find('#quantityUpdate').val(this.itemQuantity);
										$orderItem.find('#totalQuantity').val(this.totalItemQuantity);
										$orderItem.find('.item-total').html(this.totalLinePrice);
										if(this.freeGift == 'true') {
											//console.log(this.commerceItemId + " is a gift");
											$orderItem.find('.item-quantity').addClass("hide"); 
											$orderItem.find('.item-price-unit').addClass("hide"); 
											//$orderItem.find('.item-total').removeClass("item-total"); 
											$orderItem.find('.item-actions').addClass("hide");
											$orderItem.find('.item-total').addClass("free");
											$orderItem.find('.change-quantity').attr('data-free',this.itemQuantity);
											
										} else {
											//console.log("current data free is " + $orderItem.find('.change-quantity').attr('data-free'));
											//console.log("Set data free to " + $freeItem.find('.change-quantity').attr('data-free'));
											//console.log(this.commerceItemId + " is NOT a gift");
											//console.log("Before update " + $orderItem.find('.change-quantity').attr('data-free'));
											//console.log("Setting to " + $freeItem.find('.change-quantity').attr('data-free'));
											$orderItem.find('.change-quantity').attr('data-free',$freeItem.find('.change-quantity').attr('data-free'));
											//console.log("After update " + $orderItem.find('.change-quantity').attr('data-free'));
											$orderItem.find('#freeQuantity').val($freeItem.find('.change-quantity').attr('data-free'));
											$orderItem.find('.item-quantity').removeClass("hide");
											$orderItem.find('.item-price-unit').removeClass("hide"); 
											$orderItem.find('.item-actions').removeClass("hide");
											$orderItem.find('.item-total').removeClass("free");
										}

										// remove the item from the array of original items
										currentCartItemCIDs.pop(this.commerceItemId);
									} else if ($freeItem.length > 0){
										$freeItem.find('.item-total').html(this.totalLinePrice);
										$freeItem.find('#quantityUpdate').val(this.itemQuantity);										
									}
									else {
										// item is new, refresh page to get new cart items
										window.location.reload();
									}
								});

								// check if cart item was removed (clicked - button and now qty is 0)
								$('.order-item').each(function(){
									var ciid = $(this).attr('data-ciid'),
											matched = false;
									// loop thru cart items in json - look for match in cart
									for (var i=0; i<responseText.cartItems.length; i++) {
										if (ciid == responseText.cartItems[i].commerceItemId) {
											$(this).removeAttr( 'style' );
											matched = true;
											break;
										}
									}
									// if not matched, remove
									if (!matched) {
										$('.order-item[data-ciid="' + ciid + '"]').slideUp(200);
									}
								});

								// update order totals
								$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

								// if cart is bopis only, remove "ship my order instead" link
								if (responseText.bopisOnly == 'true') {
									$('.auto-remove-item-trigger').remove();
									// if it's a mixed bopisOnly/non-bopisOnly cart, you should never have the ship-my-order link
									// $('.ship-my-order').remove();
								}

								// // update promotions
								// var updatedAppliedPromos = Mustache.render(TEMPLATES.appliedPromotionsCart, responseText);
								// $('#promo-applied-area').html(updatedAppliedPromos);
								// if (responseText.couponMessages.length > 0) {
								// 	$('#apply-promo').after(Mustache.render(TEMPLATES.couponMessageTemplate, responseText));
								// 	$('#cartPromoForm').show();
								// }

								// LTL / Oversize / Additional Shipping / Long and Light Modal
								if (responseText.isBopisOrder !== 'true') {
									var signatureRequired = responseText.signatureRequired,
											longLight = responseText.longLight,
											isOversize = responseText.isOversize,
											isLTLOrder = responseText.isLTLOrder,
											totalLTLWeight = responseText.totalLTLWeight,
											rangeLow = responseText.rangeLow,
											rangeHigh = responseText.rangeHigh,
											ltlShippingCharges = responseText.ltlShippingCharges,
											hasSurcharge = responseText.hasSurcharge,
											totalSurcharge = responseText.totalSurcharge,
											$surcharge = $('#totalSurcharge'),
											currentSurcharge = $surcharge.val();

									if (totalSurcharge !== currentSurcharge) {
										$surcharge.val(totalSurcharge);
									}
									else {
										hasSurcharge = 'false';
									}

									// 2427 - Do not pop-up modal if ltlOrder but no surcharge because of free freight shipping
									if ((typeof signatureRequired !== 'undefined' && signatureRequired !== '0') || (typeof longLight !== 'undefined' && longLight !== '0') || (typeof isOversize !== 'undefined' && isOversize !== '0') || (typeof isLTLOrder !== 'undefined' && isLTLOrder !== 'false' && typeof ltlShippingCharges !== 'undefined' && ltlShippingCharges != '0.0') || (typeof hasSurcharge !== 'undefined' && hasSurcharge !== 'false')) {
										var $modalTarget = document.getElementById('additional-shipping-modal') ? $('#additional-shipping-modal') : global[namespace].utilities.createModal('additional-shipping-modal', 'small');
										$modalTarget.modal({'url': CONSTANTS.contextPath + '/checkout/ajax/additionalShippingModal.jsp?signatureRequired=' + signatureRequired + '&longLight=' + longLight + '&isOversize=' + isOversize + '&isLTLOrder=' + isLTLOrder + '&totalLTLWeight=' + totalLTLWeight + '&rangeLow=' + rangeLow + '&rangeHigh=' + rangeHigh + '&ltlShippingCharges=' + ltlShippingCharges + '&hasSurcharge=' + hasSurcharge + '&totalSurcharge=' + totalSurcharge});
									}
								}

							}
							else {
								// cart is empty, show appropriate message
								$('.cart-content').load(CONSTANTS.contextPath + '/checkout/fragments/cartEmpty.jspf');
							}

							// if move wish list, show modal
							if (responseText.isWishList) {
								var $modalTarget = document.getElementById('wish-list-confirmation-modal') ? $('#wish-list-confirmation-modal') : global[namespace].utilities.createModal('wish-list-confirmation-modal', 'medium');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/addToWishListConfirmation.jsp?productId=' + responseText.productId + '&skuId=' + responseText.skuId});
							}
							
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.showErrors($form, responseText);
							// BZ 2523 - When item qty fails qty validation checks
							// the UI should display the prior qty. It is retaining the qty that failed the validation
							for(var i=0; i < responseText.quantities.length; i++) {
								$('input[name="' + responseText.quantities[i].itemId + '"]').val(responseText.quantities[i].qty);
							}
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

			// remove item from cart
			$body.on('click', '.item-remove', function(e){
				var ciId = $(this).data('ciid').trim();
				var $orderItem = $('.order-item[data-ciid="' + ciId + '"]');
				
				// 2414 - when the free SKU is also added directly by user
				// it is still one commerce item in the order but displayed
				// in two lines on the cart UI. "Remove" removes the entire item
				// including the gift. This is not desired.
				// we send removalCommerceIds in the format id#qty to remove just the user added qty
				var userAddedQty = $orderItem.find('#quantityUpdate').val();
				$('#removeCommerceIds').val(ciId+"#"+userAddedQty);
				$('#cartRemoveForm').ajaxSubmit(updateCartItemOptions);
			});

			// move item to wish list
			$body.on('click', '.item-save', function(e){
				var ciId = $(this).data('ciid').trim();
				$('#moveItemId').val(ciId);
				$('#cartItemMoveToWishList').ajaxSubmit(updateCartItemOptions);
			});

			// hides/shows update link when using keyboard to update the quantity
			$('.counter').keyup(function(event){
				var counterObj = $(this),
						keycode = (event.keyCode ? event.keyCode : event.which),
						updateId = '#updateQty-' + this.getAttribute('name');
						// related to 2414
						updateId = updateId.replace('-display','');

				if ((keycode >= 48 && keycode <= 57) || keycode == 8 || keycode == 46) {
					// 48-57 - numbers
					// 8 - backspace
					// 46 - delete
					// 32 - spacebar
					if (counterObj.val() !== '') {
						$(updateId).fadeIn('fast').parents('.order-item').addClass('update-showing');
					}
					else {
						$(updateId).hide();
					}
				}
				else if (keycode == 13) {
					// 13 - enter
					$(updateId).find('a').click();
				}
				else {
					if (keycode !== 32) {
						$(updateId).hide();
					}
				}
			});

			// update item quantity in cart
			$('#cartUpdateForm').on('click', '.updateCartQty', function(e){
				e.preventDefault();
				$('#cartUpdateForm').ajaxSubmit(updateCartItemOptions);
				$('.update-qty').hide();
				$('.order-item').removeClass('update-showing');
			}).on('increment decrement', function(){
				$('#cartUpdateForm').ajaxSubmit(updateCartItemOptions);
				$('.update-qty').hide()
				$('.order-item').removeClass('update-showing');
			});

			// apply tax exemption
			var taxExemptionOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.form.hideErrors($form);
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();
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
			$('#tax-exemption-select-form').ajaxForm(taxExemptionOptions);

			$('#tax-exemption').on('change', function(e){
				$('.apply-tax-exemption').click();
			});

		},
		checkout : function() {

			// sticky checkout progress bar
			var $progressBar = $('.checkout-progress'),
					$window = $(window);

			if ($window.width() < global[namespace].config.mediumMin) {
				$window.scroll($.throttle(200, function(e){
					if ($window.scrollTop() > 100) {
						$progressBar.addClass("fixed-progress");
					} else {
						$progressBar.removeClass("fixed-progress");
					}
				}));
			}
			else {
				$progressBar.removeClass("fixed-progress");
			}

			// mask phone numbers
			$('#phone').mask('000-000-0000');
			$('#billing-phone').mask('000-000-0000');

			// enable buttons
			$('.ship-my-order, .bopis-continue, .shipping-address-continue, .shipping-method-continue, .payment-continue, .place-order-btn').removeClass('disabled');

			// shipping
			var $newShippingAddress = $('.new-shipping-address'),
					$addressName = $('.address-name');
			$('input[name="shipping-address"]').click(function(){
				if (document.getElementById('shipping-address-new').checked) {
					$newShippingAddress.slideDown(250);
				}
				else {
					document.getElementById('save-shipping-address').checked = false;
					$addressName.slideUp(250);
					$newShippingAddress.slideUp(250);
				}
			});
			$('#save-shipping-address').click(function(){
				if (this.checked) {
					$addressName.slideDown(250);
				}
				else {
					$addressName.slideUp(250);
				}
			});

			// saturday delivery
			$body.on('click', 'input[name="shipping"]', function(){
				var shippingMethod = this.value,
						$saturdayDelivery = $('.saturday-delivery');

				document.getElementById('saturday-delivery').checked = false;
				$.ajax({
					url: CONSTANTS.contextPath + '/checkout/json/saturdayDeliveryJson.jsp',
					data: {shippingMethod: shippingMethod},
					cache: false,
					beforeSend: function(){
						global[namespace].utilities.showLoader();
					},
					success: function(data){
						if (data.isSatDayDelivery == 'true') {
							$saturdayDelivery.removeClass('hide');
						}
						else {
							$saturdayDelivery.addClass('hide');
						}
						global[namespace].utilities.hideLoader();
					},
					error: function(data){
						global[namespace].utilities.hideLoader();
					}
				});
			});

			var $checkoutLeftColumn = $('.checkout-left-column'),
				shippingAddressOptions = {
					dataType: 'json',
					beforeSerialize : function($form) {
						$form.find('#phone').unmask();
					},
					beforeSubmit: function (arr, $form, options) {
						$checkoutLeftColumn.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');

						if (statusText == 'success') {
							if (responseText.success == 'true') {
								if (responseText.addressMatched == 'true') {
									// avs passed

									// update order totals
									$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

									// update gift card totals in checkout payment sections
									$.each(responseText.appliedGiftCards, function(){
										$('.amount-' + this.number).html(this.amount);
									});

									// update review panels
									if (responseText.fflOrder == 'true'){
										// update ffl dealer review panel
										$('.shipping-address-review').load(CONSTANTS.contextPath + '/checkout/includes/fflDealerInfoReview.jsp');
									}
									else {
										// update shipping address review panel
										$('.shipping-address-review').load(CONSTANTS.contextPath + '/checkout/includes/shippingAddressReview.jsp');
										//$('.shipping-address-review').load(CONSTANTS.contextPath + '/checkout/includes/fflDealerInfoReview.jsp');
									}
									// show edit shipping address link
									$('.edit-shipping-address').show(0);

									// update shipping methods
									$('.shipping-method-fields').load(CONSTANTS.contextPath + '/checkout/includes/shippingMethodAJAX.jsp .shipping-method-radios', function(){
										// move on to shipping method step
										$('.shipping-address').slideUp(250, function(){
											$('.shipping-address-review-panel').slideDown(250, function(){
												$('.shipping-method').slideDown(250, function(){
													global[namespace].utilities.hideLoader();
													$body.scrollTop($('.shipping-method').offset().top);
												});
											});
										});
									});
									if (KP.analytics) {
										if(digitalData)
											digitalData.checkoutStep = 2;
										KP.analytics.sendCheckoutOption("shipping Method");
									}
									
								}
								else {
									// avs failed
									if (typeof avsJSON !== 'undefined') {
										avsJSON = JSON.stringify(responseText);
									}
									else {
										$body.append('<script>var avsJSON = \'' + JSON.stringify(responseText) + '\';</script>');
									}
									global[namespace].utilities.hideLoader();
									var $modalTarget = document.getElementById('avsModal') ? $('#avsModal') : global[namespace].utilities.createModal('avsModal', 'medium'),
										url = CONSTANTS.contextPath + '/checkout/ajax/avsModal.jsp',
										option = {'url': url};
									$modalTarget.modal(option);
								}
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
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				},
				shippingMethodOptions = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$checkoutLeftColumn.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();

								// update shipping method in review panel
								$('.shipping-review-panel .shipping-method-review').load(CONSTANTS.contextPath + '/checkout/includes/shippingMethodReview.jsp');

								// update order totals
								$('#order-items-container').load(CONSTANTS.contextPath + '/checkout/includes/cartItems.jsp');
								$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

								// update gift card totals in checkout payment sections
								$.each(responseText.appliedGiftCards, function(){
									$('.amount-' + this.number).html(this.amount);
								});

								// hide credit card form if order is covered by gift cards
								if (responseText.orderCovered == 'true') {
									$('.payment-method-section').slideUp(250);
									$('.gift-card-info').slideUp(250);
									$('.credit-card-review').slideUp(250);
									$('.billing-address-review').slideUp(250);
								}
								else {
									$('.payment-method-section').slideDown(250);
									$('.gift-card-info').slideDown(250);
									$('.credit-card-review').slideDown(250);
									$('.billing-address-review').slideDown(250);
								}

								// mark step as complete
								$('.checkout-shipping').addClass('complete');
								$('.checkout-progress-shipping').removeClass('in-progress').addClass('complete');

								// close shipping form and show review panel
								$('.checkout-shipping').slideUp(250, function(){

									// open next incomplete step
									if ($('.checkout-payment').hasClass('complete') && responseText.orderCovered == 'true') {
										// open review because payment is complete
										$('.checkout-review').slideDown(250, function(){
											$body.scrollTop($('#checkout-review').offset().top);
											$('.checkout-payment').addClass('complete');
											$('.checkout-review').addClass('in-progress');
											$('.checkout-progress-payment').addClass('complete');
											$('.checkout-progress-review').addClass('in-progress');
											$('.promo-code-container').slideUp(250);
										});
										if (KP.analytics) {
											if(digitalData)
												digitalData.checkoutStep = 4;
											KP.analytics.sendCheckoutOption("review");
										}
									}
									else {
										$('.checkout-payment').slideDown(250, function(){
											$body.scrollTop($('.checkout-payment').offset().top);
											$('.checkout-payment').addClass('in-progress');
											$('.checkout-progress-payment').addClass('in-progress');
										});
										if (KP.analytics) {
											if(digitalData)
												digitalData.checkoutStep = 3;
											KP.analytics.sendCheckoutOption("payment");
										}
									}
								});
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
			$('#shipping-address-form').ajaxForm(shippingAddressOptions);
			$('#shipping-method-form').ajaxForm(shippingMethodOptions);

			// listener for edit shipping address while in shipping step
			$body.on('click', '.edit-shipping-address', function(e){
				e.preventDefault();

				// hide edit shipping address link
				$('.edit-shipping-address').hide(0);

				// reset avs flag
				$('#skip-avs').val(false);

				// show shipping address form
				$('.shipping-method').slideUp(250, function(){
					$('.checkout-shipping .shipping-address-review-panel').slideUp(250, function(){
						$('.shipping-address').slideDown(250, function(){
							$('#phone').mask('000-000-0000');
							$body.scrollTop($('.shipping-address').offset().top);
						});
					});
				});
			});

			// BOPIS
			var bopisPersonOptions = {
					dataType: 'json',
					beforeSerialize : function($form) {
						if ($('#pick-up-other').length > 0) {
							if (document.getElementById('pick-up-other').checked) {
								$('#bopis-name').val($('#bopis-name-other').val());
								$('#bopis-email').val($('#bopis-email-other').val());
							}
							else {
								$('#bopis-name').val($('#bopis-name-me').val());
								$('#bopis-email').val($('#bopis-email-me').val());
							}
						}
					},
					beforeSubmit: function (arr, $form, options) {
						$checkoutLeftColumn.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();

								// update shipping address review panel
								$('.bopis-person-review').load(CONSTANTS.contextPath + '/checkout/includes/bopisPersonReview.jsp');

								// update order totals
								$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

								// update gift card totals in checkout payment sections
								$.each(responseText.appliedGiftCards, function(){
									$('.amount-' + this.number).html(this.amount);
								});

								// mark step as complete
								$('.checkout-shipping').addClass('complete');
								$('.checkout-progress-shipping').removeClass('in-progress').addClass('complete');

								// close shipping form and show review panel
								$('.checkout-shipping').slideUp(250, function(){

									// open next incomplete step
									if ($('.checkout-payment').hasClass('complete')) {
										// open review because payment is complete
										$('.checkout-review').slideDown(250, function(){
											$body.scrollTop($('#checkout-review').offset().top);
											$('.checkout-payment').addClass('complete');
											$('.checkout-review').addClass('in-progress');
											$('.checkout-progress-payment').addClass('complete');
											$('.checkout-progress-review').addClass('in-progress');
											$('.promo-code-container').slideUp(250);
										});
										if (KP.analytics) {
											if(digitalData)
												digitalData.checkoutStep = 4;
											KP.analytics.sendCheckoutOption("review");
										}
									}
									else {
										$('.checkout-payment').slideDown(250, function(){
											$body.scrollTop($('.checkout-payment').offset().top);
											$('.checkout-payment').addClass('in-progress');
											$('.checkout-progress-payment').addClass('in-progress');
										});
										if (KP.analytics) {
											if(digitalData)
												digitalData.checkoutStep = 3;
											KP.analytics.sendCheckoutOption("payment");
										}
									}
								});
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
			$('#bopis-person-form').ajaxForm(bopisPersonOptions);

			// BOPIS
			var $pickUpMeForm = $('.pick-up-me-form'),
					$pickUpOtherForm = $('.pick-up-other-form');
			$('input[name="pick-up-person"]').click(function(){
				if (document.getElementById('pick-up-other').checked) {
					$pickUpMeForm.slideUp(250, function(){
						$pickUpOtherForm.slideDown(250);
					});
				}
				else {
					$pickUpOtherForm.slideUp(250, function(){
						$pickUpMeForm.slideDown(250);
					});
				}
			});

			$body.on('click', '.shipping-continue', function(e){

				// mark step as complete
				$('.checkout-shipping').addClass('complete');
				$('.checkout-progress-shipping').removeClass('in-progress').addClass('complete');

				// close shipping form and show review panel
				$('.checkout-shipping').slideUp(250, function(){

					// open next incomplete step
					if ($('.checkout-payment').hasClass('complete') && responseText.orderCovered == 'true') {
						// open review because payment is complete
						$('.checkout-review').slideDown(250, function(){
							$body.scrollTop($('#checkout-review').offset().top);
							$('.checkout-payment').addClass('complete');
							$('.checkout-review').addClass('in-progress');
							$('.checkout-progress-payment').addClass('complete');
							$('.checkout-progress-review').addClass('in-progress');
							$('.promo-code-container').slideUp(250);
						});
					}
					else {
						$('.checkout-payment').slideDown(250, function(){
							$body.scrollTop($('.checkout-payment').offset().top);
							$('.checkout-payment').addClass('in-progress');
							$('.checkout-progress-payment').addClass('in-progress');
						});
					}
				});
			});

			// edit shipping step once completed
			$body.on('click touchstart', '.checkout-review .edit-shipping, .checkout-progress-shipping.complete', function(e){
				e.preventDefault();

				// mark step as incomplete
				$('.checkout-shipping').removeClass('complete').addClass('in-progress');
				$('.checkout-payment').removeClass('in-progress');
				$('.checkout-review').removeClass('in-progress');
				$('.checkout-progress-shipping').removeClass('complete').addClass('in-progress');
				$('.checkout-progress-payment').removeClass('in-progress');
				$('.checkout-progress-review').removeClass('in-progress');

				// reset avs flag
				$('#skip-avs').val(false);

				// close review, gift card, and payment forms
				$('.checkout-review').slideUp(250, function(){
					$('.checkout-payment').slideUp(250, function(){
						$('.checkout-shipping').slideDown(250, function(){
							$body.scrollTop($('.checkout-shipping').offset().top);
							$('.promo-code-container').slideDown(250);
						});
					});
				});
			});

			// gift card
			var $gcForm = $('#gift-card-form'),
				$gcRestrictedText = $('#gift-card-restricted-text'),
				$gcInfo = $('.gift-card-info'),
				$gcReviewPanel = $('.gift-card-review-panel'),
				$ccReview = $('.credit-card-review'),
				$baReview = $('.billing-address-review'),
				$paymentMethod = $('.payment-method-section'),
				giftCardOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$checkoutLeftColumn.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {

								// clear form
								$gcForm.find('input[type="tel"]').val('');
								global[namespace].utilities.form.hideErrors($gcForm);

								// mark gift card as applied for edit link style
								$('.gift-card-info').addClass('complete');
								$('.edit-payment').addClass('gc-applied');

								$gcForm.slideUp(250, function(){
									$gcReviewPanel.slideDown(250);
								});

								// update applied gift cards
								if (responseText.appliedGiftCards.length > 0) {
									$gcReviewPanel.html(Mustache.render(TEMPLATES.appliedGiftCards, responseText));
								}
								else {
									$gcReviewPanel.html('').slideUp(250);
								}

								// update order totals
								$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

								// update gift card totals in checkout payment sections
								$.each(responseText.appliedGiftCards, function(){
									$('.amount-' + this.number).html(this.amount);
								});

								// hide credit card form if order is covered by gift cards
								if (responseText.orderCovered == 'true') {
									$paymentMethod.slideUp(250);
									$gcInfo.slideUp(250);
									$ccReview.slideUp(250);
									$baReview.slideUp(250);
								}
								else {
									$paymentMethod.slideDown(250);
									$gcInfo.slideDown(250);
									$ccReview.slideDown(250);
									$baReview.slideDown(250);
								}

								// hide ajax loader
								global[namespace].utilities.hideLoader();
								$body.scrollTop($('#checkout-payment').offset().top);

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

			// gift card ajax form handler
			$gcForm.ajaxForm(giftCardOptions);

			// remove gift card from order
			$('body').on('click', '.gift-card-remove', function(){
				$('#giftCardId').val($(this).data('number'));
				$('#giftCardRemovalForm').ajaxSubmit(giftCardOptions);
			});

			// show gift card form
			$('.add-gift-card').click(function(e){
				e.preventDefault();
				$gcForm.slideToggle(250);
			});

			// BZ 2505 - show restricted GC text instead of the form
			$('.gift-card-restricted').click(function(e){
				e.preventDefault();
				$gcRestrictedText.slideToggle(250);
			});
			// payment method
			var $newPaymentMethod = $('.new-payment-method'),
					$paymentName = $('.payment-name'),
					$billingAddress = $('.billing-address'),
					$cvvInput = $('.cvv-input'),
					$paymentInfoForm = $('#payment-info-form');
			$('input[name="payment-method"]').click(function(){
				$cvvInput.val('');
				if (document.getElementById('payment-method-new').checked) {
					$newPaymentMethod.slideDown(250);
				}
				else {
					document.getElementById('save-payment-method').checked = false;
					$paymentName.slideUp(250);
					$newPaymentMethod.slideUp(250);
				}
			});
			$cvvInput.on('blur', function(){
				$('#cvv').val($(this).val());
			});
			
			//2619 - set the cvv val before enter button
			$cvvInput.keypress(function(event){
				var Obj = $(this),
				keycode = (event.keyCode ? event.keyCode : event.which);
				if (keycode == 13) {
					// 13 - enter
					$('#cvv').val($(this).val());
				}
			})
			$('#save-payment-method').click(function(){
				if (this.checked) {
					$paymentName.slideDown(250);
				}
				else {
					$paymentName.slideUp(250);
				}
			});
			$('#same-as-shipping').click(function(){
				if (this.checked) {
					$billingAddress.slideUp(250);
					// $billingAddress.find('input, select').val('').validate('clearFormErrors')
				}
				else {
					$billingAddress.slideDown(250);
				}
			});

			// mff brand synchrony cards
			$('#card-type').on('change', function(){
				var $this = $(this),
					$month = $('#month'),
					$year = $('#year');
				if ($this.val() == 'millsCredit') {
					global[namespace].utilities.form.hideErrors($paymentInfoForm);
					$month.val('12').addClass('disabled').attr('tabindex', '-1');
					$year.val('2049').addClass('disabled').attr('tabindex', '-1');
				}
				else {
					$month.val('').removeClass('disabled').removeAttr('tabindex');
					$year.val('').removeClass('disabled').removeAttr('tabindex');
				}
			});

			var paymentOptions = {
					dataType: 'json',
					beforeSerialize : function($form) {
						$form.find('#billing-phone').unmask();
					},
					beforeSubmit: function (arr, $form, options) {
						$checkoutLeftColumn.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						// re-apply phone mask
						$('#billing-phone').mask('000-000-0000');

						if (statusText == 'success') {
							if (responseText.success == 'true') {
								if (responseText.addressMatched == 'true') {
									// avs passed
									
									// update order totals
									$('.totals').html(Mustache.render(TEMPLATES.orderTotals, responseText));

									// listrak email signup
									if (document.getElementById('promo-emails').checked) {
										if (typeof _ltk !== 'undefined') {
											_ltk.SCA.CaptureEmail('email');
										}
									}

									// update payment info in review panel
									$('.payment-info-review-panel').load(CONSTANTS.contextPath + '/checkout/includes/paymentMethodReview.jsp');
									// BZ 2505 - Refresh BOPIS Pickup section when payment details are changed
									$('.bopis-person-review').load(CONSTANTS.contextPath + '/checkout/includes/bopisPersonReview.jsp');
									// mark step as complete
									$('.checkout-payment').removeClass('in-progress').addClass('complete');
									$('.checkout-review').addClass('in-progress');
									$('.checkout-progress-payment').removeClass('in-progress').addClass('complete');
									$('.checkout-progress-review').addClass('in-progress');

									// close gift card and payment forms and show review panel
									$('.checkout-payment').slideUp(250, function(){
										$('.checkout-review').slideDown(250, function(){
											global[namespace].utilities.hideLoader();
											$body.scrollTop($('#checkout-review').offset().top);
											$('.promo-code-container').slideUp(250);
										});
									});
									
									if (KP.analytics) {
										if(digitalData)
											digitalData.checkoutStep = 4;
										KP.analytics.sendCheckoutOption("Review");
									}

								}
								else {
									// avs failed
									if (typeof avsJSON !== 'undefined') {
										avsJSON = JSON.stringify(responseText);
									}
									else {
										$body.append('<script>var avsJSON = \'' + JSON.stringify(responseText) + '\';</script>');
									}
									global[namespace].utilities.hideLoader();
									var $modalTarget = document.getElementById('avsModal') ? $('#avsModal') : global[namespace].utilities.createModal('avsModal', 'medium'),
										url = CONSTANTS.contextPath + '/checkout/ajax/avsModal.jsp',
										option = {'url': url};
									$modalTarget.modal(option);
								}
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
						// re-apply phone mask
						$('#billing-phone').mask('000-000-0000');
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
			$paymentInfoForm.ajaxForm(paymentOptions);

			$body.on('click touchstart', '.edit-payment, .checkout-progress-payment.complete', function(e){
				e.preventDefault();

				// mark step as incomplete
				$('.checkout-payment').removeClass('complete').addClass('in-progress');
				$('.checkout-review').removeClass('in-progress');
				$('.checkout-progress-payment').removeClass('complete').addClass('in-progress');
				$('.checkout-progress-review').removeClass('in-progress');

				// reset avs flag
				$('#billing-skip-avs').val(false);

				// close review and shipping forms
				$('.checkout-review').slideUp(250, function(){
					$('.checkout-shipping').slideUp(250, function(){
						$('.checkout-payment').slideDown(250, function(){
							$body.scrollTop($('.checkout-payment').offset().top);
							$('.promo-code-container').slideDown(250);
						});
					});
				});
			});

			// order review
			var submitOptions = {
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					$checkoutLeftColumn.find('.alert-box').remove();
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					if (statusText == 'success') {
						if (responseText.success == 'true') {
							window.location = responseText.url;
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
			$('#submitOrderForm').ajaxForm(submitOptions);

			// fake commit order button click
			$('#fake-commit-order').on('click', function(e){
				e.preventDefault();
				$('#commit-order').trigger('click');
			});

		},
		orderConfirmation : function() {

			// ensure profile name gets updated
			global[namespace].profileController.getProfileStatus(true);

			$('.create-account-button').click(function(e){
				$('.create-account-message').slideUp(250, function(){
					$('.create-account-form').slideDown(250);
				});
			});

			var profileOptions = {
				dataType : 'json',
				beforeSubmit : function(arr, $form, options) {
					global[namespace].utilities.showLoader();
				},
				success: function(responseText, statusText, xhr, $form) {
					if (statusText == 'success') {
						if (responseText.success == 'true') {
							// reset profile cookie on login
							global[namespace].profileController.resetProfileStatus();
							window.location = responseText.url;
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
			$('#register-form').ajaxForm(profileOptions);
		}
	};

	global[namespace] = global[namespace] || {};

	global[namespace].checkout = checkout;

})(this, window.jQuery, "KP");
