<dsp:page>

	<%-- Page Variables --%>
	<dsp:getvalueof var="billing" param="billing" vartype="boolean" />
	<c:set var="prefix" value="" scope="request" />
	<c:set var="firstNamePlaceholder" value="" scope="request" />
	<c:set var="lastNamePlaceholder" value="" scope="request" />
	<c:set var="address1Placeholder" value="Street address" scope="request" />
	<c:set var="address2Placeholder" value="Apartment, suite, unit, building, floor" scope="request" />
	<c:set var="cityPlaceholder" value="" scope="request" />
	<c:set var="zipPlaceholder" value="5 digits" scope="request" />
	<c:set var="phonePlaceholder" value="Area Code and Number" scope="request" />
	<c:set var="nicknamePlaceholder" value="ex: John's House" scope="request" />
	<c:set var="emailPlaceholder" value="" scope="request" />

	<c:choose>
		<c:when test="${billing}">
			<c:set var="prefix" value="billing-" scope="request" />
			<dsp:getvalueof var="formHandler" value="/atg/commerce/order/purchase/PaymentGroupFormHandler" />
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="formHandler" value="/atg/commerce/order/purchase/ShippingGroupFormHandler" />
		</c:otherwise>
	</c:choose>

	<div class="field-group">
		<label for="${prefix}first">* First Name</label>
		<dsp:input id="${prefix}first" name="firstName" maxlength="15" type="text" bean="${formHandler}.firstName" autocapitalize="off" data-validation="required name" data-fieldname="First Name" placeholder="${firstNamePlaceholder}" value=""/>
	</div>

	<div class="field-group">
		<label for="${prefix}last">* Last Name</label>
		<dsp:input id="${prefix}last" name="lastName" maxlength="20" type="text" bean="${formHandler}.lastName" autocapitalize="off" data-validation="required name" data-fieldname="Last Name" placeholder="${lastNamePlaceholder}" value=""/>
	</div>

	<c:if test="${not billing}">
		<div class="field-group">
			<label for="attention">Attention (optional)</label>
			<dsp:input id="attention" name="attention" maxlength="254" type="text" bean="${formHandler}.specialInstructions" autocapitalize="off" data-validation="alphaspace" data-fieldname="Attention" placeholder="Attention (optional)" value=""/>
		</div>
	</c:if>

	<c:set var="poboxClass" value="nopobox" scope="request" />
	<c:if test="${billing}">
		<c:set var="poboxClass" value="" scope="request" />
	</c:if>

	<div class="field-group">
		<label for="${prefix}address">* Address</label>
		<dsp:input id="${prefix}address" name="address1" maxlength="30" type="text" bean="${formHandler}.address1" autocapitalize="off" data-validation="required address ${poboxClass}" data-fieldname="Address" placeholder="${address1Placeholder}" value=""/>
	</div>

	<div class="field-group">
		<label for="${prefix}address2">Address 2 (optional)</label>
		<dsp:input id="${prefix}address2" name="address2" maxlength="30" type="text" bean="${formHandler}.address2" autocapitalize="off" data-validation="address" data-fieldname="Address 2" placeholder="${address2Placeholder}" value=""/>
	</div>

	<div class="field-group">
		<label for="${prefix}city">* City</label>
		<dsp:input id="${prefix}city" name="city" maxlength="25" type="text" bean="${formHandler}.city" autocapitalize="off" data-validation="required nameField" data-fieldname="City" placeholder="${cityPlaceholder}" value=""/>
	</div>

	<div class="field-group">
		<div class="field">
			<label for="${prefix}state">* State</label>
			<dsp:select class="form-control" id="${prefix}state" name="state" bean="${formHandler}.state" data-validation="required" data-fieldname="State" value="">
				<%@include file="/sitewide/includes/countryStatePicker.jspf" %>
			</dsp:select>
		</div>
		<div class="field">
			<label for="${prefix}zip">* Zip Code</label>
			<dsp:input id="${prefix}zip" name="zip" type="text" bean="${formHandler}.postalCode" autocapitalize="off" data-validation="required uspostal" data-fieldname="Zip Code" placeholder="${zipPlaceholder}" value=""/>
		</div>
	</div>

	<%-- This flag will skip the Address validation service --%>
	<dsp:input bean="${formHandler}.addressVerified" type="hidden" id="${prefix}skip-avs" value="" />

	<dsp:input type="hidden" bean="${formHandler}.country" value="US" />

	<div class="field-group">
		<label for="${prefix}phone">* Phone</label>
		<dsp:input id="${prefix}phone" name="phoneNumber" type="text" bean="${formHandler}.phoneNumber" autocapitalize="off" data-validation="required usphone" data-fieldname="Phone" placeholder="${phonePlaceholder}" value=""/>
	</div>

	<%-- option to save address if authenticated (shipping address only) --%>
	<c:if test="${userIsAuthenticated && not billing}">
		<div class="field-group">
			<div class="checkbox">
				<label for="save-shipping-address">
					<dsp:input id="save-shipping-address" bean="ShippingGroupFormHandler.saveShippingAddress" type="checkbox" checked="false" /> Save Shipping Address
				</label>
			</div>
		</div>
		<div class="field-group address-name">
			<label for="address-name">* Address Name</label>
			<dsp:input id="address-name" name="address-name" maxlength="42" bean="ShippingGroupFormHandler.addressNickname" type="text" autocapitalize="off" data-validation="required nameField" data-fieldname="Address Name" placeholder="${nicknamePlaceholder}" value="${addressName}"/>
		</div>
	</c:if>

</dsp:page>
