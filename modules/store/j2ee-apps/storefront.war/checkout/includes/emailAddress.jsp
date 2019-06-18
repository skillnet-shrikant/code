<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>

	<h3>Contact Information</h3>
	<div class="required-note">* Required</div>

	<c:choose>
		<c:when test="${userIsAuthenticated}">
			<dsp:getvalueof var="contactEmail" bean="Profile.email" />
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="contactEmail" value="" />
		</c:otherwise>
	</c:choose>

	<div class="field-group">
		<label for="email">* Email Address</label>
		<dsp:input id="email" name="email" type="text" bean="PaymentGroupFormHandler.contactEmail" autocapitalize="off" data-validation="required email" data-fieldname="Email Address" value="${contactEmail}"   maxlength="255"/>
	</div>

	<div class="field-group">
		<div class="checkbox">
			<label for="promo-emails">
				<input type="checkbox" id="promo-emails" name="promo-emails" checked /> Receive Promotional Emails
			</label>
		</div>
	</div>

</dsp:page>
