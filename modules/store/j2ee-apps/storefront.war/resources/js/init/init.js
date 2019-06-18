/*!
 * The MAIN Controller
 */

(function (global, $, namespace) {
	//"use strict";

	var CONSTANTS = global[namespace].constants;
	var UTILITIES = global[namespace].utilities;
	var errorTemplate = global[namespace].templates.errorMessageTemplate;
	var loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug;
	var $body = $('body');
	var $window = $(window);

	// basic form handler options
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

	/*
	 * Initilization code
	 * Based on Garber-Irish method of DOM-ready execution
	 * @see viget.com/inspire/extending-paul-irishs-comprehensive-dom-ready-execution/
	 *
	 * MAIN is an object that contains dom ready actions for pages.
	 * The format is {namespace}.{controller}.{action}
	 * Three functions are executed on dom ready:
	 * 1) common.init()	- not called for modals.
	 * 2) {controller}.init() or modal.init()
	 * 3) {controller}.{action}()
	 */

	var initFunctions = {
		init : function (pController, pAction, pOptions) {
			// Call the page-specific controller methods

			var body = document.body,
				mController = (pController === undefined) ? body.getAttribute('data-controller') : pController,
				mAction = (pAction === undefined) ? body.getAttribute('data-action') : pAction,
				mOptions = (pOptions === undefined) ? {} : pOptions;

			// UTILITIES.startSessionTimeout();

			if (mController !== 'modal' && mController !== 'proxy') {
				this.fire('common', 'init', mOptions);
			}
			this.fire(mController, 'init', mOptions);
			this.fire(mController, mAction, mOptions);
		},
		fire : function (controller, action, options) {
			var action = (action === undefined) ? 'init' : action;
			if (controller !== '' && this[controller] && typeof this[controller][action] === 'function') {
				if (loggingDebug) {
					console.log('calling:' + controller + '.' + action);
				}
				this[controller][action](options);
			}
		},
		common : {
			init : function(){

				// update user status and cart count
				global[namespace].profileController.getProfileStatus();

				// reset profile cookie on logout
				$('.sign-out-link').click(function(){
					global[namespace].profileController.clearProfileCookie();
				});

				// don't submit empty search
				$('#search-desktop').on('submit', function(e){
					if ($('#Ntt').val() === '') {
						e.preventDefault();
					}
				});

				$('#search-mobile').on('submit', function(e){
					if ($('#Ntt-mobile').val() === '') {
						e.preventDefault();
					}
				});

				// icon-close button clicks
				$body.on('click', '.icon-close', function(){
					$($(this).data('target')).slideUp(200);
				});

				// print button clicks
				$body.on('click', '.print', function(){
					window.print();
				});

				// footer accordions on small screen
				function toggleFooterMenus(){
					$('.footer-links-group h3').each(function(){
						var $this = $(this);

						if ($window.width() < global[namespace].config.mediumMin) {
							$this.siblings('ul').hide();
							$this.removeClass('active');
						}
						else {
							$this.siblings('ul').removeAttr('style');
							$this.removeClass('active');						}
					});
				}

				toggleFooterMenus();
				$window.resize($.throttle(250, toggleFooterMenus));

				$('.footer-links-group').on('click', 'h3', function(e){
					var $this = $(this);
					if ($window.width() < global[namespace].config.mediumMin) {
						$this.siblings('ul').slideToggle();
						$this.toggleClass('active');
					}
				});

				// dynamically add/handle vimeo thumbnails
				if (typeof Vimeo !== 'undefined') {
					function loadVimeoThumbnail($this) {
						var id = $this.data('vimeo-id');
						$.ajax({
							url: 'https://vimeo.com/api/v2/video/' + id + '.json',
							dataType: 'json',
							cache: false,
							success: function (data) {
								$this.html('<img src="' + data[0].thumbnail_medium + '" alt="Vimeo Thumbnail"/>');
								setTimeout(function() {
									$this.addClass('loaded');
								}, 50);
							},
							error: function () {
								$this.remove();
							}
						});
					}
					$('.vimeo-thumb').each(function() {
						loadVimeoThumbnail($(this));
					});
					$('.vimeo-modal').on('click', function(evt) {
						evt.preventDefault();
						evt.stopPropagation();
						// prevent modal from launching if click event
						// is actually the PDP slick carousel being swiped
						var $thumbs = $('.viewer-thumbnails');
						var slickObj = $thumbs.length && $thumbs[0].slick || {};
						var slidesToShow = slickObj.options && slickObj.options.slidesToShow || 1;
						var isSwipable = slickObj.slideCount > slidesToShow;
						var touchObj = slickObj && slickObj.touchObject || {};
						if (isSwipable && typeof touchObj.curX === 'undefined' && typeof touchObj.curY === 'undefined') {
							return;
						}
						var id = $(this).data('vimeo-id');
						var $modalTarget = document.getElementById('vimeo-modal') ? $('#vimeo-modal') : global[namespace].utilities.createModal('vimeo-modal');
						$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/vimeo.jsp?id=' + id });
					});
					$(document).on('hidden.modal', '#vimeo-modal', function() {
						// prevent audio from playing in the
						// background once user closes the modal
						$('#vimeo-modal-iframe').remove();
					});
					
				}

				// bopis - ship my order (pdp, cart, checkout)
				var shipMyOrderOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {

								// update header item counter
								global[namespace].profileController.getProfileStatus(true);

								if ($body.hasClass('checkout')) {
									return window.location = CONSTANTS.contextPath + '/checkout/cart.jsp';
								}

								// strip this query parameter, as it tends to override
								// the default functionality of the page, which is technically
								// incorrect once the user chooses to ship the order instead.
								var gStoreId = global[namespace].utilities.getStoreIdURLParam();
								if (gStoreId !== '') {
									if (window.location.pathname.indexOf('/store/detail/') === 0) {
										// ex: /store/detail/product-name/product-id/{PARAM}
										// 1.) remove last character if it is a slash
										// 2.) remove gStoreId if it is at the very end of the URL
										var path = window.location.pathname;
										if (path.substr(-1) === '/') {
											path = path.substr(0, path.length - 1);
										}
										if (path.substr(gStoreId.length * -1) === gStoreId) {
											path = path.replace('/' + gStoreId, '');
										}
										else {
											path = path.replace('/' + gStoreId + '/', '/');
										}
										path = path.replace('/store/detail/', '/detail/');
										return window.location.pathname = path;
									}
									else {
										// ex: /detail/product-name/product-id?gStoreId={PARAM}
										var search = window.location.search;
										search = search.replace('gStoreId=' + gStoreId, '');
										search = search.replace('?&', '?');
										return window.location.search = search;
									}
								}
								return window.location.reload();
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
				$body.on('click', '.ship-my-order', function(e){
					e.preventDefault();
					$('#ship-my-order-form').ajaxSubmit(shipMyOrderOptions);
				});

				var updateHomeStore = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						global[namespace].utilities.hideLoader();
						$('.home-store-menu').hide();
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $modalTarget = document.getElementById('update-store-modal') ? $('#update-store-modal') : global[namespace].utilities.createModal('update-store-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/homeStoresModal.jsp'});
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.form.ajaxError(xhr, statusText, "update home store:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "update home store: " + exception, $form);
					}
				};
				var makeToHomeStore = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						global[namespace].utilities.hideLoader();
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								// update header item counter
								//global[namespace].profileController.getProfileStatus(true);
								var pageName = $('body').data('action');

								$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, responseText));
								$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, responseText));
								$('.home-store-menu').hide();

								if(pageName === 'product'){
									//$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisLocationInfoTemplate, responseText));
									$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText));
									if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
										$('.bopis-location-info').removeClass('hide');
									}
									$('#bopis-store-id').val(responseText.locationId);
									$('#bopis-zip-inventory').val(responseText.postalCode);
								}

								if(pageName === 'storeLocator'){
									$.ajax({
										url: CONSTANTS.contextPath + '/sitewide/json/storeLocationJson.jsp',
										dataType: 'json',
										cache: false,
										success: function (data) {
											console.log(data);
											var mffLocations = data.locations,
											numLocations = mffLocations.length,
											//templateStoreNav = '{{#locations}}<div class="store-card"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{storeIndex}}.&nbsp;{{name}}</strong></br><span class="store-open-info">Open until 9pm</span></br>{{address}}</br>{{city}}, {{state}} {{zip}}</br>{{phone}}</div>{{#isHomeStore}}<div class="home-this-store" id="{{locationId}}" data-store-id="{{locationId}}"><span class="icon icon-locator" aria-hidden="true"></span> My Store </div>{{/isHomeStore}}{{^isHomeStore}}<div class="make-this-store" data-store-id="{{locationId}}">Make This My Store</div>{{/isHomeStore}}<div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}">store details</a></div><hr class="divider"></div>{{/locations}}';
											templateStoreNav = '{{#locations}}<div class="store-card"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{storeIndex}}.&nbsp;{{name}}</strong></br>{{#storeClosingTime}}<span class="store-open-info">{{storeClosingTime}}</span>{{/storeClosingTime}}</br>{{address}}</br>{{city}}, {{state}} {{zip}}</br>{{^isComingSoon}}<span class="coming-store">{{phone}}{{/isComingSoon}}</span>{{#isComingSoon}}{{phone}}{{/isComingSoon}}</div>{{#isComingSoon}}{{#isHomeStore}}<div class="home-this-store" id="{{locationId}}" data-store-id="{{locationId}}"><span class="icon icon-locator" aria-hidden="true"></span> My Store </div>{{/isHomeStore}}{{^isHomeStore}}<div class="make-this-store" data-store-id="{{locationId}}">Make This My Store</div>{{/isHomeStore}}{{/isComingSoon}}<div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}" class="button expand dark">store details</a></div><hr class="divider"></div>{{/locations}}';
											// render store locations
											$('.store-location-results').html(Mustache.render(templateStoreNav, data));
											$('.home-this-store').trigger('click');
										},
										error: function () {
											console.log("error");
										}
									});
								}

								if(pageName === 'storeDetail'){
									var templateStoreDet = '<button class="button expand primary-dark"><span class="icon icon-locator"></span>&nbsp;MY STORE</button>';
									$('.home-store-section').html(Mustache.render(templateStoreDet, responseText));
								}
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "change home store: " + exception, $form);
					}
				};

				$('body').on('click','.make-this-store', function(e){
					e.preventDefault();
					$('#homestore').val($(this).data('store-id'));
					var pageName = $('body').data('action'),
						label = $(this).text(),
						action = window.location.href;
					if(pageName === 'storeLocator' ||pageName === 'storeDetail'){
						if(KP.analytics){
							KP.analytics.trackEvent("Store Detail Button","Store Locator", label, action);
						}
					}
					$('#home-store-form').ajaxSubmit(makeToHomeStore);
				});

				var validateProductOptions = function(prodId) {
					var $container = $('#product-' + prodId);
					var $skuPicker = $container.find('.product-form-pickers');
					var $selectors = $('div.product-options', $skuPicker);
					$selectors.each(function(i) {
						var $selector = $(this),
								variantId,
								variantName,
								variantTypes,
								errorMessage = '',
								x=0,
								max;
						if ($selector.find('.active').length === 0) {
							variantId = $selector.attr('data-typeid');
							variantTypes = KP_PRODUCT[product].variantTypes;
							max = variantTypes.length;
							for (x; x < max; x++) {
								if (variantTypes[x].id == variantId) {
									variantName = variantTypes[x].displayName;
									if (variantName !== '') {
										errorMessage = 'Please select a ' + variantName;
									}
									else {
										errorMessage = 'Please select all options';
									}
								}
							}
							$selector.find('.product-option-errors').html(errorMessage);
						}
					});
				}

				var openBopisStoreModal = function($thisLink) {
					if ($('.product-pickers').hasClass('table-picker')) {
						$('.table-details').removeClass('active-sku');
						$thisLink.parents('.table-details').addClass('active-sku');
						var prodId = $thisLink.parents('.table-details').find('.table-product-id').val();
						var skuId = $thisLink.parents('.table-details').find('.table-sku-id').val();
					} else {
						var prodId = $('#productId').val().trim();
						var skuId = $('#catalogRefIds').val().trim();
						if (skuId === '') {
							validateProductOptions(prodId);
							return false;
						}
					}
					var storeid = $('#bopis-store-id').val() || null;
					var modalUrl = CONSTANTS.contextPath + '/browse/ajax/bopisStoreModal.jsp?skuId=' + skuId + (storeid ? '&bopisStore=' + storeid : '');
					var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
					$modalTarget.modal({'url': modalUrl});
				}

				$('body').on('click', '.select-store', function(e) {
					// select bopis option and attempt to add to cart
					// in order to bring up the find in-store modal
					if ($('.product-pickers').hasClass('table-picker')) {
						var $parent = $(this).parents('.add-to-cart-actions');
						$parent.find('.bopis-order').prop('checked', true);
						$parent.find('.add-to-cart-submit').trigger('click');
					} else {
						$('#bopis-order').prop('checked', true);
						$('.add-to-cart-submit').trigger('click');
					}
				});

				// bopis - change store
				$('body').on('click', '.change-store', function(e) {
					e.preventDefault();

					var pageName = $('body').data('action');
					$('#bopis-change-store').val('true');
					$('#bopis-from-product').val('false');

					if (pageName === 'product') {
						openBopisStoreModal($(this));
					} else {
						$('#bopis-from-product-inventory').val('false');
						var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
						$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisModal.jsp'});
					}
				});

				// bopis - update store
				$('body').on('click', '.update-bopis-store', function(e) {
					e.preventDefault();
					$('#bopis-change-store').val('false');
					$('#bopis-from-product').val('false');
					openBopisStoreModal($(this));
				});

				// bopis - change store
				$('body').on('click', '.update-store', function(e) {
					e.preventDefault();
					if ($(window).width() > global[namespace].config.smallMax) {
						var page = $('body').attr('class');
					} else {
						var page = 'mobile';
					}
					var $modalTarget = document.getElementById('update-store-modal') ? $('#update-store-modal') : global[namespace].utilities.createModal('update-store-modal', 'small');
					$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/homeStoresModal.jsp?pageType='+page});
				});


				$('body').on('click','.home-store-toggle', function(e){
					e.preventDefault();
					$('.home-store-menu').toggle();
					if ($(window).width() < global[namespace].config.smallMax) {
						$('.off-canvas-wrap').addClass('offcanvas-overlap');
					}
				});

				//update change my store locations
				$('#update-search-form').ajaxForm(updateHomeStore);

				// small screen - turn global promotions into slick slider
				function initGlobalPromo(){
					if ($window.width() > global[namespace].config.smallMax) {
						// destroy slick slider
						if ($('.promo-bar-three').hasClass('slick-initialized')) {
							$('.promo-bar-three').slick('unslick');
						}
					}
					else {
						$('.promo-bar-three,.promo-bar-two').slick({
							dots: false,
							arrows: false,
							infinite: false,
							slidesToShow: 1,
							slidesToScroll: 1,
							autoplay: true,
							autoplaySpeed: 2000,
							fade: true,
							cssEase: 'linear'
						});
					}
				}
				initGlobalPromo();
				$window.resize($.throttle(250, initGlobalPromo));

				// hero slider
				$('.hero-slider:not(.hero-slider-with-promo)').slick({
					infinite: false,
					speed: 300,
					slidesToShow: 1,
					slidesToScroll: 1,
					infinite: true,
					dots: true,
					arrows: true,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								arrows: true
							}
						}
					]
				});

				// hero slider with promo stack
				$('.hero-slider-with-promo').slick({
					infinite: false,
					speed: 300,
					slidesToShow: 1,
					slidesToScroll: 1,
					infinite: true,
					dots: true,
					arrows: true,
					responsive: [
						{
							breakpoint: global[namespace].config.mediumMin,
							settings: {
								arrows: true
							}
						}
					]
				});

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
				
				function escapeSpecialCharInSearchTerm($obj, type){
					 var searchInput = $obj.find('.keyword-search-field'),
					 	isEscapeSpecialChars = searchInput.attr('data-escape-special-char');
					 if(isEscapeSpecialChars === 'true'){
				    	 var searchTerm = searchInput.val().trim();
				      	//console.log('before searchTerm : ' + searchTerm);
				    	 searchTerm = searchTerm.replace(/[^a-z0-9 ]/gi,' ');
				      	//console.log('after searchTerm : ' + searchTerm);
				    	 $obj.find('.keyword-search-field').val(searchTerm);
				     }
					 $obj.submit();
					 if (KP.analytics) {
				    	  KP.analytics.trackEvent("Site Search","Site Search", type, searchTerm);
				      }
				}
				
				$(".keyword-search-field").on('keydown', function(e){
					var keycode = (e.keyCode ? e.keyCode : e.which);
					// enter triggers submit
					if (keycode == 13) {
						e.preventDefault();
						e.stopPropagation();
						 var currentObj = $(this).parents('.keyword-search-form');
						escapeSpecialCharInSearchTerm(currentObj,"Enter");
					}
				});
				
				$('body').on('click','.keyword-search-button',function(e){
				      e.preventDefault();
				      var currentObj = $(this).parents('.keyword-search-form');
				      //var searchInput = $this.find('.keyword-search-field');
				      //var isEscapeSpecialChars = searchInput.attr('data-escape-special-char');
				      //console.log('isEscapeSpecialChars : ' + isEscapeSpecialChars);
				      //if(isEscapeSpecialChars === 'true'){
				      //	var searchTerm = searchInput.val().trim();
				      	//console.log('before searchTerm : ' + searchTerm);
				      //	searchTerm = searchTerm.replace(/[^a-z0-9 ]/gi,' ');
				      	//console.log('after searchTerm : ' + searchTerm);
				     // 	$this.find('.keyword-search-field').val(searchTerm);
				      //}
				     // $this.submit();
				      escapeSpecialCharInSearchTerm(currentObj,e.type);
			    });
				
			}
		},
		home : {
			init: function () {

				// update user status on sign out
				var url = location.href;
				if (UTILITIES.getURLParameter(url, 'DPSLogout') == 'true') {
					global[namespace].profileController.getProfileStatus(true);
				}

				// listrak email signup
				var $emailSignupForm = $('.email-signup-cartridge-form');
				$emailSignupForm.on('submit', function(e){
					e.preventDefault();
					var formid = this.getAttribute('id');
					if (UTILITIES.form.validate($('#'+formid))) {
						if (typeof _ltk !== 'undefined') {
							_ltk.SCA.CaptureEmail('email-cartridge');
						}

						// email signup success modal
						var $modalTarget = document.getElementById('email-signup-success-modal') ? $('#email-signup-success-modal') : global[namespace].utilities.createModal('email-signup-success-modal', 'x-small'),
							url = CONSTANTS.contextPath + '/sitewide/ajax/emailSignupSuccessModal.jsp',
							option = {'url': url};
						$modalTarget.modal(option);

						// clear email form
						$('#email-signup-success-modal').on('hide.modal', function(){
							var $emailCartridgeInput = $('#'+formid).find('#email-cartridge');
							if ($emailCartridgeInput.length > 0) {
								$emailCartridgeInput.val('');
							}
						});
						if(KP.analytics){
							KP.analytics.trackEvent('Email Sign Up Middle', 'Email Sign Up', "Email Sign Up", url);
						}
					}
				});

				// for storeDetails mobile nav
				var $leftCol = $('.two-column-left');
				$('.hide-sidebar, .icon-close').on('click', function(){
					$leftCol.hide();
				});
				$('.show-sidebar').on('click', function(){
					$leftCol.show();
				});
				
				if (KP.analytics) {
					KP.analytics.sendHomePageEvents();
				}
			},
			changePassword : function() {
				$('#change-password-form').ajaxForm(basicAjaxOptions);
			},
			giftCardBalance : function() {
				var gcBalanceOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
						if(KP.analytics){
							KP.analytics.trackEvent('CTA Button', 'Button', $form.find('.button').val(), $form.find('#gift-card-error-url').val());
						}
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();
								var $modalTarget = document.getElementById('gc-balance-modal') ? $('#gc-balance-modal') : global[namespace].utilities.createModal('gc-balance-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/sitewide/ajax/gcBalanceModal.jsp?n=' + responseText.number + '&b=' + responseText.balance});
							}
							else {
								grecaptcha.reset();
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
				$('#check-balance-form').ajaxForm(gcBalanceOptions);

			},
			storeLocator : function() {

				// function to initialize google map click listeners
				function bindInfoWindow(marker, map, infowindow, html) {
					google.maps.event.addListener(marker, 'click', function() {
						infowindow.setContent(html);
						infowindow.open(map, marker);
					});
				}
				
			 // Sets the map on all markers in the array.
		      function setMapOnAll(map) {
		        for (var i = 0; i < markers.length; i++) {
		          markers[i].setMap(map);
		        }
		      }

		      // Removes the markers from the map, but keeps them in the array.
		      function clearMarkers() {
		        setMapOnAll(null);
		      }


				// initialize the google map

				function initMap() {

					var map = new google.maps.Map(document.getElementById('map'), {
								mapTypeControlOptions: {
									mapTypeIds: [] // remove the map type selector
								},
								streetViewControl: false
							}),
							bounds = new google.maps.LatLngBounds(),
							infowindow = new google.maps.InfoWindow(),
							icon = CONSTANTS.contextPath + '/resources/images/location-pin.png',
							markers = [];

					// add all the store markers to the map
					for (var i=0; i<numLocations; i++) {
						var location = mffLocations[i];

						// create marker
						markers[i] = new google.maps.Marker({
							position: new google.maps.LatLng(location.lat, location.lng),
							icon: icon,
							map: map
						});

						// display the map so it shows all the markers at once
						bounds.extend(markers[i].position);
						if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
							 var extendPoint1 = new google.maps.LatLng(bounds.getNorthEast().lat() + 0.01, bounds.getNorthEast().lng() + 0.01);
							 var extendPoint2 = new google.maps.LatLng(bounds.getNorthEast().lat() - 0.01, bounds.getNorthEast().lng() - 0.01);
							 bounds.extend(extendPoint1);
							 bounds.extend(extendPoint2);
						}
						map.fitBounds(bounds);

						// initialize click listeners
						bindInfoWindow(markers[i], map, infowindow, Mustache.render(templateInfoWindow, {store: location}));
					}

					// show marker when you click a store
					$('body').on('click','.location-details', function() {
						for (var i = 0; i < numLocations; i++){
							if (this.id == mffLocations[i].locationId) {
								var storeHours = mffLocations[i].storeHours,
										gasMartHours = mffLocations[i].gasMartHours,
										serviceCenterHours = mffLocations[i].serviceCenterHours,
										selectedMarker = markers[i];

								// show store info and center on map
								new google.maps.event.trigger(selectedMarker, 'click');
								map.setCenter(selectedMarker.getPosition());
								map.setZoom(13);

								//not required for new design
								//parseStoreHours(storeHours, gasMartHours, serviceCenterHours);

								break;
							}
						}
					});

					// show window when you click a home store
					$('body').on('click','.home-this-store', function() {
						for (var i = 0; i < numLocations; i++){
							if (this.id == mffLocations[i].locationId) {
								var selectedMarker = markers[i];

								// show store info and center on map
								new google.maps.event.trigger(selectedMarker, 'click');
								break;
							}
						}
					});

					//default home store info
					for (var i = 0; i < numLocations; i++){
						if (mffLocations[i].isHomeStore === "true") {
							var selectedMarker = markers[i];

							// show store info and center on map
							new google.maps.event.trigger(selectedMarker, 'click');
							break;
						}
					}


					// hide info window when you click the map
					google.maps.event.addListener(map, 'click', function() {
						infowindow.close();
					});

					// trigger click on locationId if in url
					var locationId = UTILITIES.getURLParameter(window.location.href, 'locationId') || '';
					if (locationId !== '') {
						for (var i = 0; i < numLocations; i++){
							if (locationId == mffLocations[i].locationId) {
								var storeHours = mffLocations[i].storeHours,
										gasMartHours = mffLocations[i].gasMartHours,
										serviceCenterHours = mffLocations[i].serviceCenterHours,
										selectedMarker = markers[i];

								// show store info and center on map
								google.maps.event.addListenerOnce(map, 'idle', function(){ // if map becomes idle before click trigger
									new google.maps.event.trigger(selectedMarker, 'click');
								});
								map.setCenter(selectedMarker.getPosition());
								map.setZoom(13);

								parseStoreHours(storeHours, gasMartHours, serviceCenterHours);

								break;
							}
						}
					}
					
					var storeDetailsList = {
						dataType : 'json',
						beforeSubmit : function(arr, $form, options) {
							global[namespace].utilities.form.hideErrors($form);
							global[namespace].utilities.showLoader();
						},
						success: function(responseText, statusText, xhr, $form) {
							if (statusText == 'success') {
								if (responseText.success == 'true') {
									global[namespace].utilities.hideLoader();
									// render store locations
									$('.store-location-results').html(Mustache.render(templateStoreNav, responseText));
									updateMap(responseText);
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
					}
					
					function updateMap(responseText) {
						for (var i=0; i<numLocations; i++) {
							markers[i].setMap(null);
						}
						mffLocations = responseText.locations;
						numLocations = responseText.locations.length;
						// add all the store markers to the map
						for (var i=0; i<responseText.locations.length; i++) {
							var location = responseText.locations[i];

							// create marker
							markers[i] = new google.maps.Marker({
								position: new google.maps.LatLng(location.lat, location.lng),
								icon: icon,
								map: map
							});

							// display the map so it shows all the markers at once
							bounds.extend(markers[i].position);
							if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
								 var extendPoint1 = new google.maps.LatLng(bounds.getNorthEast().lat() + 0.01, bounds.getNorthEast().lng() + 0.01);
								 var extendPoint2 = new google.maps.LatLng(bounds.getNorthEast().lat() - 0.01, bounds.getNorthEast().lng() - 0.01);
								 bounds.extend(extendPoint1);
								 bounds.extend(extendPoint2);
							}
							map.fitBounds(bounds);

							// initialize click listeners
							bindInfoWindow(markers[i], map, infowindow, Mustache.render(templateInfoWindow, {store: location}));
						}
					}
					function fetchAllMatchingLocationIdStores(locationId){
						var resultStores=new Array();
						for (var i=0; i<numLocations; i++) {
							if (mffLocations[i].locationId == locationId){
								resultStores.push(mffLocations[i]);
							}
						}
						return resultStores;
					}
					function fetchAllMatchingStateCodeStores(stateCode){
						var resultStores=new Array();
						for (var i=0; i<numLocations; i++) {
							if (mffLocations[i].state.toLowerCase() == stateCode){
								resultStores.push(mffLocations[i]);
							}
						}
						return resultStores;
					}
					function fetchAllMatchingStateFullNameStores(state){
						var resultStores=new Array();
						for (var i=0; i<numLocations; i++) {
							if (mffLocations[i].stateFullName.toLowerCase() == state){
								resultStores.push(mffLocations[i]);
							}
						}
						return resultStores;
					}
					function fetchAllMatchingCityStores(city){
						var resultStores=new Array();
						for (var i=0; i<numLocations; i++) {
							if (mffLocations[i].city.toLowerCase() == city){
								resultStores.push(mffLocations[i]);
							}
						}
						return resultStores;
					}
					
					//
					//$('body').on("keyup",'#store-details-list-form', function(event) {
					$("#store-details-list-form").on('keydown', function(e){
						var keycode = (e.keyCode ? e.keyCode : e.which);
						// enter triggers submit
						if (keycode == 13) {
							e.preventDefault();
							e.stopPropagation();
							$('.store-search-button').trigger('click');
						}
					});
					
					// show marker when you click a store
					$('.store-search-button').on('click', function(e){
						e.preventDefault();
						mffLocations = KP_STORES.locations;
						numLocations = mffLocations.length;
						global[namespace].utilities.form.hideErrors($('#store-details-list-form'));
						var queryString = $('#store-locator-zip').val().toLowerCase();
						var resultStores=new Array();
						if (queryString === '') {
							// render all store locations
							$('.store-location-results').html(Mustache.render(templateStoreAllNav, KP_STORES));
							initMap();
						}else{
							if(!isNaN(queryString)){
								resultStores=fetchAllMatchingLocationIdStores(queryString);
								if(resultStores.length==0){
									$.ajax({
										url: CONSTANTS.contextPath + '/sitewide/json/storeLocationJson.jsp?zipcode='+queryString,
										dataType: 'json',
										cache: false,
										success: function (responseText) {
											if(responseText.locations.length==0){
												var responseText = {
														'errorMessages':['There are currently no stores in your area. Please try a different City, State, Store or Zip code.'],
														'InlineFormErrorSupport':"false"
														};
												global[namespace].utilities.form.showErrors($('#store-details-list-form'), responseText);
											}else{
												$('.store-location-results').html(Mustache.render(templateStoreNav, responseText));
												updateMap(responseText);
											}
										},
										error: function () {
											console.log("error");
											var responseText = {
													'errorMessages':['There are currently no stores in your area. Please try a different City, State, Store or Zip code.'],
													'InlineFormErrorSupport':"false"
													};
											global[namespace].utilities.form.showErrors($('#store-details-list-form'), responseText);
										}
									});
									return false;
								}
								
							}else{
								if(queryString.length==2){
									resultStores=fetchAllMatchingStateCodeStores(queryString);
								}else{
									resultStores = fetchAllMatchingStateFullNameStores(queryString);
									if(resultStores.length==0){
										resultStores=fetchAllMatchingCityStores(queryString);
									}
									
								}
								
							}
							if(resultStores.length>0){
								for(var i=0;i<resultStores.length;i++){
									resultStores[i].storeIndex=i+1;
								}
								var responseText = {"locations":resultStores};
								$('.store-location-results').html(Mustache.render(templateStoreAllNav, responseText));
								updateMap(responseText);
							}else{
								//$('.store-location-results').empty();
								var responseText = {
										'errorMessages':['There are currently no stores in your area. Please try a different City, State, Store or Zip code.'],
										'InlineFormErrorSupport':"false"
										};
								global[namespace].utilities.form.showErrors($('#store-details-list-form'), responseText);
							}
						}
						
						
						/*if (queryString === '') {
							$('.store-card').removeClass('hide');
						}
						else {
							for (var i=0; i<numLocations; i++) {
								var $store = $('#' + mffLocations[i].locationId);
								
								
								if (mffLocations[i].stateFullName.toLowerCase() == state || mffLocations[i].state.toLowerCase() == state || mffLocations[i].city.toLowerCase() == state || mffLocations[i].zip == state || mffLocations[i].locationId == state) {
									$store.parent().removeClass('hide');
									$('#store-locator-zip').val(state);
									$('.store-card').removeClass('hide');
									
									//var storeHours = mffLocations[i].storeHours,
									//gasMartHours = mffLocations[i].gasMartHours,
									//serviceCenterHours = mffLocations[i].serviceCenterHours,
									//selectedMarker = markers[i];

									// show store info and center on map
									//new google.maps.event.trigger(selectedMarker, 'click');
									//map.setCenter(selectedMarker.getPosition());
									//map.setZoom(13);
			
									//parseStoreHours(storeHours, gasMartHours, serviceCenterHours);
									
									// show marker when you click a store
									$('#store-details-list-form').ajaxSubmit(storeDetailsList);
								}
							}
						}*/
					});
				};
				

				var mffLocations = KP_STORES.locations,
						numLocations = mffLocations.length,
						templateStoreNav = '{{#locations}}<div class="store-card"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{storeIndex}}.&nbsp;{{name}}</strong></br>{{#storeClosingTime}}<span class="store-open-info">{{storeClosingTime}}</span></br>{{/storeClosingTime}}{{address}}<span class="right">{{distance}}&nbsp;mi</span></br>{{city}}, {{state}} {{zip}}</br>{{^isComingSoon}}<span class="coming-store">{{phone}}{{/isComingSoon}}</span>{{#isComingSoon}}{{phone}}{{/isComingSoon}}</div>{{#isComingSoon}}{{#isHomeStore}}<div class="home-this-store" id="{{locationId}}" data-store-id="{{locationId}}"><span class="icon icon-locator" aria-hidden="true"></span> My Store </div>{{/isHomeStore}}{{^isHomeStore}}<div class="make-this-store" data-store-id="{{locationId}}">Make This My Store</div>{{/isHomeStore}}{{/isComingSoon}}<div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}" class="button expand dark">store details</a></div><hr class="divider"></div>{{/locations}}',
						templateStoreAllNav = '{{#locations}}<div class="store-card"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{storeIndex}}.&nbsp;{{name}}</strong></br>{{#storeClosingTime}}<span class="store-open-info">{{storeClosingTime}}</span></br>{{/storeClosingTime}}{{address}}</br>{{city}}, {{state}} {{zip}}</br>{{^isComingSoon}}<span class="coming-store">{{phone}}{{/isComingSoon}}</span>{{#isComingSoon}}{{phone}}{{/isComingSoon}}</div>{{#isComingSoon}}{{#isHomeStore}}<div class="home-this-store" id="{{locationId}}" data-store-id="{{locationId}}"><span class="icon icon-locator" aria-hidden="true"></span> My Store </div>{{/isHomeStore}}{{^isHomeStore}}<div class="make-this-store" data-store-id="{{locationId}}">Make This My Store</div>{{/isHomeStore}}{{/isComingSoon}}<div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}" class="button expand dark">store details</a></div><hr class="divider"></div>{{/locations}}',
						templateInfoWindow = '{{#store}}<div><span class="map-bold">{{name}}</span><br>{{address}}<br>{{city}}, {{state}} {{zip}}<hr>{{phone}}<br><a href="' + CONSTANTS.contextPath  + '{{redirectUrl}}">View store details</a></div>{{/store}}';

				// render store locations
				$('.store-location-results').html(Mustache.render(templateStoreAllNav, KP_STORES));

				function parseStoreHours(storeHours, gasMartHours, serviceCenterHours) {
					var linesStore = storeHours.split(',') || '',
						linesGas = gasMartHours.split(',') || '',
						linesAuto = serviceCenterHours.split(',') || '',
						htmlStore = '',
						htmlGas = '',
						htmlAuto = '';

					function convertMilTime(timeString) {
						var hours24 = parseInt(timeString.substring(0, 2), 10),
							hours = ((hours24 + 11) % 12) + 1,
							amPm = hours24 > 11 ? 'pm' : 'am',
							minutes = timeString.substring(2);
						if (minutes == ':00') {
							minutes = '';
						}
						return hours + minutes + amPm;
					}

					// update store hours
					if (linesStore[0] !== '') {
						for (var j = 0; j < linesStore.length; j++) {
							// convert military to standard
							var timeForDay = linesStore[j].trim().split(' '),
									storeHours = timeForDay[1].split('-'),
									openTime = convertMilTime(storeHours[0]),
									closeTime = convertMilTime(storeHours[1]);
							if (j !== 0) {
								htmlStore += '<br>'
							}
							htmlStore += '<strong>' + timeForDay[0] + ': </strong> ' + openTime + ' - ' + closeTime;
						}
						$('#store-hours-title').removeClass('hide');
						$('#store-hours').html(htmlStore);
					}
					else {
						$('#store-hours-title').addClass('hide');
						$('#store-hours').html('');
					}

					// update gas mart hours
					if (linesGas[0] !== '') {
						for (var j = 0; j < linesGas.length; j++) {
							// convert military to standard
							var timeForDay = linesGas[j].trim().split(' '),
									storeHours = timeForDay[1].split('-'),
									openTime = convertMilTime(storeHours[0]),
									closeTime = convertMilTime(storeHours[1]);
							if (j !== 0) {
								htmlGas += '<br>'
							}
							htmlGas += '<strong>' + timeForDay[0] + ': </strong> ' + openTime + ' - ' + closeTime;
						}
						$('#gas-hours-title').removeClass('hide');
						$('#gas-hours').html(htmlGas);
					}
					else {
						$('#gas-hours-title').addClass('hide');
						$('#gas-hours').html('');
					}

					// update auto service center hours
					if (linesAuto[0] !== '') {
						for (var j = 0; j < linesAuto.length; j++) {
							// convert military to standard
							var timeForDay = linesAuto[j].trim().split(' '),
									storeHours = timeForDay[1].split('-'),
									openTime = convertMilTime(storeHours[0]),
									closeTime = convertMilTime(storeHours[1]);
							if (j !== 0) {
								htmlAuto += '<br>'
							}
							htmlAuto += '<strong>' + timeForDay[0] + ': </strong> ' + openTime + ' - ' + closeTime;
						}
						$('#auto-hours-title').removeClass('hide');
						$('#auto-hours').html(htmlAuto);
					}
					else {
						$('#auto-hours-title').addClass('hide');
						$('#auto-hours').html('');
					}
				}
				
				// show marker when you click a store
				$('#store-locator-state').on('change', function(e){
					e.preventDefault();
					var state = this.value;

					if (state === '') {
						$('.location-details').removeClass('hide');
					}
					else {
						for (var i=0; i<numLocations; i++) {
							var $store = $('#' + mffLocations[i].locationId);
							if (mffLocations[i].state == state) {
								$store.parent().removeClass('hide');
							}
							else {
								$store.parent().addClass('hide');
							}
						}
					}
				});

				// wait for page to load before initializing map
				setTimeout(function(){
					initMap();
				}, 500);

			},
			storeDetail : function() {
				// function to initialize google map click listeners
				function bindInfoWindow(marker, map, infowindow, html) {
					google.maps.event.addListener(marker, 'click', function() {
						infowindow.setContent(html);
						infowindow.open(map, marker);
					});
				}

				// initialize the google map

				function initMap() {

					var map = new google.maps.Map(document.getElementById('map'), {
							mapTypeControlOptions: {
								mapTypeIds: [] // remove the map type selector
							},
							streetViewControl: false
						}),
						bounds = new google.maps.LatLngBounds(),
						infowindow = new google.maps.InfoWindow(),
						icon = CONSTANTS.contextPath + '/resources/images/location-pin.png',
						markers = [];

						// add all the store markers to the map
						for (var i=0; i<numLocations; i++) {
							var location = mffLocations[i];
	
							// create marker
							markers[i] = new google.maps.Marker({
								position: new google.maps.LatLng(location.lat, location.lng),
								icon: icon,
								map: map
							});
	
							// display the map so it shows all the markers at once
							bounds.extend(markers[i].position);
							if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
								 var extendPoint1 = new google.maps.LatLng(bounds.getNorthEast().lat() + 0.01, bounds.getNorthEast().lng() + 0.01);
								 var extendPoint2 = new google.maps.LatLng(bounds.getNorthEast().lat() - 0.01, bounds.getNorthEast().lng() - 0.01);
								 bounds.extend(extendPoint1);
								 bounds.extend(extendPoint2);
							}
							map.fitBounds(bounds);
	
							// initialize click listeners
							bindInfoWindow(markers[i], map, infowindow, Mustache.render(templateInfoWindow, {store: location}));
					};
				}
				
				function removeSelectedStore(allStores,removeStoreId){
					var resultStores = new Array();
					for (var i=0; i<numLocations; i++) {
						if (mffLocations[i].locationId != removeStoreId) {
							resultStores.push(mffLocations[i]);
						}
					}
					return {"locations":resultStores};
				};
				
				var mffLocations = KP_STORES.locations,
				numLocations = mffLocations.length,
				templateStoreNav = '{{#locations}}<li><img alt="" src="/resources/images/location-pin.png" draggable="false"><div class="store-card" style="display: inline-block;"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{name}}</strong></br>{{address}}</br>{{city}}, {{state}} {{zip}}</br><div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}">store details&nbsp></a></div></div></div></li>{{/locations}}',
				templateInfoWindow = '{{#store}}<div><span class="map-bold">{{name}}</span><br>{{address}}<br>{{city}}, {{state}} {{zip}}<hr>{{phone}}<br><a href="' + CONSTANTS.contextPath  + '{{redirectUrl}}">View store details</a></div>{{/store}}';
				var filtered_KP_Stores = removeSelectedStore(KP_STORES,storeId);
				// render store locations
				if(numLocations > 0){
					$('.store-location-list').html(Mustache.render(templateStoreNav, filtered_KP_Stores));
				}else{
					//$('.store-location-list').html("<li>No Find nearest stores.</li>">
				}
				
				// wait for page to load before initializing map
				setTimeout(function(){
					initMap();
				}, 500);
			}
		},
		content : {
			init : function(){
				// mobile sidebar
				var $leftCol = $('.two-column-left');
				$('.hide-sidebar, .icon-close').on('click', function(){
					$leftCol.hide();
				});
				$('.show-sidebar').on('click', function(){
					$leftCol.show();
				});

				// contact us form subject selections
				$('.subject-select').change(function() {
					$('.option-div').hide();
					$('#' + $(this).val()).show();
				});

				// add selected text to subject field
				$('.sub-select').change(function(){
					var value=$(this).find("option:selected").val();
					var text = $(this).find("option:selected").text();
					$('#topic').val(value);
					var area = $('.subject-select').find('option:selected').text();
					if (text !== 'Select a topic'){
						$('.contact-us-subject').val(area + ' - ' + text);
					} else {
						$('.contact-us-subject').val('');
					}
				})


				// careers-center image responsive image mapping
				$('img[usemap]').rwdImageMaps();


				// on Static Content page, only open accordion section relative to the article being displayed
				var windowLoc = window.location.href;
				var n = windowLoc.lastIndexOf('/');
				var articleUrl = windowLoc.substring(n + 1);

				if (articleUrl !== '') {
					$('#staticContentAccordion a[href]').each(function(){
						var accordionData = $(this).attr('href');
						var x = accordionData.lastIndexOf('/');
						var accordionLink = accordionData.substring(x + 1);

						if (accordionLink == articleUrl) {
							// test if nested accordion and add appropriate classes
							if ($(this).parents('.has-submenu').length){
								$(this).parentsUntil('.top-level').siblings('.accordion-title').attr('aria-expanded', 'true').addClass('active');
								$(this).parents('.nest-target').css('display', 'block');
								$(this).closest('li').addClass('accordion-nav-active');
							} else {
								// make sure we don't add the active class to important selectors in the tree
								$(this).parentsUntil('.accordion-container').siblings('.accordion-title').not('.has-submenu .accordion-title, .content-acc-title').attr('aria-expanded', 'true').addClass('active');
								$(this).closest('.accordion-body').css('display', 'block');
								$(this).closest('li').addClass('accordion-nav-active');
							}
						}
					})
				}

			},
			staticContent : function(){
					$('#phone').mask('000-000-0000');

					var contactUsOptions = {
						dataType : 'json',
						beforeSubmit : function(arr, $form, options) {
							global[namespace].utilities.showLoader();
						},
						success: function(responseText, statusText, xhr, $form) {
							// re-apply phone mask
							$('#phone').mask('000-000-0000');

							if (statusText == 'success') {
								if (responseText.success == 'true') {
									global[namespace].utilities.hideLoader();
									var $modalTarget = document.getElementById('contact-us-success-modal') ? $('#contact-us-success-modal') : global[namespace].utilities.createModal('contact-us-success-modal', 'small');
									$modalTarget.modal({'url': CONSTANTS.contextPath + '/content/ajax/contactUsSuccessModal.jsp'});
									KP.analytics.trackEvent('Contact Us', 'Contact Us', 'click', 'submit');
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
					},
					orderTrackingOptions = {
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
				$('#contact-us-form').ajaxForm(contactUsOptions);
				$('#order-tracking-form').ajaxForm(orderTrackingOptions);

				// faq accordions
				var hash = window.location.hash;
				if (hash !== '') {
					var $hash = $(window.location.hash);
					$body.scrollTop($hash.offset().top);
					$hash.accordion('openAll');
				}

			},
			gardenCenter : function(){
				// open all Garden Center accordion sections on page load
				$('#gardenCenterAccordion').accordion('openAll');

				// listrak email signup
				var $emailSignupForm = $('#garden-center-email-signup-form');
				$emailSignupForm.on('submit', function(e){
					e.preventDefault();
					if (UTILITIES.form.validate($emailSignupForm)) {
						if (typeof _ltk !== 'undefined') {
							_ltk.SCA.CaptureEmail('garden-center-email');
						}

						// email signup success modal
						var $modalTarget = document.getElementById('email-signup-success-modal') ? $('#email-signup-success-modal') : global[namespace].utilities.createModal('email-signup-success-modal', 'x-small'),
							url = CONSTANTS.contextPath + '/sitewide/ajax/emailSignupSuccessModal.jsp',
							option = {'url': url};
						$modalTarget.modal(option);

						// clear email form
						$('#email-signup-success-modal').on('hide.modal', function(){
							var $emailCartridgeInput = $('#garden-center-email');
							if ($emailCartridgeInput.length > 0) {
								$emailCartridgeInput.val('');
							}
						});
					}
				});
			}
		},
		modal : {
			init : function(){
				// initialize plugins in case modal was loaded via ajax
				$('[data-tabs]').tabs();
				$('[data-accordion]').accordion();
				$('[data-validate]').validate();
			},
			avsModal : function() {

				var parsedJson = JSON.parse(avsJSON),
						suggested = parsedJson.suggestedAddress,
						entered = parsedJson.enteredAddress,
						$address1 = $('#address'),
						$address2 = $('#address2'),
						$city = $('#city'),
						$state = $('#state'),
						$zip = $('#zip'),
						$skipAVS = $('#skip-avs'),
						$formSubmit = $(parsedJson.submitId),
						$avsModal = $('#avsModal'),
						template = global[namespace].templates.avsTemplate,
						content = Mustache.render(template, parsedJson);
				$('.avs-modal .avs-grid').html(content);
				
				var pageName = $('body').data('action');

				// payment step avs
				if ($('.checkout-progress-payment').hasClass('in-progress')) {
					$address1 = $('#billing-address');
					$address2 = $('#billing-address2');
					$city = $('#billing-city');
					$state = $('#billing-state');
					$zip = $('#billing-zip');
					$skipAVS = $('#billing-skip-avs');
				}

				// reapply phone mask when modal closes
				$body.on('hide.modal', function(){
					var $phone = $('#phone') || '',
							$billingPhone = $('#billing-phone') || '';
					if ($phone !== '') {
						$phone.mask('000-000-0000');
					}
					if ($billingPhone !== '') {
						$billingPhone.mask('000-000-0000');
					}
				});

				// user clicks 'use as entered'
				$('.use-entered').on('click', function(e){
					e.preventDefault();

					// fill in hidden form values
					$address1.val(entered.address1);
					$address2.val(entered.address2);
					$city.val($('<div/>').html(entered.city).text());
					$state.val(entered.state);
					$zip.val(entered.postalCode);

					// skip avs on next submission
					$skipAVS.val(true);

					// submit hidden form
					$avsModal.modal('hide');
					if (KP.analytics && pageName == 'checkout') {
						KP.analytics.sendCheckoutOption("AVS Entered");
					}
					$formSubmit.click();
					
				});

				// user clicks 'use suggested'
				$('.use-suggested').on('click', function(e){
					e.preventDefault();

					// fill in hidden form values
					$address1.val(suggested.address1);
					$address2.val(suggested.address2);
					$city.val(suggested.city);
					$state.val(suggested.state);
					$zip.val(suggested.postalCode);

					// skip avs on next submission
					$skipAVS.val(true);

					// submit hidden form
					$avsModal.modal('hide');
					if (KP.analytics && pageName == 'checkout') {
						KP.analytics.sendCheckoutOption("AVS Suggested");
					}
					$formSubmit.click();
				});
			},
			backInStockModal : function(){
				var backInStockEmailOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $modalTarget = document.getElementById('back-in-stock-confirmation-modal') ? $('#back-in-stock-confirmation-modal') : UTILITIES.createModal('back-in-stock-confirmation-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/backInStockConfirmationModal.jsp'});
								global[namespace].utilities.hideLoader();
								$('#back-in-stock-modal').modal('hide');
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
				$('#back-in-stock-form').ajaxForm(backInStockEmailOptions);
			},
			bopisModal : function() {

				// prevent background from scrolling when modal is open
				// (CSS class works for all devices except iPhone, which needs a JS hack)
				var preventIphoneScroll = function(evt) {
					if ($(evt.target).parents('.scrollbar').length === 0) {
						evt.preventDefault();
						evt.stopPropagation();
						evt.stopImmediatePropagation();
					}
				};
				$('body').addClass('prevent-user-scroll').on('touchmove', preventIphoneScroll);

				// change store link should reload the page
				var changeStore = false;
				var pageName = $('body').data('action');

				if ($('#bopis-change-store').val() == 'true' || global[namespace].utilities.getURLParameter(window.location.href, 'changeStore') == 'true') {
					changeStore = true;
					$('#bopis-change-store').val('false');
				}
				// set form values
				$('#bopis-quantity-modal').val($('#bopis-quantity').val());
				$('#bopis-from-product-modal').val($('#bopis-from-product').val());
				$('#bopis-product-id-modal').val($('#bopis-product-id').val());

				if(pageName === 'product'){
					$('#bopis-from-product-modal').val('true');
					if($('.product-pickers').hasClass('table-picker')){
						$('#bopis-sku-id-modal').val($('.table-details.active-sku').find('.table-sku-id').val());
					}else{
						$('#bopis-sku-id-modal').val($('#catalogRefIds').val());
					}
				}else{
					$('#bopis-sku-id-modal').val($('#bopis-sku-id').val());
				}

				var setResultsListMaxheight = function() {
					var $modal = $('.modal .modal-window');
					var $scrollbar = $modal.find('.scrollbar').css('max-height', '0');
					if (!$scrollbar.length) {
						return;
					}
					var modalHeight = $modal.height();
					var targetHeight = $(window).height() - 10;
					if (matchMedia('(min-width: 768px)').matches) {
						targetHeight = targetHeight - 30;
					}
					if (modalHeight > targetHeight) {
						// failsafe: technically, this should never happen.
						// if it somehow does happen, allow the user to scroll.
						$('body').removeClass('prevent-user-scroll').off('touchmove', preventIphoneScroll);
						$scrollbar.css('max-height', '');
						return;
					}
					$scrollbar.css('max-height', (targetHeight - modalHeight));
				};

				var searchResults = {};
				var $bopisModal = $('#bopis-modal');
				var $bopisResults = $('.bopis-results');
				var $storeResults = $('.store-results');
				var $updateStoreModal = $('#update-store-modal');
				var $bopisStoreModal = $('#bopis-store-modal');
				var $addToCartButton = $('.add-to-cart-submit');
				var $inventoryEmail = $('.no-inventory-email-trigger');

				var bopisSearchOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						searchResults = responseText;
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $bsModal = $('.bopis-modal');
								$bopisResults.html(Mustache.render(global[namespace].templates.bopisSearchTemplate, responseText)).show(0);
								$bsModal.addClass('active');
								$bsModal.find('label').show();
								if(responseText.available === 'true') {
									$bsModal.find('.reserve-msg').removeClass('hide');
								}
								setResultsListMaxheight();
								$bopisModal.modal('reposition');
								global[namespace].utilities.hideLoader();
							} else {
								// pass an errorTemplate in order to display errors after the form (instead of before)
								global[namespace].utilities.form.showErrors($form, responseText, undefined, errorTemplate);
								$bopisResults.empty();
								setResultsListMaxheight();
								$bopisModal.modal('reposition');
								global[namespace].utilities.hideLoader();
							}
						} else {
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

				var storeSearchOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						searchResults = responseText;
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								$storeResults.html(Mustache.render(global[namespace].templates.storeSearchTemplate, responseText)).show(0);
								$updateStoreModal.modal('reposition');
								global[namespace].utilities.hideLoader();
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
								$storeResults.empty();
								$updateStoreModal.modal('reposition');
								global[namespace].utilities.hideLoader();
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

				var bopisStoreSearchOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						searchResults = responseText;
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								var $bsModal = $('.bopis-store-modal');
								$bopisResults.html(Mustache.render(global[namespace].templates.bopisStoreSearchTemplate, responseText)).show(0);
								$bsModal.addClass('active');
								$bsModal.find('label').show();
								$bsModal.find('h2').html('Find Store Availability');
								if(responseText.available === 'true') {
									$bsModal.find('.reserve-msg').removeClass('hide');
								}
								setResultsListMaxheight();
								$bopisModal.modal('reposition');
								global[namespace].utilities.hideLoader();
							}
							else {
								// pass an errorTemplate in order to display errors after the form (instead of before)
								global[namespace].utilities.form.showErrors($form, responseText, undefined, errorTemplate);
								$bopisResults.empty();
								setResultsListMaxheight();
								$bopisModal.modal('reposition');
								global[namespace].utilities.hideLoader();
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

				var addToCartOptions = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						$bopisModal.modal('hide');
						global[namespace].utilities.hideLoader();
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								// change store link should reload the page to get the correct...
								//    cart - max qty
								//    pdp  - add to cart button
								if (changeStore) {
									window.location = window.location.href.split('?')[0];
								}
								else {
									// show bopis store selected
									var selectedStore = {};
									for (var i=0; i<searchResults.stores.length; i++) {
										if (searchResults.stores[i].locationId == $('#bopis-store-id').val()) {
											selectedStore = searchResults.stores[i];
											if (responseText.bopisOnly !== 'true') {
												// if bopisOnly order, don't show the "ship my order instead" link
												selectedStore.bopisOnly = 'false';
											}
											break;
										}
									}

									// updated selected store info
									if ($('.bopis-location-info').length == 0) {
										$('#add-to-cart-form').append('<div class="bopis-location-info"></div>');
									}

									if($('.product-pickers').hasClass('table-picker')){
										$('.bopis-order').attr('checked',true);
										$('.bopis-location-info').removeClass('hide');
										$('.table-picker-details').each(function(index){
											var productId = $('#productId').val().trim(),
												skuId = $('#catalogRefIds-'+index).val().trim(),
												storeId = $('#bopis-store-id').val();
											$.ajax(CONSTANTS.contextPath + '/sitewide/json/updateMyHomeStoreTablePickerSuccess.jsp?productId=' + productId + '&storeId=' + storeId, {
												cache: false,
												dataType : 'json',
												success: function(responseText) {
													$('.table-picker-details').each(function(index){
														$('#bopis-location-info'+index).html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText.skus[index]));
														if(responseText.skus[index].eligible !== "true"){
															$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).addClass('disabled');
															//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
														}else{
															$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).removeClass('disabled disable-add-to-cart');
															//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).addClass('hide');
														}
													})
												},
												error: function() {
													console.log("error on update bopis location info");
												}
											});
										});
									}else{
										$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, selectedStore));
										if(selectedStore.eligible !== "true"){
											$addToCartButton.addClass('disable-add-to-cart');
											//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
										}else{
											$addToCartButton.removeClass('disabled disable-add-to-cart');
											//$inventoryEmail.addClass('hide');
										}
									}

									//$('.utility-home-store').html(Mustache.render(global[namespace].templates.headerStoreTemplate, selectedStore));
									$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, selectedStore));
									$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, selectedStore));
									//$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisLocationInfoTemplate, selectedStore));
									$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, selectedStore));
									$('#bopis-zip-inventory').val(selectedStore.postalCode);
									$('.ship-to-home').addClass('hide');

									// only add to cart if fromProduct == true
									if ($('#bopis-from-product-modal').val() == 'true') {
										// update header item counter
										global[namespace].profileController.getProfileStatus(true);

										// BZ 2523 - If there are validation errors during ship to home
										// and user goes the BOPIS route
										// the previous ship to home errors have to be cleared
										UTILITIES.form.hideErrors($('#add-to-cart-form'));
										// update side cart items
										$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp', function(){
											if ($(window).width() > global[namespace].config.smallMax) {
												$('.desktop-header .side-cart-toggle').click();
											}
											else {
												$('.mobile-header .side-cart-toggle').click();
											}
										});
									}
								}
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);

								var selectedStore = {};
								for (var i=0; i<searchResults.stores.length; i++) {
									if (searchResults.stores[i].locationId == $('#bopis-store-id').val()) {
										selectedStore = searchResults.stores[i];
										if (responseText.bopisOnly !== 'true') {
											// if bopisOnly order, don't show the "ship my order instead" link
											selectedStore.bopisOnly = 'false';
										}
										break;
									}
								}

								// updated selected store info
								if ($('.bopis-location-info').length == 0) {
									$('#add-to-cart-form').append('<div class="bopis-location-info"></div>');
								}

								if($('.product-pickers').hasClass('table-picker')){
									$('.table-picker-details').each(function(index){
										var productId = $('#productId').val().trim(),
											skuId = $('#catalogRefIds-'+index).val().trim(),
											storeId = $('#bopis-store-id').val();
										$.ajax(CONSTANTS.contextPath + '/sitewide/json/updateMyHomeStoreTablePickerSuccess.jsp?productId=' + productId + '&storeId=' + storeId, {
											cache: false,
											dataType : 'json',
											success: function(responseText) {
												$('.table-picker-details').each(function(index){
													$('#bopis-location-info'+index).html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText.skus[index]));
													if(responseText.skus[index].eligible !== "true"){
														$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).addClass('disabled');
														//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
													}else{
														$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).removeClass('disabled disable-add-to-cart');
														//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).addClass('hide');
													}
												})
											},
											error: function() {
												console.log("error on update bopis location info");
											}
										});
									});
								}else{
									$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, selectedStore));
									if(selectedStore.eligible !== "true"){
										$addToCartButton.addClass('disable-add-to-cart');
										//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
									}else{
										$addToCartButton.removeClass('disabled disable-add-to-cart');
										//$inventoryEmail.addClass('hide');
									}
								}

								//$('.utility-home-store').html(Mustache.render(global[namespace].templates.headerStoreTemplate, selectedStore));
								$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, selectedStore));
								$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, selectedStore));
								//$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisLocationInfoTemplate, selectedStore));
								$('#bopis-zip-inventory').val(selectedStore.postalCode);
								$('.ship-to-home').addClass('hide');

							}
						}
						else {
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						$bopisModal.modal('hide');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
					}
				};

				var addToHomeStore = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						$updateStoreModal.modal('hide');
						global[namespace].utilities.hideLoader();
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								// update header item counter
								//global[namespace].profileController.getProfileStatus(true);
								var pageName = $('body').data('action');
								//$('.utility-home-store').html(Mustache.render(global[namespace].templates.headerStoreTemplate, responseText));
								$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, responseText));
								$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, responseText));
								//$('.home-store-menu').hide();
								$('html, body').animate({scrollTop: 0}, 400);

								if(pageName === 'product'){
									if($('.product-pickers').hasClass('table-picker')){
										$('.table-picker-details').each(function(index){
											$('.table-picker-details').each(function(index){
												$('#bopis-location-info'+index).html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText.skus[index]));
												if(responseText.skus[index].eligible !== "true"){
													if($(this).find('#bopis-order'+index).is(':checked')){
														$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).addClass('disabled');
													}
													//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
												}else{
													$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).removeClass('disabled disable-add-to-cart');
													//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).addClass('hide');
												}
											})
										});
									}else{
										$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText));
										if(responseText.eligible !== "true"){
											if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
												$addToCartButton.addClass('disable-add-to-cart');
											}
											//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
										}else{
											$addToCartButton.removeClass('disabled disable-add-to-cart');
											//$inventoryEmail.addClass('hide');
										}
									}


									//if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
									//	$('.bopis-location-info').removeClass('hide');
									//}
									$('#bopis-store-id').val(responseText.locationId);
									$('#bopis-zip-inventory').val(responseText.postalCode);

									//}
								}
								if(pageName === 'account') {
									$('.home-store').html(Mustache.render(global[namespace].templates.accountStoreTemplate, responseText));
								};

								if(pageName === 'storeLocator'){
									$.ajax({
										url: CONSTANTS.contextPath + '/sitewide/json/storeLocationJson.jsp',
										dataType: 'json',
										cache: false,
										success: function (data) {
											var mffLocations = data.locations,
											numLocations = mffLocations.length,
											templateStoreNav = '{{#locations}}<div class="store-card"><div id="{{locationId}}" class="location-details"><strong class="store_{{locationId}}">{{storeIndex}}.&nbsp;{{name}}</strong></br>{{#storeClosingTime}}<span class="store-open-info">{{storeClosingTime}}</span>{{/storeClosingTime}}</br>{{address}}</br>{{city}}, {{state}} {{zip}}</br>{{^isComingSoon}}<span class="coming-store">{{phone}}{{/isComingSoon}}</span>{{#isComingSoon}}{{phone}}{{/isComingSoon}}</div>{{#isComingSoon}}{{#isHomeStore}}<div class="home-this-store" id="{{locationId}}" data-store-id="{{locationId}}"><span class="icon icon-locator" aria-hidden="true"></span> My Store </div>{{/isHomeStore}}{{^isHomeStore}}<div class="make-this-store" data-store-id="{{locationId}}">Make This My Store</div>{{/isHomeStore}}{{/isComingSoon}}<div class="view-store-details" id="{{locationId}}" data-store-id="{{locationId}}"><a href="{{redirectUrl}}" alt="{{name}}" class="button expand dark">store details</a></div><hr class="divider"></div>{{/locations}}';
											// render store locations
											$('.store-location-results').html(Mustache.render(templateStoreNav, data));
											$('.home-this-store').trigger('click');
										},
										error: function () {
											console.log("error");
										}
									});
								};

								if(pageName === 'storeDetail'){
									var redirectURL = responseText.website;
									window.location.href = CONSTANTS.contextPath + redirectURL;
								};

							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						$bopisModal.modal('hide');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
					}
				};

				var changeToBopisStore = {
					dataType: 'json',
					beforeSubmit: function (arr, $form, options) {
						$form.find('.alert-box').remove();
						global[namespace].utilities.showLoader();
					},
					success: function (responseText, statusText, xhr, $form) {
						$bopisModal.modal('hide');
						global[namespace].utilities.hideLoader();
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								if (changeStore) {
									window.location = window.location.href.split('?')[0];
								}else{
									// updated selected store info
									if ($('.bopis-location-info').length == 0) {
										$('#add-to-cart-form').append('<div class="bopis-location-info"></div>');
									}
									$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, responseText));
									$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, responseText));
									$('#bopis-zip-inventory').val(responseText.postalCode);

									if($('.product-pickers').hasClass('table-picker')){
										$('.table-picker-details').each(function(index){
											$('#bopis-location-info'+index).html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText.skus[index]));
											if(responseText.skus[index].eligible !== "true"){
													$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).addClass('disabled');
												//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
											}else{
												$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($addToCartButton).removeClass('disabled disable-add-to-cart');
												//$('#bopis-location-info'+index).parents('.add-to-cart-actions').find($inventoryEmail).addClass('hide');
											}
										})
									}else{
										$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, responseText));
										if(responseText.eligible !== "true"){
											$addToCartButton.addClass('disable-add-to-cart');
											//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + skuId);
										}else{
											$addToCartButton.removeClass('disabled disable-add-to-cart');
											//$inventoryEmail.addClass('hide');
										}
									}
								}
							}
							else {
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.form.ajaxError(xhr, statusText, "update bopis store error:  Missing statusText parameter", $form);
						}
					},
					error: function (xhr, statusText, exception, $form) {
						$bopisModal.modal('hide');
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "update bopis store error: " + exception, $form);
					}
				};

				$('#bopis-search-form').ajaxForm(bopisSearchOptions);
				$('#store-search-form').ajaxForm(storeSearchOptions);
				$('#bopis-store-search-form').ajaxForm(bopisStoreSearchOptions);

				$bopisModal.on('click', '.choose-this-store', function(){
					$('#bopis-store-id').val($(this).data('bopis-store-id'));
					$('#select-bopis-store').ajaxSubmit(addToCartOptions);
				});
				$updateStoreModal.on('click', '.choose-store', function(e){
					e.preventDefault();
					$('#homestore').val($(this).data('store-id'));
					var pageName = $('body').data('action'),
						$this = $(this);
					if(pageName === 'product'){
						if($('.product-pickers').hasClass('table-picker')){
							$('#home-store-successUrl').val(CONSTANTS.contextPath + '/sitewide/json/updateMyHomeStoreTablePickerSuccess.jsp?storeId='+$(this).data('store-id')+'&productId='+$('#productId').val());
						}else{
							$('#home-store-successUrl').val($('#home-store-successUrl').val().split('?')[0]+'?storeId='+$this.data('store-id')+'&productId='+$('#productId').val()+'&skuId='+$('#catalogRefIds').val());
						}
						$('#home-store-form').ajaxSubmit(addToHomeStore);
					}else{
						$('#home-store-form').ajaxSubmit(addToHomeStore);
					}
				});
				$bopisModal.on('click', '.change-this-store', function(e){
					e.preventDefault();
					var $this = $(this);
					$('#bopis-store-id').val($this.data('change-bopis-store-id'));
					$('#select-bopis-store').ajaxSubmit(addToCartOptions);
				});
				$bopisModal.on('hide.modal', function(){
					$('body').removeClass('prevent-user-scroll').off('touchmove', preventIphoneScroll);
					$bopisModal.off('click', '.change-this-store');
					$bopisResults.hide(0);
				});
				$updateStoreModal.on('hide.modal', function(){
					$('body').removeClass('prevent-user-scroll').off('touchmove', preventIphoneScroll);
					$updateStoreModal.off('click', '.choose-store');
					$storeResults.hide(0);
				});
				// function to initialize google map click listeners
				function bindInfoWindow(marker, map, infowindow, html) {
					google.maps.event.addListener(marker, 'click', function() {
						infowindow.setContent(html);
						infowindow.open(map, marker);
					});
				}
				// initialize the google map
				function initMap() {
					var templateInfoWindow = '{{#store}}<div><span class="map-bold">{{name}}</span><br>{{address}}<br>{{city}}, {{state}} {{zip}}<hr>{{phone}}<br><a href="{{redirectUrl}}">View store details</a></div>{{/store}}';
					var storeLoc = {};
					storeLoc['address'] = $('.storeAddress').html();
					storeLoc['city'] = $('.storeCity').html();
					storeLoc['state'] = $(".storeState").html();
					storeLoc['zip'] = $(".storeZip").html();
					storeLoc['phone'] = $(".storePhone").html();
					storeLoc['name'] = $(".storeName").html();
					storeLoc['redirectUrl'] = $(".storeUrl").html();

				var map = new google.maps.Map(document.getElementById('storemap'), {
							mapTypeControlOptions: {
								mapTypeIds: [] // remove the map type selector
							},
							streetViewControl: false
						}),
						bounds = new google.maps.LatLngBounds(),
						infowindow = new google.maps.InfoWindow(),
						icon = '/resources/images/location-pin.png',
						markers = [];

					// create marker
					markers[0] = new google.maps.Marker({
						position: new google.maps.LatLng($(".storeLat").html(), $(".storeLng").html()),
						icon: icon,
						map: map
					});

					// display the map so it shows all the markers at once
					bounds.extend(markers[0].position);
					if (bounds.getNorthEast().equals(bounds.getSouthWest())) {
						 var extendPoint1 = new google.maps.LatLng(bounds.getNorthEast().lat() + 0.01, bounds.getNorthEast().lng() + 0.01);
						 var extendPoint2 = new google.maps.LatLng(bounds.getNorthEast().lat() - 0.01, bounds.getNorthEast().lng() - 0.01);
						 bounds.extend(extendPoint1);
						 bounds.extend(extendPoint2);
					}
					map.fitBounds(bounds);

					// initialize click listeners
					bindInfoWindow(markers[0], map, infowindow, Mustache.render(templateInfoWindow, {store: storeLoc}));
				}

				setTimeout(function(){
					initMap();
				}, 500);
			},
			bopisNotificationModal : function() {

				var $bopisModal = $('#bopis-notification-modal');
				var $bopisResults = $bopisModal.find('.bopis-results');

				// insert results into modal body
				$bopisResults.html(Mustache.render(global[namespace].templates.bopisNotificationTemplate, global[namespace].searchResults)).show(0);
				$('#bopis-notification-modal').modal('reposition');

				var addToCartOptions = {
						dataType: 'json',
						beforeSubmit: function (arr, $form, options) {
							$form.find('.alert-box').remove();
							global[namespace].utilities.showLoader();
						},
						success: function (responseText, statusText, xhr, $form) {
							$bopisModal.modal('hide');
							global[namespace].utilities.hideLoader();
							if (statusText == 'success') {
								if (responseText.success == 'true') {
									// update header item counter
									global[namespace].profileController.getProfileStatus(true);

									// update side cart items
									$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp', function(){
										if ($(window).width() > global[namespace].config.smallMax) {
											$('.desktop-header .side-cart-toggle').click();
										}
										else {
											$('.mobile-header .side-cart-toggle').click();
										}
									});

									// show bopis store selected
									var selectedStore = {},
											searchResults = global[namespace].searchResults;
									for (var i=0; i<searchResults.stores.length; i++) {
										if (searchResults.stores[i].locationId == $('#bopis-store-id').val()) {
											selectedStore = searchResults.stores[i];
											break;
										}
									}
									$('.utility-home-header').html(Mustache.render(global[namespace].templates.storeHeaderTemplate, selectedStore));
									$('.store-location-info').html(Mustache.render(global[namespace].templates.storeBodyTemplate, selectedStore));
									// updated selected store info
									//$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisLocationInfoTemplate, selectedStore));
									$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, selectedStore));
									$('#bopis-zip-inventory').val(selectedStore.postalCode);
									$('#bopis-edit-mode').val(searchResults.editMode);
									$('#bopis-removalIds').val(searchResults.removalCommerceIds);
									$('.ship-to-home').addClass('hide');

									// only add to cart if fromProduct == true
									if ($('#bopis-from-product-modal').val() == 'true') {
										// update header item counter
										global[namespace].profileController.getProfileStatus(true);

										// update side cart items
										$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp', function(){
											if ($(window).width() > global[namespace].config.smallMax) {
												$('.desktop-header .side-cart-toggle').click();
											}
											else {
												$('.mobile-header .side-cart-toggle').click();
											}
										});
									}
								}
								else {
									global[namespace].utilities.form.showErrors($form, responseText);
								}
							}
							else {
								global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:  Missing statusText parameter", $form);
							}
						},
						error: function (xhr, statusText, exception, $form) {
							$bopisModal.modal('hide');
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
						}
					};

				$bopisModal.on('click', '.choose-this-store', function(){
					$('#bopis-store-id').val($(this).data('bopis-store-id'));
					$('.bopis-removalIds').val($('#removalIds').val());
					//$('.bopis-edit-mode').val($('#bopis-edit-mode-inventory').val());
					$('.bopis-edit-mode').val(false);
					$('#select-bopis-store').ajaxSubmit(addToCartOptions);
				});

				$bopisModal.on('hide.modal', function(){
					$bopisResults.hide(0);
				});
			},
			deleteAddressModal : function () {
				var $modal = $('#delete-address-modal'),
					deleteAddressOptions = {
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
									$modal.find('.cancel-button').addClass('expand').html('close');
									$modal.find('.modal-body, .delete-button').remove();
									global[namespace].utilities.form.showErrors($form, responseText);
									$modal.modal('reposition');
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
				$('#delete-address-form').ajaxForm(deleteAddressOptions);
			},
			deletePaymentModal : function () {
				$('#delete-payment-form').ajaxForm(basicAjaxOptions);
			},
			emailProductModal : function(){
				var emailProductOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();
								$('#email-product-modal').modal('hide');
								var $modalTarget = document.getElementById('email-product-confirmation-modal') ? $('#email-product-confirmation-modal') : UTILITIES.createModal('email-product-confirmation-modal', 'small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/emailProductConfirmationModal.jsp'});
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
				$('#email-product-form').ajaxForm(emailProductOptions);
				
				$(".email-product-submit input:submit").on('click', function(){
					KP.analytics.trackEvent('Product Detail', 'Product Detail', "send", window.location.href);
				});
			},
			emailWishListModal : function(){
				var emailWishListOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$('.modal').modal('hide');
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {
								global[namespace].utilities.hideLoader();
								$('#email-product-modal').modal('hide');
								var $modalTarget = document.getElementById('email-wish-list-confirmation-modal') ? $('#email-wish-list-confirmation-modal') : UTILITIES.createModal('email-wish-list-confirmation-modal', 'x-small');
								$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/emailWishListConfirmationModal.jsp'});
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
				$('#email-wish-list-form').ajaxForm(emailWishListOptions);
			},
			fflModal : function () {

				// show item removal message if necessary
				if ($.cookie("user-data")) {
					userData = JSON.parse($.cookie("user-data"));
					cartCount = userData.cartCount;
					if (cartCount > 0) {
						$('.mixed-cart-note').removeClass('hide');
					}
				}

				// add to cart
				var addToCartOptions = {
					dataType : 'json',
					beforeSubmit : function(arr, $form, options) {
						$('.modal').modal('hide');
						global[namespace].utilities.showLoader();
					},
					success: function(responseText, statusText, xhr, $form) {
						if (statusText == 'success') {
							if (responseText.success == 'true') {

								// update header item counter
								global[namespace].profileController.getProfileStatus(true);

								// update side cart items
								$('#side-cart').load(CONSTANTS.contextPath + '/sitewide/sideCart.jsp', function(){
									if ($(window).width() > global[namespace].config.smallMax) {
										$('.desktop-header .side-cart-toggle').click();
									}
									else {
										$('.mobile-header .side-cart-toggle').click();
									}
								});
								global[namespace].utilities.hideLoader();
							}
							else {
								global[namespace].utilities.hideLoader();
								global[namespace].utilities.form.showErrors($form, responseText);
							}
						}
						else {
							global[namespace].utilities.hideLoader();
							global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error:	Missing statusText parameter", $form);
						}
					},
					error: function(xhr, statusText, exception, $form) {
						global[namespace].utilities.hideLoader();
						global[namespace].utilities.form.ajaxError(xhr, statusText, "add to cart error: " + exception, $form);
					}
				};

				$('.add-ffl-to-cart-submit').on('click', function(e){
					e.preventDefault();
					e.stopPropagation();

					var prodId = $('#productId').val().trim(),
							skuId = $('#catalogRefIds').val().trim();

					// make sure there's a sku set
					if (skuId === '') {
						productControllers[prodId].showSelectionErrors();
					}
					else {
						if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
							var $modalTarget = document.getElementById('bopis-modal') ? $('#bopis-modal') : global[namespace].utilities.createModal('bopis-modal', 'small');
							$modalTarget.modal({'url': CONSTANTS.contextPath + '/browse/ajax/bopisModal.jsp'});
						}
						else {
							$('#add-to-cart-form').ajaxSubmit(addToCartOptions);
						}
					}
				});
			},
			gcBalanceModal : function () {
				$('#gc-balance-modal').on('hide.modal', function(){
					global[namespace].utilities.form.hideErrors($('#check-balance-form'));
					$('#gift-card-number').val('');
					$('#gc-pin').val('');
					grecaptcha.reset();
				});
			},
			ltlModal : function () {
				var $ltlModal = $('#ltl-modal');
				$ltlModal.find('.modal-body').html(Mustache.render(global[namespace].templates.ltlTemplate, JSON.parse(ltlJSON)));
				$ltlModal.modal('reposition');
			},
			quickViewModal : function() {

				// adding thumbnail image error event listener
				$('.viewer-thumb-image').error(function(){
					$(this).siblings('.th-image').attr('srcset', CONSTANTS.productImageRoot + '/unavailable/th.jpg');
				});

				// swap images on thumbnail image click
				$('.viewer-thumb-image').on('click', function (e) {
					var $this = $(this),
						imageName = $this.attr('data-image-name'),
						$mainImage = $('.viewer-main-image');

					// check to see if image is already selected
					if ($mainImage.attr('data-image-name') == imageName) {
						return;
					}
					else {
						var path = CONSTANTS.productImageRoot + '/'+ $this.attr('data-id'),
							lPath = path + '/l/' + imageName,
							xlPath = path + '/x/' + imageName,
							zPath = path + '/z/' + imageName;

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

				// open accordion on page load
				$('#product-info-accordion').accordion('openAll');
			},
			sizeChartModal : function () {
				$('img').on('load', function(){
					$('#size-chart-modal').modal('reposition');
				});
			},
			giftCardInfoModal : function () {
				$('img').on('load', function(){
					$('#gift-card-info-modal').modal('reposition');
				});
			},
			loginModal : function() {
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
									$('.modal').modal('hide');
									global[namespace].profileController.getProfileStatus(true);
									$('.wishlist-section').html(Mustache.render(global[namespace].templates.appliedWishList, responseText));

									//window.location = responseText.url;
									$('#wishListId').val(responseText.wishlistId);
									$('#addItemToWishList').ajaxSubmit(addToWishListOptions);
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
				$('#login-form').ajaxForm(profileOptions);
				$('#register-form').ajaxForm(profileOptions);
			}
		}
	};

	$.extend(global[namespace], initFunctions);

	$(document).ready(function () {
		global[namespace].init();
	})

})(this, window.jQuery, "KP");
