/*!
 * Account Init
 */
(function (global, $, namespace) {
	//"use strict";

	var loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			CONSTANTS = global[namespace].constants,
			$window = $(window),
			$body = $('body');

	// same height for all grid elements
	function equalizeGrid() {
		if ($window.width() > global[namespace].config.smallMax) {
			// equalize the grid heights
			var maxHeight = [],
					$card = $('.card');
			for (var i=0; i<($card.length/3); i++) {
				maxHeight[i] = 0;
			}
			$card.each(function(index){
				var height = $(this).outerHeight(),
						row = Math.floor(index / 3);
				if (height > maxHeight[row]) {
					maxHeight[row] = height;
				}
			});
			$card.each(function(index){
				$(this).outerHeight(maxHeight[Math.floor(index / 3)]);
			});
			$('.card-links').addClass('equalized');
		}
		else {
			// remove the equalization heights
			$('.card').each(function(index){
				$(this).removeAttr('style');
			});
			$('.card-links').removeClass('equalized');
		}
	}

	// handle a successful AVS AJAX request
	function handleAvsSuccess(responseText, statusText, xhr, $form){
		if (statusText == 'success') {
			if (responseText.success == 'true') {
				if (responseText.addressMatched == 'true') {
					window.location = responseText.url;
				}
				else {
					if (typeof avsJSON !== 'undefined') {
						avsJSON = JSON.stringify(responseText);
					}
					else {
						$body.append('<script>var avsJSON = \'' + JSON.stringify(responseText) + '\';</script>');
					}
					global[namespace].utilities.hideLoader();
					var $modalTarget = document.getElementById('avsModal') ? $('#avsModal') : global[namespace].utilities.createModal('avsModal', 'medium'),
							url = CONSTANTS.contextPath + '/account/ajax/avsModal.jsp',
							option = {'url': url};
					$modalTarget.modal(option);
				}
			}
			else {
				global[namespace].utilities.hideLoader();
				global[namespace].utilities.form.showErrors($form, responseText);
			}
		}
		else{
			console.log('Malformed JSON : missing statusText parameter:');
			global[namespace].utilities.hideLoader();
			global[namespace].utilities.form.ajaxError(xhr, statusText, responseText, $form);
		}
	}

	// form handler options
	var basicAjaxOptions = {
		dataType : 'json',
		beforeSubmit : function(arr, $form, options) {
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

	var account = {
			init : function() {
				equalizeGrid();
				$window.resize($.throttle(250, equalizeGrid));
				if (KP.analytics) {
					KP.analytics.sendAccountPageEvents();
				}
			},
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
								var action = $form.attr("id") == "register-form" ? "Create Account" : "Sign In";
								KP.analytics.trackEvent(action, action, action);
								if(action === "Sign In"){
									responseText.url = global[namespace].utilities.addURLParameter(responseText.url, 'tus', 'yes');
								}
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
				$('#register-form').ajaxForm(profileOptions);
			},
			account : function() {
				// ensure profile name gets updated
				global[namespace].profileController.getProfileStatus(true);

				// display welcome modal
				if (global[namespace].utilities.getURLParameter(window.location.href, 'new') == 'true') {
					var $modalTarget = document.getElementById('welcome-modal') ? $('#welcome-modal') : global[namespace].utilities.createModal('welcome-modal', 'x-small'),
						url = CONSTANTS.contextPath + '/account/ajax/welcomeModal.jsp',
						option = {'url': url};
					$modalTarget.modal(option);
				}
			},
			profile : function() {
				$('#phone').mask('000-000-0000');

				var profileOptions = {
					dataType : 'json',
					beforeSerialize : function($form) {
						$form.find('#phone').unmask();
					},
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');

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
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#profile-form').ajaxForm(profileOptions);
			},
			address : function() {
				$('#phone').mask('000-000-0000');

				var newAddressOptions = {
					dataType : 'json',
					beforeSerialize : function($form) {
						var $zip = $form.find('#zip');
						$form.find('#phone').unmask();
						$zip.val(global[namespace].utilities.hyphenateZip($zip.val()));
					},
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						if (KP.analytics && responseText.success == 'true' && responseText.addressMatched == 'true') {
							KP.analytics.trackEvent('My Account CTA', 'My Account', $("#address-submit").val(), $("#address-submit").val());
						}
						handleAvsSuccess(responseText, statusText, xhr, $form);
					},
					error: function(xhr, statusText, exception, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#address-form').ajaxForm(newAddressOptions);
			},
			payment : function() {
				$('#phone').mask('000-000-0000');

				// mff brand synchrony cards
				$('#card-type').on('change', function(){
					var $this = $(this),
							$month = $('#month'),
							$year = $('#year');
					if ($this.val() == 'millsCredit') {
						global[namespace].utilities.form.hideErrors($('#payment-form'));
						$month.val('12').addClass('disabled').attr('tabindex', '-1');
						$year.val('2049').addClass('disabled').attr('tabindex', '-1');
					}
					else {
						$month.val('').removeClass('disabled').removeAttr('tabindex');
						$year.val('').removeClass('disabled').removeAttr('tabindex');
					}
				});

				// new address drawer
				var $newPaymentAddressForm = $('.new-payment-address-form');
				if (document.getElementById('new-payment-address').checked) {
					$newPaymentAddressForm.show(0);
				}
				$('input[name="payment-address"]').click(function(){
					if (document.getElementById('new-payment-address').checked) {
						$newPaymentAddressForm.slideDown(250);
					}
					else {
						$newPaymentAddressForm.slideUp(250);
					}
				});

				// ajax form handler
				var paymentFormOptions = {
					dataType : 'json',
					beforeSerialize : function($form) {
						var $zip = $form.find('#postalCode');
						$form.find('#phoneNumber').unmask();
						$zip.val(global[namespace].utilities.hyphenateZip($zip.val()));
					},
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						
						if (KP.analytics) {
							KP.analytics.trackEvent('My Account CTA', 'My Account', $("#payment-form-submit").val(), $("#payment-form-submit").val());
						}
						handleAvsSuccess(responseText, statusText, xhr, $form);
					},
					error: function(xhr, statusText, exception, $form) {
						// re-apply phone mask
						$('#phone').mask('000-000-0000');
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#payment-form').ajaxForm(paymentFormOptions);
			},
			taxExemption : function() {
				var taxExemptionOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (KP.analytics) {
							KP.analytics.trackEvent('My Account CTA', 'My Account', $("#tax-exemption-submit").val(), $("#tax-exemption-submit").val());
						}
						handleAvsSuccess(responseText, statusText, xhr, $form);
					},
					error: function(xhr, statusText, exception, $form) {
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};
				$('#tax-exemption-form').ajaxForm(taxExemptionOptions);
			},
			wishList : function() {

				// ajax form handler
				var removeItemOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								if ($('#wishlist-num-pages').val() > 1) {
									window.location.reload();
								}
								else {
									if (responseText.wishListCount > 0) {
										$form.slideUp(200);
										global[namespace].utilities.hideLoader();
									}
									else {
										// cart is empty, show appropriate message
										$('.wish-list-content').load(CONSTANTS.contextPath + '/account/fragments/wishListEmpty.jspf');
										$('.title-buttons').remove();
										global[namespace].utilities.hideLoader();
									}
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
						console.log('AJAX Error:');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, exception, $form);
					}
				};

				// remove item from wish list
				$body.on('click', '.item-remove', function(e){
					e.preventDefault();
					var formId = $(this).data('form-id').trim();
					$('#' + formId).ajaxSubmit(removeItemOptions);
				});
			},
			wishListPrint : function() {
				window.print();
			},
			changeEmail : function() {
				$('#change-email-form').ajaxForm(basicAjaxOptions);
			},
			changePassword : function() {
				$('#change-password-form').ajaxForm(basicAjaxOptions);
			},
			expressCheckout : function() {
				$('#express-checkout-form').ajaxForm(basicAjaxOptions);
			},
			orderTracking : function() {
				$('#order-tracking-form').ajaxForm(basicAjaxOptions);
			},
			passwordReset : function() {
				var passwordResetOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							global[namespace].utilities.hideLoader();
							if (responseText.success == 'true') {
								$('#password-reset-form').remove();
								$('.password-reset-errors').remove();
								$('.password-reset-message').html('<p>A password reset email has been sent to the email address provided.</p><a href="' + CONSTANTS.contextPath + '/account/account.jsp" class="button primary">Login</a>');
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
								grecaptcha.reset();
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
				$('#password-reset-form').ajaxForm(passwordResetOptions);
			}
		};

	global[namespace] = global[namespace] || {};

	global[namespace].account = account;

})(this, window.jQuery, "KP");
