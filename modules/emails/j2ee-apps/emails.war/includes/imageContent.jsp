<%--
  - File Name: imageContent.jsp
  - Author(s): Travis Meinders
  - Copyright Notice:
  - Description:
  - Parameters:
  -  image - imageContent map containing URL's for different URL's for use on different media screen profiles
  -  defaultImage - not required. Either "desktop" or "tablet". This will choose the image to be set to the img
  		src tag. Foundation recommendation is to always set it to the smallest, so without this parameter
		the src will be the mobile image if it exists. This param is being included only if one wants
		to override the default default with another default.
  - altDescOverride - override the alt text in the image object
  - linkUrlOverride - override the URL from the image object
  --%>

<dsp:page>
	<dsp:getvalueof var="image" param="image" />
	<dsp:getvalueof var="defaultImage" param="defaultImage" />
	<dsp:getvalueof var="altDescOverride" param="altDescOverride" />
	<dsp:getvalueof var="linkUrlOverride" param="linkUrlOverride" />

	<c:set var="altDesc" value="${image.altDesc}" />
	<c:set var="linkUrl" value="${image.linkUrl}" />

	<c:if test="${not empty altDescOverride}">
		<c:set var="altDesc" value="${altDescOverride}" />
	</c:if>

	<c:if test="${not empty linkUrlOverride}">
		<c:set var="linkUrl" value="${linkUrlOverride}" />
	</c:if>

	<c:set var="imageCount" value="0" />
	<%-- how many sizes do we have? if only 1, forget the data-interchange stuff --%>
	<c:if test="${not empty image.desktopImageUrl}">
		<c:set var="srcval" value="${image.desktopImageUrl}" />
		<c:set var="imageCount" value="${imageCount + 1}" />
	</c:if>
	<c:if test="${not empty image.tabletImageUrl}">
		<c:set var="srcval" value="${image.tabletImageUrl}" />
		<c:set var="imageCount" value="${imageCount + 1}" />
	</c:if>
	<c:if test="${not empty image.mobileImageUrl}">
		<c:set var="srcval" value="${image.ImageUrl}" />
		<c:set var="imageCount" value="${imageCount + 1}" />
	</c:if>

	<c:if test="${imageCount > 1}">

		<%--
			line 57 desktop URL is not nullable, so need to check if it is empty
			line 64 there are only a couple ways to get down to this otherwise, but we are going to do a thorough check from smallest
					to largest until we find a nonempty one for the src since there was either no default set or the default failed
		--%>
		<c:choose>
			<c:when test="${defaultImage eq 'desktop'}">
				<c:set var="defaultImageUrl" value="${image.desktopImageUrl}" />
			</c:when>
			<c:when test="${defaultImage eq 'tablet' && not empty image.tabletImageUrl}">
				<c:set var="defaultImageUrl" value="${image.tabletImageUrl}" />
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${not empty image.mobileImageUrl}">
						<c:set var="defaultImageUrl" value="${image.mobileImageUrl}" />
					</c:when>
					<c:when test="${not empty image.tabletImageUrl}">
						<c:set var="defaultImageUrl" value="${image.tabletImageUrl}" />
					</c:when>
					<c:otherwise>
						<c:set var="defaultImageUrl" value="${image.desktopImageUrl}" />
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>

		<%-- 	Builds the rules for data interchange. --%>
		<c:set var="defaultRule" value="[${defaultImageUrl}, (default)]" />
		<c:if test="${not empty image.mobileImageUrl}">
			<c:set var="smallRule" value=",[${image.mobileImageUrl}, (small)]" />
		</c:if>
		<c:if test="${not empty image.tabletImageUrl}">
			<c:set var="mediumRule" value=",[${image.tabletImageUrl}, (medium)]" />
		</c:if>
		<c:if test="${not empty image.desktopImageUrl}">
			<c:set var="largeRule" value=",[${image.desktopImageUrl}, (large)]" />
		</c:if>

		
	</c:if> <%-- end imageCount if --%>
	
	<c:if test="${not empty linkUrl}">
		<a ${(image.LinkTargetType eq 'external') ? 'target="_blank"':''} ${(image.LinkTargetType eq 'modal') ? 'data-reveal-id="static-modal" data-reveal-ajax="true"':''} href="${linkUrl}">
	</c:if>
	<c:set var="imageAttributes" value="" />
	
	<%-- a little ugly, but using this to help ensure unique ID for image map --%>
	<c:if test="${not empty image.imageMap}">
		<jsp:useBean id="date" class="java.util.Date"/>
		<c:set var="imageMapName" value="imageMap${fn:replace(image.imageMapName,' ','')}${date.time}" />
	</c:if>

	<c:if test="${imageCount > 1}">
		<c:set var="imageAttributes" value='data-interchange="${defaultRule}${smallRule}${mediumRule}${largeRule}"' />
	</c:if>
	<c:if test="${not empty image.height}">
		<c:set var="imageAttributes" value='${imageAttributes} height="${image.height}"' />
	</c:if>
	<c:if test="${not empty image.width}">
		<c:set var="imageAttributes" value='${imageAttributes} width="${image.width}"' />
	</c:if>
	<c:if test="${not empty image.imageMap}">
		<c:set var="imageAttributes" value='${imageAttributes} usemap="#${imageMapName}"' />
	</c:if>

	<c:choose>
		<c:when test="${imageCount == 1}">
			<img src="${srcval}" alt="${altDesc}" ${imageAttributes} />
		</c:when>
		<c:otherwise>
			<img  alt="${altDesc}" ${imageAttributes} />
		</c:otherwise>
	</c:choose>

	<c:if test="${not empty linkUrl}"></a></c:if>

	<c:if test="${not empty image.imageMap}">
		<map name="${imageMapName}" id="${imageMapName}">
			<c:out value="${image.imageMap}" escapeXml="false" />
		</map>
	</c:if>

</dsp:page>
