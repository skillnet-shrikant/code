<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/include/top.jspf" %>

<dspel:setLayeredBundle basename="atg.svc.agent.WebAppResources" />

<dspel:page xml="true">
<dspel:importbean bean="/atg/dynamo/service/VersionService" var="version"/>
<html style="overflow-y:auto">
<head>
  <title>
    <fmt:message key="about.title">
      <fmt:param><fmt:message key="about.product.name.full"/></fmt:param>
    </fmt:message>
  </title>
  <link href="<c:out value='${cssPath}'/>/workspace-sprite.css" rel="stylesheet" type="text/css" />
</head>
<body class="popup">
<div id="assetBrowserHeader">
  <h2><fmt:message key="about.product.name"/></h2>
</div>
<div id="nonTableContent">
  <table border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td><p><c:out value="${version.fullVersionString}"/></p>
        <fmt:message key="about.text"/>
      </td>
    </tr>
  </table>
</div>
</body>
</html>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/about.jsp#1 $$Change: 946917 $--%>
