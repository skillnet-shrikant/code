<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/return/itemsReturned.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<!-- begin ordersummary/return/itemsReturned.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
<dsp:importbean bean="/atg/commerce/custsvc/returns/GetReturnRequestItemCountDroplet"/>

<dsp:getvalueof var="step" param="step"/>
<c:if test="${!empty returnRequest}">

<dsp:droplet name="GetReturnRequestItemCountDroplet">
  <dsp:param name="item" value="${returnRequest}"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="returnItemCount" param="itemCount"/>
  </dsp:oparam>
</dsp:droplet>


<dt>
    <a style="text-decoration: underline;" href="#" onclick="atg.commerce.csr.openPanelStack('cmcReturnsPS');return false;"><fmt:message key="orderSummary.itemsReturned"/></a>
  </dt>
  <dd>
    <span id="atg_csc_ordersummary_itemsReturned">
      <fmt:message key="orderSummary.numItems">
        <fmt:param>
          <web-ui:formatNumber value="${returnItemCount}" type="number"/>
        </fmt:param>
      </fmt:message>
    </span>
  </dd>
  <dt>
    <em><fmt:message key="orderSummary.returnCredit"/></em>
  </dt>
  <dd>
    <span id="atg_csc_ordersummary_returnCredit">
      <csr:formatNumber value="${-returnRequest.totalRefundAmount}" type="currency" currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
    </span>
  </dd>
 </c:if>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/return/itemsReturned.jsp#2 $$Change: 1179550 $--%>
