<%--
  Side panel find by id
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/sideFindByIdPanel.jsp#1 $$Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>

<dspel:page xml="true">
<div class="atg_next_steps_find_by_id_panel">
  <div class="layoutTable">
    <dspel:importbean var="contentFragments" bean="/atg/svc/agent/ui/sidepanels/SideFindByIdPanelContent"/>
    <c:forEach var="pageFragment" items="${contentFragments.contentFragmentsByPriority}">
       <dspel:include page="${pageFragment.fragment}" otherContext="${pageFragment.context}"/>
     </c:forEach>
  </div>
</div>
</dspel:page>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/sideFindByIdPanel.jsp#1 $$Change: 946917 $--%>
