<%--
 This page defines the schedule update panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/scheduleUpdate.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

  <dsp:importbean var="CSRScheduledOrderFormHandler" bean="/atg/commerce/custsvc/order/scheduled/CSRScheduledOrderFormHandler"/>
  <dsp:importbean var="CSRScheduledOrderTools" bean="/atg/commerce/custsvc/order/scheduled/CSRScheduledOrderTools"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart"/>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:getvalueof var="datePattern" bean="CSRScheduledOrderFormHandler.localizedDateFormatString"/>

  <script type="text/javascript" charset="utf-8">
      dojo.require("dijit._Widget");
      dojo.require("dijit._Templated");
      dojo.require("dijit._Container");
      dojo.require("dijit.form.Slider");
      dojo.require("dijit.layout.ContentPane");
      dojo.require("dijit.layout.TabContainer");
      dojo.require("dijit.form.DateTextBox");
      dojo.require("dojo.date.locale");
      dojo.require("dojo.parser");  // scan page for widgets and instantiate them
      dojo.registerModulePath("atg.csc", atg.commerce.csr.getContextRoot() + "/script/widget");
      dojo.require("atg.csc.dateRangePicker");
      dojo.require("atg.csc.toggleLink");

      _container_.onLoadDeferred.addCallback(function () {
        atg.commerce.csr.order.scheduled.loadUpdateForm("atg_commerce_csr_order_calendarScheduledOrderUpdate");   
      });

  </script>

  <svc-ui:frameworkUrl var="updateSuccessURL" panelStacks="globalPanels,cmcConfirmUpdateSchedulePS"/>
  <svc-ui:frameworkUrl var="updateErrorURL"/>
  <div class="atg_commerce_csr_scheduleForm">
  <dsp:form id="atg_commerce_csr_order_calendarScheduledOrderUpdate"  formid="atg_commerce_csr_order_calendarScheduledOrderUpdate">

    <dsp:input type="hidden" priority="-10" value="" bean="CSRScheduledOrderFormHandler.update" />

    <input type="hidden" priority="-10" name="cancelScheduleProcess" value="${cancelScheduleProcess}"/>

    <dsp:getvalueof var="scheduleId" param="scheduledOrderId"/>    
    <dsp:input type="hidden" value="${scheduleId }"  bean="CSRScheduledOrderFormHandler.repositoryId" />
    <dsp:setvalue bean="CSRScheduledOrderFormHandler.repositoryId" value="${scheduleId }"/>

    <dsp:input type="hidden" value="${updateErrorURL }" bean="CSRScheduledOrderFormHandler.updateErrorURL" />
    <dsp:input type="hidden" value="${updateSuccessURL }"  bean="CSRScheduledOrderFormHandler.updateSuccessURL" />

    <ul class="atg-csc-base-table">
      <li class="atg_dataForm_first atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label">
          <label>
            <fmt:message key="schedule.name"/>:
          </label>
        </span>
        <div class="atg-csc-base-table-cell">
          <dsp:input type="text" bean="CSRScheduledOrderFormHandler.value.name" maxlength="32" iclass="atg-base-table-product-catalog-search-input"/>
        </div>
      </li>

      <li class="atg_dataForm_first atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label" style="padding-top:10px;">
          <label>
            <fmt:message key="schedule.startDate"/>:
          </label>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-product-catalog-search-input-padding">
          <dsp:getvalueof var="timePattern" bean="CSRScheduledOrderFormHandler.localizedTimeFormatString"/>
          <web-ui:formatDate type="time" value="${CSRScheduledOrderFormHandler.scheduleStartDate}" timeStyle="short" var="startTime" />
          <dsp:input 
            type="hidden" 
            id="startDateHidden" 
            name="startDateHidden" 
            bean="CSRScheduledOrderFormHandler.startDate" 
            maxlength="10"/>
          <input 
            type="text" 
            id="startDate" 
            name="startDate" 
            maxlength="10"
            dojoType="dijit.form.DateTextBox"
            constraints="{datePattern:'${datePattern}'}"
            onchange="dojo.byId('startDateHidden').value = dojo.byId('startDate').value;"
            value="now" />
          <img id="startDateImg"
           src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
             width="16"
             height="16"
             border="0"
             class="atg_commerce_csr_scheduleFormCalendarIcon"
             title="<fmt:message key='schedule.startDateCalendar.title'/>"
             onclick="dojo.byId('startDate').focus()"/>
          <c:out value="${startTime}"/>
          <dsp:valueof bean="CSRScheduledOrderTools.schedulerTimeZoneDisplay"/>
        </div>
      </li>


      <li class="atg_dataForm_first atg-csc-base-table-row" >
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label atg-base-table-schedule-create-label-valign">
          <label>
            <fmt:message key="schedule.scheduleBy"/>:
          </label>
        </span>
        <!-- DATE RANGE WIDGET HERE -->
        <div class="atg-csc-base-table-cell atg-base-table-product-catalog-search-input-padding" style="padding-top:8px;">
          <div id="cscDateRange">
          </div>
        </div>
        <!-- END DATE RANGE WIDGET -->

        <!-- hidden form fields that are used to setup the values in the widget and are set via javascript based on values --> 
        <!-- provided by the widget -->
        <dsp:input type="hidden" id="scheduleType" bean="CSRScheduledOrderFormHandler.scheduleType" />
        <dsp:input type="hidden" id="daysOption" bean="CSRScheduledOrderFormHandler.daysOption" />
        <dsp:input type="hidden" id="occurrencesOption" bean="CSRScheduledOrderFormHandler.occurrencesOption" />
        <dsp:input type="hidden" id="monthsOption" bean="CSRScheduledOrderFormHandler.monthsOption" />
        <dsp:input type="hidden" id="intervalOption"  bean="CSRScheduledOrderFormHandler.intervalOption"/>
        <dsp:input type="hidden" id="selectedInterval" bean="CSRScheduledOrderFormHandler.selectedInterval" converter="Number" nullable="true"/>

        <dsp:input type="hidden" id="selectedOccurrences" bean="CSRScheduledOrderFormHandler.selectedOccurrences" converter="intArray"/>
        <dsp:input type="hidden" id="selectedMonths" bean="CSRScheduledOrderFormHandler.selectedMonths" converter="intArray"/>
        <dsp:input type="hidden" id="selectedDays" bean="CSRScheduledOrderFormHandler.selectedDays" converter="intArray"/>
        <dsp:input type="hidden" id="selectedDates" bean="CSRScheduledOrderFormHandler.selectedDates" converter="intArray"/>
        <dsp:input type="hidden" id="selectedHours" bean="CSRScheduledOrderFormHandler.selectedHours" converter="intArray"/>
        <dsp:input type="hidden" id="selectedMinutes" bean="CSRScheduledOrderFormHandler.selectedMinutes" converter="intArray"/>
      </li>

      <li class="atg_dataForm_first atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label atg-base-table-schedule-create-label-valign">
          <label>
            <fmt:message key="schedule.endDate"/>:
          </label>
        </span>
        <div class="atg-csc-base-table-cell atg-base-table-product-catalog-search-input-padding" style="padding-top:11px;">
          <ul class="atg-csc-base-table" style="margin-left:0;">
            <li class="atg_dataForm_first atg-csc-base-table-row">
              <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-schedule-create-label-padding">
                <dsp:input type="radio" value="none" bean="CSRScheduledOrderFormHandler.endDateOption"/>
                <label>
                  <fmt:message key="schedule.noEndDate"/>
                </label>
              </span>
            </li>

            <li class="atg_dataForm_first atg-csc-base-table-row">
              <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-schedule-create-label-padding">
                <dsp:input type="radio" value="endBy" bean="CSRScheduledOrderFormHandler.endDateOption"/>
                <label><fmt:message key="schedule.endBy"/></label>
              </span>
              <div class="atg-csc-base-table-cell atg-base-table-product-catalog-search-input-padding">
                <dsp:getvalueof var="endDateOption" bean="CSRScheduledOrderFormHandler.endDateOption"/>
                <c:choose>
                  <c:when test="${endDateOption eq 'endBy'}">
                  <web-ui:formatDate type="time" value="${CSRScheduledOrderFormHandler.scheduleEndDate}" timeStyle="short" var="endTime" />
                  <dsp:input 
                    type="hidden" 
                    id="endDateHidden" 
                    bean="CSRScheduledOrderFormHandler.endDate" 
                    maxlength="10"/>
                 <input 
                    type="text" 
                    id="endDate" 
                    name="endDate" 
                    maxlength="10"
                    dojoType="dijit.form.DateTextBox"
                    constraints="{datePattern:'${datePattern}'}"
                    onchange="dojo.byId('endDateHidden').value = dojo.byId('endDate').value;"/>
                 <img id="endDateImg"
                    src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
                      width="16"
                      height="16"
                      border="0"
                      class="atg_commerce_csr_scheduleFormCalendarIcon"                
                    title="<fmt:message key='schedule.endDateCalendar.title'/>"
                    onclick="dojo.byId('endDate').focus()"/>
                  <c:out value="${endTime}"/>
                  </c:when>
                <c:otherwise>
                <web-ui:formatDate type="time" value="${CSRScheduledOrderFormHandler.scheduleEndDate}" timeStyle="short" var="endTime" />
                <dsp:input 
                    type="hidden" 
                    value="" 
                    id="endDateHidden" 
                    bean="CSRScheduledOrderFormHandler.endDate" 
                    maxlength="10"/>
                <input 
                    type="text" 
                    id="endDate" 
                    name="endDate" 
                    maxlength="10"
                    dojoType="dijit.form.DateTextBox"
                    constraints="{datePattern:'${datePattern}'}"
                    onchange="dojo.byId('endDateHidden').value = dojo.byId('endDate').value;"/>
                <img id="endDateImg"
                  src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
                    width="16"
                    height="16"
                    border="0"
                    class="atg_commerce_csr_scheduleFormCalendarIcon"
                    title="<fmt:message key='schedule.endDateCalendar.title'/>"
                    onclick="dojo.byId('endDate').focus()"/>
                    <c:out value="${endTime}"/>
                    <dsp:valueof bean="CSRScheduledOrderTools.schedulerTimeZoneDisplay"/>       
                </c:otherwise>
                </c:choose>
              </div>
            </li>
          </ul>
        </div>
      </li>
    </ul>
    
  </dsp:form>

  <dsp:getvalueof var="cancelScheduleProcess" param="cancelScheduleProcess"/>
  <c:choose>
  <c:when test="${cancelScheduleProcess == 'cancelUpdateSchedule'}">
    <svc-ui:frameworkUrl var="cancelURL" panelStacks="cmcScheduledOrderPS"/>
  </c:when>
  </c:choose>
  <dsp:form style="display:none" id="atg_commerce_csr_order_cancelScheduleUpdate"
    formid="atg_commerce_csr_order_cancelScheduleUpdate">
    <input type="hidden" priority="-10" name="cancelScheduleProcess" value="${cancelScheduleProcess}"/>
    <dsp:input type="hidden" value="${cancelURL }"
      bean="CSRScheduledOrderFormHandler.cancelURL" />
    <dsp:input type="hidden" priority="-10" value=""
      bean="CSRScheduledOrderFormHandler.cancel" />
   </dsp:form> 

  <div class="atg_commerce_csr_scheduleControls">  
  <input type="button" name="updateSchedule" id="updateSchedule" class="atg_commerce_csr_activeButton"
    onclick="atg.commerce.csr.order.scheduled.updateSchedule('atg_commerce_csr_order_calendarScheduledOrderUpdate'); return false;"
    value="<fmt:message key='schedule.update.updateButton'/>"/>

    <input type="button" name="cancelUpdate" id="cancelUpdate"
      onclick="atg.commerce.csr.order.scheduled.cancelUpdate('atg_commerce_csr_order_cancelScheduleUpdate'); return false;"
      value="<fmt:message key='schedule.update.cancelUpdateButton'/>"/>
  </div>
    <script type="text/javascript">
    atg.progress.update("cmcScheduleUpdatePS");
    _container_.onLoadDeferred.addCallback(function () {
      dojo.byId("startDate").value = "${CSRScheduledOrderFormHandler.startDate}";
      dojo.byId("endDate").value = "${CSRScheduledOrderFormHandler.endDate}";
    });
    </script>
  </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/scheduleUpdate.jsp#1 $$Change: 946917 $--%>
