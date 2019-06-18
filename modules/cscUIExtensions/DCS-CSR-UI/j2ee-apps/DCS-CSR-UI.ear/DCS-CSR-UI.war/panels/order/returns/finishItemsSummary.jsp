<%--
Display details of the returned items. Use the
completeItemLineItem.jspf to render each returned item.

Expected params
returnRequest : The return request.

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">

  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

  <dsp:getvalueof var="returnRequest" param="returnRequest"/>
    <div class="atg_commerce_csr_corePanelData">

      <table class="atg_dataTable atg_commerce_csr_billingTable">
        <thead>
            <th class="atg_numberValue">
              <fmt:message key='finishReturn.returnItems.table.header.quantityToReturn.title'/>
            </th>
            <th>
              <fmt:message key='finishReturn.returnItems.table.header.sku.title'/>
            </th>
            <th>
              <fmt:message key='finishReturn.returnItems.table.header.name.title'/>
            </th>
            <th class="atg_numberValue">
              <fmt:message key='finishReturn.returnItems.table.header.actualRefundAmount.title'/>
            </th>
            <th>&nbsp;</th>
        </thead>

        <%-- Loop each of the return items --%>
          <c:forEach items="${returnRequest.returnItemList}"
                      var="returnItem" varStatus="riIndex">
          <tr>
            <td class="atg_numberValue">
              <web-ui:formatNumber value="${returnItem.quantityToReturn}"/>
            </td>
            <td>
              <c:out value="${returnItem.commerceItem.catalogRefId}"/>
            </td>
            <td>
              <dsp:tomap var="sku" value="${returnItem.commerceItem.auxiliaryData.catalogRef}"/>
              <c:out value="${sku.displayName}"/>
            </td>
            <td class="atg_numberValue">
              <csr:formatNumber value="${returnItem.refundAmount}" type="currency" currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
            </td>
            <td></td>
          </tr>
          </c:forEach>
      </table>

</div>
  </dsp:layeredBundle>

</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/finishItemsSummary.jsp#2 $$Change: 1179550 $--%>
