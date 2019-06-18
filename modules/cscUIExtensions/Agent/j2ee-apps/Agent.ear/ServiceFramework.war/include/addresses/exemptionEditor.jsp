<%--
Exemption Editor 
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:importbean bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler"/>
  <dspel:importbean bean="/atg/svc/agent/customer/CustomerPanelConfig" var="customerPanelConfig"/>
  <dspel:getvalueof var="exemptionMap" value="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler.editValue"/>
  <dspel:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dspel:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  <dspel:importbean bean="/com/mff/userprofiling/droplet/TaxExemptionClassificationsDroplet"/>
  <dspel:getvalueof var="customerProfile" bean="/atg/userprofiling/ActiveCustomerProfile"/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <fmt:message var="invalidNick" key="customer.exemption.invalidNickName"/>
	<fmt:message var="firstNameMissing" key="firstNameMissing" />
	<fmt:message var="lastNameMissing" key="lastNameMissing" />
	<fmt:message var="address1Missing" key="address1Missing" />
  	<fmt:message var="invalidState" key="invalidState" />
  	<fmt:message var="invalidTaxId" key="customer.exemption.invalidTaxId"/>
  	<fmt:message var="invalidOrgName" key="customer.exemption.invalidOrgName"/>
  	<fmt:message var="cityMissing" key="customer.exemption.invalidCity"/>
  	<fmt:message var="zipMissing" key="customer.exemption.invalidZip"/>
  	<fmt:message var="buzDescMissing" key="customer.exemption.invalidBuzDesc"/>
  	<fmt:message var="merchPurMissing" key="customer.exemption.invalidMerchPur"/>
  	
	<c:set var="countryCode" value="US"/>
    <svc-ui:frameworkPopupUrl var="url"
      value="/include/addresses/exemptionEditor.jsp"
      context="/agent"
      nickname="${param.nickname}"
      windowId="${windowId}"/>
    <svc-ui:frameworkPopupUrl var="successUrl"
      value="/include/addresses/exemptionEditor.jsp"
      context="/agent"
      success="true"
      nickname="${param.nickname}"
      windowId="${windowId}"/>

    <div class="atg_svc_popupPanel">
      <dspel:getvalueof var="formId" value="addExemptionForm"/>
      <dspel:form method="post" id="${formId}" formid="${formId}" action="#">
        <h3 id="atg_commerce_csr_customerinfo_popNewAddress"/>
        	<div class="atg_commerce_csr_popupPanelCloseButton">
        </div>
        <div class="atg-csc-base-table atg-base-table-customer-address-add-form">

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Nickname
					</label>
					<span class="requiredStar">*</span>
				</span>
				
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_nickname" name="${formId}_nickname" type="text" maxlength="40" bean="CustomerProfileFormHandler.nickname" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
			            <dspel:tagAttribute name="required" value="true"/>
			            <dspel:tagAttribute name="trim" value="true"/>
						<dspel:tagAttribute name="regExp" value="^[a-zA-Z0-9 ]+$"/>
						<dspel:tagAttribute name="invalidMessage" value="${invalidNick}"/>
					</dspel:input>
	        	</div>
			</div>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Classification
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dsp:getvalueof var="classValue" bean="${exemptionMap}.classification" />
					<dspel:select bean="${exemptionMap}.classification" id="${formId}_classification" name="${formId}_classification">
						<dspel:tagAttribute name="required" value="true"/>
						<dspel:droplet name="TaxExemptionClassificationsDroplet">
							<dspel:oparam name="output">
							<dspel:option value="">Please Select</dspel:option>
							<dspel:droplet name="ForEach">
								<dspel:param name="array" param="classifications"/>
								<dspel:oparam name="output">
									<dspel:getvalueof var="id" param="element.repositoryId" />
									<dspel:getvalueof var="displayName" param="element.displayName"/>
									<dspel:option value="${id}">${displayName}</dspel:option>
								</dspel:oparam>
							</dspel:droplet>
							</dspel:oparam>
						</dspel:droplet>
					</dspel:select>
	        	</div>
			</div>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Tax id
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_tax-id" name="${formId}_tax-id" type="text" bean="${exemptionMap}.taxId" maxlength="" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true"/>
						<dspel:tagAttribute name="regExp" value='^[a-zA-Z0-9.,-/&quot]+$'/>
						<dspel:tagAttribute name="trim" value="true" />
						<dspel:tagAttribute name="invalidMessage" value="${invalidTaxId}"/>
					</dspel:input>
	        	</div>
			</div>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						First Name
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_firstName" name="${formId}_firstName" type="text" bean="${exemptionMap}.firstName" maxlength="40" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true"/>
						<dspel:tagAttribute name="regExp" value="^[a-zA-Z. ]+$"/>
						<dspel:tagAttribute name="trim" value="true" />
						<dspel:tagAttribute name="invalidMessage" value="${firstNameMissing}"/>
					</dspel:input>
	        	</div>
			</div>
			
			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Last Name
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_lastName" name="${formId}_lastName" type="text" bean="${exemptionMap}.lastName" maxlength="40" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true"/>
						<dspel:tagAttribute name="regExp" value="^[a-zA-Z. ]+$"/>
						<dspel:tagAttribute name="trim" value="true" />
						<dspel:tagAttribute name="invalidMessage" value="${lastNameMissing}"/>
					</dspel:input>
	        	</div>
			</div>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Organization Name
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_orgName" name="${formId}_orgName" type="text" bean="${exemptionMap}.orgName" maxlength="240" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true"/>
						<dspel:tagAttribute name="regExp" value="^[a-zA-Z \.]+$"/>
						<dspel:tagAttribute name="trim" value="true"/>
						<dspel:tagAttribute name="invalidMessage" value="${invalidOrgName}"/>
					</dspel:input>
	        	</div>
			</div>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Address
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_address1" name="${formId}_address1" type="text" bean="${exemptionMap}.address1" maxlength="50" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true"/>
						<dspel:tagAttribute name="regExp" value="^[0-9a-zA-Z., ]+$"/>
						<dspel:tagAttribute name="trim" value="true"/>
						<dspel:tagAttribute name="invalidMessage" value="${address1Missing}"/>
					</dspel:input>
	        	</div>
			</div>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label>
						Address 2 (optional)
					</label>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_address2" name="${formId}_address2" type="text" bean="${exemptionMap}.address2" maxlength="50" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="false"/>
						<dspel:tagAttribute name="regExp" value="^[0-9a-zA-Z., ]+$"/>
						<dspel:tagAttribute name="trim" value="true"/>
						<dspel:tagAttribute name="invalidMessage" value="${address1Missing}"/>
					</dspel:input>
	        	</div>
			</div>
			
			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						City
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_city" name="${formId}_city" type="text" bean="${exemptionMap}.city" maxlength="30" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true"/>
	            		<dspel:tagAttribute name="trim" value="true"/>
						<dspel:tagAttribute name="regExp" value="^[a-zA-Z \.]+$"/>
						<dspel:tagAttribute name="invalidMessage" value="${cityMissing}"/>
					</dspel:input>
	        	</div>
			</div>
			
			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						State
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
		          <dspel:input id="${formId}_state" iclass="atg-base-table-customer-address-add-form-input-small-dojo" bean="${exemptionMap}.state">
		            <dspel:tagAttribute name="dojoType" value="atg.widget.form.FilteringSelect" />
		            <dspel:tagAttribute name="autoComplete" value="true" />
		            <dspel:tagAttribute name="searchAttr" value="name" />
		            <dspel:tagAttribute name="store" value="stateStore" />
		            <dspel:tagAttribute name="invalidMessage" value="${invalidState}" />
		          </dspel:input>
		          <div dojoType="dojo.data.ItemFileReadStore" jsId="stateStore"
		            url="${customerPanelConfig.stateDataUrl}?${stateHolder.windowIdParameterName}=${windowId}&countryCode=${countryCode}"></div>
		        </div >
			</div>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Zip Code
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_postalCode" name="${formId}_postalCode" type="text" bean="${exemptionMap}.postalCode" maxlength="10">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true"/>
	            		<dspel:tagAttribute name="trim" value="true"/>
						<dspel:tagAttribute name="regExp" value="^\\d{5}(-\\d{4})?$"/>
						<dspel:tagAttribute name="invalidMessage" value="${zipMissing}"/>
					</dspel:input>
	        	</div>
			</div>

			<dspel:input id="${formId}_country" name="${formId}_country" type="hidden" bean="${exemptionMap}.country" value="US"/>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Business Description
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_description" name="${formId}_description" type="text" bean="${exemptionMap}.businessDesc" maxlength="240" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true"/>
	            		<dspel:tagAttribute name="trim" value="true"/>
						<dspel:tagAttribute name="regExp" value="^[a-zA-Z \.]+$"/>
						<dspel:tagAttribute name="invalidMessage" value="${buzDescMissing}"/>
					</dspel:input>
	        	</div>
			</div>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Merchandise to be Purchased
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_merchandise" name="${formId}_merchandise" type="text" bean="${exemptionMap}.merchandise" maxlength="240" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true"/>
	            		<dspel:tagAttribute name="trim" value="true"/>
						<dspel:tagAttribute name="regExp" value="^[a-zA-Z \.]+$"/>
						<dspel:tagAttribute name="invalidMessage" value="${merchPurMissing}"/>
					</dspel:input>
	        	</div>
			</div>
			
			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Tax Jurisdiction City
					</label>
					<span class="requiredStar">*</span>
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input id="${formId}_tax-city" name="${formId}_tax-city" type="text" bean="${exemptionMap}.taxCity" maxlength="30" iclass="atg-base-table-customer-address-add-form-input-dojo">
						<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true"/>
	            		<dspel:tagAttribute name="trim" value="true"/>
						<dspel:tagAttribute name="regExp" value="^[a-zA-Z \.]+$"/>
						<dspel:tagAttribute name="invalidMessage" value="${cityMissing}"/>
					</dspel:input>
	        	</div>
			</div>
			
			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Tax Jurisdiction State
					</label>
					<span class="requiredStar">*</span>
				</span>
        		<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
		          <dspel:input id="${formId}_taxState" iclass="atg-base-table-customer-address-add-form-input-small-dojo" bean="${exemptionMap}.taxState">
		            <dspel:tagAttribute name="dojoType" value="atg.widget.form.FilteringSelect" />
		            <dspel:tagAttribute name="autoComplete" value="true" />
		            <dspel:tagAttribute name="searchAttr" value="name" />
		            <dspel:tagAttribute name="store" value="stateStore" />
		            <dspel:tagAttribute name="invalidMessage" value="${invalidState}" />
		          </dspel:input>
		          <div dojoType="dojo.data.ItemFileReadStore" jsId="stateStore"
		            url="${customerPanelConfig.stateDataUrl}?${stateHolder.windowIdParameterName}=${windowId}&countryCode=${countryCode}"></div>
		        </div >
			</div>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
				</span>
				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
					<dspel:input type="checkbox" id="${formId}_agree-to-terms" bean="CustomerProfileFormHandler.taxExemptionAgreed">
	            		<dspel:tagAttribute name="required" value="true"/>
						<dspel:tagAttribute name="invalidMessage" value="${termsMissing}"/>
					</dspel:input> I agree to the <a href="#terms">Online Store Terms Of Use</a>
	        	</div>
			</div>

        </div>

        <div class="atg_svc_formActions">
          <dspel:input type="hidden" value="${successUrl}" 
            bean="CustomerProfileFormHandler.exemptionSuccessUrl"/>
          <dspel:input type="hidden" value="${url}" 
            bean="CustomerProfileFormHandler.exemptionErrorUrl"/>
          <dspel:input type="hidden" value="--" priority="-100"
            id="addExemptionAction"
            bean="CustomerProfileFormHandler.addTaxExemption"/>
          <div>
            <input id="saveChoice" name="saveChoice" value="<fmt:message key='address.save.label'/>" type="button"
                   onclick="atgSubmitPopup({url: '${url}', 
                              form: dojo.byId('${formId}'),
                              popup: getEnclosingPopup('${formId}_nickname')});
                            return false;"/>
            <input value="<fmt:message key='address.cancel.label'/>" 
              type="button" id="cancelChoice"
              onClick="hidePopupWithResults('exemptionPopup', {result:'cancel'}); 
              return false;"/>
          </div>
        </div>

      </dspel:form>
    </div>
    <c:if test="${param.success}">
      <script type="text/javascript">
        hidePopupWithResults('exemptionPopup', {result : 'save'});
      </script>
    </c:if>
    
    <script type="text/javascript">
      var ${formId}Validate = function () {
        var disable = false;
        if (!dijit.byId("${formId}_nickname").isValid()) disable = true;
        if (!dijit.byId("${formId}_tax-id").isValid()) disable = true;
        if (!dijit.byId("${formId}_firstName").isValid()) disable = true;
        if (!dijit.byId("${formId}_lastName").isValid()) disable = true;
        if (!dijit.byId("${formId}_orgName").isValid()) disable = true;
        if (!dijit.byId("${formId}_address1").isValid()) disable = true;
        if (!dijit.byId("${formId}_city").isValid()) disable = true;
        if (!dijit.byId("${formId}_state").isValid()) disable = true;
        if (!dijit.byId("${formId}_postalCode").isValid()) disable = true;
        if (!dijit.byId("${formId}_description").isValid()) disable = true;
        if (!dijit.byId("${formId}_merchandise").isValid()) disable = true;
        if (!dijit.byId("${formId}_tax-city").isValid()) disable = true;
        if (!dijit.byId("${formId}_taxState").isValid()) disable = true;

        document.getElementById("saveChoice").disabled=disable;
      };
      _container_.onLoadDeferred.addCallback(function () {
		atg.service.form.watchInputs("${formId}", ${formId}Validate);
        document.getElementById("saveChoice").disabled=true;
      });
	  _container_.onUnloadDeferred.addCallback(function () {
		atg.service.form.unWatchInputs('${formId}');
	  });
    </script>
    
   
  </dspel:layeredBundle>
</dspel:page>
