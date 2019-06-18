<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>

	<div class="field-group">
		<label for="first">First Name</label>
		<dsp:input bean="ProfileFormHandler.value.firstName" maxlength="40" id="first" name="first" type="text" autocapitalize="off" data-validation="required name" data-fieldname="First Name" beanvalue="Profile.firstName"/>
	</div>

	<div class="field-group">
		<label for="last">Last Name</label>
		<dsp:input bean="ProfileFormHandler.value.lastName" id="last" maxlength="40" name="last" type="text" autocapitalize="off" data-validation="required name" data-fieldname="Last Name" beanvalue="Profile.lastName"/>
	</div>

	<div class="field-group">
		<label for="phone">Phone</label>
		<dsp:input bean="ProfileFormHandler.value.phoneNumber" id="phone" name="phone" type="text" autocapitalize="off" data-validation="usphone" data-fieldname="Phone" placeholder="Area Code and Number" beanvalue="Profile.phoneNumber"/>
	</div>

	<div class="field-group">
		<label for="som">Employee Card #</label>
		<dsp:input bean="ProfileFormHandler.value.somCard" id="som" name="som" type="text" autocapitalize="off" data-fieldname="Som" placeholder="SOM Card #" beanvalue="Profile.somCard"/>
	</div>

		
	<dsp:getvalueof bean="Profile.gender" var="selectedGender"/>
	<div class="field-group">
		<span class="form-label">Gender</span>
		<div class="radio">
			<label for="gender-none">
				<c:choose>
					<c:when test="${selectedGender eq 'Prefer Not To Say'}">
						<dsp:input bean="ProfileFormHandler.value.gender"  type="radio" value="Prefer Not To Say" id="gender-none" name="gender" checked="true"/>
					</c:when>
					<c:otherwise>
						<dsp:input bean="ProfileFormHandler.value.gender"  type="radio" value="Prefer Not To Say" id="gender-none" name="gender"/>
					</c:otherwise>
				</c:choose>
				Prefer Not To Say
			</label>
		</div>
		<div class="radio">
			<label for="gender-male">
				<c:choose>
					<c:when test="${selectedGender eq 'Male'}">
						<dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="Male" id="gender-male" name="gender" checked="true"/>
					</c:when>
					<c:otherwise>
						<dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="Male" id="gender-male" name="gender"/>
					</c:otherwise>
				</c:choose>
				Male
			</label>
		</div>
		<div class="radio">
			<label for="gender-female">
				<c:choose>
					<c:when test="${selectedGender eq 'Female'}">
						<dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="Female" id="gender-female" name="gender" checked="true"/>
					</c:when>
					<c:otherwise>
						<dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="Female" id="gender-female" name="gender"/>
					</c:otherwise>
				</c:choose>
				Female
			</label>
		</div>
	</div>

	<div class="field-group">
		<dsp:input bean="ProfileFormHandler.updateErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
		<dsp:input bean="ProfileFormHandler.updateSuccessURL" type="hidden" value="${contextPath}/account/json/profileSuccess.jsp"/>
		<dsp:input bean="ProfileFormHandler.updateProfile" type="hidden" value="Update Profile" />
		<input id="update-profile-submit" name="update-profile-submit" type="submit" class="button primary" value="Update Profile" />
	</div>
</dsp:page>
