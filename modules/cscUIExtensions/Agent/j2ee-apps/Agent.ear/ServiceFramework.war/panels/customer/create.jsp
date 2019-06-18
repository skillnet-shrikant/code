<%--
 This page defines the Create New Customer Panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/create.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  
  <dspel:importbean var="defaultPageFragment" bean="/atg/svc/agent/ui/fragments/customer/CustomerNewDefault" /> 
  <dspel:importbean var="extendedPageFragment" bean="/atg/svc/agent/ui/fragments/customer/CustomerNewExtended" /> 
  <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels,customerPanels" context="${UIConfig.contextRoot}"/>
  <svc-ui:frameworkUrl var="errorURL" panelStacks="customerAccountPanels" context="${UIConfig.contextRoot}"/>

  <dspel:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dspel:importbean var="CustomerProfileFormHandler" bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler"/>
  <dspel:setvalue bean="CustomerProfileFormHandler.extractDefaultValuesFromProfile" value="true"/> 
  <dspel:form action="#" id="customerCreateForm" formid="customerCreateForm">
  <dspel:input type="hidden" id="successURL" value="${successURL}" bean="CustomerProfileFormHandler.successUrl" />
  <dspel:input type="hidden" id="errorURL" value="${errorURL}" bean="CustomerProfileFormHandler.errorUrl" />
    <dspel:input type="hidden" id="password" name="password" value="tempPassword" bean="CustomerProfileFormHandler.value.password" />
    <input id="atg.successMessage" name="atg.successMessage" type="hidden" value=""/>
    <input id="atg.failureMessage" name="atg.failureMessage" type="hidden" value=""/>

    <div class="atg_commerce_csr_coreCustomerInfo">
      <div class="atg-csc-base-table atg-base-table-customer-create-form">

        <c:if test="${not empty defaultPageFragment.URL}">
          <dspel:include src="${defaultPageFragment.URL}" otherContext="${defaultPageFragment.servletContext}" />
        </c:if>
        
        <c:if test="${not empty extendedPageFragment.URL}">
          <dspel:include src="${extendedPageFragment.URL}" otherContext="${extendedPageFragment.servletContext}" />
        </c:if>

        <div class="atg_commerce_csr_coreCustomerInfoDataAction atg-csc-base-table-row">
          <div class="atg-csc-base-table-cell atg-base-table-customer-create-first-label"></div>
          <div class="atg-csc-base-table-cell"></div>
          <div class="atg-csc-base-table-cell atg-base-table-customer-create-label"></div>
          <div class="atg_actionTo atg-csc-base-table-cell">
            <dspel:input type="hidden" priority="-10" value="" bean="CustomerProfileFormHandler.update"/>
            <fmt:message key="customer.create.save.button" var="saveButton" />
            <fmt:message key="customer.create.success.message" var="createSuccessMessageFormat" />
            <fmt:message key="customer.create.failure.message" var="createFailureMessageFormat" />
            <input id="update" type="button" value="${saveButton}" dojoType="atg.widget.validation.SubmitButton" 
              onclick='createCustomer("${fn:escapeXml(createSuccessMessageFormat)}", "${fn:escapeXml(createFailureMessageFormat)}");return false;'/>
          </div>
        </div>
      </div>
    </div>
        

  </dspel:form>
  <script type="text/javascript">
  _container_.onLoadDeferred.addCallback(function () {
    customerCreateFormValidate();
    atg.service.form.watchInputs('customerCreateForm', customerCreateFormValidate);
    
    // The dsp:input button tag doesn't seem to be applying the tabIndex correctly, so we will do it manually
    var _submitButton = dojo.byId("update");
    if (_submitButton != null) {
      _submitButton.tabIndex = "10";
    }
    
  });
  _container_.onUnloadDeferred.addCallback(function () {
    atg.service.form.unWatchInputs('customerCreateForm');
  });
  </script>

  </dspel:layeredBundle>
</dspel:page>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/create.jsp#1 $$Change: 946917 $--%>
