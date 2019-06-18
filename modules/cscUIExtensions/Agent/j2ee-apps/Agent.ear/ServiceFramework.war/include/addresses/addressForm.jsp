<%--
 This page defines the address form
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/addresses/addressForm.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dspel:page xml="true">
  	<dspel:getvalueof var="formHandler" param="formHandler"/>
    <dspel:getvalueof var="formId" param="formId"/>
    <dspel:getvalueof var="addressBean" param="addressBean"/>
    <dspel:getvalueof var="submitButtonId" param="submitButtonId"/>
    <dspel:getvalueof var="isDisableSubmit" param="isDisableSubmit"/>
    <dspel:getvalueof var="validateIf" param="validateIf"/>
    <dspel:importbean bean="/atg/svc/agent/customer/CustomerPanelConfig" var="customerPanelConfig"/>
    <dspel:importbean bean="/com/mff/commerce/order/PrevAddressRequest" var="prevRequest"/>
     
    <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
	<c:if test="${prevRequest.suggestedAddress != null }"> 
		<dspel:input id="${formId}_suggestedAddress" bean="${formHandler}.selectSuggestedAddress" value="yes" type="radio" /> 
		<c:out value="${prevRequest.suggestedAddress.address1}"/>
		<c:out value="${prevRequest.suggestedAddress.city}"/>
		<c:out value="${prevRequest.suggestedAddress.state}"/>
		<c:out value="${prevRequest.suggestedAddress.postalCode}"/>
		<c:out value="${prevRequest.suggestedAddress.country}"/><br/>
		
		<dspel:input id="${formId}_suggestedAddress" bean="${formHandler}.selectSuggestedAddress" value="no" type="radio" /> Use the address below:
	</c:if>
      <fmt:message var="firstNameMissing" key="firstNameMissing" />
      <fmt:message var="lastNameMissing" key="lastNameMissing" />
      <fmt:message var="address1Invalid" key="address1Missing.addressForm.custom"/>
      <fmt:message var="address1Missing" key="address1Missing"/>
      <fmt:message var="address2Invalid" key="address2Invalid.addressForm.custom"/>
      <fmt:message var="cityMissing" key="cityMissing" />
      <fmt:message var="invalidState" key="invalidState" />
      <fmt:message var="invalidCountry" key="invalidCountry" />
      <fmt:message var="postalCodeMissing" key="postalCodeMissing" />
	  <fmt:message var="phoneNumberMissing" key="phoneNumberMissing" />

      <div class="atg_commerce_csr_firstName atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label class="atg_messaging_requiredIndicator">
            <fmt:message key="newAddress.firstName" />
          </label>
          <span class="requiredStar">*</span>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
          <dspel:getvalueof var="firstName" bean="/atg/userprofiling/ActiveCustomerProfile.firstName"/>
          <dspel:getvalueof var="currentFirstName" bean="${addressBean}.firstName"/>
          <dspel:getvalueof var="currentLastName" bean="${addressBean}.lastName"/>
          <dspel:getvalueof var="currentMiddleName" bean="${addressBean}.middleName"/>

          <dspel:input id="${formId}_firstName" iclass=" atg-base-table-customer-address-add-form-input-dojo" type="text" bean="${addressBean}.firstName" size="25" maxlength="40" value="${empty currentFirstName? firstName : currentFirstName}">
            <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
            <dspel:tagAttribute name="required" value="true" />
            <dspel:tagAttribute name="trim" value="true" />
            <dspel:tagAttribute name="promptMessage" value="${firstNameMissing}"/>
          </dspel:input>
       </div >
      </div>

      <div class="atg_commerce_csr_middleName atg_inlineLabel atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label>
            <fmt:message key="newAddress.middleName" />
          </label>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
          <dspel:getvalueof var="middleName" bean="/atg/userprofiling/ActiveCustomerProfile.middleName"/>
          <dspel:input id="${formId}_middleName" iclass="atg-base-table-customer-address-add-form-input" type="text" bean="${addressBean}.middleName" size="25" maxlength="40" value="${empty currentMiddleName ? middleName : currentMiddleName}">
          </dspel:input>
        </div>
      </div>

      <div class="atg_commerce_csr_lastName atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label class="atg_messaging_requiredIndicator">
            <fmt:message key="newAddress.lastName" />
          </label>
          <span class="requiredStar">*</span>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
          <dspel:getvalueof var="lastName" bean="/atg/userprofiling/ActiveCustomerProfile.lastName"/>
          <dspel:input id="${formId}_lastName" type="text" iclass="atg-base-table-customer-address-add-form-input-dojo" bean="${addressBean}.lastName" size="25" maxlength="40" value="${empty currentLastName ? lastName : currentLastName}" >
            <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
            <dspel:tagAttribute name="required" value="true" />
            <dspel:tagAttribute name="trim" value="true" />
            <dspel:tagAttribute name="promptMessage" value="${lastNameMissing}"/>
          </dspel:input>
        </div>
      </div>

	<div dojoType="dojo.data.ItemFileReadStore" jsId="countryStore"
	url="${customerPanelConfig.countryDataUrl}?${stateHolder.windowIdParameterName}=${windowId}"></div>
      <div class="atg_commerce_csr_country atg-csc-base-table-row">
		<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label class="atg_messaging_requiredIndicator">
            <fmt:message key="newAddress.country" />
          </label>
          <span class="requiredStar">*</span>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
          <fmt:message key="newAddress.defaultCountryCode" var="defaultCountryCode" />
          <dspel:getvalueof var="currentCountryCode" bean="${addressBean}.country"/>
          <dspel:input id="${formId}_country" iclass="atg-base-table-customer-address-add-form-input-big-dojo" bean="${addressBean}.country"
            onchange="${formId}ChangeCountry();" value="${empty currentCountryCode ? defaultCountryCode : currentCountryCode}">
            <dspel:tagAttribute name="dojoType" value="atg.widget.form.FilteringSelect" />
            <dspel:tagAttribute name="autoComplete" value="true" />
            <dspel:tagAttribute name="searchAttr" value="name" />
            <dspel:tagAttribute name="store" value="countryStore" />
            <dspel:tagAttribute name="invalidMessage" value="${invalidCountry}" />
          </dspel:input>
          </div>
	  </div>

      <div class="atg_commerce_csr_address atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label class="atg_messaging_requiredIndicator">
            <fmt:message key="newAddress.address1" />
          </label>
          <span class="requiredStar">*</span>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
        <c:choose>
			<c:when test="${not empty formId && (formId == 'editCreditCardForm' || formId == 'csrBillingAddCreditCard')}">
				<dspel:input id="${formId}_address1" iclass="atg-base-table-customer-address-add-form-input-big-dojo" type="text" bean="${addressBean}.address1" size="25" maxlength="30">
            		<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
            		<dspel:tagAttribute name="required" value="true" />
            		<dspel:tagAttribute name="trim" value="true" />
					<dspel:tagAttribute name="invalidMessage" value="${address1Missing}"/>
				</dspel:input>
			</c:when>
	  		<c:otherwise>
				<dspel:input id="${formId}_address1" iclass="atg-base-table-customer-address-add-form-input-big-dojo" type="text" bean="${addressBean}.address1" size="25" maxlength="30">
            		<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
            		<dspel:tagAttribute name="required" value="true" />
            		<dspel:tagAttribute name="trim" value="true" />
					<dspel:tagAttribute name="regExp" value="^(?!.*([P|p](OST|ost)*.*s*[O|o|0](ffice|FFICE)*.*s*[B|b][O|o|0][X|x]s*(\d.)*)).*$"/>
            		<dspel:tagAttribute name="invalidMessage" value="${address1Invalid}"/>
				</dspel:input>
	  		</c:otherwise>
	  	</c:choose>
        </div>
      </div>

      <div class="atg_commerce_csr_address atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label>
            <fmt:message key="newAddress.address2" />
          </label>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
        <c:choose>
			<c:when test="${not empty formId && (formId == 'editCreditCardForm' || formId == 'csrBillingAddCreditCard')}">
	          <dspel:input type="text" iclass="atg-base-table-customer-address-add-form-input-big" bean="${addressBean}.address2" size="25" maxlength="30">
				<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            <dspel:tagAttribute name="required" value="false" />
	            <dspel:tagAttribute name="trim" value="true" />
			  </dspel:input>
			</c:when>
	  		<c:otherwise>
	          <dspel:input type="text" iclass="atg-base-table-customer-address-add-form-input-big" bean="${addressBean}.address2" size="25" maxlength="30">
				<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            <dspel:tagAttribute name="required" value="false" />
	            <dspel:tagAttribute name="trim" value="true" />
	          	<dspel:tagAttribute name="regExp" value="^(?!.*([P|p](OST|ost)*.*s*[O|o|0](ffice|FFICE)*.*s*[B|b][O|o|0][X|x]s*(\d.)*)).*$"/>
	          	<dspel:tagAttribute name="invalidMessage" value="${address2Invalid}"/>
			  </dspel:input>
	  		</c:otherwise>
	  	</c:choose>
        </div >
      </div>

      <div class="atg_commerce_csr_city atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label class="atg_messaging_requiredIndicator">
            <fmt:message key="newAddress.city" />
          </label>
          <span class="requiredStar">*</span>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
          <dspel:input id="${formId}_city" iclass="atg-base-table-customer-address-add-form-input-dojo" type="text" bean="${addressBean}.city" size="25" maxlength="30">
            <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
            <dspel:tagAttribute name="required" value="true" />
            <dspel:tagAttribute name="trim" value="true" />
            <dspel:tagAttribute name="promptMessage" value="${cityMissing}"/>
          </dspel:input>
        </div >
      </div>

      <div class="atg_commerce_csr_state atg-csc-base-table-row">
        <dspel:getvalueof var="countryCode" bean="${addressBean}.country" />
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label class="atg_messaging_requiredIndicator">
            <fmt:message key="newAddress.state" />
          </label>
          <span class="requiredStar">*</span>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
          <dspel:input id="${formId}_state" iclass="atg-base-table-customer-address-add-form-input-small-dojo" bean="${addressBean}.state">
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

      <div class="atg_commerce_csr_zipcode atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label class="atg_messaging_requiredIndicator">
            <fmt:message key="newAddress.postalCode" />
          </label>
          <span class="requiredStar">*</span>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
          <dspel:input id="${formId}_postalCode" iclass="atg-base-table-customer-address-add-form-input-small-dojo" type="text" bean="${addressBean}.postalCode" size="10" maxlength="10">
            <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
            <dspel:tagAttribute name="required" value="true" />
            <dspel:tagAttribute name="regExp" value="^\d{5}(?:[-\s]\d{4})?$"/>
            <dspel:tagAttribute name="promptMessage" value="${postalCodeMissing}"/>
          </dspel:input>
        </div>
      </div>

      <div class="atg_commerce_csr_phone atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label class="atg_messaging_requiredIndicator">
            <fmt:message key="newAddress.phoneNumber" />
          </label>
          <span class="requiredStar">*</span>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
          <dspel:input id="${formId}_phoneNumber" iclass="atg-base-table-customer-address-add-form-input" type="text" bean="${addressBean}.phoneNumber" size="25" maxlength="15">
          	<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
            <dspel:tagAttribute name="required" value="true" />
			<dspel:tagAttribute name="regExp" value="^\d{10}$"/>
			<dspel:tagAttribute name="invalidMessage" value="Phone missing or invalid. Requires 10 digits."/>
            <dspel:tagAttribute name="promptMessage" value="${phoneNumberMissing}"/>
          </dspel:input>
        </div>
      </div>
	  
	  	
      <script type="text/javascript">
      
      var formId="${formId}";
      var addressType="${prevRequest.addressType}";
      
      // load previous values only if the form is shipping or billing address
      if(formId != null && addressType != null)
    	 if((formId == "csrAddShippingAddress" && addressType == "shippingAddress") || (formId == "csrBillingAddCreditCard" && addressType == "billingAddress")){
    		 loadPrevValues(); 
    	 }
      
      function loadPrevValues(){
    	  
    	  //check if first name from prev request available
    	  // yes - set the value from prev request
    	  //no - if first name is available from Profile set the value
    	  var firstName="${prevRequest.previousAddress.firstName}";
    	  if(firstName != null && firstName != "")
    		  dojo.byId("${formId}_firstName").setAttribute('value',firstName);
    	  else{
    		  var currentFirstName="${currentFirstName}";
        	  if(currentFirstName != null && currentFirstName != "")
        		  dojo.byId("${formId}_firstName").setAttribute('value',currentFirstName);
    	  }
    	  
    	  var lastName="${prevRequest.previousAddress.lastName}";
    	  if(lastName != null && lastName != "")
    		  dojo.byId("${formId}_lastName").setAttribute('value',lastName);
    	  else{
    		  var currentLastName="${currentLastName}";
        	  if(currentLastName != null && currentLastName != "")
        		  dojo.byId("${formId}_lastName").setAttribute('value',currentLastName);
    	  }
    	  
    	  var middleName="${prevRequest.previousAddress.middleName}";
    	  if(middleName != null && middleName != "")
    		  dojo.byId("${formId}_middleName").setAttribute('value',middleName);
    	  else{
    		  var currentMiddleName="${currentMiddleName}";
        	  if(currentMiddleName != null && currentMiddleName != "")
        		  dojo.byId("${formId}_middleName").setAttribute('value',currentMiddleName);
    	  }
    	  
    	  
    	  //address1
    	  var address1="${prevRequest.previousAddress.address1}";
    	  if(address1 != null && address1 != "")
    		  dojo.byId("${formId}_address1").setAttribute('value',address1);
    	  
    	  //address2
    	  var address2="${prevRequest.previousAddress.address2}";
    	  if(address2 != null && address2 != "")
    		  dojo.byId("${formId}_address2").setAttribute('value',address2);
    	  
    	  //country
    	  var country="${prevRequest.previousAddress.country}";
    	  if(country != null && country != "")
    		  dojo.byId("${formId}_country").setAttribute('value',country);
    	  else{
    		  var currentCountryCode= "${currentCountryCode}";
    		  var defaultCountryCode="${defaultCountryCode}";
    		  
    		  if(currentCountryCode !=null && currentCountryCode != ""){
    			dojo.byId("${formId}_country").setAttribute('value',currentCountryCode);
    		  }
    		  else{
    			dojo.byId("${formId}_country").setAttribute('value',defaultCountryCode);
    		  }
    	  }
    	  
    	  //city
    	  var city="${prevRequest.previousAddress.city}";
    	  if(city != null && city != "")
    		  dojo.byId("${formId}_city").setAttribute('value',city);
    	  
    	//state
    	  var state="${prevRequest.previousAddress.state}";
    	  if(state != null && state != "")
    		  dojo.byId("${formId}_state").setAttribute('value',state);
    	  
    	  //postal code
    	  var postalCode="${prevRequest.previousAddress.postalCode}";
    	  if(postalCode != null && postalCode != "")
    		  dojo.byId("${formId}_postalCode").setAttribute('value',postalCode);
    	  
    	  //phone number
    	  var phoneNumber="${prevRequest.phoneNumber}";
    	  if(phoneNumber != null && phoneNumber != "")
    		  dojo.byId("${formId}_phoneNumber").setAttribute('value',phoneNumber);
    	  
        }
      
        var ${formId}ChangeCountry = function () {
          if (stateStore) {
            var states = dijit.byId("${formId}_state");
            var countries = dijit.byId("${formId}_country");
            var stateUrl = "${customerPanelConfig.stateDataUrl}?${stateHolder.windowIdParameterName}=${windowId}&countryCode=";
            stateUrl += countries.getValue() || countries.value;
            stateStore = new dojo.data.ItemFileReadStore({url:stateUrl});
            states.store = stateStore;
            states.setValue(states.value);
            if (!states.value) {
              states.setDisplayedValue("")
              states.valueNode.value = "";
            }
            states.value = "";
          }
        };
        var ${formId}Validate = function () {
          var disable = false;
          <c:if test="${!empty isDisableSubmit}">disable = ${isDisableSubmit}();</c:if>
          <c:if test="${!empty validateIf}">if (${validateIf}) {</c:if>
            if (!dijit.byId("${formId}_firstName").isValid()) disable = true;
            if (!dijit.byId("${formId}_lastName").isValid()) disable = true;
            if (!dijit.byId("${formId}_address1").isValid()) disable = true;
            if (!dijit.byId("${formId}_city").isValid()) disable = true;
            if (!dijit.byId("${formId}_state").isValid()) disable = true;
            if (!dijit.byId("${formId}_postalCode").isValid()) disable = true;
            if (!dijit.byId("${formId}_country").isValid()) disable = true;
			if (!dijit.byId("${formId}_phoneNumber").isValid()) disable = true;
          <c:if test="${!empty validateIf}">}</c:if>
          dojo.byId("${formId}").${submitButtonId}.disabled = disable;
        };
        _container_.onLoadDeferred.addCallback(function () {
          ${formId}Validate();
          ${formId}ChangeCountry();
          atg.service.form.watchInputs("${formId}", ${formId}Validate);
        });
        _container_.onUnloadDeferred.addCallback(function () {
          atg.service.form.unWatchInputs('${formId}');
        });
      </script>
    </dspel:layeredBundle>
  </dspel:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/addresses/addressForm.jsp#1 $$Change: 946917 $--%>
