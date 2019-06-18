<%--
Display details of the shopping cart. Use the 
finishOrderCartLineItem.jsp to render each commerce item.

Expected params
currentOrder : The order that the shopping cart details are retrieved from.

@version $Id: 
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">

  <dsp:importbean bean="/atg/commerce/custsvc/order/GetTotalAppeasementsForOrderDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/appeasement/GetTotalAmountOfAppeasementByTypeDroplet"/>
  
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  
  <dsp:getvalueof var="order" param="currentOrder"/>
  <dsp:getvalueof var="isExistingOrderView" param="isExistingOrderView"/>
  
  <c:if test="${empty isExistingOrderView}">
    <c:set var="isExistingOrderView" value="false" />
  </c:if>
  
  <csr:renderer name="/atg/commerce/custsvc/ui/renderers/ItemDescription">
      <jsp:attribute name="setPageData">
      </jsp:attribute>
      <jsp:body>
        <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
          <dsp:param name="currentOrder" value="${order}"/>
          <dsp:param name="isExistingOrderView" value="${isExistingOrderView}"/>
        </dsp:include>
      </jsp:body>
    </csr:renderer>
  
  <div class="atg_commerce_csr_orderReview">
  <%-- Display Promotions --%> 
  
  <div class="atg_commerce_csr_orderModifications">
  <dsp:include src="/include/order/promotionsSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
    <dsp:param name="order" value="${order}" />
  </dsp:include>
  </div>
  
  <%-- Display Pricing Summary --%> 
  <csr:displayOrderSummary order="${order}" isShowHeader="false"/>
  <csr:getCurrencyCode order="${order}">
   <c:set var="currencyCode" value="${currencyCode}" scope="request" />
  </csr:getCurrencyCode>
  
  <%-- Table to display Order Adjustments and appeasements for order --%>
  <table class="atg_dataForm atg_commerce_csr_appeasementsDetails">
    <tbody>
    <dsp:droplet name="GetTotalAppeasementsForOrderDroplet">
      <dsp:param name="order" value="${order}"/>
      <dsp:param name="elementName" value="totalAppeasements"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="totalAppeasements" param="totalAppeasements"/>
        <tr>
          <td><strong><fmt:message key='view.order.appeasementTotal'/></strong></td> 
          <td class="atg_commerce_csr_appeasementsDetailsValue"><csr:formatNumber value="${totalAppeasements}" type="currency" currencyCode="${currencyCode}"/></td>
        </tr>
      </dsp:oparam>
    </dsp:droplet>

  <%-- Table to display appeasements after order is placed (items, shipping, taxes, customer appreciation) --%>    
    <dsp:droplet name="GetTotalAmountOfAppeasementByTypeDroplet">
      <dsp:param name="orderId" value="${order.id}"/>
      <dsp:param name="appeasementType" value="items"/>
      <dsp:setvalue param="total" paramvalue="element"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="total" param="total"/>
        <%-- Only display items appeasement total if it has a value of greater than 0 --%>
        <c:if test="${total > 0}">
          <tr>
            <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">
              <td><strong><fmt:message key='appeasement.shoppingCart.items.orderAppeasementsTotal'/></strong></td>
              <td class="atg_commerce_csr_appeasementsDetailsValue"><csr:formatNumber value="${0-total}" type="currency" currencyCode="${currencyCode}"/></td>
            </dsp:layeredBundle>
          </tr>
        </c:if>
      </dsp:oparam>
    </dsp:droplet>

    <dsp:droplet name="GetTotalAmountOfAppeasementByTypeDroplet">
      <dsp:param name="orderId" value="${order.id}"/>
      <dsp:param name="appeasementType" value="shipping"/>
      <dsp:setvalue param="total" paramvalue="element"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="total" param="total"/>
        <%-- Only display shipping appeasement total if it has a value of greater than 0 --%>
        <c:if test="${total > 0}">
          <tr>
            <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">
              <td><strong><fmt:message key='appeasement.shoppingCart.shipping.orderAppeasementsTotal'/></strong></td>
              <td class="atg_commerce_csr_appeasementsDetailsValue"><csr:formatNumber value="${0-total}" type="currency" currencyCode="${currencyCode}"/></td>
            </dsp:layeredBundle>
          </tr>
        </c:if>
      </dsp:oparam>
    </dsp:droplet>

    <dsp:droplet name="GetTotalAmountOfAppeasementByTypeDroplet">
      <dsp:param name="orderId" value="${order.id}"/>
      <dsp:param name="appeasementType" value="taxes"/>
      <dsp:setvalue param="total" paramvalue="element"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="total" param="total"/>
        <%-- Only display items appeasement total if it has a value of greater than 0 --%>
        <c:if test="${total > 0}">
          <tr>
            <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">
              <td><strong><fmt:message key='appeasement.shoppingCart.taxes.orderAppeasementsTotal'/></strong></td>
              <td class="atg_commerce_csr_appeasementsDetailsValue"><csr:formatNumber value="${0-total}" type="currency" currencyCode="${currencyCode}"/></td>
            </dsp:layeredBundle>
          </tr>
        </c:if>
      </dsp:oparam>
    </dsp:droplet>

    <dsp:droplet name="GetTotalAmountOfAppeasementByTypeDroplet">
      <dsp:param name="orderId" value="${order.id}"/>
      <dsp:param name="appeasementType" value="appreciation"/>
      <dsp:setvalue param="total" paramvalue="element"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="total" param="total"/>
        <%-- Only display items appeasement total if it has a value of greater than 0 --%>
        <c:if test="${total > 0}">
          <tr>
            <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">
              <td><strong><fmt:message key='appeasement.shoppingCart.appreciation.orderAppeasementsTotal'/></strong></td>
              <td class="atg_commerce_csr_appeasementsDetailsValue"><csr:formatNumber value="${0-total}" type="currency" currencyCode="${currencyCode}"/></td>
            </dsp:layeredBundle>
          </tr>
        </c:if>
      </dsp:oparam>
    </dsp:droplet>
    </tbody>
  </table>
  
  </div>
  
  </dsp:layeredBundle>
</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/shoppingCartSummary.jsp#2 $$Change: 1179550 $--%>
