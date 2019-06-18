<dsp:page>
	<dsp:importbean bean="/com/mff/droplet/NavigationUrlGenerator"/>
	<dsp:droplet name="/com/mff/droplet/EndecaResultPagination">
		<dsp:param name="lastRecNum" param="lastRecNum"/>
		<dsp:param name="firstRecNum" param="firstRecNum"/>
		<dsp:param name="recsPerPage" param="recsPerPage"/>
		<dsp:param name="totalNumRecs" param="totalNumRecs"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="recsPerPage" param="recsPerPage"/>
			<dsp:getvalueof var="currentPage" param="currentPage"/>
			<dsp:getvalueof var="totalPages" param="totalPages"/>
			<dsp:getvalueof var="beginIndex" param="beginIndex"/>
			<dsp:getvalueof var="endIndex" param="endIndex"/>
			<dsp:getvalueof var="requestUri" value="${requestScope['javax.servlet.forward.request_uri']}"/>
			<div class="pagination">
				<c:choose>
					<c:when test = "${currentPage == 1}">
						<a class="pagination-prev disabled">
							<span class="icon icon-arrow-left" aria-hidden="true"></span>
						</a>
					</c:when>
					<c:otherwise>
						<dsp:droplet name="NavigationUrlGenerator">
							<dsp:param name="paramName" value="No"/>
							<dsp:param name="paramValue" value="${(currentPage-2)*recsPerPage}"/>
							<dsp:param name="requestUri" value="${requestUri}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="url" param="url"/>
								<a class="pagination-prev" href="${url}">
									<span class="icon icon-arrow-left" aria-hidden="true"></span>
								</a>
							</dsp:oparam>
						</dsp:droplet>
						
					</c:otherwise>
				</c:choose>
				<c:forEach begin="${beginIndex}" end="${endIndex}" varStatus="status">
					<c:choose>
						<c:when test = "${status.index == currentPage}">
							<a class="page-num active">${status.index}</a>
						</c:when>
						<c:otherwise>
							<dsp:droplet name="NavigationUrlGenerator">
								<dsp:param name="paramName" value="No"/>
								<dsp:param name="paramValue" value="${(status.index-1)*recsPerPage}"/>
								<dsp:param name="requestUri" value="${requestUri}"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="url" param="url"/>
									<a class="page-num" href="${url}">${status.index}</a>
								</dsp:oparam>
							</dsp:droplet>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<c:choose>
					<c:when test = "${currentPage == totalPages}">
						<a class="pagination-next disabled">
							<span class="icon icon-arrow-right" aria-hidden="true"></span>
						</a>
					</c:when>
					<c:otherwise>
						<dsp:droplet name="NavigationUrlGenerator">
							<dsp:param name="paramName" value="No"/>
							<dsp:param name="paramValue" value="${(currentPage)*recsPerPage}"/>
							<dsp:param name="requestUri" value="${requestUri}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="url" param="url"/>
								<a class="pagination-next" href="${url}">
									<span class="icon icon-arrow-right" aria-hidden="true"></span>
								</a>
							</dsp:oparam>
						</dsp:droplet>
					</c:otherwise>
				</c:choose>
			</div>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>