<%--

This file is for prompting to restore a ticket.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/archivePrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:setLayeredBundle basename="atg.svc.agent.WebAppResources" />
  <div class="popupwindow">
    <form name="merge">
      <div align="center">
        <table class="modal" id="archivePromptTable">
          <tr>
            <td class="center">
              The Ticket you are attempting to access has been archived.
              <br />
              Would you like to request that it be restored?
              <br />
              <fmt:message var="yes" key="text.OK" />
              <fmt:message var="no" key="text.no" />
              <input id="archiveOk" type="submit" class="buttonFixed" value="<c:out value='${yes}' />" onclick="requestTicketRestore(); return false;" />
              <input id="archiveCancel" type="submit" class="buttonFixed" value="<c:out value='${no}' />" onclick="return false;" />
            </td>
          </tr>
        </table>
      </div>
    </form>
  </div>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/archivePrompt.jsp#1 $$Change: 946917 $--%>
