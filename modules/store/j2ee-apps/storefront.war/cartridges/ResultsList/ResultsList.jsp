<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />
	<dsp:getvalueof var="productImageRoot" bean="/mff/MFFEnvironment.productImageRoot" />
	<dsp:getvalueof var="searchTerm" value="${searchTerm}" />
	<dsp:getvalueof var="pageType" value="${pageType}" />
	<dsp:getvalueof var="gtmEnabled" bean="/mff/MFFEnvironment.gtmEnabled"/>
	
	<%-- Page Variables --%>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>

	<c:choose>
		<c:when test="${not empty contentItem.records}">

			<%-- show refinements on small screens --%>
			<button class="button secondary show-refinements">Refine</button>

			<%-- category dropdowns --%>
			<div class="category-dropdowns">
				<dsp:include page="/browse/includes/sorting.jsp">
					<dsp:param name="totalNumRecs" value="${contentItem.totalNumRecs}"/>
				</dsp:include>
			</div>

			<%-- pagination top --%>
			<dsp:include page="/browse/includes/pagination.jsp" >
				<dsp:param name="lastRecNum" value="${contentItem.lastRecNum}"/>
				<dsp:param name="firstRecNum" value="${contentItem.firstRecNum}"/>
				<dsp:param name="recsPerPage" value="${contentItem.recsPerPage}"/>
				<dsp:param name="totalNumRecs" value="${contentItem.totalNumRecs}"/>
			</dsp:include>

			<%-- applied facets --%>
			<div class="applied-facets-nav"></div>

			<div class="category-product-grid ${(!empty leafCategory) ? 'notLeafCat' : '' }">
				<c:if test="${bvEnabled}">
					<c:choose>
						<c:when test="${not empty bvRecords}">
							<dsp:droplet name="/com/mff/droplet/MergeLists">
								<dsp:param name="list1" value="${bvRecords}"/>
								<dsp:param name="list2" value="${contentItem.records}"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="bvRecords" param="finalList" scope="request"/>
								</dsp:oparam>
							</dsp:droplet>
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="bvRecords" value="${contentItem.records}" scope="request"/>
						</c:otherwise>
					</c:choose>
					<c:if test="${isAjax}">
					<dsp:include otherContext="/bv" page="/productListing/common/bv_plp_script.jsp">
						<dsp:param name="records" value="${contentItem.records}" />
					</dsp:include>
					<c:set var="contextPath" value="${currentContext}" />
					</c:if>
				</c:if>
				<%-- At the time when img error event fires, KP JS namespace is not yet defined, and no error listeners are bound yet, so use inline JS for initial handling of image errors --%>
				<script>
				function productImagesPageOnImgError(img, imgType){
			 		img.onerror = null; // reset onerror
				 	var lPath  = '${productImageRoot}/unavailable/l.jpg';
					var xlPath = '${productImageRoot}/unavailable/xl.jpg';
					var thPath = '${productImageRoot}/unavailable/th.jpg';
					if (imgType === 'main') {
						img.parentNode.getElementsByTagName('source')[0].srcset = lPath;
						img.parentNode.getElementsByTagName('source')[1].srcset = xlPath;
						img.src = lPath;
					} else if (imgType === 'thumb') {
						img.parentNode.getElementsByTagName('source')[0].srcset = thPath; // update source's srcset
						img.src = thPath;
					}
				 }
				<c:if test="${gtmEnabled}">
					if (!digitalData.products) {
						digitalData.products = [];
					}
					<c:set var="list" value="Product Category Page"/>
					<c:if test="${not empty searchTerm}">
						<c:set var="list" value="Keyword Search: ${searchTerm}"/>
					</c:if>
				</c:if>
				</script>
				<ul class="product-grid">
					<c:forEach var="record" items="${contentItem.records}" varStatus="loopStatus">
						<li>
							<dsp:include page="/browse/includes/productTile.jsp">
								<dsp:param name="productId" value="${record.attributes['product.repositoryId']}"/>
								<dsp:param name="gridType" value="catgrid" />
								<dsp:param name="showQuickView" value="false" />
								<dsp:getvalueof var="index" value="${loopStatus.index}" />
								<dsp:param name="position" value="${loopStatus.count}" />
								<dsp:param name="pageType" value="${pageType}" />
								<dsp:param name="list" value="${list}" />
							</dsp:include>
						</li>
					</c:forEach>
				</ul>
			</div>

			<%-- pagination bottom --%>
			<div class="pagination-container">
				<dsp:include page="/browse/includes/pagination.jsp" >
					<dsp:param name="lastRecNum" value="${contentItem.lastRecNum}"/>
					<dsp:param name="firstRecNum" value="${contentItem.firstRecNum}"/>
					<dsp:param name="recsPerPage" value="${contentItem.recsPerPage}"/>
					<dsp:param name="totalNumRecs" value="${contentItem.totalNumRecs}"/>
				</dsp:include>
			</div>
			<div id="null-filters-message"></div>
		</c:when>
		<c:when test="${ not empty contentItem['@error']}">
			<div>${contentItem['@error']}</div>
			<div id="null-filters-message"></div>
		</c:when>
		<c:otherwise>
			<div id="null-filters-message">Sorry, no products found.</div>
			<script>
				<dsp:param name="totalNumRecs" value="${contentItem.totalNumRecs}"/>
				if (digitalData.page) {
					digitalData.page.searchResults = ${totalNumRecs};
					digitalData.page.searchTerm = '${searchTerm}';
				}
			</script>
		</c:otherwise>
	</c:choose>

</dsp:page>
