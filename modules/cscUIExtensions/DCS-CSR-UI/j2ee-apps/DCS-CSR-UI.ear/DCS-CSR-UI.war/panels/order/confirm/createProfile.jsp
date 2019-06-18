<%--
This file displays a pane that allows the user to enter 
customer profile details and save them to the repository.

Order - the current order.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/createProfile.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">

  <dsp:importbean var="CustomerProfileFormHandler" bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler"/>
  <dspel:getvalueof var="customerProfile" bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler.profile"/>

  <dsp:setvalue bean="CustomerProfileFormHandler.extractDefaultValuesFromProfile" value="true"/>
  
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  <dsp:tomap var="order" param="currentOrder"/>
  
  <%-- Create success and error urls for Submit Order --%>
  <dsp:getvalueof param="panelStackId" var="currentPanel"/>
  <svc-ui:frameworkUrl var="successURL" panelStacks="${currentPanel}" context="${UIConfig.contextRoot}"/>
    
  <div id="atg_commerce_csr_confirm_createCustomer_div">
    <h3 class="atg_svc_subSectionTitle atg_commerce_create_customer_confirm_header" id="atg_commerce_csr_orderconfirm_createNewAccount">
      <fmt:message key='confirmOrder.newCustomerProfile.header'/>
    </h3>        
    
    <dsp:form id="atg_commerce_csr_customerCreateForm" 
              formid="atg_commerce_csr_customerCreateForm"
              method="POST">    
      <dl class="atg_commerce_csr_customerInfo">
        <dsp:input type="hidden" name="password" value="tempPassword" bean="CustomerProfileFormHandler.value.password" />
        <input id="atg.successMessage" name="atg.successMessage" type="hidden" value=""/>
        <input id="atg.failureMessage" name="atg.failureMessage" type="hidden" value=""/>
           
        <table border="0" cellpadding="0" cellspacing="0" class="atg_commerce_create_customer_confirm">
          <tr>
            <%-- First Name --%>  
            <td class="field-label-first-column">
                <span id="atg_commerce_csr_confirm_fNameAlert" class="atg_messaging_requiredIndicator">
              <fmt:message key='confirmOrder.newCustomerProfile.firstName.label'/></span><span class="requiredStar">*</span>          
            </td>
            <td class="field-value-first-column">
              <fmt:message key="confirmOrder.newCustomerProfile.firstNameMissing" var="firstNameMissing"/>
              <dsp:input type="text" maxlength="40"
                      id="atg_commerce_csr_confirm_fName" 
                      name="atg_commerce_csr_confirm_fName"
                      required="${true}" bean="CustomerProfileFormHandler.value.firstName">     
                <dsp:tagAttribute name="tabindex" value="1"/>
                <dsp:tagAttribute name="dojoType" value="dijit.form.ValidationTextBox"/>
                <dsp:tagAttribute name="required" value="true"/>
                <dsp:tagAttribute name="trim" value="true"/>
                <dsp:tagAttribute name="promptMessage" value="${firstNameMissing}"/>
                <dsp:tagAttribute name="inlineIndicator" value="atg_commerce_csr_confirm_fNameAlert"/>
              </dsp:input>
            </td>
            
            <%-- Phone Number --%>  
            <td class="field-label-second-column">
              <label for="phoneNumber"><span class="atg_messaging_emptyIndicator" 
                  id="atg_commerce_csr_confirm_phoneAlert">
              </span><fmt:message key='confirmOrder.newCustomerProfile.phoneNumber.label'/></label>
            </td>
            <td class="field-value-second-column">
              <dsp:input type="text" maxlength="15" tabindex="4"
                     id="atg_commerce_csr_confirm_phone" name="atg_commerce_csr_confirm_phone" 
                     bean="CustomerProfileFormHandler.value.homeAddress.phoneNumber"/>
            </td>
          </tr>
          <tr>
            <%-- Middle Name --%>  
            <td class="field-label-first-column">
              <label for="middleName"><span id="middleName">
                <fmt:message key="confirmOrder.newCustomerProfile.middleName.label"/></span></label>
            </td>
            <td class="field-value-first-column">
              <dsp:input id="middleName" name="middleName" type="text" maxlength="40" 
                bean="CustomerProfileFormHandler.value.middleName">  
                <dsp:tagAttribute name="tabindex" value="2"/>          
                <dsp:tagAttribute name="dojoType" value="dijit.form.ValidationTextBox"/>
                <dsp:tagAttribute name="trim" value="true"/>
              </dsp:input>
            </td>
            
            <%-- Email Address --%>  
            <td class="field-label-second-column">
              <span class="atg_messaging_requiredIndicator" id="atg_commerce_csr_confirm_emailAlert">
                <fmt:message key='confirmOrder.newCustomerProfile.email.label'/></span><span class="requiredStar">*</span>
            </td>
            <td class="field-value-second-column">
              <fmt:message key="confirmOrder.newCustomerProfile.emailAddressMissing" var="emailAddressMissing"/>
              <fmt:message key="confirmOrder.newCustomerProfile.emailAddressInvalid" var="emailAddressInvalid"/>
              <dsp:input id="atg_commerce_csr_confirm_email" name="atg_commerce_csr_confirm_email" 
                         type="text" maxlength="255"
                         required="${true}" bean="CustomerProfileFormHandler.value.email">
                <dsp:tagAttribute name="tabindex" value="5"/>                    
                <dsp:tagAttribute name="dojoType" value="dijit.form.ValidationTextBox"/>
                <dsp:tagAttribute name="validator" value="dojox.validate.isEmailAddress"/>
                <dsp:tagAttribute name="required" value="true"/>
                <dsp:tagAttribute name="trim" value="true"/>
                <dsp:tagAttribute name="promptMessage" value="${emailAddressMissing}"/>
                <dsp:tagAttribute name="invalidMessage" value="${emailAddressInvalid}"/>
                <dsp:tagAttribute name="inlineIndicator" value="atg_commerce_csr_confirm_emailAlert"/>
              </dsp:input>
            </td>
          </tr>
          <tr>
            <%-- Last Name --%>  
            <td class="field-label-first-column">
              <span class="atg_messaging_requiredIndicator" id="atg_commerce_csr_confirm_lNameAlert">
              <fmt:message key='confirmOrder.newCustomerProfile.lastName.label'/></span><span class="requiredStar">*</span>
            </td>
            <td class="field-value-first-column">
              <fmt:message key="confirmOrder.newCustomerProfile.lastNameMissing" var="lastNameMissing"/>
              <dsp:input id="atg_commerce_csr_confirm_lastName" name="atg_commerce_csr_confirm_lastName" 
                         type="text" maxlength="40"
                         required="${true}" bean="CustomerProfileFormHandler.value.lastName">
                <dsp:tagAttribute name="tabindex" value="3"/>                     
                <dsp:tagAttribute name="dojoType" value="dijit.form.ValidationTextBox"/>
                <dsp:tagAttribute name="required" value="true"/>
                <dsp:tagAttribute name="trim" value="true"/>
                <dsp:tagAttribute name="promptMessage" value="${lastNameMissing}"/>
                <dsp:tagAttribute name="inlineIndicator" value="atg_commerce_csr_confirm_lNameAlert"/>
              </dsp:input>
            </td>
          </tr>
          <tr>
            <%-- Login Name --%>  
            <td class="field-label-first-column">
              <span class="atg_messaging_requiredIndicator" id="atg_commerce_csr_confirm_loginAlert">
                <fmt:message key='confirmOrder.newCustomerProfile.loginName.label'/></span><span class="requiredStar">*</span>
            </td>
            <td class="field-value-first-column">
              <fmt:message key="confirmOrder.newCustomerProfile.loginMissing" var="loginMissing"/>
              <dsp:input id="atg_commerce_csr_confirm_login" name="atg_commerce_csr_confirm_login" 
                         type="text" maxlength="40"
                         required="${true}" bean="CustomerProfileFormHandler.value.login">
                <dsp:tagAttribute name="tabindex" value="6"/>
                <dsp:tagAttribute name="dojoType" value="dijit.form.ValidationTextBox"/>
                <dsp:tagAttribute name="required" value="true"/>
                <dsp:tagAttribute name="trim" value="true"/>
                <dsp:tagAttribute name="promptMessage" value="${loginMissing}"/>
                <dsp:tagAttribute name="inlineIndicator" value="atg_commerce_csr_confirm_loginAlert"/>
              </dsp:input>
            </td>
          </tr>
          <tr>
            <%-- Save Credit Cards --%>  
            <td class="field-label-first-column" colspan="4">
              <label><dsp:input type="checkbox" checked="${true}" 
                        name="atg_commerce_csr_confirm_saveCreditCards" 
                        bean="CustomerProfileFormHandler.saveCreditCards"
                        />&nbsp;
            <fmt:message key='confirmOrder.newCustomerProfile.saveCardsChecbox.description'/></label>
            </td>
          </tr>
        </table>   
           
      
      <%-- Creating a new customer profile, so save it --%>  
      <dsp:input type="hidden" value="${true}" bean="CustomerProfileFormHandler.saveOnUpdate"/>        
      <dsp:input type="hidden" value="${order.id}" bean="CustomerProfileFormHandler.orderId"/>
      <dsp:input type="hidden" value="${successURL}" bean="CustomerProfileFormHandler.successUrl"/>
       
       <%-- Save button --%>              
       <div class="atg_commerce_csr_coreCustomerInfoDataAction">
        <dsp:input type="hidden" priority="-10" value="" bean="CustomerProfileFormHandler.update"/>             
        <div class="atg_actionTo"> 
          <fmt:message key="confirmOrder.newCustomerProfile.createUserOk" var="createUserOkFormat" />
          <fmt:message key="confirmOrder.newCustomerProfile.createUserFailure" var="createUserFailureFormat" />         
          <input type="button" 
            name="atg_commerce_csr_confirm_saveButton"
            id="atg_commerce_csr_confirm_saveButton"
            onclick="atg.commerce.csr.order.confirm.saveCustomerProfile('${createUserOkFormat}', '${createUserFailureFormat}');return false;"
            dojoType="atg.widget.validation.SubmitButton"
            value="<fmt:message key='common.save'/>"/>
        </div>
      </div>
    </dsp:form>
   </div>   
          
  </dsp:layeredBundle> 
      <script type="text/javascript">
var atg_commerce_csr_customerCreateFormValidate = function () {
  var disable = false;
  
  if (!dijit.byId("atg_commerce_csr_confirm_fName").isValid()) disable = true;
  if (!dijit.byId("atg_commerce_csr_confirm_email").isValid()) disable = true;
  if (!dijit.byId("atg_commerce_csr_confirm_lastName").isValid()) disable = true;
  if (!dijit.byId("atg_commerce_csr_confirm_login").isValid()) disable = true;
   
  dojo.byId("atg_commerce_csr_customerCreateForm").atg_commerce_csr_confirm_saveButton.disabled = disable;

}
_container_.onLoadDeferred.addCallback(function () {
  atg_commerce_csr_customerCreateFormValidate();
  atg.service.form.watchInputs('atg_commerce_csr_customerCreateForm', atg_commerce_csr_customerCreateFormValidate);
});
_container_.onUnloadDeferred.addCallback(function () {
  atg.service.form.unWatchInputs('atg_commerce_csr_customerCreateForm');
});
</script>        
</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/createProfile.jsp#1 $$Change: 946917 $--%>
