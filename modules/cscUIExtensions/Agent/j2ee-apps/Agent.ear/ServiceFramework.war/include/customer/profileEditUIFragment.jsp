<%--
 This UI fragment defines the Edit Profile Information Panel that is shipped with the product, 
 but may be replaced by a customer at their discretion. Customers need simply point the configuration file
 to the new JSP snippet for this to take effect.
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customer/profileEditUIFragment.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <dspel:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dspel:importbean var="CustomerProfileFormHandler" bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler"/>
  <dspel:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dspel:importbean bean="/atg/core/i18n/LocaleTools"/>

  <fmt:message key="customer.firstName.required.invalid" var="firstNameReqInv"/>
  <fmt:message key="customer.middleName.invalid" var="middleNameInv"/>
  <fmt:message key="customer.lastName.required.invalid" var="lastNameReqInv"/>
  <fmt:message key="customer.phone.invalid" var="phoneInv"/>
  <fmt:message key="customer.email.required.invalid" var="emailReqInv"/>
  
  <script type="text/javascript">
    dojo.require("dijit.form.DateTextBox");
  </script>
     <div class="atg_svc_coreCustomerInfo">
    <div class="coreCustomerInfo">

      <div class="atg-csc-base-table atg-base-table-customer-create-form">
        <div class="atg-csc-base-table-row">
          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
            <label for="firstName" class="atg_messaging_requiredIndicator" id="firstNameAlert">
          <fmt:message key="customer.firstName.label"/></label>
          <span class="requiredStar">*</span>
          </span>
          <div class="atg-csc-base-table-cell">
            <dspel:input id="firstName" name="firstName" iclass="" type="text" size="25"  maxlength="40"
                         required="${true}" bean="CustomerProfileFormHandler.value.firstName">
              <dspel:tagAttribute name="tabindex" value="1"/>
			  <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
              <dspel:tagAttribute name="required" value="true"/>
              <dspel:tagAttribute name="regExp" value="^[a-zA-Z. ]+$"/>
              <dspel:tagAttribute name="trim" value="true"/>
			  <dspel:tagAttribute name="invalidMessage" value="${firstNameReqInv}"/>
              <dspel:tagAttribute name="inlineIndicator" value="firstNameAlert"/>
            </dspel:input>
          </div>

          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-label">
            <label for="phone" class="atg_messaging_emptyIndicator" id="phoneAlert">
            <fmt:message key="customer.phone.label"/></label>
            <span class="requiredStar">*</span>
          </span>
          <div class="atg-csc-base-table-cell">
			<dspel:input type="text" id="phone" name="${agentUIConfig.customerAddressPropertyName}.phone" bean="CustomerProfileFormHandler.value.${agentUIConfig.customerAddressPropertyName}.phoneNumber" iclass="text atg-base-table-customer-edit-phone-input" maxlength="15">
				<dspel:tagAttribute name="tabindex" value="5"/>
				<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
				<dspel:tagAttribute name="required" value="true"/>
				<dspel:tagAttribute name="regExp" value="^([0-9]){3}\-?([0-9]){3}\-?([0-9]){4}$"/>
				<dspel:tagAttribute name="trim" value="true"/>
				<dspel:tagAttribute name="invalidMessage" value="${phoneInv}"/>
				<dspel:tagAttribute name="inlineIndicator" value="phoneAlert"/>
			</dspel:input>
          </div>
        </div>

        <div class="atg-csc-base-table-row">
          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
            <label for="middleName">
            <fmt:message key="customer.middleName.label"/></label>
          </span>
          <div class="atg-csc-base-table-cell">
            <dspel:input id="middleName" iclass="" name="middleName" type="text" size="25" maxlength="40"
              bean="CustomerProfileFormHandler.value.middleName">
              <dspel:tagAttribute name="tabindex" value="2"/>
              <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
              <dspel:tagAttribute name="required" value="false"/>
              <dspel:tagAttribute name="regExp" value="^[a-zA-Z. ]+$"/>
              <dspel:tagAttribute name="trim" value="true"/>
			  <dspel:tagAttribute name="invalidMessage" value="${middleNameInv}"/>
            </dspel:input>
          </div>

          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-label">
            <label for="dateOfBirth" id="cpdateOfBirthAlert">
              <fmt:message key="customer.birthDate.label"/>
            </label>
          </span>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-one-top">
            <dspel:getvalueof var="datePattern" scope="request" bean="LocaleTools.userFormattingLocaleHelper.datePatterns.short" />  
            <dspel:input type="hidden"
                         bean="CustomerProfileFormHandler.value.dateOfBirth"
                         id="dateOfBirthHidden"
                         name="dateOfBirthHidden"
                         maxlength="10"
                         converter="date"
                         format="${datePattern}" nullable="true">
              <dspel:tagAttribute name="tabindex" value="4"/>
            </dspel:input>
            <input type="text"
                         id="dateOfBirth"
                         name="dateOfBirth"
                         maxlength="10"
                         dojoType="dijit.form.DateTextBox"
                         constraints="{datePattern:'${datePattern}'}"
                         onchange="dojo.byId('dateOfBirthHidden').value = dojo.byId('dateOfBirth').value;">
            <dspel:img src="/CAF/images/calendar/calendar.gif"
              id="dateOfBirthIcon"
              title="${birthDateTooltip}"
              align="absmiddle"
              style="cursor:pointer;margin-top: 3px !important; position: absolute !important; cursor: hand; float: left !important;"
              onclick="dojo.byId('dateOfBirth').focus()"/>
          </div>
        </div>

        <div class="atg-csc-base-table-row">
          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
            <label for="lastName" class="atg_messaging_requiredIndicator" id="lastNameAlert">
            <fmt:message key="customer.lastName.label"/></label>
            <span class="requiredStar">*</span>
          </span>
          <div class="atg-csc-base-table-cell">
            <dspel:input id="lastName" name="lastName" type="text" size="25" iclass="text" maxlength="40"
                                   required="${true}" bean="CustomerProfileFormHandler.value.lastName">
              <dspel:tagAttribute name="tabindex" value="3"/>
              <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
              <dspel:tagAttribute name="required" value="true"/>
              <dspel:tagAttribute name="regExp" value="^[a-zA-Z. ]+$"/>
              <dspel:tagAttribute name="trim" value="true"/>
			  <dspel:tagAttribute name="invalidMessage" value="${lastNameReqInv}"/>
            </dspel:input>
          </div>

          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-label">
            <label for="profileId"><fmt:message key="customer.view.profileId.label"/></label>
          </span>
          <span class="plainText atg-csc-base-table-cell">
            <dspel:valueof bean="CustomerProfileFormHandler.profile.repositoryId"/>
          </span>
        </div>
        <div class="atg-csc-base-table-row">
          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
            <label for="email" class="atg_messaging_requiredIndicator" id="emailAlert">
            <fmt:message key="customer.email.label"/></label>
            <span class="requiredStar">*</span>
          </span>
          <div class="atg-csc-base-table-cell">
            <dspel:input id="email"  name="email" type="text" size="25" iclass="text"
                                   required="${true}" bean="CustomerProfileFormHandler.value.email">
              <dspel:tagAttribute name="tabindex" value="6"/>
              <dspel:tagAttribute name="inlineIndicator" value="emailAlert"/>
              <dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
              <dspel:tagAttribute name="required" value="true"/>
              <dspel:tagAttribute name="regExp" value="^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,})+$"/>
              <dspel:tagAttribute name="trim" value="true"/>
			  <dspel:tagAttribute name="invalidMessage" value="${emailReqInv}"/>
              
            </dspel:input>
          </div>

          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-label">
            <label for="creationDate"><fmt:message key="customer.view.creationDate.label"/></label>
          </span>
          <span class="plainText atg-csc-base-table-cell">
            <dspel:getvalueof var="registrationDate" bean="CustomerProfileFormHandler.value.registrationDate" scope="request"/>
            <web-ui:formatDate value="${registrationDate}" type="both" dateStyle="medium" timeStyle="short"/>
          </span>
        </div>
        <div class="atg-csc-base-table-row">
          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
            <label for="login" id="loginAlert">
            <fmt:message key="customer.login.label"/><fmt:message key="text.field.terminator"/></label>
          </span>
          <span class="plainText atg-csc-base-table-cell">
            <dspel:valueof bean="CustomerProfileFormHandler.value.login"/>
          </span>

          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-label">
            <label for="password"><fmt:message key="customer.view.password.label"/></label>
          </span>
          <span class="plainText atg-csc-base-table-cell">
            <a href="#" onclick="emailNewPassword('<fmt:message key="customer.view.password.confirmationMessage"/>')"><fmt:message key="customer.view.password.link"/></a>
          </span>
        </div>
        <div class="atg-csc-base-table-row">
          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label" style="vertical-align: top;">
            <label for="segments"><fmt:message key="customer.view.segments.label"/></label>
          </span>
          <div class="atg-csc-base-table-cell">
            <textarea name="segments" cols="" rows="" readonly="readonly"><dspel:droplet name="ForEach"><dspel:param bean="CustomerProfileFormHandler.value.profileGroupsSnapshot" name="array"/><dspel:setvalue param="segment" paramvalue="element"/><dspel:oparam name="empty"><fmt:message key="customer.view.noSegmentsFound" /></dspel:oparam><dspel:oparam name="output"><dspel:valueof param="segment"/>
              </dspel:oparam></dspel:droplet></textarea>
          </div>
          
		  <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-label" style="vertical-align: top;">
            <label for="gender">
            Gender:
          </span>
          <div class="atg-csc-base-table-cell" style="vertical-align: top;">
          	<dspel:getvalueof var="genderValue" bean="CustomerProfileFormHandler.value.gender"/>
			<dspel:select id="gender" name="gender" bean="CustomerProfileFormHandler.value.gender">
				<dspel:option value="Prefer Not To Say">Prefer not to say</dspel:option>
				<dspel:option value="Female" selected="${genderValue == 'Female'}">Female</dspel:option>
				<dspel:option value="Male" selected="${genderValue == 'Male'}">Male</dspel:option>
			</dspel:select>
          </div>
          
        </div>
      </div>
    </div>
  <script type="text/javascript">
    _container_.onLoadDeferred.addCallback(function () {
      dojo.byId('dateOfBirth').value = dojo.byId('dateOfBirthHidden').value;
    });
  </script> 
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customer/profileEditUIFragment.jsp#1 $$Change: 946917 $--%>
