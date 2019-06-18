<%--
  - File Name: productData.jsp
  - Author(s): DMI UX
  - Copyright Notice:
  - Description: Product data for a single product
  - Parameters:
  -   product (required) the product object
  --%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/mff/browse/droplet/ProductPickerDetailsDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

	<%-- Page Parameters --%>
	<dsp:getvalueof var="product" param="product" />
	<dsp:getvalueof var="prevSku" param="prevSku"/>

	<%-- productData JSON --%>
	<json:object>
		<%@ include file="/browse/fragments/productData.jspf" %>
	</json:object>

</dsp:page>
