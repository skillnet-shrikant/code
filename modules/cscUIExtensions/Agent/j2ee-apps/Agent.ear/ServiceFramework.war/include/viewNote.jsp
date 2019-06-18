<%@ include file="/include/top.jspf" %>
<% pageContext.setAttribute("newLine", "\n"); %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources"> 
  <dspel:importbean bean="/atg/dynamo/droplet/ForEach"/>
  
  <dspel:getvalueof var="notes" param="notes"/>
  <dspel:getvalueof var="onClickURL" param="popupURL"/>
  <dspel:getvalueof var="emptyMessage" param="emptyMessage"/>
   
   <div id="atg_service_customerinfo_notes_subPanel" class="atg_svc_subPanel">
     <div class="atg_svc_subPanelHeader" >       
         <ul class="atg_svc_panelToolBar">
           <li class="atg_svc_header">
             <h4 id="atg_svc_customerinfo_notes"><fmt:message key="customer.notes.title.label"/> </h4>
           </li>
           <dspel:droplet name="/atg/dynamo/droplet/Switch">
             <dspel:param param="mode" name="value"/>
             <dspel:oparam name="edit">
               <li class="atg_svc_last">          
                 <a href="#"
                   class="atg_svc_popupLink"
                   onClick="<c:out value='${onClickURL}'/>">
                   <fmt:message key="customer.notes.addNote.label"/>
                 </a>
              </li>
            </dspel:oparam>
            </dspel:droplet>           
          </ul>
       </div>
   
  <c:set var="count" value="0"/>
  <table style="table-layout:fixed;">
    <dspel:droplet name="ForEach">
      <dspel:param value="${notes}" name="array"/>
      <dspel:param value="+creationDate" name="sortProperties"/>
        <dspel:setvalue param="note" paramvalue="element"/>
          <dspel:oparam name="empty">
            <tr><td style="width:80%">
            <div class="emptyLabel">
              <c:out value='${emptyMessage}'/>
            </div>
          </td></tr>
          </dspel:oparam>

          <dspel:oparam name="output">
            <dspel:getvalueof param="note.comment" var="comment"/>
            <c:set var="count" value="${count + 1}"/>
            <c:set var="customerNoteDiv" value="atg_commerce_csr_customerNoteDiv${count}"/>
            <c:set var="customerFullNoteDiv" value="atg_commerce_csr_customerFullNoteDiv${count}"/>
            <tr>
              <td valign="top" width="12">
                <c:if test="${fn:length(comment) > 256}">
                  <a href="#" id="atg_commerce_csr_customerNote${count}"
                    class="customerInfoSectionClosed"
                    style="display:inline; text-decoration:none;"
                    onclick="toggleNoteComment('atg_commerce_csr_customerNote${count}', '${customerNoteDiv}', '${customerFullNoteDiv}', 'customerInfoSectionOpen', 'customerInfoSectionClosed');return false;">&nbsp;</a>
                </c:if>
              </td>
              <td valign="top" width="100">
              	<dspel:getvalueof param="note.creationDate" var="creationDate"/>
                <web-ui:formatDate value="${creationDate}" type="both" dateStyle="short" timeStyle="short"/>
              </td>
              <td class="notes">
                <c:choose>
                  <c:when test="${fn:length(comment) > 256}">
                    <span id="${customerNoteDiv}" style="word-wrap:break-word;">
                      ${fn:replace(fn:substring(comment, 0, 256), newLine, '<br>')}
                      <fmt:message key="text.ellipsis"/>
                    </span>
                    <span id="${customerFullNoteDiv}" style="word-wrap:break-word; display:none;">
                      ${fn:replace(comment, newLine, '<br>')}
                    </span>
                  </c:when>
                  <c:otherwise>
                    <span id="${customerNoteDiv}" style="word-wrap:break-word;">
                      ${fn:replace(comment, newLine, '<br>')}
                    </span>
                  </c:otherwise>
                </c:choose>
              </td>
          </tr>
          <tr>
            <td colspan="2">
              <br />
            </td>
          </tr>
        </dspel:oparam>
      </dspel:droplet>
    </table>
    <script type="text/javascript">
      // wrap long lines
      var i=1;
      while (document.getElementById("atg_commerce_csr_customerNoteDiv" + i)) {
        var preEl = document.getElementById("atg_commerce_csr_customerNoteDiv" + i);
        preEl.innerHTML = preEl.innerHTML.replace(/([^\s]{10})(?=[^\s]{2,})/g,"$1<wbr/>");
        preEl = document.getElementById("atg_commerce_csr_customerFullNoteDiv" + i);
        if(preEl){
          preEl.innerHTML = preEl.innerHTML.replace(/([^\s]{10})(?=[^\s]{2,})/g,"$1<wbr/>");
        }
        i = i + 1;
      }
    </script>
    </div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/viewNote.jsp#1 $$Change: 946917 $--%>
