<%--
 Order Data Column Renderer
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/columnRenderer.jsp#3 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%><%@ include file="/include/top.jspf"%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/custsvc/promotion/ProfileHasPromotionDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/ActiveCustomerProfile" var="activeCustomerProfile"/>
  <dsp:importbean bean="/atg/commerce/custsvc/ui/tables/promotion/AvailablePromotionsGrid" var="walletGridConfig"/>
  <dsp:importbean bean="/atg/commerce/custsvc/multisite/IsPromotionAccessibleDroplet"/>
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <dsp:getvalueof var="field" param="field"/>
    <dsp:getvalueof var="colIndex" param="colIndex"/>
    <dsp:getvalueof var="isComma" param="isComma"/>
    <dsp:getvalueof var="promotion" param="promotionMap"/>
    <dsp:getvalueof var="promotionItem" param="promotionItem"/>
    <dsp:getvalueof var="state" param="promotionState"/>
    <dsp:getvalueof var="order" param="order"/>

    <fmt:message key="common.separator.semicolon" var="separator"/>
    <fmt:message key="promotion.applied" var="appliedLabel"/>
    <fmt:message key="promotion.close" var="closeLabel"/>
    <fmt:message key="promotion.future" var="futureLabel"/>
    <fmt:message key="promotion.notApplied" var="notAppliedLabel"/>
    <fmt:message key="promotion.granted" var="grantedLabel"/>
    <fmt:message key="promotion.grant" var="grantLink"/>
    <fmt:message key="promotion.removeGrant" var="removeLink"/>

    <c:choose>
    <c:when test="${field == 'closenessQualifiers'}">
      <c:choose>
      <c:when test="${!empty state && state.applied}">
        <json:property name="closenessQualifiers" value="<span class=\"atg_commerce_csr_iconPromoApplied\" title=\"${appliedLabel}\"/>"/>
      </c:when>
      <c:when test="${!empty state && state.close}">
        <json:property name="closenessQualifiers" value="<span class=\"atg_commerce_csr_iconPromoAlmost\" title=\"${closeLabel}\"/>"/>
      </c:when>
      <c:when test="${!empty state && !state.usable}">
        <json:property name="closenessQualifiers" value="<span class=\"atg_commerce_csr_iconPromoNotAvailable\" title=\"${futureLabel}\"/>"/>
      </c:when>
      <c:otherwise>
        <json:property name="closenessQualifiers" value="<span class=\"atg_commerce_csr_iconPromoNotQualified\" title=\"${notAppliedLabel}\"/>"/>
      </c:otherwise>
      </c:choose>
    </c:when>

    <c:when test="${field == 'displayName'}">
      <c:set var="displayName" value="${promotion.displayName}"/>
      <c:if test="${fn:length(displayName) > 50}">
        <fmt:message key="common.ellipsis" var="displayName">
          <fmt:param>${fn:substring(promotion.displayName, 0, 50)}</fmt:param>
        </fmt:message>
      </c:if>
      <json:property name="displayName" value="<span title=\"${fn:escapeXml(promotion.displayName)}\">${displayName}</span>"/>
      <c:remove var="displayName"/>
    </c:when>

    <c:when test="${field == 'site'}">
      <c:set var="sites" value=""/>
      <c:set var="sitesHover" value=""/>
      <dsp:droplet name="/atg/commerce/custsvc/promotion/GetAvailableSitesForPromotion">
        <dsp:param name="promotion" value="${promotionItem}"/>
        <dsp:param name="elementName" value="siteList"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="siteList" param="siteList"/>
          <c:choose>
            <c:when test="${fn:length(siteList) > 0}">
              <c:forEach var="siteName" items="${siteList}" varStatus="status">
                <c:choose>
                  <c:when test="${status.index == 0}">
                    <c:set var="sites" value="${siteName}"/>
                    <c:set var="sitesHover" value="${siteName}"/>
                  </c:when>
                  <c:when test="${status.index == 1}">
                    <c:set var="sites" value="${sites}${separator} ${siteName}"/>
                    <c:set var="sitesHover" value="${sitesHover}${separator} ${siteName}"/>
                  </c:when>
                  <c:when test="${status.index == 2}">
                    <fmt:message key="common.ellipsis" var="sites">
                      <fmt:param>${sites}</fmt:param>
                    </fmt:message>
                    <c:set var="sitesHover" value="${sitesHover}${separator} ${siteName}"/>
                  </c:when>
                  <c:otherwise>
                    <c:set var="sitesHover" value="${sitesHover}${separator} ${siteName}"/>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <fmt:message var="sites" key="promotion.anySite"/>
            </c:otherwise>
          </c:choose>
        </dsp:oparam>
      </dsp:droplet>
      <json:property name="site" value="<span title=\"${fn:escapeXml(sitesHover)}\">${sites}</span>"/>
      <c:remove var="sites"/><c:remove var="sitesHover"/>
    </c:when>

    <c:when test="${field == 'global'}">
      <c:choose>
      <c:when test="${!empty state && state.agentGranted}">
        <json:property name="global" value="<span class=\"atg_commerce_csr_iconPromoGranted\" title=\"${grantedLabel}\"/>"/>
      </c:when>
      <c:when test="${!empty state && state.active}">
        <json:property name="global" value="<span class=\"atg_commerce_csr_iconPromoGranted\" title=\"${grantedLabel}\"/>"/>
      </c:when>
      <c:when test="${!empty state}">
        <json:property name="global" value=""/>
      </c:when>
      <c:otherwise>
        <dsp:droplet name="ProfileHasPromotionDroplet">
          <dsp:param name="customerProfile" value="${activeCustomerProfile}"/>
          <dsp:param name="promotionId" value="${promotion.id}"/>
          <dsp:oparam name="true">
            <json:property name="global" value="<span class=\"atg_commerce_csr_iconPromoGranted\" title=\"${grantedLabel}\"/>"/>
          </dsp:oparam>
          <dsp:oparam name="false">
            <json:property name="global" value=""/>
          </dsp:oparam>
        </dsp:droplet>
      </c:otherwise>
      </c:choose>
    </c:when>

    <c:when test="${field == 'beginUsable'}">
      <c:choose>
        <c:when test="${not empty promotion.beginUsable}">
          <web-ui:formatDate type="date" value="${promotion.beginUsable}" dateStyle="short" var="beginUsable" />
          <json:property name="beginUsable" value="${beginUsable}"/>
        </c:when>
        <c:otherwise>
	      <json:property name="beginUsable" value=""/>
        </c:otherwise>
      </c:choose>
    </c:when>

    <c:when test="${field == 'endUsable'}">
      <c:choose>
        <c:when test="${not empty promotion.endUsable}">
          <web-ui:formatDate type="date" value="${promotion.endUsable}" dateStyle="short" var="endUsable" />
          <json:property name="endUsable" value="${endUsable}"/>
        </c:when>
        <c:otherwise>
	      <json:property name="endUsable" value=""/>
        </c:otherwise>
      </c:choose>
    </c:when>

    <c:when test="${field == 'ignore'}">
    
      <c:set var="accessible" value="false"/>
    
      <dsp:droplet name="IsPromotionAccessibleDroplet">
        <dsp:param name="promotion" value="${promotion}"/>
        <dsp:oparam name="true">
          <c:set var="accessible" value="true"/>
        </dsp:oparam>
      </dsp:droplet>
    
      <c:choose>
      <c:when test="${(!empty state && !state.usable) || !accessible}">
        <json:property name="ignore" value="<input type=\"checkbox\" id=\"checkbox${state.id}\" class=\"${promotion.id}\" disabled=\"disabled\"/>"/>
      </c:when>
      <c:otherwise>
        <c:set var="checked" value=""/>
        <c:if test="${state.ignored}"><c:set var="checked" value="checked=\"true\""/></c:if>
        <json:property name="ignore" value="<input type=\"checkbox\" id=\"checkbox${state.id}\" class=\"${promotion.id}\" ${checked} onclick=\"javascript:atg.commerce.csr.promotion.checkPromotion('${promotion.id}', this.checked);\"/>"/>
      </c:otherwise>
      </c:choose>
    </c:when>

    <c:when test="${field == 'discountAmount'}">
      <c:choose>
      <c:when test="${!empty state && state.applied}">
        <csr:formatNumber var="discountAmount" value="${state.discount}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
        <json:property name="discountAmount" value="${discountAmount}"/>
        <c:remove var="discountAmount"/>
      </c:when>
      <c:when test="${!empty state && state.considered}">
        <json:property name="discountAmount" value="<b>*</b>"/>
      </c:when>
      <c:otherwise>
        <json:property name="discountAmount" value=" "/>
      </c:otherwise>
      </c:choose>
    </c:when>

    <c:when test="${field == 'add'}">
      <c:set var="isExpired" value="${false}"/>
      <dsp:droplet name="/atg/commerce/custsvc/promotion/IsExpiredPromotion">
        <dsp:param name="promotionId" value="${promotion.id}"/>
        <dsp:oparam name="true">
          <c:set var="isExpired" value="${true}"/>
        </dsp:oparam>
      </dsp:droplet>
      <c:choose>
      <c:when test="${!promotion.global && !isExpired}">
        <json:property name="add" value="<a href=\"#\" class=\"blueU\" onclick=\"javascript:atg.commerce.csr.promotion.grantPromotion('${promotion.id}',${walletGridConfig.gridWidgetId}_refreshSearchResults);\">${grantLink}</a>"/>
      </c:when>
      <c:otherwise>
        <json:property name="add" value=""/>
      </c:otherwise>
      </c:choose>
      <c:remove var="isExpired"/>
    </c:when>

    <c:when test="${field == 'remove'}">
      <c:choose>
      <c:when test="${state.agentGranted}">
        <json:property name="remove" value="<a href=\"#\" onclick=\"javascript:atg.commerce.csr.promotion.removePromotion('${state.id}',${walletGridConfig.gridWidgetId}_refreshSearchResults);\" class=\"atg_commerce_csr_propertyClear\" title=\"${removeLink}\">${removeLink}</a>"/>
      </c:when>
      <c:otherwise>
        <json:property name="remove" value=""/>
      </c:otherwise>
      </c:choose>
    </c:when>

    <c:otherwise>
    </c:otherwise>
    </c:choose>
  </dsp:layeredBundle>
</dsp:page>
<%-- Version: $Change: 1179550 $$DateTime: 2015/07/10 11:58:13 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/columnRenderer.jsp#3 $$Change: 1179550 $--%>