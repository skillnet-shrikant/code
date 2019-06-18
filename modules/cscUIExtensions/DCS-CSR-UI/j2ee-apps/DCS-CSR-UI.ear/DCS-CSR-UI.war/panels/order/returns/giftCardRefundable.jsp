<%--
This file gets the order from the orderId parameter and displays
gift cards that need to be fulfilled.
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
	<dsp:page xml="true">

		<dsp:importbean
			bean="/com/mff/commerce/order/GiftcardPendingFulfillment" />

		<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

			<dsp:getvalueof var="order"
				bean="/atg/commerce/custsvc/order/ViewOrderHolder.current"/>
			<dsp:getvalueof var="isExistingOrderView" value="${true}" />

			<script type="text/javascript">
				if (!dijit.byId("printLabelPopupFloatingPane")) {
					new dojox.Dialog({
						id : "printLabelPopupFloatingPane",
						cacheContent : "false",
						executeScripts : "true",
						scriptHasHooks : "true",
						duration : 100,
						"class" : "atg_commerce_csr_popup"
					});
				}
				if (!dijit.byId("approvalRejectPopup")) {
					new dojox.Dialog( {
						id :"approvalRejectPopup",
						cacheContent :"false",
						executeScripts :"true",
						scriptHasHooks :"true"
					});
				}
				function submitReadyForActivation() {
					popupUrl='${CSRConfigurator.contextRoot}' + '/panels/approvals/readyForActivationConfirm.jsp?_windowid=${windowId}';
					dojo.byId('activationCheckbox').checked = true;
					dojo.byId('activationCheckbox').value = true;
				    atg.commerce.csr.common.showPopupWithReturn({
					      popupPaneId: 'approvalRejectPopup',
					      title: "Confirm gift card(s) Ready for Activation",url: popupUrl,
					      onClose: function(args) {
					        if (args.result == 'confirm') {          
								atgSubmitAction({
									form : dojo.byId("gcItemsForm1"),
							        panels: ["mffGiftCardFulfillmentP"],
							        panelStack : ['cmcExistingOrderPS','globalPanels']
								});
					        }
					    }});
					    return false;
					  }
				function updateGCNumber() {
					dojo.byId('activationCheckbox').checked = false;
					dojo.byId('activationCheckbox').value = false;
					atgSubmitAction({
						form : dojo.byId("gcItemsForm1")
					});
				}
			</script>

			<dsp:droplet name="GiftcardPendingFulfillment">
				<dsp:param name="orderid" value="${order.id}" />
				<dsp:param name="itemType" value="false"/>
				<dsp:oparam name="true">
					<c:if test="${empty isExistingOrderView}">
						<c:set var="isExistingOrderView" value="false" />
					</c:if>
					<csr:renderer
						name="/atg/commerce/custsvc/ui/renderers/MFFPayGroupDescription">
						<jsp:attribute name="setPageData"></jsp:attribute>
						<jsp:body>
		        			<dsp:include src="${renderInfo.url}"
										otherContext="${renderInfo.contextRoot}">
		          				<dsp:param name="currentOrder" value="${order}" />
		          				<dsp:param name="isExistingOrderView" value="${isExistingOrderView}" />
		          				<dsp:param name="ciPendingFulfillment" param="elements" />
		        			</dsp:include>
      					</jsp:body>
					</csr:renderer>
				</dsp:oparam>
				<dsp:oparam name="false">
					<span style="width: 80%">
						<div class="emptyLabel">There are no available refundable gift cards in the
							order that need to be fulfilled</div>
					</span>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:layeredBundle>
	</dsp:page>

</c:catch>
<c:if test="${exception != null}">
	<c:out value="${exception}" />
</c:if>
