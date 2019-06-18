<%--
 This page defines the related orders panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/displaySchedulesTable.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />

<script type="text/javascript">
  if (!dijit.byId("viewScheduleErrors")) {
    new dojox.Dialog({ id: "viewScheduleErrors",
                     cacheContent: "false",
                     executeScripts: "false",
                     scriptHasHooks: "false",
                     duration: 100,
                     "class": "atg_commerce_csr_popup"});
}
</script>

<dsp:page xml="true">

<dsp:getvalueof var="templateOrderId" param="templateOrderId"/>
<dsp:getvalueof var="templateOrder" param="templateOrder"/>
<dsp:getvalueof var="scheduledOrders" param="scheduledOrderItems"/>
<dsp:getvalueof var="showActions" param="showActions"/>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<div style="width:100%">
  <div id="atg_commerce_csr_scheduledorder_scheduleTable" style="width:100%">
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
    <dsp:param name="array" value="${scheduledOrders}" />
    <dsp:param name="elementName" value="scheduledItem" />
    <dsp:oparam name="outputStart">
      <table id="schedulesTable" class="atg_dataTable" cellspacing="0" cellpadding="0">
      <thead>
        <tr>
          <th style="width: 400px;"><fmt:message key='schedules.description'/></th>
          <th style="width: 130px;"><fmt:message key='schedules.nextRunTime'/></th>
          <th style="width: 80px;"><fmt:message key='schedules.state'/></th>
          <c:if test="${not empty showActions}">
            <th><fmt:message key='schedules.actions'/></th>
          </c:if>
        </tr>
      </thead>
      <tbody>
    </dsp:oparam>
    <dsp:oparam name="outputEnd">
      </tbody>
      </table>
    </dsp:oparam>
    <dsp:oparam name="output">
        <dsp:tomap var="scheduledItemMap" param="scheduledItem"/>
 
        <dsp:droplet name="atg/commerce/custsvc/order/scheduled/CSRScheduledOrderInfo">
        <dsp:param name="item" param="scheduledItem"/>
        <dsp:oparam name="output">
        
        
        <tr>
        <td>
        <ul class="atg_commerce_csr_itemDesc">
          <%/* display the name */%>
          <dsp:getvalueof var="type" param="scheduleClassName"/>
          <li>
            <c:if test="${not empty scheduledItemMap.name}">
              <c:out value="${scheduledItemMap.name}"/>
            </c:if>
            <c:if test="${empty scheduledItemMap.name}">
              &nbsp;
            </c:if>
          </li>
          
          <%/* display the start and end dates */%>
          <li>
            <c:if test="${not empty scheduledItemMap.startDate}">
              <fmt:message key='scheduleStartDate'/>&nbsp;<web-ui:formatDate type="both" value="${scheduledItemMap.startDate}" dateStyle="short" timeStyle="short" var="startDate"/><c:out value="${startDate}"/>, &nbsp;
            </c:if>
          
            <c:if test="${not empty scheduledItemMap.endDate}">
              <fmt:message key='scheduleEndDate'/>&nbsp;<web-ui:formatDate type="both" value="${scheduledItemMap.endDate}" dateStyle="short" timeStyle="short" var="endDate" /><c:out value="${endDate}"/>
            </c:if>
            <c:if test="${empty scheduledItemMap.endDate}">
              <fmt:message key='scheduleNoEndDate'/>
            </c:if>
          </li>
            
          <%/* display calendar info */%>

          <c:if test="${type eq 'atg.service.scheduler.CalendarSchedule'}">
          
            <li>
              <strong><fmt:message key='calendarScheduleMonths'/>:</strong><dsp:getvalueof var="months" param="readableMonths"/>&nbsp;<c:out value="${months}"/>
              
              <dsp:getvalueof var="dates" param="readableDates"/>
              <c:if test="${not empty dates}">;&nbsp;<strong><fmt:message key='calendarScheduleDates'/>:</strong>&nbsp;<c:out value="${dates}"/></c:if>
              
              
              <dsp:getvalueof var="days" param="readableDays"/>
              <c:if test="${not empty days}">;&nbsp;<strong><fmt:message key='calendarScheduleDaysOfWeek'/>:</strong>&nbsp;<c:out value="${days}"/></c:if>
              
              <dsp:getvalueof var="occurrences" param="readableOccurences"/>
              <c:if test="${not empty occurrences}">;&nbsp;<strong><fmt:message key='calendarScheduleOccurrencesInMonth'/>:</strong>&nbsp;<c:out value="${occurrences}"/></c:if>
             
              <dsp:getvalueof var="hours" param="readableHours"/>
              <dsp:getvalueof var="minutes" param="readableMinutes"/>
              <c:if test="${hours != '0' and  minutes != '0'}">;&nbsp;<strong><fmt:message key='calendarScheduleHours'/>:</strong>&nbsp;<c:out value="${hours}"/>; &nbsp;
                <strong><fmt:message key='calendarScheduleMinutes'/>:</strong>&nbsp;<c:out value="${minutes}"/>
              </c:if>
            </li>
            
          </c:if>
          
          <%/* display periodic info */%>
          <c:if test="${type eq 'atg.service.scheduler.PeriodicSchedule'}">
            <li>
              <dsp:valueof param="readableInterval"/>
            </li>
          </c:if>
         </ul>
        </td>

        <td>
        <%/* display the next scheduled run time. shows past end date when end date > next scheduled time */%>
          <c:choose>
            <c:when test="${not empty scheduledItemMap.endDate and scheduledItemMap.nextScheduledRun.time > scheduledItemMap.endDate.time}">
              <span class="atg_commerce_csr_dataHighlight"><fmt:message key='schedulePastEndDate'/></span>          
            </c:when>
            <c:otherwise>
              <web-ui:formatDate type="both" value="${scheduledItemMap.nextScheduledRun}" dateStyle="short" timeStyle="short" var="nextRunDate"/>
              <c:out value="${nextRunDate}"/>
            </c:otherwise>
          </c:choose>
        </td>

        <td>
          
          
          <dsp:droplet name="/atg/dynamo/droplet/PossibleValues">
          <dsp:param name="repository" bean="/atg/commerce/order/OrderRepository"/>
          <dsp:param name="itemDescriptorName" value="scheduledOrder" />
          <dsp:param name="propertyName" value="state" />
          <dsp:param name="returnValueObjects" value="true" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="displayValues" param="displayValues" />
            <c:set var="enumeratedOptionPossibleValue" value="${displayValues[scheduledItemMap.state]}"/>
            <c:set var="stateCode" value="${enumeratedOptionPossibleValue.underlyingObject.code}"/>
            <c:set var="localizedState" value="${enumeratedOptionPossibleValue.localizedLabel}"/>
          </dsp:oparam>
          </dsp:droplet>
          
          <%/* display the state information. state can be a link when showActions variable is not null. error state appears in red */%>
          <%/* state code 0:active, 1:inactive, 2:error */%>
          <dsp:getvalueof var="state" value="${scheduledItemMap.state}"/>
          <c:choose>
          <c:when test="${not empty showActions}">
            <c:if test="${not empty scheduledItemMap.lastError}">
              <svc-ui:frameworkPopupUrl var="viewScheduleErrorsPopup"
                value="/panels/order/viewScheduleErrors.jsp"
                context="${CSRConfigurator.contextRoot}"
                windowId="${windowId}"
                scheduledOrderId="${scheduledItemMap.repositoryId}"/>
              <c:if test="${stateCode eq 2}">
                  <a href="#" class="atg_commerce_csr_dataHighlight" title="<fmt:message key='viewScheduleErrors'/>" onclick="atg.commerce.csr.order.scheduled.viewScheduleErrors('${viewScheduleErrorsPopup}','<fmt:message key="schedules.errorsForSchedule"/>');return false;"><span class="atg_commerce_csr_dataHighlight"><c:out value="${localizedState}"/></span></a> <span id="ea_csc_order_scheduled_status_failed"></span>
              </c:if>
              <c:if test="${stateCode != 2}">
                <a href="#" class="blueU" title="<fmt:message key='viewScheduleErrors'/>" onclick="atg.commerce.csr.order.scheduled.viewScheduleErrors('${viewScheduleErrorsPopup}','<fmt:message key="schedules.errorsForSchedule"/>');return false;"><c:out value="${localizedState}"/></a>
              </c:if>
            </c:if>
            <c:if test="${empty scheduledItemMap.lastError}">
                <c:if test="${stateCode eq 2}">
                  <span class="atg_commerce_csr_dataHighlight"><c:out value="${localizedState}"/></span>
                </c:if>
                <c:if test="${stateCode != 2}">
                  <c:out value="${localizedState}"/>
                </c:if>
            </c:if>
          </c:when>
          <c:otherwise>
            <c:if test="${stateCode eq 2}">
            <span class="atg_commerce_csr_dataHighlight"><c:out value="${localizedState}"/></span>
            </c:if>
            <c:if test="${stateCode != 2}">
              <c:out value="${localizedState}"/>
            </c:if>
          </c:otherwise>
          </c:choose>
         </td>
         <c:if test="${not empty showActions}">
           <td>
           <dsp:droplet name="/atg/commerce/custsvc/order/IsOrderSupportedForUpdate">
           <dsp:param name="order" value="${templateOrder}"/>
           <dsp:oparam name="true">
 
             <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels,cmcConfirmUpdateSchedulePS"/>
             <svc-ui:frameworkUrl var="errorURL"/>
             <a href="#" class="blueU" title="<fmt:message key='schedules.action.changeSchedule'/>" onclick="atg.commerce.csr.order.scheduled.loadOrderForChangeSchedule('${templateOrderId}','${scheduledItemMap.repositoryId}');return false;"><fmt:message key="schedules.action.changeSchedule"/></a>&nbsp;|&nbsp;  
             <c:if test="${stateCode != 0}">
               <a href="#" class="blueU" title="<fmt:message key='schedules.action.activateSchedule'/>" 
                 onclick="atgSubmitAction({formId: 'atg_commerce_csr_scheduled_activateSchedule',
                    formInputValues : {scheduledOrderId:'${scheduledItemMap.repositoryId}',
                      orderId:'${templateOrderId}',
                      activateSuccessURL: '${successURL}', 
                      activateErrorURL: '${errorURL}'}});return false;">
                 <fmt:message key="schedules.action.activateSchedule"/>
               </a>
             </c:if>
             <c:if test="${stateCode eq 0}">
               <a href="#" class="blueU" title="<fmt:message key='schedules.action.deactivateSchedule'/>" 
                 onclick="atgSubmitAction({formId: 'atg_commerce_csr_scheduled_deactivateSchedule',
                    formInputValues : {scheduledOrderId:'${scheduledItemMap.repositoryId}',
                      orderId:'${templateOrderId}',
                      deactivateSuccessURL: '${successURL}', 
                      deactivateErrorURL: '${errorURL}'}});return false;"><fmt:message key="schedules.action.deactivateSchedule"/></a>
             </c:if>
          </dsp:oparam>
          </dsp:droplet> 
           </td>
         </c:if>
        </tr>
        </dsp:oparam>
        </dsp:droplet>
    </dsp:oparam>
    </dsp:droplet>
  </div>
</div>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/displaySchedulesTable.jsp#1 $$Change: 946917 $--%>
