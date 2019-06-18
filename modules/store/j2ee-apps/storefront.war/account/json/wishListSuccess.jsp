<%--
  - File Name: wishListSuccess.jsp
  - Author(s):
  - Copyright Notice:
  - Description: Creates a json success message after successful add to wish list.
  - Parameters:
  --%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>

	<json:object>
		<json:property name="success">true</json:property>
		<json:property name="productId"><dsp:valueof bean="GiftlistFormHandler.productId"/></json:property>
		<json:property name="skuId"><dsp:valueof bean="GiftlistFormHandler.catalogRefIds[0]"/></json:property>
	</json:object>

</dsp:page>
