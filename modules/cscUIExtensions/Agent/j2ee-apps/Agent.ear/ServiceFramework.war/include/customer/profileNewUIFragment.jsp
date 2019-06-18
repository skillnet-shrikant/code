<%--
 This UI fragment defines the Create Profile Panel that is shipped with the product, 
 but may be replaced by a customer at their discretion. Customers need simply point the configuration file
 to the new JSP snippet for this to take effect.
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customer/profileNewUIFragment.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <dspel:importbean var="CustomerProfileFormHandler" bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler"/>
  <dspel:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dspel:setvalue bean="CustomerProfileFormHandler.extractDefaultValuesFromProfile" value="true"/> 
  <dspel:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dspel:importbean bean="/atg/multisite/ProfileRealmManager"/>

  <fmt:message key="customer.firstName.required.invalid" var="firstNameReqInv"/>
  <fmt:message key="customer.middleName.invalid" var="middleNameInv"/>
  <fmt:message key="customer.lastName.required.invalid" var="lastNameReqInv"/>
  <fmt:message key="customer.phone.invalid" var="phoneInv"/>
  <fmt:message key="customer.email.required.invalid" var="emailReqInv"/>

      <div class="atg-csc-base-table-row"><span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
        <label for="firstName" class="atg_messaging_requiredIndicator" id="firstNameAlert">
          <fmt:message key="customer.firstName.label"/>
        </label>
        <span id="firstNameRequiredStar" class="requiredStar">*</span></span>
        <div class="atg-csc-base-table-cell">
            <dspel:input id="cpFirstName" iclass="atg-base-table-customer-create-input" name="cpFirstName" type="text" maxlength="40" style="position:relative !important;"
                                   bean="CustomerProfileFormHandler.value.firstName">
              <dspel:tagAttribute name="tabindex" value="1"/>
              <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
              <dspel:tagAttribute name="required" value="true"/>
              <dspel:tagAttribute name="regExp" value="^[a-zA-Z. ]+$"/>
              <dspel:tagAttribute name="trim" value="true"/>
			  <dspel:tagAttribute name="invalidMessage" value="${firstNameReqInv}"/>
            </dspel:input>
        </div>

        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-label">
        <label for="phone" class="atg_messaging_emptyIndicator" id="phoneAlert">
          <fmt:message key="customer.phone.label"/>
        </label>
        <span id="phoneRequiredStar" class="requiredStar">*</span></span>
        <div class="atg-csc-base-table-cell">
            <dspel:input type="text" tabindex="5" id="cpPhone" name="${agentUIConfig.customerAddressPropertyName}.cpPhone" bean="CustomerProfileFormHandler.value.${agentUIConfig.customerAddressPropertyName}.phoneNumber" iclass="atg-base-table-customer-edit-phone-input atg-base-table-customer-create-input text"
                         size="25" maxlength="15">
              <dspel:tagAttribute name="tabindex" value="4"/>
              <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
              <dspel:tagAttribute name="required" value="true"/>
              <dspel:tagAttribute name="regExp" value="^([0-9]){3}\-?([0-9]){3}\-?([0-9]){4}$"/>
              <dspel:tagAttribute name="trim" value="true"/>
			  <dspel:tagAttribute name="invalidMessage" value="${phoneInv}"/>
            </dspel:input>
        </div>
      </div>

      <div class="atg-csc-base-table-row">  <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
        <label for="middleName">
          <fmt:message key="customer.middleName.label"/>
        </label></span>
        <div class="atg-csc-base-table-cell">
            <dspel:input id="middleName" iclass="atg-base-table-customer-create-input" name="middleName" type="text" maxlength="40" style="position:relative !important;"
              bean="CustomerProfileFormHandler.value.middleName">
              <dspel:tagAttribute name="tabindex" value="2"/>
              <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
              <dspel:tagAttribute name="required" value="false"/>
              <dspel:tagAttribute name="regExp" value="^[a-zA-Z. ]+$"/>
              <dspel:tagAttribute name="trim" value="true"/>
			  <dspel:tagAttribute name="invalidMessage" value="${middleNameInv}"/>
            </dspel:input>
        </div>

        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-label">
        <label for="email" class="atg_messaging_requiredIndicator" id="emailAlert">
          <fmt:message key="customer.email.label"/>
        </label>
        <span id="emailRequiredStar" class="requiredStar">*</span></span>
        <div class="atg-csc-base-table-cell">
            <dspel:input id="cpEmail" name="cpEmail" type="text" iclass="atg-base-table-customer-create-input text" maxlength="255" style="position:relative !important;"
                                   bean="CustomerProfileFormHandler.value.email">
              <dspel:tagAttribute name="tabindex" value="5"/>
              <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
              <dspel:tagAttribute name="required" value="true"/>
              <dspel:tagAttribute name="regExp" value="^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,})+$"/>
              <dspel:tagAttribute name="trim" value="true"/>
			  <dspel:tagAttribute name="invalidMessage" value="${emailReqInv}"/>
            </dspel:input>
        </div>
      </div>

      <div class="atg-csc-base-table-row"> <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
        <label for="lastName" class="atg_messaging_requiredIndicator" id="lastNameAlert">
          <fmt:message key="customer.lastName.label"/>
        </label>
        <span id="lastNameRequiredStar" class="requiredStar">*</span></span>
        <div class="atg-csc-base-table-cell">
            <dspel:input id="cpLastName" name="cpLastName" type="text" iclass="atg-base-table-customer-create-input text" maxlength="40" style="position:relative !important;"
                                   bean="CustomerProfileFormHandler.value.lastName">
              <dspel:tagAttribute name="tabindex" value="3"/>
              <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
              <dspel:tagAttribute name="required" value="true"/>
              <dspel:tagAttribute name="regExp" value="^[a-zA-Z. ]+$"/>
              <dspel:tagAttribute name="trim" value="true"/>
			  <dspel:tagAttribute name="invalidMessage" value="${lastNameReqInv}"/>
            </dspel:input>
        </div>

        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-label">
        <label for="createAccount">
          <fmt:message key="customer.create.createAccount.label"/>
        </label></span>
        <div class="atg-csc-base-table-cell">
          <dspel:input type="checkbox" name="saveOnUpdate" 
                      bean="CustomerProfileFormHandler.saveOnUpdate" 
                      checked="true" 
                      iclass="atg-base-table-customer-create-input"
                      onchange="createAccountResetRequiredFields();return false;">
           <dspel:tagAttribute name="tabindex" value="8"/>
           </dspel:input>
        </div>
      </div>
 
      <%-- REALMS --%>
      <dspel:getvalueof bean="ProfileRealmManager.profileRealmsEnabled" var="profileRealmsEnabled" />
      <c:if test="${profileRealmsEnabled}">
        <%-- When current realm is 'Default' (null) then realmId for new customer should be null, not '__null__' as it should for searching purposes. So we even do not add it. --%>
        <dspel:getvalueof bean="ProfileRealmManager.currentlyDefaultRealm" var="currentlyDefaultRealm" />
        <c:if test="${not currentlyDefaultRealm}">
          <dspel:input converter="nullable" id="createInRealm" bean="CustomerProfileFormHandler.value.realmId"  beanvalue="ProfileRealmManager.currentRealm.repositoryId" type="hidden"/>
        </c:if>
      </c:if>
      <%-- END REALMS --%>

    <script type="text/javascript">
    dojo.require("dijit.form.DateTextBox");
    var customerCreateFormValidate = function () {
    var disable = false;
    
    if (validateForCreate()) {
      dijit.byId("cpFirstName").required = true;
      dijit.byId("cpEmail").required = true;
      dijit.byId("cpLastName").required = true;
      dijit.byId("cpPhone").required = true;
  
      dojo.style("firstNameRequiredStar", "visibility", "visible");
      dojo.style("emailRequiredStar", "visibility", "visible");
      dojo.style("lastNameRequiredStar", "visibility", "visible");
      dojo.style("phoneRequiredStar", "visibility", "visible");
      
      if (!dijit.byId("cpFirstName").isValid()) disable = true;
      if (!dijit.byId("cpEmail").isValid()) disable = true;
      if (!dijit.byId("cpLastName").isValid()) disable = true;
      if (!dijit.byId("cpPhone").isValid()) disable = true;
      
    }else{
      dijit.byId("cpFirstName").required = false;
      dijit.byId("cpEmail").required = false;
      dijit.byId("cpLastName").required = false;
      dijit.byId("cpPhone").required = false;
      
      dojo.style("firstNameRequiredStar", "visibility", "hidden");
      dojo.style("emailRequiredStar", "visibility", "hidden");
      dojo.style("lastNameRequiredStar", "visibility", "hidden");
      dojo.style("phoneRequiredStar", "visibility", "hidden");
      
      dijit.byId("cpFirstName").validate();
      dijit.byId("cpEmail").validate();
      dijit.byId("cpLastName").validate();
      dijit.byId("cpPhone").validate();
    }
  
    dojo.byId("customerCreateForm").update.disabled = disable;
  
  }
  </script>

   </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customer/profileNewUIFragment.jsp#1 $$Change: 946917 $--%>
