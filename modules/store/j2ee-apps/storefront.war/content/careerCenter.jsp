<%--
  - File Name: careerCenter.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is a template for the careers page pages
  --%>

<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<dsp:setvalue param="pageContent" value="${requestScope['mffPageContent']}" />
<dsp:getvalueof var="staticURI" param="pageContent.redirectUrl"/>
<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
	<dsp:param name="key" value="${fn:toLowerCase(staticURI)}" />
	<dsp:param name="defaultPageTitle" value="Career Center" />
	<dsp:param name="defaultMetaDescription" value="" />
	<dsp:param name="defaultCanonicalURL" value="" />
	<dsp:param name="defaultRobotsIndex" value="index" />
	<dsp:param name="defaultRobotsFollow" value="follow" />
</dsp:include>

<layout:default>
	<jsp:attribute name="pageTitle">${pageTitle}</jsp:attribute>
	<jsp:attribute name="metaDescription">${metaDescription}</jsp:attribute>
	<jsp:attribute name="metaKeywords"></jsp:attribute>
	<jsp:attribute name="seoCanonicalURL">${canonicalURL}</jsp:attribute>
	<jsp:attribute name="seoRobots">${robots}</jsp:attribute>
	<jsp:attribute name="lastModified"></jsp:attribute>
	<jsp:attribute name="section">content</jsp:attribute>
	<jsp:attribute name="pageType">staticContent</jsp:attribute>
	<jsp:attribute name="bodyClass">career-center</jsp:attribute>
	<jsp:body>

		

		<%-- breadcrumbs --%>
		<section class="breadcrumbs">
			<ul aria-label="breadcrumbs" role="navigation">
				<li><a href="${contextPath}/" class="crumb">Home</a></li>
				<li><span class="crumb active">Career Center</span></li>
			</ul>
		</section>

		<div class="two-column-container">
			<div class="section-title">
				<h1>Career Center</h1>
			</div>
				
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="pageContent.contentSections" />
			  	<dsp:setvalue param="rltdArticle" paramvalue="element"/>
			  	<dsp:oparam name="outputStart">
					<div class="content-container">
			  	</dsp:oparam>
			  	<dsp:oparam name="output">
					<dsp:getvalueof var="body" param="rltdArticle.body" />
					
					<!-- for all the copy text -->
					<c:if test="${not empty body && empty question}">
						<div class="content-section content-copy">
							<p>${body}</p>
						</div>
					</c:if>

				</dsp:oparam>
				<dsp:oparam name="outputEnd">
					</div>
				</dsp:oparam>
			</dsp:droplet>
		</div>
	</jsp:body>
</layout:default>
</dsp:page>
