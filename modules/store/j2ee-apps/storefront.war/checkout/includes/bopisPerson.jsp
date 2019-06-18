<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

	<h3>Selected Pick-Up Person</h3>

	<%-- BZ 2505 - Allow pickup details to be specified only if cart does not contain age restricted items --%>
	<dsp:getvalueof bean="ShoppingCart.current.signatureRequired" var="hasRestrictedItems"/>
	<dsp:getvalueof var="bopis" bean="ShoppingCart.current.bopisOrder"/>

	<c:choose>
	  <c:when test="${hasRestrictedItems eq true && bopis eq true}">
			<div class="restricted-message">
				<h4><span class="icon icon-error"></span> Age Restricted Item </h4>
				<p>
					Items(s) in your order have an age restriction for purchase. This order will require the
					purchaser to be present at time of pick up.
				</p>
				<p>
					The name on card provided on the next page <b><u>must</u></b> match the ID of the
					purchaser at the time of pickup for this order to be released.
				</p>
			</div>
	  </c:when>
	  <c:otherwise>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param bean="Profile.hardLoggedIn" name="value"/>
				<dsp:oparam name="true">
					<dsp:getvalueof var="first" bean="Profile.firstName"/>
					<dsp:getvalueof var="last" bean="Profile.lastName"/>
					<dsp:getvalueof var="email" bean="Profile.email"/>

					<div class="radio">
						<label for="pick-up-me">
							<input type="radio" value="pick-up-me" id="pick-up-me" name="pick-up-person" checked>
							<span class="label">I will pick up the order</span>
							<div class="pick-up-me-form">
									<p>${first}&nbsp;${last}</p>
									<p>${email}</p>
									<input id="bopis-name-me" name="bopis-name-me" type="hidden" value="${first.concat('-').concat(last)}"/>
									<input id="bopis-email-me" name="bopis-email-me" type="hidden" value="${email}"/>
							</div>
						</label>
					</div>
					<div class="radio">
						<label for="pick-up-other">
							<input type="radio" value="pick-up-other" id="pick-up-other" name="pick-up-person">
							<span class="label">Someone else will pick up the order</span>
							<div class="pick-up-other-form">
								<div class="field-group">
									<label for="bopis-name-other">Full Name of Pick-Up Person</label>
									<input id="bopis-name-other" maxlength="81" name="bopis-name-other" type="text" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Full Name of Pick-Up Person" placeholder="Full Name of Pick-Up Person" />
								</div>
								<div class="field-group">
									<label for="bopis-email-other">Email Address of Pick-Up Person</label>
									<input id="bopis-email-other" name="bopis-email-other" type="email" bautocapitalize="off" data-validation="required email" data-fieldname="Email Address of Pick-Up Person" placeholder="Email Address of Pick-Up Person" maxlength="255"/>
								</div>
							</div>
						</label>
					</div>

					<dsp:input id="bopis-name" name="bopis-name" type="hidden" bean="ShippingGroupFormHandler.pickUpPersonName" value="${first.concat('-').concat(last)}"/>
					<dsp:input id="bopis-email" name="bopis-email" type="hidden" bean="ShippingGroupFormHandler.pickUpPersonEmail" value="${email}"/>

				</dsp:oparam>
				<dsp:oparam name="false">
					<div class="field-group">
						<label for="bopis-name">Full Name of Person Picking Up Order</label>
						<dsp:input id="bopis-name" maxlength="81" name="bopis-name" type="text" bean="ShippingGroupFormHandler.pickUpPersonName" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Full name of person picking up order" value=""/>
					</div>
					<div class="field-group">
						<label for="bopis-email">Email Address of Person Picking Up Order</label>
						<dsp:input id="bopis-email" name="bopis-email" type="email" bean="ShippingGroupFormHandler.pickUpPersonEmail" autocapitalize="off" data-validation="required email" data-fieldname="Email address of person picking up order" value=""  maxlength="255"/>
					</div>
				</dsp:oparam>
			</dsp:droplet>
	  </c:otherwise>
	</c:choose>

</dsp:page>
