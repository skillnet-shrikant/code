/* =========================================================
 * kp.typeahead.js
 * =========================================================

 * ========================================================= */

(function (global, $, namespace) {

	"use strict";

	var Typeahead = function Typeahead(element, options) {
			this.init ('typeahead', element, options);
		},
		CONSTANTS = global[namespace].constants,
		loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug;

	//PUBLIC
	Typeahead.prototype = {
		constructor: Typeahead,
		init: function init(type, element, options) {
			if (loggingDebug) {
				console.debug('init typeahead with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}

			var self = this,
				counter = 0,
				typeaheadResultsSelector = '.typeahead',
				typeaheadSuggestionsSelector = '.typeahead-suggestions',
				typeaheadDetailsSelector = '.typeahead-details',
				$results = $(typeaheadResultsSelector),
				$suggestions = $(typeaheadSuggestionsSelector),
				$details = $(typeaheadDetailsSelector),
				$body = $('body');

			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);
			this.$results = $(typeaheadResultsSelector);
			this.$suggestions = this.$results.children(typeaheadSuggestionsSelector);
			this.$details = this.$results.children(typeaheadDetailsSelector);

			this.$element.keyup(function(e) {
				var searchTerm = String($(this).val());
				if (searchTerm.length >= self.options.trigger_num_chars) {
					//no!
					$details.html('');
					self.showSuggestions(searchTerm, $suggestions, $details);
				}
				else {
					self.hideResults();
				}
			});

			$body.on('mouseenter', '.typeahead-suggestions li a', function(e){
				self.showDetails($(this).data('detail-url'), $details);
			});
			$body.on('mouseleave', '.typeahead-suggestions li a', function(e){
				self.showDetails($(this).data('detail-url'), $details);
			});
			$body.on('focus', '.typeahead-suggestions li a', function(){
				self.showDetails($(this).data('detail-url'), $details);
			});

			// make typeahead details keyboard accessible
			$body.on('keydown', '.typeahead-suggestions a', function(e){
				var keycode = (e.keyCode ? e.keyCode : e.which);
				if (keycode == 9 && !e.shiftKey) {
					if ($('.typeahead-details a').length > 0) {
						e.preventDefault();
						$('.typeahead-details li:first a').focus();
					}
				}
			});
			$body.on('keydown', '.typeahead-details a:last', function(e){
				var keycode = (e.keyCode ? e.keyCode : e.which);
				if (keycode == 9 && !e.shiftKey) {
					var $next = $('.typeahead-suggestions a[data-detail-url="' + $('.typeahead-details').attr('data-detail-url') + '"]').parent().next();
					if ($next.length > 0) {
						e.preventDefault();
						$next.find('a').focus();
					}
					else {
						e.preventDefault();
						$('.keyword-search .keyword-search-button').focus();
						self.hideResults();
					}
				}
			});
			$body.on('keydown', '.typeahead a', function(e){
				var keycode = (e.keyCode ? e.keyCode : e.which);
				if (keycode == 13 || keycode == 32) {
					window.location.href = this.href;
				}
			});

		},
		hideResults : function (){
			this.$element.attr('aria-expanded', false);
			this.$results.addClass('hide');
			this.$suggestions.empty();
			this.$details.empty();
		},
		showSuggestions : function (searchTerm, $suggestions, $details) {
			var self = this,
				$results = this.$results,
				$element = this.$element;
			$.ajax({
//				url: global[namespace].constants.contextPath + '/sitewide/json/typeahead.jsp', // test URL
				url: global[namespace].constants.contextPath + '/typeahead/suggest/' + searchTerm + '.js',
				dataType: 'json',
				success: function(data){
					var query = data.searchTerm,
						queryBold = '<strong>' + query + '</strong>',
						suggestions = data.results,
						output = '',
						count = 0;

					// only show suggestions if there are some
					if (suggestions.length > 0) {
						// make query string bold in suggestions list
						$(suggestions).each(function(e){
							data.results[count].term = this.term.replace(new RegExp(query, 'g'), queryBold);
							count++;
						});

						// apply mustache template
						output = Mustache.render(global[namespace].templates.typeaheadSuggestionsTemplate, data);
						$suggestions.html(output);

						// call the first detailUrl
						if (suggestions.length > 0) {
							self.showDetails(suggestions[0].detailUrl, $details);
						}
						else {
							$details.html();
						}

						// hide results on click
						$('body').on('click', function(){
							self.hideResults();
						});

						$results.removeClass('hide');
						$element.attr('aria-expanded', true);
					}
				},
				error: function(data){
					console.log('error: ', data);
				}
			});
		},
		showDetails : function (detailUrl, $details) {
			var self = this;
			$.ajax({
				url: global[namespace].constants.contextPath + detailUrl,
				dataType: 'json',
				success: function(data){
					if (loggingDebug) {
						console.log(data);
					}

					var sections = data.sections,
						resultsTop = {},
						resultsBottom = [],
						outputTop = '',
						outputBottom = '';

					for (var i=0; i<sections.length; i++) {
						if ($(sections)[i].title == 'Products') {
							resultsTop = sections[i];
						}
						else {
							resultsBottom.push(sections[i]);
						}
					}

					// apply mustache template
					outputTop = Mustache.render(global[namespace].templates.typeaheadDetailsTopTemplate, resultsTop);
					outputBottom = Mustache.render(global[namespace].templates.typeaheadDetailsBottomTemplate, resultsBottom);
					$details.attr('data-detail-url', detailUrl);
					$details.html(outputTop + outputBottom);
				},
				error: function(data){
					console.log('error: ', data);
				}
			});
		}
	};

	$.fn.typeahead = function typeahead(option) {
		var el = this,
			options = $.extend({}, $.fn.typeahead.defaults, typeof option === 'object' && option);
		return el.each(function () {
			var data = $.data(this, 'typeahead');
			if (!data) {
				$.data(this, 'typeahead', (data = new Typeahead(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
		});
	};

	$.fn.typeahead.defaults = {
		trigger_num_chars:3
	};

	$.fn.typeahead.Constructor = Typeahead;


	$(function () {
		$('[data-typeahead]').typeahead();
	});


}(this, window.jQuery, "KP"));
