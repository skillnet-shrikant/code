<%--

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/search/columnRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">

<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean var="Giftlists" bean="/atg/commerce/gifts/Giftlists"/>

<dsp:layeredBundle basename="atg.commerce.csr.Messages">
  <dsp:getvalueof var="field" param="field"/>
  <dsp:getvalueof var="colIndex" param="colIndex"/>
  <dsp:getvalueof var="giftlistItemMap" param="giftlistItemMap"/>
  <dsp:tomap var="ownerItemMap" value="${giftlistItemMap.owner}"/>
  
  <c:choose>
  <c:when test="${field == 'eventName'}">
  
    <c:choose>
      <c:when test="${isMultiSiteEnabled == 'true'}">
    
  	    <dsp:getvalueof var="siteId" param="giftlistItemMap.siteId" />
  	
  	    <dsp:droplet name="/atg/dynamo/droplet/multisite/GetSiteDroplet">
          <dsp:param name="siteId" value="${siteId}" />
          <dsp:oparam name="output">
            <dsp:getvalueof param="site" var="site" />
            <dsp:getvalueof var="siteIcon" param="site.favicon" />
            <dsp:getvalueof var="siteIconHover" param="site.name" />
            <c:choose>
              <c:when test="${!empty siteIcon}">
                "eventName":"<img src=\"${siteIcon}\" title=\"${siteIconHover}\"> <a href=\"#\" class=\"blueU\" onclick=\"atg.commerce.csr.order.gift.giftlistBuyFrom(\'${giftlistItemMap.id}\');return false;\">${fn:escapeXml(giftlistItemMap.eventName)}</a>"
              </c:when>
              <c:otherwise>
                "eventName":"<img src=\"${CSRConfigurator.defaultSiteIconURL}\" title=\"${siteIconHover}\"></img> <a href=\"#\" class=\"blueU\" onclick=\"atg.commerce.csr.order.gift.giftlistBuyFrom(\'${giftlistItemMap.id}\');return false;\">${fn:escapeXml(giftlistItemMap.eventName)}</a>"
              </c:otherwise>
            </c:choose>
          </dsp:oparam>
        </dsp:droplet>
  	  </c:when>
  	  <c:otherwise>
  	    "eventName":"<a href=\"#\" class=\"blueU\" onclick=\"atg.commerce.csr.order.gift.giftlistBuyFrom(\'${giftlistItemMap.id}\');return false;\">${fn:escapeXml(giftlistItemMap.eventName)}</a>"
      </c:otherwise>
    </c:choose>
  </c:when> 

  <c:when test="${field == 'lastName'}">
    "lastName":"${fn:escapeXml(ownerItemMap.lastName)}"
  </c:when>

  <c:when test="${field == 'firstName'}">
    "firstName":"${fn:escapeXml(ownerItemMap.firstName)}"
  </c:when>

  <c:when test="${field == 'eventDate'}">
    <web-ui:formatDate type="date" value="${giftlistItemMap.eventDate}" dateStyle="short" timeStyle="short" var="eventDate"/>
    "eventDate":"${fn:escapeXml(eventDate)}"
  </c:when>

  <c:when test="${field == 'eventType'}">
      <dsp:droplet name="/atg/dynamo/droplet/PossibleValues">
       <dsp:param name="repository" value="${Giftlists}" />
       <dsp:param name="itemDescriptorName" value="gift-list" />
       <dsp:param name="propertyName" value="eventType" />
       <dsp:param name="returnValueObjects" value="true" />
       <dsp:oparam name="output">
        <dsp:getvalueof var="displayValues" param="displayValues" />
        <dsp:getvalueof var="i18nEventType" vartype="java.lang.String" value="${displayValues[giftlistItemMap.eventType].localizedLabel}" /> 
       </dsp:oparam>
      </dsp:droplet> 
    "eventType":"${fn:escapeXml(i18nEventType)}"
  </c:when>

  <c:otherwise>
  </c:otherwise>
  </c:choose>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/search/columnRenderer.jsp#1 $$Change: 946917 $--%>