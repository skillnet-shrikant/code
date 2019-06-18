/* =========================================================
 * product.js
 * Created by DMI.
 * ==========================================================
 * Functionality for the product display page including:
 * - attribute selection display and interaction
 * @requires collapse
 * @requires validate
 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

		var CONSTANTS = global[namespace].constants,
				UTILITIES = global[namespace].utilities,
				TEMPLATES = global[namespace].templates;

	function getObjectSize(pObj) {
		var size = 0, key;
		if (typeof (pObj) === 'object') {
			for (key in pObj) {
				if (pObj.hasOwnProperty(key)) {
					size++;
				}
			}
		}
		else if (Array.isArray(pObj)) {
			size = pObj.length;
		}
		return size;
	}

	global[namespace].ProductData = function (data) {
		this.data = data || {};
		this.allOptions = {};
		this.allInventory = {};
		this.init();
	};

	global[namespace].ProductData.prototype = {
		init : function () {
			this.cleanSkuData();
			this.generateOptions();
		},
		cleanSkuData: function () {
			var variantSize = 0,
					cleanSkus = [],
					x,
					y,
					productSkus = this.data.skus,
					skuLength = productSkus.length,
					variantTypes = this.data.variantTypes,
					variantTypeLength = variantTypes.length;

			// loop though the skus and check that they have the proper amount of variants and that the
			// variants defined on the product exist on the skus.
			for (x = 0; x < skuLength; x++) {
				variantSize = getObjectSize(productSkus[x].variants);
				if (variantSize != variantTypeLength) {
					continue;
				}
				for (y = 0; y < variantTypeLength; y++) {
					if (typeof productSkus[x].variants[variantTypes[y].id] == 'undefined') {
						break;
					}
				}
				cleanSkus.push(productSkus[x]);
			}
			this.data.skus = cleanSkus;
		},
		hasSkus : function () {
			return this.data.skus.length > 0;
		},
		getSku: function (skuId) {
			var skus = this.data.skus,
					skuLength = skus.length,
					i;
			for (i = 0; i < skuLength; i++) {
				if (skus[i].catalogRefId == skuId) {
					return skus[i];
				}
			}
		},
		getSkus: function () {
			return this.data.skus;
		},
		getVariantTypes: function () {
			return this.data.variantTypes;
		},
		getVariantTypeName: function (variantId) {
			var x = 0,
					variantTypes = this.data.variantTypes,
					max = variantTypes.length,
					variantName = '';
			for (x; x < max; x++) {
				if (variantTypes[x].id == variantId) {
					variantName = variantTypes[x].displayName;
					break;
				}
			}
			return variantName;
		},
		getAllOptions : function () {
			return this.allOptions;
		},
		getAllInventory : function() {
			return this.allInventory;
		},
		getSizeChartUrl : function () {
			return this.data.sizeChartUrl.trim();
		},
		generateOptions: function () {
			// allOptions is an array of all the available variants by variant type. It is used during the
			// initialization of the display of the pickers. Using an array because object iteration is
			// unreliable between browsers.
			var skus = this.data.skus,
					variantTypes = this.data.variantTypes,
					variantTypeLength = variantTypes.length,
					variantType = '',
					variant = {},
					options = [],
					variantTypeId,
					skusLength = skus.length,
					optionList = [],
					optionData = [],
					variantSwatch,
					isSelected,
					inventoryStock = [],
					inventoryStockList = [],
					i,
					j;

			// for each type
			for (i = 0; i < variantTypeLength; i++) {
				variantType = (variantTypes[i].displayName).toLowerCase();
				variantTypeId = variantTypes[i].id;
				optionList = [];
				for (j = 0; j < skusLength; j++) {
					variant = skus[j].variants[variantTypeId];
					if(skus[j].inventory !== undefined && Number(skus[j].inventory) <= 0) {
						inventoryStock = [skus[j].catalogRefId, variant.id, variant.displayName, skus[j].inventory];
						inventoryStockList.push(inventoryStock);
					}
					variantSwatch = (variantType == 'color') ? '/images/swatch/' + this.data.productId + '/' + variant.displayName.toLowerCase().replace(/[^a-z]/ig, "_") + '.jpg' : '';
					if(skus[j].catalogRefId === this.data.prevSku){
						isSelected = true;
					}else {
						isSelected = false;
					}
					optionData = [variant.id, variant.displayName, variantSwatch, isSelected];
					optionList.push(optionData);
				}
				optionList = UTILITIES.dedup(optionList);
				options.push(optionList);
			}
			this.allOptions = options;
			this.allInventory = inventoryStockList;
		},
		getFilteredOptions: function (selectedOptions) {
			var variantTypes = this.data.variantTypes,
					variantTypeLength = variantTypes.length,
					tmpArray = [],
					allSkus = this.data.skus,
					filteredSkuArray,
					options = {},
					inventory = {},
					variantTypeId,
					optionKey = '',
					i,
					j,
					k,
					selectedVariantType;

			/*
			 * For each Variant Type selection, filter down the matching skus for the other selectors'
			 * selected elements. From the filtered sku set, get back all available options for this
			 * option type
			 */
			for (i = 0; i < variantTypeLength; i++) {
				variantTypeId = variantTypes[i].id;
				filteredSkuArray = allSkus;
				for (selectedVariantType in selectedOptions) {

					if (selectedVariantType != variantTypeId) {
						// look at each sku and see if this variant value is present
						for (j = 0; j < filteredSkuArray.length; j++) {
							if (filteredSkuArray[j].variants[selectedVariantType] !== undefined) {
								if (filteredSkuArray[j].variants[selectedVariantType].displayName == selectedOptions[selectedVariantType]) {
									tmpArray.push(filteredSkuArray[j]);
								}
							}
						}
						filteredSkuArray = tmpArray;
						tmpArray = [];
					}
				}

				for (k = 0; k < filteredSkuArray.length; k++) {
					if (filteredSkuArray[k].variants[variantTypeId] !== undefined) {
						optionKey = filteredSkuArray[k].variants[variantTypeId].displayName;
						if (options[variantTypeId] === undefined) {
							options[variantTypeId] = variantTypes[i];
							options[variantTypeId].variants = {};
							options[variantTypeId].variants[optionKey] = filteredSkuArray[k].variants[variantTypeId];
							options[variantTypeId].variants[optionKey]["stock"] = filteredSkuArray[k].inventory;
						}
						else if (options[variantTypeId].variants[optionKey] === undefined) {
							options[variantTypeId].variants[optionKey] = filteredSkuArray[k].variants[variantTypeId];
							options[variantTypeId].variants[optionKey]["stock"] = filteredSkuArray[k].inventory;
						}
					}
				}
			}

			return options;
		},
		getSkuVariants: function (skuId) {
			var skus = this.data.skus,
					variants = {},
					i,
					j;
			for (i = 0; i < skus.length; i++) {
				if (skus[i].catalogRefId == skuId) {
					for (j in skus[i].variants) {
						if (variants[j] === undefined) {
							variants[j] = skus[i].variants[j].id;
						}
					}
					break;
				}
			}
			return variants;
		},
		getFilteredSkus: function (selectedOptions) {
			var tempArray = [],
					filteredSkus = this.data.skus,
					variantType,
					i;

			for (variantType in selectedOptions) {
				for (i = 0; i < filteredSkus.length; i++) {
					if (filteredSkus[i].variants[variantType] !== undefined && filteredSkus[i].variants[variantType].displayName == selectedOptions[variantType]) {
						tempArray.push(filteredSkus[i]);
					}
				}
				filteredSkus = tempArray;
				tempArray = [];
			}
			return filteredSkus;
		}
	};

	global[namespace].ProductController = function (data, isModal) {
		this.productId = data.productId;
		this.catalogRefId = '';
		this.productData = new global[namespace].ProductData(data);
		this.$container = (isModal) ? $('#quickView-product-' + this.productId) : $('#product-' + this.productId);
		if (this.$container.length === 0) {
			return;
		}
		this.$skuPicker = this.$container.find('.product-form-pickers');
		this.$formCatalogRefId = this.$container.find('.input-selected-sku');
		this.options = {
			swatchClass : 'option-link ',
			selectedClass : 'active',
			disabledClass : 'disabled',
			dropdownClass: 'option-dropdown',
			optionSwatches: 'option-swatches'
		};

		this.init();

	};
	global[namespace].ProductController.prototype = {
		init : function () {
			var self = this,
					allOptions = {},
					variantTypes = [],
					variantTypeDisplayName = '',
					variantTypeName = '',
					variantTypeId,
					variantOptions = [],
					selectorGroupHtml = '',
					availableSkus = this.productData.data.skus,
					productOutOfStock=this.productData.data.productOutOfStock,
					bopisOnlyAvailable=this.productData.data.bopisOnlyAvailable,
					templateType = this.productData.data.template,
					sizeChartUrl = this.productData.getSizeChartUrl(),
					addToCart = false,
					$addToCartButton = $('.add-to-cart-submit'),
					$bopisOrderRadio = $('.bopis-order'),
					$shipHomeRadio = $('#shipping-order'),
					isEdsPPSOnly = this.productData.data.isEdsPPSOnly,
					isBopisOrder = this.productData.data.isBopisOrder,
					outOfStock,
					allInventoryStock = {},
					x,
					y,
					z;

			this.productData.cleanSkuData();
			if( (productOutOfStock=='true' && bopisOnlyAvailable != 'true') || (isEdsPPSOnly == 'true' && isBopisOrder =='true')) {
				$addToCartButton.addClass('disabled');
			}
			
			if (templateType === 'PICKER') {
				// normal pickers
				if (this.productData.hasSkus()){
					allOptions = this.productData.getAllOptions();
					variantTypes = this.productData.getVariantTypes();
					selectorGroupHtml = '';
					allInventoryStock = this.productData.getAllInventory();

					for (x=0; x < variantTypes.length; x++){

						variantTypeDisplayName = variantTypes[x].displayName;
						variantTypeName = variantTypeDisplayName.toLowerCase().replace(/[^a-z]/ig, "-");
						variantTypeId = variantTypes[x].id;
						variantOptions = self.removeDuplicates(allOptions[x], allOptions[x][1]);
						//variantOptions = Options[x];
						variantTypeDisplayName = 'Select ' + variantTypeDisplayName;

						if (variantTypeName == 'color') {
							// color swatches
							var options = [];
							for (y=0; y<variantOptions.length; y++) {
								options.push({type: variantTypeName, optionValue: variantOptions[y][1], optionId: variantOptions[y][1], imageSrc: variantOptions[y][2], isSelected: variantOptions[y][3]});
							}
							selectorGroupHtml = Mustache.render(TEMPLATES.templatePickerTypeSwatch, {title: variantTypeName, type: variantTypeName, typeId: variantTypeId, availableOptions: options});
							if (selectorGroupHtml !== '') {
									this.$skuPicker.append(selectorGroupHtml);
							}
						}
						else {
							// select dropdowns
							var options = [];
							options.push({type: variantTypeName, optionValue: variantTypeDisplayName, optionId: ''});
							if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
								for (y=0; y<variantOptions.length; y++) {
									options.push({type: variantTypeName, optionValue: variantOptions[y][1], optionId: variantOptions[y][1], isSelected: variantOptions[y][3], outOfStock: outOfStock});
								}
							}
							else{
								for (y=0; y<variantOptions.length; y++) {
									outOfStock = false;
									for(z = 0; z<allInventoryStock.length; z++){
										if(variantOptions[y][1] === allInventoryStock[z][2]){
											outOfStock = true;
											break;
										}
									}
									options.push({type: variantTypeName, optionValue: variantOptions[y][1], optionId: variantOptions[y][1], isSelected: variantOptions[y][3], outOfStock: outOfStock});
								}
							}
							selectorGroupHtml = Mustache.render(TEMPLATES.templatePickerTypeDropdown, {title: variantTypeDisplayName.toLowerCase(), type: variantTypeName, typeId: variantTypeId, availableOptions: options, mediaUrl: sizeChartUrl});
							if (selectorGroupHtml !== '') {
									this.$skuPicker.append(selectorGroupHtml);
							}
						}
					}

					if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
						addToCart = true;
					}else{
						for(var i = 0; i<availableSkus.length; i++){
							if(allInventoryStock !== undefined && Number(availableSkus[i].inventory) > 0){
								addToCart = true;
								break;
							}
						}
					}

					if(!addToCart) {
						$addToCartButton.addClass('disabled');
					}else{
						$addToCartButton.removeClass('disabled');
					}

					this.$selectors = $('div.product-options', this.$skuPicker);
					var $target = $('.'+this.options.dropdownClass),
					$targetOption = $target.find('.'+this.options.selectedClass),
					optionDropDownId = $targetOption.attr('data-id') || '',
					$targetSwatchOption = $('.'+this.options.optionSwatches).find('.'+this.options.selectedClass),
					optionSwatchId = $targetSwatchOption.attr('data-id') || '';
					if(optionDropDownId !== ''){
						this.changeAttribute($targetOption);
					}
					if(optionSwatchId !== ''){
						this.changeAttribute($targetSwatchOption);
					}
				}
				else {
					// No skus, sold out.
					return;
				}

				// action listeners (click/change)
				this.$skuPicker.on('click', '.' + self.options.swatchClass, function (e) {
					// listen for clicks on swatch templates
					var $target = $(e.currentTarget);
					self.changeAttribute($target);
				}).on('change', '.' + self.options.dropdownClass ,function(e){
					//listen for change if we use the dropdown template
					var $target = $(e.target.options[e.target.selectedIndex]);
					self.changeAttribute($target);
				});

			}

		},
		removeDuplicates :  function(originalArray, prop) {
			var newArray = [],
			lookupObject  = {},
			i;

			for(i in originalArray) {
				lookupObject[originalArray[i][1]] = originalArray[i];
			}

			// To ensure that any "selected" variant is not
			// being removed as a dupe
			for(i in originalArray) {
				if(originalArray[i][3]===true) {
					lookupObject[originalArray[i][1]] = originalArray[i];
				}
			}

			for(i in lookupObject) {
				newArray.push(lookupObject[i]);
			}
			return newArray;
		 },
		changeAttribute : function ($target) {
			var // autoSelect = true,
					optionType = '',
					optionId = '',
					triggerOptionType = $target.attr('data-typeid'),
					triggerOptionId = $target.attr('data-id'),
					selectedOptions = {},
					selectedClass = this.options.selectedClass;

			// select this variant
			this.selectVariant(triggerOptionType, triggerOptionId);

			// Generate SelectedOptions
			this.$selectors.each(function () {
				var $this = $(this);
						optionType = $this.attr('data-typeid');
						optionId =  $this.find('.' + selectedClass).attr('data-id') || '';
				if (optionId !== '') {
					selectedOptions[optionType] = optionId;
				}
			});

			this.updateSelectors({'selectedOptions': selectedOptions, 'triggerOptionType': triggerOptionType});
		},
		updateSelectors : function (options) {
			var self = this,
					selectedOptions = options.selectedOptions,
					// autoSelect = (options.autoSelect !== undefined) ? options.autoSelect : false,
					availableOptions = this.productData.getFilteredOptions(selectedOptions),
					variantTypes = this.productData.getVariantTypes(),
					availableSkus = this.productData.data.skus,
					optionId = '',
					skuSelected = true,
					isDisabled = false,
					inStock = true,
					skuEnabled = true,
					swatchClass = this.options.swatchClass,
					dropdownClass = this.options.dropdownClass,
					selectedClass = this.options.selectedClass,
					addToCart = false,
					$addToCartButton = $('.add-to-cart-submit'),
					$bopisOrderRadio = $('.bopis-order'),
					$shipHomeRadio = $('#shipping-order');

			this.$selectors.each(function(i) {
				var $selector = $(this),
						optionType = $selector.attr('data-typeid'),
						selectedValue = $selector.find('.' + selectedClass).attr('data-id'),
						enabledItems = 0,
						option;

				// update dropdowns
				$selector.find('option').each(function(index) {
					if (index > 0) {
						var $this = $(this);
						optionId = $(this).attr('data-id');
						isDisabled = (availableOptions[optionType] === undefined || availableOptions[optionType].variants[optionId] === undefined) ? true : false;

						if (isDisabled) {
							$this.addClass('hide').removeClass(selectedClass);
						}
						else {
							$this.removeClass('hide');
							addToCart = true;
							enabledItems++;
						}
					}
				});

				// update color swatches
				$selector.find('.' + swatchClass).each(function() {
					var $this = $(this);
					optionId = $(this).attr('data-id');
					isDisabled = (availableOptions[optionType] === undefined || availableOptions[optionType].variants[optionId] === undefined) ? true : false;

					if (isDisabled) {
						$this.addClass('hide').removeClass(selectedClass);
					}
					else {
						$this.removeClass('hide');
						enabledItems++;
					}
				});


				// auto-select the single item (only if there wasn't already a selection), then re-update in case it narrows other selectors.
				// if (enabledItems == 1 && selectedOptions[optionType] === undefined && autoSelect === true) {
				// 	for (option in availableOptions[optionType].variants) {
				// 		selectedOptions[optionType] = availableOptions[optionType].variants[option].id;
				// 	}
				// 	self.updateSelectors({'selectedOptions': selectedOptions, 'updateQty': updateQty});
				// 	return;
				// }

				//resets or selects item in this VariantGroup
				if (selectedOptions[optionType] === undefined) {
					self.selectVariant(optionType);
					skuSelected = false;
				}
				else {
					selectedValue = selectedOptions[optionType];
					self.selectVariant(optionType, selectedValue);
				}

			});

			if(!addToCart) {
				console.log('not addtocart');
				$addToCartButton.addClass('disabled');
			}else{
				console.log(' addtocart');
				$addToCartButton.removeClass('disabled');
			}

			// update sku information on page
			var $productSku = $('.product-sku'),
					$skuNumber = $('.sku-number'),
					$productSkuModel = $('.product-sku-model'),
					$modelNumber = $('.model-number'),
					$price = this.$container.find('.product-price'),
					priceHtml = '',
					$inventoryEmail = $('.no-inventory-email-trigger');

			if (skuSelected !== false) {
				var selectedSku = self.productData.getFilteredSkus(selectedOptions)[0],
						productId = this.productData.productId;
				this.catalogRefId = selectedSku.catalogRefId;

				$skuNumber.html(this.catalogRefId);
				$productSku.removeClass('hide');
				$modelNumber.html(selectedSku.modelNumber);
				if(Boolean(selectedSku.modelNumber)){
					$productSkuModel.removeClass('hide');
				}else{
					if(!$productSkuModel.hasClass('hide')){
						$productSkuModel.addClass('hide');
					}
				}
				if (selectedSku.hidePrice == 'true') {
					priceHtml = Mustache.render(TEMPLATES.templateHidePrice);
				} else {
					if (selectedSku.sale == 'true') {
						if (selectedSku.clearance == 'true') {
							priceHtml = Mustache.render(TEMPLATES.templateClearancePrice, {originalPrice: selectedSku.originalPrice.replace('$', ''), salePrice: selectedSku.salePrice.replace('$', '')});
						} else {
							priceHtml = Mustache.render(TEMPLATES.templateSalePrice, {originalPrice: selectedSku.originalPrice.replace('$', ''), salePrice: selectedSku.salePrice.replace('$', '')});
						}
					}
					else {
						priceHtml = Mustache.render(TEMPLATES.templateRegularPrice, {regularPrice: selectedSku.regularPrice.replace('$', '')});
					}
				}

				
				// enable/disable add to cart button
				if (selectedSku.inventory == '0' || selectedSku.inventory == null || selectedSku.inventory == '') {
					$addToCartButton.addClass('disabled');
					//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + this.catalogRefId);
				}
				else {
					$addToCartButton.removeClass('disabled');
					
					if(selectedSku.bopisOnlyAvailable == 'true' && selectedSku.productOutOfStock == 'true') {
						$shipHomeRadio.attr('disabled',true);
						$bopisOrderRadio.attr('checked', true);
					} else {
						$shipHomeRadio.attr('disabled',false);
					}
					//$inventoryEmail.addClass('hide');
				}
				
				if (document.getElementById('bopis-order') !== null && document.getElementById('bopis-order').checked) {
					var storeId = $('#bopis-store-id').val() !== '' ? $('#bopis-store-id').val() : $('#homestore').val();
					if(selectedSku !== '' && storeId !== '' && storeId !== undefined){
						$.ajax(CONSTANTS.contextPath + '/sitewide/json/updateMyHomeStoreSuccess.jsp?productId=' +this.productId + '&skuId=' + this.catalogRefId + '&storeId=' + storeId, {
							cache: false,
							dataType : 'json',
							success: function(data) {
								$('.bopis-location-info').html(Mustache.render(global[namespace].templates.bopisStoreLocationInfoTemplate, data));
								if(data.eligible !== "true"){
									$addToCartButton.addClass('disabled');
									//document.getElementById('bopis-order').addClass('disabled');
									$bopisOrderRadio.attr('disabled',true);
									//$inventoryEmail.removeClass('hide').attr('href', CONSTANTS.contextPath + '/browse/ajax/backInStockModal.jsp?productId=' +productId + '&skuId=' + this.catalogRefId);
								}else{
									$addToCartButton.removeClass('disabled disable-add-to-cart');
									$bopisOrderRadio.attr('disabled',false);
									//$inventoryEmail.addClass('hide');
								}
							},
							error: function() {
								console.log("error on update bopis location info");
							}
						});
					}
				}
			}
			else {
				var product = self.productData.data;
				this.catalogRefId = '';
				$productSku.addClass('hide');
				
				if (product.hidePrice == 'true') {
					priceHtml = Mustache.render(TEMPLATES.templateHidePrice);
				} else {
					if (product.priceRange == 'true') {
						priceHtml = Mustache.render(TEMPLATES.templateRegularPrice, {regularPrice: product.minPrice.replace('$', '') + ' - ' + product.maxPrice.replace('$', '')});
					}
					else {
						if (product.sale == 'true') {
							priceHtml = Mustache.render(TEMPLATES.templateSalePrice, {originalPrice: product.originalPrice.replace('$', ''), salePrice: product.salePrice.replace('$', '')});
						}
						else {
							priceHtml = Mustache.render(TEMPLATES.templateRegularPrice, {regularPrice: product.regularPrice.replace('$', '')});
						}
					}
				}
			}
			$price.html(priceHtml);
			this.$formCatalogRefId.val(this.catalogRefId);
		},
		selectVariant : function (variantTypeId, variantId) {
			var selectedClass = this.options.selectedClass,
					$selectedItem;

			if (variantId) {
				$selectedItem = this.$skuPicker.find('[data-id="' + variantId + '"]');

				// already selected? exit.
				if ($selectedItem.hasClass(selectedClass)) {
					return;
				}

				// change the selected class
				this.$skuPicker.find('[data-typeid=' + variantTypeId + ']').removeClass(selectedClass);
				$selectedItem.addClass(selectedClass);

				// clear errors
				this.$skuPicker.find('[data-typeid=' + variantTypeId + '] .product-option-errors').html('');
				UTILITIES.form.hideErrors($('#add-to-cart-form'));
			}
			else {
				// deselect all options for this variant type
				this.$skuPicker.find('[data-typeid=' + variantTypeId + ']').removeClass(selectedClass);
			}
		},
		showSelectionErrors : function() {
			var that = this,
					selectedClass = this.options.selectedClass;
			this.$selectors.each(function(i) {
				var $selector = $(this),
						variantId,
						variantName,
						errorMessage = '';

				if ($selector.find('.' + selectedClass).length === 0) {
					variantId = $selector.attr('data-typeid');
					variantName = that.productData.getVariantTypeName(variantId);
					if (variantName !== '') {
						errorMessage = 'Please select a ' + variantName;
					}
					else {
						errorMessage = 'Please select all options';
					}
					$selector.find('.product-option-errors').html(errorMessage);
				}
			});
		}
	};

})(this, window.jQuery, "KP");
