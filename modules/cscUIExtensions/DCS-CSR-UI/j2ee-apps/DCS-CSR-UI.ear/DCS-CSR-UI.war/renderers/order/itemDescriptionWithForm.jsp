<%@ include file="/include/top.jspf"%>

<c:catch var="exception">
	<dsp:page xml="true">
		<dsp:importbean
			bean="/com/mff/commerce/order/MFFCSCTasksOnOrderFormHandler" />
		<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
			<dsp:getvalueof var="order" param="currentOrder" />
			<dsp:getvalueof var="isExistingOrderView" param="isExistingOrderView" />
			<dsp:getvalueof var="ciPendingFulfillment"
				param="ciPendingFulfillment" />
			<c:set var="gcform1" value="gcItemsForm1" />
			<svc-ui:frameworkUrl var="successErrorURL"
				panelStacks="globalPanels,cmcExistingOrderPS" tab="commerceTab" contentHeader="true" />
			<dsp:setvalue bean="MFFCSCTasksOnOrderFormHandler.inputSizeGC"
				value="${fn:length(ciPendingFulfillment)}" />
			<dsp:form method="post" name="${gcform1}" id="${gcform1}"
				formid="${gcform1}">
				<dsp:input type="hidden" id="activationCheckbox"
					name="activationCheckbox"
					bean="MFFCSCTasksOnOrderFormHandler.activationCheckbox" />
				<dsp:input type="hidden" priority="-10" value=""
					bean="MFFCSCTasksOnOrderFormHandler.fulfillGiftCard" />
				<dsp:input type="hidden" priority="10"
					value="${fn:length(ciPendingFulfillment)}"
					bean="MFFCSCTasksOnOrderFormHandler.inputSizeGC" />
				<dsp:input bean="MFFCSCTasksOnOrderFormHandler.orderId"
					type="hidden" value="${order.id}" />
				<dsp:input
					bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderSuccessUrl"
					type="hidden" value="${successErrorURL}" />
				<dsp:input bean="MFFCSCTasksOnOrderFormHandler.tasksOnOrderErrorUrl"
					type="hidden" value="${successErrorURL}" />
				<table class="atg_dataTable atg_commerce_csr_innerTable"
					cellspacing="0" cellpadding="0"
					summary="<fmt:message key='shoppingCartSummary.itemDetails' />">
					<thead>
						<tr>
							<c:if test="${isMultiSiteEnabled == true}">
								<th></th>
							</c:if>
							<th><fmt:message
									key='shoppingCartSummary.commerceItem.header.itemDesc' /></th>
							<th><fmt:message
									key='shoppingCartSummary.commerceItem.header.status' /></th>
							<th class="atg_numberValue"><fmt:message
									key='shoppingCartSummary.commerceItem.header.qty' /></th>
							<th class="atg_numberValue"><fmt:message
									key='shoppingCartSummary.commerceItem.header.priceEach' /></th>
							<th class="atg_numberValue"><fmt:message
									key='shoppingCartSummary.commerceItem.header.totalPrice' /></th>
							<th class="atg_numberValue">GiftCard Number</th>
							<th class="atg_numberValue">Check</th>
							<th class="atg_numberValue">Print</th>
						</tr>
					</thead>
					<tbody>
						<%-- Iterate items --%>
						<c:forEach items="${ciPendingFulfillment}" var="commerceItem"
							varStatus="commerceItemIndex">

								<dsp:include
									src="/panels/order/finish/finishOrderCartLineItemWithForm.jsp"
									otherContext="${CSRConfigurator.contextRoot}" flush="true">
									<dsp:param name="order" value="${order}" />
									<dsp:param name="commerceItem" value="${commerceItem}" />
									<dsp:param name="commerceItemIndex"
										value="${commerceItemIndex.index}" />
									<dsp:param name="itemIndex" value="${commerceItemIndex}" />
									<dsp:param name="currencyCode"
										value="${order.priceInfo.currencyCode}" />
								</dsp:include>

						</c:forEach>
					</tbody>
				</table>
				<input type="button" name="updateGCNumberButton"
					id="updateGCNumberButton" class="atg_commerce_csr_activeButton"
					onclick="updateGCNumber();" value="Update GC Number" />
				<input type="button" name="printPackingSlipButton"
					id="printPackingSlipButton" class="atg_commerce_csr_activeButton"
					onclick="displayShipLabel('${order.id}');return false;"	value="Print Packing Slip"/>
				<input type="button" name="readyForActivationButton"
					id="readyForActivationButton" class="atg_commerce_csr_activeButton"
					onclick="submitReadyForActivation();"
					value="Set ready for Activation" />
			</dsp:form>
			<script type="text/javascript">
				function displayShipLabel(orderId) {
		  	 		popupUrl='${CSRConfigurator.contextRoot}' + '/include/order/displayPackingSlip.jsp?_windowid=${windowId}&currentOrder=' + orderId;
					var w = window.open(popupUrl, 'printShippingLabel');
					return false;
		   		}
  			</script>
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
