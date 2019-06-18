<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
 
<dsp:page xml="true">
	
   <dsp:importbean bean="/com/mff/commerce/order/RefundGCOrderLookupDroplet"/>
   <body>
    <br />
    <dsp:droplet name="RefundGCOrderLookupDroplet">
		<dsp:oparam name="output">
			
			<dsp:getvalueof var="result" param="result"/>
			
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" value="${result}"/>
				<dsp:oparam name="outputStart">
					<table border-collapse: separate; border-spacing: 10px; cellpadding="1">
					<tr>
						<td><b>Refund GC Orders</b></td>			
					</tr>
					<tr bgcolor="#989898">
						 <td><b>Order Number</b></td>		
				   	</tr>
				</dsp:oparam>
				<dsp:oparam name="output">
				    <dsp:getvalueof param="element" var="refundGCResponse"/>
				 	<tr bgcolor="silver">
				 		<td><a href="#" onclick="atg.commerce.csr.order.loadExistingOrder('<c:out value="${refundGCResponse.orderId}" />','<c:out value="${refundGCResponse.orderNumber}" />');return false;">${refundGCResponse.orderNumber}</a></td>		 			 			
				   	</tr>
				</dsp:oparam>
				<dsp:oparam name="outputEnd">
					</table>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="empty">
			No Orders for Refund.
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>