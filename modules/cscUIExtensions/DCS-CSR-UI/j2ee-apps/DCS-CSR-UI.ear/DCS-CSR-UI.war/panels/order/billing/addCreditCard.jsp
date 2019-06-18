<%-- This page is used to add credit card as payment option.

--%>

<%@ include file="/include/top.jspf"%>

<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
  <c:set var="currentOrder" value="${cart.current}"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CreateCreditCardFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/profile/AddressHolder"/>
  <dsp:importbean var="addCreditCard" bean="/atg/commerce/custsvc/ui/fragments/order/AddCreditCard"/>
  <dsp:importbean var="creditCardConfig"
                  bean="/atg/commerce/custsvc/ui/CreditCardConfiguration"/>                  
  <dsp:importbean var="creditCardForm" bean="/atg/commerce/custsvc/ui/fragments/CreditCardForm"/>

  <svc-ui:frameworkUrl var="successURL" panelStacks="cmcBillingPS,globalPanels"/>
  <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcBillingPS"/>
  <dsp:droplet name="Switch">
    <dsp:param bean="CreateCreditCardFormHandler.formError" name="value"/>
    <dsp:oparam name="false">
      <dsp:setvalue bean="CreateCreditCardFormHandler.clearCreditCard" value="true"/>
      <%-- This resets the addresses in the address holder. --%>
      <dsp:setvalue bean="AddressHolder.resetAddresses" value="true"/>
    </dsp:oparam>
  </dsp:droplet>
  <dsp:setvalue bean="CreateCreditCardFormHandler.autoSelectInitialAddress" value="true"/>
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <c:set var="formId" value="csrBillingAddCreditCard"/>
    <dsp:form method="POST" id="${formId}" formid="${formId}">

      <dsp:input type="hidden" priority="-10" value="" bean="CreateCreditCardFormHandler.newCreditCard"/>
      <dsp:input type="hidden" value="${successURL}" bean="CreateCreditCardFormHandler.newCreditCardErrorURL"/>
      <dsp:input type="hidden" value="${errorURL}" bean="CreateCreditCardFormHandler.newCreditCardSuccessURL"/>

      <%-- If the tab container is used by the billing page, then this snippet below selects the right
      tab on error. --%>
      <dsp:droplet name="Switch">
        <dsp:param bean="CreateCreditCardFormHandler.formError"
                   name="value"/>
        <dsp:oparam name="true">
          <script type="text/javascript">
            _container_.onLoadDeferred.addCallback(function () {
              var pgAddTabContainer = dijit.byId("paymentOptionAddContainer");
              if (pgAddTabContainer) pgAddTabContainer.selectChild("${creditCardConfig.type}");
            });
          </script>
        </dsp:oparam>
      </dsp:droplet>


      <div class="atg_commerce_csr_add_credit_card_form">
        <dsp:include src="${creditCardForm.URL}" otherContext="${creditCardForm.servletContext}">
          <dsp:param name="formId" value="${formId}"/>
          <dsp:param name="creditCardBean"
                     value="/atg/commerce/custsvc/order/CreateCreditCardFormHandler.creditCard"/>
          <dsp:param name="creditCardAddressBean"
                     value="/atg/commerce/custsvc/order/CreateCreditCardFormHandler.creditCard.billingAddress"/>
          <dsp:param name="creditCardFormHandler" value="/atg/commerce/custsvc/order/CreateCreditCardFormHandler"/>
          <dsp:param name="submitButtonId" value="${formId}_billingAddCreditCardButton"/>
          <dsp:param name="isMaskCardNumber" value="${false}"/>
          <dsp:param name="isUseExistingAddress" value="${true}"/>
        </dsp:include>
      
              <div class="atg_commerce_csr_saveProfile">
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param
                    bean="/atg/userprofiling/ActiveCustomerProfile.transient"
                    name="value"/>
                  <dsp:oparam name="false">
                    <dsp:input type="checkbox" checked="${true}" 
                    bean="CreateCreditCardFormHandler.copyToProfile" name="copyToProfile"/>
                    <fmt:message key="newOrderBilling.addEditCreditCard.field.card.saveToProfile"/>
                  </dsp:oparam>
                  <dsp:oparam name="true">
                    <dsp:input type="hidden" bean="CreateCreditCardFormHandler.copyToProfile" value="false"/>
                  </dsp:oparam>
                </dsp:droplet>
              </div>
              <div class="atg_commerce_csr_addControls">
                <fmt:message key="newOrderBilling.addCreditCard.header.addCreditCard" var="addCreditCardLabel"/>
                <input type="button" id="${formId}_billingAddCreditCardButton" name="billingAddCreditCardButton"
                       value="${addCreditCardLabel}"
                       onclick="${formId}HideValidationPopups();atg.commerce.csr.order.billing.saveUserInputAndAddCreditCard();return false;"
                       dojoType="atg.widget.validation.SubmitButton"/>
              </div>
       
      </div>
    </dsp:form>
    <%-- Before adding the credit card, save the user input in the page such as amount.--%>
    <script type="text/javascript">
      atg.commerce.csr.order.billing.saveUserInputAndAddCreditCard = function() {
        var returnValue = atg.commerce.csr.order.billing.saveUserInput();
        if (returnValue == true) {
          atg.commerce.csr.order.billing.addCreditCard();
        }
        ;
      }
      
      var ${formId}HideValidationPopups = function () {
          if (dojo.byId("dijit__MasterTooltip_0")) {
            dojo.byId("dijit__MasterTooltip_0").style.display="none";
          }
        };
    </script>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/addCreditCard.jsp#1 $$Change: 946917 $--%>
