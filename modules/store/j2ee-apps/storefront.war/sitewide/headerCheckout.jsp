<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/endeca/assembler/droplet/InvokeAssembler"/>

	<%-- Page Variables --%>
	<dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>

	<!-- header -->
	<header class="desktop-header">

		<%-- masthead --%>
		<div class="header-masthead">
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
							<dsp:param name="mobile" value="false"/>
						</dsp:renderContentItem>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="logo">
						<a href="${contextPath}/">
							<img src="${contextPath}/resources/images/new_logo.jpg" alt="Fleet Farm Logo" />
						</a>
					</div>
				</c:otherwise>
			</c:choose>

			<%-- utility nav --%>
			<div class="utility-nav">
				<div class="checkout-header-item">
					<span aria-hidden="true" class="icon icon-lock"></span> <strong>Secure Checkout</strong>
				</div>
				<div class="checkout-header-item">
					Need Help? Call us at <a href="tel:1-877-633-7456">1-877-633-7456</a>
				</div>
			</div>
		</div>

	</header>
	<!-- /header -->
</dsp:page>
