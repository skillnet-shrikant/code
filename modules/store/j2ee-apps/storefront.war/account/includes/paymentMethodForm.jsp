<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/mff/droplet/MFFExpiryYearDroplet"/>
	<dsp:importbean bean="/atg/commerce/payment/CreditCardTools"/>

	<%-- Page Variables --%>
	<dsp:getvalueof param="editCard" var="editCard"/>
	<dsp:getvalueof param="editCardName" var="editCardName"/>
	<dsp:getvalueof param="editCard.creditCardType" var="creditCardType"/>
	<dsp:getvalueof param="editCard.creditCardNumber" var="creditCardNumber"/>
	<dsp:getvalueof param="editCard.expirationMonth" var="expirationMonth"/>
	<dsp:getvalueof param="editCard.expirationYear" var="expirationYear"/>
	<dsp:getvalueof param="editCard.nameOnCard" var="nameOnCard"/>
	<dsp:getvalueof param="editCard.id" var="cardId"/>

	<dsp:input bean="ProfileFormHandler.editValue.cardId" value="${cardId}" type="hidden"/>

	<%-- Payment Name --%>
	<div class="field-group">
		<label for="payment-name">* Payment Name</label>
		<c:choose>
			<c:when test="${not empty editCard}">
				<dsp:input type="hidden" bean="ProfileFormHandler.editValue.editCardName" value="${editCardName}"/>
				<dsp:input bean="ProfileFormHandler.editValue.creditCardNickname" maxlength="42" id="payment-name" name="payment-name" type="text" autocapitalize="off" data-validation="required nameField" data-fieldname="Payment Name" placeholder="ex: John's Visa" value="${editCardName}"/>
			</c:when>
			<c:otherwise>
				<dsp:input bean="ProfileFormHandler.editValue.creditCardNickname" maxlength="42" id="payment-name" name="payment-name" type="text" autocapitalize="off" data-validation="required nameField" data-fieldname="Payment Name" placeholder="ex: John's Visa" value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<%-- Card Type --%>
	<div class="field-group">
		<label for="name">* Card Type</label>
		<dsp:select bean="ProfileFormHandler.editValue.creditCardType" id="card-type" data-validation="required" data-fieldname="Card Type">


			<dsp:getvalueof var="supportedCreditCardTypes" bean="CreditCardTools.supportedCreditCardTypes" />
			<dsp:option value="">Card Type</dsp:option>
			<c:forEach var="supportedCardType" items="${supportedCreditCardTypes}">
               <c:choose>
						<c:when test="${creditCardType eq supportedCardType}">
							<dsp:option value="${supportedCardType}" selected="true">
								<fmt:message key="paymentMethod.card.${supportedCardType}"/>
							</dsp:option>
						</c:when>
						<c:otherwise>
							<dsp:option value="${supportedCardType}">
							<fmt:message key="paymentMethod.card.${supportedCardType}"/>
							</dsp:option>
						</c:otherwise>
					</c:choose>
             </c:forEach>

		</dsp:select>
	</div>

	<%-- Card Number --%>
	<div class="field-group">
		<label for="card-number">* Card Number</label>
		<c:choose>
			<c:when test="${not empty editCard}">
				<dsp:input bean="ProfileFormHandler.editValue.creditCardNumber" id="card-number" name="card-number" type="text" autocapitalize="off" data-validation="required" data-fieldname="Card Number" value="${creditCardNumber}" maxlength="16"/>
			</c:when>
			<c:otherwise>
				<dsp:input bean="ProfileFormHandler.editValue.creditCardNumber" id="card-number" name="card-number" type="text" autocapitalize="off" data-validation="required" data-fieldname="Card Number" value="" maxlength="16"/>
			</c:otherwise>
		</c:choose>
	</div>

	<%-- Name on Card --%>
	<div class="field-group">
		<label for="name">* Name on Card</label>
		<c:choose>
			<c:when test="${not empty editCard}">
				<dsp:input bean="ProfileFormHandler.editValue.nameOnCard" maxlength="254" id="name" name="name" type="text" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Name on Card" value="${nameOnCard}"/>
			</c:when>
			<c:otherwise>
				<dsp:input bean="ProfileFormHandler.editValue.nameOnCard" maxlength="254" id="name" name="name" type="text" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Name on Card" value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<%-- Expiration / CVV --%>
	<div class="field-group">
		<div class="field">

			<%-- Expiration --%>
			<div class="field-group">
				<div class="field">
					<label for="month">* Month</label>
					<dsp:select bean="ProfileFormHandler.editValue.expirationMonth" id="month" data-validation="required" data-fieldname="Month">
						<dsp:option value="">MM</dsp:option>
						<dsp:droplet name="MFFExpiryYearDroplet">
							<dsp:param name="type" value="month"/>
							<dsp:oparam name="output">
								<dsp:getvalueof param="ExpMonth" var="ExpMonth"/>
								<c:choose>
									<c:when test="${expirationMonth eq ExpMonth}">
										<dsp:option value="${ExpMonth}" selected="true"><c:out value="${ExpMonth}"/> </dsp:option>
									</c:when>
									<c:otherwise>
										<dsp:option value="${ExpMonth}" ><c:out value="${ExpMonth}"/> </dsp:option>
									</c:otherwise>
								</c:choose>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:select>
				</div>
				<div class="field">
					<label for="year">* Year</label>
					<dsp:select bean="ProfileFormHandler.editValue.expirationYear" id="year" data-validation="required" data-fieldname="Year">
						<dsp:option value="">YYYY</dsp:option>
						<dsp:droplet name="MFFExpiryYearDroplet">
							<dsp:param name="type" value="year"/>
							<dsp:oparam name="output">
								<dsp:getvalueof param="ExpYear" var="ExpYear"/>
								<c:choose>
									<c:when test="${expirationYear eq ExpYear}">
										<dsp:option value="${ExpYear}" selected="true"><c:out value="${ExpYear}"/> </dsp:option>
									</c:when>
									<c:otherwise>
										<dsp:option value="${ExpYear}"><c:out value="${ExpYear}"/> </dsp:option>
									</c:otherwise>
								</c:choose>
							</dsp:oparam>
						</dsp:droplet>
						<dsp:option value="2049" iclass="hide">2049</dsp:option>
					</dsp:select>
				</div>
			</div>

		</div>

		<%-- CVV --%>
		<div class="field">
			<label for="cvv">* CVV</label>
			<c:choose>
				<c:when test="${not empty editCard}">
					<dsp:input bean="ProfileFormHandler.editValue.cvv" id="cvv" name="cvv" type="tel" autocapitalize="off" data-validation="required numeric minlength" data-fieldname="CVV" min-length="3" maxlength="4" value="${cvv}"/>
				</c:when>
				<c:otherwise>
					<dsp:input bean="ProfileFormHandler.editValue.cvv" id="cvv" name="cvv" type="tel" autocapitalize="off" data-validation="required numeric minlength" data-fieldname="CVV" min-length="3" maxlength="4" value=""/>
				</c:otherwise>
			</c:choose>
		</div>

	</div>

</dsp:page>
