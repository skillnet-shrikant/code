<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

	<%-- Page Variables --%>
	<dsp:getvalueof param="isPaymentForm" var="isPaymentForm"/>

	<div class="field-group">
		<label for="address-name">* Address Name</label>
		<c:choose>
			<c:when test="${not empty isPaymentForm}">
				<dsp:input maxlength="42" bean="ProfileFormHandler.editValue.nickname" id="address-name" name="address-name" type="text" autocapitalize="off" data-validation="required nameField" data-fieldname="Address Name" placeholder="ex: John's House" value=""/>
			</c:when>
			<c:otherwise>
				<dsp:getvalueof var="nname" bean="ProfileFormHandler.editValue.nickname"/>
				<c:choose>
					<c:when test="${empty nname}">
						<dsp:input bean="ProfileFormHandler.editValue.nickname" id="address-name" name="address-name" type="text" autocapitalize="off" data-validation="required nameField" maxlength="42" data-fieldname="Address Name" placeholder="ex: John's House"/>
					</c:when>
					<c:otherwise>
						<dsp:input bean="ProfileFormHandler.editValue.nickname" id="address-name" name="address-name" type="text" autocapitalize="off" data-validation="required nameField" maxlength="42" data-fieldname="Address Name" placeholder="ex: John's House"/>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="first">* First Name</label>
		<dsp:input bean="ProfileFormHandler.editValue.firstName" maxlength="15" id="first" name="firstName" type="text" autocapitalize="off" data-validation="required name" data-fieldname="First Name" value=""/>
	</div>

	<div class="field-group">
		<label for="last">* Last Name</label>
		<dsp:input bean="ProfileFormHandler.editValue.lastName" maxlength="20" id="last" name="lastName" type="text" autocapitalize="off" data-validation="required name" data-fieldname="Last Name" value=""/>
	</div>
	
	<div class="field-group">
		<label for="attention">Attention (optional)</label>
		<dsp:input bean="ProfileFormHandler.editValue.attention" maxlength="254" id="attention" name="attention" type="text" autocapitalize="off" data-validation="alphaspace" data-fieldname="Attention" placeholder="Attention (optional)" value=""/>
	</div>

	<c:set var="poboxClass" value="nopobox" scope="request" />
	<c:if test="${not empty isPaymentForm}">
		<c:set var="poboxClass" value="" scope="request" />
	</c:if>

	<div class="field-group">
		<label for="address">* Address</label>
		<dsp:input bean="ProfileFormHandler.editValue.address1" maxlength="30" id="address" name="address1" type="text" autocapitalize="off" data-validation="required address ${poboxClass}" data-fieldname="Address" placeholder="Street address" value=""/>
	</div>

	<div class="field-group">
		<label for="address2">Address 2 (optional)</label>
		<dsp:input bean="ProfileFormHandler.editValue.address2" maxlength="30" id="address2" name="address2" type="text" autocapitalize="off" data-validation="address" data-fieldname="Address 2" placeholder="Apartment, suite, unit, building, floor" value=""/>
	</div>

	<div class="field-group">
		<label for="city">* City</label>
		<dsp:input bean="ProfileFormHandler.editValue.city" maxlength="25" id="city" name="city" type="text" autocapitalize="off" data-validation="required alphaspace" data-fieldname="City" value=""/>
	</div>

	<div class="field-group">
		<div class="field">
			<label for="state">* State</label>
			<dsp:select bean="ProfileFormHandler.editValue.state" id="state" name="state" data-validation="required" data-fieldname="State" nodefault="true">
				<%@include file="/sitewide/includes/countryStatePicker.jspf" %>
			</dsp:select>
		</div>
		<div class="field">
			<label for="zip">* Zip Code</label>
			<dsp:input bean="ProfileFormHandler.editValue.postalCode" id="zip" name="zip" type="text" autocapitalize="off" data-validation="required uspostal" data-fieldname="Zip Code" placeholder="5 digits" value=""/>
		</div>
	</div>

	<div class="field-group">
		<label for="phone">* Phone</label>
		<dsp:input bean="ProfileFormHandler.editValue.phoneNumber" id="phone" name="phoneNumber" type="text" autocapitalize="off" data-validation="required usphone" data-fieldname="Phone" placeholder="Area Code and Number" value=""/>
	</div>

	<%-- This flag will skip the Address validation service --%>
	<dsp:input bean="ProfileFormHandler.addressVerified" type="hidden" id="skip-avs" value=""/>

	<dsp:input type="hidden" bean="ProfileFormHandler.editValue.country" value="US" />

	<c:if test="${empty isPaymentForm}">
		<div class="field-group">
			<dsp:input bean="ProfileFormHandler.newAddress" id="address-submit" name="address-submit" type="submit" class="button primary" title="${saveTitle}" value="Save Address" />
			<dsp:input bean="ProfileFormHandler.newAddressSuccessURL" type="hidden" value="${contextPath}/account/json/avsAddressSuccess.jsp"/>
			<dsp:input bean="ProfileFormHandler.newAddressErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
		</div>
	</c:if>
</dsp:page>
