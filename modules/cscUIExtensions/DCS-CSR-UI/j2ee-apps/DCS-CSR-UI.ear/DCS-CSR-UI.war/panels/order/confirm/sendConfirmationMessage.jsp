<%--
This file displays a panel that allows the user to enter a 
To Address and send a confirmation message. If the current
customer profile has an email address, the email address
field is pre-populated with that value.

Order - the current order.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/sendConfirmationMessage.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/CommitOrderFormHandler"
   var="commitOrderFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/repository/CustSvcRepositoryItemServlet"/>
  <dsp:importbean bean="/atg/userprofiling/ActiveCustomerProfile" var="activeCustomerProfile"/>    
  
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  <dsp:getvalueof var="confirmInfo" param="confirmationInfo"/>
  <dsp:getvalueof var="order" value="${confirmationInfo.order}"/>
  
  <%-- Create success and error urls for Confirmation Message --%>
  <svc-ui:frameworkUrl var="successURL"/>
  <svc-ui:frameworkUrl var="errorURL"/>
  <fmt:message key="confirmOrder.confirmationMessage.noAddress.successMessage" 
               var="confirmationMsg"/>
                                        
  <ul class="atg_dataForm atg_commerce_csr_emailConfirmationForm">
    <dsp:form
      id="atg_commerce_csr_sendConfirmationMessageForm"
      onsubmit="return false"
      formid="atg_commerce_csr_sendConfirmationMessageForm"> 
      <li>
        <label for="emailAddress"><span class="requiredStar">*</span>
        <fmt:message key='confirmOrder.confirmationMessage.field.email'/></label>
      
      
        <script type="text/javascript">
          var validateCustomerEmail = function () {
            var disable = false;      
            if (!dijit.byId("confirmEmail").isValid())  disable = true;  
            dojo.byId("atg_commerce_csr_sendConfirmationMessageForm").sendConfirmEmail.disabled = disable;
          }
          _container_.onLoadDeferred.addCallback(function() {
            validateCustomerEmail();
            atg.service.form.watchInputs('atg_commerce_csr_sendConfirmationMessageForm', validateCustomerEmail);
                
            atg.keyboard.registerDefaultEnterKey({form:"atg_commerce_csr_sendConfirmationMessageForm", name:"atg_commerce_csr_confirm_toAddress"}, 
              dijit.byNode(dojo.byId("atg_commerce_csr_sendConfirmationMessageForm")["atg_commerce_csr_sendConfirmationMessageButton"]),"buttonClick");
          });
          _container_.onUnloadDeferred.addCallback(function() {
            atg.service.form.unWatchInputs('atg_commerce_csr_sendConfirmationMessageForm');
            atg.keyboard.unRegisterDefaultEnterKey({form:"atg_commerce_csr_sendConfirmationMessageForm", name:"atg_commerce_csr_confirm_toAddress"});
          });
        </script>
        <input id="atg.successMessage" name="atg.successMessage" type="hidden"
                   value="${confirmationMsg}"/>    
        <dsp:input type="hidden" priority="-10" value=""
              bean="CommitOrderFormHandler.sendConfirmationMessage" />        
        <dsp:input type="hidden" value="${errorURL }"
                bean="CommitOrderFormHandler.commitOrderUpdatesErrorURL" />          
        <dsp:input type="hidden" value="${successURL }"
          bean="CommitOrderFormHandler.commitOrderUpdatesSuccessURL" /> 
        
     
        
        <dsp:tomap var="customerProfile" value="${confirmInfo.profile}" />      
        <%-- If the Profile has an address, put it in the Email Address field. --%>  
        <dsp:input 
          id="confirmEmail"
          type="text"
          bean="CommitOrderFormHandler.confirmationInfo.toEmailAddress"          
          name="atg_commerce_csr_confirm_toAddress"
          value="${fn:escapeXml(order.contactEmail)}"
          maxlength="100"
          size="40">  
          <dsp:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
          <dsp:tagAttribute name="validator" value="dojox.validate.isEmailAddress"/>
          <dsp:tagAttribute name="required" value="true" />
          <dsp:tagAttribute name="trim" value="true" />
          <dsp:tagAttribute name="invalidMessage" value="Invalid Email Address." />            
        </dsp:input>                      
        <input id="sendConfirmEmail" type="button" 
          name="atg_commerce_csr_sendConfirmationMessageButton" 
          onclick="atg.commerce.csr.order.confirm.sendConfirmationMessage();return false;"
          dojoType="atg.widget.validation.SubmitButton"
          value="<fmt:message key='confirmOrder.confirmationMessage.send.display'/>"
          tabindex="10"/>

    </dsp:form>  <%-- End Send Confirmation Form --%>
  </li>
  </ul>  
  </dsp:layeredBundle>      
</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/sendConfirmationMessage.jsp#1 $$Change: 946917 $--%>
