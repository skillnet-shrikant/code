<%--
  - File Name: staticContentOne.jsp
  - Author(s): KnowledgePath Solutions
  - Copyright Notice:
  - Description: This is a template for static content that is a title bar and simple html text content
  --%>

<dsp:page>

<dsp:importbean bean="/atg/targeting/RepositoryLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<dsp:setvalue param="pageContent" value="${requestScope['mffPageContent']}" />


<%--
	sets SEO title & desc into request vars that are used later in the file.
	For these static pages, if seoTags tags are defined in the MFFContentRepository:staticContentItem,
	they would be used as default.
	If defined in SEOTags repository, the seoTags will override the values set in staticContentItem.
--%>

<dsp:getvalueof var="defaultMetaDescription" param="pageContent.metaDescription"/>
<dsp:getvalueof var="defaultPageTitle" param="pageContent.pageTitle"/>
<dsp:getvalueof var="staticURI" param="pageContent.redirectUrl"/>

<dsp:include page="/sitewide/includes/seoTags.jsp" flush="true">
	<dsp:param name="key" value="${fn:toLowerCase(staticURI)}" />
	<dsp:param name="defaultPageTitle" value="${defaultPageTitle}" />
	<dsp:param name="defaultMetaDescription" value="${defaultMetaDescription}" />
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
	<jsp:attribute name="bodyClass">static-content</jsp:attribute>
	<jsp:body>


 		<dsp:getvalueof var="articleId" param="articleId"/>

		<%-- breadcrumbs --%>
		<section class="breadcrumbs">
			<ul aria-label="breadcrumbs" role="navigation">
				<li><a href="${contextPath}/" class="crumb">Home</a></li>
				<li><span class="crumb active">About Fleet Farm</span></li>
			</ul>
		</section>

		<div class="two-column-container">
			<div class="section-title">
				<h1>About Fleet Farm</h1>
			</div>
			<div class="two-column-left">

				<div class="mobile-refinements">
					<div class="section-title">
						<h2>Choose a topic</h2>
					</div>
					<div class="hide-refinements">
						<span class="icon icon-close" aria-hidden="true"></span>
						<span class="sr-only">close</span>
					</div>
					<button class="button secondary expand hide-sidebar">Back to Content</button>
				</div> <!-- /mobile-refinements -->

				<%-- topic accordion --%>
				<div class="topic-accordion">
					<dsp:include page="/content/includes/topicAccordion.jsp" />
				</div>

			</div> <!-- /two-column-left -->

			<c:if test="${empty articleId}">
				<dsp:getvalueof var="articleId" param="pageContent.contentId"/>
			</c:if>

			<dsp:getvalueof var="jspPagePath" value="" />

			<dsp:droplet name="RepositoryLookup">
				   <dsp:param name="id" value="${articleId}"/>
				   <dsp:param name="itemDescriptor" value="mffStaticContent"/>
				   <dsp:param name="repository" bean="/com/mff/content/repository/MFFContentRepository"/>
				   <dsp:oparam name="output">
						<dsp:getvalueof var="staticContentItem" param="element" />
						<!--<dsp:getvalueof var="jspPagePath" param="element.jspPagePath" />-->
					</dsp:oparam>
				</dsp:droplet>
			<dsp:setvalue param="staticContentItem" value="${staticContentItem}"/>
			<dsp:getvalueof var="jspPagePath" param="staticContentItem.jspPagePath" />

			<div class="two-column-right">
	   		<%-- show topic accordion on small screens --%>
				<button class="button secondary expand show-sidebar">Choose topic</button>

				<c:choose>
					<c:when test="${not empty jspPagePath}">
							<%-- for contact us page - to be hidden until click request --%>
							<div class="contact-us-form">
								<dsp:include page="${jspPagePath}" />
							</div>
					</c:when>

					<c:otherwise>
						<dsp:droplet name="ForEach">
							<c:set var="counter" value="${counter + 1}" scope="page"/>
						  <dsp:param name="array" param="staticContentItem.contentSections" />
						  <dsp:setvalue param="rltdArticle" paramvalue="element"/>
						  <dsp:oparam name="outputStart">
								<!--<div class="content-title"><dsp:valueof param="rltdArticle.name"/></div>-->
						   		<div class="content-container">
						  </dsp:oparam>
						  <dsp:oparam name="output">
								<dsp:getvalueof var="headline" param="rltdArticle.headline" />
								<dsp:getvalueof var="contentKey" param="staticContentItem.contentKey" />
								<dsp:getvalueof var="body" param="rltdArticle.body" />
								<dsp:getvalueof var="contentSectionHeading" param="rltdArticle.name" />

								<%-- for random headings or subheadings --%>
								<%-- <c:if test="${not empty headline}">
									<div class="content-section">
										<div class="content-title">
											${headline}
										</div>
									</div>
								</c:if> --%>

								<%-- for FAQ page --%>
								<c:if test="${contentKey == 'FAQ'}">

									<%-- show topic accordion on small screens --%>
									<div class="content-container">
										<dsp:getvalueof var="faqId" param="rltdArticle.headline" />
										<dsp:getvalueof var="question" param="rltdArticle.abstract" />
										<dsp:getvalueof var="answer" param="rltdArticle.body" />

										<div id="${faqId}" class="accordion" role="tablist" aria-multiselectable="true" data-accordion>
											<div class="accordion-container">
												<div class="accordion-title content-acc-title" role="tab" aria-controls="panel${counter}" id="tab${counter}">
													<div class="content-section question">
														${question}
														<span class="icon icon-plus" aria-hidden="true"></span>
													</div>
												</div>
												<div class="accordion-body" aria-labelledby="tab${counter}" role="tabpanel" id="panel${counter}">
													<div class="accordion-body-content">
														<p>${answer}</p>
													</div>
												</div>
											</div>
										</div>

									</div>
								</c:if>

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
					</c:otherwise>
				</c:choose>

			</div> <!-- /two-column-right -->
		</div> <!-- /two-column-container -->
	</jsp:body>
</layout:default>
</dsp:page>
