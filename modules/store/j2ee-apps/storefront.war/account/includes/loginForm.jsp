<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />

	<%-- Page Variables --%>
	<dsp:getvalueof param="originalURL" var="originalURL"/>

	<div class="field-group">
		<label for="login-email">Email Address</label>
		<dsp:input id="login-email" bean="ProfileFormHandler.value.login" name="login-email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Email" />
	</div>

	<div class="field-group">
		<label for="login-password">Password</label>
		<dsp:input bean="ProfileFormHandler.value.password" id="login-password" name="login-password" type="password" autocapitalize="off" data-validation="required" data-fieldname="Password" />
	</div>

	<div class="field-group">
		<div class="field right">
			<a href="${contextPath}/account/passwordReset.jsp" class="forgot-password">Forgot Password</a>
		</div>
	</div>

	<div class="field-group">
		<dsp:input bean="ProfileFormHandler.loginErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
		<c:choose>
			<c:when test="${not empty originalURL}">
				<dsp:input bean="ProfileFormHandler.loginSuccessURL" type="hidden" value="${contextPath}/account/json/loginSuccess.jsp?originalURL=${originalURL}"/>
			</c:when>
			<c:otherwise>
				<dsp:input bean="ProfileFormHandler.loginSuccessURL" type="hidden" value="${contextPath}/account/json/loginSuccess.jsp"/>
			</c:otherwise>
		</c:choose>
		<dsp:input bean="ProfileFormHandler.login" type="hidden" value="Sign In" />
		<input id="login-submit" name="login-submit" type="submit" class="button primary" value="Sign In" />
	</div>

</dsp:page>
