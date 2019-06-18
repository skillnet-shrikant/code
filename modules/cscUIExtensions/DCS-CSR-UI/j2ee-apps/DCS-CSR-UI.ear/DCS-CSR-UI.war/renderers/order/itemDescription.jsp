
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
  	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnDroplet"/>
    <dsp:importbean	bean="/com/mff/commerce/order/MFFCSCTasksOnOrderFormHandler"/>
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
			<script type="text/javascript">
				function cancelSelectedItems() {
					atgSubmitAction({
						form : dojo.byId("orderItemsMainForm")
					});
				}
			</script>
      <dsp:getvalueof var="order" param="currentOrder"/>
      <dsp:getvalueof var="isExistingOrderView" param="isExistingOrderView"/>
      <dsp:getvalueof var="sourceForceAllocation" param="sourceForceAllocation"/>
      
      <c:set var="sentToStoreItems" value="false"/>
	  <c:choose>
		<c:when test="${sourceForceAllocation}">
			<c:set var="orderItemsMainform" value="orderItemsForceAllocForm"/>
		</c:when>
	  	<c:otherwise>
			<c:set var="orderItemsMainform" value="orderItemsMainForm"/>
	  	</c:otherwise>
	  </c:choose>
	  <svc-ui:frameworkUrl var="successErrorURL"
		panelStacks="globalPanels,cmcExistingOrderPS" contentHeader="true" />
	  <dsp:setvalue bean="MFFCSCTasksOnOrderFormHandler.inputSize" value="${fn:length(order.commerceItems)}"/>
	  <dsp:form method="post" name="${orderItemsMainform}" id="${orderItemsMainform}" formid="${orderItemsMainform}">
		  <c:choose>
			<c:when test="${sourceForceAllocation}">
				<dsp:input type="hidden" priority="-10" value="" bean="MFFCSCTasksOnOrderFormHandler.forceAllocateAndCancelItem"/>
			</c:when>
		  	<c:otherwise>
				<dsp:input type="hidden" priority="-10" value="" bean="MFFCSCTasksOnOrderFormHandler.cancelItem"/>
		  	</c:otherwise>
		  </c:choose>
		<dsp:input type="hidden" priority="10" value="${fn:length(order.commerceItems)}" bean="MFFCSCTasksOnOrderFormHandler.inputSize"/>
		<dsp:input bean="MFFCSCTasksOnOrderFormHandler.submitAction" name="submitAction" type="hidden" value=""/>
		<dsp:input bean="MFFCSCTasksOnOrderFormHandler.orderId" type="hidden" value="${order.id}" />
		<dsp:input bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderSuccessUrl" type="hidden" value="${successErrorURL}" />
		<dsp:input bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderErrorUrl" type="hidden" value="${successErrorURL}" />
      <table class="atg_dataTable atg_commerce_csr_innerTable"
        cellspacing="0" cellpadding="0" summary="<fmt:message key='shoppingCartSummary.itemDetails' />">
        <thead>
          <tr>
            <c:if test="${isMultiSiteEnabled == true}">
              <th></th>
            </c:if>
            <th><fmt:message key='shoppingCartSummary.commerceItem.header.itemDesc' /></th>
            <th><fmt:message key='shoppingCartSummary.commerceItem.header.status' /></th>

            <th class="atg_numberValue"><fmt:message key='shoppingCartSummary.commerceItem.header.qty' /></th>
            <th class="atg_numberValue"><fmt:message key='shoppingCartSummary.commerceItem.header.priceEach' /></th>
            <th class="atg_numberValue"><fmt:message key='shoppingCartSummary.commerceItem.header.totalPrice' /></th>
            <th class="atg_numberValue"><fmt:message key='shoppingCartSummary.commerceItem.header.priceOverride' /></th>
            <th class="atg_numberValue">Tracking Number</th>
            <c:choose>
				<c:when test="${sourceForceAllocation}">
					 <th class="atg_numberValue">Cancel/Allocate</th>
				</c:when>
			  	<c:otherwise>
					 <th class="atg_numberValue">Cancel</th>
			  	</c:otherwise>
			 </c:choose>
            <th class="atg_numberValue">Split Item</th>
          </tr>
        </thead>
        <tbody>
          <%-- Iterate items in the cart --%>
          <c:forEach items="${order.commerceItems}" var="commerceItem"
            varStatus="commerceItemIndex">
			<c:choose>
	        	<c:when test="${sourceForceAllocation}">
	        		<c:if test="${commerceItem.stateAsString == 'FORCED_ALLOCATION' || (commerceItem.stateAsString == 'PENDING_GC_FULFILLMENT' && commerceItem.gwp)}">
			            <dsp:include src="/panels/order/finish/finishOrderCartLineItem.jsp" otherContext="${CSRConfigurator.contextRoot}"
			              flush="false">
			              <dsp:param name="order" value="${order}" />
			              <dsp:param name="commerceItem" value="${commerceItem}" />
			              <dsp:param name="commerceItemIndex"
			                value="${commerceItemIndex.index}" />
			              <dsp:param name="currencyCode"
			                value="${order.priceInfo.currencyCode}" />
			                <dsp:param name="sourceForceAllocation"
			                value="${sourceForceAllocation}" />
			            </dsp:include>
          			</c:if>
				</c:when>
				<c:otherwise>
		            <dsp:include src="/panels/order/finish/finishOrderCartLineItem.jsp" otherContext="${CSRConfigurator.contextRoot}"
		              flush="false">
		              <dsp:param name="order" value="${order}" />
		              <dsp:param name="commerceItem" value="${commerceItem}" />
		              <dsp:param name="commerceItemIndex"
		                value="${commerceItemIndex.index}" />
		              <dsp:param name="currencyCode"
		                value="${order.priceInfo.currencyCode}" />
		                <dsp:param name="sourceForceAllocation"
		                value="${sourceForceAllocation}" />
		            </dsp:include>
				</c:otherwise>
			</c:choose>
			<c:if test="${commerceItem.stateAsString == 'SENT_TO_STORE' || commerceItem.stateAsString == 'PENDING_GC_FULFILLMENT'}">
				<c:set var="sentToStoreItems" value="true"/>
			</c:if>
          </c:forEach>

          <%-- Render Returned Items if in Existing Order View --%>
          <c:if test="${isExistingOrderView}">
            <dsp:droplet name="ReturnDroplet">
              <dsp:param name="orderId" value="${order.id}" />
              <dsp:param name="resultName" value="returns" />
              <dsp:oparam name="error">
                <tr>
                  <td><dsp:valueof param="errorMessage">
                    <fmt:message key="shoppingCartSummary.returnListError" />
                  </dsp:valueof></td>
                </tr>
              </dsp:oparam>
              <dsp:oparam name="output">
                <dsp:getvalueof var="returnList" param="returns" />
                <c:choose>
                  <c:when test="${ not empty returnList }">
                    <%-- Loop Return Requests --%>
                    <c:forEach var="request" items="${returnList}" varStatus="rowCounter">
                      <dsp:tomap var="returnRequestMap" value="${request}" />

                      <dsp:droplet name="/atg/commerce/custsvc/returns/AdminReturnRequestLookup">
                        <dsp:param name="returnRequestId" value="${request.repositoryId}" />
                        <dsp:oparam name="output">

                          <dsp:getvalueof var="returnObject" param="result" />

                          <%-- Loop Items in the Return Request --%>
                          <c:forEach var="returnItem" items="${returnObject.returnItemList}" varStatus="riIndex">
                            <dsp:include src="/panels/order/finish/finishOrderReturnLineItem.jsp" otherContext="${CSRConfigurator.contextRoot}" flush="false">
                              <dsp:param name="returnItem" value="${returnItem}" />
                            </dsp:include>
                          </c:forEach>

                        </dsp:oparam>
                      </dsp:droplet>
                    </c:forEach>
                  </c:when>
                </c:choose>
              </dsp:oparam>
            </dsp:droplet>
          </c:if>

        </tbody>
      </table>
		<c:if test="${order.stateAsString == 'IN_REMORSE' || sentToStoreItems && !sourceForceAllocation}">
			Select Cancellation Reason : 
			<c:set var="beanName">CSRAgentTools.ppsCancelReasonCodes</c:set>
			<c:if test="${order.bopisOrder}">
				<c:set var="beanName">CSRAgentTools.bopisCancelReasonCodes</c:set>
			</c:if>
			<dsp:select bean="MFFCSCTasksOnOrderFormHandler.cancelOrderReasonCode" id="cancelReasonCode" name="cancelReasonCode">
				 <dsp:droplet name="ForEach">
	        		<dsp:param name="array" bean="${beanName}"/>
	        		 <dsp:param name="elementName" value="reasonCodes" />
	        			<dsp:oparam name="output">
	        				<dsp:getvalueof var="key" param="key" />
	        				<dsp:getvalueof var="reasonCode" param="reasonCodes" />
	        				<dsp:option value="${key}"><c:out value="${reasonCode}" /></dsp:option>
	        			</dsp:oparam>
	        	 </dsp:droplet>
        	 </dsp:select>
        	<input type="button" name="cancelItemButton" id="cancelItemButton" class="atg_commerce_csr_activeButton" onclick="cancelSelectedItems();" value="Cancel Items" />
        </c:if>
        <c:if test="${sourceForceAllocation}">
	        <div class="atg-csc-base-table-row">
				<span
					class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-label">
					<label for="orderNumber"> Order id: </label>
				</span>
				<div class="atg-csc-base-table-cell">
					<dsp:valueof value="${order.id}" />
					<dsp:input bean="MFFCSCTasksOnOrderFormHandler.orderId"
						type="hidden" value="${order.id}" />
				</div>
				<span class="atg_commerce_csr_fieldTitle"> <label
					for="reasonCode"> Store: </label></span>
				<div
					class="atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<dsp:getvalueof var="storeCodeValue"
						bean="MFFCSCTasksOnOrderFormHandler.storeCode" />
					<%-- Full list of stores sorted by code id --%>
					<dsp:select id="storeCode" name="storeCode"
						bean="MFFCSCTasksOnOrderFormHandler.storeCode">
						<dsp:option>Select a store</dsp:option>
						<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
							<dsp:param name="queryRQL" value="ALL" />
							<dsp:param name="repository"
								value="/atg/commerce/locations/LocationRepository" />
							<dsp:param name="itemDescriptor" value="location" />
							<dsp:param name="sortProperties" value="+city"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="currentLocationId" param="element.locationId"/>
								<dsp:option value="${currentLocationId}"
									selected="${storeCodeValue == currentLocationId}">
									<dsp:valueof param="element.city"/>${currentLocationId}
								</dsp:option>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:select>
				</div>
				<div class="atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<c:set var="beanName">CSRAgentTools.ppsCancelReasonCodes</c:set>
					<c:if test="${order.bopisOrder}">
						<c:set var="beanName">CSRAgentTools.bopisCancelReasonCodes</c:set>
					</c:if>				
					Select Cancellation Reason : 
					<dsp:select bean="MFFCSCTasksOnOrderFormHandler.cancelOrderReasonCode" id="cancelReasonCode" name="cancelReasonCode">
						 <dsp:droplet name="ForEach">
			        		<dsp:param name="array" bean="${beanName}"/>
			        		 <dsp:param name="elementName" value="reasonCodes" />
			        			<dsp:oparam name="output">
			        				<dsp:getvalueof var="key" param="key" />
			        				<dsp:getvalueof var="reasonCode" param="reasonCodes" />
			        				<dsp:option value="${key}"><c:out value="${reasonCode}" /></dsp:option>
			        			</dsp:oparam>
			        	 </dsp:droplet>
		        	 </dsp:select>
	        	 </div>
			</div>
		</c:if>
	  </dsp:form>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <% 
    Exception ee = (Exception) pageContext.getAttribute("exception"); 
    ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/itemDescription.jsp#1 $$Change: 946917 $--%>
