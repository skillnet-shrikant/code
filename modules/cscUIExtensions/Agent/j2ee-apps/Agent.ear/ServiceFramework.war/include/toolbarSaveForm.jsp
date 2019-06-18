<%--
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/toolbarSaveForm.jsp#1 $ $Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $ $Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:importbean bean="/atg/svc/ui/formhandlers/ToolbarFormHandler"/>
<dspel:form style="display:none" id="findEditQueriesPanel_save" formid="findEditQueriesPanel_save" action="#">
  <dspel:input priority="-10" type="hidden" value="" bean="ToolbarFormHandler.save"/>
  <dspel:input type="hidden" name="queryDesc" bean="ToolbarFormHandler.queryDesc" value="" />
  <dspel:input type="hidden" name="queryCompositeId" bean="ToolbarFormHandler.queryCompositeId" value="" />
  <dspel:input type="hidden" name="queryToSelect" bean="ToolbarFormHandler.queryToSelect" value="" />
  <dspel:input type="hidden" name="publishedGroups" bean="ToolbarFormHandler.publishedGroups" value="" />
  <dspel:input type="hidden" name="statuses" bean="ToolbarFormHandler.statuses" value="" />
  <dspel:input type="hidden" name="statusesOp" bean="ToolbarFormHandler.statusesOp" value="" />
  <dspel:input type="hidden" name="useStatusConstraint" bean="ToolbarFormHandler.useStatusConstraint" value="" />
  <dspel:input type="hidden" name="creationDateTo" bean="ToolbarFormHandler.creationDateTo" value="" />
  <dspel:input type="hidden" name="creationDateFrom" bean="ToolbarFormHandler.creationDateFrom" value="" />
  <dspel:input type="hidden" name="listCreationRange" bean="ToolbarFormHandler.listCreationRange" value="" />
  <dspel:input type="hidden" name="creationDateType" bean="ToolbarFormHandler.creationDateType" value="" />
  <dspel:input type="hidden" name="useCreationDateConstraint" bean="ToolbarFormHandler.useCreationDateConstraint" value="" />
  <dspel:input type="hidden" name="lastModifiedDateFrom" bean="ToolbarFormHandler.lastModifiedDateFrom" value="" />
  <dspel:input type="hidden" name="lastModifiedDateTo" bean="ToolbarFormHandler.lastModifiedDateTo" value="" />
  <dspel:input type="hidden" name="listLastModifiedRange" bean="ToolbarFormHandler.listLastModifiedRange" value="" />
  <dspel:input type="hidden" name="lastModifiedDateType" bean="ToolbarFormHandler.lastModifiedDateType" value="" />
  <dspel:input type="hidden" name="useLastModifiedDateConstraint" bean="ToolbarFormHandler.useLastModifiedDateConstraint" value="" />
  <dspel:input type="hidden" name="author" bean="ToolbarFormHandler.author" value="" />
  <dspel:input type="hidden" name="authorOp" bean="ToolbarFormHandler.authorOp" value="" />
  <dspel:input type="hidden" name="useAuthorConstraint" bean="ToolbarFormHandler.useAuthorConstraint" value="" />
  <dspel:input type="hidden" name="modifiedBy" bean="ToolbarFormHandler.modifiedBy" value="" />
  <dspel:input type="hidden" name="modifiedByOp" bean="ToolbarFormHandler.modifiedByOp" value="" />
  <dspel:input type="hidden" name="useModifiedByConstraint" bean="ToolbarFormHandler.useModifiedByConstraint" value="" />
  <dspel:input type="hidden" name="useCountRelation" bean="ToolbarFormHandler.useCountRelation" value="" />
  <dspel:input type="hidden" name="useCountValue" bean="ToolbarFormHandler.useCountValue" value="" />
  <dspel:input type="hidden" name="useCountValue2" bean="ToolbarFormHandler.useCountValue2" value="" />
  <dspel:input type="hidden" name="useUseCountConstraint" bean="ToolbarFormHandler.useUseCountConstraint" value="" />
  <dspel:input type="hidden" name="viewCountRelation" bean="ToolbarFormHandler.viewCountRelation" value="" />
  <dspel:input type="hidden" name="viewCountValue" bean="ToolbarFormHandler.viewCountValue" value="" />
  <dspel:input type="hidden" name="viewCountValue2" bean="ToolbarFormHandler.viewCountValue2" value="" />
  <dspel:input type="hidden" name="useViewCountConstraint" bean="ToolbarFormHandler.useViewCountConstraint" value="" />
  <dspel:input type="hidden" name="topics" bean="ToolbarFormHandler.topics" value="" />
  <dspel:input type="hidden" name="topicsOp" bean="ToolbarFormHandler.topicsOp" value="" />
  <dspel:input type="hidden" name="useTopicsConstraint" bean="ToolbarFormHandler.useTopicsConstraint" value="" />
  <dspel:input type="hidden" name="owningGroup" bean="ToolbarFormHandler.owningGroup" value="" />
  <dspel:input type="hidden" name="owningGroupOp" bean="ToolbarFormHandler.owningGroupOp" value="" />
  <dspel:input type="hidden" name="useOwningGroupConstraint" bean="ToolbarFormHandler.useOwningGroupConstraint" value="" />
  <dspel:input type="hidden" name="internalAudience" bean="ToolbarFormHandler.internalAudience" value="" />
  <dspel:input type="hidden" name="internalAudienceOp" bean="ToolbarFormHandler.internalAudienceOp" value="" />
  <dspel:input type="hidden" name="useInternalAudienceConstraint" bean="ToolbarFormHandler.useInternalAudienceConstraint" value="" />
  <dspel:input type="hidden" name="externalAudience" bean="ToolbarFormHandler.externalAudience" value="" />
  <dspel:input type="hidden" name="externalAudienceOp" bean="ToolbarFormHandler.externalAudienceOp" value="" />
  <dspel:input type="hidden" name="useExternalAudienceConstraint" bean="ToolbarFormHandler.useExternalAudienceConstraint" value="" />
  <dspel:input type="hidden" name="solutionLanguage" bean="ToolbarFormHandler.solutionLanguage"/>
  <dspel:input type="hidden" name="solutionLanguageOp"  bean="ToolbarFormHandler.solutionLanguageOp"/>
  <dspel:input type="hidden" name="useSolutionLanguageConstraint" bean="ToolbarFormHandler.useSolutionLanguageConstraint" value="" />
  <dspel:input type="hidden" name="bestBets" bean="ToolbarFormHandler.bestBets" value="" />
  <dspel:input type="hidden" name="useBestBetsConstraint" bean="ToolbarFormHandler.useBestBetsConstraint" value="" />
  <dspel:input type="hidden" name="title" bean="ToolbarFormHandler.title" value="" />
  <dspel:input type="hidden" name="titleMatchCase" bean="ToolbarFormHandler.titleMatchCase" value="" />
  <dspel:input type="hidden" name="titleHasValue" bean="ToolbarFormHandler.titleHasValue" value="" />
  <dspel:input type="hidden" name="titleOp" bean="ToolbarFormHandler.titleOp" value="" />
  <dspel:input type="hidden" name="useTitleConstraint" bean="ToolbarFormHandler.useTitleConstraint" value="" />
  <dspel:input type="hidden" name="keywordAllOfTheWords" bean="ToolbarFormHandler.keywordAllOfTheWords" value="" />
  <dspel:input type="hidden" name="keywordAtLeastOneOfTheWords" bean="ToolbarFormHandler.keywordAtLeastOneOfTheWords" value="" />
  <dspel:input type="hidden" name="keywordNotTheWords" bean="ToolbarFormHandler.keywordNotTheWords" value="" />
  <dspel:input type="hidden" name="useKeywordConstraint" bean="ToolbarFormHandler.useKeywordConstraint" value="" />
  <dspel:input type="hidden" name="disabledSolutionClasses" bean="ToolbarFormHandler.disabledSolutionClasses" value="" />
  <dspel:input type="hidden" name="useDisabledSolutionClassesConstraint" bean="ToolbarFormHandler.useDisabledSolutionClassesConstraint" value="" />
  <dspel:input type="hidden" name="fieldNames" bean="ToolbarFormHandler.fieldNames" value="" />
  <dspel:input type="hidden" name="fieldOperations" bean="ToolbarFormHandler.fieldOperations" value="" />
  <dspel:input type="hidden" name="fieldValues" bean="ToolbarFormHandler.fieldValues" value="" />
  <dspel:input type="hidden" name="fieldValues2" bean="ToolbarFormHandler.fieldValues2" value="" />
  <dspel:input type="hidden" name="queryBuilderMode" bean="ToolbarFormHandler.parameterMap.queryBuilderMode" value="" />
  <dspel:input type="hidden" name="refreshLowerPanel" bean="ToolbarFormHandler.parameterMap.refreshLowerPanel" value="" />
</dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/toolbarSaveForm.jsp#1 $$Change: 946917 $--%>
