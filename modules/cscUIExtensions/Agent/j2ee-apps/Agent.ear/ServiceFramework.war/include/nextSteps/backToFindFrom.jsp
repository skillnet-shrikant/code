<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:importbean bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler"/>
  <dspel:form style="display:none" formid="backToFind" id="backToFind" action="#" >
    <dspel:input name="transform" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.transform" priority="-10"/>
    <dspel:input name="errorURL" type="hidden" value="/error.jsp?${stateHolder.windowIdParameterName}=${windowId}" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.errorURL"/>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/nextSteps/backToFindFrom.jsp#1 $$Change: 946917 $--%>
