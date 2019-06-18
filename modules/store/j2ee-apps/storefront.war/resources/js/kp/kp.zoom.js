(function (global, $, namespace) {

	"use strict";

	/* edge tests */
	function overleft(x, width, left) {
		return (x - (width + 2) / 2) < left;
	}
	function overright(x, width, right) {
		return (x + (width + 2) / 2) > right;
	}
	function overtop(y, height, top) {
		return (y - (height + 2) / 2) < top;
	}
	function overbottom(y, height, bottom) {
		return (y + (height + 2) / 2) > bottom;
	}

	// helper for browsers that can't measure unless we have dom insertion
	function measure (el){
		var $el = $(el).clone(false),
				dims = {};
		$el.css('visibility','hidden').css('position','absolute');
		$el.appendTo('body');
		dims.width = $el.width();
		dims.height = $el.height();
		$el.remove();
		return dims;
	}

	var Zoom = function (element, options) {
				this.init ('zoom', element, options);
			},
			CONSTANTS = global[namespace].constants,
			CONFIG = global[namespace].config,
			TEMPLATES = global[namespace].templates,
			$window = $(window);

	Zoom.prototype = {
		constructor: Zoom,
		init : function (type, element ,options) {
			var self = this,
					supportTouch = Modernizr.touchevents,
					startEvent = supportTouch ? "touchstart" : "mouseenter",
					stopEvent = supportTouch ? "touchend" : "mouseleave",
					moveEvent = supportTouch ? "touchmove" : "mousemove";

			this.$element = $(element);
			this.options = $.extend({}, $.fn[type].defaults, options);
			this.zoomLoaded = false;
			this.loadtest = { main: false, magnified: false };
			this.isActive = false;
			this.isHovering = false;
			this.isDisabled = false;
			this.$main = $('.viewer-main');
			this.$mainImg = $('.viewer-main-image');
			this.$lens = $('<div class="zoom-lens"/>');
			this.$lensImg = this.$mainImg.clone();
			this.$magnifiedContainer = $('<div class="zoom-magnified"/>');
			this.$magnifiedImg = $('<img class="zoom-magnified-image fade" alt="high resolution image" data-pin-nopin="true"‰/>');
			this.smallimagedata = {};
			this.lensdata = {};
			this.scale = {};

			this.$lens.append(this.$lensImg);
			this.$magnifiedContainer.append(this.$magnifiedImg);
			this.$element.append(this.$magnifiedContainer);
			this.$main.append(this.$lens);

			this.imageLoadTimeout = null;

			// trigger the sizeMeasurement on page ready
			self.refreshMeasurements();

			// refresh the Image sizeMeasurement when window size changed.
			$window.resize(function() {
				self.refreshMeasurements();
			});

			// adding main image error event listener
			this.$mainImg.error(function(){
				self.isDisabled = true;
				var lPath = CONSTANTS.productImageRoot + '/unavailable/l.jpg',
						xlPath = CONSTANTS.productImageRoot + '/unavailable/xl.jpg';
				$('#ml-main-image').attr('srcset', lPath);
				$('#s-main-image').attr('srcset', xlPath);
				self.$mainImg.attr('src', lPath);
			});

			// adding load event listener
			this.$magnifiedImg.on('load', function() {
				var dims = measure(this),
						height = Math.round(dims.height);
				self.$magnifiedImg = $(this);
				self.$magnifiedContainer.append(self.$magnifiedImg);
				self.zoomLoaded = true;
				self.loadtest['magnified'] = true;
				self._hideLoader();
			});

			// when the main image loads
			this.$mainImg.on('load', function() {
				self.$lensImg.attr('src', self.$mainImg.attr('src'));
				self.refreshMeasurements();
				self.loadtest['main'] = true;
			});

			this.$main.on(startEvent, function(e) {
				if ($window.width() < CONFIG.mediumMin) {
					return;
				}
				self.activate(e);
				e.preventDefault();
			}).on(stopEvent, function() {
				if ($window.width() < CONFIG.mediumMin) {
					return;
				}
				self.deactivate();
			}).on(moveEvent, function(e){
				if ($window.width() < CONFIG.mediumMin) {
					return;
				}
				self._setImagePositions(e);
				e.preventDefault();
			});

			this.setImage(this.$mainImg.attr('data-id'), this.$mainImg.attr('data-image-name'));

		},
		activate : function(e) {
			this.isHovering = true;
			if (this.isActive){
				return;
			}
			if (this.isDisabled || !this.zoomLoaded) {
				this._waitForImageLoad(e);
				return;
			}
			this._setImagePositions(e);
			this.$magnifiedContainer.show();
			this.isActive = true;
			this.$lens.show();
			this.$mainImg.addClass('is-active');
			return false;
		},
		deactivate: function() {
			this.isHovering = false;
			if (this.isDisabled) {
				return;
			}
			this.$magnifiedContainer.hide();
			if (!this.isActive) {
				return;
			}
			this.isActive = false;
			this.$lens.hide();

			this.$mainImg.removeClass('is-active');
		},
		/* This function figures the ratio of the original image to the maginified image box to set the lens size
		 appropriately. Also sets the position on the fly out zoom container.
		 */
		refreshMeasurements : function () {
			var pos = {};
			this.smallimagedata = {
				w: this.$mainImg.width(),
				h: this.$mainImg.height()
			};
			pos.l = this.$mainImg.offset().left;
			pos.t = this.$mainImg.offset().top;
			pos.r = this.smallimagedata.w + pos.l;
			pos.b = this.smallimagedata.h + pos.t;
			this.smallimagedata.pos = pos;

			// calculate scale based on zoom height and small image ratio
			this.scale.y = this.options.zoomSize / this.smallimagedata.h;
			this.scale.x = ((this.smallimagedata.w/this.smallimagedata.h) * this.options.zoomSize) / this.smallimagedata.w;

			this.$magnifiedContainer.css('left', this.smallimagedata.w + 4);

			this._setLensScale();
		},
		setImage : function (productId, imageName) {
			var zoomurl = this._getImagePath(productId, imageName);
			this._showLoader();
			this._loadZoomImage(zoomurl);
		},
		_getImagePath: function (productId, imageName) {
			if (typeof productId !== 'undefined' && typeof imageName !== 'undefined') {
				return CONSTANTS.productImageRoot + '/'+ productId + '/z/' + imageName;
			}
			else {
				return CONSTANTS.productImageRoot + '/unavailable/z.jpg';
			}
		},
		_updateMainImage : function(imageName){

			// check to see if image is already selected
			if (this.$mainImg.attr('data-imageName') == imageName) {
				return;
			}

			this.$main.html($.mustache(TEMPLATES.productImageTemplate, {imageName : imageName}));
		},
		_loadZoomImage : function (url) {
			this.$magnifiedImg.attr('src', url);
		},
		_setLensScale: function() {
			this.lensdata.w = this.$magnifiedContainer.width() / this.scale.x;
			this.lensdata.h = this.$magnifiedContainer.height() / this.scale.y;
			this.$lens.css({
				width: this.lensdata.w + "px",
				height: this.lensdata.h + "px"
			});
			this.$lensImg.css({'height': this.smallimagedata.h, 'width': this.smallimagedata.w});
		},
		_setImagePositions: function(e) {
			if (this.isDisabled) {
				return;
			}
			/* “After deep debugging, I eventually discovered that the source of this problem was the
			 fix method in jquery’s event code. The fix method tries to copy the event object in
			 order to fix various cross browser issues. Unfortunately, it seems that mobile safari
			 does not allow the e.touches and e.changedTouches properties on event objects to be
			 copied to another object. This is weird and annoying. Luckily you can get around this
			 issue by using e.originalEvent.”
			 -  http://www.the-xavi.com/articles/trouble-with-touch-events-jquery
			 */
			var touch = (Modernizr.touchevents) ? e.originalEvent.touches[0] || e.originalEvent.changedTouches[0] : e,
				x = touch.pageX,
				y = touch.pageY,
				lensWidth = this.lensdata.w,
				lensHeight = this.lensdata.h,
				stageLeft = this.smallimagedata.pos.l,
				stageBottom =  this.smallimagedata.pos.b,
				stageTop =  this.smallimagedata.pos.t,
				stageRight =  this.smallimagedata.pos.r,
				lensleft = x - stageLeft - (lensWidth) / 2,
				lenstop = y - stageTop - (lensHeight) / 2,
				self = this;
			/* edge detection */
			if (overleft(x, lensWidth, stageLeft)) {
				lensleft = 0;
			} else if (overright(x, lensWidth, stageRight)) {
				lensleft = this.smallimagedata.w - this.lensdata.w - 1;
			}
			if (overtop(y, lensHeight, stageTop)) {
				lenstop = 0;
			} else if (overbottom(y, lensHeight, stageBottom)) {
				lenstop = this.smallimagedata.h - this.lensdata.h - 1;
			}

			lensleft = parseInt(lensleft, 10);
			lenstop = parseInt(lenstop, 10);
			this.$lensImg.css({
				position: "absolute",
				top: -(lenstop + 1),
				left: -(lensleft + 1)
			});

			this.$lens.css({
				top: lenstop,
				left: lensleft
			});

			if (this.zoomLoaded) {
				this.$magnifiedImg.css({
					"left": Math.ceil(-this.scale.x * parseInt(lensleft, 10)),
					"top": Math.ceil(-this.scale.y * parseInt(lenstop, 10))
				});
			}
		},
		_showLoader: function () {
			if (this.isLoaderShowing) {
				return;
			}
			this.$magnifiedImg.removeClass('in');
			this.$magnifiedContainer.addClass('loading');
			this.isLoaderShowing = true;
		},
		_hideLoader: function () {
			if (!this.isLoaderShowing) {
				return;
			}
			this.$magnifiedContainer.removeClass('loading');
			this.$magnifiedImg.addClass('in');
			this.isLoaderShowing = false;
		},
		_waitForImageLoad: function (e) {
			var self = this;
			if (this.loadtest['main'] && this.loadtest['magnified']) {
				this.isDisabled = false;
				// for delayed activation if image is not loaded yet while hovering.
				if (this.isHovering) {
					this.activate(e);
				}
				this.loadtest['main'] = false;
				this.loadtest['magnified'] = false;
			} else {
				setTimeout(function() {
					self._waitForImageLoad(e);
				}, 400);
			}
		}
	};

	$.fn.zoom = function (option) {
		var args = Array.prototype.slice.call( arguments, 1 );
		return this.each(function () {
			var $this = $(this),
				data = $this.data('zoom'),
				options = typeof option === 'object' && option;
			if (!data) {
				$this.data('zoom', (data = new Zoom(this, options)));
			}
			if (typeof option === 'string') {
				data[option].apply(data, args);
			}
		});
	};

	$.fn.zoom.defaults = {
		viewerWidth: 364,
		viewerHeight: 364,
		zoomSize : 1000,
		lens: true,
		imageOpacity: 0.8,
		showEffect: "fadein",
		hideEffect: "hide",
		fadeinSpeed: "slow",
		fadeoutSpeed: "slow"
	};

	$.fn.zoom.Constructor = Zoom;

}(this, window.jQuery, "KP"));
