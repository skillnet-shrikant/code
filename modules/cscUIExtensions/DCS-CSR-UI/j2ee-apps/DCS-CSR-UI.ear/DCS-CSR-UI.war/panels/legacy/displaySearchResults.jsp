<%@ include file="/include/top.jspf" %>
<c:catch var="exception">

<dsp:page xml="true">

<dsp:importbean bean="/atg/commerce/catalog/SKULookup" />
<dsp:importbean bean="/atg/dynamo/servlet/RequestLocale" />
<dsp:getvalueof var="searchResults" param="searchResults" />
<dsp:getvalueof var="fromSearch" param="fromSearch" />

<dsp:droplet name="/atg/dynamo/droplet/ForEach">
	<dsp:param name="array" value="${searchResults}"/>
	<dsp:param name="sortProperties" value="-submittedDate"/>
	<dsp:oparam name="outputStart">
		<table border-collapse: separate; border-spacing: 10px; cellpadding="1">
		<tr>
			<td><b>Legacy Orders</b></td>			
		</tr>
		<tr bgcolor="#989898">
			 <td><b>Submitted Date</b></td>
			 <td><b>Order Number</b></td>
			 <td><b>State</b></td>
			 <td><b>ProfileId</b></td>
			 <td><b>Description</b></td>
			 <td><b>FirstName</b></td>
			 <td><b>LastName</b></td>
			 <td><b>Email</b></td>
			 <td><b>Phone</b></td>			
	   	</tr>
	</dsp:oparam>
	<dsp:oparam name="output">
	    <dsp:getvalueof param="element" var="storeAllocationItem"/>
	 	<tr bgcolor="silver">
	 		<td>${storeAllocationItem.submittedDate}</td>
	 		<%-- <td><a href="#" onclick="atg.commerce.csr.order.viewExistingOrder('${storeAllocationItem.id}','${storeAllocationItem.state}');return false;">${storeAllocationItem.id}</a></td>--%>
	 		<td>
	 			<c:set var="hrefUrl" value="${CSRConfigurator.contextRoot}/legacy/legacyOrderDisplay.jsp?_windowId=${windowId}&orderId=${storeAllocationItem.id}" />
	 			<dsp:a href="${hrefUrl}" target="_blank">${storeAllocationItem.id}</dsp:a>
	 		</td>
	 		<td>${storeAllocationItem.state}</td>
	 		<td>${storeAllocationItem.profileId}</td>
	 		<td>${storeAllocationItem.description}</td>	
	 		<td>${storeAllocationItem.shippingGroups[0].firstName}</td>
	 		<td>${storeAllocationItem.shippingGroups[0].lastName}</td>
	 		 <td><c:if test="${storeAllocationItem.paymentGroups[0].type == 'creditCard'}">	
	 			${storeAllocationItem.paymentGroups[0].email}
	 		 </c:if></td>
	 		 <td><c:if test="${not empty storeAllocationItem.shippingGroups[0].phoneNumber}">
	 			${storeAllocationItem.shippingGroups[0].phoneNumber}
	 		 </c:if>
	 		 </td>
	 		 <script type="text/javascript">
				function displayPopup(orderId) {
		  	 		popupUrl='${CSRConfigurator.contextRoot}' + '/legacy/legacyOrderDisplay.jsp?_windowId=${windowId}&orderId=' + orderId;
					var w = window.open(popupUrl, 'popup');
					return false;
		   		}
  			</script>
	 			 			
	   	</tr>
	</dsp:oparam>
	<dsp:oparam name="outputEnd">
		</table>
	</dsp:oparam>
	<dsp:oparam name="empty">
		No Result.
	</dsp:oparam>
</dsp:droplet>

</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>