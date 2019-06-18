<%@ include file="/include/top.jspf"%>
<c:catch var="exception">
	<dsp:page xml="true">
		<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
			<dsp:importbean bean="/atg/commerce/custsvc/util/AltColor"/>
			<dsp:getvalueof var="skuId" param="skuId"/>
			<dsp:droplet name="/com/mff/commerce/order/MFFStoreInventoryForSku">
				<dsp:param name="skuid" value="${skuId}"/>
				<dsp:oparam name="output">
					<dsp:getvalueof param="elements" var="availability"/>
						<table class="atg_dataTable atg_commerce_csr_innerTable"
							cellspacing="0" cellpadding="0"
							summary="Stock Level Availability per Store">
							<thead>
								<tr>
									<th>Store info</th>
									<th class="atg_numberValue">Stock Level</th>
								</tr>
							</thead>
							<tbody>
								<%-- Iterate items --%>
								<c:forEach items="${availability}" var="storeAvail"
									varStatus="storeAvailIndex">
									<dsp:droplet name="AltColor">
										<dsp:param name="value" param="commerceItemIndex" />
										<dsp:oparam name="odd">
											<tr class="atg_dataTable_altRow">
										</dsp:oparam>
										<dsp:oparam name="even">
											<tr>
										</dsp:oparam>
									</dsp:droplet>
									<td>
										<dsp:droplet name="/com/mff/locator/droplet/StoreLookupDroplet">
											<dsp:param name="id" value="${storeAvail.storeNo}"/>
											<dsp:param name="elementName" value="store"/>
											<dsp:oparam name="output">${storeAvail.storeNo} <dsp:valueof param="store.city"/>
											</dsp:oparam>
										</dsp:droplet>
									</td>
									<c:choose>
										<c:when test="${storeAvail.storeStockLevel lt 0}">
											<td class="atg_numberValue">0</td>
										</c:when>
										<c:otherwise>
											<td class="atg_numberValue">${storeAvail.storeStockLevel}</td>
										</c:otherwise>
									</c:choose>
									
									<tr>
								</c:forEach>
							</tbody>
						</table>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:layeredBundle>
	</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
    Exception ee = (Exception) pageContext.getAttribute("exception");
      ee.printStackTrace();
  %>
</c:if>
