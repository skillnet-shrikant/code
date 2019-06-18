<%--
 This page defines the Customer Credit Cards Panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/creditCardDisplay.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <dsp:getvalueof var="mode" param="mode"/>
    <dsp:getvalueof var="ccm" param="ccm"/>
    <%-- credit card repository item --%>
    <c:set var="ccItem" value="${ccm.creditCard}"/>
    <%-- credit card as map --%>
    <dsp:tomap var="cc" value="${ccm.creditCard}"/>
    <dsp:tomap var="addr" value="${cc.billingAddress}"/>
    <c:if test="${not empty ccm.params.defaultOptions}">
      <div class="atg_svc_iconSet">
        <c:forEach var="defaultType" items="${ccm.params.defaultOptions}">
          <ul>
            <li>
              <span class="atg_svc_defaultAddress"
                title="<fmt:message key='${defaultType.value.symbolMouseoverResource}'/>">
                <fmt:message key="${defaultType.value.symbolResource}"/>
              </span>
            </li>
          </ul>
        </c:forEach>
      </div>
    </c:if>
    <div class="atg_svc_addressWrapper">
    <dl class="atg_svc_shipAddress">
      <dd>
        <c:catch var="exception">
          ${fn:escapeXml(cc.creditCardType)} - 
          <dsp:valueof converter="creditCard" value="${cc.creditCardNumber}"/>
        </c:catch>
        <c:if test="${exception!=null}">
          <script type="text/javascript">
            console.debug("${exception}");
          </script>
        </c:if>
      </dd>
      <dd>
        <fmt:message key="creditCard.expires.month.year.label"> 
          <fmt:param value="${fn:escapeXml(cc.expirationMonth)}" />
          <fmt:param value="${fn:escapeXml(cc.expirationYear)}" />
        </fmt:message>
      </dd>
    </dl>
    <dl class="atg_svc_shipAddress">
      <dt><fmt:message key="creditCard.billingAddress" /></dt>
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
      <dd>${fn:escapeXml(addr.city)}, ${fn:escapeXml(addr.state)} ${fn:escapeXml(addr.postalCode)}</dd>
      <dd>
        <dsp:getvalueof var="places" bean="/atg/core/i18n/CountryList.places"/>
        <c:forEach var="country" items="${places}">
          <c:if test="${country.code == addr.country}">
            ${country.displayName}
          </c:if>
        </c:forEach>
      </dd>
      <dd>${fn:escapeXml(addr.phoneNumber)}</dd>
    </dl>
    <c:if test="${mode == 'edit'}">
      <ul class="atg_svc_shipAddressControls default" >
        <%-- <li>
          <svc-ui:frameworkPopupUrl var="creditCardEdit"
            value="/include/creditcards/creditCardEditor.jsp"
            context="${CSRConfigurator.contextRoot}"
            creditCardId="${ccm.creditCard.repositoryId}"
            windowId="${windowId}"/>
          <a href="#" class="atg_tableIcon atg_propertyEdit" title="<fmt:message key='catalogBrowse.addProductsById.editTooltip' />"
            class="atg_svc_popupLink"
            onclick="atg.commerce.csr.common.showPopupWithReturn({
              popupPaneId: 'creditCardPopup',
              url: '${creditCardEdit}',
              title: '<fmt:message key="creditCard.editTitle"/>',
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
            <fmt:message key="creditCard.edit.label"/>
          </a>
        </li> --%>
        <c:if test="${ccm.deletable}">
          <svc-ui:frameworkPopupUrl var="creditCardDelete"
            value="/include/creditcards/creditCardDeleter.jsp"
            context="${CSRConfigurator.contextRoot}"
            creditCardId="${ccm.creditCard.repositoryId}"
            windowId="${windowId}"/>
          <li>
            <a href="#" class="atg_tableIcon atg_propertyDelete" 
              title="<fmt:message key='creditCard.delete.mouseover'/>"
              onClick="atg.commerce.csr.common.showPopupWithReturn({
                popupPaneId: 'creditCardPopup',
                title: '<fmt:message key="creditCard.deleteCreditCard"/>',
                url: '${creditCardDelete}',
                onClose: function( args ) {
                  if ( args.result == 'delete' ) {
                    atgSubmitAction({
                      panels : ['customerInformationPanel'],
                      panelStack : ['customerPanels','globalPanels'],
                      form : document.getElementById('transformForm')
                    });
                  }
                }
              }); return false;">
              <fmt:message key="creditCard.delete.label"/>
            </a>
          </li>
        </c:if>
      </ul>
    </c:if>
  </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/creditCardDisplay.jsp#2 $$Change: 1179550 $--%>