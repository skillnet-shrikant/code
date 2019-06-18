<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/include/top.jspf" %>   
<dspel:page >
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <caf:outputJavaScript>
      alert("<fmt:message key='window.deprecated'/>");
      window.onbeforeunload=null;
      window.location="/agent/main.jsp";
      </caf:outputJavaScript>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/login-redir-ajax.jsp#1 $$Change: 946917 $--%>
