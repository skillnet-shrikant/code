<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

	<%-- Page Variables --%>
	<dsp:getvalueof param="originalURL" var="originalURL"/>

	<div class="field-group">
		<label for="login-email">Email Address</label>

		<%-- disable changing email address for soft-logged in users --%>
		<dsp:droplet name="Switch">
			<dsp:param bean="Profile.softLoggedInRegistered" name="value"/>
			<dsp:oparam name="true">
				<dsp:input id="login-email" bean="ProfileFormHandler.value.login" name="login-email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Email" >
					<dsp:tagAttribute name="disabled" value=""/>
				</dsp:input>
			</dsp:oparam>
			<dsp:oparam name="false">
				<dsp:input id="login-email" bean="ProfileFormHandler.value.login" name="login-email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Email" />
			</dsp:oparam>
		</dsp:droplet>
	</div>

	<div class="field-group">
		<label for="login-password">Password</label>
		<dsp:input bean="ProfileFormHandler.value.password" id="login-password" name="login-password" type="password" autocapitalize="off" data-validation="required" data-fieldname="Password" value="" />
	</div>

	<div class="field-group">
		<div class="field right">
			<a href="${contextPath}/account/passwordReset.jsp" class="forgot-password">Forgot Password</a>
		</div>
	</div>

	<div class="field-group">
		<dsp:input bean="ProfileFormHandler.loginErrorURL" type="hidden" value="${contextPath}/checkout/json/loginError.jsp"/>
		<dsp:input bean="ProfileFormHandler.loginSuccessURL" type="hidden" value="${contextPath}/checkout/json/loginSuccess.jsp"/>
		<dsp:input bean="ProfileFormHandler.login" type="hidden" value="Sign In" />
		<input id="login-submit" name="login-submit" type="submit" class="button primary" value="Sign In" />
	</div>

</dsp:page>
