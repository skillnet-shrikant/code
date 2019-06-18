<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">

<%--
    Login page for Agent application.
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/accessDenied.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<dspel:importbean var="profileErrorMessageForEach"
  bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dspel:importbean var="switch"
  bean="/atg/dynamo/droplet/Switch"/>

<dspel:importbean bean="/atg/svc/repository/service/StateHolderService"
  var="stateHolderService"/>
<c:set value="${stateHolderService.newWindowId}"
       var="windowId"/>

<dspel:setvalue bean="AgentProfileFormHandler.value.password" value="" />

  <dspel:setLayeredBundle basename="atg.svc.agent.WebAppResources" />
  <html>
    <head>
      <meta http-equiv="pragma" content="no-cache"></meta>
      <title>
        <fmt:message key="appName" />
      </title>
      
      <script type="text/javascript">
        //<![CDATA[
        // having this in the url will cause unexpected behavior and needs to be removed 
        // it will be there if use logs out from UE header
        
        var url = document.location.href;
        removeParam ="%26DPSLogout%3Dtrue";
        if (url.indexOf(removeParam) == -1 ){ 
          removeParam ="DPSLogout%3Dtrue";
        }
        if (url.indexOf(removeParam) > -1 ){   
          urlA = url.substr(0,url.indexOf(removeParam));
          afterNum = ( url.indexOf( "%26_requestid%3D" ) < 0 ) ?  url.length : url.indexOf( "%26_requestid%3D" );
          urlB = (afterNum-url.indexOf(removeParam) == removeParam.length ) ? 
            urlA :
            urlA+url.substr(url.indexOf(removeParam)+removeParam.length );
          document.location.replace(urlB);
        }

        var domLevel;
        var found = false;
        while(domLevel != null && !found) {
          domLevel = domLevel.parent;
          if(domLevel != null)
            found = (domLevel.getElementById("content") != null);
        }
        if(domLevel != null && found) {
          domLevel.location = document.location;
        }
        
        function setFocus() {
          getElt("logoutYes").focus();
        }
        //]]>
      </script>
      <style type="text/css">
        html, body, #loginBody {
          height: 100%;
          margin: 0px auto;
          padding: 0px;
          border: none;
          text-align: center;
          background: url(<c:out value="${imageLocation}" />/bg/bg_body.gif) repeat-x left top;
          font-size: 69%;
          font-size: 1.0em;
          font-family: Arial,Helvetica,Sans-serif;
        }

        div#centered {
          border: 0;
          position: absolute;
          left: 25%;
          top: 15%;
          width: 503px;
          height: 379px;
          background: url(<c:out value="${imageLocation}" />/bg/service_center_login.png);
          background-repeat: no-repeat;
          background-position: top left;
        }
        table.wh100p{
          width: 100%;
          height: 100%;
        }
        form {
          margin: 0px;
          padding: 0px;
        }
        td.loginBottom{
          vertical-align: middle;
          padding: 17px;
        }
        td.loginBottom table{
          margin-right: 2px;
        }
        .loginLabel{
          font-size: .75em;
          color: #B8E2FC;
          vertical-align: middle;
        }

        .loginHeader {
          font-size: 1.3em;
          font-weight: bold;
          color: #EB910F;
          position: relative; 
          float: left; 
          top:-5px;
          left: 19px;
          padding: 0px;
          margin:-15px;
        }
        input.loginTxtField{
          border: 1px solid #272727;
          width: 142px;
          filter: none;
          background-color: white;
        }
        .error{
          color: #cc0000;
          font-weight: 600;
          font-size: .9em;
        }
        .buttonSmall{
          display: inline-block !important;
          padding: 2px 5px 2px 5px !important;
          background: url(<c:out value="${imageLocation}" />/bg/bg_button.gif) #ffffff repeat-x left bottom !important;
          border: 1px solid !important;
          border-color: #c2c6ca #a1a4a7 #a1a4a7 #c2c6ca !important;
          font: small-caption !important;
          font-weight: 600 !important;
          text-decoration: none !important;
          text-align: center !important;
          white-space: nowrap;
          cursor: pointer; cursor: hand;
          color: black;
        }
        .go span{
          display: inline-block;
          margin: 0px;
          padding: 0px 12px 0px 2px;
          width: 2px;
          height: 4px;
          background: url(<c:out value="${imageLocation}" />/icons/icon_go.gif) no-repeat right 4px;
        }
        .padRight10{
          padding-right: 15px;
          text-align: right;
          padding-top: 4px;
          padding-bottom: 8px;
        }
      </style>
    </head>
    <body onload="//setFocus();">
      <div id="centered"> 
        <table border="0" class="wh100p"> 
          <tr> 
            <td class="loginBottom">
              <table align="center"> 
                <tr> 
                  <td valign="top">
                    <dspel:form action="${thisPage}?${stateHolder.windowIdParameterName}=${windowId}" method="post" name="loginForm"> 
                      <dspel:input bean="AgentProfileFormHandler.loginErrorURL" type="hidden" value="${thisPage}?${stateHolder.windowIdParameterName}=${windowId}"/>
                      <dspel:input bean="AgentProfileFormHandler.loginSuccessURL" type="hidden" value="main.jsp?${stateHolder.windowIdParameterName}=${windowId}" />

                      <table class="wh100p loginTable" border="0"> 
                        <tr> 
                          <td class="error center">
                            <br/><br/><br/><br/>
                            <fmt:message key="accessDenied.header" />
                            <br/><br/>
                            <fmt:message key="accessDenied.text" />
                          </td>
                        </tr>
                        <tr> 
                          <td class="center"> 
                            <br/><br/><br/><br/>
                            <fmt:message  var="logoutButtonLabel" key="text.logout"/>
                            <dspel:input type="submit" iclass="buttonSmall go" id="logoutYes" value="${logoutButtonLabel}" bean="AgentProfileFormHandler.logout" />
                          </td> 
                        </tr>
                      </table> 
                    </dspel:form> 
                  </td> 
                </tr> 
              </table> 
            </td> 
          </tr> 
        </table> 
      </div> 
    </body>
  </html>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/accessDenied.jsp#1 $$Change: 946917 $--%>