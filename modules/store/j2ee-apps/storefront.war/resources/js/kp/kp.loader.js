/* =========================================================
 /* =========================================================
 * kp.loader.js
 * Created by KnowledgePath Solutions.
 * ==========================================================
 * Simple utility to show a loading screen. This informs the
 * user that a process is running and prevents them from
 * interacting with the UI.
 * ========================================================= */

(function (global, $, namespace) {

	"use strict";
	var CONSTANTS = global[namespace].constants,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			loader = {
				init: function() {
					var loaderDiv = '<div class="loader"><div class="loader-backdrop fade"/><div class="loader-content"><img class="loader-animation" src="' + CONSTANTS.contextPath + '/resources/images/loading.gif" width="60" height="60" /><span class="loader-text">just a moment...</span></div></div>';
					this.$element = $(loaderDiv).appendTo('body');
				},
				showLoader: function() {
					this.$element.show();
				},
				hideLoader: function() {
					this.$element.hide();
				}
			};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].loader = loader;

	$(document).ready(function() {
		global[namespace].loader.init();
	});


}(this, window.jQuery, "KP"));
