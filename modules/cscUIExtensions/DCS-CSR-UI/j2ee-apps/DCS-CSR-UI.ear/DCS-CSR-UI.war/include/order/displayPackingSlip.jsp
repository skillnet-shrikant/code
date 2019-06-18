<%--
Display the Packing Slip for gift cards purchased in the order

Expected params
order : The order
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:importbean bean="/com/mff/commerce/order/GiftcardPackingSlip"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<link rel="stylesheet" type="text/css" href="../../css/packingSlip.css">
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
	<dsp:droplet name="GiftcardPackingSlip">
		<dsp:param name="orderid" param="currentOrder"/>
		<dsp:param name="itemType" value="true"/>
		<dsp:oparam name="true">
			<dsp:getvalueof var="commerceItems" param="elements"/>
			<dsp:getvalueof var="order" param="orderout"/>
			<dsp:getvalueof var="currencyCode" param="orderout.priceInfo.currencyCode"/>
			<dsp:getvalueof var="shippingGroup" param="orderout.shippingGroups[0]"/>
			<dsp:getvalueof var="payGroup" param="orderout.paymentGroups[0]"/>
			<div class="row header-row">
				<div class="column-8">
					<img src="/DCS-CSR/images/logo.png" alt="Fleet Farm" />
				</div>
				<div class="column-4">
					<dl>
						<dt>Web:</dt>
						<dd>www.fleetfarm.com</dd>
						<dt>Phone:</dt>
						<dd>1-877-633-7456</dd>
						<dt>Email:</dt>
						<dd>CS@fleetfarm.com</dd>
					</dl>
				</div>
			</div>
			<div class="row">
				<div class="column-4">
					<c:choose>
						<c:when test="${payGroup.paymentGroupClassType == 'giftCard'}">
							<%-- dont display anything --%>
						</c:when>
						<c:otherwise>
							<h4>Sold To:</h4>
							<ul>
								<li>${fn:escapeXml(payGroup.billingAddress.firstName)} ${fn:escapeXml(payGroup.billingAddress.lastName)}</li>
								<li>${fn:escapeXml(payGroup.billingAddress.address1)}</li>
								<dsp:droplet name="IsEmpty">
								<dsp:param name="value" value="${payGroup.billingAddress.address2}"/>
									<dsp:oparam name="false">
										<li>${fn:escapeXml(payGroup.billingAddress.address2)}</li>
									</dsp:oparam>
								</dsp:droplet>
								<li>${fn:escapeXml(payGroup.billingAddress.city)}, ${fn:escapeXml(payGroup.billingAddress.state)} ${fn:escapeXml(payGroup.billingAddress.postalCode)}</li>
							</ul>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="column-4">
					<h4>Ship To:</h4>
					<ul>
						<li>${fn:escapeXml(shippingGroup.shippingAddress.firstName)} ${fn:escapeXml(shippingGroup.shippingAddress.lastName)}</li>
						<li>${fn:escapeXml(shippingGroup.shippingAddress.address1)}</li>
						<dsp:droplet name="IsEmpty">
						<dsp:param name="value" value="${shippingGroup.shippingAddress.address2}"/>
							<dsp:oparam name="false">
								<li>${fn:escapeXml(shippingGroup.shippingAddress.address2)}</li>
							</dsp:oparam>
						</dsp:droplet>
						<li>${fn:escapeXml(shippingGroup.shippingAddress.city)}, ${fn:escapeXml(shippingGroup.shippingAddress.state)} ${fn:escapeXml(shippingGroup.shippingAddress.postalCode)}</li>
					</ul>
				</div>
				<div class="column-4">
					<dl>
						<dt>Page:</dt>
						<dd>1 of 1</dd>
						<dt>Order #:</dt>
						<dd>${order.orderNumber}</dd>
						<dt>Order Date:</dt>
						<dd><web-ui:formatDate type="date" value="${order.submittedDate}" dateStyle="medium"/></dd>
						<dt>Customer #:</dt>
						<dd>${order.profileId}</dd>
					</dl>
				</div>
			</div>

			<div class="row">
				<table>
					<thead>
						<th>UPC</th>
						<th>SKU</th>
						<th>GIFT CARD NUMBER</th>
						<th>DESCRIPTION</th>
						<th class="center">QTY</th>
						<th class="right">PRICE</th>
						<th class="right">DISCOUNT</th>
						<th class="right">NET PRICE</th>
					</thead>
					<tbody>
						<c:forEach items="${commerceItems}" var="commerceItem" varStatus="commerceItemIndex">
						<tr>${fn:escapeXml(catalogRef.displayName)}
							<td>${fn:escapeXml(commerceItem.auxiliaryData.catalogRef.upc1)}</td>
							<td>${fn:escapeXml(commerceItem.auxiliaryData.catalogRef.id)}</td>
							<td>${fn:escapeXml(commerceItem.giftCardNumber)}</td>
							<td>${fn:escapeXml(commerceItem.auxiliaryData.productRef.description)}</td>
							<td class="center"><web-ui:formatNumber value="${commerceItem.quantity}"/></td>
							<td class="right"><csr:formatNumber value="${commerceItem.priceInfo.listPrice}" type="currency" currencyCode="${currencyCode}"/></td>
							<td class="right"><csr:formatNumber value="${commerceItem.priceInfo.listPrice-commerceItem.priceInfo.salePrice}" type="currency" currencyCode="${currencyCode}"/></td>
							<td class="right"><csr:formatNumber value="${commerceItem.priceInfo.amount}" type="currency" currencyCode="${currencyCode}"/></td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="row fixed-bottom">
				PLEASE MAIL ALL RETURNS TO: FLEET FARM ATTN: GIFT CARD COORDINATOR, 1300 SOUTH LYNNDALE DRIVE, PO BOX 1199, APPLETON, WI 54912-1199
			</div>
		</dsp:oparam>
		<dsp:oparam name="false">
			<span style="width: 80%">
				<div class="emptyLabel">There are no available gift cards in the order that need to be fulfilled</div>
			</span>
		</dsp:oparam>
	</dsp:droplet>
  </dsp:layeredBundle>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/displayCommerceItem.jsp#2 $$Change: 1179550 $--%>
