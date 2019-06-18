<%--
	- File Name: seoTags.jsp
	- Author(s): KnowledgePath Solutions UX Team
	- Copyright Notice:
	- Description: Fetches SEO Tags from SEORepository & renders them
	- Parameters:
	- 	key	- The key to the SEOTag repository item
	- 	defaultMetaDescription - Default description if an SEOTag item is not found or configured.
	-	defaultPageTitle - Default page title if an SEOTag item is not found or configured.
--%>

<dsp:page>

	<dsp:getvalueof var="key" param="key" />
	<dsp:getvalueof var="defaultPageTitle" param="defaultPageTitle" />
	<dsp:getvalueof var="defaultMetaDescription" param="defaultMetaDescription" />
	<dsp:getvalueof var="defaultCanonicalURL" param="defaultCanonicalURL" />
	<dsp:getvalueof var="robotsIndex" param="defaultRobotsIndex" />
	<dsp:getvalueof var="robotsFollow" param="defaultRobotsFollow" />

	<c:if test="${not empty key}">
		<dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange">
		   <dsp:param name="repository" value="/atg/seo/SEORepository" />
		   <dsp:param name="itemDescriptor" value="SEOTags" />
		   <dsp:param name="howMany" value="1" />
		   <dsp:param name="mykey" value="${key}" />
		   <dsp:param name="queryRQL" value="key= :mykey" />
		
		   <dsp:oparam name="output">
		        <c:set var="pageTitle" scope="request"><dsp:valueof param="element.title"/></c:set>
		        <c:set var="metaDescription" scope="request"><dsp:valueof param="element.description"/></c:set>
		        <c:set var="canonicalURL" scope="request"><dsp:valueof param="element.canonicalURL"/></c:set>
	        	<c:if test = "${empty canonicalURL}">
			       <c:set var="canonicalURL" scope="request"><dsp:valueof value="${defaultCanonicalURL}"/></c:set>
		        </c:if>
		        <dsp:getvalueof var="robotsIndexVal" param="element.robotsIndex" />
		        <c:if test="${robotsIndexVal == 'false'}">
		        	<dsp:getvalueof var="robotsIndex" value="noindex" />
		        </c:if>
		        <dsp:getvalueof var="robotsFollowVal" param="element.robotsFollow" />
		        <c:if test="${robotsFollowVal == 'false'}">
		        	<dsp:getvalueof var="robotsFollow" value="nofollow" />
		        </c:if>
		        <c:set var="robots" scope="request">${robotsIndex},${robotsFollow}</c:set>
		   </dsp:oparam>
		   <dsp:oparam name="empty">
		        <c:set var="pageTitle" scope="request">${defaultPageTitle}</c:set>
		        <c:set var="metaDescription" scope="request">${defaultMetaDescription}</c:set>
		        <c:set var="canonicalURL" scope="request">${defaultCanonicalURL}</c:set>
		        <c:set var="robots" scope="request">${robotsIndex},${robotsFollow}</c:set>
		   </dsp:oparam>
		</dsp:droplet>
	</c:if>
	
</dsp:page>	