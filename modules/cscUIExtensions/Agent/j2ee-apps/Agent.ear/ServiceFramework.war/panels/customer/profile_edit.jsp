<%--
 This page defines the Customer Information Panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/profile_edit.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <dspel:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dspel:importbean var="CustomerProfileFormHandler" bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler"/>
  <dspel:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>

  <dspel:importbean var="defaultPageFragment" bean="/atg/svc/agent/ui/fragments/customer/CustomerEditDefault" /> 
  <dspel:importbean var="extendedPageFragment" bean="/atg/svc/agent/ui/fragments/customer/CustomerEditExtended" /> 

  <fmt:message key="customer.emailAddress.invalid" var="emailAddressInvalid"/>
  <fmt:message key="customer.emailAddress.required" var="emailAddressRequired"/>
  <fmt:message key="customer.firstName.required" var="firstNameRequired"/>
  <fmt:message key="customer.lastName.required" var="lastNameRequired"/>
  <fmt:message key="customer.loginName.required" var="loginNameRequired"/>
  <fmt:message key="customer.birthDate.tooltip" var="birthDateTooltip"/>

  <dspel:form style="display:none" action="#" id="customerResetPasswordForm" formid="customerResetPasswordForm">
    <dspel:input type="hidden" priority="-10" value="" bean="CustomerProfileFormHandler.resetPassword"/>
  </dspel:form>

  <dspel:setvalue bean="CustomerProfileFormHandler.extractDefaultValuesFromProfile" value="true"/> 

  <dspel:form action="#" id="customerCreateForm" formid="customerCreateForm">
    <input id="atg.successMessage" name="atg.successMessage" type="hidden" value=""/>
    <input id="atg.failureMessage" name="atg.failureMessage" type="hidden" value=""/>
  
  <c:if test="${not empty defaultPageFragment.URL}">			  
    <dspel:include src="${defaultPageFragment.URL}" otherContext="${defaultPageFragment.servletContext}" />
  </c:if>	
  
  <c:if test="${not empty extendedPageFragment.URL}">			  
    <dspel:include src="${extendedPageFragment.URL}" otherContext="${extendedPageFragment.servletContext}" />
  </c:if>	

  <div>
     <ul class="atg_svc_addressForm customerInfo">
       <li class="coreCustomerInfoDataAction">
          <dspel:input type="hidden" priority="-10" value="" bean="CustomerProfileFormHandler.update"/>
          <fmt:message key="customer.view.update.button" var="updateButton" />
          <fmt:message key="customer.update.success.message" var="updateSuccessMessageFormat" />
          <dspel:getvalueof var="customerFirstName" bean="CustomerProfileFormHandler.value.firstName"/>
          <dspel:getvalueof var="customerLastName" bean="CustomerProfileFormHandler.value.lastName"/>
          <fmt:message key="customer.update.failure.message" var="updateFailureMessage" >
            <fmt:param value="${fn:escapeXml(customerFirstName)}" />
            <fmt:param value="${fn:escapeXml(customerLastName)}" />
          </fmt:message>
          <input id="update" type="button" value="${updateButton}" dojoType="atg.widget.validation.SubmitButton" 
            onclick='editCustomer("${fn:escapeXml(updateSuccessMessageFormat)}", "${fn:escapeXml(updateFailureMessage)}");return false;'/>
       </li>
    </ul>
  </div>

  </dspel:form>
  <script type="text/javascript">
    atg.progress.update('cmcCustomerSearchPS');
  </script>
  <script type="text/javascript">
var customerCreateFormValidate = function () {
  var disable = false;

  if (!dijit.byId("firstName").isValid()) disable = true;
  if (!dijit.byId("email").isValid()) disable = true;
  if (!dijit.byId("middleName").isValid()) disable = true;
  if (!dijit.byId("lastName").isValid()) disable = true;
  if (!dijit.byId("phone").isValid()) disable = true;

  dojo.byId("customerCreateForm").update.disabled = disable;
}

_container_.onLoadDeferred.addCallback(function () {
  customerCreateFormValidate();
  atg.service.form.watchInputs('customerCreateForm', customerCreateFormValidate);
  
  // The dsp:input button tag doesn't seem to be applying the tabIndex correctly, so we will do it manually
  var _submitButton = dojo.byId("update");
  if (_submitButton != null) {
    _submitButton.tabIndex = "8";
  }
});
</script>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/profile_edit.jsp#1 $$Change: 946917 $--%>

