<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>
	<c:if test="${not empty contentItem.adjustedSearches}">
		<c:forEach var="originalTerm" items="${contentItem.originalTerms}">
			<c:forEach var="adjustedSearch" items="${contentItem.adjustedSearches[originalTerm]}">
				<c:if test="${adjustedSearch.spellCorrected}">
					<div>${originalTerm} adjusted to ${adjustedSearch.adjustedTerms}</div>
				</c:if>
			</c:forEach>
		</c:forEach>
	</c:if>

	<c:if test="${not empty contentItem.suggestedSearches}">
		<c:forEach var="originalTerm" items="${contentItem.originalTerms}">
			<c:forEach var="suggestedSearch" items="${contentItem.suggestedSearches[originalTerm]}">
				<div>Did you mean <a href="${contextPath}/search?Ntt=${suggestedSearch.label}">${suggestedSearch.label}</a></div>
			</c:forEach>
		</c:forEach>
	</c:if>



</dsp:page>
