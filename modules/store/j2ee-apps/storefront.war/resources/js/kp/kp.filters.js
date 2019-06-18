/* =========================================================
 /* =========================================================
 * kp.filters.js
 * Created by KnowledgePath Solutions.
 * ==========================================================
 * Accordion-structured sidebar feature that allows users to
 * refine items displayed on a department or category with
 * predermined filter rules.
 * Also handles applied facet UX (any filters currently applied
 * on the page).
 * NOTE: because the links on the filters and the breadcrumbs
 * are generated by Endeca, we are using the response from the
 * backend to update the interface.
 * ========================================================= */

(function (global, $, namespace) {
	"use strict";

	var CONSTANTS = global[namespace].constants,
		loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
		options = {
			applied_filter_container: '.applied-facets-nav',
			product_grid_container : '.category-product-grid',
			pagination_container : '.pagination',
			sort_container : '.category-dropdowns',
			filter_container : '.filters',
			num_results : '#total-num-recs',
			show_more_threshold : 14
		},
	  // dependencies
		addURLParameter = global[namespace].utilities.addURLParameter,
		showLoader = function() {
			global[namespace].loader.showLoader();
		},
		hideLoader = function(){
			global[namespace].loader.hideLoader();
		};

	/* Filter Menu Object */
	function FilterMenu(element) {
		this.$element = $(element);
		this.init();
	}
	FilterMenu.prototype =  {
		init: function () {
			// this.initializeShowMore();

			/* EVENT LISTENERS */
			/* click event for the facet clicks facet menu */
			this.$element.on('click', '.facet', function(e) {

				// if facet is a link (categories) don't do all this
				if (!$(this).parents('.facet-list').hasClass('links')) {

					/* clicking on a label triggers a second click on the checkbox. We only want to handle one click */
					/* compare e.nodeName to this to see if bubbling is taking place*/
					if (e.target.nodeName == 'LABEL') {
						e.stopPropagation();
						return;
					}

					// add class on click event for checkboxes
					$('input:checkbox').change(function(){
						if ($(this).is(':checked')) {
							$(this).parents('.facet').addClass('active');
							$facet.siblings('.clear-filter').addClass('enabled');
						}
						else {
							$(this).parents('.facet').removeClass('active');

							// if nothing is active hide the clear all filter
							if (!$facet.siblings().hasClass('active')) {
								$facet.siblings('.clear-filter').removeClass('enabled');
							}
						}
					});

					var $facet = $(this),
						getid = $facet.attr('data-id'),
						facetnstate = $facet.attr('data-nstate');

					if (!$facet.hasClass('active')) {
						// add highlighting to facet
						$facet.addClass('active');
						$facet.siblings('.clear-filter').addClass('enabled');
					}
					else {
						// turn off active facet state
						$facet.removeClass('active');
						// remove applied facet item from bar
						$('.applied-facet[data-id="' + getid + '"]').remove();

						// if nothing is active hide the clear all filter
						if (!$facet.siblings().hasClass('active')) {
							$facet.siblings('.clear-filter').removeClass('enabled');
						}
					}

					// ajax request to display the refined results.
					window.location.hash = '#' + facetnstate;
				}
				

			})
			/* click event for the clear link within the facet menu */
			.on('click', '.clear-filter', function(e){
				var common = [],
						clearAllURL = '',
						$clearAll = $(this),
						queryString='',
						contextPath='';

				$clearAll.siblings('.facet.active').each(function(ind){
					var removeNstate = this.getAttribute('data-nstate'),
							index;

					//if the clear all link does not have N value, return the removeNstate
					if ((removeNstate.indexOf('Ntt') !== -1) && (removeNstate.indexOf('N-') === -1)){
						clearAllURL = removeNstate;
						return false;
					}
					/* the url could have a query string, or could be an endeca url (ex /store/browse/shoes-casual-shoes/_/N-10504) */
					if(queryString === '' && removeNstate.indexOf('?') != -1){
						queryString = removeNstate.substring(removeNstate.indexOf('?'), removeNstate.length);
					}
					if(contextPath === '' && (removeNstate.indexOf('N-') != -1 || removeNstate.indexOf('?'))){
						if(removeNstate.indexOf('N-') != -1 ){
							contextPath = removeNstate.substring(0,removeNstate.indexOf('N-'));
						}
						else{
							contextPath = removeNstate.substring(0,removeNstate.indexOf('?'));
						}
					}
					var startI = removeNstate.indexOf('N-') + 2;
					var endI = removeNstate.indexOf('?')!= -1  ? removeNstate.indexOf('?') : removeNstate.length;
					index = removeNstate.substring(startI, endI).split('+');

					if(common.length === 0){
						common = index;
					}
					else {
						common = $.grep(common, function(element) {
							return $.inArray(element, index ) !== -1;
						});
					}
				});

				// remove all active states from respective category's filters incl checkboxes
				$clearAll.parent().find('div').removeClass('active');
				$clearAll.parent().find('input[type=checkbox]').removeClass('active').removeAttr('checked');

				if (common.length > 0) {
					// building the clear-all url using context path, N values, queryStrings from above loop
					clearAllURL = contextPath + 'N-' + common.join('+') + queryString;
				}
				else {
					// there are no facets from other categories, just clear the hash but keep the URL parameters
					// this only happens for search pages
					clearAllURL = '/search?' + window.location.hash.split('?')[1];
				}

				// hide clear-all button
				$clearAll.removeClass('enabled').slideUp();
				window.location.hash = '#' + clearAllURL;
			})
			/* clicks on the show more link within the facet list should show all the hidden facets, then remove the link */
			// .on('click', '.facet-show-more', function() {
			// 	$(this).siblings(':hidden').show().end()
			// 			.closest('.facet-list').addClass('full-list').end()
			// 			.remove();
			// })
			;

			/* Mobile facet menu, cancel or apply button (facets are already applied) */
			$('.close-facets, .apply-facets').click(function(e) {
				if ($('.facet-sidebar').hasClass('open')){
					$('.facet-sidebar').removeClass('open').removeAttr('tabindex');
					$('.off-canvas-wrap').attr('aria-hidden', false);
					$('html').removeClass('no-scroll');
				}
			});
			
			if(typeof $('.applied-facet') === 'object' && $('.applied-facet') !== undefined && $('.applied-facet').length > 0){
				this.$element.find('.facet-list').each(function(){
					var $self = $(this);
					if ($self.attr('data-dim') === undefined || $(this).attr('data-dim') === 'category' ) {
						return;
					}
					$('.applied-facet').each(function(){
						var $appliedFacet = $(this);
						if ($self.attr('data-dim') == $appliedFacet.attr('data-dim')){
							var appliedFacetResult = $appliedFacet.find('.applied-facet-'+$appliedFacet.attr('data-dim'));
							$self.find('.facet').first().before(appliedFacetResult);
						}
					});
				});
			}			
		},
		/* When we initialize, hide the extra items in the facet list. They can be shown by clicking a show more button */
		// initializeShowMore: function($lists){
		// 	var self = this;
		// 	$lists = $lists || this.$element.find('.facet-list');
		// 	$lists.each(function(index, element){
		// 		var $list;
		// 		/* user has clicked the show more for this facet list. Honor it and don't re-collapse */
		// 		if (/full-list/.test(element.getAttribute("class"))){
		// 			return;
		// 		}
		// 		$list = $(element);
		// 		if ($list.find('.facet').length > options.show_more_threshold + 1) {
		// 			$list.find('.facet:gt(' + options.show_more_threshold + ')').hide();
		// 			if ($.find('.facet-show-more', element).length === 0) {
		// 				$list.append('<div class="facet-show-more" tabindex="0">See All</div>');
		// 			}
		// 		}
		// 	});
		// },
		applyFacets: function(response){
			// if the facets were not clicked, this iteration will mark them as clicked
			$(response).find('.applied-facet').each(function(){
				var $responseAppliedFacet = $(this),
						facetidRes = $responseAppliedFacet.attr('data-id'),
						$facetMatch = $('.facet[data-id="' + facetidRes + '"]');

				if (!$facetMatch.hasClass('active')) {
					// add active class
					$($facetMatch).addClass('active');

					// check the checkbox
					$facetMatch.find('[type="checkbox"]').prop('checked', true);

					// enable the clear all filter
					if (!$facetMatch.siblings('.clear-filter').hasClass('enabled')) {
						$facetMatch.siblings('.clear-filter').addClass('enabled');
					}
				}
			});
		},
		mergeRefinements: function(response) {
			var self = this,
					$response = $(response);
			
			//compare existing DOM with ajax response and merge. Disables irrelevant refinements
			$response.find('.facet-list').each(function(){
				
				var $responseRefinementMenu = $(this);


				
				$('.facet-list').each(function(){
					if ($responseRefinementMenu.attr('data-dim') === undefined || $(this).attr('id') === 'category' ) {
						return;
					}
					/* cloning to manipulate dom off canvas before inserting updated content */
					var $originalRefinementMenu = $(this),
							$originalRefinementMenuClone = $originalRefinementMenu.clone();

					
					//Iterate only if the facet type is the same
					if ($originalRefinementMenuClone.attr('data-dim') == $responseRefinementMenu.attr('data-dim')){
						$($responseRefinementMenu).find('.facet').each(function(index){
							var $responseFacet = $(this),
									isNewFacet = true;
							$($originalRefinementMenuClone).find('.facet').each(function(index){
								var $originalFacet = $(this);
								if ($originalFacet.attr('data-id') == $responseFacet.attr('data-id')){
									$originalFacet.replaceWith($responseFacet);
									//Marking all visited refinement links
									$responseFacet.attr('data-visited','y');
									isNewFacet = false;
								}
							});

							// If the domFacet was not assigned to any of the existing DOM, the responseFacet is new - hence append.
							if (isNewFacet) {
								$originalRefinementMenuClone.find('.facet').last().after($responseFacet);
								$responseFacet.attr('data-visited','y');
							}
						});

						/* Hide any extra items */
						// self.initializeShowMore($originalRefinementMenuClone);

						/* insert updated content */
							$originalRefinementMenu.replaceWith($originalRefinementMenuClone);
					}
				});
			});


			/*
			 * Loop through the facets, check for facets not visited in previous loop through the response facets - disable
			 * them as they are not present in the ajax response - hence not valid anymore. For all the active facets, update
			 * the attributes in the applied facets bar.
			 */
			$('.facet').each(function(index){
				var $originalFacet = $(this),
						$appliedFacet;
				if ($originalFacet.parent('div').attr('id') === 'category'){
					return;
				}
				if (!$originalFacet.hasClass('active')){
					if ($originalFacet.attr('data-visited') != 'y') {
						// making unavailable doesn't work properly with the data heirarchy we have. let's just remove it instead
						// $originalFacet.addClass('unavailable');
						$originalFacet.addClass('hide');
						$originalFacet.find('.swatch').removeAttr('tabindex');
						if ($originalFacet.find('[type="checkbox"]')) {
							$originalFacet.find('[type="checkbox"]').prop('disabled', true);
						}
					}
					else {
						// removing flag for next iteration
						$originalFacet.removeAttr('data-visited');
					}
				}
				else {
					$appliedFacet = $(options.applied_filter_container).find('[data-id="' + $originalFacet.attr('data-id') + '"]');
					if ($appliedFacet.length > 0) {
						$originalFacet.attr('data-nstate', $appliedFacet.attr('data-nstate'));
						$originalFacet.find(".ref-count").html(' ('+$appliedFacet.attr('data-count')+')');
						if($originalFacet.find('.swatch').length > 0) {
							$originalFacet.find(".icon-check").css('display', 'inline-block');
						}
					}
				}
			});
			
			this.$element.find('.accordion-container').each(function(){
				var prodAttrbs = $(this).find('.facet-list'),
				totalFacets = 0,
				visible = false;
				if (prodAttrbs.attr('data-dim') === 'Category'){
					return;
				}
				prodAttrbs.find('.facet').each(function(){
					if(!$(this).hasClass('hide')){
						totalFacets++;
						visible = true;
					}
				});
				if(totalFacets > 10){
					prodAttrbs.parents('.facet-body').addClass('scrollbar');
				}else{
					prodAttrbs.parents('.facet-body').removeClass('scrollbar');
				}
				if(visible){
					$(this).show();
				}else{
					$(this).hide();
				}
			});
		},
		update: function(response) {
			// Refresh left nav after merge
			this.applyFacets(response);
			this.mergeRefinements(response);
			if (KP.analytics) {
				KP.analytics.sendBrowsePageEvents();
				KP.analytics.sendProductViews();
			}
		}
	};

	/* Sort Menu Object*/
	function SortMenu(element) {
		this.$element = $(element);
		this.buttonTemplate = '{{text}} <span aria-hidden="true" class="icon icon-arrow-down"></span>';
		this.init();
	}
	SortMenu.prototype = {
		init : function () {
			// sort options - defined here because the facets should know about the sort user selected
			this.$element.on('click','a', function(e) {
				e.preventDefault();

				// if user clicks on already selected item, we don't need to do anything.
				if (/active/.test(e.target.getAttribute("class"))) {
					return;
				}

				var selectedSortValue = $(e.target).attr('data-sortvalue'),
						No = decodeURI(global[namespace].utilities.getURLParameter(selectedSortValue, 'No')),
						Nrpp = decodeURI(global[namespace].utilities.getURLParameter(selectedSortValue, 'Nrpp'));

				// fix page number before setting hash so product grid is correct in response
				if (No !== '' && Nrpp !== '') {
					selectedSortValue = selectedSortValue.replace('No=' + No,'No=' + Math.floor(parseInt(No) / parseInt(Nrpp)) * parseInt(Nrpp));
				}

				window.location.hash = '#' + selectedSortValue;
			});
		},
		update : function(response) {
			var currentHash = window.location.hash,
					Ns = decodeURI(global[namespace].utilities.getURLParameter(currentHash, 'Ns')),
					Nrpp = decodeURI(global[namespace].utilities.getURLParameter(currentHash, 'Nrpp'));

			this.$element.html($(response).find(options.sort_container).html());
			$('.category-sort, .category-items-per-page').dropdown();

			// display active sort/items-per-page in title
			$('#category-sort-menu a').removeClass('active');
			$('#items-per-page-menu a').removeClass('active');
			if (Ns !== '') {
				$('#category-sort-menu a[data-sortparam="' + Ns + '"]').addClass('active');
				this.setButtontext(this.$element.find('#category-sort-title'), this.$element.find('#category-sort-menu a.active').html());
			}
			if (Nrpp !== '') {
				$('#items-per-page-menu a[data-sortparam="' + Nrpp + '"]').addClass('active');
				this.setButtontext(this.$element.find('#items-per-page-title'), this.$element.find('#items-per-page-menu a.active').html());
			}
		},
		setButtontext : function ($button, text){
			if ($button && $button !== '' && text && text !== '') {
				$button.html(Mustache.render(this.buttonTemplate, {text: text.trim()}));
			}
		}
	};

	/* Product Grid Object */
	function ProductGrid(element) {
		this.$element = $(element);
		this.init();
	}
	ProductGrid.prototype = {
		init : function(){},
		update: function(response) {
			/* Update grid contents and initialize responsive images */
			this.$element.html($(response).find(options.product_grid_container).html());
			global.picturefill();

			var emptyResultsMessage = $(response).find('#null-filters-message').html();
			if (emptyResultsMessage !== '') {
				$('#null-filters-message').html(emptyResultsMessage).show();
			}
			else {
				$('#null-filters-message').empty().hide();
			}
		}
	};

	/* Applied Filters Object*/
	function AppliedFilters(element) {
		this.$element = $(element);
		this.init();
	}
	AppliedFilters.prototype = {
		init : function () {
			// ajax request to display the refined results.
			this.$element.on('click', '.applied-facet-item', function(e) {
				window.location.hash = '#' + $(this).attr('data-nstate');
			});
		},
		update: function(response) {
			// update the applied facets
			this.$element.html($(response).find('#applied-facet-breadcrumbs').html());
			$('#applied-facet-breadcrumbs').remove();
		}
	};

	/* Pagination Object */
	function Pagination(element) {
		this.$element = $(element);
		this.init();
	}
	Pagination.prototype = {
		init : function(){
			// pagination ajax
			$('.category .pagination, .search .pagination').on('click', function(event) {
				event.preventDefault();
				var $selector = $(event.target),
						paginationURL = '';
					if ($selector.hasClass('page-num')) {
						paginationURL = $selector.attr('href');
					}
					else {
						paginationURL = $selector.parent().attr('href');
					}
					
					if(typeof paginationURL === 'undefined' ){
						return;
					}
					
					window.location.hash = '#'+paginationURL;
					global[namespace].utilities.showLoader();

					$.ajax(paginationURL, {
						success: function(data) {
							global[namespace].utilities.hideLoader();
							$('.pagination').html($(data).find('.pagination').html());
							$('.category-product-grid').html($(data).find('.category-product-grid').html());
							/* load responsive images */
							global.picturefill();
							$('html, body').animate({scrollTop: 0}, 400);
						},
						error: function() {
							global[namespace].utilities.hideLoader();
						}
					});
			});
		},
		update: function(response) {
			this.$element.html($(response).find(options.pagination_container).html());
		}
	};

	/* Number of Results Object*/
	function NumberOfResults(element) {
		this.$element = $(element);
		// this.init();
	}
	NumberOfResults.prototype = {
		// init : function(){},
		update: function(response) {
			// update the total number of search results
			this.$element.html($(response).find('#total-num-recs').html());
		}
	};

	/* the Controller */
	function FilterController(){
		this.init();
	}
	FilterController.prototype = {
		init: function() {
			if (loggingDebug) {
				console.debug('init filter controller');
			}

			var self = this;
			this.appliedFilters = new AppliedFilters(options.applied_filter_container);
			this.productGrid = new ProductGrid(options.product_grid_container);
			this.pagination = new Pagination(options.pagination_container);
			this.sortMenu = new SortMenu(options.sort_container);
			this.filterMenu = new FilterMenu(options.filter_container);
			this.numResults = new NumberOfResults(options.num_results);

			/* Handle hash change */
			$(window).on('hashchange', function(e) {
				if (window.location.hash.indexOf('#') != -1){
					var hashUrl = window.location.hash.substring(1),
							pathname = window.location.pathname;
					if (hashUrl == pathname) {
						// reload page without hash to ensure back button is not an ajax request
						window.location = pathname;
					}
					else {
						//if(digitalData){
						//	digitalData.products = [];//re initialized
						//}
						self.makeEndecaRequest(hashUrl);
					}
				}
				else {
					//non hash url handle
					self.makeEndecaRequest(e.originalEvent.newURL);
				}
			});

			/* Fire the ajax request if the url has hash in it */
			$(window).on('load', function(e) {
				if(window.location.hash !== '' && (window.location.hash.indexOf('#') != -1)){
					$(window).trigger('hashchange');
				}
			});

		},
		makeEndecaRequest : function(url){
			var self = this;
			showLoader();
			$.ajax(url, {
				cache: false,
				success: function(data) {
					hideLoader();
					self.updatePageWithResults(data);
				},
				error: function() {
					hideLoader();
				}
			});
		},
		updatePageWithResults: function(data) {
			//Refresh product grid, sort options, pagination, breadcrumbs with ajax response
			this.pagination.update(data);
			this.sortMenu.update(data);
			this.productGrid.update(data);
			this.appliedFilters.update(data);
			this.filterMenu.update(data);
			this.numResults.update(data);
		}
	};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].FilterController = FilterController;

}(this, window.jQuery, "KP"));
