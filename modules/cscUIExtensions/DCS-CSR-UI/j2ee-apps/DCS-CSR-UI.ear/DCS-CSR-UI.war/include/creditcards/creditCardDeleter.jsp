<%--
 This page deletes a Customer Credit Card

 @param creditCardId - The ID of the credit card to delete

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/creditcards/creditCardDeleter.jsp#3 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <c:set var="creditCardFormHandlerPath"
    value="/atg/commerce/custsvc/repository/CreditCardFormHandler"/>
  <dsp:importbean var="ccfh" bean="${creditCardFormHandlerPath}"/>
  <dsp:setvalue bean="CreditCardFormHandler.repositoryId"
    value="${fn:escapeXml(param.creditCardId)}"/>
  <c:set var="ccm" value="${ccfh.creditCardMetaInfo}"/>
  <dsp:tomap var="cc" value="${ccm.creditCard}"/>
  <c:choose>
    <c:when test="${cc.id eq fn:escapeXml(param.creditCardId)}">
      <c:set var="isDeleted" value="false" />
    </c:when>
    <c:otherwise>
      <c:set var="isDeleted" value="true" />
    </c:otherwise>
  </c:choose>
  <svc-ui:frameworkPopupUrl var="url"
    value="/include/creditcards/creditCardDeleter.jsp"
    context="${CSRConfigurator.contextRoot}"
    creditCardId="${param.creditCardId}"
    windowId="${windowId}"/>
  <svc-ui:frameworkPopupUrl var="successUrl"
    value="/include/creditcards/creditCardDeleter.jsp"
    context="${CSRConfigurator.contextRoot}"
    success="true"
    creditCardId=""
    windowId="${windowId}"/>
  <svc-ui:frameworkPopupUrl var="errorUrl"
    value="/include/creditcards/creditCardDeleter.jsp"
    context="${CSRConfigurator.contextRoot}"
    success="true"
    windowId="${windowId}"/>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <c:choose>
    <c:when test="${isDeleted == false or empty isDeleted}" >
    <h3 id="atg_commerce_csr_customerinfo_popDeleteCreditCard">
      <fmt:message key="creditCard.deleteCreditCard"/>
    </h3>
    <h4>
      <fmt:message key="creditCard.areYouSureYouWantToDelete"/>
    </h4>
    <dsp:form method="post" id="creditCardDeleterForm" formid="creditCardDeleterForm">
      <dsp:setvalue bean="CreditCardFormHandler.repositoryId"
        value="${fn:escapeXml(param.creditCardId)}"/>
      <dsp:input type="hidden" bean="CreditCardFormHandler.repositoryId"
        value="${fn:escapeXml(param.creditCardId)}" priority="1000"/>
      <dsp:input type="hidden" value="${successUrl}" priority="500"
        bean="CreditCardFormHandler.deleteSuccessURL"/>
      <dsp:input type="hidden" value="${errorUrl}" priority="500"
        bean="CreditCardFormHandler.deleteErrorURL"/>
      <c:if test="${ not param.success }">
        <%-- credit card repository item --%>
        <c:set var="ccm" value="${ccfh.creditCardMetaInfo}"/>
        <c:set var="ccItem" value="${ccm.creditCard}"/>
        <%-- credit card as map --%>
        <dsp:tomap var="cc" value="${ccm.creditCard}"/>
        <dsp:tomap var="addr" value="${cc.billingAddress}"/>
        <dl class="atg_svc_shipAddress">
          <dt>
            <c:choose>
              <c:when test="${empty ccm.nicknames}">
                <fmt:message key='creditCard.noNickname'/>
              </c:when>
              <c:otherwise>
                ${fn:escapeXml(ccm.nicknames)}
              </c:otherwise>
            </c:choose>
          </dt>
          <dd>
            ${fn:escapeXml(cc.creditCardType)} - <dsp:valueof converter="creditCard" value="${cc.creditCardNumber}"/>
          </dd>
          <dd>
            <fmt:message key="creditCard.expires.month.year.label"> 
              <fmt:param value="${fn:escapeXml(cc.expirationMonth)}" />
              <fmt:param value="${fn:escapeXml(cc.expirationYear)}" />
            </fmt:message>
          </dd>
        </dl>
        <dl class="atg_svc_shipAddress">
          <dt>
            <fmt:message key="creditCard.billingAddress"/>
          </dt>
          <dd>
            <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
              <c:choose>
                <c:when test="${!empty addr.middleName }">
                  <fmt:message key="customer.name.first.middle.last"> 
                    <fmt:param value="${fn:escapeXml(addr.firstName)}" />
                    <fmt:param value="${fn:escapeXml(addr.middleName)}" />
                    <fmt:param value="${fn:escapeXml(addr.lastName)}" />
                  </fmt:message> 
                </c:when>
                <c:otherwise>
                  <fmt:message key="customer.name.first.last"> 
                    <fmt:param value="${fn:escapeXml(addr.firstName)}" />
                    <fmt:param value="${fn:escapeXml(addr.lastName)}" />
                  </fmt:message> 
                </c:otherwise>
              </c:choose>
            </dsp:layeredBundle>
          </dd>
          <dd>${fn:escapeXml(addr.address1)}</dd>
          <c:if test="${!empty addr.address2}">
            <dd>${fn:escapeXml(addr.address2)}</dd>
          </c:if>
          <dd>
            ${fn:escapeXml(addr.city)}, 
            ${fn:escapeXml(addr.state)} ${fn:escapeXml(addr.postalCode)}
          </dd>
          <dd>${fn:escapeXml(addr.country)}</dd>
          <dd>${fn:escapeXml(addr.phoneNumber)}</dd>
        </dl>
      </c:if>
      <dsp:input type="hidden" value="--" priority="-100"
        bean="CreditCardFormHandler.delete"/>
      <div class="atg_commerce_csr_panelFooter">
        <input value="<fmt:message key='creditCard.delete.label'/>" 
          type="button" id="deleteChoice"
          onClick="
            atgSubmitPopup({url: '${url}', 
              form: document.getElementById('creditCardDeleterForm'),
        success: true,
              popup: getEnclosingPopup('deleteChoice')});
              atg.commerce.csr.common.hidePopup(dijit.byId('creditCardPopup'));
            return false;"/>
        <input value="<fmt:message key='creditCard.cancel.label'/>" 
          type="button" id="cancelChoice"
          onClick="hidePopupWithResults('creditCardPopup', {result:'cancel'}); 
          return false;"/>
      </div>
    </dsp:form>
    </c:when>
  <c:otherwise>
      <c:set var="param.success" value="true" />
      <div  style="font-size:1.1667em; font-weight:bold;">This credit card is already deleted!<br></div>
      <div class="atg_commerce_csr_panelFooter">
        <input value="Return" 
          type="button" id="deleteChoice"
          onClick="hidePopupWithResults('creditCardPopup', {result:'delete'}); 
          return false;"/>    
      </div>
  </c:otherwise>
  </c:choose>
    <c:if test="${param.success}">
      <script type="text/javascript">
        hidePopupWithResults( 'deleteChoice', {result : 'delete'});
      </script>
    </c:if>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/creditcards/creditCardDeleter.jsp#3 $$Change: 1179550 $--%>
