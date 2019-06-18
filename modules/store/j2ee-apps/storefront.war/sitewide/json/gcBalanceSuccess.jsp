<%--
- File Name: gcBalanceSuccess.jsp
- Author(s): jjensen
- Copyright Notice:
- Description: Creates a json message for getting the balance of a gift card.
- Parameters:
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
<dsp:importbean bean="/com/mff/commerce/order/purchase/GiftCardBalanceFormHandler"/>
	<json:object>
		<json:property name="success">true</json:property>
		<json:property name="number"><dsp:valueof bean="GiftCardBalanceFormHandler.giftCardNumber" /></json:property>
		<json:property name="balance"><dsp:valueof bean="GiftCardBalanceFormHandler.giftCardBalance" converter="currency"/></json:property>
	</json:object>
</dsp:page>
