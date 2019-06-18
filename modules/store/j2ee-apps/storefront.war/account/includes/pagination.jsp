<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ProtocolChange"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="currentPage" param="currentPage"/>
	<dsp:getvalueof var="totalPages" param="totalPages"/>
	<dsp:getvalueof var="baseUrl" param="baseUrl"/>

	<div class="pagination">

		<%-- previous link arrow --%>
		<c:choose>
			<c:when test="${currentPage == 1}">
				<a href="#" class="pagination-prev disabled">
					<span class="icon icon-arrow-left" aria-hidden="true"></span>
				</a>
			</c:when>
			<c:otherwise>
				<dsp:droplet name="ProtocolChange">
					<dsp:param name="inUrl" value="${baseUrl}?pageNumber=${currentPage - 1}"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="prevUrl" scope="request" param="secureUrl"/>
						<a href="${prevUrl}" class="pagination-prev">
							<span class="icon icon-arrow-left" aria-hidden="true"></span>
						</a>
					</dsp:oparam>
				</dsp:droplet>
			</c:otherwise>
		</c:choose>

		<%-- set beginning and end of page links foreach loop --%>
		<c:choose>
			<c:when test="${totalPages le 5}">
				<c:set var="begin" value="1" scope="request" />
				<c:set var="end" value="${totalPages}" scope="request" />
			</c:when>
			<c:when test="${currentPage le 3}">
				<c:set var="begin" value="1" scope="request" />
				<c:set var="end" value="5" scope="request" />
			</c:when>
			<c:when test="${(currentPage eq totalPages) || (currentPage eq (totalPages - 1))}">
				<c:set var="begin" value="${totalPages - 4}" scope="request" />
				<c:set var="end" value="${totalPages}" scope="request" />
			</c:when>
			<c:otherwise>
				<c:set var="begin" value="${currentPage - 2}" scope="request" />
				<c:set var="end" value="${currentPage + 2}" scope="request" />
			</c:otherwise>
		</c:choose>

		<%-- page links --%>
		<c:forEach begin="${begin}" end="${end}" var="index">
			<c:choose>
				<c:when test="${currentPage == index}">
					<a class="page-num active" href="#">${index}</a>
				</c:when>
				<c:otherwise>
					<dsp:droplet name="ProtocolChange">
						<dsp:param name="inUrl" value="${baseUrl}?pageNumber=${index}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="pageUrl" scope="request" param="secureUrl"/>
							<a href="${pageUrl}" class="page-num">${index}</a>
						</dsp:oparam>
					</dsp:droplet>
				</c:otherwise>
			</c:choose>
		</c:forEach>

		<%-- next link arrow --%>
		<c:choose>
			<c:when test="${currentPage == totalPages}">
				<a href="#" class="pagination-next disabled">
					<span class="icon icon-arrow-right" aria-hidden="true"></span>
				</a>
			</c:when>
			<c:otherwise>
				<dsp:droplet name="ProtocolChange">
					<dsp:param name="inUrl" value="${baseUrl}?pageNumber=${currentPage + 1}"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="nextUrl" scope="request" param="secureUrl"/>
						<a href="${nextUrl}" class="pagination-next">
							<span class="icon icon-arrow-right" aria-hidden="true"></span>
						</a>
					</dsp:oparam>
				</dsp:droplet>
			</c:otherwise>
		</c:choose>

	</div>

</dsp:page>
