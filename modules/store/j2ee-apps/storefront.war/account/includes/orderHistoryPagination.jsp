<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ProtocolChange"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="items" param="items" />
	<dsp:getvalueof var="itemsPerPage" param="itemsPerPage" />
	<dsp:getvalueof var="currentPage" param="currentPage" />
	<dsp:getvalueof var="numPages" value="${items / itemsPerPage}" />
	<dsp:getvalueof var="totalPages" value="${numPages + (1 - (numPages % 1)) % 1}" />

	<%-- pagination --%>
	<c:forEach var="i" begin="1" end="${totalPages}">
		<c:choose>
			<c:when test="${i != currentPage}">
			   <dsp:a href="${contextPath}/account/orderHistory.jsp">
					 <c:out value="${i}"/>
					<dsp:param name="pageNum" value="${i}"/>
			   </dsp:a>
			</c:when>
			<c:otherwise>
				<c:out value="${i}"/>
			</c:otherwise>
		</c:choose>
		&nbsp;&nbsp;
	</c:forEach>

</dsp:page>
