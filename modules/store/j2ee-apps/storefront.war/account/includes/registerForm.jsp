<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />

	<%-- Page Variables --%>
	<dsp:getvalueof param="originalURL" var="originalURL"/>

	<div class="field-group">
		<label for="first">First Name</label>
		<dsp:input bean="ProfileFormHandler.value.firstName" maxlength="40" id="first" name="first" type="text" autocapitalize="off" data-validation="required name" data-fieldname="First Name" />
	</div>

	<div class="field-group">
		<label for="last">Last Name</label>
		<dsp:input bean="ProfileFormHandler.value.lastName" maxlength="40" id="last" name="last" type="text" autocapitalize="off" data-validation="required name" data-fieldname="Last Name" />
	</div>

	<div class="field-group">
		<label for="email">Email Address</label>
		<dsp:input bean="ProfileFormHandler.value.email" id="email" name="email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Email" maxlength="255"/>
	</div>

	<div class="field-group">
		<label for="password">Password</label>
		<dsp:input bean="ProfileFormHandler.value.password" id="password" name="password" type="password" autocapitalize="off" data-validation="required password" data-fieldname="Password" placeholder="minimum 8 characters, 1 uppercase, 1 lowercase, 1 number"/>
	</div>

	<div class="field-group">
		<label for="confirm">Confirm Password</label>
		<dsp:input bean="ProfileFormHandler.value.confirmpassword" id="confirm" name="confirm" type="password" autocapitalize="off" data-validation="required matchPassword" data-fieldname="Confirm Password" data-matchfield="#password" placeholder="re-enter your password"/>
	</div>

	<div class="field-group">
		<dsp:input bean="ProfileFormHandler.createErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
		<c:choose>
			<c:when test="${not empty originalURL}">
				<dsp:input bean="ProfileFormHandler.createSuccessURL" type="hidden" value="${contextPath}/account/json/registerSuccess.jsp?originalURL=${originalURL}"/>
			</c:when>
			<c:otherwise>
				<dsp:input bean="ProfileFormHandler.createSuccessURL" type="hidden" value="${contextPath}/account/json/registerSuccess.jsp"/>
			</c:otherwise>
		</c:choose>
		<input id="register-submit" name="register-submit" type="submit" class="button primary" value="Create Account" />
		<dsp:input bean="ProfileFormHandler.create" type="hidden" value="Create Account" />
	</div>
</dsp:page>
