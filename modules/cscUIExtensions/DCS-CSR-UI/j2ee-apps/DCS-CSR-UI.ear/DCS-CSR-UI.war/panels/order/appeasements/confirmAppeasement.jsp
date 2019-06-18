<%--
 This page defines the appeasements summary panel
 It displays the details of the appeasement including refund types (credit cards and store credit) as read only
 entries and allows the agent to continue to submit appeasement, return to the previous (appeasements.jsp) screen 
 to edit appeasement or cancel it completely, returning to the order view page.
 
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/confirmAppeasement.jsp#1 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">
  
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
  <dsp:tomap var="customerProfile" bean="/atg/userprofiling/ActiveCustomerProfile"/>
  <dsp:getvalueof var="currencyCode" bean="ShoppingCart.appeasement.originatingOrder.priceInfo.currencyCode"/>
  <dsp:getvalueof var="appeasement" bean="ShoppingCart.appeasement"/>
  
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
  <dsp:importbean bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dsp:importbean var="CSRAgentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools"/>
  
  <c:set var="currencyCode" value="${currencyCode}"/>
  <c:set var="appeasement" value="${appeasement}"/>
  <input type="hidden" id="atg_commerce_csr_order_appeasements_activeCurrencyCode" value='<dsp:valueof value="${currencyCode}" />'/>
  
  <dsp:droplet name="/atg/commerce/pricing/CurrencyDecimalPlacesDroplet">
    <dsp:param name="currencyCode" value="${currencyCode}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="currencyDecimalPlaces" param="currencyDecimalPlaces"/>
      </dsp:oparam>
  </dsp:droplet>

  <script type="text/javascript">
    atg.commerce.csr.order.billing.initializePaymentContainer("<c:out value='${appeasement.appeasementAmount}'/>", "<c:out value='${currencyCode}'/>", { "locale" : "<dsp:valueof bean='AgentUIConfiguration.javaScriptFormattingLocale' />", "currencySymbol" : "<dsp:valueof bean='CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale' />", "places" : "<c:out value='${currencyDecimalPlaces}' />"});
  </script>

  <%-- ######################### Appeasement Refund Types #################### --%>
  <div class="atg_commerce_csr_subPanel">
    <div class="atg_commerce_csr_subPanelHeader">
      <ul class="atg_commerce_csr_panelToolBar">
        <li class="atg_commerce_csr_last">

          <%-- Link to return to the order the appeasement has been applied to --%>
          <a href="#" onclick="atg.commerce.csr.order.viewExistingOrder('${appeasement.orderId}','')">
            <fmt:message key='appeasements.confirm.link.returnToOrder'>
              <fmt:param value="${appeasement.orderId}"/>
            </fmt:message>
          </a>
        </li>
      </ul>
    </div>
    
    <ul>
      <%-- Display appropriate warnings for an appeasement with a "Pending Approval" status --%>
      <c:if test="${appeasement.appeasementState == 'PENDING_APPROVAL'}">
          <li class="atg_commerce_csr_appeasementWarning">
            <img src="/agent/images/notify_icons/icon_warningSmall.gif"/>
            <fmt:message key='appeasements.confirm.message.notSubmitted'/>
          </li>
          <li>
            <fmt:message key='appeasements.confirm.message.limitExceeded'/>
          </li>
      </c:if>
      <li>
        <fmt:message key='appeasements.confirm.message.authorizationNumber'> 
          <fmt:param value="${appeasement.appeasementId}"/>
        </fmt:message>
      </li>
      
            <%-- ONLY DISPLAY FOR APPEASEMENT STATUS OF COMPLETE --%>
      <c:if test="${appeasement.appeasementState == 'COMPLETE'}">
        <li>  
          <fmt:message key='appeasements.confirm.message.paymentDetails'/><br>
          <ul class="atg_commerce_csr_appeasement_appeasementRefundList">
            
            <%-- Iterate through the refund methods and print the refund type with its associated amount for the appeasement --%>
            <c:forEach items="${appeasement.refundList}" var="refundMethod" varStatus="rmIndex">
              <li>
                <dsp:getvalueof var="pgType" value="${refundMethod.refundType}"/>
                <dsp:getvalueof var="pgTypeConfig" bean="CSRConfigurator.paymentGroupTypeConfigurationsAsMap.${pgType}"/>
                <c:if test="${pgTypeConfig != null && pgTypeConfig.displayRefundMethodPageFragment != null}">
                    <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                                 otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                        <dsp:param name="refundMethod" value="${refundMethod}"/>
                        <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                        <dsp:param name="propertyName" value="value1"/>
                        <dsp:param name="displayValue" value="${true}"/>
                    </dsp:include>
                    <csr:formatNumber value="${refundMethod.amount}" type="currency" currencyCode="${currencyCode}"/>
                </c:if>
              </li>
            </c:forEach>
          </ul>
        </li>
      </c:if>

    </ul>
    
  
      <%-- Send Confirmation bar --%>
      <div class="atg_svc_subPanelHeader" >       
        <ul class="atg_svc_panelToolBar">
          <li class="atg_svc_header">
            <h4 id="atg_commerce_csr_orderconfirm_sendConfirmation"><fmt:message key='appeasements.confirm.label.sendConfirmation'/> </h4>
              </li>       
        </ul>
      </div>
  
  
    <ul>
      <%-- Display labels for option to change email address for appeasements with "Pending Approval" states --%>
      <c:if test="${appeasement.appeasementState == 'PENDING_APPROVAL'}">
        <li>
          <fmt:message key='appeasements.confirm.message.emailAddressNotification'>
            <fmt:param value="${customerProfile.email}"/>
          </fmt:message>
        </li>
        <li>
          <fmt:message key='appeasements.confirm.message.emailAddressChange'/>
        </li>
      </c:if>
      

		  <%-- Email text box --%>
		  
		  <script type="text/javascript">
			  var validateCustomerEmail = function () {
			    var disable = false;      
			    if (!dijit.byId("atg_commerce_csr_appeasement_appeasementEmailConfirm").isValid())  disable = true;  
			    dojo.byId("csrSendAppeasementConfirmationMessage").sendConfirmEmail.disabled = disable;
			  }
			  _container_.onLoadDeferred.addCallback(function() {
			    validateCustomerEmail();
			    atg.service.form.watchInputs('csrSendAppeasementConfirmationMessage', validateCustomerEmail);
			        
			    atg.keyboard.registerDefaultEnterKey({form:"csrSendAppeasementConfirmationMessage", name:"atg_commerce_csr_confirm_toAddress"}, 
			      dijit.byNode(dojo.byId("csrSendAppeasementConfirmationMessage")["atg_commerce_csr_sendConfirmationMessageButton"]),"buttonClick");
			  });
			  _container_.onUnloadDeferred.addCallback(function() {
			    atg.service.form.unWatchInputs('csrSendAppeasementConfirmationMessage');
			    atg.keyboard.unRegisterDefaultEnterKey({form:"csrSendAppeasementConfirmationMessage", name:"atg_commerce_csr_confirm_toAddress"});
			  });
			</script>
      <li>
      
        <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels" tab="commerceTab"/>
        <svc-ui:frameworkUrl var="errorURL" panelStacks="globalPanels"/>
        <dsp:form id="csrSendAppeasementConfirmationMessage" formid="csrSendAppeasementConfirmationMessage">

          <fmt:message key='appeasements.confirm.label.emailAddress'/>

        <dsp:input 
          id="atg_commerce_csr_appeasement_appeasementEmailConfirm"
          type="text"
          bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.overrideEmailAddress"          
          name="atg_commerce_csr_appeasement_appeasementEmailConfirm"
          value="${fn:escapeXml(customerProfile.email)}"
          maxlength="100"
          size="40">  
          <dsp:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
          <dsp:tagAttribute name="validator" value="dojox.validate.isEmailAddress"/>
          <dsp:tagAttribute name="required" value="true" />
          <dsp:tagAttribute name="trim" value="true" />    
          <dsp:tagAttribute name="class" value="input-large" />    
        </dsp:input> 
        
          <%-- Send Button --%>
            <dsp:input name="successURL" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.sendConfirmationMessageSuccessURL" value="${successURL}" type="hidden" />
            <dsp:input type="hidden" priority="-11" value="${appeasement}" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.appeasement"/>
			<dsp:input name="errorURL" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.sendConfirmationMessageErrorURL"  value="${errorURL}" type="hidden" />
            <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.sendConfirmationMessage"/>
            <input id="sendConfirmEmail" type="button" 
                   name="atg_commerce_csr_sendConfirmationMessageButton" 
                   onclick="atg.commerce.csr.order.appeasement.sendConfirmationMessage();return false;"
                   dojoType="atg.widget.validation.SubmitButton"
                   value="<fmt:message key='appeasements.confirm.button.sendEmail'/>"
                   tabindex="10"/>
        </dsp:form>
      </li>
    </ul>
 
 
   </div>
  </dsp:layeredBundle>

  <script type="text/javascript">
    atg.progress.update('cmcConfirmAppeasementPS');
  </script>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/confirmAppeasement.jsp#1 $$Change: 1179550 $--%>
