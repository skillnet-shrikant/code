<%--
  - File Name: promoSuccess.jsp
  - Author(s): jjensen
  - Copyright Notice:
  - Description: Creates a json success message after successfully adding a promo to the order.
  - Parameters:
  --%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

	<json:object>
		<json:property name="success">true</json:property>
		<json:property name="promoCode"><dsp:valueof bean="CartModifierFormHandler.couponCode"/></json:property>
		<json:property name="promoDetails"><dsp:valueof bean="CartModifierFormHandler.appliedCouponDescription"/></json:property>
	</json:object>

</dsp:page>
