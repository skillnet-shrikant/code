<%--
 Related Orders
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returnsHistory.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@  include file="/include/top.jspf"%>

<dsp:page xml="true">
<dsp:getvalueof var="viewOrderId" bean="atg/commerce/custsvc/order/ViewOrderHolder.current.id"/>
<dsp:getvalueof var="historyReturnRequestId" param="historyReturnRequestId"/>
<dsp:getvalueof var="originatingOrderId" param="originatingOrderId"/>

<dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

<%-- start of left side selection list --%>

<dsp:droplet name="/atg/commerce/custsvc/returns/GetRelatedReturnRequests">
<dsp:param value="${viewOrderId}" name="orderId" />
<dsp:oparam name="output">
  <div class="atg_store_csr_returnOrderHistory">
  <ul class="atg_commerce_csr_orderHistoryOrderList">
  
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
    <dsp:param param="relatedReturnRequests" name="array" />
    <dsp:param value="returnRequestItem" name="elementName" />
    <dsp:param name="sortProperties" value="-createdDate"/>
    <dsp:oparam name="output">
    
      <dsp:tomap var="returnRequestMap" param="returnRequestItem"/>
  
      <c:choose>
      <c:when test="${!empty historyReturnRequestId && historyReturnRequestId eq returnRequestMap.repositoryId}">
       <li  class="active">
         <web-ui:formatDate type="date" value="${returnRequestMap.createdDate}" dateStyle="short"  />
         <fmt:message key="returnsHistory.rma"/>&nbsp;<dsp:valueof param="returnRequestItem.rma"/>
         <c:if test="${!empty returnRequestMap.replacementOrderId}">
           (<fmt:message key="returnsHistory.exchange"/>)
         </c:if>
       </li>
       </c:when>
       <c:otherwise>
       <li>
         <web-ui:formatDate type="date" value="${returnRequestMap.createdDate}" dateStyle="short"  />
         <a href="#" onclick="atg.commerce.csr.order.returns.selectReturnRequest('<c:out value="${returnRequestMap.repositoryId}"/>');return false;">
         <fmt:message key="returnsHistory.rma"/>&nbsp;<c:out value="${returnRequestMap.rma}"/>
         <c:if test="${!empty returnRequestMap.replacementOrderId}">
           (<fmt:message key="returnsHistory.exchange"/>)
         </c:if>
    
         </a>
       </li>
       </c:otherwise>
       </c:choose>
    
    <!-- end ForEach -->
    </dsp:oparam>
    </dsp:droplet>
  
    <dsp:getvalueof var="firstReturnOriginatingOrderId" param="relatedReturnRequests[0].orderId"/>
    <c:if test="${firstReturnOriginatingOrderId != viewOrderId}">
    <dsp:droplet name="/atg/commerce/custsvc/order/OrderItemLookup">
    <dsp:param name="id" value="${firstReturnOriginatingOrderId}"/>
    <dsp:oparam name="output">
    
      <dsp:tomap var="order" param="element"/>
        <c:choose>
        <c:when test="${!empty originatingOrderId}">
         <li  class="active">
         <web-ui:formatDate type="date" value="${order.submittedDate}" dateStyle="short"  />
         <fmt:message key="returnsHistory.originalOrder"/>
        </li>
        </c:when>
        <c:otherwise>
        <li>
          <web-ui:formatDate type="date" value="${order.submittedDate}" dateStyle="short"  />
          <a href="#" onclick="atg.commerce.csr.order.returns.selectOriginatingOrder('<c:out value="${firstReturnOriginatingOrderId}"/>');return false;">
          <fmt:message key="returnsHistory.originalOrder"/>
          </a>
        </li>
        </c:otherwise>
        </c:choose>
        <!-- end OrderItemLookup -->
    </dsp:oparam>
    </dsp:droplet>
    </c:if>
  
  </ul>
  </div>
<!-- end GetRelatedReturnRequests -->


  <div class="atg_commerce_csr_orderHistoryOrderDetails">
  
  <c:choose>
  <c:when test="${!empty historyReturnRequestId}">
  
    <!-- this is what's displayed when a return request is clicked in the list -->
  
    <%-- load the return request --%>
    <dsp:droplet name="/atg/commerce/custsvc/returns/AdminReturnRequestLookup">
    <dsp:param value="${historyReturnRequestId}" name="returnRequestId" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="returnRequest" param="result"/>
    </dsp:oparam>
    </dsp:droplet>
  
    <%-- display RMA and determine if there's an exchange order --%>
    
    <c:set var="exchangeOrder" value="${null}"/>
    <h4 class="atg_commerce_csr_orderTitle">
      <fmt:message key="returnsHistory.rma"/>&nbsp; <dsp:valueof value="${returnRequest.authorizationNumber}"/> 
      <c:if test="${!empty returnRequest.replacementOrder}">
        (<fmt:message key="returnsHistory.exchange"/>)
        <c:set var="exchangeOrder" value="${returnRequest.replacementOrder}"/>
      </c:if>
    </h4>
    
    <div class="atg_commerce_csr_corePanelData">
  
      <%-- display the refund details --%>
  
      <dsp:include src="/panels/order/returns/refundDetails.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="returnRequest" value="${returnRequest}"/>
      <dsp:param name="modifiable" value="false"/>
      </dsp:include>
  
      <%-- determine if we need to display refund types  --%>
  
      <c:set var="isDisplayRefundType" value="${true}"/>
      <c:if test="${!empty exchangeOrder}">
        <c:if test="${exchangeOrder.priceInfo.total ge returnRequest.totalRefundAmount}">
          <c:set var="isDisplayRefundType" value="${false}"/>
        </c:if>
      </c:if>
      
      <%-- display refund types  --%>
      <c:if test="${isDisplayRefundType}">
       <div class="atg_commerce_csr_subPanel">
         <div class="atg_commerce_csr_subPanelHeader">
           <ul class="atg_commerce_csr_panelToolBar">
             <li class="atg_commerce_csr_header">
               <h4><fmt:message key='finishReturn.refundTypes.table.header'/></h4>
             </li>
           </ul>
         </div>
         <dsp:include src="/panels/order/returns/finishRefundSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
           <dsp:param name="returnRequest" value="${returnRequest}"/>
         </dsp:include>
       </div>
       </c:if>
     </div>
  
       <!--display exchange order info, if there is one -->
       <c:if test="${!empty exchangeOrder}">
  
         <%-- display a link for viewing the order in existing order view --%>
  
         <h4 class="atg_commerce_csr_orderTitle">
           <fmt:message key="returnsHistory.exchangeOrder"/>&nbsp;<strong><a href="#" class="blueU" onclick="atg.commerce.csr.order.viewExistingOrder('${exchangeOrder.id}','${exchangeOrder.stateAsString}');return false;">${fn:escapeXml(exchangeOrder.id)}</a></strong>
         </h4>
  
         <div class="atg_commerce_csr_corePanelData">
       
         <%-- display exhange cart summary --%>
         
         <dsp:include src="/panels/order/finish/shoppingCartSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
         <dsp:param name="currentOrder" value="${exchangeOrder}"/>
         <dsp:param name="isExistingOrderView" value="${false}"/>
         </dsp:include>
       
         <div class="atg_commerce_csr_subPanel">
         <div class="atg_commerce_csr_subPanelHeader" >
         <ul class="atg_commerce_csr_panelToolBar">
           <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
           <li class="atg_commerce_csr_header">
           <h4><fmt:message key="finishOrder.billingSummary.header"/></h4></li>
           </dsp:layeredBundle>
         </ul>
         </div>
         <dsp:include src="/panels/order/finish/billingSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
         <dsp:param name="currentOrder" value="${exchangeOrder}"/>
         <dsp:param name="isExistingOrderView" value="${false}"/>
         </dsp:include>
         </div>
       </c:if>
      
       </div>
    
  </c:when>
  <c:when test="${!empty originatingOrderId}">
  
    <%-- this is what's displayed when the original order is clicked in the list --%>
  
  
  <%-- Load the order --%>
  <dsp:droplet name="/atg/commerce/order/AdminOrderLookup">
  <dsp:param name="orderId" value="${originatingOrderId}"/>
  <dsp:oparam name="output">
    
    <dsp:getvalueof var="originatingOrder" param="result"/>
    
    <%-- display a link for viewing the order in existing order view --%>
  
    <h4 class="atg_commerce_csr_orderTitle">
      <fmt:message key="returnsHistory.originalOrder"/>&nbsp;<strong><a href="#" class="blueU" onclick="atg.commerce.csr.order.viewExistingOrder('${originatingOrder.id}','${originatingOrder.stateAsString}');return false;">${fn:escapeXml(originatingOrder.id)}</a></strong>
    </h4>
  
    
    <div class="atg_commerce_csr_corePanelData">
  
      <%-- display cart summary --%>
      
      <dsp:include src="/panels/order/finish/shoppingCartSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="currentOrder" value="${originatingOrder}"/>
      <dsp:param name="isExistingOrderView" value="${false}"/>
      </dsp:include>
      
      <%-- display billing summary --%>
  
      <div class="atg_commerce_csr_subPanel">
      <div class="atg_commerce_csr_subPanelHeader" >
      <ul class="atg_commerce_csr_panelToolBar">
        <li class="atg_commerce_csr_header">
        <h4>Billing </h4></li>
      </ul>
      </div>
      <dsp:include src="/panels/order/finish/billingSummary.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="currentOrder" value="${originatingOrder}"/>
      <dsp:param name="isExistingOrderView" value="${false}"/>
      </dsp:include>
      </div>
  
    </div>
    
  </dsp:oparam>
  </dsp:droplet>
  
  </c:when>
  <c:otherwise>
    <!-- this is what's displayed when nothing is selected in the list -->
    
    <div class="atg_commerce_csr_corePanelData">
      <fmt:message key="returnsHistory.pleaseSelectLink"/>
    </div>
  </c:otherwise>
  </c:choose>
  
  </div>

</dsp:oparam>

<dsp:oparam name="empty">

<div class="atg_commerce_csr_corePanelData">
  <fmt:message key="returnsHistory.noReturns"/>
</div>


</dsp:oparam>
</dsp:droplet>

<%-- end of left side selection list --%>




</dsp:layeredBundle>



</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returnsHistory.jsp#1 $$Change: 946917 $--%>
