<%@ include file="/sitewide/fragments/content-type-javascript.jspf" %>

<%--
- File Name: jsConstants.jsp
- Author(s):
- Copyright Notice:
- Description: Sets parameters in a javascript object for use in plugin scripts.
-    PLEASE NOTE: a lot of variables are set as jstl vars then output on page using c:out. We are
-    taking advantage of the escapeXML attribute (true by default) to escape values in these strings.
- Parameters:
-
--%>

<%-- Imports --%>
<dsp:importbean bean="/mff/MFFEnvironment" />

<fmt:message var="errorheader" key="common.errorHeader"/>
<fmt:message var="ajaxError" key="common.errorAjax"/>

<%-- Session Length --%>
<dsp:getvalueof var="sessionTimeoutSecs" bean="MFFEnvironment.sessionTimeoutRefresh" />

<%-- image paths --%>
<dsp:getvalueof var="productImageRoot" bean="MFFEnvironment.productImageRoot" />
<dsp:getvalueof var="swatchImageRoot" bean="MFFEnvironment.swatchImageRoot" />
<dsp:getvalueof var="marketingImageRoot" bean="MFFEnvironment.marketingImageRoot" />

<%-- validation error messages --%>
<fmt:message var="errorAlphaNamed" key="error.alpha.named"/>
<fmt:message var="errorAlphaUnnamed" key="error.alpha.unnamed"/>
<fmt:message var="errorAlphanumericNamed" key="error.alphanumeric.named"/>
<fmt:message var="errorAlphanumericspaceUnnamed" key="error.alphanumericspace.unnamed"/>
<fmt:message var="errorAlphanumericspaceNamed" key="error.alphanumericspace.named"/>
<fmt:message var="errorAlphanumericUnnamed" key="error.alphanumeric.unnamed"/>
<fmt:message var="errorAlphaspaceNamed" key="error.alphaspace.named"/>
<fmt:message var="errorAlphaspaceUnnamed" key="error.alphaspace.unnamed"/>
<fmt:message var="errorAddressNamed" key="error.address.named"/>
<fmt:message var="errorAddressUnnamed" key="error.address.unnamed"/>
<fmt:message var="errorEmail" key="error.email"/>
<fmt:message var="errorUspostal" key="error.uspostal"/>
<fmt:message var="errorCApostal" key="error.capostal"/>
<fmt:message var="errorUsOrCApostal" key="error.usorcapostal"/>
<fmt:message var="errorUsphone" key="error.usphone"/>
<fmt:message var="errorIntPhone" key="error.internationalphone"/>
<fmt:message var="errorNameNamed" key="error.name.named"/>
<fmt:message var="errorNameUnnamed" key="error.name.unnamed"/>
<fmt:message var="errorNameFieldNamed" key="error.nameField.named"/>
<fmt:message var="errorNameFieldUnnamed" key="error.nameField.unnamed"/>
<fmt:message var="errorNumericNamed" key="error.numeric.named"/>
<fmt:message var="errorNumericUnnamed" key="error.numeric.unnamed"/>
<fmt:message var="errorCreditcard" key="error.creditcard"/>
<fmt:message var="errorRequiredNamed" key="error.required.named"/>
<fmt:message var="errorRequiredUnnamed" key="error.required.unnamed"/>
<fmt:message var="errorMinlengthNamed" key="error.minlength.named"/>
<fmt:message var="errorMinlengthUnnamed" key="error.minlength.unnamed"/>
<fmt:message var="errorMaxlengthNamed" key="error.maxlength.named"/>
<fmt:message var="errorMaxlengthUnnamed" key="error.maxlength.unnamed"/>
<fmt:message var="errorMatchPassword" key="error.matchPassword"/>
<fmt:message var="errorMatchEmail" key="error.matchEmail"/>
<fmt:message var="errorNoPOBox" key="error.nopobox"/>
<fmt:message var="errorQty" key="error.qty"/>
<fmt:message var="errorPassword" key="error.password"/>
<fmt:message var="errorBirthdayDate" key="error.birthday.date"/>
<fmt:message var="errorBirthdayAge" key="error.birthday.age"/>
<fmt:message var="errorGiftMessage" key="error.gift.message"/>
<fmt:message var="maxPromotionsReached" key="cart.promotions.max"/>
<fmt:message var="errorTaxFieldNamed" key="error.taxField.named"/>
<fmt:message var="errorTaxFieldUnnamed" key="error.taxField.unnamed"/>


(function (global, namespace ) {
	"use strict";

	var constants = {
		contextPath : '${contextPath}',
		sessionTimeoutMillis : 1000 * <c:out value="${sessionTimeoutSecs}"/>,

		/* image paths */
		productImageRoot : '<c:out value="${productImageRoot}"/>',
		swatchImageRoot : '<c:out value="${swatchImageRoot}"/>',
		marketingImageRoot : '<c:out value="${marketingImageRoot}"/>',

		/* lang */
		errorheader : '<c:out value="${errorheader}"/>',
		ajaxError : '<c:out value="${ajaxError}"/>' ,

		messages : {
			alpha : {
				named : '<c:out value="${errorAlphaNamed}"/>',
				unnamed : '<c:out value="${errorAlphaUnnamed}"/>'
			},
			alphanumeric : {
				named : '<c:out value="${errorAlphanumericNamed}"/>',
				unnamed : '<c:out value="${errorAlphanumericUnamed}"/>'
			},
			alphanumericspace : {
				named : '<c:out value="${errorAlphanumericspaceNamed}"/>',
				unnamed : '<c:out value="${errorAlphanumericspaceUnamed}"/>'
			},
			alphaspace : {
				named : '<c:out value="${errorAlphaspaceNamed}"/>',
				unnamed : '<c:out value="${errorAlphaspaceUnamed}"/>'
			},
			address : {
				named : '<c:out value="${errorAddressNamed}"/>',
				unnamed : '<c:out value="${errorAddressUnnamed}"/>'
			},
			email : '<c:out value="${errorEmail}"/>',
			uspostal : '<c:out value="${errorUspostal}"/>',
			capostal : '<c:out value="${errorCApostal}"/>',
			usorcapostal : '<c:out value="${errorUsOrCApostal}"/>',
			usphone : '<c:out value="${errorUsphone}"/>',
			internationalphone : '<c:out value="${errorIntPhone}"/>',
			name : {
				named : '<c:out value="${errorNameNamed}"/>',
				unnamed : '<c:out value="${errorNameUnnamed}"/>'
			},
			nameField : {
				named : '<c:out value="${errorNameFieldNamed}"/>',
				unnamed : '<c:out value="${errorNameFieldUnnamed}"/>'
			},
			numeric : {
				named : '<c:out value="${errorNumericNamed}"/>',
				unnamed : '<c:out value="${errorNumericUnnamed}"/>'
			},
			creditcard : '<c:out value="${errorCreditcard}"/>',
			required : {
				named : '<c:out value="${errorRequiredNamed}"/>',
				unnamed : '<c:out value="${errorRequiredUnnamed}"/>'
			},
			minlength : {
				named : '<c:out value="${errorMinlengthNamed}"/>',
				unnamed : '<c:out value="${errorMinlengthUnnamed}"/>'
			},
			maxlength : {
				named : '<c:out value="${errorMaxlengthNamed}"/>',
				unnamed : '<c:out value="${errorMaxlengthUnnamed}"/>'
			},
			matchPassword : '<c:out value="${errorMatchPassword}"/>',
			matchEmail : '<c:out value="${errorMatchEmail}"/>',
			nopobox : '<c:out value="${errorNoPOBox}"/>',
			qty : '<c:out value="${errorQty}"/>',
			password : '<c:out value="${errorPassword}"/>',
			dateOfBirthDate : '<c:out value="${errorBirthdayDate}"/>',
			dateOfBirthAge : '<c:out value="${errorBirthdayAge}"/>',
			invalidGiftMessage : '<c:out value="${errorGiftMessage}"/>',
			maxPromotionsReached: '<c:out value="${maxPromotionsReached}"/>',
			taxField : {
				named : '<c:out value="${errorTaxFieldNamed}"/>',
				unnamed : '<c:out value="${errorTaxFieldUnnamed}"/>'
			}
		}
	};

	if (!global[namespace]) {
		global[namespace] = {};
	}
	global[namespace].constants = constants;

}(this, "KP"));
