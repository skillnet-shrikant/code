/*!
 * Browse Init
 */
(function (global, $, namespace) {
	//"use strict";

	var CONSTANTS = global[namespace].constants,
			TEMPLATES = global[namespace].templates,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			$body = $('body'),
			$window = $(window);

	var browse = {
		init : function () {
			// pagination click listener
			$body.on('click', '.page-num', function(){
				$('.page-num').removeClass('active');
				$(this).addClass('active');
			});
		},
		category : function() {

			// mobile refinements
			var $leftCol = $('.two-column-left'),
					$catDrop = $('.category-dropdowns');
			$('.hide-refinements').on('click', function(){
				$leftCol.hide();
				$catDrop.removeClass('open');
			});
			$('.show-refinements').on('click', function(){
				$leftCol.show();
				$catDrop.addClass('open');
			});

			//initalize filters
			var filterController = new global[namespace].FilterController();

			// three promo slider
			$('.promo-slider-three').slick({
				dots: true,
				infinite: false,
				slidesToShow: 3,
				slidesToScroll: 3,
				arrows: true,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// four promo slider
			$('.promo-slider-four').slick({
				dots: true,
				infinite: false,
				slidesToShow: 4,
				slidesToScroll: 4,
				arrows: true,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// five promo slider
			$('.promo-slider-five').slick({
				dots: true,
				infinite: false,
				slidesToShow: 5,
				slidesToScroll: 5,
				arrows: true,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 3.5,
							slidesToScroll: 3
						}
					}
				]
			});

			// six promo slider
			$('.promo-slider-six').slick({
				dots: true,
				infinite: false,
				slidesToShow: 6,
				slidesToScroll: 6,
				arrows: true,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 3.5,
							slidesToScroll: 3
						}
					}
				]
			});

			// three product slider
			$('.product-slider-three').slick({
				dots: false,
				infinite: false,
				slidesToShow: 3,
				slidesToScroll: 3,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// four product slider
			$('.product-slider-four').slick({
				dots: false,
				infinite: false,
				slidesToShow: 4,
				slidesToScroll: 4,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// five product slider
			$('.product-slider-five').slick({
				dots: true,
				infinite: false,
				slidesToShow: 5,
				slidesToScroll: 5,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 3.5,
							slidesToScroll: 3
						}
					}
				]
			});


			// six product slider
			$('.product-slider-six').slick({
				dots: true,
				infinite: false,
				slidesToShow: 6,
				slidesToScroll: 6,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: false,
							slidesToShow: 3.5,
							slidesToScroll: 3
						}
					}
				]
			});

			if (KP.analytics) {
				KP.analytics.sendBrowsePageEvents();
				$(document).on('click', '.product-grid a', function(event) {
					var pid = $(this).data("pid"),
						action = $(this).data("action");
					if (pid) {
						KP.analytics.sendProductClick(pid, action);
					}
				});
			}
		},
		department : function() {

			// mobile refinements
			var $leftCol = $('.two-column-left'),
					$catDrop = $('.category-dropdowns');
			$('.hide-refinements').on('click', function(){
				$leftCol.hide();
				$catDrop.removeClass('open');
			});
			$('.show-refinements').on('click', function(){
				$leftCol.show();
				$catDrop.addClass('open');
			});

			//initalize filters
			var filterController = new global[namespace].FilterController();

		},
		product : function() {

			// enable buttons
			$('.back-in-stock-modal-trigger, .ffl-modal-trigger, .add-to-cart-submit, .add-to-wish-list, .item-save-login, .change-store, .ship-my-order, .login-modal-trigger').removeClass('disabled');

			// initialize pickers
			var productControllers = {};
			if (typeof KP_PRODUCT !== 'undefined') {
				for (product in KP_PRODUCT) {
					productControllers[product] = new global[namespace].ProductController(KP_PRODUCT[product]);
				}
			}

			// if gStoreId query parameter exists, silently submit a backend request to
			// update the home store. this will prevent backend dependency issues when the
			// user interacts with the page (changing store, selecting product variant, etc.)
			var gStoreId = global[namespace].utilities.getStoreIdURLParam();
			if (gStoreId !== '') {
				$('#homestore').val(gStoreId);
				$('#home-store-form').ajaxSubmit({dataType:'json'});
			}

			// open accordion on page load
			$('#product-info-accordion').accordion('openAll');

			// gift card page
			if ($body.hasClass('gift-card')) {
				$('#gift-card-amount').on('keydown', function(e){
					var keycode = (e.keyCode ? e.keyCode : e.which);
					// enter triggers submit
					if (keycode == 13) {
						e.preventDefault();
						e.stopPropagation();
						$('.add-to-cart-submit').trigger('click');
					}
				});
			}

			// bopis table pickers - only allow 1 sku to be added at a time
			function disableQtyFields($fieldToStayEnabled){
				$('.table-qty').each(function(){
					var $this = $(this);
					$this.addClass('disabled').attr('disabled', true);
					$this.parents('tr').addClass('disabled');
					$fieldToStayEnabled.removeClass('disabled').removeAttr('disabled');
					$fieldToStayEnabled.parents('tr').removeClass('disabled');
				});
			}
			function enableQtyFields(clearQtys){
				$('.table-qty').each(function(){
					var $this = $(this),
						id = $(this).attr('id');
					$this.removeClass('disabled').removeAttr('disabled');
					$this.parents('tr').removeClass('disabled');
					if (clearQtys === true) {
						$this.val('0');
						if("span."+id !== undefined){
							if(id === $("span."+id).attr('class')){
								$("."+id).hide();
								$this.attr('type','tel');
							}
						}
					}else{
						if("span."+id !== undefined){
							if(id === $("span."+id).attr('class')){
								$("."+id).show();
								$this.attr('type','hidden');
							}
						}
					}
				});
			}
			$('.table-qty').click(function(e){
				$(this).select();
			});
			$('.table-qty').keyup(function(e){
				var $self = $(this);
				if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
					if ($self.val() > 0) {
						disableQtyFields($self);
					}
					else {
						enableQtyFields();
					}
				}
			});
			$('input[name^="order-type"]').click(function(){
				var $productPickers = $('.product-pickers'),
					$this = $(this);
				if($productPickers.hasClass('table-picker')){
					if($this.parents('.add-to-cart-actions').find('.bopis-order') !== null && $this.parents('.add-to-cart-actions').find('.bopis-order').is(':checked')) {
						enableQtyFields();
						//$('.bopis-location-info').removeClass('hide');
						$this.parents('.add-to-cart-actions').find('.bopis-location-info').removeClass('hide');
						if($('.alert-box') !== undefined && $('.alert-box').length > 0){
							//$('.alert-box').remove();
						}
						if($this.parents('.add-to-cart-actions').find('.bopis-store-unavailable').length > 0 ){
							$(this).parents('.add-to-cart-actions').find('.add-to-cart-submit').addClass('disabled');
						}else{
							$(this).parents('.add-to-cart-actions').find('.add-to-cart-submit').removeClass('disabled');
						}
					}else {
						enableQtyFields();
						//$('.bopis-location-info').addClass('hide');
						$this.parents('.add-to-cart-actions').find('.bopis-location-info').addClass('hide');
						//switch to home to remove the bopis error if exist
						if($('.alert-box') !== undefined && $('.alert-box').length > 0){
							//$('.alert-box').remove();
						}
						var index = $('.add-to-cart-actions').index($this.parents('.add-to-cart-actions'));
						var inventory = KP_PRODUCT[product].skus[index].inventory;
						if(inventory === "0"){
							$(this).parents('.add-to-cart-actions').find('.add-to-cart-submit').addClass('disabled');
						}else{
							$(this).parents('.add-to-cart-actions').find('.add-to-cart-submit').removeClass('disabled');
						}
					}
				}else if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
					enableQtyFields(true);
					$('.bopis-location-info').removeClass('hide');
					if($('.alert-box') !== undefined && $('.alert-box').length > 0){
						$('.alert-box').remove();
					}
					if($('.add-to-cart-actions').find('.bopis-store-unavailable').length > 0 ){
						$('.add-to-cart-actions').find('.add-to-cart-submit').addClass('disable-add-to-cart');
					}else{
						$(this).parents('.add-to-cart-actions').find('.add-to-cart-submit').removeClass('disable-add-to-cart');
					}
					// jjensen: i don't know why suresh had this in here, but it's not what we want. if you
					//   reinitialize the plugin it will recreate all the pickers and you'll have to delete
					//   them...also not what we want
					// if (typeof KP_PRODUCT !== 'undefined') {
					// 	for (product in KP_PRODUCT) {
					// 		productControllers[product] = new global[namespace].ProductController(KP_PRODUCT[product]);
					// 	}
					// }
				}
				else {
					enableQtyFields();
					$('.bopis-location-info').addClass('hide');
					//switch to home to remove the bopis error if exist
					if($('.alert-box') !== undefined && $('.alert-box').length > 0){
						$('.alert-box').remove();
					}
					//No inventory available for selected store
					$('.add-to-cart-actions').find('.add-to-cart-submit').removeClass('disable-add-to-cart');
					// jjensen: i don't know why suresh had this in here, but it's not what we want. if you
					//   reinitialize the plugin it will recreate all the pickers and you'll have to delete
					//   them...also not what we want
					// if (typeof KP_PRODUCT !== 'undefined') {
					// 	for (product in KP_PRODUCT) {
					// 		productControllers[product] = new global[namespace].ProductController(KP_PRODUCT[product]);
					// 	}
					// }
				}
			});

			// add to cart
			var addToCartOptions = {
					dataType: 'json',
					resetForm: true,
					beforeSerialize : function($form) {
						// gift card
						var $gcAmount = $('#gift-card-amount');

						global[namespace].utilities.form.hideErrors($form);
						$form.validate('clearFieldMessage', $gcAmount);

						if ($gcAmount.length > 0) {
							var ogAmount = $gcAmount.val(),
									newAmount = ogAmount.replace('.', '-').replace(/[^0-9\-]/g, '').replace('-', '.');
							if (newAmount.toString().indexOf('.') > 0) {
								$gcAmount.val(parseFloat(newAmount).toFixed(2));
							}
							if (newAmount < 2 || newAmount > 500 || newAmount !== ogAmount) {
								if (newAmount !== ogAmount) {
									$gcAmount.val(newAmount);
								}
								$gcAmount.focus();
								global[namespace].utilities.form.showInlineErrors({'formId': 'add-to-cart-form', 'fieldsWithErrors': [{'field': $gcAmount, 'errors': ['Please enter a value between $2 and $500']}]});
								return false;
							}
						}
					},
					beforeSubmit: function (arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {

								// update header item counter
								global[namespace].profileController.getProfileStatus(true);

								// update side cart items
								$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp', function(){
									if ($window.width() > global[namespace].config.smallMax) {
										$('.desktop-header .side-cart-toggle').click();
									}
									else {
										$('.mobile-header .side-cart-toggle').click();
									}
								});

								$('.promo-line-item-msg').html(Mustache.render(TEMPLATES.lineItemPromotions, responseText));

								// clear inputs
								$('.table-qty').each(function(){
									$(this).val('0');
								});

								if($form.attr('id') === "select-bopis-store"){
									$('.ship-to-home').remove();
									$('.bopis-order').attr('checked',true);
									$('.bopis-location-info').removeClass('hide');
								}
								global[namespace].utilities.hideLoader();
								
								if(KP.analytics){
									if(responseText.productId != '')
									KP.analytics.sendAddProduct(responseText.productId);
								}
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
					}
				},
				// Cart Edit Options
				updateCartOptions = {
						dataType: 'json',
						resetForm: true,
						beforeSerialize : function($form) {
							// gift card
							var $gcAmount = $('#gift-card-amount');

							global[namespace].utilities.form.hideErrors($form);
							$form.validate('clearFieldMessage', $gcAmount);

							if ($gcAmount.length > 0) {
								var ogAmount = $gcAmount.val(),
										newAmount = ogAmount.replace('.', '-').replace(/[^0-9\-]/g, '').replace('-', '.');
								if (newAmount.toString().indexOf('.') > 0) {
									$gcAmount.val(parseFloat(newAmount).toFixed(2));
								}
								if (newAmount < 2 || newAmount > 500 || newAmount !== ogAmount) {
									if (newAmount !== ogAmount) {
										$gcAmount.val(newAmount);
									}
									$gcAmount.focus();
									global[namespace].utilities.form.showInlineErrors({'formId': 'add-to-cart-form', 'fieldsWithErrors': [{'field': $gcAmount, 'errors': ['Please enter a value between $2 and $500']}]});
									return false;
								}
							}
						},
						beforeSubmit: function (arr, $form, options) {
							global[namespace].utilities.showLoader();
						},
						success: function (responseText, statusText, xhr, $form) {
							if (statusText == 'success') {
								if (responseText.success == 'true') {

									// update header item counter
									global[namespace].profileController.getProfileStatus(true);

									// update sidecart
									$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp');

									// clear inputs
									$('.table-qty').each(function(){
										$(this).val('0');
									});
									global[namespace].utilities.hideLoader();
									window.location = responseText.url;
								}
								else {
									$('#quantity').val($('#prevQuantity').val());
									global[namespace].utilities.hideLoader();
									global[namespace].utilities.form.showErrors($form, responseText);
								}
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
							}
						},
						error: function (xhr, statusText, exception, $form) {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
						}
					},
				addToWishListOptions = {
					dataType: 'json',
					resetForm: true,
					beforeSubmit: function (arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $modalTarget = document.getElementById('wish-list-confirmation-modal') ? $('#wish-list-confirmation-modal') : global[namespace].utilities.createModal('wish-list-confirmation-modal', 'medium');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/addToWishListConfirmation.jsp?productId=' + responseText.productId + '&skuId=' + responseText.skuId});

								if ($('.product-pickers').hasClass('table-picker')) {
									$('.table-details.active-sku').find('.add-to-wish-list').replaceWith('<span class="added-to-wish-list">Added To Wish List</span>');
								}else{
									$('.add-to-wish-list').replaceWith('<span class="added-to-wish-list">Added To Wish List</span>');
								}
								global[namespace].utilities.hideLoader();
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to wish list error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to wish list error: " + exception, $form);
					}
				};

			$('.add-to-cart-submit').on('click', function (e) {
				e.preventDefault();
				e.stopPropagation();

				var prodId = '',
						quantity = '',
						skuId = '',
						skuSet = false,
						$addToCartForm = $('#add-to-cart-form'),
						$productPickers = $('.product-pickers'),
						selectedStoreId = '',
						$this = $(this);

				if ($productPickers.hasClass('table-picker')) {
					/*$('.table-qty').each(function(){*/
					$('.table-details').removeClass("active-sku");
					if ($this.parents('.table').find('.table-qty').val() > 0) {
						skuSet = true;
						skuId = $this.parents('.table').find('.table-sku-id').val();
						quantity = $this.parents('.table').find('.table-qty').val()
						selectedStoreId = $('#bopis-store-id').val();
						$this.parents('.table').addClass('active-sku');
						global[namespace].utilities.form.hideErrors($addToCartForm);
						//return false;
					}
				/*});*/
				}
				else {
					prodId = $('#productId').val();
					skuId = $('#catalogRefIds').val();
					quantity = $('#quantity').val();
					selectedStoreId = $('#bopis-store-id').val();
					if (skuId !== '') {
						skuSet = true;
					}
				}

				// make sure there's a sku set
				if (skuSet) {

					// bopis add to cart

					if($this.parents('.add-to-cart-actions').find('.bopis-order') !== null && $this.parents('.add-to-cart-actions').find('.bopis-order').is(':checked')) {

						var edsPPSOnlyOrder = $('#eds-pps-only-inventory').val();
						var bopisOnlyItem = $('#bopis-order-inventory').val();
						// set skuId, quantity and fromProduct hidden bopis inputs
						$('#bopis-sku-id').val(skuId);
						$('#bopis-sku-id-inventory').val(skuId);
						$('#bopis-quantity').val(quantity);
						$('#bopis-quantity-inventory').val(quantity);
						$('#bopis-from-product').val('true');
						$('#bopis-from-product-inventory').val('true');

						if (selectedStoreId !== '' && edsPPSOnlyOrder != 'true') {
							// there's already a bopis store selected, check inventory at current store

							// inventory check
							$('#bopis-inventory-form').ajaxSubmit({
								dataType: 'json',
								beforeSubmit: function (arr, $form, options) {
									global[namespace].utilities.showLoader();
								},
								success: function (responseText, statusText, xhr, $form) {
									global[namespace].searchResults = responseText;

									if (statusText == 'success') {
										if (responseText.success == 'true') {

											// iterate through selected stores
											var inventoryAvailable = false;
											for (var i=0; i<global[namespace].searchResults.stores.length; i++) {
												if (global[namespace].searchResults.stores[i].locationId == selectedStoreId) {
													global[namespace].searchResults.current = global[namespace].searchResults.stores[i];
													if (global[namespace].searchResults.stores[i].eligible == 'true') {
														inventoryAvailable = true;
														break;
													}
												}
											}
											if (inventoryAvailable) {
												// there is sufficient inventory, submit add to cart
												$('#select-bopis-store').ajaxSubmit(addToCartOptions);
											}
											else {
												// not enough inventory, pop up bopis modal with available stores
												var $modalTarget = document.getElementById('bopis-notification-modal') ? $('#bopis-notification-modal') : global[namespace].utilities.createModal('bopis-notification-modal', 'small');
												$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisNotificationModal.jsp'});
												global[namespace].utilities.hideLoader();
											}
										}
										else {
											global[namespace].utilities.hideLoader();
											global[namespace].utilities.form.showErrors($form, responseText);
										}
									}
									else {
										global[namespace].utilities.hideLoader();
										global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
									}
								},
								error: function (xhr, statusText, exception, $form) {
									global[namespace].utilities.hideLoader();
									global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
								}
							});
						} else if (selectedStoreId !== '' && edsPPSOnlyOrder == 'true') {
							var $modalTarget = document.getElementById('eds-pps-only-notification-modal') ? $('#eds-pps-only-notification-modal') : global[namespace].utilities.createModal('eds-pps-only-notification-modal', 'small');
							$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/edsPPSNotificationModal.jsp?bopisOnlyItem='+bopisOnlyItem});
							global[namespace].utilities.hideLoader();
						}
						else {

							if(edsPPSOnlyOrder != 'true') {
								// there's not a bopis store selected, show the modal
								var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisModal.jsp'});
							} else {
								var $modalTarget = document.getElementById('eds-pps-only-notification-modal') ? $('#eds-pps-only-notification-modal') : global[namespace].utilities.createModal('eds-pps-only-notification-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/edsPPSNotificationModal.jsp?bopisOnlyItem='+bopisOnlyItem});
								global[namespace].utilities.hideLoader();
							}

						}
					}
					else {
						// normal add to cart
						$addToCartForm.ajaxSubmit(addToCartOptions);
					}
				}
				else {
					if ($productPickers.hasClass('table-picker')) {
						global[namespace].utilities.form.showFormErrors($addToCartForm, {'errorMessages': 'Please enter a quantity for SKU'});
					}
					else {
						productControllers[prodId].showSelectionErrors();
					}
				}
			});

			// eds modal
			$('.eds-message .reveal-eds-modal').on('click', function(e) {
				e.preventDefault();
				var html = $('#eds-info-modal').html();
				var $modalTarget = document.getElementById('eds-modal') ? $('#eds-modal') : global[namespace].utilities.createModal('eds-modal', 'small');
  			  	$modalTarget.modal({ 'content': html });
			});

			// add to wish list
			$body.on('click','.add-to-wish-list, .login-modal-trigger', function (e) {
				e.preventDefault();
				e.stopPropagation();

				var skuId = '',
						skuSet = false,
						isTablePicker= $('.product-pickers').hasClass('table-picker'),
						$addToCartForm = $('#add-to-cart-form'),
						$this = $(this);

				if (isTablePicker) {
					/*$('.table-qty').each(function(){
						var $this = $(this);
						if ($this.val() > 0) {
							skuSet = true;
							skuId = $this.siblings('.table-sku-id').val();
							global[namespace].utilities.form.hideErrors($addToCartForm);
							return false;
						}
					});*/
					$('.table-details').removeClass("active-sku");
					if ($this.parents('.table').find('.table-qty').val() > 0) {
						skuSet = true;
						skuId = $this.parents('.table').find('.table-sku-id').val();
						quantity = $this.parents('.table').find('.table-qty').val();
						$this.parents('.table').addClass('active-sku');
						global[namespace].utilities.form.hideErrors($addToCartForm);
						//return false;
					}
				}
				else {
					skuId = $('#catalogRefIds').val();
					if (skuId !== '') {
						skuSet = true;
					}
				}

				// make sure there's a sku set
				if (skuSet) {
					$('#wish-list-sku').val(skuId);
					if($(this).hasClass('login-modal-trigger')){
						var $modalTarget = document.getElementById('login-modal') ? $('#login-modal') : global[namespace].utilities.createModal('login-modal', 'medium');
						$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/login-modal.jsp'});
					}else{
						$('#addItemToWishList').ajaxSubmit(addToWishListOptions);
					}
				}
				else {
					if (isTablePicker) {
						global[namespace].utilities.form.showFormErrors($addToCartForm, {'errorMessages': 'Please enter a quantity for SKU'});
					}
					else {
						//productControllers[prodId].showSelectionErrors();
						global[namespace].utilities.form.showFormErrors($addToCartForm, {'errorMessages': 'Please select a Length'});
					}
				}
			});

			// Edit Cart form
			$('.update-cart-submit').on('click', function (e) {
				e.preventDefault();
				e.stopPropagation();

				var skuId = '',
					quantity = 0,
					itemId=$('#removalIds').val(),
					editMode=$('#bopis-edit-mode-inventory').val(),
						skuSet = false,
						isTablePicker= $('.product-pickers').hasClass('table-picker'),
						$updateCart = $('#updateCart'),
						$this = $(this);

				if (isTablePicker) {
					/*$('.table-qty').each(function(){
						var $this = $(this);
						if ($this.val() > 0) {
							skuSet = true;
							quantity = $this.val();
							skuId = $this.siblings('.table-sku-id').val();
							global[namespace].utilities.form.hideErrors($updateCart);
							return false;
						}
					});*/
					if ($this.parents('.table').find('.table-qty').val() > 0) {
						skuSet = true;
						skuId = $this.parents('.table').find('.table-sku-id').val();
						quantity = $this.parents('.table').find('.table-qty').val()
						global[namespace].utilities.form.hideErrors($updateCart);
						//return false;
					}
				}
				else {
					skuId = $('#catalogRefIds').val();
					if (skuId !== '') {
						skuSet = true;
					}
				}
				console.log('update cart');
				// make sure there's a sku set
				if (skuSet) {
					// bopis add to cart
					if($this.parents('.add-to-cart-actions').find('.bopis-order') !== null && $this.parents('.add-to-cart-actions').find('.bopis-order').is(':checked')) {

						var selectedStoreId = $('#bopis-store-id').val();
						// set skuId, quantity and fromProduct hidden bopis inputs
						//skuId = $('#catalogRefIds').val();
						//quantity = $('#quantity').val();

						$('#bopis-sku-id').val(skuId);
						$('#bopis-sku-id-inventory').val(skuId);
						if(editMode) {
							$('#bopis-removalIds').val(itemId);
							$('#bopis-edit-mode').val(editMode);
						}
						$('#bopis-quantity').val(quantity);
						$('#bopis-quantity-inventory').val(quantity);
						$('#bopis-edit-mode').val(editMode);
						$('#bopis-from-product').val('true');
						$('#bopis-from-product-inventory').val('true');
						$('#bopis-removalIds').val(itemId);
						
						if (selectedStoreId !== '') {
							// there's already a bopis store selected, check inventory at current store

							// inventory check
							$('#bopis-inventory-form').ajaxSubmit({
								dataType: 'json',
								beforeSubmit: function (arr, $form, options) {
									global[namespace].utilities.showLoader();
								},
								success: function (responseText, statusText, xhr, $form) {
									global[namespace].searchResults = responseText;

									if (statusText == 'success') {
										if (responseText.success == 'true') {

											// iterate through selected stores
											var inventoryAvailable = false;
											for (var i=0; i<global[namespace].searchResults.stores.length; i++) {
												if (global[namespace].searchResults.stores[i].locationId == selectedStoreId) {
													global[namespace].searchResults.current = global[namespace].searchResults.stores[i];
													if (global[namespace].searchResults.stores[i].eligible == 'true') {
														inventoryAvailable = true;
														break;
													}
												}
											}
											if (inventoryAvailable) {
												// there is sufficient inventory, submit add to cart
												//$('#select-bopis-store').ajaxSubmit(addToCartOptions);
												$('#updateItemSku').val(skuId);
												var editQty = isTablePicker?quantity:$('#quantity').val();
												$('#editQuantity').val(editQty);
												$('#updateItem').ajaxSubmit(updateCartOptions);
											}
											else {
												// not enough inventory, pop up bopis modal with available stores
												var $modalTarget = document.getElementById('bopis-notification-modal') ? $('#bopis-notification-modal') : global[namespace].utilities.createModal('bopis-notification-modal', 'small');
												$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisNotificationModal.jsp'});
												global[namespace].utilities.hideLoader();
											}
										}
										else {
											global[namespace].utilities.hideLoader();
											global[namespace].utilities.form.showErrors($form, responseText);
										}
									}
									else {
										global[namespace].utilities.hideLoader();
										global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
									}
								},
								error: function (xhr, statusText, exception, $form) {
									global[namespace].utilities.hideLoader();
									global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
								}
							});
						}
						else {
							// there's not a bopis store selected, show the modal
							var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
							$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisModal.jsp'});
						}
					} else {
						$('#updateItemSku').val(skuId);
						var editQty = isTablePicker?quantity:$('#quantity').val();
						$('#editQuantity').val(editQty);
						$('#updateItem').ajaxSubmit(updateCartOptions);
					}
				}
				else {
					if (isTablePicker) {
						global[namespace].utilities.form.showFormErrors($updateCart, {'errorMessages': 'Please enter a quantity for at least one SKU'});
					}
					else {
						productControllers[prodId].showSelectionErrors();
					}
				}
			});

			// initialize cross sells slider
			$('.cross-sells').slick({
				dots: false,
				infinite: false,
				slidesToShow: 4,
				slidesToScroll: 4,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// initialize recently viewed slider
			$('.recently-viewed').slick({
				dots: false,
				arrows: false,
				infinite: false,
				slidesToShow: 4,
				slidesToScroll: 4,
				responsive: [
					{
						breakpoint: global[namespace].config.mediumMin,
						settings: {
							arrows: true,
							slidesToShow: 2,
							slidesToScroll: 2
						}
					}
				]
			});

			// initialize product image zoom
			$('.product-image-viewer').zoom();

			// turn thumbnails into slick slider
			function initImageViewer() {
				// small-only settings
				var cssClass = 'small-slick';
				var wrongClass = 'medium-slick';
				var slickOpts = {
					dots: true,
					arrows: false,
					infinite: false,
					slidesToShow: 1,
					slidesToScroll: 1
				};
				// medium-up settings
				if ($window.width() > global[namespace].config.smallMax) {
					cssClass = 'medium-slick';
					wrongClass = 'small-slick';
					slickOpts = {
						dots: false,
						arrows: true,
						infinite: false,
						slidesToShow: 4,
						slidesToScroll: 2
					};
				}
				// slick slider is already
				// initialized at the correct size
				var $thumbs = $('.viewer-thumbnails').removeClass(wrongClass);
				if ($thumbs.hasClass(cssClass)) {
					return;
				}
				// initialize slick slider
				if ($thumbs.hasClass('slick-initialized')) {
					$thumbs.slick('unslick');
				}
				$thumbs.addClass(cssClass).slick(slickOpts);
			}

			// make sure vimeo thumbs are vertically centered
			function centerVimeoThumbs() {
				var targetHeight = $('.viewer-thumb').eq(0).height();
				$('.vimeo-thumb').each(function() {
					var $this = $(this);
					var margin = 0;
					var thisHeight = $this.find('img').height();
					if (thisHeight < targetHeight) {
						margin = (targetHeight - thisHeight) / 2;
					}
					$this.css('margin', margin + 'px 0px');
				});
			}

			initImageViewer();
			$window.load(centerVimeoThumbs);
			$window.resize($.throttle(250, initImageViewer));
			$window.resize($.throttle(250, centerVimeoThumbs));

			// adding thumbnail image error event listener
			$('.viewer-thumb-image').error(function(){
				$(this).siblings('.th-image').attr('srcset', CONSTANTS.productImageRoot + '/unavailable/th.jpg');
			});

			// swap images on thumbnail image click
			$('.viewer-thumb-image').on('click', function (e) {
				var $this = $(this);
				var imageName = $this.attr('data-image-name');
				var $mainImage = $('.viewer-main-image');

				//  set active thumbnail if the user clicked a video
				if ($this.find('.vimeo-modal').length > 0) {
					$('.viewer-thumb').removeClass('active');
					$this.parents('.viewer-thumb').addClass('active');
					return;
				}

				// check to see if image is already selected
				if ($mainImage.attr('data-image-name') == imageName) {
					return;
				}
				else {
					var path = CONSTANTS.productImageRoot + '/'+ $this.attr('data-id');
					var lPath = path + '/l/' + imageName;
					var xlPath = path + '/x/' + imageName;
					var zPath = path + '/z/' + imageName;

					// set active main image
					$('#ml-main-image').attr('srcset', lPath);
					$('#s-main-image').attr('srcset', xlPath);
					$mainImage.attr('src', lPath);
					$mainImage.attr('data-image-name', imageName);

					// set active zoom image
					$('.zoom-magnified-image').attr('src', zPath);

					// set active thumbnail
					$('.viewer-thumb').removeClass('active');
					$this.parents('.viewer-thumb').addClass('active');
				}
			});

			// gift card amounts
			$('.gift-card-button').on('click', function(e){
				e.preventDefault();
				$('#gift-card-amount').val($(this).data('amount'));
			});

		},
		search : function() {

			// mobile refinements
			var $leftCol = $('.two-column-left'),
				$catDrop = $('.category-dropdowns');
			$('.hide-refinements').on('click', function(){
				$leftCol.hide();
				$catDrop.removeClass('open');
			});
			$('.show-refinements').on('click', function(){
				$leftCol.show();
				$catDrop.addClass('open');
			});

			//initalize filters
			var filterController = new global[namespace].FilterController();
			
			if (KP.analytics) {
				KP.analytics.sendBrowsePageEvents();
				$(document).on('click', '.product-grid a', function(event) {
					var pid = $(this).data("pid"),
						action = $(this).data("action");
					if (pid) {
						KP.analytics.sendProductClick(pid, action);
					}
				});
			}

		}
	};

	global[namespace] = global[namespace] || {};

	global[namespace].browse = browse;

})(this, window.jQuery, "KP");
