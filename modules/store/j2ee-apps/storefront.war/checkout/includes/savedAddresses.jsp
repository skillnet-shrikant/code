<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CheckoutManager" />

	<%-- Page Variables --%>
	<c:if test="${empty defaultAddressId}">
		<dsp:getvalueof var="defaultAddressId" bean="Profile.shippingAddress.repositoryId" />
	</c:if>

	<dsp:droplet name="MapToArrayDefaultFirst">
		<dsp:param name="map" bean="Profile.secondaryAddresses" />
		<dsp:param name="defaultId" value="${defaultAddressId}" />
		<dsp:oparam name="output">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="sortedArray"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="count" param="count"/>
					<dsp:getvalueof var="size" param="size"/>
					<dsp:getvalueof var="addressId" param="element.value.id"/>
					<dsp:getvalueof var="nickName" param="element.key"/>
					<c:set var="checked" value="false" />
					<c:if test="${defaultAddressId == addressId or (shipAddressId!='0' and count==1)}">
						<c:set var="checked" value="true" />
					</c:if>
					<div class="radio">
						<label for="shipping-address-${count}">
							<dsp:input type="radio" id="shipping-address-${count}" name="shipping-address" bean="ShippingGroupFormHandler.addressId" value="${addressId}" checked="${checked}" />
							<div class="card">
								<div class="card-title">${nickName}</div>
								<div class="card-content">
									<p><dsp:valueof param="element.value.firstName"/>&nbsp;<dsp:valueof param="element.value.lastName"/></p>
									<dsp:getvalueof var="address2" param="element.value.address2"/>
									<dsp:getvalueof var="attention" param="element.value.attention"/>
									<c:if test="${!empty attention}"><p><span class="label">Attention:</span>${attention}</p></c:if>
									<p><dsp:valueof param="element.value.address1"/></p>
									<c:if test="${!empty address2}"><p><dsp:valueof param="element.value.address2"/></p></c:if>
									<p><dsp:valueof param="element.value.city"/>,&nbsp;<dsp:valueof param="element.value.state"/>&nbsp;<dsp:valueof param="element.value.postalCode"/></p>
									<p>
										<dsp:getvalueof var="phone" param="element.value.phoneNumber" />
										<c:out value="${fn:substring(phone, 0, 3)}-${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, 10)}" />
									</p>
								</div>
							</div>
						</label>
					</div>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

	<div class="radio shipping-address-new">
		<label for="shipping-address-new">
			<dsp:input type="radio" id="shipping-address-new" name="shipping-address" bean="ShippingGroupFormHandler.addressId" value="0"/>
			<span class="label">New Shipping Address</span>

			<div class="new-shipping-address">
				<%@ include file="/checkout/includes/addressForm.jsp"%>
			</div>

		</label>
	</div>

</dsp:page>
