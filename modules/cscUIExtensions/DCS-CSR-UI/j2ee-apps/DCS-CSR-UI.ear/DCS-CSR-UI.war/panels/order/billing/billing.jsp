<%--
This page defines the billing panel
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/billing.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/PaymentGroupDroplet"/>
  <c:set var="currentOrder" value="${cart.current}"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
  <dsp:importbean var="AddClaimables" bean="/atg/commerce/custsvc/ui/fragments/order/AddClaimables"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CreateCreditCardFormHandler"/>
  	
  <dsp:getvalueof var="returnRequest" bean="/atg/commerce/custsvc/order/ShoppingCart.returnRequest"/>
  <c:set var="displayPaymentOptions" value="true"/>

  <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnActive">
  <dsp:oparam name="true">
    <c:if test="${returnRequest.returnPaymentState eq 'None'}">
      <c:set var="displayPaymentOptions" value="false"/>
    </c:if>
  </dsp:oparam>
  </dsp:droplet>

  <c:if test="${displayPaymentOptions == false}">
    <div class="atg_svc_content atg_commerce_csr_content">
    <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
      <fmt:message key="exchange.noMoneyDue"/>
    </dsp:layeredBundle>
    </div>
  </c:if>
  

  <%-- The PaymentGroupDroplet is used to initialize desire payment groups. In CSC we are
  interested in only order level pricing and not on item level pricing. The total cost for the
  order is assigned to the available payment options. Using the current CSC implementation,
  you can't perform item to payment option pricing. In order order to accomplish this, you need
  to over-ride this page --%>
  
  <dsp:droplet name="PaymentGroupDroplet">
    <dsp:param name="clear" param="init"/>
    <dsp:param name="paymentGroupTypes" bean="CSRConfigurator.paymentGroupTypesToBeInitialized"/>
    <dsp:param name="clear" param="init"/> 
    <dsp:param name="initOrderPayment" param="init"/>
    <dsp:param name="initPaymentGroups" param="init"/>
    <dsp:param name="initBasedOnOrder" param="init"/>
    <dsp:param name="createAllPaymentInfos" value="true"/>
    <dsp:oparam name="output">
      <dsp:getvalueof param="paymentGroups" var="paymentGroups"/>
      <dsp:getvalueof param="order" var="order"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <c:if test="${empty paymentGroups}">
    <c:set var='addNewPaymentOptionSelected' value=' selected="true"' />
  </c:if>
  
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <div dojoType="dijit.layout.AccordionContainer" duration="200" sizeMin="20" sizeShare="38">

      <div dojoType="dijit.layout.AccordionPane" title="<fmt:message key='newOrderBilling.addNewPaymentOption.header.title' />" <c:out value="${addNewPaymentOptionSelected}" />>
         <div class="atg-csc-instore-payment-div">
            <c:set var="paymentGroupsCount" value="${paymentGroups == null ? 0 : fn:length(paymentGroups)}"/>
            <div id="atg_commerce_csr_addNewCreditCardPane">
            <dsp:getvalueof var="pgTypeConfigs" bean="CSRConfigurator.paymentGroupTypeConfigurations"/>
            <%-- Determine how many add fragments are in the payment group type configs.--%>
            <c:set var="addPageFragmentCount" value="${0}"/>
            <c:forEach var="pgTypeConfig" items="${pgTypeConfigs}">
              <c:if test="${addPageFragmentCount < 2 && pgTypeConfig!= null && pgTypeConfig.addPageFragment != null }">
                <c:set var="addPageFragmentCount" value="${addPageFragmentCount + 1}"/>
              </c:if>
            </c:forEach>
      
            <%-- If there is only one addPageFragment, do not display the tab. --%>
            <c:if test="${addPageFragmentCount == 1}">
              <c:forEach var="pgTypeConfig" items="${pgTypeConfigs}">
                <c:if test="${pgTypeConfig!= null && pgTypeConfig.addPageFragment != null }">
                  <dsp:include src="${pgTypeConfig.addPageFragment.URL}"
                               otherContext="${pgTypeConfig.addPageFragment.servletContext}"/>
                </c:if>
              </c:forEach>
            </c:if>

            <%-- If there is more than one addPageFragments, then display the tab. --%>
            <c:if test="${addPageFragmentCount > 1}">
              <div id="paymentOptionAddContainer" dojoType="dijit.layout.TabContainer"
                   doLayout="false">
      
                <%---The dijit.layout.Contentpane does not support scripts execution. Because of that
                we are using dojox.layout.ContentPane. This will help us to validate the
                form fields
                --%>
                <c:forEach var="pgTypeConfig" items="${pgTypeConfigs}">
                  <c:if test="${pgTypeConfig!= null && pgTypeConfig.addPageFragment != null }">
                   <dsp:layeredBundle basename="${pgTypeConfig.resourceBundle}">
                    <fmt:message var="addPageFragmentTitle" key="${pgTypeConfig.addPageFragmentTitleKey}"/>
                   </dsp:layeredBundle>
                    <div id="${pgTypeConfig.type}" dojoType="dojox.layout.ContentPane" executeScripts="true"
                         scriptHasHooks="true" title="${addPageFragmentTitle}">
                      <dsp:include src="${pgTypeConfig.addPageFragment.URL}"
                                   otherContext="${pgTypeConfig.addPageFragment.servletContext}">
                      </dsp:include>
                    </div>
                  </c:if>
                </c:forEach>
              </div>
              
            </c:if>

          </div>
          </div>
      </div>
      
       <dsp:droplet name="Switch">
        <dsp:param bean="CreateCreditCardFormHandler.formError" name="value"/>
        <dsp:oparam name="true">	
        	<div dojoType="dijit.layout.AccordionPane" title="<fmt:message key='newOrderBilling.availablePaymentOptions.title' />">
        </dsp:oparam>
        <dsp:oparam name="false">	
        	<div dojoType="dijit.layout.AccordionPane" title="<fmt:message key='newOrderBilling.availablePaymentOptions.title' />" selected="true">
        </dsp:oparam>
      </dsp:droplet>

        <c:if test="${displayPaymentOptions == true}">
        <div class="atg_svc_content atg_commerce_csr_content">
        <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
      
        <%-- We do not want to include the claimables in the tab and we want to provide more visibility for the claimables.
        Because of that it is not included in the tab and it is considered as separate item. --%>
        <dsp:include src="${AddClaimables.URL}" otherContext="${AddClaimables.servletContext}"/>
       </dsp:layeredBundle>
       </c:if>
      
        <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
          <dsp:include src="/panels/order/billing/choosePaymentOptions.jsp" otherContext="${CSRConfigurator.contextRoot}">
            <dsp:param name="displayPaymentOptions" value="${displayPaymentOptions}"/>
            <dsp:param name="paymentGroups" value="${paymentGroups}"/>
            <dsp:param name="order" value="${order}"/>
          </dsp:include>
        </dsp:layeredBundle>
        </div>
        <script type="text/javascript">
          atg.progress.update('cmcBillingPS');
          _container_.onUnloadDeferred.addCallback(function () {
            atgHideLoadingIcon();
          });
        </script>

      </div>
      
    </div>
  </dsp:layeredBundle>


</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/billing.jsp#1 $$Change: 946917 $--%>