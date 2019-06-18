<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/mff/userprofiling/droplet/TaxExemptionClassificationsDroplet"/>
	<dsp:importbean bean="/atg/multisite/SiteContext"/>
	<dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="formHandlerComponent" value="/atg/userprofiling/ProfileFormHandler.editValue"/>
	<dsp:getvalueof var="preFillValues" vartype="java.lang.Boolean" param="preFillValues" />
	<dsp:getvalueof var="addEditMode" param="addEditMode"/>

	<dsp:input bean="ProfileFormHandler.editValue.ownerId" beanvalue="Profile.id" type="hidden"/>

	<div class="field-group">
		<c:choose>
			<c:when test="${addEditMode == 'edit'}">
				<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="true"/>
				<label for="nickname">* Nickname</label>
				<dsp:input type="hidden" bean="ProfileFormHandler.editValue.nickname"/>
				<dsp:input id="nickname" name="nickname" type="text" autocapitalize="off" maxlength="42" bean="ProfileFormHandler.editValue.newNickname" data-validation="required nameField" data-fieldname="nickname" placeholder="ex: John's Farm"/>
			</c:when>
			<c:otherwise>
				<label for="nickname">* Nickname</label>
				<dsp:getvalueof var="nname" bean="ProfileFormHandler.editValue.nickname"/>
				<c:choose>
					<c:when test="${empty nname}">
						<dsp:input id="nickname" name="nickname" type="text" autocapitalize="off" maxlength="42" bean="ProfileFormHandler.editValue.nickname" data-validation="required nameField" data-fieldname="nickname" placeholder="ex: John's Farm" value=""/>
					</c:when>
					<c:otherwise>
						<dsp:input id="nickname" name="nickname" type="text" autocapitalize="off" maxlength="42" bean="ProfileFormHandler.editValue.nickname" data-validation="required nameField" data-fieldname="nickname" placeholder="ex: John's Farm"/>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="classification">* Classification</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:select bean="${formHandlerComponent}.classificationId" id="classification" data-validation="required" data-fieldname="Classification">
					<dsp:droplet name="TaxExemptionClassificationsDroplet">
						<dsp:oparam name="output">
							<dsp:option value="">Please Select</dsp:option>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="classifications"/>
								<dsp:param name="sortProperties" value="+taxExmpCode"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="id" param="element.repositoryId" />
									<dsp:getvalueof var="displayName" param="element.displayName"/>
									<dsp:option value="${id}">${displayName}</dsp:option>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:select>
			</c:when>
			<c:otherwise>
				<dsp:select bean="${formHandlerComponent}.classificationId" id="classification" nodefault="true" data-validation="required" data-fieldname="Classification">
					<dsp:droplet name="TaxExemptionClassificationsDroplet">
						<dsp:oparam name="output">
							<dsp:option value="">Please Select</dsp:option>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="classifications"/>
								<dsp:param name="sortProperties" value="+taxExmpCode"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="id" param="element.repositoryId" />
									<dsp:getvalueof var="displayName" param="element.displayName"/>
									<dsp:option value="${id}">${displayName}</dsp:option>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:select>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="tax-id">* Tax ID</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:input id="tax-id" name="tax-id" type="text" bean="${formHandlerComponent}.taxId" maxlength="" autocapitalize="off" data-validation="required taxField" data-fieldname="Tax ID" placeholder="Tax ID" />
			</c:when>
			<c:otherwise>
				<dsp:input id="tax-id" name="tax-id" type="text" bean="${formHandlerComponent}.taxId" maxlength="" autocapitalize="off" data-validation="required taxField" data-fieldname="Tax ID" placeholder="Tax ID" value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="first">* First Name</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:input id="first" name="first" type="text" autocapitalize="off" bean="${formHandlerComponent}.firstName" maxlength="40" data-validation="required name" data-fieldname="First Name" />
			</c:when>
			<c:otherwise>
				<dsp:input id="first" name="first" type="text" autocapitalize="off" bean="${formHandlerComponent}.firstName" maxlength="40" data-validation="required name" data-fieldname="First Name" value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="last">* Last Name</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:input id="last" name="last" type="text" bean="${formHandlerComponent}.lastName" maxlength="40" autocapitalize="off" data-validation="required name" data-fieldname="Last Name" />
			</c:when>
			<c:otherwise>
				<dsp:input id="last" name="last" type="text" bean="${formHandlerComponent}.lastName" maxlength="40" autocapitalize="off" data-validation="required name" data-fieldname="Last Name" value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="organization">* Organization Name</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:input id="organization" name="organization" type="text" bean="${formHandlerComponent}.orgName"  maxlength="244" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Organization Name" placeholder="ex: John's Farm"/>
			</c:when>
			<c:otherwise>
				<dsp:input id="organization" name="organization" type="text" bean="${formHandlerComponent}.orgName"  maxlength="244" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Organization Name" placeholder="ex: John's Farm" value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="address">* Address</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:input id="address" name="address" type="text" bean="${formHandlerComponent}.address1" maxlength="50" autocapitalize="off" data-validation="required address" data-fieldname="Address" placeholder="Street address"/>
			</c:when>
			<c:otherwise>
				<dsp:input id="address" name="address" type="text" bean="${formHandlerComponent}.address1" maxlength="50" autocapitalize="off" data-validation="required address" data-fieldname="Address" placeholder="Street address" value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="address2">Address 2 (optional)</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:input id="address2" name="address2" type="text" bean="${formHandlerComponent}.address2" maxlength="50" autocapitalize="off" data-validation="address" data-fieldname="Address 2" placeholder="Apartment, suite, unit, building, floor"/>
			</c:when>
			<c:otherwise>
				<dsp:input id="address2" name="address2" type="text" bean="${formHandlerComponent}.address2" maxlength="50" autocapitalize="off" data-validation="address" data-fieldname="Address 2" placeholder="Apartment, suite, unit, building, floor" value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="city">* City</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:input id="city" name="city" type="text" bean="${formHandlerComponent}.city" maxlength="30" autocapitalize="off" data-validation="required alphaspace" data-fieldname="City" />
			</c:when>
			<c:otherwise>
				<dsp:input id="city" name="city" type="text" bean="${formHandlerComponent}.city" maxlength="30" autocapitalize="off" data-validation="required alphaspace" data-fieldname="City" value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<dsp:input type="hidden" bean="${formHandlerComponent}.country" value="US"/>

	<div class="field-group">
		<div class="field">
			<label for="state">* State</label>
			<c:choose>
				<c:when test="${preFillValues}">
					<dsp:select bean="${formHandlerComponent}.state" id="state" data-validation="required" data-fieldname="State">
						<%@include file="/sitewide/includes/countryStatePicker.jspf" %>
					</dsp:select>
				</c:when>
				<c:otherwise>
					<dsp:select bean="${formHandlerComponent}.state" nodefault="true" id="state" data-validation="required" data-fieldname="State">
						<%@include file="/sitewide/includes/countryStatePicker.jspf" %>
					</dsp:select>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="field">
			<label for="zip">* Zip Code</label>
			<c:choose>
				<c:when test="${preFillValues}">
					<dsp:input id="zip" name="zip" type="text" bean="${formHandlerComponent}.postalCode" maxlength="10" autocapitalize="off" data-validation="required uspostal" data-fieldname="Zip Code" placeholder="5 digits"/>
				</c:when>
				<c:otherwise>
					<dsp:input id="zip" name="zip" type="text" bean="${formHandlerComponent}.postalCode" maxlength="10" autocapitalize="off" data-validation="required uspostal" data-fieldname="Zip Code" placeholder="5 digits" value=""/>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<div class="field-group">
		<label for="description">* Business Description</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:input id="description" name="description" type="text" bean="${formHandlerComponent}.businessDesc"  maxlength="244" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Business Description" placeholder="ex: Farm, Non-profit, Religious Organization, etc."/>
			</c:when>
			<c:otherwise>
				<dsp:input id="description" name="description" type="text" bean="${formHandlerComponent}.businessDesc"  maxlength="244" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Business Description" placeholder="ex: Farm, Non-profit, Religious Organization, etc." value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="merchandise">* Merchandise to be Purchased</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:input id="merchandise" name="merchandise" type="text" bean="${formHandlerComponent}.merchandise"  maxlength="244" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Merchandise to be Purchased" placeholder="ex: Farming Equipment, School Supplies, etc."/>
			</c:when>
			<c:otherwise>
				<dsp:input id="merchandise" name="merchandise" type="text" bean="${formHandlerComponent}.merchandise"  maxlength="244" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Merchandise to be Purchased" placeholder="ex: Farming Equipment, School Supplies, etc." value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="tax-city">* Tax Jurisdiction City</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:input id="tax-city" name="tax-city" type="text" bean="${formHandlerComponent}.taxCity" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Tax Jurisdiction City" />
			</c:when>
			<c:otherwise>
				<dsp:input id="tax-city" name="tax-city" type="text" bean="${formHandlerComponent}.taxCity" autocapitalize="off" data-validation="required alphaspace" data-fieldname="Tax Jurisdiction City" value=""/>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<label for="tax-state">* Tax Jurisdiction State</label>
		<c:choose>
			<c:when test="${preFillValues}">
				<dsp:select bean="${formHandlerComponent}.taxState" id="tax-state" data-validation="required" data-fieldname="Tax Jurisdiction State">
					<%@include file="/sitewide/includes/countryStatePicker.jspf" %>
				</dsp:select>
			</c:when>
			<c:otherwise>
				<dsp:select bean="${formHandlerComponent}.taxState" nodefault="true" id="tax-state" data-validation="required" data-fieldname="Tax Jurisdiction State">
					<%@include file="/sitewide/includes/countryStatePicker.jspf" %>
				</dsp:select>
			</c:otherwise>
		</c:choose>
	</div>

	<div class="field-group">
		<div class="checkbox">
			<label for="agree-to-terms">
				<dsp:input type="checkbox" id="agree-to-terms" bean="ProfileFormHandler.taxExemptionAgreed" data-validation="required" data-fieldname="Agreeing to the Terms of Use"/>
				* I agree to the <a href="${contextPath}/static/visitor-info-collection" target="_blank">Online Store Terms Of Use</a>
			</label>
		</div>
	</div>

	<%-- This flag will skip the Address validation service --%>
	<dsp:input bean="ProfileFormHandler.addressVerified" type="hidden" id="skip-avs" value=""/>

	<div class="field-group">
		<c:choose>
			<c:when test="${addEditMode == 'edit'}">
				<dsp:input id="tax-exemption-submit" name="tax-exemption-submit" bean="ProfileFormHandler.updateTaxExemption" type="submit" class="button primary" value="Update Tax Exemption"/>
				<dsp:input bean="ProfileFormHandler.updateTaxExmpSuccessURL" type="hidden" value="${contextPath}/account/json/avsTaxExemptionSuccess.jsp"/>
				<dsp:input bean="ProfileFormHandler.updateTaxExmpErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
			</c:when>
			<c:otherwise>
				<dsp:input id="tax-exemption-submit" name="tax-exemption-submit" bean="ProfileFormHandler.addTaxExemption" type="submit" class="button primary" value="Create Tax Exemption" />
				<dsp:input bean="ProfileFormHandler.addTaxExmpSuccessURL" type="hidden" value="${contextPath}/account/json/avsTaxExemptionSuccess.jsp"/>
				<dsp:input bean="ProfileFormHandler.addTaxExmpErrorURL" type="hidden" value="${contextPath}/account/json/profileError.jsp"/>
			</c:otherwise>
		</c:choose>
	</div>

</dsp:page>
