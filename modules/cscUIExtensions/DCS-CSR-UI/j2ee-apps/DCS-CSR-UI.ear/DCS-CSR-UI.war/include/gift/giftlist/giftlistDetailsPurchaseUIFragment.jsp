<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlist/giftlistDetailsPurchaseUIFragment.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistLookupDroplet" />
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet" />
  <dsp:importbean var="Giftlists" bean="/atg/commerce/gifts/Giftlists"/>
  
  <dsp:getvalueof var="giftlistId" param="giftlistId" scope="request" />
  <dsp:getvalueof var="idEdit" param="isEdit" scope="request" />
    <c:if test="${not empty giftlistId }">
      <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
        <dsp:param name="id" param="giftlistId" />
        <dsp:oparam name="output">
          <dsp:setvalue paramvalue="element" param="giftlist" />
          <div class="atg_svc_subPanel">
            <dsp:layeredBundle basename="atg.commerce.csr.Messages">
              <ul class="atg_commerce_csr_giftRegistryInfo">
                <li><span class="atg_svc_fieldTitle"><label><fmt:message
                  key="giftlists.view.eventName" /></label></span> <span class="plainText"><dsp:getvalueof
                  var="eventName" vartype="java.lang.String"
                  param="giftlist.eventName" /> <c:out value="${eventName}" />
                </span></li>
                <li><span class="atg_svc_fieldTitle"><label><fmt:message
                  key="giftlists.view.eventType" /></label></span> <span class="plainText">
                <dsp:droplet name="/atg/dynamo/droplet/PossibleValues">
                 <dsp:param name="repository" value="${Giftlists}" />
                 <dsp:param name="itemDescriptorName" value="gift-list" />
                 <dsp:param name="propertyName" value="eventType" />
                 <dsp:param name="returnValueObjects" value="true" />
                 <dsp:oparam name="output">
                  <dsp:getvalueof var="displayValues" param="displayValues" />
                  <dsp:getvalueof  var="eventType" vartype="java.lang.String"  param="giftlist.eventType" /> 
                  <dsp:getvalueof var="i18nEventType" vartype="java.lang.String" value="${displayValues[eventType].localizedLabel}" /> 
                  <c:out value="${i18nEventType}" />                  
                 </dsp:oparam>
                </dsp:droplet> 
                </span></li>
                <li><span class="atg_svc_fieldTitle"><label><fmt:message
                  key="giftlists.view.eventDate" /></label></span> <span class="plainText">
                  <dsp:getvalueof var="giftlistEventDate" param="giftlist.eventDate"/>
                  <web-ui:formatDate type="date" value="${giftlistEventDate}" dateStyle="short" timeStyle="short" var="eventDate"/>
                  <c:out value="${eventDate}"/>
                  </span></li>
              </ul>
              <ul class="atg_commerce_csr_giftRegistryInfo">
                <li>
                  <span class="atg_svc_fieldTitle"><label><fmt:message key="giftlists.view.owner" /></label></span>
                  <span class="plainText">
                    <dsp:valueof param="giftlist.owner.firstName"/>
                    <dsp:valueof param="giftlist.owner.middleName"/>
                    <dsp:valueof param="giftlist.owner.lastName"/>
                  </span>
                </li> 
             
                <c:if test="${isMultiSiteEnabled == true}">
                  <dsp:getvalueof var="siteId" param="giftlist.siteId" />
                  <dsp:droplet name="/atg/dynamo/droplet/multisite/GetSiteDroplet">
                    <dsp:param name="siteId" value="${siteId}" />
                    <dsp:oparam name="output">
                      <dsp:getvalueof param="site" var="site" />
                      <dsp:getvalueof var="siteName" param="site.name" />
                        <li>
                          <span class="atg_svc_fieldTitle"><label><fmt:message key="giftlists.view.site" /></label></span> 
                          <span class="plainText"><c:out value="${siteName}"/></span>
                        </li>
                    </dsp:oparam>
                  </dsp:droplet>
                </c:if>
              </ul>
            </dsp:layeredBundle>

          </div>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlist/giftlistDetailsPurchaseUIFragment.jsp#1 $$Change: 946917 $--%>
