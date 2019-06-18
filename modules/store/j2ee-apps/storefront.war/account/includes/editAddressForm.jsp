<dsp:page>

	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:getvalueof param="isPaymentForm" var="isPaymentForm"/>

	<c:choose>
		<c:when test="${not empty isPaymentForm}">
			<dsp:getvalueof param="billingAddress.firstName" var="firstName"/>
			<dsp:getvalueof param="billingAddress.lastName" var="lastName"/>
			<dsp:getvalueof param="billingAddress.address1" var="address1"/>
			<dsp:getvalueof param="billingAddress.address2" var="address2"/>
			<dsp:getvalueof param="billingAddress.city" var="city"/>
			<dsp:getvalueof param="billingAddress.state" var="state"/>
			<dsp:getvalueof param="billingAddress.postalCode" var="postalCode"/>
			<dsp:getvalueof param="billingAddress.phoneNumber" var="phoneNumber"/>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="nickName" param="nickName" />
			<dsp:getvalueof var="firstName" bean="ProfileFormHandler.editValue.firstName" />
			<dsp:getvalueof var="lastName" bean="ProfileFormHandler.editValue.lastName" />
			<dsp:getvalueof var="attention" bean="ProfileFormHandler.editValue.attention" />
			<dsp:getvalueof var="address1" bean="ProfileFormHandler.editValue.address1" />
			<dsp:getvalueof var="address2" bean="ProfileFormHandler.editValue.address2" />
			<dsp:getvalueof var="city" bean="ProfileFormHandler.editValue.city" />
			<dsp:getvalueof var="state" bean="ProfileFormHandler.editValue.state" />
			<dsp:getvalueof var="postalCode" bean="ProfileFormHandler.editValue.postalCode" />
			<dsp:getvalueof var="phoneNumber" bean="ProfileFormHandler.editValue.phoneNumber" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${not empty isPaymentForm}">
			<c:if test="${empty firstName}">
				<div class="field-group">
					<label for="address-name">* Address Name</label>
					<dsp:input bean="ProfileFormHandler.editValue.nickname" maxlength="42" id="address-name" name="address-name" type="text" autocapitalize="off" data-validation="required nameField" data-fieldname="Address Name" placeholder="Address Name" value=""/>
				</div>
			</c:if>
			<%-- Added this for avoiding the required field validation error --%>
			<c:if test="${not empty firstName}">
				<dsp:input bean="ProfileFormHandler.editValue.nickname" type="hidden" value="${firstName}"/>
			</c:if>
		</c:when>
		<c:otherwise>
			<dsp:input type="hidden" bean="ProfileFormHandler.editValue.nickname"/>
			<div class="field-group">
				<label for="address-name">* Address Name</label>
				<dsp:input bean="ProfileFormHandler.editValue.newNickname" maxlength="42" id="address-name" name="address-name" type="text" autocapitalize="off" data-validation="required nameField" data-fieldname="Address Name" placeholder="Address Name"/>
			</div>
		</c:otherwise>
	</c:choose>

	<div class="field-group">
		<label for="first">* First Name</label>
		<dsp:input bean="ProfileFormHandler.editValue.firstName" maxlength="15" id="first" name="firstName" type="text" autocapitalize="off" data-validation="required name" data-fieldname="First Name" placeholder="First Name" value="${firstName}"/>
	</div>

	<div class="field-group">
		<label for="last">* Last Name</label>
		<dsp:input bean="ProfileFormHandler.editValue.lastName" maxlength="20" id="last" name="lastName" type="text" autocapitalize="off" data-validation="required name" data-fieldname="Last Name" placeholder="Last Name" value="${lastName}"/>
	</div>
	
	<div class="field-group">
		<label for="attention">Attention (optional)</label>
		<dsp:input bean="ProfileFormHandler.editValue.attention" maxlength="254" id="attention" name="attention" type="text" autocapitalize="off" data-validation="alphaspace" data-fieldname="Attention" placeholder="Attention (optional)" value="${attention}"/>
	</div>
	
	<c:set var="poboxClass" value="nopobox" scope="request" />
	<c:if test="${not empty isPaymentForm}">
		<c:set var="poboxClass" value="" scope="request" />
	</c:if>

	<div class="field-group">
		<label for="address">* Address</label>
		<dsp:input bean="ProfileFormHandler.editValue.address1" id="address" maxlength="30" name="address1" type="text" autocapitalize="off" data-validation="required address ${poboxClass}" data-fieldname="Address" placeholder="Address" value="${address1}"/>
	</div>

	<div class="field-group">
		<label for="address2">Address 2 (optional)</label>
		<dsp:input bean="ProfileFormHandler.editValue.address2" id="address2" maxlength="30" name="address2" type="text" autocapitalize="off" data-validation="address" data-fieldname="Address 2" placeholder="Address 2 (optional)" value="${address2}"/>
	</div>

	<div class="field-group">
		<label for="city">* City</label>
		<dsp:input bean="ProfileFormHandler.editValue.city" id="city" maxlength="25" name="city" type="text" autocapitalize="off" data-validation="required alphaspace" data-fieldname="City" placeholder="City" value="${city}"/>
	</div>

	<div class="field-group">
		<div class="field">
			<label for="state">* State</label>
			<dsp:select bean="ProfileFormHandler.editValue.state" id="state" name="state" data-validation="required" data-fieldname="State">
				<%@include file="/sitewide/includes/countryStatePicker.jspf" %>
			</dsp:select>
		</div>
		<div class="field">
			<label for="zip">* Zip Code</label>
			<dsp:input bean="ProfileFormHandler.editValue.postalCode" id="zip" name="zip" type="text" autocapitalize="off" data-validation="required uspostal" data-fieldname="Zip Code" placeholder="Zip Code" value="${postalCode}"/>
		</div>
	</div>

	<div class="field-group">
		<label for="phone">* Phone</label>
		<dsp:input bean="ProfileFormHandler.editValue.phoneNumber" id="phone" name="phoneNumber" type="text" autocapitalize="off" data-validation="required usphone" data-fieldname="Phone" placeholder="Area Code and Number" value="${phoneNumber}"/>
	</div>

	<%-- This flag will skip the Address validation service --%>
	<dsp:input bean="ProfileFormHandler.addressVerified" type="hidden" id="skip-avs" value=""/>

	<dsp:input type="hidden" bean="ProfileFormHandler.country" value="US" />

	<c:if test="${empty isPaymentForm}">
		<div class="field-group">
			<dsp:input bean="ProfileFormHandler.updateAddress" id="address-submit" name="address-submit" type="submit" class="button primary" value="Save Address"/>
			<dsp:input bean="ProfileFormHandler.updateAddressSuccessURL" type="hidden" value="${contextPath}/account/json/avsAddressSuccess.jsp"/>
			<dsp:input bean="ProfileFormHandler.updateAddressErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
		</div>
	</c:if>
</dsp:page>
