<%--
 This page defines the returns panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/appeasement.jsp#1 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">
   
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ViewOrderHolder" var="viewOrder"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  
  <dsp:importbean bean="/atg/commerce/custsvc/appeasement/StartAppeasementFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler"/>
  
  <dsp:importbean bean="/atg/commerce/custsvc/profile/AddressHolder"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
  <dsp:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dsp:importbean var="CSRAgentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools"/>
  
  <dsp:importbean bean="/atg/commerce/custsvc/appeasement/GetAppeasementOrderBalanceDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/appeasement/GetAppeasementShippingBalanceDroplet"/>
  <dsp:importbean bean="/com/mff/commerce/csr/order/appeasement/GetAppeasementTaxBalanceDroplet"/>
  <dsp:getvalueof var="currencyCode" bean="ShoppingCart.appeasement.originatingOrder.priceInfo.currencyCode"/>
  <dsp:getvalueof var="appeasement" bean="ShoppingCart.appeasement"/>
  <c:set var="currencyCode" value="${currencyCode}"/>
  <c:set var="appeasement" value="${appeasement}"/>
  <input type="hidden" id="atg_commerce_csr_order_appeasements_activeCurrencyCode" value='<dsp:valueof value="${currencyCode}" />'/>
  
  <dsp:droplet name="/atg/commerce/pricing/CurrencyDecimalPlacesDroplet">
    <dsp:param name="currencyCode" value="${currencyCode}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="currencyDecimalPlaces" param="currencyDecimalPlaces"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <c:set var="order" value="${viewOrder.current}"/>
    
  <script type="text/javascript">
    atg.commerce.csr.order.billing.initializePaymentContainer("<c:out value='${appeasement.appeasementAmount}'/>", "<c:out value='${currencyCode}'/>", { "locale" : "<dsp:valueof bean='AgentUIConfiguration.javaScriptFormattingLocale' />", "currencySymbol" : "<dsp:valueof bean='CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale' />", "places" : "<c:out value='${currencyDecimalPlaces}' />"});
  </script>

  <ul class="atg_commerce_csr_panelToolBar">
    <li class="atg_commerce_csr_return">
      <dsp:include src="/include/order/returnPreviousPage.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="order" value="${order}"/>
      </dsp:include>
    </li>
  </ul>
  
  <input type="hidden" id="atg_commerce_csr_order_appeasements_activeCurrencyCode" value='<dsp:valueof value="${currencyCode}" />'/>
  
  <div class="atg_commerce_csr_corePanelData">
    <b><fmt:message key="appeasements.orderTotals" /></b>
    <div>
     <span>
       <b><fmt:message key="appeasements.itemsTotal" /></b>
       <dsp:droplet var="items" name="/atg/commerce/custsvc/appeasement/GetAppeasementOrderBalanceDroplet">
         <dsp:param name="orderId" value="${order.id}"/>
         <dsp:setvalue param="itemBalance" paramvalue="element"/>
         <dsp:oparam name="output">
           <dsp:getvalueof var="itemBalance" param="itemBalance"/>      
         </dsp:oparam>
       </dsp:droplet>
       <input type="hidden" value="${itemBalance}" id="itemBalance"/>
       <csr:formatNumber value="${itemBalance}" type="currency" currencyCode="${currencyCode}" />
     </span> &nbsp;
     <span>
       <b><fmt:message key="appeasements.shippingTotal" /></b>
       <dsp:droplet name="/atg/commerce/custsvc/appeasement/GetAppeasementShippingBalanceDroplet">
         <dsp:param name="orderId" value="${order.id}"/>
         <dsp:setvalue param="shippingBalance" paramvalue="element"/>
         <dsp:oparam name="output">
           <dsp:getvalueof var="shippingBalance" param="shippingBalance"/>
         </dsp:oparam>
       </dsp:droplet>
       <input type="hidden" value="${shippingBalance}" id="shippingBalance"/>
       <csr:formatNumber value="${shippingBalance}" type="currency" currencyCode="${currencyCode}" />
     </span>
     <span>
     	<b><fmt:message key="appeasements.taxesTotal" /></b>
     	<dsp:droplet name="GetAppeasementTaxBalanceDroplet">
     		<dsp:param name="orderId" value="${order.id}"/>
     		<dsp:setvalue param="taxBalance" paramvalue="element"/>
     		<dsp:oparam name="output">
     			<dsp:getvalueof var="taxBalance" param="taxBalance"/>
     		</dsp:oparam>
     	</dsp:droplet>
     	<input type="hidden" value="${taxBalance}" id="taxBalance"/>
     	<csr:formatNumber value="${taxBalance}" type="currency" currencyCode="${currencyCode}" />
     </span>      
    </div>
  </div>
   
  <div class="atg_commerce_csr_corePanelData">
    <b><fmt:message key="appeasements.appeasementSelectons.label"/></b>
    <div>
      <table class="atg_dataTable atg_commerce_csr_innerTable">

        <%-- Items/Shipping Appeasement Type Dropdown --%>
        <dsp:form method="post" id="appeasementTypeSelectForm" formid="appeasementTypeSelectForm"> 
          <dsp:select bean="AppeasementFormHandler.appeasement.appeasementType" 
          			  iclass="input-medium"  
          			  id="atg_commerce_csr_appeasement_appeasementType" 
          			  onchange="atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount();">
            <dsp:option selected="${true}" value="items"><fmt:message key="appeasements.typeDropdown.newitems"/></dsp:option>
            <dsp:option value="shipping"><fmt:message key="appeasements.typeDropdown.shipping"/></dsp:option>
            <dsp:option value="taxes"><fmt:message key="appeasements.typeDropdown.tax"/></dsp:option>
            <dsp:option value="appreciation"><fmt:message key="appeasements.typeDropdown.appreciation"/></dsp:option>
          </dsp:select>
    
          <%-- Amount/Percentage off Radio button group --%>
          <span class="atg_commerce_csr_extendedFieldTitle">          
            <label>
            
              <input type="radio" name="atg_commerce_csr_appeasement_appeasementAmountorPercentRadio" 
              					  id="atg_commerce_csr_appeasement_appeasementAmountRadio" 
              					  onclick="atg.commerce.csr.order.appeasement.setAppeasementValues(this); atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount();" 
              					  value="selectAmountOff" 
              					  checked="true">
              <fmt:message key="appeasements.amountOff"/>
            </label>&nbsp;          
            <%--<label>
              <input type="radio" name="atg_commerce_csr_appeasement_appeasementAmountorPercentRadio" id="atg_commerce_csr_appeasement_appeasementPercentageRadio" onclick="atg.commerce.csr.order.appeasement.setAppeasementValues(this); atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount;" value="selectPercentageOff">
              <fmt:message key="appeasements.percentageOff"/>
            </label>&nbsp;--%>
            
            <%-- Hidden field to hold whether the amount or percentage discount type was selected  --%>
            <input type="hidden" id="atg_commerce_csr_appeasement_amountOrPercentageSelectedValue"/>
             
            <div id="atg_commerce_csr_appeasement_appeasementAmountInputDiv" style="display: inline-block">
              <fmt:message key="appeasements.invalidAppeasementAmount" var="invalidAppeasementAmount"/>
              <fmt:message key="appeasements.requiredAppeasementAmount" var="requiredAppeasementAmount"/>
            
              <span id="atg_commerce_csr_appeasement_appeasementAmountAlert" class="atg_messaging_requiredIndicator">
                <dsp:input bean="AppeasementFormHandler.appeasement.appeasementAmount" name="atg_commerce_csr_appeasement_appeasementAmountValue" id="atg_commerce_csr_appeasement_appeasementAmountValue" type="text">
                  <dsp:tagAttribute name="dojoType" value="atg.widget.validation.CurrencyTextboxEx"/>
                  <dsp:tagAttribute name="required" value="true"/>
                  <dsp:tagAttribute name="trim" value="true"/>
                  <dsp:tagAttribute name="invalidMessage" value="${invalidAppeasementAmount}"/>
                  <dsp:tagAttribute name="missingMessage" value="${requiredAppeasementAmount}"/>
                  <dsp:tagAttribute name="inlineIndicator" value="atg_commerce_csr_appeasement_appeasementAmountAlert"/>
                  <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
                  <dsp:tagAttribute name="currency" value="${currencyCode}"/>
                  <dsp:tagAttribute name="currencySymbol" value="${CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale}"/>
                  <dsp:tagAttribute name="constraints" value="{min: 0,places:${currencyDecimalPlaces}}"/>
                </dsp:input>
              </span>
            </div>
            
            <div id="atg_commerce_csr_appeasement_appeasementPercentInputDiv" style="display: none;">
              <fmt:message key="appeasements.invalidAppeasementPercent" var="invalidAppeasementPercent"/>
              <fmt:message key="appeasements.requiredAppeasementPercent" var="requiredAppeasementPercent"/>
            
              <span id="atg_commerce_csr_appeasement_appeasementPercentAlert" class="atg_messaging_requiredIndicator">
                <dsp:input bean="AppeasementFormHandler.percentageAppeasementAmount" name="atg_commerce_csr_appeasement_appeasementPercentValue" id="atg_commerce_csr_appeasement_appeasementPercentValue" type="text">
                  <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox"/>
                  <dsp:tagAttribute name="required" value="true"/>
                  <dsp:tagAttribute name="trim" value="true"/>
                  <dsp:tagAttribute name="invalidMessage" value="${invalidAppeasementPercent}"/>
                  <dsp:tagAttribute name="missingMessage" value="${requiredAppeasementPercent}"/>
                  <dsp:tagAttribute name="inlineIndicator" value="atg_commerce_csr_appeasement_appeasementPercentAlert"/>
                  <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
                  <dsp:tagAttribute name="constraints" value="{min: 0, max: 100}"/>
                                                                          
                </dsp:input>
              </span>
            </div>
            
            <input type="hidden" id="atg_commerce_csr_appeasement_appeasementResult" />
            
          </span>
          
          <script type="text/javascript">
          _container_.onLoadDeferred.addCallback(function() {
              
        	  atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount;
              
             if(window.appeasementAmountBlurConnect) {
                // remove old connection to avoid memory leaks
                dojo.disconnect(window.appeasementAmountBlurConnect);
                dojo.disconnect(window.appeasementAmountKeyUpConnect);
                dojo.disconnect(window.appeasementAmountKeyPressConnect);
              }
             if(window.appeasementPercentBlurConnect) {
               // remove old connection to avoid memory leaks
                dojo.disconnect(window.appeasementPercentBlurConnect);
                dojo.disconnect(window.appeasementPercentKeyUpConnect);
                dojo.disconnect(window.appeasementPercentKeyPressConnect);
              }
              if (dojo.byId("atg_commerce_csr_appeasement_appeasementAmountValue")) {
               // Connect to events on the amount input text area so that the user input can be validated.
                window.appeasementAmountBlurConnect = dojo.connect(dojo.byId("atg_commerce_csr_appeasement_appeasementAmountValue"), "onblur", atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount);
                window.appeasementAmountKeyUpConnect = dojo.connect(dojo.byId("atg_commerce_csr_appeasement_appeasementAmountValue"), "onkeyup", atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount);
                window.appeasementAmountKeyPressConnect = dojo.connect(dojo.byId("atg_commerce_csr_appeasement_appeasementAmountValue"), "onkeypress", atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount);
              }
              if(dojo.byId("atg_commerce_csr_appeasement_appeasementPercentValue")){
             // Connect to events on the percent input text area so that the user input can be validated.
                window.appeasementPercentBlurConnect = dojo.connect(dojo.byId("atg_commerce_csr_appeasement_appeasementPercentValue"), "onblur", atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount);
                window.appeasementPercentKeyUpConnect = dojo.connect(dojo.byId("atg_commerce_csr_appeasement_appeasementPercentValue"), "onkeyup", atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount);
                window.appeasementPercentKeyPressConnect = dojo.connect(dojo.byId("atg_commerce_csr_appeasement_appeasementPercentValue"), "onkeypress", atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount);
              }
            });
          </script>

          <%-- Reason Dropdown --%>
          <dsp:select bean="AppeasementFormHandler.appeasement.reasonCode" id="atg_commerce_csr_appeasement_appeasementReasonCode" onchange="atg.commerce.csr.order.appeasement.mffDisplayAppeasementAmount();">
          
            <%-- If an appeasement already exists then make sure it is selected --%>
            <c:choose>
              <c:when test="${empty appeasement.reasonCode}">
                <dsp:option selected="${true}" value="" >
                  <fmt:message key="appeasements.reasonDropdown.select"/>
                </dsp:option>
              </c:when>
            </c:choose>
            
            <dsp:droplet name="ForEach">
              <dsp:param bean="AppeasementFormHandler.reasonCodes" name="array"/>
              <dsp:param name="elementName" value="reasonCode"/>
              <dsp:param name="sortProperties" value="+description"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="elementReasonCode" param="reasonCode"/>
                <dsp:option selected="${elementReasonCode.repositoryId == appeasement.reasonCode}" paramvalue="reasonCode.repositoryId">
                  <dsp:valueof param="reasonCode.readableDescription"/>
                </dsp:option>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:select>&nbsp;
          
          <%-- Comments textbox --%>
          <fmt:message key="appeasements.comments"/>&nbsp;
          <input type="text" name="atg_commerce_csr_appeasement_appeasementsCommentsId" value="${appeasement.appeasementNotes}" id="atg_commerce_csr_appeasement_appeasementsCommentsId" class="input-large"/>
          
        </dsp:form>
      </table>
    </div>
    
    <%-- Apply Button and total --%>
    <div class="pull-right">
      <span class="atg_commerce_csr_appeasement_appeasementTotal">
        <b><fmt:message key="appeasements.appeasementTotal"/></b>
        <span id="atg_commerce_csr_appeasement_total">
          <input type="hidden" value="" id="appeasementTotal"/>
        </span>
      </span>
      
      <%-- Output the appeasement total - if percentage is selected, calculate from FH --%>
      
      <svc-ui:frameworkUrl var="successURL" panelStacks="cmcAppeasementsPS,globalPanels" tab="commerceTab"/>
      <svc-ui:frameworkUrl var="errorURL" panelStacks="globalPanels"/>
      <dsp:form id="atg_commerce_csr_updateAppeasement" formid="atg_commerce_csr_updateAppeasement">
        <dsp:input name="successURL" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.successURL" value="${successURL}" type="hidden" />
        <dsp:input name="errorURL" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.errorURL"  value="${errorURL}" type="hidden" />
        <dsp:input type="hidden" id="atg_commerce_csr_appeasement_updateAppeasementType" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.appeasement.appeasementType" value=""/>
        <dsp:input type="hidden" id="atg_commerce_csr_appeasement_updateAppeasementAmount" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.appeasement.appeasementAmount" value=""/>
        <dsp:input type="hidden" id="atg_commerce_csr_appeasement_updateAppeasementReasonCode" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.appeasement.reasonCode" value=""/>
        <dsp:input type="hidden" id="atg_commerce_csr_appeasement_updateAppeasementNotes" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.appeasement.appeasementNotes" value=""/>
        <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.applyAppeasementRefunds"/>
  
        <input id="applyAppeasementValuesButton" type="button" 
               onclick="atg.commerce.csr.order.appeasement.applyAppeasementRefundValues();"
               value="<fmt:message key="appeasements.button.apply" />"
               dojoType="atg.widget.validation.SubmitButton"
               disabled="disabled"/>
      </dsp:form>
      
    </div>
  </div>
   
  
    
  <%-- start section for appeasement refund types --%>
  <div class="atg_commerce_csr_corePanelData">
  <svc-ui:frameworkUrl var="successURL" panelStacks="cmcAppeasementSummaryPS,globalPanels" tab="commerceTab"/>
  <svc-ui:frameworkUrl var="errorURL" panelStacks="globalPanels"/>
  <dsp:form id="csrApplyAppeasements" formid="csrApplyAppeasements" method="post">

    <dsp:input bean="AppeasementFormHandler.validateAppeasementValuesSuccessURL" value="${successURL}" type="hidden"/>
    <dsp:input bean="AppeasementFormHandler.validateAppeasementValuesErrorURL" value="${errorURL}" type="hidden"/>
    <dsp:input name="handleApplyAppeasement" bean="AppeasementFormHandler.validateAppeasementValues" type="hidden" priority="-10" value=""/>
    
    <dsp:input bean="AppeasementFormHandler.cancelAppeasementSuccessURL" value="${cancelAppeasementSuccessURL}"
               type="hidden"/>
    <dsp:input bean="AppeasementFormHandler.cancelAppeasementErrorURL" value="${errorURL}" type="hidden"/>
    <dsp:input name="handleCancelAppeasement" bean="AppeasementFormHandler.cancelAppeasement" type="hidden" priority="-10"
               value=""/>
    
    <%-- We need to reset for the edit credit card page. This resets the addresses in the address holder. --%>
    <dsp:setvalue bean="AddressHolder.resetAddresses" value="true"/>
    
    

    <%--This variable is to keep track of the payment type that is being served. We need to display the
    the headings for the each payment type, then serve the payment options of that type. This variable is
    used to keep track of the previous type is served and if the type changes, then we need to display the heading
    for the new type. --%>
    
    <c:set var="previousPGType" value=""/>
    
    <%-- loop through all of the refund methods --%>
    <dsp:droplet name="ForEach">
      <dsp:param bean="AppeasementFormHandler.sortedRefundList" name="array"/>
      <dsp:param name="elementName" value="refundMethod"/>
      <dsp:param name="sortProperties" value="refundType"/>
    
      <dsp:oparam name="outputStart">
        <b><fmt:message key="appeasements.appeasementCreditTypes.label"/></b>
      </dsp:oparam>
  
      <%-- Render the refund method --%>
      <dsp:oparam name="output">
        <dsp:getvalueof var="refundMethod" param="refundMethod"/>
        <dsp:getvalueof var="index" param="index"/>
        <dsp:getvalueof var="pgType" value="${refundMethod.refundType}"/>
    
        <%--
        If the payment option amount to be restricted for a maximum payment amount,
        then the maxAllowedAmount should be passed in and payment option amount could
        be assigned to the maximum amount.
        --%>
        <%--
        The index is used as the unique identifier to hold the refund type information.
        The same index is used as the widgetId for the input amount field.
        --%>
    
        <dsp:getvalueof var="maxAllowedAmount" param="refundMethod.maximumRefundAmount" vartype="java.lang.String"/>
        <c:set var="rmWidgetId" value="refundMethod_${index}"/>
        
        <c:choose>
        <c:when test="${!empty maxAllowedAmount && maxAllowedAmount != '-1.0' }">
          <script type="text/javascript">
            _container_.onLoadDeferred.addCallback(function() {
              atg.commerce.csr.order.billing.addPaymentMethod({
                paymentGroupId: '${rmWidgetId}',
                amount: '${refundMethod.amount}',
                maxAllowedAmount:'${maxAllowedAmount}'
              });
            });
          </script>
        </c:when>
        <c:otherwise>
          <script type="text/javascript">
            _container_.onLoadDeferred.addCallback(function() {
              atg.commerce.csr.order.billing.addPaymentMethod({
                paymentGroupId: '${rmWidgetId}',
                amount: '${refundMethod.amount}'
              });
            });
          </script>
        </c:otherwise>
        </c:choose>
        <dsp:getvalueof var="pgTypeConfig" bean="CSRConfigurator.paymentGroupTypeConfigurationsAsMap.${pgType}"/>
    
          <%--If there is no previous patyment type or if it is a new payment type, display the headings. --%>
          <c:choose>
            <c:when test="${empty previousPGType}">
              <table class="atg_dataTable atg_commerce_csr_innerTable">
                <thead>
                <th class="atg_commerce_csr_shortData">
                  <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                               otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                    <dsp:param name="refundMethod" value="${refundMethod}"/>
                    <dsp:param name="propertyName" value="value1"/>
                    <dsp:param name="displayHeading" value="${true }"/>
                    <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                  </dsp:include>
                </th>
                <th>
                  <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                               otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                    <dsp:param name="refundMethod" value="${refundMethod}"/>
                    <dsp:param name="propertyName" value="value2"/>
                    <dsp:param name="displayHeading" value="${true }"/>
                    <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                  </dsp:include>
                </th>
                <th>
                  <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                               otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                    <dsp:param name="refundMethod" value="${refundMethod}"/>
                    <dsp:param name="propertyName" value="value3"/>
                    <dsp:param name="displayHeading" value="${true }"/>
                    <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                  </dsp:include>
                </th>
                <th class="atg_numberValue atg_commerce_csr_abbrData">
                  <fmt:message key="appeasements.refundType.table.header.amountOrgChrg.title"/>
                </th>
                <th class="atg_numberValue atg_commerce_csr_abbrData">
                  <fmt:message key="appeasements.refundType.table.header.amountCredited.title"/>
                </th>
                <th
                  class="atg_numberValue atg_commerce_csr_validatedField atg_commerce_csr_billingAmount atg_commerce_csr_returnAmount">
                  <fmt:message key="appeasements.refundType.table.header.amount.title"/>
                </th>
                <th></th>
                </thead>
            </c:when>
            <c:when test="${previousPGType != pgType}">
              </table>
              <table class="atg_dataTable atg_commerce_csr_innerTable">
                <thead>
                <th class="atg_commerce_csr_shortData">
                  <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                               otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                    <dsp:param name="refundMethod" value="${refundMethod}"/>
                    <dsp:param name="propertyName" value="value1"/>
                    <dsp:param name="displayHeading" value="${true }"/>
                    <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                  </dsp:include>
                </th>
                <th>
                  <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                               otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                    <dsp:param name="refundMethod" value="${refundMethod}"/>
                    <dsp:param name="propertyName" value="value2"/>
                    <dsp:param name="displayHeading" value="${true }"/>
                    <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                  </dsp:include>
                </th>
                <th>
                  <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                               otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                    <dsp:param name="refundMethod" value="${refundMethod}"/>
                    <dsp:param name="propertyName" value="value3"/>
                    <dsp:param name="displayHeading" value="${true }"/>
                    <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                  </dsp:include>
                </th>
                <th class="atg_numberValue atg_commerce_csr_abbrData">
                  <fmt:message key="appeasements.refundType.table.header.amountOrgChrg.title"/>
                </th>
                <th class="atg_numberValue atg_commerce_csr_abbrData">
                  <fmt:message key="appeasements.refundType.table.header.amountCredited.title"/>
                </th>
                <th
                  class="atg_numberValue atg_commerce_csr_validatedField atg_commerce_csr_billingAmount atg_commerce_csr_returnAmount">
                  <fmt:message key="appeasements.refundType.table.header.amount.title"/>
                </th>
                <th></th>
                </thead>
            </c:when>
          </c:choose>
          <c:set var="previousPGType" value="${pgType}"/>
          <tr class="atg_commerce_csr_billingGroup">
            <td>
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="propertyName" value="value1"/>
                <dsp:param name="displayValue" value="${true }"/>
                <dsp:param name="order" value="${appeasement.originatingOrder }"/>
              </dsp:include>
            </td>
            <td>
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="propertyName" value="value2"/>
                <dsp:param name="displayValue" value="${true }"/>
                <dsp:param name="order" value="${appeasement.originatingOrder }"/>
              </dsp:include>
            </td>
            <td>
              <ul class="atg_svc_shipAddress">
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="propertyName" value="value3"/>
                <dsp:param name="displayValue" value="${true }"/>
                <dsp:param name="order" value="${appeasement.originatingOrder }"/>
              </dsp:include>
             </ul>
            </td>
            <dsp:include src="/panels/order/appeasements/displayAppeasementPmtCenterFrag.jsp"
                          otherContext="${CSRConfigurator.contextRoot}">
               <dsp:param name="refundMethodIndex" param="index"/>
               <dsp:param name="refundMethod" param="refundMethod"/>
               <dsp:param name="widgetId" value="${rmWidgetId}"/>
            </dsp:include>
            <td class="atg_iconCell">
              <dsp:include src="/panels/order/appeasements/displayAppeasementPmtEndFrag.jsp" otherContext="${CSRConfigurator.contextRoot}">
               <dsp:param name="refundMethod" param="refundMethod"/>
               <dsp:param name="refundMethodIndex" param="index"/>
              </dsp:include>
             </td>
          </tr>
      </dsp:oparam>
      <dsp:oparam name="outputEnd">
        </table>
      </dsp:oparam>
      <dsp:oparam name="outputEmpty">
      <table>
      <tr><td>EMPTY REFUND METHODS</td></tr>
      </table>
    </dsp:oparam>
    </dsp:droplet>
    <%-- ForEach refundMethod --%>
    </dsp:form>
    <%-- bottom appeasement type/refund type area --%>
    
    <div class="atg_commerce_csr_corePanelData">
      <br><br>
      <div>
        <input id="checkoutFooterCancelButton" type="button" value="<fmt:message key='appeasements.button.cancel'/>"
               onclick="atg.commerce.csr.order.appeasement.cancelAppeasement()" 
               class="pull-right"/>&nbsp;&nbsp;
	        <input id="checkoutFooterNextButton" type="button"
               onclick="atg.commerce.csr.order.appeasement.openAppeasementSummary();"
               value="<fmt:message key="appeasements.button.continue" />"
               dojoType="atg.widget.validation.SubmitButton"
               class="pull-right"/>
      </div>
      <br><br>
    </div>
    
  </dsp:layeredBundle>
  
  <script type="text/javascript">
  if (!dijit.byId("editPaymentOptionFloatingPane")) {
    new dojox.Dialog({ id: "editPaymentOptionFloatingPane",
      cacheContent: "false",
      executeScripts: "true",
      scriptHasHooks: "true",
      duration: 100,
      "class": "atg_commerce_csr_popup"});
  }
  </script>

  <script type="text/javascript">
    atg.progress.update('cmcAppeasementsPS');
  </script>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/appeasement.jsp#1 $$Change: 1179550 $--%>
