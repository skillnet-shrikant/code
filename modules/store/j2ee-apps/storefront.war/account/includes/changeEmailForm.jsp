<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />

	<div class="field-group">
		<label for="current-password">Current Password</label>
		<dsp:input id="current-password" bean="ProfileFormHandler.editValue.currentPassword" name="current-password" type="password" autocapitalize="off" data-validation="required" data-fieldname="Current Password" />
	</div>

	<div class="field-group">
		<label for="new-email">New Email Address</label>
		<dsp:input id="new-email" bean="ProfileFormHandler.editValue.newEmail" name="new-email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="New Email Address" maxlength="255" />
	</div>

	<div class="field-group">
		<label for="confirm-email">Confirm Email Address</label>
		<dsp:input id="confirm-email" bean="ProfileFormHandler.editValue.confirmEmail" name="confirm-email" type="email" autocapitalize="off" data-validation="required matchEmail" data-fieldname="Confirm Email Address" data-matchfield="#new-email" maxlength="255"/>
	</div>

	<div class="field-group">
		<dsp:input bean="ProfileFormHandler.updateEmailErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
		<dsp:input bean="ProfileFormHandler.updateEmailSuccessURL" type="hidden" value="${contextPath}/account/json/loginSuccess.jsp"/>
		<dsp:input bean="ProfileFormHandler.updateEmail" type="hidden" value="Update Email" />
		<input id="change-email-submit" name="change-email-submit" type="submit" class="button primary" value="Update Email" />
	</div>

</dsp:page>
