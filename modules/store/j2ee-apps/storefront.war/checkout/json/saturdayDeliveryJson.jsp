<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler" />
	<dsp:importbean bean="/com/mff/commerce/order/purchase/SaturdayDeliveryDroplet" />

	<json:object>
		<dsp:droplet name="SaturdayDeliveryDroplet">
			<dsp:param name="shippingGroup" bean="ShippingGroupFormHandler.shippingGroup" />
			<dsp:param name="shippingMethod" param="shippingMethod" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="isSatDayDelivery" param="isSatDayDelivery"/>
				<c:choose>
					<c:when test="${isSatDayDelivery}">
						<json:property name="isSatDayDelivery">true</json:property>
					</c:when>
					<c:otherwise>
						<json:property name="isSatDayDelivery">false</json:property>
					</c:otherwise>
				</c:choose>
			</dsp:oparam>
		</dsp:droplet>
	</json:object>

</dsp:page>
