<%--

This JSP is for displaying the information about all the recnlty 
viewed and worked by the logged in user

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/sideViewRecentTicketsPanel.jsp#3 $$Change: 1179550 $

@updated $DateTime: 2015/07/10 11:58:13 $$Author:

--%>

<%@ include file="/include/top.jspf" %>
<%@ page import="java.util.*" %>
<%@ page import="atg.svc.ui.formhandlers.RecentTicketsFormHandler" %>
<%@ page import="atg.svc.ticketing.TicketDetails" %>
<%@ page import="atg.svc.repository.beans.RecentTickets" %>


<dspel:page xml="true">
 <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <dspel:importbean scope="request" var="recentTicketsBean"
                    bean="/atg/svc/ui/formhandlers/RecentTicketsFormHandler" />
  <dspel:form style="display:none" id="viewRecentTicketsForm" action="#" formid="viewRecentTicketsForm">
    <dspel:input name="viewRecentTickets" priority="-10" type="hidden" value="" bean="/atg/svc/ui/formhandlers/RecentTicketsFormHandler.viewRecentTickets"/>
    <dspel:input name="type" type="hidden" value="refresh" bean="/atg/svc/ui/formhandlers/RecentTicketsFormHandler.type"/>
  </dspel:form>

  <caf:size var="recentTicketsCount" collection="${recentTicketsBean.recentTickets}" />                    

  <svc-ui:getOptionAsString var="maxRecentTickets" optionName="RecentTicketsMaximum" />
  
  <%-- checking for maximum number of tickets to be shown --%>  
  <c:choose>  
    <c:when test="${recentTicketsCount ge maxRecentTickets}">
      <c:set var="totRec" value="${maxRecentTickets}" />
    </c:when>
    <c:otherwise>
      <c:set var="totRec" value="${recentTicketsCount}" />
    </c:otherwise>
  </c:choose>  
    <c:if test="${totRec lt 0}">
     <c:set var="totRec" value="0" />
  </c:if>
  <%-- checking for ticket type --%>
   <c:choose>
     <c:when test="${empty recentTicketsBean.type}">
      <c:set var="ticketType" value="worked" />
     </c:when>
     <c:otherwise>
      <c:set var="ticketType" value="${recentTicketsBean.type}" />
     </c:otherwise>
   </c:choose>
  <%-- Displaying ticket menu (worked or viewed) --%>
  <table class="ticketType atg_next_steps_view_recent_tickets_type" cellpadding="0" cellspacing="0" align="center" parseWidgets="false">
   <tr>
      
      <td valign="top" nowrap="true"> 
         <c:choose>
           <c:when test="${ticketType =='viewed'}">
              <a href="#" 
                  class="atg_navigationHighlight ticketingOptionsLink" 
                  id="recentTicketsWorked"
                  onClick="openWorkedTickets();">         
                  <fmt:message key="sidePanel.recentTickets.worked.label"/>
              </a>
           </c:when>
           <c:otherwise>
              <fmt:message key="sidePanel.recentTickets.worked.label"/>
           </c:otherwise>
         </c:choose>
       </td>
       <td nowrap="true" class="inputTextLabel atg_next_steps_view_recent_tickets_input">
           <fmt:message key="text.separator.vertical"/>
       </td>  
       <td align="left" valign="top" nowrap="true"> 
         <c:choose>
           <c:when test="${ticketType =='worked'}">       
              <a href="#" 
                 class="atg_navigationHighlight ticketingOptionsLink" 
                 id="recentTicketsViewed"
                 onClick="openViewedTickets();">         
                <fmt:message key="sidePanel.recentTickets.viewed.label"/>
              </a>  
           </c:when>
           <c:otherwise>
              <fmt:message key="sidePanel.recentTickets.viewed.label"/>
           </c:otherwise>
         </c:choose>   
      </td>
    </tr>
    </table>
    <%-- displaying ticket details --%>
     <c:choose>
          <c:when test="${totRec == 0}">
                <c:choose>
                 <c:when test="${ticketType =='viewed'}">
                    <fmt:message var="noRecentTickets" key="sidePanel.recentTickets.viewed.none" />
                 </c:when>
                 <c:otherwise>
                   <fmt:message var="noRecentTickets" key="sidePanel.recentTickets.worked.none" />
                 </c:otherwise>
                </c:choose> 
              <table class="layoutTable atg_next_steps_view_recent_tickets_details">    
              <tr>
                <td >
                    <c:out value="${noRecentTickets}" />                
              </td>
            </tr>
           </table>                
          </c:when>
          <c:otherwise>
           <table class="layoutTable atg_next_steps_view_recent_tickets_details">
            <c:forEach var="var_recent_tickets" items="${recentTicketsBean.recentTickets}" begin="0" end="${totRec -1}" step="1">
            
            <%
              RecentTicketsFormHandler recentTicketHandler =null;
              TicketDetails ticketDetails =null;
              String description ="";
              try{
                 recentTicketHandler = (RecentTicketsFormHandler)request.getAttribute("recentTicketsBean");
                 String ticketId = ((RecentTickets)pageContext.getAttribute("var_recent_tickets")).getTicketId();
                 ticketDetails = recentTicketHandler.getTicketDetails(ticketId);
                                
                 
                }catch(Exception e){}
               pageContext.setAttribute("ticketDetails", ticketDetails);
               if(ticketDetails !=null)
                {
                  description =ticketDetails.getDescription();
                  if(description!= null && description.length() >25) 
                    description= description.substring(0,24);
                }
                if(description!=null)
                   pageContext.setAttribute("description", description);                  
                else  
                   pageContext.setAttribute("description", ""); 
            %>
            
              <tr>
	               <td width="20%">
                    <a href="#" 
                      class="atg_navigationHighlight nextStepsLink" 
                      id="recentTickets"
                      onClick="openByIdTicket('<c:out value="${var_recent_tickets.ticketId}" />');">       
                      <c:out value="${var_recent_tickets.ticketId}" /> 
                    </a>        
           
                  </td>       
                  <td>
                   <c:out value="${description}" escapeXml="false" />
                 </td>   
                  <c:choose>
                   <c:when test="${ticketDetails.statusName == 'Closed'}">
       	             <td width="30%" class="ticketingClosedStatus atg_next_steps_view_recent_tickets_status">
       		          
                    </c:when>
                    <c:otherwise>
       	              <td width="30%" class="ticketingOpenStatus atg_next_steps_view_recent_tickets_status">
       		          
                    </c:otherwise>
                   </c:choose>
                   <dspel:droplet name="/atg/ticketing/TicketStatusDescription">
                    <dspel:param name="descriptionId" value="${ticketDetails.statusName}"/>
                    <dspel:param name="baseName" value="STATUS"/>
                    <dspel:param name="elementName" value="transDescription"/>
                    <dspel:oparam name="output">
                      <dspel:getvalueof var="transDescription" param="transDescription"/>
                      <c:out value="${transDescription}"/>
                    </dspel:oparam>
                    </dspel:droplet>
                    </td>
                </tr>
             </c:forEach> 
           </table>
         </c:otherwise>               
      </c:choose>    
  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/sideViewRecentTicketsPanel.jsp#3 $ $Change: 1179550 $ $DateTime: 2015/07/10 11:58:13 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/sideViewRecentTicketsPanel.jsp#3 $$Change: 1179550 $--%>
