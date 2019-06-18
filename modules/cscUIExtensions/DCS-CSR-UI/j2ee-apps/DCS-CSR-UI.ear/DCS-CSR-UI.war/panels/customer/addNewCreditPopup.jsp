<%--
 This page defines the popup for adding new credit

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/addNewCreditPopup.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/profile/CreateStoreCreditFormHandler" var="customerServiceProfileFormHandler"/>
  <dsp:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dsp:importbean bean="/atg/commerce/pricing/CurrencyCodeDroplet"/>
  <dsp:importbean bean="/atg/commerce/pricing/PricingTools"/>
  <dsp:importbean var="CSRAgentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools"/>

    <script type="text/javascript">
      dojo.require("atg.widget.validation.CurrencyTextboxEx");
    </script>
  
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <div class="atg_commerce_csr_popupPanel">
      <div class="atg_commerce_csr_popupPanelCloseButton" >
      </div>
      <dsp:form id="addNewCreditForm" formid="addNewCreditForm" name="addNewCreditForm">
        <dsp:input bean="CreateStoreCreditFormHandler.createStoreCredit" id="createStoreCredit" name="createStoreCredit" type="hidden" value="1" priority="-10"/>
        <svc-ui:frameworkUrl var="url"/>
        <dsp:input bean="CreateStoreCreditFormHandler.updateSuccessURL" type="hidden" value="${url}" />
        <dsp:input bean="CreateStoreCreditFormHandler.updateErrorURL" type="hidden" value="${url}" />
        <fmt:message key="customer.credits.addNewCredit.required" var="requiredMessage"/>
        <div class="add_aCredit atg-csc-base-table">
          <%--TODO: Uncomment when we are going to support credit card credits<dt>--%>
          <%--<dt>
            <input name="" type="radio" value="" checked="checked">
            <fmt:message key="customer.credits.addNewCredit.storeCredit"/>
          </dt>
          <dd>&nbsp;</dd>
          <dt>
            <input name="" type="radio" value="">
            &nbsp;<fmt:message key="customer.credits.addNewCredit.creditCard"/>
          </dt>
          <dd>
            <select disabled="disabled">
              <option value="v1234" selected>&nbsp</option>
            </select>
          </dd>--%>
          <div class="atg-csc-base-table-row">
            <div class="atg-csc-base-table-cell atg-base-table-customer-create-first-label">
              <fmt:message key="customer.credits.addNewCredit.creditAmount"/>
              <span class="requiredStar">*</span>
            </div>
            <div class="atg-csc-base-table-cell">
              <dsp:droplet name="CurrencyCodeDroplet">
                <dsp:param name="locale" bean="PricingTools.defaultLocale"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="currencyCode" vartype="java.lang.String" param="currencyCode"/>
                </dsp:oparam>
              </dsp:droplet>

              <dsp:droplet name="/atg/commerce/pricing/CurrencyDecimalPlacesDroplet">
                <dsp:param name="currencyCode" value="${currencyCode}"/>
                <dsp:oparam name="output">
                   <dsp:getvalueof var="currencyDecimalPlaces" param="currencyDecimalPlaces"/>
                </dsp:oparam>
              </dsp:droplet>

              <fmt:message key="customer.credits.addNewCredit.amountRequired" var="amountRequiredMessage"/>
              <dsp:input bean="CreateStoreCreditFormHandler.editValue.amount" name="creditAmount" style="position:relative !important;" id="creditAmount" type="text" size="10" maxlength="10">
                <dsp:tagAttribute name="dojoType" value="atg.widget.validation.CurrencyTextboxEx"/>
                <dsp:tagAttribute name="required" value="true"/>
                <dsp:tagAttribute name="inlineIndicator" value="creditAmountAlert"/>
                <dsp:tagAttribute name="missingMessage" value="${amountRequiredMessage}"/>
                <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
                <dsp:tagAttribute name="currency" value="${currencyCode}"/>
                <dsp:tagAttribute name="currencySymbol" value="${CSRAgentTools.pricingToolsDefaultCurrencySymbolInFormattingLocale}"/>
                <dsp:tagAttribute name="onkeyup" value="return addNewCreditFormValidate()"/>
                <dsp:tagAttribute name="constraints" value="{places:${currencyDecimalPlaces}}"/>
              </dsp:input>
            </div>
          </div>
          <%--TODO: "reason code" feature is not feasible for this release. Uncomment when it is feasible.--%>
          <%--
          <dt >
            <span class="requiredStar">*</span>
            <fmt:message key="customer.credits.addNewCredit.reason"/>
          </dt>
          <dd>
            <dsp:select bean="CreateStoreCreditFormHandler.editValue.reason" name="reason"></dsp:select>
          </dd>--%>
          <div class="atg-csc-base-table-row">
            <div class="atg-csc-base-table-cell atg-base-table-customer-create-first-label">
              <span class="atg_messaging_requiredIndicator" id="commentsAlert"></span>
              <fmt:message key="customer.credits.addNewCredit.comments"/>
              <span class="requiredStar">*</span>
            </div>
            <div class="atg-csc-base-table-cell">
              <fmt:message key="customer.credits.addNewCredit.noteRequired" var="noteRequiredMessage"/>
              <fmt:message key="customer.credits.addNewCredit.noteRange" var="noteRangeMessage"/>
              <dsp:input type="text" bean="CreateStoreCreditFormHandler.editvalue.comments" name="note" style="position:relative !important;" id="note">
                <dsp:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox"/>
                <dsp:tagAttribute name="required" value="true"/>
                <dsp:tagAttribute name="cols" value="40"/>
                <dsp:tagAttribute name="rows" value="5"/>
                <dsp:tagAttribute name="maxlength" value="254"/>
                <dsp:tagAttribute name="rangeMessage" value="${noteRangeMessage}"/>
                <dsp:tagAttribute name="missingMessage" value="${noteRequiredMessage}"/>
                <dsp:tagAttribute name="inlineIndicator" value="commentsAlert"/>
                <dsp:tagAttribute name="onkeyup" value="return addNewCreditFormValidate()"/>  
              </dsp:input>
            </div>
          </div>
        </div>
        <div class="atg_commerce_csr_panelFooter">
          <input id="addNewCreditFormButton" disabled="true" type="button" dojoType="atg.widget.validation.SubmitButton" name="addButton" value="<fmt:message key='customer.credits.addNewCredit.save'/>" onclick="atg.commerce.csr.customer.addNewCredit('<fmt:message key="customer.credits.addNewCredit.amountInvalid"/>', '<fmt:message key="customer.credits.addNewCredit.noteInvalid"/>');"/>
          <input type="button" name="cancelButton" value="<fmt:message key='customer.credits.addNewCredit.cancel'/>" onclick="atg.commerce.csr.common.hidePopupWithReturn('addNewCreditPopup', {result:'cancel'});"/>
        </div>
      </dsp:form>
    </div>
  </dsp:layeredBundle>
  
<script type="text/javascript">
  var addNewCreditFormValidate = function () {
    var disable = true;
    var _creditAmount = dojo.byId("creditAmount").value;
    var _note = dojo.byId("note").value;

    if ((_creditAmount.length > 0) && (_note.length > 0))
      disable = false;
     
    dojo.byId("addNewCreditFormButton").disabled = disable;
  }

  _container_.onLoadDeferred.addCallback(function () {
    addNewCreditFormValidate();
    atg.service.form.watchInputs('addNewCreditForm', addNewCreditFormValidate);
  });
  _container_.onUnloadDeferred.addCallback(function () {
    atg.service.form.unWatchInputs('addNewCreditForm');
  });
</script> 
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/addNewCreditPopup.jsp#2 $$Change: 1179550 $--%>
