<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
	<dsp:importbean bean="/atg/commerce/payment/CreditCardTools"/>
	<dsp:importbean bean="/com/mff/droplet/MFFExpiryYearDroplet"/>

	<div class="field-group">
		<label for="card-type">* Card Type</label>
		<dsp:select bean="PaymentGroupFormHandler.creditCardType" id="card-type" name="card-type" class="form-control" data-validation="required" data-fieldname="Card Type">
			<dsp:getvalueof var="supportedCreditCardTypes" bean="CreditCardTools.supportedCreditCardTypes" />
			<option value="">Please Select</option>
			<c:forEach var="creditCardType" items="${supportedCreditCardTypes}">
	          <dsp:option value="${creditCardType}"><fmt:message key="paymentMethod.card.${creditCardType}"/></dsp:option>
	        </c:forEach>
		</dsp:select>
	</div>

	<div class="field-group">
		<label for="card-number">* Card Number</label>
		<dsp:input type="text" bean="PaymentGroupFormHandler.creditCardNumber" id="card-number" name="card-number" autocapitalize="off" data-validation="required" data-fieldname="Card Number" maxlength="16" value=""/>
	</div>

	<div class="field-group">
		<label for="name">* Name on Card</label>
		<dsp:input type="text" bean="PaymentGroupFormHandler.nameOnCard" maxlength="254" id="name" name="name" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Name on Card" value=""/>
	</div>

	<div class="field-group three-fields">
		<div class="field">
			<label for="month">* Month</label>
			<dsp:select bean="PaymentGroupFormHandler.creditCardExpMonth" id="month" name="month" class="form-control" data-validation="required" data-fieldname="Month">
				<option value="">MM</option>
				<dsp:droplet name="MFFExpiryYearDroplet">
					<dsp:param name="type" value="month"/>
					<dsp:oparam name="output">
						<dsp:getvalueof param="ExpMonth" var="ExpMonth"/>
						<dsp:option value="${ExpMonth}" ><c:out value="${ExpMonth}"/> </dsp:option>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:select>
		</div>
		<div class="field">
			<label for="year">* Year</label>
			<dsp:select bean="PaymentGroupFormHandler.creditCardExpYear" id="year" name="year" class="form-control" data-validation="required" data-fieldname="Year">
				<option value="">YYYY</option>
				<dsp:droplet name="MFFExpiryYearDroplet">
					<dsp:param name="type" value="year"/>
					<dsp:oparam name="output">
						<dsp:getvalueof param="ExpYear" var="ExpYear"/>
						<dsp:option value="${ExpYear}"><c:out value="${ExpYear}"/> </dsp:option>
					</dsp:oparam>
				</dsp:droplet>
				<dsp:option value="2049" iclass="hide">2049</dsp:option>
			</dsp:select>
		</div>
		<div class="field">
			<label for="cvv">* CVV <a href="${contextPath}/checkout/ajax/cvvModal.jsp" class="modal-trigger" data-target="cvv-modal" data-size="small"><span class="icon icon-info"></span></a></label>
			<dsp:input type="tel" bean="PaymentGroupFormHandler.creditCardCVV" id="cvv" name="cvv" iclass="cvv-input" autocapitalize="off" data-validation="required numeric minlength" data-fieldname="CVV" min-length="3" maxlength="4" value=""/>
		</div>
	</div>

</dsp:page>
