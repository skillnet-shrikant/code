<%--
This JSP is used to filter out products that are not applicable to the current cart sharing group, contain only disabled sites and are added to the cart.
The above filtered out products should not be displayed in the cross sell items list.
 
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/filterCrossSellItems.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf"%>

<dsp:page xml="true">
 <dsp:importbean  bean="/atg/commerce/custsvc/collections/filter/droplet/CrossSellItemsFilterDroplet" />
 <dsp:getvalueof var="relatedProducts" param="relatedProducts" />

<%-- filter out items in the cart.  --%>
<%-- Filter out non-current cart sharing products and products with only disabled sites. --%>

  <c:set var="filteredCrossSellItems" scope="request" />
  <c:set var="filteredCrossSellItemsCount" scope="request" value="0" />

	<dsp:droplet name="CrossSellItemsFilterDroplet">
		<dsp:param name="collection" param="relatedProducts" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="filteredCollection" param="filteredCollection" />
			<c:set var="filteredCrossSellItems" scope="request"	value="${filteredCollection}" />
			<c:set var="filteredCrossSellItemsCount" scope="request"	value="${fn:length(filteredCollection)}" />
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/filterCrossSellItems.jsp#1 $$Change: 946917 $--%>