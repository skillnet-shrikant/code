<%--
 This page defines the complete order/return/exchange panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/confirmReturn.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/returns/GetReturnItemQuantityInfoDroplet"/>
  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

  <dsp:getvalueof var="returnObject" param="returnObject"/>

  <ul class="atg_commerce_csr_panelToolBar">
      <li class="atg_commerce_csr_last">
        <a href="#"  onclick="atg.commerce.csr.order.viewExistingOrder('${returnObject.order.id}','${returnObject.order.stateAsString}');return false;"><fmt:message key="confirmReturn.returnToOrder.label"><fmt:param>${fn:escapeXml(returnObject.order.id)}</fmt:param></fmt:message></a>
      </li>
  </ul>
  <ul class="atg_commerce_csr_simpleList">
    <c:if test="${returnObject.exchangeProcess}">
      <li class="atg_commerce_csr_rtnConfirmationNumber"><fmt:message key="confirmReturn.orderConfirmationNumber.label"/> 
      <a href="#"  onclick="atg.commerce.csr.order.viewExistingOrder('${returnObject.replacementOrder.id}','${returnObject.replacementOrder.stateAsString}');return false;">${fn:escapeXml(returnObject.replacementOrder.id)}</a>
      </li>
    </c:if>
    <li class="atg_commerce_csr_rtnAuthorizationNum"><fmt:message key="confirmReturn.returnAuthorizationNumber.label"/> <strong>${fn:escapeXml(returnObject.authorizationNumber)}</strong></li>
    <c:if test="${returnObject.returnProcess}">
      <li class="atg_commerce_csr_confirmReturn"><fmt:message key="confirmReturn.refunds.label"/></li>
      <li class="atg_commerce_csr_rtnAmt">
        <dl class="refunds_made">
          <c:forEach var="refund" items="${returnObject.refundMethodList}">
            <dt>
              <c:choose>
                <c:when test="${refund.refundType == 'creditCard' }">
                  <c:out value="${refund.creditCard.creditCardType}"/> <c:out value="${refund.creditCardSuffix}"/>
                </c:when>
                <c:when test="${refund.refundType == 'storeCredit' }">
                  <fmt:message key="finishReturn.refundTypes.storeCredit.label" />
                </c:when>
                <c:otherwise><fmt:message key="finishReturn.refundTypes.other.label"/></c:otherwise>
              </c:choose>
            </dt>
            <dd><csr:formatNumber value="${refund.amount }" type="currency"
                  currencyCode="${returnObject.order.priceInfo.currencyCode}"/>
            </dd>
          </c:forEach>
        </dl>
      </li>
    </c:if>
  </ul>

  <c:if test="${returnObject.returnProcess}">
    <h3 class="atg_svc_subSectionTitle"> <span id="atg_commerce_csr_return_itemsReturned"><fmt:message key="confirmReturn.returnItemList"/></span> </h3>

    <div class="atg_commerce_csr_corePanelData">
      <table class="atg_dataTable atg_commerce_csr_billingTable">
        <thead>
          <tr>
            <th class="atg_numberValue"><fmt:message key="confirmReturn.quantityToReturn.table.header.title"/></th>
            <th><fmt:message key="confirmReturn.id.table.header.title"/></th>
            <th><fmt:message key="confirmReturn.description.table.header.title"/></th>
          </tr>
        </thead>
        <tbody>

          <c:forEach var="returnItem" items="${returnObject.returnItemList}" varStatus="rowCounter">
            <c:set var="currencyCode" value="${returnItem.commerceItem.priceInfo.currencyCode}"/>
            <tr>
              <dsp:droplet name="GetReturnItemQuantityInfoDroplet">
                <dsp:param name="item" value="${returnItem}"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="quantityToReturn" param="quantityToReturn"/>
                </dsp:oparam>
              </dsp:droplet>
              <td class="atg_numberValue"><web-ui:formatNumber value="${quantityToReturn}"/></td>
              <td><c:out value="${returnItem.commerceItem.catalogRefId}"/></td>
              <td><dsp:tomap var="productRef" value="${returnItem.commerceItem.auxiliaryData.productRef}"/>
              ${fn:escapeXml(productRef.displayName)}
              </td>

            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:if>

  <%-- Show the return shipping address --%>
  <csr:renderer name="/atg/commerce/custsvc/ui/renderers/ReturnShippingAddress">
    <jsp:attribute name="setPageData">
    </jsp:attribute>
    <jsp:body>
      <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
        <dsp:param name="returnObject" value="${returnObject}"/>
      </dsp:include>
     </jsp:body>
  </csr:renderer>

  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/confirmReturn.jsp#2 $$Change: 1179550 $--%>
