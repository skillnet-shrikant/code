<%--
  - File Name: additionalShippingModal.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This modal tells the user additional shipping charges apply to their order
  --%>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/pricing/calculators/ShippingUpChargeCalculator" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="signatureRequiredFee" bean="ShippingUpChargeCalculator.upCharges.SignatureRequired" />
	<dsp:getvalueof var="longLightFee" bean="ShippingUpChargeCalculator.upCharges.AdditionalHandling" />
	<dsp:getvalueof var="isOversizeFee" bean="ShippingUpChargeCalculator.upCharges.Oversized" />
	<dsp:getvalueof var="signatureRequired" param="signatureRequired" />
	<dsp:getvalueof var="longLight" param="longLight" />
	<dsp:getvalueof var="isOversize" param="isOversize" />
	<dsp:getvalueof var="isLTLOrder" param="isLTLOrder" />
	<dsp:getvalueof var="totalLTLWeight" param="totalLTLWeight" />
	<dsp:getvalueof var="rangeLow" param="rangeLow" />
	<dsp:getvalueof var="rangeHigh" param="rangeHigh" />
	<dsp:getvalueof var="ltlShippingCharges" param="ltlShippingCharges" />
	<dsp:getvalueof var="hasSurcharge" param="hasSurcharge" />
	<dsp:getvalueof var="totalSurcharge" param="totalSurcharge" />

	<layout:ajax>
		<jsp:attribute name="section">checkout</jsp:attribute>
		<jsp:attribute name="pageType">additionalShippingModal</jsp:attribute>
		<jsp:body>

			<div class="additional-shipping-modal">

				<div class="modal-header">
					<h2>
						<c:choose>
							<c:when test="${(not empty isLTLOrder) and (isLTLOrder ne 'false')}">
								LTL Shipping Charges
							</c:when>
							<c:when test="${(not empty signatureRequired) and (signatureRequired gt 0)}">
								SIGNATURE REQUIRED UPON DELIVERY
							</c:when>
							<c:otherwise>
								Additional Shipping Charges
							</c:otherwise>
						</c:choose>
					</h2>
				</div>

				<div class="modal-body">

					<c:if test="${(not empty signatureRequired) and (signatureRequired gt 0)}">
						<p>
							Your order consists of an item(s) that requires signature upon delivery. 
							Your order will be charged an additional shipping fee of 
							<strong><fmt:formatNumber value="${signatureRequired * signatureRequiredFee}" type="currency" /></strong>.
						</p>
					</c:if>

					<c:if test="${(not empty longLight) and (longLight gt 0)}">
						<p>
							Your order consists of an item(s) that requires Additional Handling. 
							Your order will be charged an additional shipping fee of
							<strong><fmt:formatNumber value="${longLight * longLightFee}" type="currency" /></strong>.
						</p>
					</c:if>

					<c:if test="${(not empty isOversize) and (isOversize gt 0)}">
						<p>
							Your order consists of an item(s) that requires Oversized Handling. 
							Your order will be charged an oversized shipping fee of 
							<strong><fmt:formatNumber value="${isOversize * isOversizeFee}" type="currency" /></strong>.
						</p>
					</c:if>

					<c:if test="${(not empty isLTLOrder) and (isLTLOrder ne 'false')}">
						<p>
							Your order consists of an item(s) that requires LTL Shipping. Your total order weight 
							for LTL item(s) is ${totalLTLWeight} lbs. All orders for LTL
							item(s) between ${rangeLow} to ${rangeHigh} lbs. will be charged a shipping fee of
							<strong><fmt:formatNumber value="${ltlShippingCharges}" type="currency" /></strong>.
						</p>
					</c:if>
					
					<c:if test="${(not empty hasSurcharge) and (hasSurcharge ne 'false')}">
						<p>
							Your cart contains an item(s) with a quantity shipping surcharge. 
							Your order will be charged an additional shipping fee of <strong><fmt:formatNumber value="${totalSurcharge}" type="currency" /></strong>.
						</p>
					</c:if>

				</div>

			</div>

		</jsp:body>
	</layout:ajax>

</dsp:page>
