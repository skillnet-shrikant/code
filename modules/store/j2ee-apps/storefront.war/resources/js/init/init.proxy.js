/* =========================================================
 * kp.proxy.js
 * =========================================================
 * Some common functions for submitting forms through the proxy iframe.
 *
 * @requires postmessage.js
 * ========================================================= */
(function (global, $, namespace) {
	"use strict";

	var loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug;

	var proxy = {
		init : function () {
			this.sendContent();
		},
		handleProxySubmit : function handleProxySubmit(e, formId) {
			if (loggingDebug) {
				console.log("proxy.handleProxySubmit called with the following parameters");
				console.log([e, formId])
			}
			var formData,
					formId = formId || e.target.id,
					pmData = {form: formId};

			formData =  form2js(e.target);
			pmData = $.extend(pmData, formData);
			pm({
				target: window.frames["proxy"],
				type: "postForm",
				data: pmData
			});
			e.preventDefault();
		},
		handlePostForm : function (data, submitId) {
			if (loggingDebug) {
				console.log("proxy.handlePostForm called with the following parameters");
				console.log([data, submitId])
			}
			var form = document.getElementById(data.form),
					$form,
					$submitBtn;
			if (form) {
				js2form(form, data);
				if (submitId) {
					$(submitId).click();
				} else {
					//HERE
					$form = $(form);
					$submitBtn = $form.find('input[type=submit]');
					if ($submitBtn.length > 0) {
						$submitBtn.click();
					} else {
						$form.submit();
					}
				}
			}
		},
		sendContent : function () {
			if (loggingDebug) {
				console.log("proxy.sendContent");
			}
			var pmData = {content: document.getElementById('proxyContent').innerHTML};
			pm({
				target: window.parent,
				type:"setModalContent",
				data: pmData
			});
		},
		sendReadyState : function () {
			if (loggingDebug) {
				console.log("proxy.sendReadyState");
			}
			pm({
				target: window.parent,
				type:"proxyIsReady",
				data: {'state': 'ready'}
			});
		}
	};

	global[namespace] = global[namespace] || {};

	global[namespace].proxy = proxy;


})(this, window.jQuery, "KP");
