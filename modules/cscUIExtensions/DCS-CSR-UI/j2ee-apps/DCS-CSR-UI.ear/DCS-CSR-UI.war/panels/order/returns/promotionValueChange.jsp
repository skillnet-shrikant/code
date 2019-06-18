<%--
 This page displays the change in promotion value for a return
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/promotionValueChange.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
  <dsp:page xml="true">

<dsp:getvalueof var="returnRequest" param="returnRequest"/>
<dsp:getvalueof var="currencyCode" param="currencyCode"/>

<dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">


<dsp:droplet name="/atg/dynamo/droplet/ForEach">
<dsp:param value="${returnRequest.promotionValueAdjustments}" name="array" />
<dsp:param name="elementName" value="changeInValue"/>
<dsp:oparam name="outputStart">
<div class="atg_commerce_csr_orderModifications">
  <div class="atg_commerce_csr_promotionsBox">
    <div class="atg_commerce_csr_promotionsListing">
    <table class="atg_dataTable">
    <thead>
      <th class="atg_commerce_csr_promotionsBoxTitle"><fmt:message key="promotionValueChange.promotion"/></th>
      <th class="atg_commerce_csr_promotionsBoxTitle atg_numberValue"><fmt:message key="promotionValueChange.changeInValue"/></th>
    </thead>
    <tbody>
</dsp:oparam>
<dsp:oparam name="outputEnd">
    </tbody>
    </table>
  </div>
  </div>
  </div>
</dsp:oparam>
<dsp:oparam name="output">

  <dsp:droplet name="/atg/commerce/custsvc/catalog/CSRPromotionLookup">
  <dsp:param name="id" param="key"/>
  <dsp:param name="elementName" value="promotion"/>
  <dsp:oparam name="output">
    <tr>
      <dsp:tomap var="promotionMap" param="promotion"/>
      <td>
      ${fn:escapeXml(promotionMap.displayName)}
      </td>
      <td class="atg_numberValue">
      <dsp:getvalueof var="changeInValue" param="changeInValue"/><csr:formatNumber value="${changeInValue}" type="currency"  currencyCode="${currencyCode}"/>
    
      </td>
      </tr>
  </dsp:oparam>
  </dsp:droplet>

</dsp:oparam>
</dsp:droplet>


</dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/promotionValueChange.jsp#2 $$Change: 1179550 $--%>
