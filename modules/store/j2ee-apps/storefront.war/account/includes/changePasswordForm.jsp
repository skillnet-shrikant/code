<dsp:page>

	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />

	<dsp:setvalue bean="ProfileFormHandler.value.password" value=""/>
	<div class="field-group">
		<label for="current-password">Current Password</label>
		<dsp:input bean="ProfileFormHandler.value.OLDPASSWORD" id="current-password" name="current-password" type="password" autocapitalize="off" data-validation="required" data-fieldname="Current Password" />
	</div>

	<div class="field-group">
		<label for="new-password">New Password</label>
		<dsp:input bean="ProfileFormHandler.value.password" id="new-password" name="new-password" type="password" autocapitalize="off" data-validation="required password" data-fieldname="New Password" placeholder="minimum 8 characters, 1 uppercase, 1 lowercase, 1 number"/>
	</div>

	<div class="field-group">
		<label for="confirm">Confirm Password</label>
		<dsp:input bean="ProfileFormHandler.value.CONFIRMPASSWORD" id="confirm" name="confirm" type="password" autocapitalize="off" data-validation="required matchPassword" data-fieldname="Confirm Password" data-matchfield="#new-password" placeholder="re-enter new password"/>
	</div>

	<div class="field-group">
		<dsp:input bean="ProfileFormHandler.changePasswordSuccessURL" type="hidden" value="${contextPath}/account/json/profileSuccess.jsp" />
		<dsp:input bean="ProfileFormHandler.changePasswordErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp" />
		<dsp:input bean="ProfileFormHandler.changePassword" type="hidden" value="Update Password" />
		<input id="change-password-submit" name="change-password-submit" type="submit" class="button primary" value="Update Password" />
	</div>

</dsp:page>
