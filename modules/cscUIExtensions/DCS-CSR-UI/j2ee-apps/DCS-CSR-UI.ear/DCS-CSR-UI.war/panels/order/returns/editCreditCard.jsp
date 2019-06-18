<%--
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/editCreditCard.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
    <dsp:importbean bean="/atg/userprofiling/ActiveCustomerProfile"/>
    <dsp:importbean var="CSRUpdateCreditCardFormHandler"
      bean="/atg/commerce/custsvc/order/UpdateCreditCardFormHandler" />
    <dsp:importbean var="urlDroplet"
      bean="/atg/svc/droplet/FrameworkUrlDroplet" />
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
    <dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" />
    <dsp:importbean var="pgConfig"
                  bean="/atg/commerce/custsvc/ui/CreditCardConfiguration"/>
    <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler"/>
    <dsp:importbean var="creditCardForm" bean="/atg/commerce/custsvc/ui/fragments/CreditCardForm"/>

    <dsp:getvalueof var="refundMethodIndex" param="refundMethodIndex" />
    <dsp:getvalueof var="refundMethod" bean="ReturnFormHandler.sortedRefundMethodList[param:refundMethodIndex]" />
    
    <c:set var="paymentGroupId" value="${refundMethod.creditCard.id}"/>
    
    <dsp:getvalueof var="success" param="success"/>
    <dsp:getvalueof var="returnRequest" bean="ShoppingCart.returnRequest"/>

    <c:url var="successErrorURL" context="/${pgConfig.editRefundMethodPageFragment.servletContext}"
      value="${pgConfig.editRefundMethodPageFragment.URL}">
      <c:param name="refundMethodIndex" value="${refundMethodIndex}" />
      <c:param name="${stateHolder.windowIdParameterName}"
        value="${windowId}" />
      <c:param name="success" value="true" />
    </c:url>

    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

      <c:set var="formId" value="csrEditCreditCard"/>
      <dsp:form method="POST" action="#" id="${formId}" formid="${formId}">
        <dsp:input type="hidden" priority="-10" value=""
          bean="UpdateCreditCardFormHandler.updateCreditCard" />

        <dsp:input type="hidden" value="${successErrorURL }"
          bean="UpdateCreditCardFormHandler.updateCreditCardErrorURL" />

        <dsp:input type="hidden" value="${successErrorURL }"
          bean="UpdateCreditCardFormHandler.updateCreditCardSuccessURL" />

        <c:if test="${!empty returnRequest && !empty returnRequest.order}">
          <dsp:setvalue bean="UpdateCreditCardFormHandler.workingOrderId" value="${returnRequest.order.id }"/>
          <dsp:input type="hidden" value="${returnRequest.order.id }" bean="UpdateCreditCardFormHandler.workingOrderId" priority="10"/>
        </c:if>

        <dsp:input type="hidden" value="${paymentGroupId }"
          bean="UpdateCreditCardFormHandler.creditCardByPaymentGroupId" priority="5"/>

        <dsp:input type="hidden" value="false"  bean="UpdateCreditCardFormHandler.updateContainer" />
        <dsp:input type="hidden" value="false"  bean="UpdateCreditCardFormHandler.updateProfile" />


        <dsp:setvalue bean="UpdateCreditCardFormHandler.creditCardByPaymentGroupId" value="${paymentGroupId }"/>

        <div id="editCreditCardPagePrompt" class="atg_commerce_csr_popupPanel">

         <dsp:layeredBundle basename="${pgConfig.resourceBundle}">
          <fmt:message var="editPageFragmentTitle" key="${pgConfig.editRefundMethodPageFragmentTitleKey}"/>
         </dsp:layeredBundle>
      
         
        <div><dsp:droplet name="Switch">
          <dsp:param bean="UpdateCreditCardFormHandler.formError"
            name="value" />
          <dsp:oparam name="true">
          &nbsp;<br/><br/>
            <span class="atg_commerce_csr_common_content_alert"><fmt:message key="common.error.header" /></span>
            <br>
            <span class="atg_commerce_csr_common_content_alert">
            <ul>
              <dsp:droplet name="ErrorMessageForEach">
                <dsp:param bean="UpdateCreditCardFormHandler.formExceptions"
                  name="exceptions" />
                <dsp:oparam name="output">
                  <li><dsp:valueof param="message" /></li>
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
                hidePopupWithResults( 'editCreditCardPagePrompt', {result : 'ok'});
              </script>
            </c:if>
          </dsp:oparam>
        </dsp:droplet></div>

        <script type="text/javascript">
          _container_.onLoadDeferred.addCallback(function(){
               var ccType = dijit.byId('${formId}_creditCardType');
               if (ccType) ccType.setDisabled(true);
               atg.commerce.csr.common.disableTextboxWidget('${formId}_maskedCreditCardNumber');
          });
        </script>


        <ul class="atg_dataForm atg_commerce_csr_paymentForm">
          <dsp:include src="${creditCardForm.URL}" otherContext="${creditCardForm.servletContext}">
            <dsp:param name="formId" value="${formId}"/>
            <dsp:param name="creditCardBean" value="/atg/commerce/custsvc/order/UpdateCreditCardFormHandler.workingCreditCard"/>
            <dsp:param name="creditCardAddressBean" value="/atg/commerce/custsvc/order/UpdateCreditCardFormHandler.newAddress"/>
            <dsp:param name="creditCardFormHandler" value="/atg/commerce/custsvc/order/UpdateCreditCardFormHandler"/>
            <dsp:param name="submitButtonId" value="billingEditCreditCardButton"/>
            <dsp:param name="isMaskCardNumber" value="${true}"/>
            <dsp:param name="isUseExistingAddress" value="${true}"/>
          </dsp:include>
        </ul>
        <div class="atg_commerce_csr_panelFooter">
        <input type="button" id="billingEditCreditCardButton"
          value="<fmt:message key='common.save.title'/>"
          onclick="atg.commerce.csr.order.returns.editCreditCard('${successErrorURL}');"
          dojoType="atg.widget.validation.SubmitButton"/>
          <input type="button" value="<fmt:message key='common.cancel.title'/>" onclick="hidePopupWithResults( 'editCreditCardPagePrompt', {result : 'cancel'});return false;" />
        </div>
        </div>
        </dsp:form>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/editCreditCard.jsp#1 $$Change: 946917 $--%>
