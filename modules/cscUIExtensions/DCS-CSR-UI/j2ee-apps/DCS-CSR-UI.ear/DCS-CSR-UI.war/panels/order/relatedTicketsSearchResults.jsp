<%--
 This page defines the related tickets panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/relatedTicketsSearchResults.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <dsp:importbean bean="/atg/commerce/custsvc/ui/tables/ticket/RelatedTicketGrid" var="gridConfig"/>
    <dsp:importbean var="relatedTicketsFormHandler" bean="/atg/commerce/custsvc/order/RelatedTicketsTableFormHandler" scope="request" />
    <dsp:form style="display:none" id="relatedTicketsForm" formid="relatedTicketsForm">
      <dsp:input type="hidden" priority="-10" value="" bean="/atg/commerce/custsvc/order/RelatedTicketsTableFormHandler.search"/>
      <dsp:input type="hidden" name="currentPage" bean="/atg/commerce/custsvc/order/RelatedTicketsTableFormHandler.currentPage"/>
      <dsp:getvalueof param="orderItemId" var="orderRepositoryItemId"/>
      <dsp:input type="hidden" name="orderItemId" bean="/atg/commerce/custsvc/order/RelatedTicketsTableFormHandler.orderItemId" value="${orderRepositoryItemId}"/>
      <dsp:input type="hidden" name="sortProperty" bean="/atg/commerce/custsvc/order/RelatedTicketsTableFormHandler.sortField"/>
      <dsp:input type="hidden" name="sortDirection" bean="/atg/commerce/custsvc/order/RelatedTicketsTableFormHandler.sortDirection"/>
    </dsp:form> 
 
    <dsp:form style="display:none" id="orderRelatedTicketsViewTicketForm" formid="orderRelatedTicketsViewTicketForm">
      <dsp:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket"/>
      <dsp:input type="hidden" value="" name="ticketId" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
    </dsp:form>
    <dsp:form style="display:none" id="orderRelatedTicketsWorkTicketForm" formid="orderRelatedTicketsWorkTicketForm">
      <dsp:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/WorkTicket.changeEnvironment"/>
      <dsp:input type="hidden" name="ticketId" value="" bean="/atg/svc/agent/ui/formhandlers/WorkTicket.inputParameters.ticketId"/>
    </dsp:form>

    <dsp:include src="${gridConfig.gridPage.URL}" otherContext="${gridConfig.gridPage.servletContext}">
      <dsp:param name="gridConfig" value="${gridConfig}"/>
    </dsp:include>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/relatedTicketsSearchResults.jsp#1 $$Change: 946917 $--%>
