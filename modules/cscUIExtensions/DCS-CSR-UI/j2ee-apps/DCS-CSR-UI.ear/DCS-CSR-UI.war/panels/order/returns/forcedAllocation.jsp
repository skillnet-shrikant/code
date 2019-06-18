<%--
This file gets the order from the orderId parameter and displays
commerce items in forced allocation status.
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
	<dsp:page xml="true">
		<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
		<dsp:importbean
			bean="/com/mff/commerce/order/IsOrderInForceAllocation" />
		<dsp:importbean
			bean="/com/mff/commerce/order/MFFCSCTasksOnOrderFormHandler" />

		<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

			<script type="text/javascript">
				if (!dijit.byId("approvalRejectPopup")) {
					new dojox.Dialog( {
						id :"approvalRejectPopup",
						cacheContent :"false",
						executeScripts :"true",
						scriptHasHooks :"true"
					});
				}
				function submitForceAllocation() {
					popupUrl='${CSRConfigurator.contextRoot}' + '/panels/approvals/readyForceAllocationConfirm.jsp?_windowid=${windowId}';
					var theForm = dojo.byId("orderItemsForceAllocForm");
					theForm.submitAction.value = 'forceAllocation';
				    atg.commerce.csr.common.showPopupWithReturn({
					      popupPaneId: 'approvalRejectPopup',
					      title: "Confirm manual allocation to Store",url: popupUrl,
					      onClose: function(args) {
					        if (args.result == 'reject') {          
								atgSubmitAction({
									form : dojo.byId("orderItemsForceAllocForm"),
							        panels: ["mffForcedAllocationP"]
								});
					        }
					    }});
					return false;
				}
				function submitCancelItems() {
					var theForm = dojo.byId("orderItemsForceAllocForm");
					theForm.submitAction.value = 'cancelItems';
					atgSubmitAction({
						form : dojo.byId("orderItemsForceAllocForm"),
				        panels: ["mffForcedAllocationP"]
					});
				}
				submitSplitItems = function(pCommerceItemId){
				  //alert(pCommerceItemId);
				  var theForm = dojo.byId("splitItemForm");
				  theForm.commerceItemId.value = pCommerceItemId;
					atgSubmitAction({
						form : dojo.byId(theForm),
				        panels: ["mffForcedAllocationP"]
					});
				}
			</script>

			<dsp:getvalueof var="order"
				bean="/atg/commerce/custsvc/order/ViewOrderHolder.current" />
			<dsp:getvalueof var="isExistingOrderView" value="${true}" />

			<dsp:droplet name="IsOrderInForceAllocation">
				<dsp:param name="orderId" value="${order.id}" />
				<dsp:oparam name="true">
					<c:if test="${empty isExistingOrderView}">
						<c:set var="isExistingOrderView" value="false" />
					</c:if>
					<csr:renderer
						name="/atg/commerce/custsvc/ui/renderers/ItemDescription">
						<jsp:attribute name="setPageData"></jsp:attribute>
						<jsp:body>
        					<dsp:include src="${renderInfo.url}"
								otherContext="${renderInfo.contextRoot}">
          						<dsp:param name="currentOrder" value="${order}" />
          						<dsp:param name="isExistingOrderView"
									value="${isExistingOrderView}" />
          						<dsp:param name="sourceForceAllocation" value="true" />
        					</dsp:include>
      					</jsp:body>
					</csr:renderer>
					<!-- Area for Cancel Force allocation items in the order -->
					<c:set var="formId1" value="cancelforceAllocItemsForm" />
					<svc-ui:frameworkUrl var="successErrorURL"
						panelStacks="globalPanels,cmcExistingOrderPS" contentHeader="true" />
					<dsp:form id="${formId1}" formid="${formId1}" name="${formId1}"
						method="post">
						<dsp:input type="hidden" priority="-10" value=""
							bean="MFFCSCTasksOnOrderFormHandler.cancelForceAllocItems" />
						<dsp:input
							bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderSuccessUrl"
							type="hidden" value="${successErrorURL}" />
						<dsp:input
							bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderErrorUrl"
							type="hidden" value="${successErrorURL}" />
						<dsp:input bean="MFFCSCTasksOnOrderFormHandler.orderId"
							type="hidden" value="${order.id}" />
					</dsp:form>
					
					<!-- Area for Split Force allocation items in the order -->
					<c:set var="formId3" value="splitItemForm" />
					<svc-ui:frameworkUrl var="successErrorURL"
						panelStacks="globalPanels,cmcExistingOrderPS" contentHeader="true" />
					<dsp:form id="${formId3}" formid="${formId3}" name="${formId3}" method="post">
						<dsp:input type="hidden" priority="-10" value="" bean="MFFCSCTasksOnOrderFormHandler.splitItems" />
						<dsp:input bean="MFFCSCTasksOnOrderFormHandler.orderId" type="hidden" value="${order.id}" />
						<dsp:input bean="MFFCSCTasksOnOrderFormHandler.splitCommerceItemId" name="commerceItemId" type="hidden" value="" />
						<dsp:input bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderSuccessUrl" type="hidden" value="${successErrorURL}" />
						<dsp:input bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderErrorUrl" type="hidden" value="${successErrorURL}" />
					</dsp:form>
					
					<!-- Area for Force the allocation to a store -->
					<%-- <c:set var="formId2" value="forceAllocationForm" />
					<svc-ui:frameworkUrl var="successErrorURL"
						panelStacks="globalPanels,cmcExistingOrderPS" contentHeader="true" />
					<dsp:form id="${formId2}" formid="${formId2}" name="${formId2}"
						method="post">
						<dsp:input type="hidden" priority="-10" value=""
							bean="MFFCSCTasksOnOrderFormHandler.forceAllocateOrder" />
						<dsp:input
							bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderSuccessUrl"
							type="hidden" value="${successErrorURL}" />
						<dsp:input
							bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderErrorUrl"
							type="hidden" value="${successErrorURL}" />

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
								Full list of stores sorted by code id
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
								Select Cancellation Reason : 
								<dsp:select bean="MFFCSCTasksOnOrderFormHandler.cancelOrderReasonCode" id="cancelReasonCode" name="cancelReasonCode">
									 <dsp:droplet name="ForEach">
						        		<dsp:param name="array" bean="CSRAgentTools.cancelReasonCodes"/>
						        		 <dsp:param name="elementName" value="reasonCodes" />
						        			<dsp:oparam name="output">
						        				<dsp:getvalueof var="key" param="key" />
						        				<dsp:getvalueof var="reasonCode" param="reasonCodes" />
						        				<dsp:option value="${key}"><c:out value="${reasonCode}" /></dsp:option>
						        			</dsp:oparam>
						        	 </dsp:droplet>
					        	 </dsp:select>
				        	 </div>
						</div> --%>
						<div>
							<BR><p style="padding-left: 4em;">  <input
								type="button" name="forceAllocationButton"
								id="forceAllocationButton" class="atg_commerce_csr_activeButton"
								onclick="submitForceAllocation();" value="Allocate to Store" />
								<span style="display:inline-block; margin-left: 18em; ">
								<input type="button" name="cancelItemsButton"
								id="cancelItemsButton" class="atg_commerce_csr_activeButton"
								onclick="submitCancelItems();" value="Cancel Items" />
								</span></p>
							<BR>
						</div>
					<%-- </dsp:form> --%>
				</dsp:oparam>
				<dsp:oparam name="false">
					<span style="width: 80%">
						<div class="emptyLabel">There are no available items in the
							order for manual allocation or the order is not in modifiable
							state</div>
					</span>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:layeredBundle>
	</dsp:page>

</c:catch>
<c:if test="${exception != null}">
	<c:out value="${exception}" />
</c:if>
