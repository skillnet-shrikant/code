<%--

This file is for prompting to restore a ticket.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/uploadingPrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%@  include file="/include/top.jspf" %>
<dspel:setLayeredBundle basename="atg.svc.agent.WebAppResources" />

<dspel:page xml="true">
  
  <div id="uploadingPrompt_div" class="modal" style="display:none;">
    <form name="uploadPrompt_form">
      <div align="center">
        <table class="modal" id="uploadingPrompt_table">
          <tr>
            <td class="center">
              <div class="loadingIcon"></div>&nbsp;
              <span>
                <fmt:message key="response.message.uploading.label"/> <br/>
                <fmt:message key="pleaseWait.label" />
                <fmt:message key="text.ellipsis"/>
              </span>&nbsp;
              <br />              
            </td>
          </tr>
        </table>
      </div>
    </form>
  </div>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/uploadingPrompt.jsp#1 $$Change: 946917 $--%>
