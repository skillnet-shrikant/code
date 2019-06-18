<%--
 This page displays the change in promotion value for a return
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/promotionsLost.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
  <dsp:page xml="true">

<dsp:getvalueof var="returnRequest" param="returnRequest"/>

<dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

<dsp:droplet name="/atg/commerce/custsvc/returns/LostPromotions">
<dsp:param value="${returnRequest}" name="returnRequest" />
<dsp:oparam name="output">

  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
  <dsp:param name="array" param="promotionsLost"/>
  <dsp:param name="elementName" value="promotionId"/>
  <dsp:oparam name="outputStart">
  <div class="atg_commerce_csr_orderModifications">
    <div class="atg_commerce_csr_promotionsBox">
      <div class="atg_commerce_csr_promotionsListing">
      <table class="atg_dataTable">
       <thead>
           <th class="atg_commerce_csr_promotionsBoxTitle"><fmt:message key="promotionsLost.noLongerQualifying"/></th>               
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
    <dsp:param name="id" param="promotionId"/>
    <dsp:param name="elementName" value="promotion"/>
    <dsp:oparam name="output">
        <dsp:tomap var="promotionMap" param="promotion"/>
        <tr>
        <td>${fn:escapeXml(promotionMap.displayName)}</td>               
      </tr>
    </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
  </dsp:droplet>

</dsp:oparam>
<dsp:oparam name="empty">
</dsp:oparam>
</dsp:droplet>


</dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/promotionsLost.jsp#1 $$Change: 946917 $--%>
