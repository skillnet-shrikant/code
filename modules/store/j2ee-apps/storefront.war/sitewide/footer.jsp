<dsp:page>

	<dsp:importbean bean="/atg/endeca/assembler/droplet/InvokeAssembler"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="listrakEnabled" bean="/mff/MFFEnvironment.listrakEnabled"/>
	<jsp:useBean id="date" class="java.util.Date" />

	<footer>
		<c:if test="${not param.isCheckout}">
			<dsp:droplet name="InvokeAssembler">
				<dsp:param name="includePath" value=""/>
				<dsp:param name="contentCollection" value="/content/Shared/Global Footer"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="footerContent" vartype="com.endeca.infront.assembler.ContentItem" param="contentItem" />
				</dsp:oparam>
			</dsp:droplet>
			<c:forEach var="contentElement" items="${footerContent.contents}">
				<dsp:renderContentItem contentItem="${contentElement}" />
			</c:forEach>
		</c:if>

		<div class="footer-legal">
			<div class="footer-copyright">
				<p>&copy;<fmt:formatDate value="${date}" pattern="yyyy" /> Fleet Farm E-Commerce Enterprises LLC, or their affiliates.</p>
			</div>
			<div class="footer-legal-links">
				<ul>
					<li><a href="${contextPath}/static/visitor-info-collection">Terms of Use</a></li>
					<li><a href="${contextPath}/static/respect-for-privacy">Privacy Policy</a></li>
				</ul>
			</div>
		</div>

	</footer>

	<dsp:param name="pageType" value="${pageType}" />
	<c:if test="${pageType eq 'product' or pageType eq 'cart' or pageType eq 'orderConfirmation'}">
		<%@ include file="/sitewide/third_party/gtm_adwords.jspf" %>
	</c:if>

</dsp:page>
