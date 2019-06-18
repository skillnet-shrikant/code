<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
 
<dsp:page xml="true">
	
   <dsp:importbean bean="/com/mff/commerce/order/ForcedAllocationCountDroplet"/>
   <body>
    <br />
    <table border-collapse: separate; border-spacing: 10px; cellpadding="1">
		<tr>
			<td><b>Total Number of Orders in Force Allocation :
				<dsp:droplet name="ForcedAllocationCountDroplet">
					<dsp:oparam name="output">
						<dsp:valueof param="count"/></b>
					</dsp:oparam>
				</dsp:droplet> 
				
			</td>			
		</tr>
	</table>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>