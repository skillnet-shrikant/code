<%--
 This page defines the Customer Credit Cards Panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/creditCards.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <dsp:getvalueof var="mode" param="mode"/>
    <dspel:importbean var="profile"
      bean="/atg/userprofiling/ServiceCustomerProfile"/>
    <dsp:importbean var="ccfh" 
      bean="/atg/commerce/custsvc/repository/CreditCardFormHandler"/>
    <c:set var="wallet" value="${ccfh.creditCardWallet}"/>
<script type="text/javascript">
  if (!dijit.byId("creditCardPopup")) {
    new dojox.Dialog({ id: "creditCardPopup",
                       cacheContent: "false", 
                       executeScripts: "true",
                       scriptHasHooks: "true"});
  }
</script>


<div id="atg_commerce_csr_customerinfo_credit_cards_subPanel" class="atg_svc_subPanel">
  <div class="atg_svc_subPanelHeader" >       
      <ul class="atg_svc_panelToolBar">
        <li class="atg_svc_header">
          <h4 id="atg_commerce_csr_customerinfo_paymentMethods"><fmt:message key="customer.creditCards.title.label"/> </h4>
        </li>
        <dspel:getvalueof var="mode" param="mode"/>
        <c:if test="${mode == 'edit'}">   
          <li class="atg_svc_last">          
            <svc-ui:frameworkPopupUrl var="creditCardEdit"
              value="/include/creditcards/creditCardEditor.jsp"
              context="${CSRConfigurator.contextRoot}"
              windowId="${windowId}"/>
            <a href="#"
              class="atg_svc_popupLink"
              onClick="atg.commerce.csr.common.showPopupWithReturn({
                popupPaneId: 'creditCardPopup',
                title: '<fmt:message key="customer.creditCards.addCreditCard.label"/>',
                url: '${creditCardEdit}',
                onClose: function( args ) {
                  if ( args.result == 'save' ) {
                    atgSubmitAction({
                      panels : ['customerInformationPanel'],
                      panelStack : ['customerPanels','globalPanels'],
                      form : document.getElementById('transformForm')
                    });
                  }
                }});
                return false;">
              <fmt:message key="customer.creditCards.addCreditCard.label"/>
            </a>
          </li>
        </c:if>
       </ul>
    </div>

    <c:choose>
      <c:when test="${not empty wallet.creditCardMetaInfos}">
        <div class="creditCards">
          <%-- default credit cards --%>
          <c:forEach var="ccm" items="${wallet.creditCardMetaInfos}">
            <c:if test="${not empty ccm.value.params.defaultOptions}">
              <div class="customerInfo_creditCards atg_svc_iconD" title="<fmt:message key='creditCard.defaultCreditCard'/>">
                <dsp:include src="/panels/customer/creditCardDisplay.jsp" otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="mode" param="mode"/>
                  <dsp:param name="ccm" value="${ccm.value}"/>
                </dsp:include>
              </div>
            </c:if>
          </c:forEach>
          <%-- non-default credit cards --%>
          <c:forEach var="ccm" items="${wallet.creditCardMetaInfos}">
            <c:if test="${empty ccm.value.params.defaultOptions}">
              <div class="customerInfo_creditCards atg_svc_iconD">
                <dsp:include src="/panels/customer/creditCardDisplay.jsp" otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="mode" param="mode"/>
                  <dsp:param name="ccm" value="${ccm.value}"/>
                </dsp:include>
              </div>
            </c:if>
          </c:forEach>
        </div>
      </c:when>
      <c:otherwise>
        <div class="emptyLabel">
          <fmt:message key="customer.creditCards.noCreditCards.label"/>
        </div>
      </c:otherwise>
    </c:choose>  
    </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/creditCards.jsp#1 $$Change: 946917 $--%>
