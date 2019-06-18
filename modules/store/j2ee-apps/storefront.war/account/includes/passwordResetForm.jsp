<dsp:page>

	<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler"/>

	<div class="field-group">
		<label for="email">Email Address</label>
		<dsp:input bean="ForgotPasswordHandler.value.login" id="email" name="email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Email Address" maxlength="255"/>
	</div>
	<%-- recaptcha --%>
	<div class="field-group captcha">
		<div id="gc-balance-captcha" class="g-recaptcha"></div>
	</div>
	<div class="field-group">
		<dsp:input bean="ForgotPasswordHandler.forgotPasswordErrorURL" type="hidden" value="${contextPath}/account/json/passwordResetError.jsp" />
		<dsp:input bean="ForgotPasswordHandler.forgotPasswordSuccessURL" type="hidden" value="${contextPath}/account/json/passwordResetSuccess.jsp" />
		<dsp:input bean="ForgotPasswordHandler.resetPassword" type="hidden" value="Reset Password" />
		<input id="password-reset-submit" name="password-reset-submit" type="submit" class="button primary" value="Reset Password" />
	</div>

</dsp:page>
