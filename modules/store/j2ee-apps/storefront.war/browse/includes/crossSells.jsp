<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

	<%-- Page Variables --%>
	<%-- TODO: Phase 2 - recommendations --%>
	<%-- get cross sells product array --%>
	<%--<dsp:getvalueof var="crossSells" bean="" />--%>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${crossSells}"/>
		<dsp:oparam name="outputStart">
			<section class="you-may-also-like">
				<div class="section-title">
					<h2>You May Also Like</h2>
				</div>
				<div class="section-row">
					<div class="section-content">
						<div class="product-slider cross-sells">
		</dsp:oparam>
		<dsp:oparam name="output">
			<dsp:include page="/browse/includes/productTile.jsp">
				<dsp:param name="product" param="element.product" />
				<dsp:param name="gridType" value="grid4" />
			</dsp:include>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
						</div>
					</div>
				</div>
			</section>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
