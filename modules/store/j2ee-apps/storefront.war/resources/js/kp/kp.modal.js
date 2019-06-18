/* =========================================================
 * kp.modal.js
 * =========================================================
 * Function to launch modal. Typical usage is
 * <a href="yourlink.jsp" class="modal-trigger" data-target="modalId">Link</a>
 * Link will open href in modal specified by modal target.
 * Has fallback for cross protocol links which are opened in an iframe.
 *
 * @requires postmessage.js
 * @requires Modernizr.js
 * ========================================================= */

(function (global, $, namespace) {
	/*jshint validthis: true */
	"use strict";

	var Modal = function Modal(element, options) {
				this.init ('modal', element, options);
			},
			CONSTANTS = global[namespace].constants,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			ajaxError = '<div class="error">' + CONSTANTS.ajaxError + '</div>',
			transition = Modernizr.csstransitions;

	function bindings(bindType) {
		var delegateType = (bindType === "bind")?'delegate':'undelegate';
		this.$element[delegateType]('[data-dismiss="modal"], .cancel', 'click.dismiss.modal', $.proxy(this.hide, this));
	}

	function isExternal(url) {
		var match = url.match(/^([^:\/?#]+:)?(?:\/\/([^\/?#]*))?([^?#]+)?(\?[^#]*)?(#.*)?/);
		if (typeof match[1] === "string" && match[1].length > 0 && match[1].toLowerCase() !== location.protocol) {
			return true;
		}
		if (typeof match[2] === "string" && match[2].length > 0 && match[2].replace(new RegExp(":("+{"http:":80,"https:":443}[location.protocol]+")?$"), "") !== location.host) {
			return true;
		}
		return false;
	}

	function loadUrl (url) {
		var modal = this,
				ajaxOptions = {
					url: url,
					dataType: 'html',
					cache: false,
					success: function (pageData) {
						if (modal.isShown && modal.isContentShown) {
							modal.$element.one('contentHidden', function () {
								showLoadedContent.call(modal, pageData);
							});
						} else {
							showLoadedContent.call(modal, pageData);
						}
					},
					error: function () {
						showLoadedContent.call(modal, ajaxError);
					},
					complete : function () {
					}};
		if (isExternal(url)) {
			if (window['postMessage']) {
				// postMessage Proxy
				loadIframe.call(this, url);
			} else {
				window.location = url;
			}
		} else {
			$.ajax(ajaxOptions);
		}
		this.options.url = '';
	}

	/* iFrame Proxy */
	function loadIframe(url) {
		var modal = this,
				$iframe;

		if (url.indexOf('?') > 0) {
			url = url + '&proxy=true';
		} else {
			url = url + '?proxy=true';
		}

		modal.showLoader();

		if (document.getElementById('proxy')) {
			$iframe = $('#proxy');
		} else {
			$iframe = $('<iframe id="proxy" name="proxy" class="" style="visibility:hidden; float:left;" width="0" height="0" frameborder="0" vspace="0" hspace="0" allowtransparency="true" scrolling="no"></iframe>')
					.appendTo('body');

			pm.bind("setModalContent", function (data) {
				//handle response from iframe
				if (modal.isShown && modal.isContentShown) {
					modal.$element.one('contentHidden', function () {
						showLoadedContent.call(modal, data.content, {proxy: true});
					});
				} else {
					showLoadedContent.call(modal, data.content, {proxy: true});
				}
			});
		}
		$iframe.attr('src', url);
	}

	function loadContent (content){
		showLoadedContent.call(this, content);
		this.options.content = '';
	}

	function showLoadedContent(content, options) {
		var modal = this,
				$loaded = $(content).filter('.ajax-wrapper'),
				controller ='modal',
				action = $loaded.attr('data-action') || '',
				initOptions = {
					$modal : this.$element
				};
		if (options) {
			initOptions = $.extend(initOptions, options);
		}
		modal.$content.empty().html(content);
		global[namespace].init(controller, action, initOptions);
		showContent.call(this);
	}

	function showContent() {
		var modal = this,
				dims;
		modal.hideLoader();
		dims = getDimensions.call(modal);
		if (modal.isShown) {
			resize.call(modal, dims, function (){
				showModalContent.call(modal);
			});
		} else {
			resize.call(modal, dims, function () {
				showModal.call(modal);
			});
		}
	}

	function showModal() {
		var modal = this,
				transitionTimeout, dims;
		reposition.call(modal);
		modal.$modal.addClass('in');
		if (transition) {
			transitionTimeout = setTimeout(function () {
				stateChange.call(modal, 'shown.modal', 'isShown', true);
				bindings.call(modal, 'bind');
			}, 300);
		} else {
			stateChange.call(modal, 'shown.modal', 'isShown', true);
			bindings.call(modal, 'bind');
		}

	}

	function hideModal() {
		var modal = this,
				transitionTimeout;
		modal.$modal.removeClass('in');
		if (transition) {
			transitionTimeout = setTimeout(function () {
				stateChange.call(modal, 'hidden.modal', 'isShown', false);
				bindings.call(modal, 'unbind');
			}, 300);
		} else {
			stateChange.call(modal, 'hidden.modal', 'isShown', false);
			bindings.call(modal, 'unbind');
		}
	}

	function resize(dims, completeCallback) {
		var modal = this,
				getOffsetWidth,
				transitionTimeout,
				transitionEnd = function () {
					// after we've done the resize transformation. release the width and height restrictions
					// this will allow for dynamic content changed inside an already opened modal.
					modal.$modal.css({'height':'auto'
						/* ,'width':'auto' */
					});
					completeCallback();
				};
		if (transition && this.isShown) {
			if (dims.currentWidth !== dims.newWidth || dims.currentHeight !== dims.newHeight) {
				getOffsetWidth = modal.$element[0].offsetWidth; // force reflow
				//set starting dims
				modal.$modal.css({'width': dims.currentWidth, 'height': dims.currentHeight});
				transitionTimeout = setTimeout(transitionEnd, 300);
				//change dims
				modal.$modal.addClass('resize').css({
					/*'width': dims.newWidth, */
					'height': dims.newHeight
					/* ,'margin-left': -(dims.newWidth/2) */
				});
			} else {
				transitionEnd();
			}
		} else {
			modal.$modal.removeClass('resize')
					.css({
						/*'width': dims.newWidth, */
						'height': dims.newHeight
						/*,'margin-left': -(dims.newWidth/2)*/
					});

			transitionEnd();
		}
	}

	function reposition() {
		var modal = this,
				modalPosition,
				docHeight,
				modalHeight;

		docHeight = Math.max($(window).height(), document.documentElement.clientHeight);
		modalHeight = modal.$modal.height();
		modalPosition = (docHeight - modalHeight)/2 + $(document).scrollTop();

		if(docHeight > modalHeight){
			modal.$modal.css('top', modalPosition + 'px');
		} else {
			modal.$modal.css('top', ($(document).scrollTop() + 20) + 'px');
		}
	}

	function getDimensions($loaded) {
		var modal = this,
				dims = {};
		dims.currentWidth = modal.$modal.outerWidth();
		dims.currentHeight = modal.$modal.outerHeight();
		if ($loaded) {
			$loaded.appendTo(modal.$stage.show());
			dims.newWidth = modal.$stage.outerWidth(true) + modal.borderSize;
			dims.newHeight = modal.$stage.outerHeight(true) + modal.borderSize;
			modal.$stage.empty().hide();
		} else {
			dims.newWidth = modal.$content.outerWidth(true) + modal.borderSize;
			dims.newHeight = modal.$content.outerHeight(true) + modal.borderSize;
		}
		return dims;
	}

	function showModalContent() {
		var modal = this,
				transitionTimeout;
		modal.$element.trigger('contentShow.modal');
		modal.$content.addClass('in');

		if (transition) {
			transitionTimeout = setTimeout(function () {
				stateChange.call(modal, 'contentShown', 'isContentShown', true);
			}, 300);
		} else {
			stateChange.call(modal, 'contentShown', 'isContentShown', true);
		}
	}

	function hideModalContent() {
		var modal = this,
				transitionTimeout;
		modal.$element.trigger('contentHide.modal');
		modal.$content.removeClass('in');
		if (transition) {
			transitionTimeout = setTimeout(function () {
				stateChange.call(modal, 'contentHidden.modal', 'isContentShown', false);
			}, 300);
		} else {
			stateChange.call(modal, 'contentHidden.modal', 'isContentShown', false);
		}
	}

	function stateChange(event, statusName, status) {
		var modal = this;
		modal[statusName] = status;
		modal.$element.trigger(event);
	}


	//PUBLIC
	Modal.prototype = {
		constructor: Modal,
		init: function init(type, element, options) {
			if (loggingDebug) {
				console.debug('init modal with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.$element = $(element);
			this.modalClass = this.options.modalClass;
			this.$modal = $('.modal-window', this.$element);
			this.$content = $('.modal-content', this.$element);
			this.$backdrop = $('.modal-backdrop', this.$element);
			this.$stage = $('<div style="position:absolute; left:-9999em; visibility:hidden; display:none" class="modal-content"></div>').appendTo('body');
			this.isLoaderShowing = false;
			this.isShown = false;
			this.isContentShown = true;
			this.borderSize = parseInt(this.$modal.css("borderLeftWidth"),10) + parseInt(this.$modal.css("borderRightWidth"),10);
		},
		toggle: function toggle() {
			return this[!this.isShown ? 'show' : 'hide']();
		},
		show: function show() {
			var $lastOverlay = $('.overlay-wrap:visible').last();

			this.$element.trigger('show.modal');

			// move this overlay after any visible overlays.
			if ($lastOverlay.size() > 0 && this.isShown === false) {
				this.$element.insertAfter($lastOverlay);
			}

			this.showLoader();

			if (this.options.url){
				// ajax content
				loadUrl.call(this, this.options.url);
			} else if (this.options.content) {
				// param content
				loadContent.call(this, this.options.content);
			} else {
				// assume content is already loaded
				showContent.call(this);
			}
		},
		hide: function hide (event) {
			this.$element.trigger('hide.modal');
			this.$element.hide();
			hideModal.call(this);
			this.$backdrop.removeClass('in');
			if (event) {
				event.preventDefault();
			}
		},
		reposition: function resize () {
			reposition.call(this);
		},
		showLoader: function () {
			if (this.isLoaderShowing) {
				return;
			}
			if (this.isShown) {
				hideModalContent.call(this);
				this.$modal.addClass('loading');
			} else {
				this.$element.show();
				this.$backdrop.addClass((this.options.url) ? 'loading in': 'in');
			}
			this.isLoaderShowing = true;
		},
		hideLoader: function () {
			if (!this.isLoaderShowing) {
				return;
			}
			if (this.isShown) {
				this.$modal.removeClass('loading');
			} else {
				this.$backdrop.removeClass('loading');
			}
			this.isLoaderShowing = false;
		}
	};

	$.fn.modal = function modal(option) {
		var el = this,
				options = $.extend({}, $.fn.modal.defaults, typeof option === 'object' && option);
		return el.each(function doModal() {
			var data = $.data(this, 'modal');
			if (!data) {
				$.data(this, 'modal', (data = new Modal(this, options)));
			} else {
				if (typeof option === 'object') {
					$.extend(data.options, option);
				}
			}
			if (typeof option === 'string') {
				data[option]();
			} else if (options.show) {
				data.show();
			}
		});
	};

	$.fn.modal.defaults = {
		show: true,
		id: 'modal-default'
	};

	$.fn.modal.Constructor = Modal;


	$(function () {
		$('body').off('modal').on('click.' + namespace + '.modal', '.modal-trigger', function openModalLink(e) {
			var $this = $(this),
					modalTarget = $this.attr('data-target') || $.fn.modal.defaults.id,
					modalSize = $this.attr('data-size') || '',
					$modalTarget = document.getElementById(modalTarget) ? $('#' + modalTarget) : global[namespace].utilities.createModal(modalTarget, modalSize),
					url = $this.attr('href'),
					option = {'url': url, 'size': modalSize};
			e.preventDefault();
			$modalTarget.modal(option);
		});
	});


}(this, window.jQuery, "KP"));
