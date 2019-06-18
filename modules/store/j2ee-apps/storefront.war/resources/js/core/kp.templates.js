(function (global, namespace ) {
	"use strict";

	if (!global[namespace]) {
		global[namespace] = {};
	}

	var CONSTANTS = global[namespace].constants;
	var templates = {

		errorMessageTemplate:
			'<div class="alert-box error" role="alert">' +
				'<p>{{#errorMessages}}{{.}}<br/>{{/errorMessages}}</p>' +
			'</div>',

		fprerrorMessageTemplate:
				'<div class="alert-box error" role="alert">' +
					'<p>{{#errorMessages}}{{.}}{{/errorMessages}}You can view more information <a class="fprer" href="https://www.fleetfarm.com/static/faq-contact-us/faq-my-account/#reset-password-security">here</a>.</p>' +
				'</div>',
			
		alertMessageTemplate:
			'<div class="alert-box info" role="alert">' +
				'<p>{{#alertMessages}}{{.}}<br/>{{/alertMessages}}</p>' +
			'</div>',

		errorPromoMessageTemplate:
			'<div class="alert promo-code-msg" role="alert">' +
				'<p>' +
					'<span class="icon icon-error"></span>' +
					'{{#errorMessages}}{{.}}<br/>{{/errorMessages}}' +
				'</p>' +
			'</div>',

		avsTemplate:
			'{{#noMatch}}' +
				'<li>' +
					'<div class="card">' +
						'<div class="card-title">No Match</div>' +
						'<div class="card-content">' +
							'<p>Sorry, we couldn\'t find a match for the address you entered.</p>' +
						'</div>' +
						'<div class="card-links">' +
							'<a href="#" data-dismiss="modal" class="button primary">Edit Address</a>' +
						'</div>' +
					'</div>' +
				'</li>' +
			'{{/noMatch}}' +
			'{{#suggestedAddress}}' +
				'<li>' +
					'<div class="card">' +
						'<div class="card-title">Suggested Address</div>' +
						'<div class="card-content">' +
							'<p>{{address1}}</p>' +
							'{{#address2}}<p>{{.}}</p>{{/address2}}' +
							'<p>{{city}}, {{state}} {{postalCode}}</p>' +
						'</div>' +
						'<div class="card-links">' +
							'<a href="#" class="button primary use-suggested">Use Suggested</a>' +
						'</div>' +
					'</div>' +
				'</li>' +
			'{{/suggestedAddress}}' +
			'{{#enteredAddress}}' +
				'<li>' +
					'<div class="card">' +
						'<div class="card-title">You Entered</div>' +
						'<div class="card-content">' +
							'<p>{{address1}}</p>' +
							'{{#address2}}<p>{{.}}</p>{{/address2}}' +
							'<p>{{{city}}}, {{state}} {{postalCode}}</p>' +
						'</div>' +
						'<div class="card-links">' +
							'<a href="#" class="button secondary use-entered">Use As Entered</a>' +
						'</div>' +
					'</div>' +
				'</li>' +
			'{{/enteredAddress}}',

		bopisNotificationTemplate:
			'{{#current}}' +
				'<div class="bopis-selected-store">' +
					'<div class="address" style="width:100%">' +
						'<h3 class="current-location">Current Pick Up Location</h3>' +
						'<h3>{{city}}, {{stateAddress}}</h3>' +
						'<p>{{address1}}</p>' +
						'<p>{{address2}}</p>' +
						'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
						'<p>{{phoneNumber}}</p>' +
						'<p><strong>Distance:</strong> {{distance}}</p>' +
					'</div>' +
				'</div>' +
			'{{/current}}' +
			'<div class="bopis-item-notifications">' +
				'<ul class="bopis-results-list">' +
					'{{#available}}' +
						'{{#stores}}' +
							'{{#eligible}}' +
								'<li>' +
									'<div class="address">' +
										'<h3>{{city}}, {{stateAddress}}</h3>' +
										'<p>{{address1}}</p>' +
										'<p>{{address2}}</p>' +
										'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
										'<p>{{phoneNumber}}</p>' +
										'<p><strong>Distance:</strong> {{distance}}</p>' +
									'</div>' +
									'<div class="actions">' +
										'<button class="button primary expand choose-this-store" data-bopis-store-id="{{locationId}}">Choose This Store</button>' +
									'</div>' +
								'</li>' +
							'{{/eligible}}' +
						'{{/stores}}' +
					'{{/available}}' +
					'{{^available}}' +
						'<li>' +
							'Sorry, this item isn\'t available at any stores within <strong>{{searchRadius}} miles</strong> of the zip code provided.' +
							'<br/><br/>' +
							'<a href="' + CONSTANTS.contextPath + '/sitewide/storeLocator.jsp">See Store Locations</a>' +
						'</li>' +
					'{{/available}}' +
				'</ul>' +
				'<a href="#" data-dismiss="modal" class="button secondary cancel-button expand">Cancel</a>' +
				'<a href="#" class="ship-my-order centered">Ship My Order Instead</a>' +
			'</div>',

		bopisLocationInfoTemplate:
			'<div class="card">' +
				'<div class="card-title">Store Pick Up Information</div>' +
				'<div class="card-content">' +
					'<h3>{{city}}, {{#state}}{{state}}{{/state}}{{#stateAddress}}{{stateAddress}}{{/stateAddress}}</h3>' +
					'<p>{{#address}}{{address}}{{/address}}{{#address1}}{{address1}}{{/address1}}</p>' +
					'<p>{{city}}, {{#zip}}{{zip}}{{/zip}}{{#postalCode}}{{postalCode}}{{/postalCode}}</p>' +
					'<p>{{#phone}}{{phone}}{{/phone}}{{#phoneNumber}}{{phoneNumber}}{{/phoneNumber}}</p>' +
				'</div>' +
				'<div class="card-links">' +
					'<a href="#" class="change-store">Change Store</a>' +
					'{{#bopisOnly}}' +
						'<a href="#" class="ship-my-order">Ship My Order Instead</a>' +
					'{{/bopisOnly}}' +
				'</div>' +
			'</div>',

		bopisStoreLocationInfoTemplate:
			'<div class="bopis-section {{isActiveTeaser}}">' +
				'<div class="bopis-store-content ">' +
					'{{#eligible}}' +
						'<span class="bopis-store-available">' +
							'Available' +
							'<span class="icon icon-available"></span>' +
						'</span>' +
					'{{/eligible}}' +
					'{{^eligible}}' +
						'<span class="bopis-store-unavailable">' +
							'Not Available' +
							'<span class="icon icon-unavailable"></span>' +
						'</span>' +
					'{{/eligible}}' +
					' <span>at</span> ' +
					'<a class="bopis-store-info update-bopis-store underlined-link" href="#">' +
						'{{city}}, ' +
						'{{#state}}{{state}}{{/state}}' +
						'{{#stateAddress}}{{stateAddress}}{{/stateAddress}}' +
					'</a>' +
					'<div class="card-links">' +
						'<a href="#" class="change-store">Change Store</a>' +
						'{{#displayShipMyOrderLink}}' +
							'<span class="seperator">&nbsp;&nbsp;|&nbsp;&nbsp;</span>' +
							'<a href="#" class="ship-my-order">Ship My Order Instead</a>' +
						'{{/displayShipMyOrderLink}}' +
						'{{^displayShipMyOrderLink}}' +
							'{{#bopisOnly}}' +
								'<span class="seperator">&nbsp;&nbsp;|&nbsp;&nbsp;</span>' +
								'<a href="#" class="ship-my-order">Ship My Order Instead</a>' +
							'{{/bopisOnly}}' +
						'{{/displayShipMyOrderLink}}' +
					'</div>' +
				'</div>' +
			'</div>',

		bopisSearchTemplate:
			'<p class="results-zip-code">' +
				'Showing results for: <strong>{{zip}}</strong> ' +
				'within <strong>{{searchRadius}} miles</strong>' +
			'</p>' +
			'{{^available}}' +
				'<div class="home-results-list scrollbar callout">' +
					'<ul class="bopis-results-list">' +
						'<li>' +
							'Sorry, this item isn\'t available at any stores within ' +
							'<strong>{{searchRadius}} miles</strong> ' +
							'of the zip code provided.<br/><br/>' +
							'<a href="' + CONSTANTS.contextPath + '/sitewide/storeLocator.jsp">See Store Locations</a>' +
						'</li>' +
					'</ul>' +
				'</div>' +
			'{{/available}}' +
			'{{#available}}' +
				'<div class="home-results-list scrollbar" id="style-1">' +
					'<ul class="bopis-results-list">' +
						'{{#stores}}' +
							'<li>' +
								'<div class="address">' +
									'<h3>{{city}}, {{stateAddress}}</h3>' +
									'<p>{{address1}}</p>' +
									'<p>{{address2}}</p>' +
									'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
									'<p>{{phoneNumber}}</p>' +
									'<p><strong>Distance:</strong> {{distance}}</p>' +
								'</div>' +
								'<div class="actions">' +
									'{{#eligible}}' +
										'<button class="button primary expand choose-this-store" data-bopis-store-id="{{locationId}}">Choose This Store</button>' +
									'{{/eligible}}' +
									'{{^eligible}}' +
										'<button class="button primary disabled expand">Not Available</button>' +
									'{{/eligible}}' +
								'</div>' +
							'</li>' +
						'{{/stores}}' +
					'</ul>' +
				'</div>' +
			'{{/available}}',

		storeSearchTemplate:
			'<p class="results-zip-code">' +
				'Showing results for: <strong>{{zip}}</strong> ' +
				'within <strong>{{searchRadius}} miles</strong>' +
			'</p>' +
			'<div class="home-results-list scrollbar" id="style-1">' +
				'<ul class="bopis-results-list">' +
					'{{#stores}}' +
						'<li>' +
							'<div class="address">' +
								'<h3>{{city}}, {{stateAddress}}</h3>' +
								'<p>{{address1}}</p>' +
								'<p>{{address2}}</p>' +
								'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
								'<p>{{phoneNumber}}</p>' +
								'<p><strong>Distance:</strong> {{distance}}</p>' +
							'</div>' +
							'<div class="actions">' +
								'{{^eligible}}' +
									'<button class="button primary expand choose-store" data-store-id="{{locationId}}">Make This My Store</button>' +
								'{{/eligible}}' +
								'{{#eligible}}' +
									'<button class="button primary disabled expand">Not Available</button>' +
								'{{/eligible}}' +
							'</div>' +
						'</li>' +
					'{{/stores}}' +
					'{{^stores}}' +
						'<li>' +
							'Sorry, this item isn\'t available at any stores within <strong>{{searchRadius}} miles</strong> of the zip code provided.' +
							'<br/><br/>' +
							'<a href="' + CONSTANTS.contextPath + '/sitewide/storeLocator.jsp">See Store Locations</a>' +
						'</li>' +
					'{{/stores}}' +
				'</ul>' +
			'</div>',

		bopisStoreSearchTemplate:
			'<p class="results-zip-code">' +
				'Showing results for: <strong>{{zip}}</strong> ' +
				'within <strong>{{searchRadius}} miles</strong>' +
			'</p>' +
			'{{^available}}' +
				'<div class="home-results-list scrollbar callout">' +
					'<ul class="bopis-results-list">' +
						'<li>' +
							'Sorry, this item isn\'t available at any stores within ' +
							'<strong>{{searchRadius}} miles</strong> of the zip code provided.' +
							'<br/><br/>' +
							'<a href="' + CONSTANTS.contextPath + '/sitewide/storeLocator.jsp">See Store Locations</a>' +
						'</li>' +
					'</ul>' +
				'</div>' +
			'{{/available}}' +
			'{{#available}}' +
				'<div class="home-results-list scrollbar" id="style-1">' +
					'<ul class="bopis-results-list">' +
						'{{#stores}}' +
							'<li>' +
								'<div class="address">' +
									'<h3>{{city}}, {{stateAddress}}</h3>' +
									'<p>{{address1}}</p>' +
									'<p>{{address2}}</p>' +
									'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
									'<p>{{phoneNumber}}</p>' +
									'<p><strong>Distance:</strong> {{distance}}</p>' +
								'</div>' +
								'<div class="actions">' +
									'{{#eligible}}' +
										'<button class="button primary expand change-this-store" data-change-bopis-store-id="{{locationId}}">Choose This Store</button>' +
									'{{/eligible}}' +
									'{{^eligible}}' +
										'<button class="button primary disabled expand">Not Available</button>' +
									'{{/eligible}}' +
								'</div>' +
							'</li>' +
						'{{/stores}}' +
					'</ul>' +
				'</div>' +
			'{{/available}}',

		storeHeaderTemplate:
			'<a href="#" class="home-store-toggle">' +
				'<span class="icon icon-locator" aria-hidden="true"></span> ' +
				'{{city}}, {{stateAddress}}' +
			'</a>',

		storeBodyTemplate:
			'<div class="card-title">' +
				'<a href="#">' +
					'<span class="icon icon-locator" aria-hidden="true"></span> ' +
					'My Store' +
				'</a>' +
			'</div>' +
			'<div class="card-content">' +
				'<p class="title">{{city}}, {{stateAddress}}</p>' +
				'<a href="{{website}}" alt="View Store Details" class="view-store">View Store Details</a>' +
			'</div>' +
			'<div class="card-content">' +
				'<p>{{address1}}</p>' +
				'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
				'<p>{{phoneNumber}}</p>' +
				'<a href="{{website}}" alt="View Store Details" class="hide-store">View Store Details</a>' +
			'</div>',

		accountStoreTemplate:
			'<div class="card" style="height:257px;">' +
				'<div class="card-title">My Store</div>' +
				'<div class="card-content home-store">' +
					'<p class="title">{{city}}, {{stateAddress}}</p>' +
					'<p>{{address1}}</p>' +
					'<p>{{city}}, {{stateAddress}} {{postalCode}}</p>' +
					'<p>{{phoneNumber}}</p>' +
					'<a href="{{website}}">View Store Details</a>' +
				'</div>' +
				'<div class="card-links equalized">' +
					'<a href="#" class="button primary outline update-store">Change my store</a>' +
				'</div>' +
			'</div>',

		templatePickerTypeSwatch :
			'<div class="product-selectors group product-selectors-{{type}} {{cssclass}}">'+
				'<div class="product-options" data-typeid="{{typeId}}">'+
					'<label for="product-{{type}}">{{title}}</label>'+
					'<div class="product-option-errors"></div>'+
					'<ul class="option-swatches" id="product-{{type}}">'+
						'{{#availableOptions}}'+
						'<li>'+
							'<a class="option-link swatch {{#isSelected}}active{{/isSelected}}" data-type="{{type}}" data-typeid="{{typeId}}" data-value="{{optionValue}}" data-id="{{optionId}}">'+
								'<img src="{{imageSrc}}" alt="{{optionValue}}" title="{{optionValue}}"/>'+
								'<img src="/images/swatch/unavailable/x-white.png" class="x-overlay"/>'+
							'</a>'+
						'</li>'+
						'{{/availableOptions}}'+
					'</ul>'+
				'</div>'+
			'</div>',

		templatePickerTypeDropdown:
			'<div class="product-selectors group product-selectors-{{type}} {{cssclass}}">' +
				'<div class="product-options" data-typeid="{{typeId}}">' +
					'<label for="product-{{type}}">{{title}}</label>' +
					'<div class="product-option-errors"></div>' +
					'<select class="option-dropdown" id="product-{{type}}">' +
						'{{#availableOptions}}' +
							'<option data-type="{{type}}" data-typeid="{{typeId}}" data-value="{{optionValue}}" data-id="{{optionId}}" class="{{#isSelected}}active{{/isSelected}}" {{#isSelected}}selected{{/isSelected}}>{{optionValue}}</option>' +
						'{{/availableOptions}}' +
					'</select>' +
					'{{#mediaUrl}}' +
						'<a href="${contextPath}/browse/ajax/sizeChartModal.jsp?url={{mediaUrl}}" class="modal-trigger" data-target="size-chart-modal" data-size="small">Size Chart</a>' +
					'{{/mediaUrl}}' +
				'</div>' +
			'</div>',

		templateRegularPrice:
			'<div class="regular-price">' +
				'<span itemprop="price" content="{{regularPrice}}">${{regularPrice}}</span>' +
			'</div>',

		templateSalePrice:
			'<div class="original-price">' +
				'<span itemprop="price" content="{{originalPrice}}">${{originalPrice}}</span>' +
			'</div>' +
			'<div class="sale-price">' +
				'<span itemprop="price" content="{{salePrice}}">${{salePrice}} SALE</span>' +
			'</div>',

		templateClearancePrice:
			'<div class="original-price">' +
				'<span itemprop="price" content="{{originalPrice}}">${{originalPrice}}</span>' +
			'</div>' +
			'<div class="sale-price">' +
				'<span itemprop="price" content="{{salePrice}}">${{salePrice}} CLEARANCE</span>' +
			'</div>' +
			'<div class="discontinued-policy-label">' +
				'<span>Clearance Item Policy</span> ' +
				'<a class="view-details modal-trigger" href="/browse/ajax/discontinuedItemPolicyModal.jsp" data-target="discontinued-item-policy-modal" data-size="small">' +
					'<span class="icon icon-info"></span>' +
				'</a>' +
			'</div>',

		templateHidePrice:
			'<div class="discontinued-map-pricing-label">' +
				'<span>Add to Cart for price</span> ' +
				'<a class="view-details modal-trigger" href="/browse/ajax/addToCartToSeePriceModal.jsp" data-target="discontinued-item-policy-modal" data-size="small">details</a>' +
			'</div>',

		typeaheadSuggestionsTemplate:
			'<h4>{{searchTerm}}</h4>' +
			'<ul>' +
				'{{#results}}' +
					'<li>' +
						'<a href="{{{url}}}" data-detail-url="{{{detailUrl}}}">' +
							'{{{term}}}' +
						'</a>' +
					'</li>' +
				'{{/results}}' +
			'</ul>',

		typeaheadDetailsTopTemplate:
			'<div class="typeahead-details-top">' +
				'<ul class="typeahead-details-top-grid">' +
					'{{#links}}' +
						'<li>' +
							'<a href="{{url}}">' +
								'<div class="product-tile">' +
									'<div class="product-image">' +
										'<img src="{{{image}}}" alt="{{title}}" />' +
									'</div>' +
									'<div class="product-tile-details">' +
										'<div class="product-tile-text">' +
											'<div class="product-name">{{title}}</div>' +
											'<div class="product-brand">{{brand}}</div>' +
										'</div>' +
									'</div>' +
								'</div>' +
							'</a>' +
						'</li>' +
					'{{/links}}' +
				'</ul>' +
			'</div>',

		typeaheadDetailsBottomTemplate:
			'<div class="typeahead-details-bottom">' +
				'<ul class="typeahead-details-bottom-grid">' +
					'{{#.}}' +
						'<li>' +
							'<h4>{{title}}</h4>' +
							'<ul>' +
								'{{#links}}' +
									'<li>' +
										'<a href="{{{url}}}">{{title}}</a>' +
									'</li>' +
								'{{/links}}' +
							'</ul>' +
						'</li>' +
					'{{/.}}' +
				'</ul>' +
			'</div>',

		// NOTE: cart page needs the JSON property isCart to be present
		// so that "*" and "Estimated" are added in the proper places
		orderTotals :
			'<div class="total-row subtotal">' +
				'<div class="total-label">Merchandise Total :</div>' +
				'<div class="total-amount">{{orderSubtotal}}</div>' +
			'</div>' +
			'{{#orderDiscount.length}}' +
				'<div class="total-row subtotal">' +
					'<div class="total-label">' +
						'Discounts ' +
						'<a class="view-details modal-trigger" href="' + CONSTANTS.contextPath + '/checkout/ajax/promoDetailsModal.jsp?p={{promoDispName}}&d={{couponDetails}}" data-target="promo-details-modal" data-size="small">' +
							'<span class="icon icon-info"></span>' +
						'</a> :' +
					'</div>' +
					'<div class="total-amount savings">- {{totalSavings}}</div>' +
				'</div>' +
			'{{/orderDiscount.length}}' +
			'{{#orderDiscount}}' +
				'<div class="total-row">' +
					'<div class="total-label">' +
						'<span class="total-promo">{{promoDispName}}</span>' +
					'</div>' +
				'</div>' +
			'{{/orderDiscount}}' +
			'<div class="total-row shipping">' +
				'<div class="total-label">' +
					'{{#isCart}}Estimated {{/isCart}}Shipping{{#isCart}}&#42;{{/isCart}} :' +
					'{{#orderShippingPromos}}' +
						'<div>' +
							'<span class="total-promo">{{{shipPromoName}}}</span>' +
						'</div>' +
					'{{/orderShippingPromos}}' +
				'</div>' +
				'<div class="total-amount {{#isFreeShipping}}savings {{/isFreeShipping}}">' +
					'{{orderShipping}}' +
				'</div>' +
			'</div>'+
			'<div class="total-row tax">' +
				'<div class="total-label">Tax{{#isCart}}&#42;{{/isCart}} :</div>' +
				'<div class="total-amount">{{orderTax}}</div>' +
			'</div>' +
			'{{^isCart}}' +
				'{{#giftCardTotal}}' +
					'<div class="total-row tax">' +
						'<div class="total-label">{{gcHeaderText}} :</div>' +
						'<div class="total-amount">- {{.}}</div>' +
					'</div>' +
				'{{/giftCardTotal}}' +
			'{{/isCart}}' +
			'<div class="total-row total">' +
				'<div class="total-label">{{#isCart}}Estimated {{/isCart}}Total :</div>' +
				'<div class="total-amount">{{orderTotal}}</div>' +
			'</div>',

		appliedCoupons:
			'{{#appliedCouponPromos}}' +
				'<div class="promo-applied">' +
					'<span class="coupon-code">' +
						'{{couponCode}}' +
					'</span>' +
					'<a class="view-details modal-trigger" href="' + CONSTANTS.contextPath + '/checkout/ajax/lineItemPromoDetailsModal.jsp?p={{couponPromoShortDesc}}&d={{couponDetails}}" data-target="promo-details-modal" data-size="small">View Details</a>' +
					'<a href="#" class="remove-link">' +
						'<span class="icon icon-remove"></span>' +
						'Remove' +
					'</a>' +
				'</div>' +
			'{{/appliedCouponPromos}}',

		lineItemPromotions:
			'{{#shortDescription}}' +
				'<span class="icon icon-check"></span>' +
				'<span class="promo-line-item-desc">{{shortDescription}}</span> ' +
				'discount applied ' +
				'<a class="view-details modal-trigger" href="' + CONSTANTS.contextPath + '/checkout/ajax/lineItemPromoDetailsModal.jsp?p={{displayName}}&d={{shortDescription}}" data-target="promo-details-modal" data-size="small">' +
					'<span class="icon icon-info"></span>' +
				'</a>' +
			'{{/shortDescription}}' +
			'{{#qualName}}' +
				'<a class="view-details modal-trigger upsells-item-msg" href="/browse/ajax/upsellMessageModal.jsp?qualName={{qualName}}&qualInstructions={{upsellInstructions}}" data-target="promo-details-modal" data-size="small">' +
					'<span class="icon icon-upsells"></span>' +
					'<span class="promo-line-item-desc orange">{{qualName}}</span>' +
					'<span class="icon icon-info"></span>' +
				'</a>' +
			'{{/qualName}}',

		appliedGiftCards:
			'<h3>{{gcAppliedHeaderText}}</h3>' +
			'<div class="applied-gift-cards">' +
				'{{#appliedGiftCards}}' +
					'<div class="applied-gift-card">' +
						'<div class="gift-card-number-applied">{{number}}</div>' +
						'<a href="#" class="gift-card-remove" data-number="{{number}}">remove</a>' +
						'<div class="gift-card-amount-applied amount-{{number}}">{{amount}}</div>' +
					'</div>' +
				'{{/appliedGiftCards}}' +
			'</div>',

		appliedWishList:
			'<a href="#" class="add-to-wish-list underlined-link">Add to Wish List</a>',

	};

	global[namespace].templates = templates;

}(this, 'KP'));
