<%--
 This UI fragment defines the Profile Search Panel that is shipped with the product, 
 but may be replaced by a customer at their discretion. Customers need simply point the configuration file
 to the new JSP snippet for this to take effect.
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customer/profileSearchUIFragment.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <dspel:importbean bean="/atg/svc/agent/ui/formhandlers/CustomerSearchTreeQueryFormHandler"/>
  <dspel:importbean bean="/atg/multisite/ProfileRealmManager"/>

    
    <div class="atg-csc-base-table-row">

      <label class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-search-first-label">
        <fmt:message key="customer.firstName.label"/>
      </label>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchFirstNameName" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[0].name"/>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchFirstNameOp" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[0].op"/>
      <span class="atg_svc_customer_firstName atg-csc-base-table-cell">
        <dspel:input type="text" converter="nullable" id="atg_service_customer_searchFirstNameValue"
               bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[0].value" beanvalue="CustomerSearchTreeQueryFormHandler.previousSearchRequest.fields[0].value" size="35" iclass="atg-base-table-customer-search-input table_textbox" maxlength="50"/>
      </span>

      <label class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-search-label">
        <fmt:message key="customer.lastName.label"/>
      </label>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchLastNameName" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[1].name"/>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchLastNameOp" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[1].op"/>
      <span class="atg_svc_customer_firstName atg-csc-base-table-cell">
        <dspel:input type="text" converter="nullable" id="atg_service_customer_searchLastNameValue"
               bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[1].value" beanvalue="CustomerSearchTreeQueryFormHandler.previousSearchRequest.fields[1].value" size="35" iclass="atg-base-table-customer-search-input table_textbox" maxlength="50"/>
      </span>

      <label class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-search-label">
        <fmt:message key="customer.login.label"/><fmt:message key="text.field.terminator"/>
      </label>
  
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchLoginName" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[2].name"/>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchLoginOp" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[2].op"/>
      <span class="atg_commerce_csr_login atg-csc-base-table-cell">
        <dspel:input type="text" converter="nullable" id="atg_service_customer_searchLoginValue"
               bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[2].value" beanvalue="CustomerSearchTreeQueryFormHandler.previousSearchRequest.fields[2].value" size="30" iclass="atg-base-table-customer-search-input table_textbox" maxlength="50"/>
      </span>

    </div>
    <div class="atg-csc-base-table-row">

      <label class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-search-first-label">
        <fmt:message key="customer.email.label"/>
      </label>

      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchEmailName" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[3].name"/>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchEmailOp" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[3].op"/>
      <span class="atg_svc_customer_email atg-csc-base-table-cell">
        <dspel:input type="text" converter="nullable" id="atg_service_customer_searchEmailValue"
                bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[3].value" beanvalue="CustomerSearchTreeQueryFormHandler.previousSearchRequest.fields[3].value" size="35" iclass="atg-base-table-customer-search-input table_textbox" maxlength="255"/>
      </span>

      <label class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-search-label">
        <fmt:message key="customer.search.postalCode.label"/>
      </label>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchPostCodeName" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[4].name" value="${agentUIConfig.customerSearchAddressPropertyName}.postalCode"/>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchPostCodeOp" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[4].op" value="starts"/>
      <span class="atg_svc_customer_postalCode atg-csc-base-table-cell">
        <dspel:input type="text" converter="nullable" id="atg_service_customer_searchPostCodeValue"
                bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[4].value" beanvalue="CustomerSearchTreeQueryFormHandler.previousSearchRequest.fields[4].value" iclass="atg-base-table-customer-search-input table_textbox" maxlength="15"/>
      </span>

      <label class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-search-label">
        <fmt:message key="customer.phone.label"/>
      </label>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchPhoneName" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[5].name"/>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchPhoneOp" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[5].op"/>
      <span class="atg_svc_customer_phone atg-csc-base-table-cell">
        <dspel:input converter="nullable" id="atg_service_customer_searchPhoneValue" bean="CustomerSearchTreeQueryFormHandler.previousSearchRequest.fields[5].value" beanvalue="CustomerSearchTreeQueryFormHandler.searchRequest.fields[5].value" type="text" iclass="atg-base-table-customer-search-input table_textbox" maxlength="50"/>
      </span>

    </div>
    
    <%-- REALMS --%>
    <dspel:getvalueof bean="ProfileRealmManager.profileRealmsEnabled" var="profileRealmsEnabled" />
    <c:if test="${profileRealmsEnabled}">
      <dspel:getvalueof bean="ProfileRealmManager.currentlyDefaultRealm" var="currentlyDefaultRealm" />
      <c:set var="realmId" value="__null__" />
      <c:if test="${not currentlyDefaultRealm}">
        <dspel:getvalueof bean="ProfileRealmManager.currentRealm.repositoryId" var="realmId" />
      </c:if>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchRealmName" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[6].name" beanvalue="CustomerSearchTreeQueryFormHandler.realmProperty"/>
      <dspel:input converter="nullable" type="hidden" size="30" id="atg_service_customer_searchRealmOp" bean="CustomerSearchTreeQueryFormHandler.searchRequest.fields[6].op" value="equal"/>
      <dspel:input converter="nullable" id="atg_service_customer_searchRealmValue" bean="CustomerSearchTreeQueryFormHandler.previousSearchRequest.fields[6].value" value="${realmId}" type="hidden"/>
    </c:if>
    <%-- END REALMS --%>
    
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customer/profileSearchUIFragment.jsp#1 $$Change: 946917 $--%>

