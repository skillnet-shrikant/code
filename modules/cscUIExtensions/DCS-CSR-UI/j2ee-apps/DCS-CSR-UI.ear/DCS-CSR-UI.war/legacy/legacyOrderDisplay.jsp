<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page  xml="true">
	<dsp:importbean bean="/com/mff/commerce/csr/legacy/LegacyOrderLookupDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/OrderStatesDetailed"/>
	<dsp:importbean bean="/atg/commerce/payment/CreditCardTools"/>
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	
	<link rel="stylesheet" type="text/css" href="../css/packingSlip.css">
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

	<dsp:droplet name="LegacyOrderLookupDroplet">
		<dsp:param name="orderId" param="orderId"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="result" var="order"/>
			<dsp:getvalueof var="orderNumber" param="result.orderNumber"/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:param name="order" value="${order}"/>
	<c:set var="currencyCode" value="USD" />
	
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
					</dl>
				</div>
			</div>
			
			<div class="row">
				<div class="column-4">
					
					<h4>Billed To:</h4>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="order.paymentGroups"/>
						<dsp:param name="elementName" value="paymentGroup"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="paymentClassType" param="paymentGroup.paymentGroupClassType" />
							<c:choose>
								<c:when test="${paymentClassType == 'creditCard'}">

								<dsp:getvalueof var="creditCardType" param="paymentGroup.creditCardType"/>
								<dsp:getvalueof var="reverseCardCodeMap" bean="CreditCardTools.reverseCardCodeMap"/>

									<dsp:getvalueof var="billingName" param="paymentGroup.nameOnCard" scope="request" />
									<ul>
										<h3>Credit Card</h3>
										<li>${billingName}</li>
										<li><c:out value="${reverseCardCodeMap[creditCardType]}"/></li>
										<!--  <p>
										<dsp:getvalueof var="isLegacy" param="order.legacyOrder"/>
										<c:if test="${isLegacy eq 'false'}">
											XXXX&nbsp;XXXX&nbsp;XXXX
										</c:if> -->
										<li><dsp:valueof param="paymentGroup.creditCardNumber" groupingsize="4" converter="CreditCard" numcharsunmasked="4"/></li>
										<li><strong>Exp:</strong>&nbsp;<dsp:valueof param="paymentGroup.expirationMonth"/>/<dsp:valueof param="paymentGroup.expirationYear"/></li>
										<li><strong>Amount:</strong> <dsp:valueof param="paymentGroup.amount" converter="currency" /></li>
									</ul>
								</c:when>
								<c:when test="${paymentClassType == 'giftCard'}">
									
								</c:when>
								<c:when test="${paymentClassType == 'giftCertificate'}">
									
								</c:when>
							</c:choose>
						</dsp:oparam>
					</dsp:droplet>
				</div>
				<div class="column-4">
					
					<h4>Shipped To:</h4>
					<ul>
						<li><dsp:valueof param="order.shippingInfo.firstName"/>&nbsp;<dsp:valueof param="order.shippingInfo.lastName"/></li>
						<li><dsp:valueof param="order.shippingInfo.address1"/></li>
						<dsp:droplet name="IsEmpty">
						<dsp:param name="value" param="order.shippingInfo.address2"/>
							<dsp:oparam name="false">
								<li><dsp:valueof param="order.shippingInfo.address2"/></li>
							</dsp:oparam>
						</dsp:droplet>
						<li><dsp:valueof param="order.shippingInfo.city"/>, <dsp:valueof param="order.shippingInfo.state"/> <dsp:valueof param="order.shippingInfo.postalCode"/></li>
						<li>Shipping Method : <dsp:valueof param="order.shippingMethod"/></li>
					</ul>
				</div>
				<div class="column-4">
					<ul>
						<li>
							Date: <dsp:valueof param="order.submittedDate" converter="date" date="MM/dd/yyyy"/>
						</li>
						<li>
							Order Number: 
							<c:choose>
								<c:when test="${not empty orderNumber}">
									${orderNumber}
								</c:when>
								<c:otherwise>
									<dsp:valueof param="order.id"/>
								</c:otherwise>
							</c:choose>
						</li>
						<li>
							Email: <dsp:valueof param="order.contactEmail" />
						</li>
						<li>
							Order Total: <dsp:valueof param="order.orderTotal" converter="currency"/>
						</li>
						<li>
							Status:
						 	<dsp:droplet name="OrderStatesDetailed">
							    <dsp:param name="state" param="order.state"/>
							    <dsp:param name="elementName" value="orderStateDescription"/>
							    <dsp:oparam name="output">
							      <dsp:valueof param="orderStateDescription"/>
							    </dsp:oparam>
						  	</dsp:droplet>
						</li>
					</ul>
					
				</div>
			</div>
			
			<div class="row">
				<table>
					<thead>
						<th>SKU</th>
						<th>DESCRIPTION</th>
						<th class="center">QTY</th>
						<th class="right">PRICE</th>
						<th class="right">Tracking Number</th>
						<th class="right">NET PRICE</th>
					</thead>
					<tbody>
						<dsp:getvalueof var="commerceItems" param="order.commerceItems"/>
						<c:forEach items="${commerceItems}" var="commerceItem" varStatus="commerceItemIndex">
						<tr>${catalogRef.displayName}
							<dsp:droplet name="ProductLookup">
								<dsp:param name="id" value="${commerceItem.productId}"/>
								<dsp:param name="filterByCatalog" value="false"/>
								<dsp:param name="filterBySite" value="false"/>
								<dsp:param name="elementName" value="productItem"/>
								<dsp:oparam name="output">
									<dsp:getvalueof param="productItem.description" var="productName" />
									<dsp:getvalueof param="productItem" var="productItem" />
								</dsp:oparam>
							</dsp:droplet>
							<dsp:setvalue param="productItem" value="${productItem}" />
							<td>${commerceItem.catalogRefId}</td>
							<td>${productName}</td>
							<td class="center"><web-ui:formatNumber value="${commerceItem.quantity}"/></td>
							<td class="right"><csr:formatNumber value="${commerceItem.listPrice}"  type="currency" currencyCode="${currencyCode}" /></td>
							<td class="right">
								<dsp:getvalueof var="trackingNumber" param="order.trackingNumber"/>
								<c:choose>
									<c:when test="${empty order.trackingNumber}">
										&nbsp;
									</c:when>
									<c:otherwise>
										${trackingNumber}
									</c:otherwise>
								</c:choose>
							</td>
							<td class="right"><csr:formatNumber value="${commerceItem.amount}"  type="currency" currencyCode="${currencyCode}" /></td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="row">
				<div class="column-4">
					<dsp:getvalueof var="rawSubtotal" param="order.rawSubtotal" />
					<dsp:getvalueof var="tax" param="order.tax" />
					<dsp:getvalueof var="shipping" param="order.shipping" />
										
					<ul>
						<li>
							Merchandise Total : <dsp:valueof value="${rawSubtotal}" converter="currency"/>
						</li>
						
						<li>
							<dsp:getvalueof var="discountAmount" param="order.discountAmount" />
							<c:if test="${not empty discountAmount && discountAmount ne 0.0}">
								Discount : - <dsp:valueof param="order.discountAmount" converter="currency"/>
							</c:if>
						</li>
						
						<li>
							<dsp:getvalueof var="gcTotal" param="order.giftCardPaymentTotal" />
							<c:if test="${gcTotal gt 0}">
								Gift Cards :- <dsp:valueof param="order.giftCardPaymentTotal" converter="currency"/>
							</c:if>
						</li>
						
						<li>
							Shipping : <dsp:valueof value="${shipping}" converter="currency"/>
						</li>
						
						<li>
							Tax : <dsp:valueof value="${tax}" converter="currency"/>
						</li>
						
						<li>
							Total : <dsp:valueof param="order.orderChargeAmount" converter="currency"/>
						</li>
					</ul>
				</div>
			</div>

	</dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>