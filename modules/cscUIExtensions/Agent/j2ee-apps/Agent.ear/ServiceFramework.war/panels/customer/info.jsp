<%--
 This page defines the Customer Information Panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/info.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:getvalueof var="isProfileTransient" bean="/atg/userprofiling/ServiceCustomerProfile.transient"/>
<dspel:getvalueof var="viewprofileid" bean="/atg/userprofiling/ServiceCustomerProfile.repositoryId"/>
<dspel:getvalueof var="activeprofileid" bean="/atg/userprofiling/ActiveCustomerProfile.repositoryId"/>
<c:if test="${activeprofileid == viewprofileid}">
  <c:set var="mode" value="edit"/>
</c:if>
<c:if test="${activeprofileid != viewprofileid}">
  <c:set var="mode" value="view"/>
</c:if>

<dspel:importbean bean="/atg/svc/agent/customer/CustomerPanelConfig" var="customerPanelConfig"/>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <div class="atg_svc_coreCustomerInfo">
  <c:if test="${!isProfileTransient}">
    
    <c:forEach items="${customerPanelConfig.subSections}" var="subSection" varStatus="status">
      <c:choose>
        <c:when test="${customerPanelConfig.contextRoots[status.index] != pageContext.request.contextPath}">
        <dspel:include src="${subSection}" otherContext="${customerPanelConfig.contextRoots[status.index]}">
            <dspel:param name="mode" value="${mode}"/>
          </dspel:include>
        </c:when>
        <c:otherwise>
          <dspel:include src="${subSection}" otherContext="${UIConfig.contextRoot}">
            <dspel:param name="mode" value="${mode}"/>
          </dspel:include>
        </c:otherwise>
      </c:choose>
    </c:forEach>
    </c:if>
    <c:if test="${isProfileTransient}">
      <dspel:include src="/panels/customer/create.jsp" otherContext="${UIConfig.contextRoot}">
      </dspel:include>
    </c:if>
  </div>

  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/info.jsp#1 $$Change: 946917 $--%>
