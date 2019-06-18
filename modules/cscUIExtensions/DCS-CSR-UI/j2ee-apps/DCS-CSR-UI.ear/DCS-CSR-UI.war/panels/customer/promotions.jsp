<%--
 This page defines the Customer Promotions Panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/promotions.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <%-- Begin security check
    -- Commented out until promotion browser is implemented --
    <dsp:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
    <dsp:droplet name="HasAccessRight">
      <dsp:param name="accessRight" value="commerce-custsvc-browse-promotions-privilege"/>
      <dsp:oparam name="accessGranted">
        <c:set var="browsePromosPriv" value="true"/>
      </dsp:oparam>
      <dsp:oparam name="accessDenied">
        <c:set var="browsePromosPriv" value="false"/>
      </dsp:oparam>
    </dsp:droplet>
    End security check --%>
    
    
    <div id="atg_commerce_csr_customerinfo_promotions_subPanel" class="atg_svc_subPanel">
      <div class="atg_svc_subPanelHeader" >       
          <ul class="atg_svc_panelToolBar">
            <li class="atg_svc_header">
              <h4 id="atg_commerce_csr_customerinfo_promotions"><fmt:message key="customer.promotions.title.label"/> </h4>
            </li>
             <%--Commented out for this release. TODO: uncomment when Promotion Browser feature is implemented
              <c:if test="${browsePromosPriv}">            
                  <li class="atg_svc_last">
                    <a href="#" onclick="alert('This feature is not yet available for selection');return false;" class="atg_svc_popupLink">Promotion Browser</a>
                  </li>            
              </c:if>
              --%>
           </ul>
        </div>
    

    <dsp:getvalueof var="activePromotions" bean="/atg/userprofiling/ServiceCustomerProfile.activePromotions"/>
    <dsp:getvalueof value="${fn:length(activePromotions)}" var="promotionCount"/>
    <c:choose>
      <c:when test="${promotionCount == 0}" >

        <div class="emptyLabel">
          <fmt:message key="customer.promotions.noPromotions.label"/>
        </div>

      </c:when>
      <c:otherwise>

        <div class="promotions">
          <table class="atg_dataTable">
            <thead>
              <tr class="atg_currentRow">
                <td><fmt:message key="customer.promotions.name"/></td>
                <td><fmt:message key="customer.promotions.startDate"/></td>
                <td><fmt:message key="customer.promotions.endDate"/></td>
                <td><fmt:message key="customer.promotions.uses"/></td>
              </tr>
            </thead>
            <tbody>

              <c:forEach items="${activePromotions}" var="promotionStatus">
                <dsp:tomap var="promotionStatusMap" value="${promotionStatus}"/>
                <dsp:tomap var="promotionMap" value="${promotionStatusMap.promotion}"/>
                <tr class="atg_altRow">
                  <td>
                    <dsp:valueof value="${promotionMap.displayName}"/>
                  </td>
                  <td>
                    <dsp:valueof date="MMM d, yyyy" value="${promotionMap.startDate}">
                      <fmt:message key="customer.promotions.dateNotAvailable"/>
                    </dsp:valueof>
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${!empty promotionStatusMap.expirationDate}" >
                        <dsp:valueof date="MMM d, yyyy" value="${promotionStatusMap.expirationDate}">
                          <fmt:message key="customer.promotions.dateNotAvailable"/>
                        </dsp:valueof>
                      </c:when>
                      <c:otherwise>
                        <dsp:valueof date="MMM d, yyyy" value="${promotionMap.endDate}">
                          <fmt:message key="customer.promotions.dateNotAvailable"/>
                        </dsp:valueof>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <dsp:getvalueof value="${promotionStatusMap.numUses}" var="numUses"/>
                    <dsp:getvalueof value="${promotionMap.uses}" var="uses"/>
                    <c:choose>
                      <c:when test="${numUses eq -1 and uses eq -1}">
                        <fmt:message key="customer.promotions.uses.unlimited"/>
                      </c:when>
                      <c:otherwise>
                        <c:out value="${numUses}"/><fmt:message key="text.slash"/><c:out value="${uses}"/>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:forEach>

            </tbody>
          </table>
        </div>
      </c:otherwise>
    </c:choose>
    </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/promotions.jsp#1 $$Change: 946917 $--%>
