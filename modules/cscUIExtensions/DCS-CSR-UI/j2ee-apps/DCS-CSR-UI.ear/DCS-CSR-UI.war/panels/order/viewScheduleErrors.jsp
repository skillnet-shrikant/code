<%@  include file="/include/top.jspf"%>
<%--
This page displays the scheduled order errors popup dialog

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/viewScheduleErrors.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<div class="atg_csr_scheduleOrderErrorPane">
<c:catch var="exception">
  <dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:importbean bean="/atg/commerce/custsvc/order/scheduled/CSRScheduledOrderInfo"/>
  <fmt:message key='scheduleFailedDescription'/><br>
  <fmt:message key='scheduleFailedDescriptionNote'/>
  <span class="atg_commerce_csr_dataHighlight">
  <ul class="atg_commerce_csr_error atg_commerce_csr_ScheduleOrderError">
  <dsp:droplet name="CSRScheduledOrderInfo">
  <dsp:param name="itemId" param="scheduledOrderId"/>
  <dsp:oparam name="output">
    <dsp:tomap var="scheduledItem" param="scheduledOrderItem"/>
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
    <dsp:param name="array" param="scheduledOrderItem.lastError.errorMessages"/>
    <dsp:oparam name="output">
    <li>
    <dsp:valueof param="element"/>
    </li>
    </dsp:oparam>
    </dsp:droplet>
   
  </dsp:oparam>
  </dsp:droplet>
  
  <input value="<fmt:message key='scheduleFailedOkButton'/>" 
    type="button" id="okChoice"
    onClick="atg.commerce.csr.common.hidePopupWithReturn('okChoice', {result:'ok'}); 
      return false;"/>

  </dsp:layeredBundle>
  </dsp:page>
  </ul>
  </span>
 </c:catch>
</div>
<c:if test="${exception != null}">
  ${exception}
  <% 
    Exception ee = (Exception) pageContext.getAttribute("exception"); 
    ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/viewScheduleErrors.jsp#1 $$Change: 946917 $--%>
