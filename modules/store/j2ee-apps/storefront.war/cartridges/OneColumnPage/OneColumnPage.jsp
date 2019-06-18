<dsp:page>
	<dsp:importbean bean="/OriginatingRequest" />
	<dsp:importbean bean="/mff/MFFEnvironment" />
	<dsp:getvalueof var="bvEnabled" bean="MFFEnvironment.bvEnabled"/>
	<dsp:getvalueof var="currentContext" value="${contextPath}" />
	<c:if test="${bvEnabled}">
		<dsp:include otherContext="/bv" page="/common/bv_common_script.jsp" />
		<c:set var="contextPath" value="${currentContext}" />
	</c:if>
	<dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" bean="OriginatingRequest.contentItem"/>

	<c:forEach var="element" items="${contentItem.MainContent}">
      <dsp:renderContentItem contentItem="${element}"/>
    </c:forEach>
	<c:if test="${bvEnabled && not empty bvRecords && not isAjax}">
		<dsp:include otherContext="/bv" page="/productListing/common/bv_plp_script.jsp">
			<dsp:param name="records" value="${bvRecords}" />
		</dsp:include>
		<c:set var="contextPath" value="${currentContext}" />
	</c:if>
</dsp:page>