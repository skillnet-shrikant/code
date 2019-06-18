<dsp:page>
	<dsp:importbean bean="/atg/endeca/assembler/droplet/InvokeAssembler"/>
	<!-- mobile header -->
	<header class="mobile-header accordion" role="tablist" aria-multiselectable="false" data-accordion>

		<%-- masthead --%>
		<div class="masthead-mobile">

			<%-- logo --%>
			<dsp:droplet name="InvokeAssembler">
				<dsp:param name="includePath" value=""/>
				<dsp:param name="contentCollection" value="/content/Shared/Global Header"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="headerLogoContent" vartype="com.endeca.infront.assembler.ContentItem" param="contentItem" />
				</dsp:oparam>
			</dsp:droplet>
			<c:choose>
				<c:when test="${not empty headerLogoContent.contents}">
					<c:forEach var="headerElement" items="${headerLogoContent.contents}">
						<dsp:renderContentItem contentItem="${headerElement}" >
							<dsp:param name="mobile" value="true"/>
						</dsp:renderContentItem>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="masthead-logo">
						<a href="${contextPath}/">
							<img src="${assetPath}/images/new_logo.png" alt="Fleet Farm">
						</a>
					</div>
				</c:otherwise>
			</c:choose>

			<%-- secure checkout copy --%>
			<div class="secure-checkout-copy">
				<div class="checkout-header-item">
					<span class="icon icon-lock" aria-hidden="true"></span>
				</div>
				<div class="checkout-header-item">
					Secure<br>Checkout
				</div>
			</div>

		</div>

	</header>
	<!-- /mobile header -->

</dsp:page>
