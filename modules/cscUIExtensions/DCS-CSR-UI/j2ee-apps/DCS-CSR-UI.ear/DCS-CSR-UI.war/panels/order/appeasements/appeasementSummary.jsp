<%--
 This page defines the appeasements summary panel
 It displays the details of the appeasement including refund types (credit cards and store credit) as read only
 entries and allows the agent to continue to submit appeasement, return to the previous (appeasements.jsp) screen 
 to edit appeasement or cancel it completely, returning to the order view page.
 
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/appeasementSummary.jsp#1 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">
  
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
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
        <li class="atg_commerce_csr_header">
          <h4><fmt:message key='appeasements.summary.table.header'/></h4>
        </li>
        <li class="atg_commerce_csr_last">
          <a href="#" onClick="atgNavigate({panelStack:'cmcAppeasementsPS'});return false;">
            <fmt:message key='appeasements.summary.link.editAppeasement'/>
          </a>
        </li>
      </ul>
    </div>
    <dsp:include src="/panels/order/appeasements/finishAppeasementSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="appeasement" value="${cart.appeasement}"/>
    </dsp:include>
  </div>
  
  <%-- Submit/Cancel buttons --%>
  <div class="atg_commerce_csr_corePanelData">
    <br><br>
    <div>
       <%/*Submit the appeasement*/%>
      <svc-ui:frameworkUrl var="successURL" panelStacks="cmcConfirmAppeasementPS,globalPanels" tab="commerceTab"/>
      <svc-ui:frameworkUrl var="errorURL" panelStacks="globalPanels"/>
      <dsp:form id="csrSubmitAppeasement" formid="csrSubmitAppeasement">
        <dsp:input name="successURL" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.submitAppeasementSuccessURL" value="${successURL}" type="hidden" />
        <dsp:input name="errorURL" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.submitAppeasementErrorURL"  value="${errorURL}" type="hidden" />
        <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/appeasement/AppeasementFormHandler.submitAppeasement"/>
        <input id="checkoutFooterCancelButton" type="button" value="<fmt:message key='appeasements.button.cancel'/>"
               onclick="atg.commerce.csr.order.appeasement.cancelAppeasement()" 
               class="pull-right"/>&nbsp;&nbsp;
        <input id="checkoutFooterNextButton" type="button"
               onclick="atg.commerce.csr.order.appeasement.submitAppeasement();"
               value="<fmt:message key="appeasements.button.submitAppeasement" />"
               dojoType="atg.widget.validation.SubmitButton"
               class="pull-right"/>  
      </dsp:form>              
    </div>
    <br><br>
  </div>
   
  </dsp:layeredBundle>

  <script type="text/javascript">
    atg.progress.update('cmcAppeasementSummaryPS');
  </script>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/appeasementSummary.jsp#1 $$Change: 1179550 $--%>
