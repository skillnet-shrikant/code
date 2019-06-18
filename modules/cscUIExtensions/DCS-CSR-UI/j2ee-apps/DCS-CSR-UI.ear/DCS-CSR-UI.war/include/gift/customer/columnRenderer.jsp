<%--

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/customer/columnRenderer.jsp#2 $$Change: 953229 $
@updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet" />
  <dsp:importbean bean="atg/commerce/gifts/GiftlistLookupDroplet" />
  <dsp:getvalueof var="cartShareableTypeId" bean="/atg/commerce/custsvc/util/CSRConfigurator.cartShareableTypeId"/>
  <dsp:getvalueof var="viewprofileid" bean="/atg/userprofiling/ServiceCustomerProfile.repositoryId" />
  <dsp:getvalueof var="activeprofileid" bean="/atg/userprofiling/ActiveCustomerProfile.repositoryId" />
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean var="Giftlists" bean="/atg/commerce/gifts/Giftlists"/>
  <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
  <dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>
  
  <dsp:getvalueof var="currentSiteId" bean="Site.id"/>
  <c:if test="${activeprofileid == viewprofileid}">
    <dsp:setvalue param="mode" value="edit" />
  </c:if>
  <c:if test="${activeprofileid != viewprofileid}">
    <dsp:setvalue param="mode" value="view" />
  </c:if>   
  
  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
  <dsp:getvalueof var="field" param="field"/>
  <dsp:getvalueof var="colIndex" param="colIndex"/>
  <dsp:getvalueof var="giftlistItemMap" param="giftlistItemMap"/>
  
  <dsp:tomap var="ownerItemMap" value="${giftlistItemMap.owner}"/>
  <c:choose>
  <c:when test="${field == 'eventName' and isMultiSiteEnabled == 'true'}">
    <dsp:getvalueof var="siteId" param="giftlistItemMap.siteId" />
    <dsp:droplet name="/atg/dynamo/droplet/multisite/GetSiteDroplet">
      <dsp:param name="siteId" value="${siteId}" />
      <dsp:oparam name="output">
        <dsp:getvalueof param="site" var="site" />
        <dsp:getvalueof var="siteIcon" param="site.favicon" />
        <dsp:getvalueof var="siteIconHover" param="site.name" />
        <c:choose>
          <c:when test="${!empty siteIcon}">
"eventName":"<img src=\"${siteIcon}\" title=\"${siteIconHover}\"> <a href=\"#\" class=\"blueU\" onclick=\"atg.commerce.csr.order.gift.giftlistSelect(\'${giftlistItemMap.id}\');return false;\">${fn:escapeXml(giftlistItemMap.eventName)}</a>"
          </c:when>
          <c:otherwise>
"eventName":"<img src=\"${CSRConfigurator.defaultSiteIconURL}\" title=\"${siteIconHover}\"></img> <a href=\"#\" class=\"blueU\" onclick=\"atg.commerce.csr.order.gift.giftlistSelect(\'${giftlistItemMap.id}\');return false;\">${fn:escapeXml(giftlistItemMap.eventName)}</a>"
          </c:otherwise>
        </c:choose>
      </dsp:oparam>
    </dsp:droplet>
  </c:when>
  
  <c:when test="${field == 'eventName' and isMultiSiteEnabled == 'false'}">
    "eventName":"<a href=\"#\" class=\"blueU\" onclick=\"atg.commerce.csr.order.gift.giftlistSelect(\'${giftlistItemMap.id}\');return false;\">${fn:escapeXml(giftlistItemMap.eventName)}</a>"
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
  
  <c:when test="${field == 'eventDate'}">
    <web-ui:formatDate type="date" value="${giftlistItemMap.eventDate}" dateStyle="short" var="eventDate"/>
    "eventDate":"${fn:escapeXml(eventDate)}"
  </c:when>

  <c:when test="${field == 'status'}">
    <c:choose>
      <c:when test="${giftlistItemMap.published == 'true'}">
        "status":"<fmt:message key="giftlists.public.label"/>"
      </c:when>
      <c:otherwise>
        "status":"<fmt:message key="giftlists.private.label"/>"
      </c:otherwise>
     </c:choose>
  </c:when>
  
  <c:when test="${field == 'buyFrom'}">
    <c:if test="${isMultiSiteEnabled == true}">
      <dsp:getvalueof var="giftlistId" param="giftlistItemMap.Id" />
      <dsp:getvalueof var="giftlistSiteId" param="giftlistItemMap.siteId" />
      <dsp:droplet name="/atg/dynamo/droplet/multisite/SitesShareShareableDroplet">
        <dsp:param name="siteId" value="${currentSiteId}" />
         <dsp:param name="otherSiteId" value="${giftlistSiteId}" />
         <dsp:param name="shareableTypeId" value="${cartShareableTypeId}" />
         <dsp:oparam name="true">
           <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
             <dsp:param name="id" value="${giftlistId}" />
             <dsp:oparam name="output">
               <dsp:setvalue paramvalue="element" param="giftlist" />
               <dsp:setvalue paramvalue="giftlist.giftlistItems" param="items" />
               <dsp:setvalue paramvalue="giftlist.id" param="giftlistId" />
               <dsp:getvalueof var="items" vartype="java.lang.Object" param="items" />
               <dsp:getvalueof var="isPublic" vartype="java.lang.String" param="giftlist.published" />
               <c:choose>
                 <c:when test="${not empty items and isPublic == 'true'}">
                   "buyFrom":"<a href=\"#\" class=\"blueU\" onclick=\"atg.commerce.csr.order.gift.giftlistBuyFrom(\'${giftlistItemMap.id}\');return false;\"><fmt:message key="giftlists.buyFrom.label"/></a>"            
                 </c:when>
                 <c:otherwise>
                   "buyFrom":""
                 </c:otherwise>
              </c:choose>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
        <dsp:oparam name="false">
          <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
            <dsp:param name="id" value="${giftlistId}" />
            <dsp:oparam name="output">
              <dsp:setvalue paramvalue="element" param="giftlist" />
              <dsp:setvalue paramvalue="giftlist.giftlistItems" param="items" />
              <dsp:setvalue paramvalue="giftlist.id" param="giftlistId" />
              <dsp:getvalueof var="items" vartype="java.lang.Object" param="items" />
              <dsp:getvalueof var="isPublic" vartype="java.lang.String" param="giftlist.published" />
              <c:choose>
                <c:when test ="${envTools.siteAccessControlOn == 'true' }">
                  <dsp:getvalueof var="siteId" param="giftlistItemMap.siteId"/>
                  <dsp:droplet name="IsSiteAccessibleDroplet">
                    <dsp:param name="siteId" value="${siteId}"/>
                    <dsp:oparam name="true">
                      <c:choose>
                        <c:when test="${not empty items and isPublic == 'true'}">
                          "buyFrom":"<a href=\"#\" class=\"blueU\" onclick=\"atg.commerce.csr.order.gift.changeSiteContext(\'${giftlistSiteId}\',\'atg_commerce_csr_customerGiftlistChangeSiteForm\');\"><fmt:message key="giftlists.giftlist.ChangeSite.label"/></a>"
                        </c:when>
                        <c:otherwise>
                          "buyFrom":""
                        </c:otherwise>
                      </c:choose>
                    </dsp:oparam>
                    <dsp:oparam name="false">
                      "buyFrom":""
                    </dsp:oparam>
                  </dsp:droplet>
                </c:when>
                <c:otherwise>
                  <c:choose>
                    <c:when test="${not empty items and isPublic == 'true'}">
                      "buyFrom":"<a href=\"#\" class=\"blueU\" onclick=\"atg.commerce.csr.order.gift.changeSiteContext(\'${giftlistSiteId}\',\'atg_commerce_csr_customerGiftlistChangeSiteForm\');\"><fmt:message key="giftlists.giftlist.ChangeSite.label"/></a>"
                    </c:when>
                    <c:otherwise>
                      "buyFrom":""
                    </c:otherwise>
                  </c:choose>
                </c:otherwise>
              </c:choose>   
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
    <c:if test="${isMultiSiteEnabled == false}">
      <dsp:getvalueof var="giftlistId" param="giftlistItemMap.Id" />
      <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
        <dsp:param name="id" value="${giftlistId}" />
        <dsp:oparam name="output">
          <dsp:setvalue paramvalue="element" param="giftlist" />
          <dsp:setvalue paramvalue="giftlist.giftlistItems" param="items" />
          <dsp:setvalue paramvalue="giftlist.id" param="giftlistId" />
          <dsp:getvalueof var="items" vartype="java.lang.Object" param="items" />
          <dsp:getvalueof var="isPublic" vartype="java.lang.String" param="giftlist.published" />
          <c:choose>
            <c:when test="${not empty items and isPublic =='true'}">
              "buyFrom":"<a href=\"#\" class=\"blueU\" onclick=\"atg.commerce.csr.order.gift.giftlistBuyFrom(\'${giftlistItemMap.id}\');return false;\"><fmt:message key="giftlists.buyFrom.label"/></a>"            
            </c:when>
            <c:otherwise>
              "buyFrom":""
            </c:otherwise>
          </c:choose>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
  </c:when>
  
  <c:when test="${field == 'remove'}">
    <fmt:message key="giftlists.delete.popup.title" var="giftlistDeletePopupTitle" />
    <dsp:droplet name="/atg/dynamo/droplet/Switch">
      <dsp:param param="mode" name="value" />
      <dsp:oparam name="edit">
        "remove":"<a href=\"#\" class=\"atg_commerce_csr_propertyClear\" onclick=\"atg.commerce.csr.order.gift.giftlistDelete(\'${giftlistItemMap.id}\',\'${giftlistDeletePopupTitle}\');return false;\"></a>"
      </dsp:oparam>
      <dsp:oparam name="view">
        "remove":"" 
      </dsp:oparam>
    </dsp:droplet>
  </c:when>
  
  <c:otherwise>
  </c:otherwise>
  </c:choose>
</dsp:layeredBundle>
</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/customer/columnRenderer.jsp#2 $$Change: 953229 $--%>