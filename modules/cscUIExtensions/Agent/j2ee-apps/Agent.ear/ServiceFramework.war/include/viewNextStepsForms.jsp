<%--

This file is used for including DSP forms for searching.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/viewNextStepsForms.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">

<dspel:importbean scope="request"
                  var="searchFormHandler"
                  bean="/atg/svc/ui/formhandlers/FavoritesFormHandler" />

<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <dspel:form style="display:none" id="addToFavoritesForm" formid="addToFavoritesForm" action="#">
    <dspel:input id="errorURL" type="hidden" value="/error.jsp?${stateHolder.windowIdParameterName}=${windowId}" bean="/atg/svc/ui/formhandlers/FavoritesFormHandler.errorURL"/>
    <dspel:input id="addToFavorites" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FavoritesFormHandler.addToFavorites" priority="-10"/>

    <dspel:input id="solutionId" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FavoritesFormHandler.solutionId"/>
    <dspel:input id="title" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FavoritesFormHandler.title"/>
    <dspel:input id="contextId" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FavoritesFormHandler.contextId"/>
    <dspel:input id="URL" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FavoritesFormHandler.URL"/>
    <dspel:input id="type" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FavoritesFormHandler.type"/>
  </dspel:form>

  <dspel:form style="display:none" id="removeFromFavoritesForm" formid="removeFromFavoritesForm" action="#">
    <dspel:input id="errorURL" type="hidden" value="/error.jsp?${stateHolder.windowIdParameterName}=${windowId}" bean="/atg/svc/ui/formhandlers/FavoritesFormHandler.errorURL"/>
    <dspel:input id="removeFavorites" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FavoritesFormHandler.removeFavorites" priority="-10"/>
    <dspel:input id="favoritesId" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FavoritesFormHandler.favoritesId"/>
  </dspel:form>

  <dspel:form style="display:none" id="addToRecommendedReadingForm" formid="addToRecommendedReadingForm" action="#">
    <dspel:input id="errorURL" type="hidden" value="/error.jsp?${stateHolder.windowIdParameterName}=${windowId}" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.errorURL"/>
    <dspel:input id="addToRecommendedReading" type="hidden" value="" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.addToRecommendedReading" priority="-10"/>
    <dspel:input id="solutionId" type="hidden" value="" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.solutionId"/>
    <dspel:input id="title" type="hidden" value="" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.title"/>
    <dspel:input id="contextId" type="hidden" value="" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.contextId"/>
    <dspel:input id="URL" type="hidden" value="" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.URL"/>
    <dspel:input id="type" type="hidden" value="" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.type"/>
    <dspel:input id="orgIds" type="hidden" value="" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.orgIds"/>
    <dspel:input id="editorField" type="hidden" value="OrgList" bean="/atg/svc/ui/formhandlers/OrgChooserFormHandler.editorField"/>
  </dspel:form>

  <dspel:form style="display:none" id="removeFromRecommendedReadingForm" formid="removeFromRecommendedReadingForm" action="#">
    <dspel:input id="errorURL" type="hidden" value="/error.jsp?${stateHolder.windowIdParameterName}=${windowId}" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.errorURL"/>
    <dspel:input id="removeRecommendedReading" type="hidden" value="" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.removeRecommendedReading" priority="-10"/>
    <dspel:input id="recommendedReadingId" type="hidden" value="" bean="/atg/svc/ui/formhandlers/RecommendedReadingFormHandler.recommendedReadingId"/>
  </dspel:form>

  <dspel:importbean var="orgChooserFormHandler" bean="/atg/svc/ui/formhandlers/OrgChooserFormHandler" />
  <dspel:form id="recommendedReadingOrgListForm" action="#" formid="recommendedReadingOrgListForm">
    <dspel:input id="list" priority="-10" type="hidden" value="" bean="/atg/svc/ui/formhandlers/OrgChooserFormHandler.list"/>
    <dspel:input id="operation" type="hidden" value="refresh" bean="/atg/svc/ui/formhandlers/OrgChooserFormHandler.operation"/>
    <dspel:input id="treeTableId" type="hidden" value="OrgListorganizationTree" bean="/atg/svc/ui/formhandlers/OrgChooserFormHandler.treeTableId"/>
    <dspel:input id="parameters" type="hidden" value="" bean="/atg/svc/ui/formhandlers/OrgChooserFormHandler.parameters"/>

    <dspel:input id="baseValue" type="hidden" value="" bean="/atg/svc/ui/formhandlers/OrgChooserFormHandler.baseValue"/>
    <dspel:input id="baseExpValue" type="hidden" value="" bean="/atg/svc/ui/formhandlers/OrgChooserFormHandler.baseExpValue"/>
    <dspel:input id="editorField" type="hidden" value="OrgList" bean="/atg/svc/ui/formhandlers/OrgChooserFormHandler.editorField"/>
  </dspel:form>

</dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/viewNextStepsForms.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/viewNextStepsForms.jsp#1 $$Change: 946917 $--%>
