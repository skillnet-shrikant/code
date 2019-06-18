<%--
 This page defines the list of promotions
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/promotionQualificationSummary.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:page xml="true">
    <dsp:getvalueof var="order" param="order"/>
      
    <csr:getCurrencyCode order="${order}">
      <c:set var="currencyCode" value="${currencyCode}" scope="request" />
    </csr:getCurrencyCode>
        
    <table class="atg_dataTable"> 
      <tbody>
      <%-- Show Item level promotions --%>
      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" value="${order.commerceItems}" />
        <dsp:param name="sortProperties" value="+catalogRefId"/>
        <dsp:oparam name="output">
          <dsp:tomap var="commerceItem" param="element"/>
          <%-- Iterate adjustments in each item --%>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" value="${commerceItem.priceInfo.adjustments}" />
            <dsp:param name="sortProperties" value="+adjustmentDescription,+pricingModel.displayName"/>
            <dsp:oparam name="output">
              <dsp:tomap var="itemAdjustment" param="element"/>
              <c:if test="${not empty itemAdjustment.pricingModel}">
                <tr>
                   <td>
                   <span class="atg_commerce_csr_iconPromoApplied" 
                   title="<fmt:message key='promotionQualificationSummary.promotionApplied'/>">
                   <fmt:message key="promotionQualificationSummary.promotionApplied"/>
                   </span>
                   ${fn:escapeXml(itemAdjustment.adjustmentDescription)}
                   (<c:out value="${commerceItem.catalogRefId}" />)&nbsp;
                   <dsp:tomap var="itemPM" value="${itemAdjustment.pricingModel}"/>         
                   ${fn:escapeXml(itemPM.displayName)}
                   </td>
                   <td class="atg_numberValue"><csr:formatNumber value="${itemAdjustment.totalAdjustment}" type="currency" currencyCode="${currencyCode}" /></td>
                 </tr>
              </c:if>                
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
      <%-- Show Order level promotions --%>
      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" value="${order.priceInfo.adjustments}" />
        <dsp:param name="sortProperties" value="+adjustmentDescription,+pricingModel.displayName"/>
        <dsp:oparam name="output">
          <dsp:tomap var="orderAdjustment" param="element"/>
          <c:if test="${not empty orderAdjustment.pricingModel}">
            <tr>
              <td>
              <span class="atg_commerce_csr_iconPromoApplied" 
                title="<fmt:message key='promotionQualificationSummary.promotionApplied'/>">
                <fmt:message key="promotionQualificationSummary.promotionApplied"/>
              </span>
              ${fn:escapeXml(orderAdjustment.adjustmentDescription)}
              <dsp:tomap var="orderPM" value="${orderAdjustment.pricingModel}"/>
              &nbsp;${fn:escapeXml(orderPM.displayName)}
            </td>
            <td class="atg_numberValue"><csr:formatNumber value="${orderAdjustment.totalAdjustment}" type="currency" currencyCode="${currencyCode}" /></td>
           </tr>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
      <%-- Show Shipping level promotions --%>
      <c:forEach items="${order.shippingGroups}" 
        var="shippingGroup" varStatus="shippingGroupIndex">
        <%-- Iterate adjustments in each shipping group --%>
        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
          <dsp:param name="array" value="${shippingGroup.priceInfo.adjustments}"/>
          <dsp:param name="sortProperties" value="+adjustmentDescription,+pricingModel.displayName"/>
          <dsp:oparam name="output">
            <dsp:tomap var="shippingAdjustment" param="element"/>
            <c:if test="${not empty shippingAdjustment.pricingModel}">
              <tr>
                <td>
                <span class="atg_commerce_csr_iconPromoApplied"
                   title="<fmt:message key='promotionQualificationSummary.promotionApplied'/>">
                  <fmt:message key="promotionQualificationSummary.promotionApplied"/>
                </span>
                ${fn:escapeXml(shippingAdjustment.adjustmentDescription)}
                &nbsp;
                <dsp:tomap var="shippingPM" value="${shippingAdjustment.pricingModel}"/>
                ${fn:escapeXml(shippingPM.displayName)}
                </td>
                <td class="atg_numberValue"><csr:formatNumber value="${shippingAdjustment.totalAdjustment}" type="currency" currencyCode="${currencyCode}" /></td>
              </tr>
            </c:if>   
          </dsp:oparam>
        </dsp:droplet>
      </c:forEach>
      <%-- Show Tax level promotions --%>
      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" value="${order.taxPriceInfo.adjustments}"/>
        <dsp:param name="sortProperties" value="+adjustmentDescription,+pricingModel.displayName"/>
        <dsp:oparam name="output">
          <dsp:tomap var="taxAdjustment" param="element"/>
          <c:if test="${not empty taxAdjustment.pricingModel}">
            <tr>
              <td>
              <span class="atg_commerce_csr_iconPromoApplied"
                 title="<fmt:message key='promotionQualificationSummary.promotionApplied'/>">
                <fmt:message key="promotionQualificationSummary.promotionApplied"/>
              </span>
              ${fn:escapeXml(taxAdjustment.adjustmentDescription)}&nbsp;
              <dsp:tomap var="taxPM" value="${taxAdjustment.pricingModel}"/>
              ${fn:escapeXml(taxPM.displayName)}
              </td>
              <td class="atg_numberValue"><csr:formatNumber value="${taxAdjustment.totalAdjustment}" type="currency" currencyCode="${currencyCode}" /></td>
            </tr>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
      <%-- Nearly qualified for promotions --%>
      <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
        <dsp:droplet name="/atg/commerce/promotion/ClosenessQualifierDroplet">
          <dsp:param name="order" param="order"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="qualifiers" param="closenessQualifiers"/>
            <c:forEach var="cq" items="${qualifiers}"
              end="${CSRConfigurator.maximumAlmostQualifiedForPromotionsInShortList}">
              <tr>
                <td>
                <span class="atg_commerce_csr_iconPromoAlmost"
                   title="<fmt:message key='promotionQualificationSummary.promotionClose'/>">
                  <fmt:message key="promotionQualificationSummary.promotionClose"/>
                </span>
                <dsp:tomap value="${cq}" var="qualifier"/>
                <dsp:tomap value="${qualifier.promotion}" var="qualifierpromo"/>
                ${fn:escapeXml(qualifierpromo.displayName)}
                </td>
                <td class="atg_numberValue"></td>
              </tr>
            </c:forEach>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:layeredBundle>
      </tbody>
    </table>
  </dsp:page>
  </dsp:layeredBundle>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
     Exception ee = (Exception) pageContext.getAttribute("exception");
     ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/promotionQualificationSummary.jsp#2 $$Change: 1179550 $--%>
