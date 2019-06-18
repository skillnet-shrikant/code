<%--
 This page defines the customer panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/customer.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
 <dsp:page xml="true">
  
  <dsp:getvalueof var="customerProfile" bean="/atg/userprofiling/ActiveCustomerProfile"/>
  <dsp:tomap var="customerProfileMap" value="${customerProfile}"/>
  <dsp:getvalueof var="isProfileTransient" value="${customerProfile['transient']}"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart"
    var="cart" />
  <c:set var="order" value="${cart.current}"/>

    <script type="text/javascript">
      if (!dijit.byId("atg_commerce_csr_catalog_customerSelectionPopup")) {
        new dojox.Dialog({ id:"atg_commerce_csr_catalog_customerSelectionPopup",
                           cacheContent:"false",
                           executeScripts:"true",
                           scriptHasHooks:"true",
                           duration: 100,
                           "class":"atg_commerce_csr_wide_popup"});
      }
    </script>
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <c:url var="customerSelectionURL" context="${CSRConfigurator.contextRoot}" value="/include/order/customerSelection.jsp">
         <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
      </c:url>
      <input type="hidden" id="atg_commerce_csr_catalog_customerSelectionURL" value="<c:out value='${customerSelectionURL}'/>"/>
      <div class="atg_commerce_csr_content">
        <c:if test="${!isProfileTransient}">
            <fmt:message var="customerName" key="customer.name.first.last">
              <fmt:param value="${customerProfileMap.firstName}"/>
              <fmt:param value="${customerProfileMap.lastName}"/>
            </fmt:message>
          <ul class="atg_dataForm">
            <li>
              <span class="atg_commerce_csr_fieldTitle">
                  <fmt:message key="cart.customer.name"/>
              </span>
              <a href="#" onclick="viewCurrentCustomer('commerceTab');event.cancelBubble=true; return false;">
              <c:out value="${customerName}"/>
              </a>
            </li>
            <li>
              <span class="atg_commerce_csr_fieldTitle">
                  <fmt:message key="cart.customer.email"/>
              </span>
              <dsp:valueof bean="/atg/userprofiling/ActiveCustomerProfile.email"/>
            </li>
            <li class="atg_commerce_csr_selectCustomerAction">
              <a href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
                popupPaneId: 'atg_commerce_csr_catalog_customerSelectionPopup',
                url: document.getElementById('atg_commerce_csr_catalog_customerSelectionURL').value,
                title: '<fmt:message key="cart.customerSelection.selectCustomer"/>'
              });return false;">
                <fmt:message key="cart.customer.changeCustomer"/>
              </a>                              
            </li>
          </ul>

        </c:if>
        <c:if test="${isProfileTransient}">
          <span class="atg_commerce_csr_selectCustomerAction"><a href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
            popupPaneId: 'atg_commerce_csr_catalog_customerSelectionPopup',
            url: document.getElementById('atg_commerce_csr_catalog_customerSelectionURL').value,
            title: '<fmt:message key="cart.customerSelection.selectCustomer"/>'
          });return false;">
            <fmt:message key="cart.customer.selectCustomer"/>
          </a></span>
          <fmt:message key="cart.customer.anonymousText"/>
        </c:if>
      </div>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/customer.jsp#1 $$Change: 946917 $--%>
