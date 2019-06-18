<%--
 This page defines the return summary panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnSummary.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

    <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>

    <c:set var="returnObject" value="${cart.returnRequest}"/>

    <!--#########  Returns panel return items start  #########-->
    <div class="atg_commerce_csr_subPanel">
      <table class="atg_dataTable atg_commerce_csr_billingTable">
        <thead>
          <tr>

            <th class="atg_numberValue"><fmt:message key='returnSummary.table.header.quantityToReturn.title'/></th>
            <th><fmt:message key='returnSummary.table.header.sku.title'/></th>
            <th><fmt:message key='returnSummary.table.header.name.title'/></th>
            <th class="atg_numberValue"><fmt:message key='finishReturn.returnItems.table.header.actualRefundAmount.title'/></th>
          </tr>
        </thead>

        <c:forEach var="returnItem" items="${returnObject.returnItemList}" varStatus="rowCounter">
          <%@ include file="returnSummaryLineItem.jspf"%>
        </c:forEach>
      </table>

    </div>
    <!--#########   Returns panel return items end  ###########-->

  </dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnSummary.jsp#1 $$Change: 946917 $--%>
