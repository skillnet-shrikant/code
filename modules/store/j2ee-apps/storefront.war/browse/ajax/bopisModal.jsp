<%--
  - File Name: bopisModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal allows the user to select a store they'll pick up their order at
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>

	<layout:ajax>
		<jsp:attribute name="pageType">bopisModal</jsp:attribute>
		<jsp:body>
			<div class="bopis-modal">
				<div class="modal-header">
					<h2>Find Store Availability</h2>
				</div>

				<div class="modal-body">
					<div class="bopis-search-form">
						<dsp:form formid="bopis-search-form" id="bopis-search-form" method="post" name="bopis-search-form" data-validate>
							<dsp:include page="/browse/includes/bopisSearchForm.jsp" />
						</dsp:form>
					</div>
					<div class="bopis-results"></div>
					<div class="item-inventory-details">
						<p class="reserve-msg hide">
							To reserve this item, place a Store Pickup order by selecting
							"Choose This Store" from an available store location listed above.
						</p>
						<p>
							We do our best to update item availability as inventory changes: however,
							there may be slight difference in availability compared to what is listed online.
						</p>
					</div>
				</div>
			</div>
		</jsp:body>
	</layout:ajax>
</dsp:page>
