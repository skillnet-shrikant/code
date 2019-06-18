<%--
During the checkout flow or process, this page is used to edit credit cards.
The updated credit card information could be saved in the profile or may be only used in the current order.

Anonymous users will not see the option to save the credit card to the profile.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/editCreditCard.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/userprofiling/ActiveCustomerProfile"/>
  <dsp:importbean var="CSRUpdateCreditCardFormHandler"
                  bean="/atg/commerce/custsvc/order/UpdateCreditCardFormHandler"/>
  <dsp:importbean var="urlDroplet"
                  bean="/atg/svc/droplet/FrameworkUrlDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  <dsp:importbean var="pageFragment"
                  bean="/atg/commerce/custsvc/ui/fragments/order/EditCreditCard"/>
  <dsp:importbean var="pgConfig"
                  bean="/atg/commerce/custsvc/ui/CreditCardConfiguration"/>
  <dsp:importbean var="creditCardForm" bean="/atg/commerce/custsvc/ui/fragments/CreditCardForm"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
  <c:set var="currentOrder" value="${cart.current}"/>

  <dsp:getvalueof var="nickname" param="nickname"/>
  <dsp:getvalueof var="success" param="success"/>

  <c:url var="successErrorURL" context="/${pgConfig.editPageFragment.servletContext}"
         value="${pgConfig.editPageFragment.URL}">
    <c:param name="nickname" value="${nickname}"/>
    <c:param name="${stateHolder.windowIdParameterName}"
             value="${windowId}"/>
    <c:param name="success" value="true"/>
  </c:url>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <c:set var="formId" value="csrBillingEditCreditCard"/>
    <dsp:form method="POST" id="${formId}" formid="${formId}">
      <dsp:input type="hidden" priority="-10" value=""
                 bean="UpdateCreditCardFormHandler.updateCreditCard"/>
      <dsp:input type="hidden" value="${successErrorURL }"
                 bean="UpdateCreditCardFormHandler.updateCreditCardErrorURL"/>
      <dsp:input type="hidden" value="${successErrorURL }"
                 bean="UpdateCreditCardFormHandler.updateCreditCardSuccessURL"/>
      <dsp:input type="hidden" value="${fn:escapeXml(nickname) }"
                 bean="UpdateCreditCardFormHandler.creditCardName"/>
      <dsp:input type="hidden" value="${fn:escapeXml(nickname) }"
                 bean="UpdateCreditCardFormHandler.creditCardByNickname" priority="5"/>
      <dsp:setvalue bean="UpdateCreditCardFormHandler.creditCardByNickname" value="${fn:escapeXml(nickname) }"/>

      <div id="editCreditCardPagePrompt" class="atg_commerce_csr_popupPanel">
      <dsp:layeredBundle basename="${pgConfig.resourceBundle}">
       <fmt:message var="editPageFragmentTitle" key="${pgConfig.editPageFragmentTitleKey}"/>
      </dsp:layeredBundle>
        <div>
          <dsp:droplet name="Switch">
            <dsp:param bean="UpdateCreditCardFormHandler.formError"
                       name="value"/>
            <dsp:oparam name="true">

              <span class="atg_commerce_csr_common_content_alert"><fmt:message key="common.error.header"/></span>
              <br>
            <span class="atg_commerce_csr_common_content_alert">
            <ul>
              <dsp:droplet name="ErrorMessageForEach">
                <dsp:param bean="UpdateCreditCardFormHandler.formExceptions"
                           name="exceptions"/>
                <dsp:oparam name="output">
                  <li>
                    <dsp:valueof param="message"/>
                  </li>
                </dsp:oparam>
              </dsp:droplet>
            </ul>
            </span>
            </dsp:oparam>
            <dsp:oparam name="false">
              <c:if test="${success}">
                <%--When there is no error on the page submission, close the popup page and refresh the parent page.
                the parent page only will refresh if the result parameter value is ok. --%>
                <script type="text/javascript">
                  hidePopupWithResults('editCreditCardPagePrompt', {result : 'ok'});
                </script>
              </c:if>
            </dsp:oparam>
          </dsp:droplet>
        </div>

						  <c:set var="isSubmitted" value="${false}"/>
						  <dsp:droplet name="/atg/commerce/custsvc/order/IsOrderSubmitted">
						    <dsp:param name="order" value="${currentOrder}"/>
						    <dsp:oparam name="true">
						      <c:set var="isSubmitted" value="${true}"/>
						    </dsp:oparam>
						  </dsp:droplet>

        <ul class="atg_dataForm atg_commerce_csr_paymentForm">
         <dsp:include src="${creditCardForm.URL}" otherContext="${creditCardForm.servletContext}">
            <dsp:param name="formId" value="${formId}"/>
            <dsp:param name="creditCardBean"
                       value="/atg/commerce/custsvc/order/UpdateCreditCardFormHandler.workingCreditCard"/>
            <dsp:param name="creditCardAddressBean"
                       value="/atg/commerce/custsvc/order/UpdateCreditCardFormHandler.newAddress"/>
            <dsp:param name="creditCardFormHandler" value="/atg/commerce/custsvc/order/UpdateCreditCardFormHandler"/>
            <dsp:param name="submitButtonId" value="billingEditCreditCardButton"/>
            <dsp:param name="isMaskCardNumber" value="${true}"/>
            <dsp:param name="isUseExistingAddress" value="${true}"/>
            <dsp:param name="disableCreditCardType" value="${isSubmitted}"/>
            <dsp:param name="disableCreditCardNumber" value="${isSubmitted}"/>
          </dsp:include>
        </ul>
        <div class="atg_commerce_csr_panelFooter">
          <dsp:droplet name="/atg/dynamo/droplet/Switch">
            <dsp:param bean="UpdateCreditCardFormHandler.creditCardExistsInProfile"
                       name="value"/>
            <dsp:oparam name="true">
              <dsp:droplet name="/atg/dynamo/droplet/Switch">
                <dsp:param
                  bean="/atg/userprofiling/ActiveCustomerProfile.transient"
                  name="value"/>
                <dsp:oparam name="false">
                  <div style="float:left;margin-left:20px">
                    <dsp:input type="checkbox" checked="${true}"
                               bean="UpdateCreditCardFormHandler.updateProfile" name="updateToProfile"/>
                    <fmt:message key="newOrderBilling.addEditCreditCard.field.card.saveToProfile"/>
                  </div>  
                </dsp:oparam>
                <dsp:oparam name="true">
                  <dsp:input type="hidden" bean="UpdateCreditCardFormHandler.updateProfile" value="false"/>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
            <dsp:oparam name="false">
              <dsp:input type="hidden" bean="UpdateCreditCardFormHandler.updateProfile" value="false"/>
            </dsp:oparam>
          </dsp:droplet>
          <input type="hidden" name="_DARGS"
                 value="${CSRConfigurator.contextRoot}/panels/order/billing/editCreditCard.jsp.csrBillingEditCreditCard"/>
          <input type="button" id="billingEditCreditCardButton"
                 value="<fmt:message key='common.save.title'/>"
                 onclick="atg.commerce.csr.order.billing.editCreditCard('${successErrorURL}');"
                 dojoType="atg.widget.validation.SubmitButton"/>
          <input type="button" value="<fmt:message key='common.cancel.title'/>"
                 onclick="hidePopupWithResults( 'editCreditCardPagePrompt', {result : 'cancel'});return false;"/>
        </div>
      </div>
    </dsp:form>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/editCreditCard.jsp#1 $$Change: 946917 $--%>
