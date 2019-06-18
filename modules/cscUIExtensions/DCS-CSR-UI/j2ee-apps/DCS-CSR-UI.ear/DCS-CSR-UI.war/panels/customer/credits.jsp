<%--
 This page defines the Customer Credits Panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/credits.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/profile/StoreCreditDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/ServiceCustomerProfile"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
  <dsp:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
  <dsp:importbean bean="/atg/commerce/pricing/CurrencyCodeDroplet"/>
  <dsp:importbean bean="/atg/commerce/pricing/PricingTools"/>
  
  <dsp:droplet name="HasAccessRight">
    <dsp:param name="accessRight" value="commerce-custsvc-issue-credit-privilege"/>
    <dsp:oparam name="accessGranted">
      <c:set var="issueCreditPriv" value="true"/>
    </dsp:oparam>
    <dsp:oparam name="accessDenied">
      <c:set var="issueCreditPriv" value="false"/>
    </dsp:oparam>
  </dsp:droplet>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <c:url var="addNewCreditURL" context="${CSRConfigurator.contextRoot}" value="/panels/customer/addNewCreditPopup.jsp">
     <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
  </c:url>
  
<script type="text/javascript">
  if (!dijit.byId("addNewCreditPopup")) {
    new dojox.Dialog({ id: "addNewCreditPopup",
                       cacheContent: "false",
                       executeScripts: "true",
                       scriptHasHooks: "true",
                       duration: 100,
                       "class": "atg_commerce_csr_popup"});
  }
</script>  
  
  <div id="atg_commerce_csr_customerinfo_credits_subPanel" class="atg_svc_subPanel">
    <div class="atg_svc_subPanelHeader" >       
        <ul class="atg_svc_panelToolBar">
          <li class="atg_svc_header">
            <h4 id="atg_commerce_csr_customerinfo_credits"><fmt:message key="customer.credits.title.label"/> </h4>
          </li>
          <dspel:getvalueof var="mode" param="mode"/>
           <c:if test="${issueCreditPriv}">
              <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param param="mode" name="value"/>
              <dsp:oparam name="edit">
            <li class="atg_svc_last">          
               <a href="#"
                   onclick="atg.commerce.csr.common.showPopupWithReturn({
                                      popupPaneId: 'addNewCreditPopup',
                                      title: '<fmt:message key="customer.credits.addNewCredit.addCredit"/>',
                                      url: '<c:out value="${addNewCreditURL}"/>'
                                      });"
                   class="atg_svc_popupLink">
                  <fmt:message key="customer.creditCards.addCredit.label"/>
                </a>
            </li>
            </dsp:oparam>
            </dsp:droplet>
          </c:if>
         </ul>
      </div>
  
  <dsp:droplet name="StoreCreditDroplet">
    <dsp:param name="profileId" bean="ServiceCustomerProfile.repositoryId"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="storeCredits" param="storeCredits"/>
    </dsp:oparam>
  </dsp:droplet>

  <c:choose>
    <c:when test="${!empty storeCredits}">
      <div class="credits" style="width:98%">
        <table id="atg_commerce_csr_customerinfo_credits_table" class="atg_dataTable">
          <thead>
            <tr class="atg_currentRow">
              <td>
                <fmt:message key="customer.credits.issueDate"/>
              </td>
              <td>
                <fmt:message key="customer.credits.redemptionCode"/>
              </td>
              <td class="atg_numberValue">
                <fmt:message key="customer.credits.amountIssued"/>
              </td>
              <td class="atg_numberValue">
                <fmt:message key="customer.credits.amountAuthorized"/>
              </td>
              <td class="atg_numberValue">
                <fmt:message key="customer.credits.amountRemaining"/>
              </td>
              <%--TODO: "reason code" feature is not feasible for this release. Uncomment when it is feasible.--%>
              <%--<td>
                <fmt:message key="customer.credits.reason"/>
              </td>--%>
            </tr>
          </thead>
          <tbody>
            <dsp:droplet name="CurrencyCodeDroplet">
              <dsp:param name="locale" bean="PricingTools.defaultLocale"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="currencyCode" vartype="java.lang.String" param="currencyCode"/>
              </dsp:oparam>
            </dsp:droplet>
          
            <c:forEach items="${storeCredits}" var="storeCredit" varStatus="status">
              <dsp:tomap var="storeCredit" value="${storeCredit}"/>
              <tr class="atg_altRow">
                <td>
                  <c:choose>
                    <c:when test="${!empty storeCredit.issueDate}">
                      <web-ui:formatDate value="${storeCredit.issueDate}" type="both" dateStyle="short" timeStyle="short"/>
                    </c:when>
                    <c:otherwise>
                      <fmt:message key="customer.credits.unknown"/>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td>
                  <dsp:valueof value="${storeCredit.repositoryId}">
                    <fmt:message key="customer.credits.unknown"/>
                  </dsp:valueof>
                </td>
                <td class="atg_numberValue">
                  <csr:formatNumber type="currency" value="${storeCredit.amount}" currencyCode="${currencyCode}"/>
                </td>
                <td class="atg_numberValue">
                  <csr:formatNumber type="currency" value="${storeCredit.amountAuthorized}" currencyCode="${currencyCode}"/>
                </td>
                <td class="atg_numberValue">
                  <csr:formatNumber type="currency" value="${storeCredit.amountRemaining}" currencyCode="${currencyCode}"/>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:when>
    <c:otherwise>
      <div class="emptyLabel">
        <fmt:message key="customer.credits.noCredits.label"/>
      </div>
    </c:otherwise>
  </c:choose>
  </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/credits.jsp#2 $$Change: 1179550 $--%>
