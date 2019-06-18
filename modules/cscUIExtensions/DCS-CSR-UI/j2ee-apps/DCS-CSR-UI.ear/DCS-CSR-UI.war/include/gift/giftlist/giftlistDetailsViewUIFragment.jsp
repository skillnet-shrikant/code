<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlist/giftlistDetailsViewUIFragment.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistLookupDroplet" />
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet" />
  <dsp:getvalueof var="giftlistId" param="giftlistId" scope="request" />
  <dsp:getvalueof var="idEdit" param="isEdit" scope="request" />
    <c:if test="${not empty giftlistId }">
      <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
        <dsp:param name="id" param="giftlistId" />
        <dsp:oparam name="output">
          <dsp:setvalue paramvalue="element" param="giftlist" />
          <div id="atg_commerce_csr_customerinfo_giftlist_subPanel" class="atg_svc_subPanel">
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
                  <dsp:param name="repository" value="${glfh.giftlistRepository}" />
                  <dsp:param name="itemDescriptorName" value="gift-list" />
                  <dsp:param name="propertyName" value="eventType" />
                  <dsp:param name="returnValueObjects" value="true" />
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="displayValues" param="displayValues" />
                    <dsp:getvalueof var="eventType" vartype="java.lang.String" param="giftlist.eventType" /> 
                    <dsp:getvalueof var="i18nEventType" vartype="java.lang.String" value="${displayValues[eventType].localizedLabel}" /> 
                    <c:out value="${i18nEventType}" />																		
                  </dsp:oparam>
                </dsp:droplet> 
                </span></li>
                <li><span class="atg_svc_fieldTitle"><label><fmt:message
                  key="giftlists.view.eventDate" /></label></span> <span class="plainText">
                  <dsp:getvalueof var="giftlistEventDate" param="giftlist.eventDate"/>
                  <web-ui:formatDate type="date" value="${giftlistEventDate}" dateStyle="medium" var="eventDate"/>
                  <c:out value="${eventDate}"/>
                  </span></li>
              </ul>
              <ul class="atg_commerce_csr_giftRegistryInfo">
                <li><span class="atg_svc_fieldTitle"><label><fmt:message key="giftlists.view.status" /> </label></span> 
                  <dsp:getvalueof var="published" param="giftlist.published" />
                  <dsp:droplet name="atg/dynamo/droplet/Switch">
                    <dsp:param name="value" value="${published}"/>
                      <span class="plainText">
                        <dsp:oparam name="true"><fmt:message key="giftlists.public.label"/></dsp:oparam>
                        <dsp:oparam name="false"><fmt:message key="giftlists.private.label"/></dsp:oparam>
                      </span>
                  </dsp:droplet>
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
              <ul class="atg_commerce_csr_giftRegistryInfo">
                <li>
                  <dsp:tomap var="addr" param="giftlist.shippingAddress"/>
                  <span class="atg_svc_fieldTitle"><label><dsp:layeredBundle basename="atg.commerce.csr.Messages"><fmt:message key="giftlists.view.shippingAddress" /></dsp:layeredBundle></label></span>
                </li>  
                <div class="atg-commerce-csr-giftlist-address">
                  <c:choose>
                    <c:when test="${!empty addr }">
                      <dsp:include src="/include/addresses/addressView.jsp" otherContext="${CSRConfigurator.contextRoot}">
                        <dsp:param name="address" value="${addr}" />
                      </dsp:include>
                    </c:when>
                    <c:otherwise>
                      <li>
                        <span class="plainText atg_commerce_csr_giftListAddress">
                          <dsp:layeredBundle basename="atg.commerce.csr.Messages">
                            <fmt:message key="giftlists.view.shippingAddress.none"/>
                          </dsp:layeredBundle>
                        </span>
                      </li>
                    </c:otherwise>
                  </c:choose>
                </div>
              </ul>
          </div>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlist/giftlistDetailsViewUIFragment.jsp#1 $$Change: 946917 $--%>
