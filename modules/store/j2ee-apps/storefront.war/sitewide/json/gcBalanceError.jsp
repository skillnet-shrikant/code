<%--
- File Name: gcBalanceError.jsp
- Author(s): jjensen
- Copyright Notice:
- Description: Creates a json error message for giftcard balance inquiry
- Parameters:
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
	<dsp:importbean bean="/com/mff/commerce/order/purchase/GiftCardBalanceFormHandler"/>
	<dsp:param name="formhandler" bean="GiftCardBalanceFormHandler" />
	<json:object>
	<json:property name="success">false</json:property>
		<%@ include file="/sitewide/includes/errors/jsonErrors.jspf" %>
	</json:object>
</dsp:page>
