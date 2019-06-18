<%--
 This page defines the credit card form using the following input parameters:

 formId - the DOM ID of the form node to submit to add or update credit cards
 creditCardBean - the object that has credit card type, number and expiration date
 creditCardAddressBean - the object that allows the option of adding a new address to the credit card
 creditCardFormHandler - the full path to the form handler to add or update the credit card
 submitButtonId - the DOM ID of the submit button so that the button can be enabled or
   disabled based on the validity of the form inputs
 isMaskCardNumber - edit credit card hides the number with a mask exposing only the last
   four digits
 isUseExistingAddress - Some of the form handlers have the opposite logic from each other:
   some use an isUseExistingAddress property whose state must be false to create new addresses,
   others use the opposite isCreateNewAddress property whose state must be true to create new addresses.
   Note: this is not a way to turn off the address or new address area of the credit card form.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/creditCardForm.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean var="addressForm" bean="/atg/svc/agent/ui/fragments/AddressForm"/>
    <dsp:getvalueof var="formId" param="formId"/>
    <dsp:getvalueof var="creditCardBean" param="creditCardBean"/>
    <dsp:getvalueof var="creditCardAddressBean" param="creditCardAddressBean"/>
    <dsp:getvalueof var="creditCardFormHandler" param="creditCardFormHandler"/>
    <dsp:getvalueof var="submitButtonId" param="submitButtonId"/>
    <dsp:getvalueof var="isMaskCardNumber" param="isMaskCardNumber"/>
    <dsp:getvalueof var="isUseExistingAddress" param="isUseExistingAddress"/>
    <dsp:getvalueof var="disableCreditCardType" param="disableCreditCardType"/>
    <dsp:getvalueof var="disableCreditCardNumber" param="disableCreditCardNumber"/>
        
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

      <fmt:message var="emptyAddressSelection" key="emptyAddressSelection" />
      <fmt:message var="invalidCreditCardType" key="invalidCreditCardType" />
      <fmt:message var="invalidCreditCardNumber" key="invalidCreditCardNumber" />
      <fmt:message var="creditCardNumberMissing" key="creditCardNumberMissing" />
      <fmt:message var="invalidCreditCardDate" key="invalidCreditCardDate" />
      <fmt:message var="invalidExistingAddress" key="invalidExistingAddress" />
      
      <c:if test="${disableCreditCardType}">
        <script type="text/javascript">
          _container_.onLoadDeferred.addCallback(function(){
               var ccType = dijit.byId('${formId}_creditCardType');
               if (ccType) ccType.setDisabled(true);
          });
        </script>
      </c:if>

      <c:if test="${disableCreditCardNumber}">
        <script type="text/javascript">
          _container_.onLoadDeferred.addCallback(function(){
            atg.commerce.csr.common.disableTextboxWidget('${formId}_maskedCreditCardNumber');
            atg.commerce.csr.common.disableTextboxWidget('${formId}_creditCardNumber');
          });
        </script>
      </c:if>

      <div dojoType="dojo.data.ItemFileReadStore" jsId="cardStore" url="${CSRConfigurator.contextRoot}/include/cardData.jsp?${stateHolder.windowIdParameterName}=${windowId}"></div>
      <div dojoType="dojo.data.ItemFileReadStore" jsId="monthStore" url="${CSRConfigurator.contextRoot}/include/monthData.jsp?${stateHolder.windowIdParameterName}=${windowId}"></div>
      <div dojoType="dojo.data.ItemFileReadStore" jsId="yearStore" url="${CSRConfigurator.contextRoot}/include/yearData.jsp?${stateHolder.windowIdParameterName}=${windowId}"></div>

      <div class="atg-csc-base-table">
        <div class="atg_commerce_csr_cardType atg-csc-base-table-row">
          <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
            <label class="atg_messaging_requiredIndicator ">
              <fmt:message key="newOrderBilling.addEditCreditCard.header.card.title"/>
            </label>
            <span class="requiredStar">*</span>
          </span>
          <div class="atg-csc-base-table-cell">
            <dsp:input id="${formId}_creditCardType" iclass="" bean="${creditCardBean}.creditCardType"
              required="<%=true%>" style="width:126px !important">
              <dsp:tagAttribute name="dojoType" value="atg.widget.form.FilteringSelect" />
              <dsp:tagAttribute name="autoComplete" value="true" />
              <dsp:tagAttribute name="searchAttr" value="name" />
              <dsp:tagAttribute name="store" value="cardStore" />
              <dsp:tagAttribute name="invalidMessage" value="${invalidCreditCardType }"/>
            </dsp:input>
          </div>
        </div>

        <div class="atg_commerce_csr_cardNumber atg-csc-base-table-row" >
          <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
            <label class="atg_messaging_requiredIndicator" >
              <fmt:message key="newOrderBilling.addEditCreditCard.field.cardNumber"/>
            </label>
            <span class="requiredStar">*</span>
          </span>
          <div class="atg-csc-base-table-cell">
            <c:choose>
              <c:when test="${isMaskCardNumber}">

                <%-- Bugs-fixed: 144174-1 The following snippet creates a parameter and the parameter
                     has the masked credit card number and the masked credit card string is passed to the
                     credit card validity routine. --%>


                <dsp:param name="maskedCreditCardNumber" bean="${creditCardBean}.creditCardNumber"
                  converter="creditCard" maskcharacter="*" numcharsunmasked="4"/>
                <dsp:getvalueof var="maskedCreditCardNumberVar" param="maskedCreditCardNumber"/>

                <dsp:input type="text" id="${formId}_maskedCreditCardNumber" iclass="atg-base-table-customer-credit-card-add-number"
                  bean="${creditCardBean}.creditCardNumber"
                  required="<%=true%>" size="25" maxlength="25"
                  converter="creditCard" maskcharacter="*" numcharsunmasked="4">
                  <dsp:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
                  <dsp:tagAttribute name="required" value="true" />
                  <dsp:tagAttribute name="trim" value="true" />
                  <dsp:tagAttribute name="promptMessage" value="${creditCardNumberMissing }" />
                  <dsp:tagAttribute name="invalidMessage" value="${invalidCreditCardNumber }"/>
                  <dsp:tagAttribute name="autocomplete" value="off" />
                </dsp:input>
                <dsp:droplet  name="/atg/commerce/custsvc/events/ViewCreditCardEventDroplet">
                  <dsp:param name="profile" bean="/atg/userprofiling/ActiveCustomerProfile" />
                  <dsp:param name="creditCardNumber" bean="${creditCardBean}.creditCardNumber" />
                </dsp:droplet>

              </c:when>
              <c:otherwise>
                <dsp:input type="text" id="${formId}_creditCardNumber" iclass="atg-base-table-customer-credit-card-add-number" bean="${creditCardBean}.creditCardNumber"
                  required="<%=true%>" size="25" maxlength="25">
                  <dsp:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
                  <dsp:tagAttribute name="required" value="true" />
                  <dsp:tagAttribute name="trim" value="true" />
                  <dsp:tagAttribute name="promptMessage" value="${creditCardNumberMissing }" />
                  <dsp:tagAttribute name="invalidMessage" value="${invalidCreditCardNumber }"/>
                  <dsp:tagAttribute name="autocomplete" value="off" />
                </dsp:input>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <div class="atg_commerce_csr_cardNumber atg-csc-base-table-row" >
          <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
            <label class="atg_messaging_requiredIndicator" >
            	<fmt:message key="newOrderBilling.addEditCreditCard.field.nameOnCard"/>
            </label>
            <span class="requiredStar">*</span>
          </span>
          <div class="atg-csc-base-table-cell">
               <dsp:input type="text" id="${formId}_nameOnCard" iclass="atg-base-table-customer-credit-card-add-number" bean="${creditCardBean}.nameOnCard"
                  required="<%=true%>" size="25" maxlength="25">
                  <dsp:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
                  <dsp:tagAttribute name="required" value="true" />
                  <dsp:tagAttribute name="trim" value="true" />
                  <dsp:tagAttribute name="autocomplete" value="off" />
                </dsp:input>
          </div>
        </div>

      <div class="atg_commerce_csr_expirationDate atg-csc-base-table-row atg-base-table-customer-credit-card-line">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label" style="height:0px">
          <label class="atg_messaging_requiredIndicator">
            <fmt:message key="newOrderBilling.addEditCreditCard.field.expDate"/>
          </label>
          <span class="requiredStar">*</span>
        </span>
        <div class="atg-csc-base-table-cell">
          <dsp:input id="${formId}_expirationMonth" iclass="" bean="${creditCardBean}.expirationMonth"
            required="<%=true%>" style="width:120px;">
            <dsp:tagAttribute name="dojoType" value="atg.widget.form.FilteringSelect" />
            <dsp:tagAttribute name="autoComplete" value="true" />
            <dsp:tagAttribute name="searchAttr" value="name" />
            <dsp:tagAttribute name="store" value="monthStore" />
            <dsp:tagAttribute name="invalidMessage" value="${invalidCreditCardDate }" />
          </dsp:input>
        </div>
        <div class="atg-csc-base-table-cell">
          <dsp:input id="${formId}_expirationYear" iclass="" bean="${creditCardBean}.expirationYear"
            required="<%=true%>" style="width:85px;">
            <dsp:tagAttribute name="dojoType" value="atg.widget.form.FilteringSelect" />
            <dsp:tagAttribute name="autoComplete" value="true" />
            <dsp:tagAttribute name="searchAttr" value="name" />
            <dsp:tagAttribute name="store" value="yearStore" />
            <dsp:tagAttribute name="invalidMessage" value="${invalidCreditCardDate }" />
          </dsp:input>
        </div>
      </div>

      <div class="atg_commerce_csr_existingBilling atg-csc-base-table-row">
          <c:choose>
            <c:when test="${isUseExistingAddress}">
              <dsp:getvalueof var="existingAddressList" bean="${creditCardFormHandler}.existingAddresses" />
              <dsp:getvalueof var="useExistingAddress" bean="${creditCardFormHandler}.useExistingAddress" />
              <%-- When there is no existing addresses, do not display the radio buttons. Instead just
              display the address form. --%>

              <c:choose>
                <c:when test="${fn:length(existingAddressList) == 0}">
                  <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label atg-base-table-customer-credit-card-spacing-two-top">
                    <label class="atg_messaging_requiredIndicator atg_commerce_csr_billingAddress" >
                      <fmt:message key="newOrderBilling.addEditCreditCard.header.billingAddress.title"/>
                    </label>
                  </span>
                  
                   <script type="text/javascript" charset="utf-8">
                    _container_.onLoadDeferred.addCallback(function () {
                    atg.commerce.csr.${formId}existingBillingAddressPicker();
                  });
                  </script>
                </c:when>
                <c:otherwise>
                  <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label atg-base-table-customer-credit-card-spacing-two-top">
                    <label class="atg_messaging_requiredIndicator atg_commerce_csr_billingAddress" >
                      <fmt:message key="newOrderBilling.addEditCreditCard.header.billingAddress.title"/>
                    </label><span class="requiredStar">*</span>
                  </span>
                  
                  <div class="atg-csc-base-table-cell">
                      
                        <dsp:input id="${formId}_existingAddress" type="radio" checked="${useExistingAddress ? true : false}"
                          bean="${creditCardFormHandler}.useExistingAddress"
                          value="true" onclick="${formId}HideAddressArea();"/>
                        <span><fmt:message key="newOrderBilling.addEditCreditCard.field.billingAddress.useExistingAddress"/></span>
                        </div>
                        <div class="atg-csc-base-table-cell">
                          <dsp:select id="${formId}_existingAddressList" iclass="" bean="${creditCardFormHandler}.addressIndex">
                            <dsp:droplet name="ForEach">
                              <dsp:param name="array" bean="${creditCardFormHandler}.existingAddresses" />
                              <dsp:oparam name="output">
                                <dsp:getvalueof var="address" param="element"/>
                                <dsp:getvalueof var="addressIndex" param="index"/>
                                <dsp:option value="${addressIndex}">
                                  ${fn:escapeXml(address.address1)}${!empty address.address2 ? ' ' : '' }${!empty address.address2 ? fn:escapeXml(address.address2) : '' }
                                </dsp:option>
                              </dsp:oparam>
                            </dsp:droplet>
                          </dsp:select>
                        </div>
                      
                  
                </c:otherwise>
              </c:choose>
              </div>
              <div class="atg-csc-base-table-row">
                <div class="atg-csc-base-table-cell"></div>
                <div class="atg_commerce_csr_createNewAddress atg-csc-base-table-cell">
                  <script type="text/javascript" charset="utf-8">
                      atg.commerce.csr.${formId}existingBillingAddressPicker = function(){
                        var existingAddressList = dojo.byId('${formId}_existingAddressList');
                        if(existingAddressList){
                          existingAddressList.disabled = true;
                        }
                        dojo.style(dojo.byId('${formId}_addressArea'),'display','block');
                        if(dojo.byId('editPaymentOptionFloatingPane').style.display == 'block'){
                          dijit.byId('editPaymentOptionFloatingPane').layout();
                        }
                      };
                  </script>
                  <div>
                    <c:choose>
                      <c:when test="${fn:length(existingAddressList) >0}">
                        <dsp:input type="radio" checked="${!useExistingAddress ? true : false}"
                          bean="${creditCardFormHandler}.useExistingAddress"
                          value="false" id="${formId}_existingAddress"
                          onclick="atg.commerce.csr.${formId}existingBillingAddressPicker();"/>
                        <fmt:message key="newOrderBilling.addEditCreditCard.field.billingAddress.createNewAddress"/>
                      </c:when>
                      <c:otherwise>
                        <dsp:input type="hidden" bean="${creditCardFormHandler}.useExistingAddress"
                          value="false" id="${formId}_existingAddress"/>
                      </c:otherwise>
                    </c:choose>
                  </div>

                </div>
              </div>
                  
                  </div>
                  <div id="${formId}_addressArea" class="atg-base-table-customer-credit-card-address-form" style="display:none;">
                    <div class="atg-csc-base-table atg-base-table-customer-address-add-form">
                      <dsp:include src="${addressForm.URL}" otherContext="${addressForm.servletContext}">
                        <dsp:param name="formId" value="${formId}"/>
                        <dsp:param name="addressBean" value="${creditCardAddressBean}"/>
                        <dsp:param name="submitButtonId" value="${submitButtonId}"/>
                        <dsp:param name="isDisableSubmit" value="${formId}DisableSubmit"/>
                        <c:choose>
                        <c:when test="${fn:length(existingAddressList)  == 0}">
                          <dsp:param name="validateIf" value="true"/>
                        </c:when>
                        <c:otherwise>
                          <dsp:param name="validateIf" value="dojo.byId('${formId}')['${creditCardFormHandler}.useExistingAddress'][0].checked == false"/>
                        </c:otherwise>
                        </c:choose>
                      </dsp:include>
                    </div>
                  </div>
            </c:when>
            <c:otherwise>
              <dsp:importbean var="ccfh" bean="${creditCardFormHandler}"/>
              <dsp:getvalueof var="addrId" bean="${creditCardFormHandler}.value.${ccfh.creditCardWallet.addressPropertyName}.repositoryId"/>
              <dsp:input type="hidden" nullable="false" value="${addrId}"
                bean="${creditCardFormHandler}.value.${ccfh.creditCardWallet.addressPropertyName}.REPOSITORYID"/>
              <c:set var="addressMetaInfos" value="${ccfh.addressBook.addressMetaInfos}"/>
              <c:set var="addressFound" value="${false}"/>

              <c:forEach var="ami" items="${ccfh.addressBook.addressMetaInfos}">
                  <c:if test="${ami.value.valid}">
                    <c:set var="addressFound" value="${true}"/>
                  </c:if>
              </c:forEach>
              <%-- When there is no existing addresses, do not display the radio buttons. Instead just
              display the address form. --%>

              <c:choose>
                <c:when test="${!addressFound}">
                  <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label atg-base-table-customer-credit-card-spacing-two-top">
                    <label class="atg_messaging_requiredIndicator atg_commerce_csr_billingAddress" >
                      <fmt:message key="newOrderBilling.addEditCreditCard.header.billingAddress.title"/>
                    </label>
                  </span>

                  <script type="text/javascript" charset="utf-8">
                    _container_.onLoadDeferred.addCallback(function () {
                    atg.commerce.csr.${formId}existingBillingAddressPicker();
                  });
                  </script>
                </c:when>
                <c:otherwise>
                  <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label atg-base-table-customer-credit-card-spacing-two-top">
                    <label class="atg_messaging_requiredIndicator atg_commerce_csr_billingAddress" >
                      <fmt:message key="newOrderBilling.addEditCreditCard.header.billingAddress.title"/>
                    </label><span class="requiredStar">*</span>
                  </span>

                  <div class="atg-csc-base-table-cell">
                    <dsp:getvalueof var="createNewAddress" bean="${creditCardFormHandler}.createNewAddress" />
                    <dsp:input type="radio" checked="true" iclass="atg_inlineLabel"
                      bean="CreditCardFormHandler.createNewAddress" value="false"
                      priority="999" id="${formId}_newAddress"
                      onclick="${formId}HideAddressArea();"/>
                    <span><fmt:message key="newOrderBilling.addEditCreditCard.field.billingAddress.useExistingAddress"/></span>
                  </div>
                  
                  <div class="atg-csc-base-table-cell">
                    <dsp:select id="${formId}_existingAddressList" iclass=""
                      bean="${creditCardFormHandler}.value.${ccfh.creditCardWallet.addressPropertyName}.repositoryId"
                      onchange="atg.commerce.csr.customer.existingCreditCardAddressChanged();">
                      <c:forEach var="ami" items="${ccfh.addressBook.addressMetaInfos}">
                        <c:if test="${ami.value.valid}">
                          <c:choose>
                          <c:when test="${!empty ami.value.params.defaultOptions.billingAddress}">
                            <dsp:option value="${ami.key}" selected="true">
                              ${fn:escapeXml(ami.value.address.address1)}
                              ${fn:escapeXml(ami.value.address.address2)}
                              ${fn:escapeXml(ami.value.address.address3)}
                            </dsp:option>
                          </c:when>
                          <c:otherwise>
                            <dsp:option value="${ami.key}">
                              ${fn:escapeXml(ami.value.address.address1)}
                              ${fn:escapeXml(ami.value.address.address2)}
                              ${fn:escapeXml(ami.value.address.address3)}
                            </dsp:option>
                          </c:otherwise>
                          </c:choose>
                          <script type="text/javascript">
                            _container_.onLoadDeferred.addCallback( function() {
                               atg.commerce.csr.customer.addrList[ "${ami.key}" ] = {
                                    id: "${ami.key}",
                                 first: "${fn:escapeXml(ami.value.address.firstName)}",
                                middle: "${fn:escapeXml(ami.value.address.middleName)}",
                                  last: "${fn:escapeXml(ami.value.address.lastName)}" };
                             });
                          </script>
                        </c:if>
                      </c:forEach>
                    </dsp:select>
                  </div>
                </c:otherwise>
              </c:choose>
              </div>
              <div class="atg-csc-base-table-row">
                <div class="atg-csc-base-table-cell"></div>
                <div class="atg_commerce_csr_createNewAddress atg-csc-base-table-cell">
                  <script type="text/javascript" charset="utf-8">
                    atg.commerce.csr.${formId}existingBillingAddressPicker = function(){
                      var existingAddressList = dojo.byId('${formId}_existingAddressList');
                      if(existingAddressList){
                        existingAddressList.disabled = true;
                      }
                      dojo.style(dojo.byId('${formId}_addressArea'),'display','block');
                      if(dojo.byId('creditCardPopup').style.display == 'block'){
                        dijit.byId('creditCardPopup').layout();
                      }
                    }
                  </script>
                  <div>
                    <c:choose>
                     <c:when test="${addressFound}">
                      <dsp:input type="radio" checked="false" id="${formId}_newAddress"
                        iclass="atg_inlineLabel" priority="999"
                        bean="CreditCardFormHandler.createNewAddress" value="true"
                        onclick="atg.commerce.csr.${formId}existingBillingAddressPicker();"/>
                      <span><fmt:message key="newOrderBilling.addEditCreditCard.field.billingAddress.createNewAddress"/></span>
                      </c:when>
                      <c:otherwise>
                        <dsp:input type="hidden" checked="false" id="${formId}_newAddress"
                          iclass="atg_inlineLabel" priority="999"
                          bean="CreditCardFormHandler.createNewAddress" value="true"
                          />
                      </c:otherwise>
                    </c:choose>
                  </div>
                  
                </div>
                  </div>
                  
                  </div>
                  <div id="${formId}_addressArea" class="atg-base-table-customer-credit-card-address-form" style="display:none;">
                      <div class="atg-csc-base-table atg-base-table-customer-address-add-form">
                        <dsp:include src="${addressForm.URL}" otherContext="${addressForm.servletContext}">
                          <dsp:param name="formId" value="${formId}"/>
                          <dsp:param name="addressBean" value="${creditCardAddressBean}"/>
                          <dsp:param name="submitButtonId" value="${submitButtonId}"/>
                          <dsp:param name="isDisableSubmit" value="${formId}DisableSubmit"/>
                           <c:choose>
                           <c:when test="${addressFound}">
                            <dsp:param name="validateIf" value="dojo.byId('${formId}')['${creditCardFormHandler}.createNewAddress'][0].checked == false"/>
                           </c:when>
                           <c:otherwise>
                              <dsp:param name="validateIf" value="true"/>
                           </c:otherwise>
                           </c:choose>
                        </dsp:include>
                      </div>
                  </div>
            </c:otherwise>
          </c:choose>
      

      
      
      <script type="text/javascript">
        var ${formId}DisableSubmit = function () {
          var disable = false;
          if (!dijit.byId("${formId}_creditCardType")._isvalid) disable = true;
          if (dijit.byId("${formId}_creditCardNumber") &&
            !dijit.byId("${formId}_creditCardNumber")._isvalid) disable = true;
          if (dijit.byId("${formId}_maskedCreditCardNumber") &&
            !dijit.byId("${formId}_maskedCreditCardNumber")._isvalid) disable = true;
          if (!dijit.byId("${formId}_expirationMonth")._isvalid) disable = true;
          if (!dijit.byId("${formId}_expirationYear")._isvalid) disable = true;
          return disable;
        };
        var ${formId}IsValidCardType = function () {
          if (dijit.byId("${formId}_creditCardNumber")) dijit.byId("${formId}_creditCardNumber").validate(false);
          if (dijit.byId("${formId}_maskedCreditCardNumber")) dijit.byId("${formId}_maskedCreditCardNumber").validate(false);
          ${formId}Validate();
          return this._isvalid;
        };
        var ${formId}IsValidCardNum = function () {
          this._isvalid = atg.commerce.csr.order.billing.isValidCreditCardNumber(
            dijit.byId("${formId}_creditCardType"),
            dijit.byId("${formId}_creditCardNumber"));
          return this._isvalid;
        };
        var ${formId}IsValidMaskedCardNum = function () {
          this._isvalid = atg.commerce.csr.order.billing.isValidCreditCardNumberInEditContext({
            creditCardType : dijit.byId('${formId}_creditCardType'),
            creditCardNumber: dijit.byId('${formId}_maskedCreditCardNumber'),
            originalMaskedCreditCardNumber: '${maskedCreditCardNumberVar}'
          });
          return this._isvalid;
        };
        var ${formId}IsValidMonthCombo = function () {
          this._isvalid = atg.commerce.csr.order.billing.isValidCreditCardMonth(
            dijit.byId("${formId}_expirationMonth"), dijit.byId("${formId}_expirationYear"));
          dijit.byId("${formId}_expirationMonth").isValid = function () { return this._isvalid; }; // prevent recursion
          dijit.byId("${formId}_expirationYear").validate(false);
          ${formId}Validate();
          dijit.byId("${formId}_expirationMonth").isValid = ${formId}IsValidMonthCombo;
          return this._isvalid;
        };
        var ${formId}IsValidYearCombo = function () {
          this._isvalid = atg.commerce.csr.order.billing.isValidCreditCardYear(
            dijit.byId("${formId}_expirationYear"));
          dijit.byId("${formId}_expirationYear").isValid = function () { return this._isvalid; }; // prevent recursion
          dijit.byId("${formId}_expirationMonth").validate(false);
          ${formId}Validate();
          dijit.byId("${formId}_expirationYear").isValid = ${formId}IsValidYearCombo;
          return this._isvalid;
        };
        var ${formId}HideAddressArea = function () {
          dojo.style(dojo.byId('${formId}_addressArea'),'display','none'); 
          var existingAddressList = dojo.byId('${formId}_existingAddressList');
          if(existingAddressList){
            existingAddressList.disabled = false;
          }
        };
        _container_.onLoadDeferred.addCallback(function () {
          if (dijit.byId("${formId}_creditCardType")) {
            dijit.byId("${formId}_creditCardType").isValid = ${formId}IsValidCardType;
          }
          if (dijit.byId("${formId}_maskedCreditCardNumber")) {
            dijit.byId("${formId}_maskedCreditCardNumber").isValid = ${formId}IsValidMaskedCardNum;
          }
          if (dijit.byId("${formId}_creditCardNumber")) {
            dijit.byId("${formId}_creditCardNumber").isValid = ${formId}IsValidCardNum;
          }
          if (dijit.byId("${formId}_expirationMonth")) {
            dijit.byId("${formId}_expirationMonth").isValid = ${formId}IsValidMonthCombo;
          }
          if (dijit.byId("${formId}_expirationYear")) {
            dijit.byId("${formId}_expirationYear").isValid = ${formId}IsValidYearCombo;
          }
        });
      </script>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/creditCardForm.jsp#1 $$Change: 946917 $--%>
