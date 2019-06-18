<dsp:page>
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:include otherContext="/bv" page="bv_common_script.jsp" />
	<dsp:include otherContext="/bv" page="bv_pdp_script.jsp">
		<dsp:param name="externalId" value="${productId}" />
	</dsp:include>
	<dsp:include otherContext="/bv" page="bv_ratings_container.jsp" />
	<dsp:include otherContext="/bv" page="bv_reviews_container.jsp">
		<dsp:param name="productId" value="${productId}" />
	</dsp:include>
</dsp:page>