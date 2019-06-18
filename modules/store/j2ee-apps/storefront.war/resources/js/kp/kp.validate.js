/* kp.validate.js
 *
 * Usage:
 *
 * For each field you wish validated, add a data-validation attribute with
 * a space-separated list of validation rules (see rules object for names).
 * Optionally add a data-fieldname attribute with a user-friendly field name
 * that the error messages can use.
 *
 * To validate a form call the following:
 *
 *   $('#exampleFormId').validate('validateForm');
 *
 * Find the status of the form with this:
 *
 *   $('#exampleFormId').data('validate').isValid;
 *
 * To validate a form on submit, add an event listener for the submission. In this
 * example, any form with the attribute data-toggle=validate will be validated when submitted.
 *
 * $body.on('submit.validate.data-api', '[data-toggle=validate]', function (e) {
 *   var $this = $(this);
 *   $this.validate('validateForm');
 *   return $this.data('validate').isValid;
 * });
 *
 */


(function (global, $, namespace){

	"use strict";

	function testPattern (value, pattern) {
		var regExp = new RegExp(pattern, "");
		return regExp.test(value);
	}

	function checkCountry($form) {
		var $country = $form.find('[name=country]'),
				countryValue = 'us';
		if ($country.length > 0) {
			countryValue = $country.val().toLowerCase();
		}
		return countryValue;
	}

	function removeLeadingTrailingSpaces(value) {
		var leadingTrailingWhiteSpaces = /^\s+|\s+$/g;
		value = value.replace(leadingTrailingWhiteSpaces,"");
		return value;
	}

	/**--------------------------
	 //* Validate Date Field script- By JavaScriptKit.com
	 //* For this script and 100s more, visit http://www.javascriptkit.com
	 //* This notice must stay intact for usage
	 ---------------------------**/
	function checkdate(input){
		var validformat=/^\d{2}\/\d{2}\/\d{4}$/; //Basic check for format validity
		var returnval = false;
		if (!validformat.test(input)) {
			returnval = false;
		} else { //Detailed check for valid date ranges
			var monthfield = input.split("/")[0];
			var dayfield = input.split("/")[1];
			var yearfield = input.split("/")[2];
			var dayobj = new Date(yearfield, monthfield - 1, dayfield);
			if ((dayobj.getMonth() + 1 != monthfield) || (dayobj.getDate() != dayfield) || (dayobj.getFullYear() != yearfield)){
				returnval = false;
			} else {
				returnval =true;
			}
		}
		return returnval;
	}

	var messages = global[namespace].constants.messages,
			loggingDebug = global[namespace] && global[namespace].config && global[namespace].config.loggingDebug,
			Validate = function (element, options)  {
				this.init ('validate', element, options);
			},
			rules = {
				email : {
					check: function (value, field) {
						if (value) {
							return testPattern (value,"^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,})+$");
						}
						return true;
					},
					msg : function () {
						return messages.email;
					}
				},
				uspostal : {
					check: function (value, field) {
						if (value) {
							// allow +4 zips
							return testPattern (value, "^\\d{5}(-\\d{4})?$");
							// Restrict to only 5 digits
							// return testPattern (value, "^\\d{5}$");
						}
						return true;
					},
					msg : function () {
						return messages.uspostal;
					}
				},
				capostal : {
					check: function (value, field) {
						if (value) {
							return testPattern (value, "^[ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJKLMNPRSTVWXYZ] \\d[ABCEGHJKLMNPRSTVWXYZ]\\d$");
						}
						return true;
					},
					msg : function () {
						return messages.capostal;
					}
				},
				postal : {
					check: function(value, field){
						var countryValue = checkCountry(this.$element);
						if (countryValue == "us") {
							return rules.uspostal.check(value, field);
						} else if (countryValue == "ca") {
							return rules.capostal.check(value.toUpperCase(), field);
						} else {
							return true;
						}
					},
					msg : function () {
						var countryValue = checkCountry(this.$element);
						if (countryValue == "us" || countryValue == "usa") {
							return rules.uspostal.msg();
						} else if (countryValue == "ca") {
							return rules.capostal.msg();
						}
					}
				},
				usorcapostal : {
					check: function (value, field) {
						if (value) {
							var catest = testPattern (value, "^[ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJKLMNPRSTVWXYZ] \\d[ABCEGHJKLMNPRSTVWXYZ]\\d$");
							var ustest=	testPattern (value, "^\\d{5}$");
							if (!catest && !ustest) {
								return false;
							}
							else {
								return true;
							}
						}
						return true;
					},
					msg : function () {
						return messages.usorcapostal;
					}
				},
				phone : {
					check: function(value, field){
						var countryValue = checkCountry(this.$element);
						if (countryValue == "us") {
							return rules.usphone.check(value, field);
						}  else {
							return rules.internationalphone.check(value, field);
						}
					},
					msg : function () {
						var countryValue = checkCountry(this.$element);
						if (countryValue == "us") {
							return rules.usphone.msg();
						} else {
							return rules.internationalphone.msg();
						}
					}
				},
				usphone : {
					check: function(value, field) {
						if (value) {
							//return testPattern (value, "(?:(?:(\s*\(?([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\s*)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\)?\s*(?:[.-]\s*)?)([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\s*(?:[.-]\s*)?([0-9]{4})");
							// return testPattern (value, "[1-9][0-9]{9}");
							return testPattern (value, "^([0-9]){3}\-([0-9]){3}\-([0-9]){4}$");
						}
						return true;
					},
					msg : function () {
						return messages.usphone;
					}
				},
				// add test for international phone
				internationalphone : {
					check: function(value,field) {
						if(value) {
							// return testPattern (value, "\+?([0-9]{2})\)?([0-9]{4})([0-9]{4})")
							return testPattern (value, "(?:[0-9] ?){6,14}[0-9]");
						}
						return true;
					},
					msg : function () {
						return messages.internationalphone;
					}
				},
				alpha : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[a-zA-Z]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.alpha.named;
						}
						return messages.alpha.unnamed;
					}
				},
				numeric : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[0-9]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.numeric.named;
						}
						return messages.numeric.unnamed;
					}
				},
				alphanumeric : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[a-zA-Z0-9]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.alphanumeric.named;
						}
						return messages.alphanumeric.unnamed;
					}
				},
				alphanumericspace : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[a-zA-Z0-9 ]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.alphanumericspace.named;
						}
						return messages.alphanumericspace.unnamed;
					}
				},
				alphaspace : {
					check: function(value, field) {
						if (value) {
							// allow a-z, A-Z, spaces, and periods (.)
							return testPattern (value, "^[a-zA-Z \.]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.alphaspace.named;
						}
						return messages.alphaspace.unnamed;
					}
				},
				creditcard : {
					// accepts all numeric or masked card
					check: function(value, field) {
						var number,
								i, len,
								total = 0,
								doubled,
								digit;
						if (value) {
							number = value.replace(/ /g,'');
							if (isNaN(number)) {
								// ignore masked credit card number
								return testPattern (value, "^[X]+\\d{4}$");
							} else {
								// mod10 check on cc number
								len = number.length - 1;
								for (i = len; i >= 0; i--) {
									if ((len - i) % 2  === 0) {
										total += parseInt(number[i]);
									} else {
										doubled = 2 * number[i];
										while (doubled !== 0) {
											digit = doubled % 10;
											doubled = parseInt(doubled / 10);
											total += digit;
										}
									}
								}
								if (total % 10 === 0) {
									return true;
								} else {
									return false;
								}
							}
						}
						return true;
					},
					msg : function () {
						return messages.creditcard;
					}
				},
				required : {
					check: function(value, field) {
						if (field.attr('type') === 'checkbox') {
							if (field[0].checked) {
								return true;
							} else {
								return false;
							}
						}
						else {
							if ($.trim(value) !== '') {
								return true;
							} else {
								return false;
							}
						}
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.required.named;
						}
						return messages.required.unnamed;
					}
				},
				minlength : {
					check : function (value, field) {
						var minlength = field.attr('min-length');
						if ($.trim(value).length >= minlength) {
							return true;
						} else {
							return false;
						}
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.minlength.named;
						}
						return messages.minlength.unnamed;
					}
				},
				maxlength : {
					check : function (value, field) {
						var maxlength = field.attr('max-length');
						if ($.trim(value).length <= maxlength) {
							return true;
						} else {
							return false;
						}
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.maxlength.named;
						}
						return messages.maxlength.unnamed;
					}
				},
				matchPassword : {
					check : function (value, field) {
						var matchField = field.attr('data-matchfield'),
								matchValue = '';
						if (matchField) {
							matchValue = this.$element.find(matchField).val();
						}
						if ($.trim(value) == $.trim(matchValue)) {
							return true;
						} else {
							return false;
						}
					},
					msg : function () {
						return messages.matchPassword;
					}
				},
				matchEmail : {
					check : function (value, field) {
						var matchField = field.attr('data-matchfield'),
								matchValue = '';
						if (matchField) {
							matchValue = this.$element.find(matchField).val();
						}
						if ($.trim(value).toLowerCase() == $.trim(matchValue).toLowerCase()) {
							return true;
						} else {
							return false;
						}
					},
					msg : function () {
						return messages.matchEmail;
					}
				},
				nameField : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[a-zA-Z0-9 \.\"'&:\/\-]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.nameField.named;
						}
						return messages.nameField.unnamed;
					}
				},
				nopobox : {
					check: function(value, field) {
			if (value) {
							return !(testPattern (value, "([P|p](OST|ost)*.*s*[O|o|0](ffice|FFICE)*.*s*[B|b][O|o|0][X|x]s*(\d.)*)"));
						}
						return true;
					},
					msg : function () {
						return messages.nopobox;
					}
				},
				qty : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[1-9][0-9]{0,2}$");
						}
						return true;
					},
					msg : function () {
						return messages.qty;
					}
				},
				password : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^.*(?=.{8,})(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$");
						}
						return true;
					},
					msg : function () {
						return messages.password;
					}
				},
				validDateOfBirth : {
					check: function(value, field) {
						return checkdate(value);
					},
					msg : function () {
						return messages.dateOfBirthDate;
					}
				},
				validDateOfBirthAge : {
					check: function(value, field) {
						var birthday = +new Date(value);
						var age = (Date.now() - birthday) / 31557600000;
						if(age < 13 || age > 120){
							return false;
						}
						return true;
					},
					msg : function () {
						return messages.dateOfBirthAge;
					}
				},
				name : {
					check: function(value, field) {
						if (value) {
							// allow alpha, period, space
							value = removeLeadingTrailingSpaces(value);
							field.val(value);
							return testPattern (value, "^[a-zA-Z. ]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.name.named;
						}
						return messages.name.unnamed;
					}
				},
				address : {
					check: function(value, field) {
						if (value) {
							// allow alpha, numeric, period, space
							value = removeLeadingTrailingSpaces(value);
							field.val(value);
							return testPattern (value, "^[0-9a-zA-Z., ]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.address.named;
						}
						return messages.address.unnamed;
					}
				},
				taxField : {
					check: function(value, field) {
						if (value) {
							return testPattern (value, "^[a-zA-Z0-9.\",-\/\]+$");
						}
						return true;
					},
					msg : function (field) {
						var fieldName = field.attr('data-fieldname');
						if (fieldName !== undefined && fieldName !== '') {
							return fieldName + ' ' + messages.taxField.named;
						}
						return messages.taxField.unnamed;
					}
				}
			};

	Validate.prototype = {
		constructor: Validate,
		init: function (type, element, options) {
			if (loggingDebug) {
				console.debug('init ' + type + ' with options:');
				console.debug(Array.prototype.slice.call(arguments));
			}
			var self = this;
			var showErrors = false;
			this.$element = $(element);
			this.options = $.extend({}, $.fn[type].defaults, options, this.$element.data());
			this.isValid = false;
			this.fields = this.$element.find('[data-validation]');
			this.submit = this.$element.find('input[type="submit"]');
			if (!this.submit || this.submit.length === 0) {
				this.submit = this.$element.find('.primary.button');
			}

			if (this.options.live_validate) {
				this.$element.on('input.validate.data-api propertychange.validate.data-api change.validate.data-api focusout.validate.data-api', function (e) {
					self.validateField($(e.target));
				});
			}
			else if (this.options.blur_validate) {
				this.$element.on('propertychange.validate.data-api change.validate.data-api focusout.validate.data-api', function (e) {
					// trim leading / trailing whitespace from field (except passwords) then validate
					var $field = $(e.target);
					if ($field[0].type !== 'password') {
						self.validateField($field.val($field.val().trim()));
					}
					else {
						self.validateField($field);
					}
				});
			}

			if (this.options.submit_validate) {
				this.$element.on('submit.validate.data-api', function (e) {
					self.validateForm();
					return self.isValid;
				});
			}
		},
		/* validates every field that was marked with data-validation rules */
		validateForm : function (fieldArray, showErrors) {
			this.$element.trigger('validate');
			var fieldsToValidate = fieldArray || this.fields,
				x = 0, max = fieldsToValidate.length, $field,
				formErrors = {},
				fieldErrors = {}, fieldName;
			if (typeof showErrors === 'undefined'){
				showErrors = true;
			}
			this.isValid = true;
			for (x, max; x < max; x++) {
				$field = $(fieldsToValidate[x]);
				fieldErrors = this.checkField($field);
				if ($.isEmptyObject(fieldErrors) === false) {
					fieldName = $field.attr('name');
					formErrors[fieldName] = fieldErrors;
				}
			}
			if (showErrors) {
				this.showFormErrors(formErrors);
			}

			return this.isValid;
		},
		/* Validates a single field. */
		validateField : function ($field){
			var fieldErrors = this.checkField($field),
					fieldName,
					formErrors = {},
					errors = {},
					error;

			if ($.isEmptyObject(fieldErrors) === false) {
				fieldName = $field.attr('name');
				formErrors[fieldName] = fieldErrors;
				errors = this.cleanFieldErrors(formErrors);

				for (error in errors) {
					if (errors.hasOwnProperty(error)) {
						this.showFieldError(errors[error]);
					}
				}
			} else if (fieldErrors) {
				// fieldErrors will be undefined if the field has no rules.
				// so if fieldErrors is defined then the field has no errors.
				this.showFieldSuccess($field);
			}

		},
		/* run validation rules on a field and return the error messages */
		checkField: function($field){
			var value,
					types,
					fieldErrors = {},
					errors = [],
					rule;

			if (typeof $field.attr('data-validation') == 'undefined') {
				return;
			}

			if ($field.prop('disabled') || $field.hasClass('disabled') || !$field.is(':visible')) {
				return;
			}

			value = $field.val();
			types = $field.attr('data-validation').split(' ');

			// First run tests from validation
			for (var x = 0, max = types.length; x < max; x++) {
				rule = rules[types[x]];
				if (rule  && !rule.check.call(this, value, $field)) {
					errors.push(rule.msg.call(this, $field));
					break;
				}
			}

			if (errors.length > 0) {
				this.isValid = false;
				fieldErrors.field = $field;
				fieldErrors.errors = errors;
			}
			return fieldErrors;
		},
		/* given an error message object, display it on a field */
		showFieldError : function (errorObj) {
			var errorMessageHtml = '',
					errors = errorObj.errors,
					fields = errorObj.fields,
					$alertTarget,
					$field = fields[fields.length - 1],
					field = $field[0],
					//in the case there are multiple fields, will tie to last field in group
					fieldId = $field.attr('id'),
					iconLeftPosition;

			$alertTarget = this.getFieldTarget(fields);

			for (var e = 0, errorsLen = errors.length; e < errorsLen; e++) {
				errorMessageHtml += errors[e];
			}

			for (var f = 0, fieldsLen = fields.length; f < fieldsLen; f++) {
				this.clearFieldMessage(fields[f]);
				fields[f].addClass("error");
				fields[f].attr('aria-describedby', 'error-msg-' + fieldId);
				fields[f].attr('aria-invalid', 'true');
			}

			var decoded = global[namespace].utilities.decodeHTMLEntities(errorMessageHtml);
			$alertTarget.after(Mustache.render(this.options.template, {fieldId: fieldId , errorMessage: decoded}));
			$('label[for=' + fieldId + ']').addClass('error');
		},
		/* get the target for the field message */
		getFieldTarget : function(fields) {
			var parent,
					field;

			if (fields instanceof jQuery) {
				field = fields;
			} else {
				//if there are multiple fields with the same name, use the last one.
				field = fields[fields.length - 1];
			}

			parent = field.attr('data-parent');
			if (parent !== undefined) {
				return this.$element.find(parent);
			} else if (field.is('.inline-form *')){
				// when this is an inline form we want to display the error message after the button.
				return field.nextAll('.button');
			} else {
				return field;
			}
		},
		/* show success validation message for a field */
		showFieldSuccess : function ($field){
			var field = $field[0],
					$alertTarget,
					iconLeftPosition,
					top = 7;

			this.clearFieldMessage($field);
		},
		/* clear error and success messages form a field */
		clearFieldMessage : function ($field) {
			$field.removeClass("error success")
					.removeAttr('aria-describedby')
					.removeAttr('aria-invalid');
			this.$element.find('label[for=' + $field.attr('id') + ']').removeClass('error');
			this.$element.find('.validation-message-for-' + $field.attr('id')).remove();
		},
		/* clears errors and success messages */
		clearFormMessages : function(){
			var x = 0;
			var max = this.fields.length;
			var $field;
			for (x, max; x < max; x++) {
				$field = $(this.fields[x]);
				this.clearFieldMessage($field);
			}
		},
		/* remove only error messages from fields */
		clearFormErrors : function () {
			var that = this;

			this.$element.find('[data-validation].error').each(function(){
				var $field = $(this);
				that.clearFieldMessage($field);
			});

			global[namespace].errors = [];
		},
		/* Given an error message object show all field error messages in a form */
		showFormErrors : function (formErrors) {
			var errors = {},
					error,
					hasErrors = false;

			/*
			 When the live-validation is off, then the form may have some lingering error messages from the back end.
			 The front-end validation would pass when they user hits submit, but when we get more messages from the back end,
			 we need to remove any existing error messages since some of them may be resolved.
			 */
			this.clearFormErrors();

			errors = this.cleanFieldErrors(formErrors);

			for (error in errors) {
				if (errors.hasOwnProperty(error)) {
					hasErrors = true;
					this.showFieldError(errors[error]);
				}
			}
			if (hasErrors) {
				this.scrollToError();
			}

		},
		cleanFieldErrors : function(formErrors){
			/* this function will take care of duplicate errors (this can happen when we receive external error message from
			 back-end validation). It will associate a target element for the error using the parent element if one is defined
			 in the form. */
			var errors = {},
					fieldError,
					$field,
					fieldName,
					fieldErrors;

			for (fieldError in formErrors) {
				if (formErrors.hasOwnProperty(fieldError)) {

					fieldName = fieldError;

					if ($.isArray(formErrors[fieldError])) {
						$field = this.$element.find("[name='" + fieldError + "']");
						fieldErrors = formErrors[fieldError];
					} else {
						$field = formErrors[fieldError].field;
						fieldErrors = formErrors[fieldError].errors;
					}

					if (errors[fieldName] === undefined) {
						errors[fieldName] = {};
						errors[fieldName].fields = [];
						errors[fieldName].errors = [];
					}

					errors[fieldName].fields.push($field);
					errors[fieldName].errors = global[namespace].utilities.dedup(errors[fieldName].errors.concat(fieldErrors));
				}
			}
			return errors;

		},

		scrollToError : function (selector) {
			var viewHeight = 0,
					bodyOffset = 0,
					errorOffset,
					scrollSelector = selector || '.error';
			if (typeof ( window.innerWidth ) == 'number' ) {
				viewHeight = window.innerHeight;
			} else if ( document.documentElement && document.documentElement.clientHeight ) {
				viewHeight = document.documentElement.clientHeight;
			}
			if( typeof( window.pageYOffset ) == 'number' ) {
				bodyOffset = window.pageYOffset;
			} else if ( document.body && document.body.scrollTop ) {
				bodyOffset = document.body.scrollTop;
			}
			errorOffset = $(this.$element.find(scrollSelector).get(0)).offset();
			this.$element.find('input.error, select.error, textarea.error, .alert-box').get(0).focus();
			if (errorOffset && errorOffset.top){
				if (bodyOffset > errorOffset.top || errorOffset.top > viewHeight + bodyOffset) {
					window.scrollTo(0, errorOffset.top);
				}
			}
		}
	};


	/*  PLUGIN DEFINITION
	 * ============================== */

	$.fn.validate = function ( option ) {
		var args = Array.prototype.slice.call( arguments, 1 );
		return this.each(function () {
			var $this = $(this),
					data = $this.data('validate'),
					options = typeof option == 'object' && option;
			if (!data) {
				$this.data('validate', (data = new Validate(this, options)));
			}
			if (typeof option == 'string') {
				data[option].apply(data, args);
			}
		});
	};

	$.fn.validate.defaults = {
		template : '<div class="error field-error-text validation-message-for-{{fieldId}}" id="error-msg-{{fieldId}}" tabindex="-1">' +
			'{{{errorMessage}}}' +
			'</div>',
		live_validate: false,
		blur_validate: true,
		submit_validate: true
	};

	$.fn.validate.Constructor = Validate;

	$(function () {
		$('[data-validate]').validate();
	});


}(this, window.jQuery, "KP"));
