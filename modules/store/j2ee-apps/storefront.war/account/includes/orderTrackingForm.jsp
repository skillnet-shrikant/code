<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />

	<%-- Page Parameters --%>
	<dsp:setvalue bean="ProfileFormHandler.value.email" value=""/>

	<div class="field-group">
		<label for="email">Email Address</label>
		<dsp:input bean="ProfileFormHandler.value.email" id="email" name="email" type="email" autocapitalize="off" data-validation="required email" data-fieldname="Email Address" maxlength="255"/>
	</div>

	<div class="field-group">
		<label for="order-number">Order Number</label>
		<dsp:input bean="ProfileFormHandler.editValue.orderNumber" id="order-number" name="order-number" type="text" autocapitalize="off" data-validation="required" data-fieldname="Order Number" />
	</div>

	<div class="field-group">
		<dsp:input bean="ProfileFormHandler.trackOrderErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
		<dsp:input bean="ProfileFormHandler.trackOrderSuccessURL" type="hidden" value="${contextPath}/account/json/orderTrackingSuccess.jsp"/>
		<dsp:input bean="ProfileFormHandler.trackOrder" type="hidden" value="Track Order" />
		<input id="order-tracking-submit" name="order-tracking-submit" type="submit" class="button primary" value="Track Order" />
	</div>

</dsp:page>
