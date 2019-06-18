/* ========================
 Google Tag Manager Analytics
 =========================*/

var dataLayer = dataLayer || [];

(function (global, $, digitalData, namespace) {

	"use strict";

var analytics = {},
		viewedPromotions = [];

	function findById(idValue, arr) {
		return findProperty(idValue, arr, "id");
	}
	function findByPid(idValue, arr) {
		return findProperty(idValue, arr, "pid");
	}
	function findProperty(propertyValue, arr, propertyName) {
		if (arr) {
			for (var i=0; i < arr.length; i++) {
				if (arr[i][propertyName] === propertyValue) {
					return i;
				}
			}
		}
		return -1;
	}

	function getCookieConfig(storageType) {
		var cookieConfig = {};
		if (digitalData.checkoutStep === 1 ||
				(storageType && storageType=="cart")) {
			// checkout pages - cart products saved in semi-permanent cookies
			cookieConfig.name = "products";
			cookieConfig.options = {path : '/', expires: 1*365};
		} else {
			// otherwise, products stored only until end of session
			cookieConfig.name = "productsTemp";
			cookieConfig.options = {path : '/', expires: 1};
		}
		return cookieConfig;
	}

	/* ========================
	 getProductCategory()
	 The product category is how the user found the product
	 and is saved in / retrieved from cookie
	 =========================*/
	function getProductCategory(pid, cookieIt) {
		var category, product;
		product = getProductFromStorage(pid, cookieIt);
		if (product) {
			category = product.category;
		}
		return category;
	}
	/* ========================
	 getProductList()
	 The product list is where the user found the product
	 and is saved in / retrieved from cookie
	 =========================*/
	function getProductList(pid, cookieIt) {
		var list, product;
		product = getProductFromStorage(pid, cookieIt);
		if (product) {
			list = product.list;
		}
		return list;
	}
	/* ========================
	 getProductFromStorage()
	 Retrieve the product from cookie.
	 =========================*/
	function getProductFromStorage(pid, cookieConfig) {
		var cookieProducts, product, productIndex;
		pid = pid.toString();
		if (!cookieConfig) {
			cookieConfig = getCookieConfig();
		}
		cookieProducts = decodeURIComponent($.parseJSON($.cookie(cookieConfig.name)));
		productIndex  = findById(pid, cookieProducts);
		if (productIndex > -1) {
			product = cookieProducts[productIndex];
		}
		return product;
	}

	function removeProductFromStorage(pid, cookieConfig) {
		var productIndex, cookieProducts;
		if (!cookieConfig) {
			cookieConfig = getCookieConfig();
		}
		cookieProducts = decodeURIComponent($.parseJSON($.cookie(cookieConfig.name)));
		if (!cookieProducts) {
			return [];
		}
		productIndex  = findById(pid, cookieProducts);
		// replace stored product with latest selection
		if (productIndex > -1) {
			cookieProducts.splice(productIndex,1);
		}
		$.cookies(cookieConfig.name, cookieProducts, cookieConfig.options);
		return cookieProducts;
	}

	/* ========================
	 addProductToStorage(pid, category, list)
	 Store some product data into a cookie. This is for storing browsing data such as category and list that is not
	 available to us in checkout.
	 =========================*/
	function addProductToStorage(productId, category, list, cookieConfig) {
		if (!cookieConfig) {
			cookieConfig = getCookieConfig();
		}
		var cookieProducts = removeProductFromStorage(productId, cookieConfig);
		cookieProducts.push({
			"id": productId,
			"category": encodeURIComponent(category),
			"list": encodeURIComponent(list)
		});
		//housekeeping. Keep a max of 10 products so that cookie doesn't get to big.
		//Really should need 2 but want to cover a little history navigation (browser back/forward)
		if (cookieConfig.name === "productsTemp" && cookieProducts.length > 10) {
			while (cookieConfig.length > 10) {
				cookieConfig.shift();
			}
		}
		$.cookie.set(cookieConfig.name, cookieProducts, cookieConfig.options);
	}

	/* ========================
	 copyProductToCartStorage(productId)
	 used to move cookie data into a longer term cookie.
	 =========================*/
	function copyProductToCartStorage(productId) {
		var category,list, cookieConfig;
		category = getProductCategory(productId);
		list = getProductList(productId);
		cookieConfig = getCookieConfig("cart");
		addProductToStorage(productId, category, list, cookieConfig);
	}

	function sendProductEvent(event, product, action) {
		/*if (!product.category) {
			product.category = getProductCategory(product.id);
		}*/
		/*if (!product.list) {
			product.list = getProductList(product.id);
		}*/
		
		if(event === "removeFromCart"){
			digitalData.events.push({
				event: event,
				ecommerce : {
					'currencyCode': 'USD',
					'remove': {
						'products': [product]
					}
				}
			});
		}else if(event === "addToCart"){
			digitalData.events.push({
				event: event,
				ecommerce : {
					'currencyCode': 'USD',
					'add': {
						'products': [product]
					}
				}
			});
			
			digitalData.events.push({
				event: 'Product Detail',
				eventCategory: 'Product Detail Delivery Method',
				eventAction: $("input[name='order-type']:checked").val(),
				eventLabel : window.location.href
			});
		}else if(event === "productClick"){
			digitalData.events.push({
				event: event,
				ecommerce : {
					'click': {
						'actionField': {'list': product.list},
						'products': [{
							'name': product.name || product.id,
							'id': product.id,
							'price': product.price,
							'brand': product.brand,
							'category': product.category,
							'variant': product.variant,
							'position': product.position
						}]
					}
				},
				eventCallback: function() {
					document.location = action;
				}
			});
		}else {
			digitalData.events.push({
				event: event,
				ecommerce : [product]
			});
		}
		analytics.sendEventsTagManager();
	}

	function initializePromotionData(){

		var promotionLinks = $('[data-promotionname]'),
				$promotionLink,
				isVisible = false;
		if (promotionLinks.length > 0 && !digitalData.promotions) {
			digitalData.promotions = [];
		}

		for (var i = 0; i < promotionLinks.length; i++) {
			$promotionLink = $(promotionLinks[i]);
			$promotionLink.attr('data-promoid', 'promo' + i);
			//items in the mega nav or the flexslider are not considered visible on load, will be tracked using sendPromotionsInContainer
			isVisible = ($promotionLink.parents('#big-menu').length === 0 && $promotionLink.parents('.flexslider').length === 0);
			digitalData.promotions.push({
				id:  'promo' + i,
				name: $promotionLink.data("promotionname"),
				creative: $promotionLink.data("promotionlink"),
				position: $promotionLink.data("promotionposition") + i,
				viewOnLoad: isVisible,
				action: $promotionLink.data("promotionlink")
			});
		}
	}

	analytics.init = function(){
	
		analytics.sendGlobalEvents();
		
		/* Page specific Tags */
		
		if ("pageType" in digitalData.page && (digitalData.page.pageType === "category" || digitalData.page.pageType === "search")) {
			$('.promo-grid-three').on('click', 'a', function(e) {
				var label = $(this).find("strong").text() || "";
				analytics.trackEvent('Product Category Cartridge', 'Product Category', $(this).attr("data-tag-title") || $(".crumb.active").text().trim(), label);
			});
			analytics.sendProductViews();
		}else if("pageType" in digitalData.page && digitalData.page.pageType === "product"){
			analytics.sendProductEvents();
			analytics.sendProductDetailView(digitalData.page.productID);
		}else if("pageType" in digitalData.page && digitalData.page.pageType === "cart"){
			analytics.sendCartEvents();
		}else if("pageType" in digitalData.page && digitalData.page.pageType === "staticContent"){
			analytics.sendStaticPageEvents();
		}else if("pageType" in digitalData.page && digitalData.page.pageType === "storeDetail"){
			analytics.sendStoreDetailPageEvents();
		}else if("pageType" in digitalData.page && digitalData.page.pageType === "storeLocator"){
			analytics.sendStoreLocatorPageEvents();
		}
		
		//listen for promotion clicks.
		$('body').on('click', '[data-promotionname]', function(e) {
			var promoId = this.getAttribute("data-promoid");
			if (promoId) {
				analytics.sendPromotionClick(promoId);
			}
		});
		
		//get the promotion data ready
		initializePromotionData();
		
		//get the promotions we should send immediately
		var visiblePromotions = [];
		if ("promotions" in digitalData) {
			for (var i = 0; i < digitalData.promotions.length; i++) {
				if (digitalData.promotions[i].viewOnLoad === true) {
					visiblePromotions.push(digitalData.promotions[i]);
				}
			}
		}
		
		// send empty keyword search event 
		if ("searchResults" in digitalData.page && digitalData.page.searchResults === 0) {
			analytics.trackEvent("Null Search Result","Null Search Result", "search", digitalData.page.searchTerm);
		}
		
	/* TODO ::::::::::::::::  1) Reassess the need of these lines. 2) Remove unwanted function calls.
	
		if ("order" in digitalData) {
			if (digitalData.checkoutStep === 7) {
				// order completed - remove products  from local storage
				$.cookies.remove('products', { path: '/' });
			}
		}

		// send empty keyword search event 
		if ("searchResults" in digitalData.page && digitalData.page.searchResults === 0) {
			digitalData.events.push({
				event: 'noSearchResults'
			});
		}

		// Store locator and in store availability events
		if ("locatorResults" in digitalData.page) {
			//In store lookup is Ajax in desktop site so tracking is event based.
			if ("pageType" in digitalData.page && digitalData.page.pageType === "InStoreLookup") {
				analytics.sendInStoreLookup('', digitalData.page.locatorZip, digitalData.page.locatorResults);
			} else {
				//Store locator search is not ajax, so only the empty search results is event based.
				if (digitalData.page.locatorResults === 0) {
					digitalData.events.push({
						event: 'noLocationResults'
					});
				}
			}
		}
		// send store directions event 
		if ("locatorStoreId" in digitalData.page) {
			digitalData.events.push({
				event: 'storeDirections'
			});
		}
		// End Store locator and in store availability events
		
		// Listen for promo code removes 
		$("#removeCoupon").on("submit", function(e) {
			var $this = $(this),
					couponId = $this.find('.coupon-id').val();
			analytics.removePromoCode(couponId);
		});

		*/

		analytics.sendPromotionViews(visiblePromotions);
		analytics.sendEventsTagManager();
	};

	analytics.sendEventsTagManager = function() {
		var event;
		if (!digitalData){
			return;
		}
		if (!digitalData.events || digitalData.events.length === 0) {
			return;
		}
		while (digitalData.events.length > 0) {
			event = digitalData.events.splice(0,1);
			dataLayer.push(event[0]);
			console.log(event[0]);
		}
	};

	/* ========================
	 sendPromotionViews()
	 Sends the promotion impression data to google tag manager. Add custom logic in here to remove promotions that are
	 hidden from view when the page loads, these can be sent by sendPromotionsInContainer. This is a cap to how much data
	 can be sent, so this is done in batches.
	 =========================*/
	analytics.sendPromotionViews = function(promotionArray) {
		if (!digitalData.promotions || digitalData.promotions.length === 0 || typeof promotionArray === 'undefined') {
			return;
		}
		var maxToSend = 24,
				promotionsToSend = [];

		for (var i = 0; i < promotionArray.length; i++) {
			if ($.inArray(promotionArray[i].id, viewedPromotions) === -1) {
				viewedPromotions.push(promotionArray[i].id);
				promotionsToSend.push({
					'id': promotionArray[i].id,
					'name': promotionArray[i].name,
					'creative': promotionArray[i].creative,
					'position': promotionArray[i].position
				});
			}
		}
		if (promotionsToSend.length === 0) {
			return;
		}

		while(promotionsToSend.length) {
			digitalData.events.push({
				event: "promotionView",
				eventPromotions: promotionsToSend.splice(0, maxToSend)
			});
		}
		analytics.sendEventsTagManager();
	};

	/* ========================
	 sendPromotionsInContainer(container)
	 Send in promotion data for promotions inside a container. Used for promotions that are hidden initially and not sent
	 in sendPromotionViews. (like promotions in a megamenu)
	 =========================*/
	analytics.sendPromotionsInContainer = function(container){
		if (!digitalData.promotions || digitalData.promotions.length === 0) {
			return;
		}
		var promotionLinks = $('[data-promotionname]', container),
				promotionsToSend = [];
		for (var i = 0; i < promotionLinks.length; i++) {
			var promoId = promotionLinks[i].getAttribute("data-promoid"),
					promoIdx  = findByPid(promoId, digitalData.promotions),
					promotion;
			if (promoIdx > -1){
				promotion = digitalData.promotions[promoIdx];
				promotionsToSend.push(promotion);
			}
		}
		if (promotionsToSend.length === 0) {
			return;
		}
		analytics.sendPromotionViews(promotionsToSend);
	};

	/* ========================
	 sendPromotionClick()
	 Sends the data for a promotion click to Google Tag Manager by looking up the promotion object by the promotionID
	 parameter. This is a cap to how much data can be sent, so this is done in batches.
	 =========================*/
	analytics.sendPromotionClick = function(promoId) {
		var promotionIndex = findById(promoId, digitalData.promotions),
				promotion,
				promotionsToSend = [];
		if (promotionIndex === -1) {
			return;
		}
		promotion = digitalData.promotions[promotionIndex];
		promotionsToSend.push({
			'id': promotion.id,
			'name': promotion.name,
			'creative': promotion.creative,
			'position': promotion.position
		});
		digitalData.events.push({
			event: "promotionClick",
			ecommerce: {
				'promoClick': {
					'promotions':[promotionsToSend]
				}
			},
			eventCallback: function() {
				document.location = promotion.action;
			}
		});
		analytics.sendEventsTagManager();
	};

	/* ========================
	 sendProductViews()
	 Sends the product impression data to google tag manager.
	 First, loops through the digitalData.products array and adds the product objects into a productView event.
	 Optionally caps the number of products in the event in order to keep below the 8k data load cap. Pushes the
	 productView event object into the digitalData.events array to queue up processing. Finally, calls
	 analytics.sendEventsToTagManager to process all the events in the queue. This function should fire only one time on
	 the page, sending all the visible products to Google Tag Manager.
	 =========================*/
	analytics.sendProductViews = function() {
		if (!digitalData.products || digitalData.products.length === 0) {
			return;
		}			
		var maxToSend = 24,
				tmpProds = digitalData.products.slice(0);
		
		while(tmpProds.length) {
			digitalData.events.push({
				event: "productView",
				ecommerce: {
					'currencyCode': 'USD',
					'impressions': [tmpProds.splice(0, maxToSend)]
				}
			});
		}
		analytics.sendEventsTagManager();
	};

	/* ========================
	 sendProductClick(productId)
	 Retrieve the product from digitalData products object and add an entry in temp storage for the product category and
	 list. Send a product click event.
	 =========================*/
	analytics.sendProductClick = function(productId, action) {
		var productIndex, product;
		productIndex  = findById(productId, digitalData.products);
		if (productIndex > -1) {
			product = digitalData.products[productIndex];
			//addProductToStorage(productId, product.category, product.list);
			sendProductEvent("productClick", product, action);
		}
	};

	/* ========================
	 sendRemoveProduct(product)
	 Send a remove from cart event
	 =========================*/
	analytics.sendRemoveProduct = function(productId) {
		var productIndex, product;
		productIndex  = findById(productId, digitalData.order.products);
		if (productIndex > -1) {
			product = digitalData.order.products[productIndex];
			//removeProductFromStorage(productId, "cart");
			sendProductEvent("removeFromCart", product);
		}
	};

	/* ========================
	 sendProductDetailView(product)
	 Send a remove from cart event
	 =========================*/
	analytics.sendProductDetailView = function(productId) {
		var productIndex, product;
		productIndex  = findById(productId, digitalData.products);
		if (productIndex > -1) {
			product = digitalData.products[productIndex];
			digitalData.products[productIndex].price = $(".product-details span[itemprop='price']:last").attr("content") || "0";
			digitalData.events.push({
				'ecommerce': {
					'detail': {
						'products': [{
							'name': product.name || product.id, // Name or ID is required.
							'id': product.id,
							'price': product.price,
							'brand': product.brand,
							'category': product.category,
							//'variant': analytics.findVariant()
							'variant': product.variant
						}]
					}
				}
			});
			analytics.sendEventsTagManager();
			
		}
	};

	/* ========================
	 sendModalPageView(name, url, pageType)
	 Used to send data for a pageview into GTM. The url should be the ajax url, not the window location. Use this when
	 you open a modal that loads a new page.
	 =========================*/
	analytics.sendModalPageView = function(name, url, pageType) {
		digitalData.events.push({
			"event": 'modalPageView',
			"eventPage" : {
				"pageName" : name,
				"pageURL" : url,
				"pageType" : pageType
			}
		});
		analytics.sendEventsTagManager();
	};

	/* ========================
	 sendQuickViewClick(productId, url)
	 Registers a product click, a modal page view, and a productDetailView.
	 =========================*/
	analytics.sendQuickViewClick = function(productId, url) {
		var product, productIndex;
		productIndex  = findById(productId, digitalData.products);
		if (productIndex > -1) {
			analytics.sendProductClick(productId);
			product = digitalData.products[productIndex];
			analytics.sendModalPageView("QuickView : " + product.name, url, "quickView");
			analytics.sendProductDetailView(productId);
		}
	};

	/* ========================
	 sendCheckoutOption(checkoutOption)
	 During checkout, registers options the user has selected; there is a limitation of one option per checkout step.
	 Possible options are the card type the user has selected during payment and the user's interactions with the AVS
	 modal.
	 =========================*/
	analytics.sendCheckoutOption = function(checkoutOption) {
		var checkoutStep = digitalData.checkoutStep;
		digitalData.events.push({
			event: 'checkoutOption',
			//checkoutStep: checkoutStep,
			//checkoutOption : checkoutOption
			ecommerce : {
				'checkout_option' : {
					"actionField" : {'step': checkoutStep, 'option': checkoutOption},
				}
			}
		});
		analytics.sendEventsTagManager();
	};

	/* ========================
	 removePromoCode(promoCodeId)
	 Send a promo Code remove event with the promo code id and name retrieved from order.discounts.
	 =========================*/
	analytics.removePromoCode = function(promoCodeId) {
		if (typeof promoCodeId != "undefined" && "order" in digitalData && "discounts" in digitalData.order) {
			var index = findById(promoCodeId, digitalData.order.discounts);
			if (index != -1) {
				digitalData.events.push({
					event: 'promoCodeRemove',
					eventDiscount: {
						id: digitalData.order.discounts[index].id,
						name: digitalData.order.discounts[index].name
					}
				});
			}
		}
		analytics.sendEventsTagManager();
	};


	/* ========================
	 sendInStoreLookupModal(productId, url)
	 Sends the modal event for when user launches in-store availability modal
	 =========================*/
	analytics.sendInStoreLookupModal = function(productId, url) {
		var product,
				productIndex  = findById(productId, digitalData.products);
		if (productIndex > -1) {
			product = digitalData.products[productIndex];
			analytics.sendModalPageView( "In-Store Availability: " + product.name, url, "inStoreAvailability");
		}
	};

	/* ========================
	 sendInStoreLookup(productId, zipCode, resultCount)
	 Sends the results when user searches for in-store availability of a product
	 =========================*/
	analytics.sendInStoreLookup = function(productId, zipCode, resultCount) {
		var productIndex, eventProducts = [], product,
				eventName = (resultCount && resultCount > 0) ? "inStoreLookup" : "noAvailabilityResults";
			productIndex  = findById(productId, digitalData.products);
			if (productIndex > -1) {
				product = digitalData.products[productIndex];
				eventProducts.push(product);
			}
		digitalData.events.push({
			"event" : eventName,
			"eventPage" : {
				"locatorZip" : zipCode,
				"locatorResults" : resultCount
			},
			"eventProducts" : eventProducts
		});
		analytics.sendEventsTagManager();
	};

	/* ========================
	 trackEvent(event, category, action, label)
	 generic tracking function that can used in content-managed areas
	 =========================*/
	analytics.trackEvent = function (event, category, action, label) {
		digitalData.events.push({
			event: event,
			eventCategory: category,
			eventAction: action,
			eventLabel : label
		});
		analytics.sendEventsTagManager();
	};
	
	/* ========================
	 sendHomePageEvents
	 event handlers to track user interactions with home page
	   ========================*/
	analytics.sendHomePageEvents = function () {
		// Home page product slider
		$('div[data-hp-offers-tag]').on('click', 'a', function(e) {
			var label = $(this).attr("data-prod-name-tag") || $(this).text(),
				action = $(this).closest("div[data-hp-offers-tag]").attr("data-hp-offers-tag");
				
			analytics.trackEvent('Product Tile', 'Product Tile', action, label);
		});
		
		// Track Product offers
		$('.PromoGridContainer [data-overlay]').on('click', function(e) {
			var hasOverlay = $(this).attr("data-overlay");
			
			if(hasOverlay == "true"){ 
				return;
			}
			
			var	label = $(this).find(".button").text() ||  $(this).find("img").attr('alt'),
				action = $(this).closest('.PromoGridContainer').attr("data-tag-title");
			
			analytics.trackEvent('Homepage Offers', 'Homepage Offers', action, label);
			
		});
		
		// Track Home Page CTAs 
		$('.hero-slider a, .promo-page-width a, .promo-grid-two a, .promo-grid-three a').on('click', function(e) {
			var action = $(this).find(".button").text(),
				label  = $(this).find("p").text();
				
			if(action.length){
				analytics.trackEvent('Homepage CTA', 'Homepage CTA', action, label);
			}
		});
	};
	
	/* ========================
	 sendHomePageEvents
	 event handlers to track user interactions with browse page
	   ========================*/
	analytics.sendBrowsePageEvents = function () {
	
		// Track click on category links from side bar.
		$('.facet-list[data-dim="category"]').on('click', 'a', function(e) {
			var label = $(this).attr("data-label") || $(this).text(),
				action = $('.breadcrumbs').find("li .active").text() || "";
			
			analytics.trackEvent('Faceted Search', 'Faceted Search Category Filter', action, label);
		});
		
		// Track click to ‘Sort by’, ‘Items to View’ and pagination sections
		$('a[data-sortparam], .pagination .page-num').on('click', function(e) {
			var label = $(this).text(),
				action = $(this).closest("ul").attr("data-action-tag") || "Pagination";
				
			analytics.trackEvent('Faceted Search', 'Faceted Search', action, label);
		});
				
		// Track click to faceted search sections
		$('.facet-list.checkbox input:checkbox').change('click', function(e) {
			var label = $(this).attr("data-label"),
				action = $(this).parent().attr("data-cat");
		
			analytics.trackEvent('Faceted Search', 'Faceted Search', action, label);
		});
				
		// Track clicks on Product tiles
		$('.product-grid').on('click', 'a', function(e) {
			var label = $(this).attr("data-prod-name-tag") || $(this).text(),
				action = "Product Catalog Tile";		
			if($('.category-product-grid').hasClass("notLeafCat")){
				action = "Product Category Page Tile";
			}
			
			analytics.trackEvent('Product Tile', 'Product Tile', action, label);
		});
	};
	 
	analytics.sendGlobalEvents = function () {
		// Track clicks on Header Navigation Bar
		$('.header-masthead').on('click', 'a', function(e) {
			var action = $(this).attr("data-action") || $(this).text(),
				label = $(this).attr("href");
			action = action.trim();
			
			if(action == 'CHOOSE A STORE'){
				label = "Store Select";
			}else if(action.indexOf("Call Us at") == 0){
				label = "Call";
			}
			
			if(action.indexOf("Hi") != 0 && !$(this).hasClass('home-store-toggle')){
				analytics.trackEvent('Header', 'Header', action, label);
			}
		});
		
		// Track clicks on Header Navigation Bar(Mobile)
		$('a[data-tag-header]').on('click', function(e) {
			var action = $(this).attr("data-tag-header") || $(this).text();
			
			analytics.trackEvent('Mobile Header', 'Header', action.trim(), $(this).attr("href"));
		});
		
		$('.masthead-mobile .masthead-logo img').on('click', function(e) {
			var action = $(this).attr("alt");
			analytics.trackEvent('Mobile Header', 'Header', action.trim(), '/');
		});
		
		// Track clicks on Footer Navigation Bar
		$('.footer-links-container, .footer-legal-links').on('click', 'a', function(e) {
			var action = $(this).text().trim(),
				label = $(this).attr("href");
			
			if(action.indexOf("Call Us at") == 0){
				label = "Call";
			}
			analytics.trackEvent('Footer', 'Footer', action, label);
		});
		
		$('.footer-social .social-icons').on('click', 'a', function(e) {
			var label = $(this).find(".sr-only").text().trim() || $(this).attr("href");
			analytics.trackEvent('Footer', 'Footer', 'Social Icons', label);
		});
		
	
		// Track clicks on Site Navigation links
		$('.primary-nav-menu').on('click', 'a', function(e) {
			var action = $(this).attr("data-parent") || "",
				label  = $(this).text();
			
			analytics.trackEvent('Site Navigation', 'Site Navigation', action.trim(), label.trim());
		});
		
		// Track clicks on Site Navigation links(Mobile)
		$('a[data-parent-mobile]').on('click', function(e) {
			var action = $(this).attr("data-parent-mobile") || "",
				label  = $(this).text();
			
			analytics.trackEvent('Mobile Site Navigation', 'Site Navigation', action.trim(), label.trim());
		});
		
		// Track clicks on Global Site Banner
		$('.global-promo-section-container').on('click', 'a', function(e) {
			var label = $(this).attr("href"),
				action  = $(this).text();
			
			analytics.trackEvent('Global Site Banner', 'Global Site Banner', action.trim(), label);
		});
		$('.footer-email-signup').on('click', 'a', function(e) {
			var label = $(this).attr("href"),
				eventLabel = $(this).parent(".footer-email-signup").find('h3').text().trim(),
				action  = $(this).text();
			
			analytics.trackEvent(eventLabel, eventLabel, action.trim(), label);
		});
		
		$('.section-row .gc-product-section').on('click', 'a', function(e) {
			var label =  $(this).text(),
				action = $(this).attr('href');
				
			analytics.trackEvent('CTA Button', 'Button', label, action);
		});
		$('body').on('click', '.back-to-top', function(e) {				
			analytics.trackEvent('Scroll To Top', 'Scroll To Top', 'Click', window.location.href);
		});
		
		$('body').on('click','.ltkmodal-subscribe',function(){
			var label = $(this).attr('title');
			analytics.trackEvent("Email Sign Up Modal","Email Sign Up", label, window.location.href);
		});
		$('body').on('click','.ltkmodal-close',function(){
			var label = $(this).attr('title');
			analytics.trackEvent("Email Sign Up Modal","Email Sign Up", label, window.location.href);
		});
		
		//typeahead event firing
		$('body').on('click', '.typeahead-container .typeahead-details-top a', function(e) {
			var action = $(this).attr('href'),
				label = $(this).find('.product-name').text();
				analytics.trackEvent("Site Search Tile","Site Search",label, label);
		});
		
		$('body').on('click', '.typeahead-container .typeahead-details-bottom a', function(e) {
			var action = $(this).attr('href'),
				label = $(this).text(),
				parent = $(this).parents('li').find('h4').text();
				analytics.trackEvent("Site Search Link","Site Search",parent, label);
		});
		$('body').on('click', '.typeahead-container .typeahead-suggestions a', function(e) {
			var action = $(this).attr('href'),
				label = $(this).text(),
				parent = $(this).parents('div.typeahead-suggestions').find('h4').text();
				analytics.trackEvent("Site Search Link","Site Search",parent, label);
		});
	};

	analytics.sendProductEvents = function () {
	
		// BV write review tracking.
		$(".product ").on('click', '.bv-write-review', function(e) {
			var action = $(this).hasClass('bv-submission-button') ? 'Write Review' : $(this).text().trim();
			
			digitalData.events.push({
				event: 'Product Reviews',
				eventCategory: 'Product Reviews',
				eventAction: 'Write Review',
				eventLabel : window.location.href
			});
			
			analytics.sendEventsTagManager();
		});
		
		// BV submit review tracking
		$(".product ").on('click', '.bv-submission-button-submit', function(e) {
			
			digitalData.events.push({
				event: 'Product Reviews',
				eventCategory: 'Product Reviews',
				eventAction: 'Submit Review',
				eventLabel : window.location.href
			});
			
			analytics.sendEventsTagManager();
		});
		
		// Track links on PDP
		$("#product-info-accordion a, .underlined-link, .bopis-location-info a").on('click', function(){			
			analytics.trackEvent('Product Detail', 'Product Detail Link', $(this).text().trim(), window.location.href);
		});
		
		// Track image selection
		$(".viewer-thumb").on('click', function(){
			var action = $(this).attr('data-slick-index') || 0;
			analytics.trackEvent('Product Detail', 'Product Detail', "Image " + (parseInt(action) + 1), window.location.href);
		});
		
		// Track social icons - pininterest
		$(".social-icons span[data-pin-log]").on('click', function(){
			analytics.trackEvent('Product Detail', 'Product Detail', "Pinterest", window.location.href);
		});
		
		// email and print
		$(".site-functions a, body .email-product-submit input:submit").on('click', function(){
			var action = $(this).hasClass("print") ? "print" : "Email";
			if($(this).hasClass("button")){
				action = "send";
			}
			
			analytics.trackEvent('Product Detail', 'Product Detail', action, window.location.href);
		});
		
		// Track recently viewed product click
		$(".recently-viewed a[data-prod-name-tag]").on('click', function(){
			var label = $(this).attr("data-prod-name-tag").trim();
			
			analytics.trackEvent('Product Tile', 'Product Tile', 'Recently Viewed', label);		
		});
		
		// Track BV reviews
		$(".bv-inline-histogram .bv-inline-histogram-ratings-star-container").on('click', function(){
			var label = $(this).attr("title").trim();
			label = label.replace("Select to filter reviews with ", "").replace(".", "");
			
			analytics.trackEvent('Product Reviews', 'Product Reviews', 'Ratings Snapshot', label);		
		});
		
		$(".product").on('click', '.bv-content-sort-dropdown .bv-dropdown-item', function(){
			analytics.trackEvent('Product Reviews', 'Product Reviews', 'Sort by',  $(this).text().trim());		
		});
		
		$(".product").on('click', '#bv-content-filter-dropdown-Rating .bv-dropdown-item, #bv-content-filter-dropdown-contextdatavalue_Age .bv-dropdown-item, #bv-content-filter-dropdown-contextdatavalue_Gender .bv-dropdown-item, #bv-content-filter-dropdown-contextdatavalue_ShoppingFrequency .bv-dropdown-item', function(){
			var label = $(this).find('span').text().trim(),
				id =  $(this).parent().attr("id"),
				action = "";
				
			switch(id){
				case 'bv-content-filter-dropdown-Rating' : 									action = 'Rating';
																							break;
				case 'bv-content-filter-dropdown-contextdatavalue_Age' : 					action = 'Age';
																							break;
				case 'bv-content-filter-dropdown-contextdatavalue_Gender' : 				action = 'Gender';
																							break;
				case 'bv-content-filter-dropdown-contextdatavalue_ShoppingFrequency' : 		action = 'Shopping Frequency';
																							break;	
			}
			analytics.trackEvent('Product Review Dropdown', 'Product Reviews', action, label);		
		});
		
		
	};
	
	analytics.sendCartEvents = function () {
		// Track remove/move to wishlist clicks
		$(".item-action a").on('click', function(){
			var action = $(this).text().trim(),
				label  = $(this).closest(".item-details").find(".product-name").text().trim();
			analytics.trackEvent('Shopping Cart', 'Shopping Cart', action, label);
		});
		
		// Track qnty +/- clicks
		$(".cart").on('click', '.quantity-group .minus-icon, .quantity-group .plus-icon', function(){
			var label = $(this).hasClass("minus-icon") ? "-" : "+";
			analytics.trackEvent('Shopping Cart', 'Shopping Cart', 'Quantity', label);
		});
		
		// Track Keep shopping/checkout buttons
		$("#moveToPurchaseInfo, .keep-shopping-btn").on('click', function(){
			var label = $(this).hasClass("keep-shopping-btn") ? "Keep Shopping" : "Proceed to Checkout";
			analytics.trackEvent('Shopping Cart', 'Shopping Cart', label, label);
		});
	};
	
	analytics.sendAccountPageEvents = function () {
		// Track CTA button in account section
		$('.site-wrapper').on('click', '.button', function(){
			if($('body').hasClass("login") || $(this).attr("id") == "tax-exemption-submit" || $(this).attr("id") == "address-submit" || $(this).attr("id") == "payment-form-submit"){
				return;
			}

			var label = $(this).text().trim() || $(this).val();
			analytics.trackEvent('My Account CTA', 'My Account', label, label);
		});
		
		// Track action links in account section
		$('.site-wrapper').on('click', 'a:not(.button)', function(){
			var label = $(this).text().trim() || $(this).val();
			analytics.trackEvent('My Account Link', 'My Account', label, label);
		});
		
	};
	
	analytics.sendStaticPageEvents = function () {
		// Track clicks on left nav links
		$('.two-column-left').on('click', 'a', function(){
			var label = $(this).text().trim(),
				action = $(this).closest(".accordion-body").prev().text();
			analytics.trackEvent('FAQ Navigation', 'FAQ Navigation', action.trim(), label);
		});
		
		// Track click on right hand accordion headers.
		$('.two-column-right').on('open.accordion', '.accordion-title', function(){
			var label = $(this).find('.question').text().trim();
			analytics.trackEvent('FAQ Navigation', 'FAQ Navigation', 'Expand', label);
		});
		// Track click on right container static links.
		$('.two-column-right .content-container').on('click', 'a', function(){
			var label = $(this).text().trim();
				action= $(this).attr("href");
			analytics.trackEvent('Link', 'Link', label, action);
		});
		
	};
	
	analytics.sendStoreDetailPageEvents = function (){
		// Track clicks on store details page links
		$('.two-column-left').on('click', '.tertiary', function(){
			var label = $(this).text().trim(),
			action = $(this).attr("href");
			analytics.trackEvent('Store Detail Button', 'Store Locator', label, action.trim());
		});
		
		$('.location-details').on('click', 'a', function(){
			var label = $(this).text(),
			action = $(this).attr("href");
			if(label !== ''){
				label = label.trim().replace(/[<>]/g,'');
			}
			analytics.trackEvent('Store Detail Link', 'Store Locator', label, action);
		});
	};
	
	analytics.sendStoreLocatorPageEvents = function(){
		$('body').on('click', '.view-store-details a', function(){
			var label = $(this).text(),
			action = $(this).attr("href");
			analytics.trackEvent('Store Detail Link', 'Store Locator', label, action);
		});
	};
	
	/* ========================
	 sendAddProduct(product)
	 Send a add to cart event
	 =========================*/
	analytics.sendAddProduct = function(productId) {
		var productIndex, product;
		productIndex  = findById(productId, digitalData.products);
		if (productIndex > -1) {
			product = digitalData.products[productIndex];
			sendProductEvent("addToCart", product);
		}
	};
	
		
	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].analytics = analytics;

}(this, window.jQuery, window.digitalData, "KP"));
