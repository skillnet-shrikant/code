<%--
Display details of all the promotions in the order. The following promotions are
shown:

Order Level Promotions
Item Level Promotions
Shipping Level Promotions
Tax Level Promotions

Expected params
currentOrder : The order that the promotion details are retrieved from.

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>

<%@ include file="../top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  <dsp:getvalueof var="order" param="order"/>
  
  <%-- PromotionViewDroplet is only used here to determine if there are any promotions applied to the order so we can avoid display the table when there are none --%>
  <dsp:importbean bean="/atg/commerce/custsvc/promotion/PromotionViewDroplet"/>
  <dsp:droplet name="PromotionViewDroplet">
  <dsp:param name="byType" value="${false}"/>
  <dsp:param name="order" value="${order}"/>
  <dsp:oparam name="output">

    <div class="atg_commerce_csr_promotionsBox">
      <div class="atg_commerce_csr_promotionsListing">
        <table class="atg_dataTable">
        <thead>
          <th class="atg_commerce_csr_promotionsBoxTitle" style="background-color:#E5E5E5;padding-top:6px;padding-bottom:6px;"><fmt:message key="shoppingCartSummary.promotionsSummary.description"/></th>
          <th class="atg_commerce_csr_promotionsBoxTitle" style="background-color:#E5E5E5;padding-top:6px;padding-bottom:6px;"></th>
        </thead>
        <tbody>
          <%-- Show Order level promotions --%>
          <c:forEach items="${order.priceInfo.adjustments}"
                            var="orderAdjustment" varStatus="orderAdjustmentIndex">
            <c:if test="${not empty orderAdjustment.pricingModel}">
              <tr>
              <td>
                <c:out value="${orderAdjustment.adjustmentDescription}" />&nbsp;
                <dsp:tomap var="orderPM" value="${orderAdjustment.pricingModel}"/>
                <c:out value="${orderPM.displayName}" />
                </td>
              <td class="atg_numberValue">
                <csr:formatNumber value="${orderAdjustment.totalAdjustment}" type="currency"  currencyCode="${order.priceInfo.currencyCode}"/>
               </td>
              </tr>
            </c:if>
          </c:forEach>
      
          <%-- Show Item level promotions --%>
          <c:forEach items="${order.commerceItems}"
                            var="commerceItem" varStatus="commerceItemIndex">
      
            <%-- Iterate adjustments in each item --%>
            <c:forEach items="${commerceItem.priceInfo.adjustments}"
                            var="itemAdjustment" varStatus="itemAdjustmentIndex">
              <c:if test="${not empty itemAdjustment.pricingModel}">
                <tr>
                <td>
                  <c:out value="${itemAdjustment.adjustmentDescription}" />
                  (<c:out value="${commerceItem.catalogRefId}" />)&nbsp;
                  <dsp:tomap var="itemPM" value="${itemAdjustment.pricingModel}"/>
                  <c:out value="${itemPM.displayName}" />
                  </td>
                <td class="atg_numberValue">
                  <csr:formatNumber value="${itemAdjustment.totalAdjustment}" type="currency"  currencyCode="${order.priceInfo.currencyCode}"/>
                 </td>
                </tr>
      
              </c:if>
            </c:forEach>
          </c:forEach>
      
          <%-- Show Shipping level promotions --%>
          <c:forEach items="${order.shippingGroups}"
                            var="shippingGroup" varStatus="shippingGroupIndex">
      
            <%-- Iterate adjustments in each shipping group --%>
            <c:forEach items="${shippingGroup.priceInfo.adjustments}"
                            var="shippingAdjustment" varStatus="shippingAdjustmentIndex">
              <c:if test="${not empty shippingAdjustment.pricingModel}">
                <tr>
                <td>
                  <c:out value="${shippingAdjustment.adjustmentDescription}" />&nbsp;
                  <dsp:tomap var="shippingPM" value="${shippingAdjustment.pricingModel}"/>
                  <c:out value="${shippingPM.displayName}" />
                  </td>
                <td class="atg_numberValue">
                  <csr:formatNumber value="${shippingAdjustment.totalAdjustment}" type="currency"  currencyCode="${order.priceInfo.currencyCode}"/>
                 </td>
                </tr>
      
              </c:if>
            </c:forEach>
          </c:forEach>
      
          <%-- Show Tax level promotions --%>
          <c:forEach items="${order.taxPriceInfo.adjustments}"
                            var="taxAdjustment" varStatus="taxAdjustmentIndex">
            <c:if test="${not empty taxAdjustment.pricingModel}">
              <tr>
              <td>
                <c:out value="${taxAdjustment.adjustmentDescription}" />&nbsp;
                <dsp:tomap var="taxPM" value="${taxAdjustment.pricingModel}"/>
                <c:out value="${taxPM.displayName}" />
                </td>
              <td class="atg_numberValue">
                <csr:formatNumber value="${taxAdjustment.totalAdjustment}" type="currency"  currencyCode="${order.priceInfo.currencyCode}"/>
               </td>
              </tr>
      
            </c:if>
          </c:forEach>
          </tbody>
          </table>
        </div>
      </div>
    </dsp:oparam>
    </dsp:droplet>
  </dsp:layeredBundle>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/promotionsSummary.jsp#2 $$Change: 1179550 $--%>
