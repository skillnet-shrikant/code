<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler" />

	<%-- section title --%>
	<h3>FFL Dealer Information</h3>
	<div class="required-note">* Required</div>

	<div class="field-group">
		<label for="ffl-business-name">* Business Name</label>
		<dsp:input id="ffl-business-name" name="ffl-business-name" type="text" bean="ShippingGroupFormHandler.companyName" autocapitalize="off" data-validation="required name" data-fieldname="Business Name" placeholder="Business Name" value=""/>
	</div>

	<div class="field-group">
		<label for="address">* Address</label>
		<dsp:input id="address" name="address" type="text" bean="ShippingGroupFormHandler.address1" autocapitalize="off" data-validation="required address" data-fieldname="Address" placeholder="Address" value=""/>
	</div>

	<div class="field-group">
		<label for="address2">Address 2 (optional)</label>
		<dsp:input id="address2" name="address2" type="text" bean="ShippingGroupFormHandler.address2" autocapitalize="off" data-validation="address" data-fieldname="Address 2" placeholder="Address 2 (optional)" value=""/>
	</div>

	<div class="field-group">
		<label for="city">* City</label>
		<dsp:input id="city" name="city" type="text" bean="ShippingGroupFormHandler.city" autocapitalize="off" data-validation="required alphaspace" data-fieldname="City" placeholder="City" value=""/>
	</div>

	<div class="field-group">
		<div class="field">
			<label for="state">* State</label>
			<dsp:select class="form-control" id="state" name="state" bean="ShippingGroupFormHandler.state" data-validation="required" data-fieldname="State" value="">
				<%@include file="/sitewide/includes/countryStatePicker.jspf" %>
			</dsp:select>
		</div>
		<div class="field">
			<label for="zip">* Zip Code</label>
			<dsp:input id="zip" name="zip" type="text" bean="ShippingGroupFormHandler.postalCode" autocapitalize="off" data-validation="required uspostal" data-fieldname="Zip Code" placeholder="Zip Code" value=""/>
		</div>
	</div>

	<div class="field-group">
		<label for="phone">* Phone</label>
		<dsp:input id="phone" name="phone" type="text" bean="ShippingGroupFormHandler.phoneNumber" autocapitalize="off" data-validation="required usphone" data-fieldname="Phone" placeholder="Area Code and Number" value=""/>
	</div>

	<div class="field-group">
		<label for="ffl-email">FFL Dealer Email Address</label>
		<dsp:input id="ffl-email" name="ffl-email" type="email" bean="ShippingGroupFormHandler.fflEmail" autocapitalize="off" data-validation="email" data-fieldname="FFL Dealer Email Address" placeholder="FFL Dealer Email Address" value="" maxlength="255"/>
	</div>

	<div class="field-group">
		<label for="ffl-contact-name">* Contact Name</label>
		<dsp:input id="ffl-contact-name" name="ffl-contact-name" type="text" bean="ShippingGroupFormHandler.fflContactName" autocapitalize="off" data-validation="required name" data-fieldname="Contact Name" placeholder="Contact Name" value=""/>
	</div>


	<div class="field-group">
		<label for="ffl-dealer-ff">* Dealer FF#</label>
		<dsp:input id="ffl-dealer-ff" name="ffl-dealer-ff" type="text" bean="ShippingGroupFormHandler.fflDealerFFNumber" autocapitalize="off" data-validation="required" data-fieldname="Dealer FF#" placeholder="Dealer FF#" value=""/>
	</div>

	<dsp:input type="hidden" bean="ShippingGroupFormHandler.country" value="US" />

	<%-- This flag will skip the Address validation service --%>
	<dsp:input bean="ShippingGroupFormHandler.fflAddressVerified" type="hidden" id="skip-avs" value="" />

</dsp:page>
