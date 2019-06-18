<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <div class="atg_dateRangePicker">
    <div dojoType="dijit.layout.TabContainer" class="atg_dateRangePickerTabs" id="atg_dateRangePickerTabs" doLayout="false" dojoAttachEvent="onClick:onScheduleType">

    <!-- Calendar Tab -->
    <fmt:message var="calendarTabTitle" key="schedule.type.calendar"/>
    <div class="atg_dateRangePicker_calendarTab" id="atg_dateRangePicker_calendarTab" dojoType="dijit.layout.ContentPane"    title="${calendarTabTitle}" dojoAttachEvent="onClick:onScheduleType">
      <input type="radio" id="atg_schedOrder_calendar" name="atg_schedOrder_type" value="calendar" style="display:none"/>
      <fieldset>
        <legend>
          <fmt:message key="dateRangePicker.days.label"/>
        </legend>
        <ul class="atg_dataForm">
          <li class="atg_dataForm_first">
           <span class="atg_commerce_csr_fieldTitle atg_commerce_csr_scheduleInterval">
              <input type="radio" dojoAttachPoint="everyDayOption" name="datePickerDay" dojoAttachEvent='onclick:onSelectChange' value="everyDay" checked="true">
              <label>
                <fmt:message key="dateRangePicker.days.everyDay.label"/>
              </label>
            </span>
          </li>

          <li class="atg_dataForm_first">
            <span class="atg_commerce_csr_fieldTitle atg_commerce_csr_scheduleInterval">
              <input type="radio" dojoAttachPoint="selectDaysOption" name="datePickerDay" dojoAttachEvent='onclick:onSelectChange' value="selectDay">
              <label>
                <fmt:message key="dateRangePicker.days.selectDays.label"/>
              </label>
              <span id="ea_csc_order_scheduled_days_of_week"></span>
            </span>
            <div class="expandedContent atg_dateRangePicker_days" dojoAttachPoint="selectDaysPanel">
              <ul  class="atg_dataForm">
                <li class="atg_dataForm_first">

                  <div class="atg_dateRangePicker_daysSelect"  dojoAttachPoint="daysSelect">
                    <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.sunday'/>" value="1"    type="day" eventTopic="daysSelect"></div>
                    <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.monday'/>" value="2"    type="day" eventTopic="daysSelect"></div>
                    <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.tuesday'/>" value="3"   type="day" eventTopic="daysSelect"></div>
                    <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.wednesday'/>" value="4" type="day" eventTopic="daysSelect"></div>                  
                    <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.thursday'/>" value="5"  type="day" eventTopic="daysSelect"></div>                  
                    <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.friday'/>" value="6"    type="day" eventTopic="daysSelect"></div>                  
                    <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.saturday'/>" value="7"  type="day" eventTopic="daysSelect"></div>                  
                  </div>



                  </li>
                  <li class="atg_dataForm_first">
                   <ul class="atg_dataForm atg_dateRangePicker_daysOptions">
                     <li>
                       <span class="atg_commerce_csr_extendedFieldTitle">
                        <input type="radio" name="datePickerWeek" dojoAttachPoint="allOccurrencesOption"  dojoAttachEvent='onclick:onAllWeeksSelect' value="selectAllWeeks" checked="true">
                        <label>
                          <fmt:message key="dateRangePicker.weeks.allWeeks.label"/>
                        </label>
                        <span id="ea_csc_order_scheduled_weeks"></span>
                      </span>
                    </li>
                      <li class="atg_dataForm_first"  >
                      <span class="atg_commerce_csr_extendedFieldTitle">
                        <input type="radio" name="datePickerWeek" dojoAttachPoint="selectedOccurrencesOption" dojoAttachEvent='onclick:onWeekSelect' value="selectWeeks">
                        <label>
                          <fmt:message key="dateRangePicker.weeks.selectedWeeks.label"/>
                        </label>
                      </span>
                        <div class="atg_dateRangePicker_weeksSelect"  dojoAttachPoint="weeksSelectPanel" id="weekListing" >
                          <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.weeks.first'/>" value="1"  type="week" eventTopic="weeksSelect"></div>
                          <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.weeks.second'/>" value="2" type="week" eventTopic="weeksSelect"></div>
                          <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.weeks.third'/>" value="3"  type="week" eventTopic="weeksSelect"></div>
                          <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.weeks.fourth'/>" value="4" type="week" eventTopic="weeksSelect"></div>
                          <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.weeks.last'/>" value="5"   type="week" eventTopic="weeksSelect"></div>                                                                                          

                        </div>                  
                      </li>
                      </ul>
                    </li>
                </ul>
            </div>
          </li>
          <li class="atg_dataForm_first">
            <span class="atg_commerce_csr_fieldTitle atg_commerce_csr_scheduleInterval">
              <input type="radio" dojoAttachPoint="selectDatesOption" name="datePickerDay" dojoAttachEvent='onclick:onSelectChange' value="selectDate">
              <label>
                <fmt:message key="dateRangePicker.days.selectDates.label"/>
              </label>
              <span id="ea_csc_order_scheduled_dates_in_month"></span>
            </span>
            <div  class="expandedContent atg_dateRangePicker_datesOfMonth"  dojoAttachPoint="selectDatesPanel">
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.1'/>" value="1"   type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.2'/>" value="2"   type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.3'/>" value="3"   type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.4'/>" value="4"   type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.5'/>" value="5"   type="date" eventTopic="datesSelect"></div>                                                                                                                              
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.6'/>" value="6"   type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.7'/>" value="7"   type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.8'/>" value="8"   type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.9'/>" value="9"   type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.10'/>" value="10" type="date" eventTopic="datesSelect"></div>                                                                                                                              
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.11'/>" value="11" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.12'/>" value="12" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.13'/>" value="13" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.14'/>" value="14" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.15'/>" value="15" type="date" eventTopic="datesSelect"></div>                                                                                                                              
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.16'/>" value="16" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.17'/>" value="17" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.18'/>" value="18" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.19'/>" value="19" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.20'/>" value="20" type="date" eventTopic="datesSelect"></div>                                                                                                                              
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.21'/>" value="21" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.22'/>" value="22" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.23'/>" value="23" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.24'/>" value="24" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.25'/>" value="25" type="date" eventTopic="datesSelect"></div>                                                                                                                              
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.26'/>" value="26" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.27'/>" value="27" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.28'/>" value="28" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.29'/>" value="29" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.30'/>" value="30" type="date" eventTopic="datesSelect"></div>                                                                                                                              
              <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.days.31'/>" value="31" type="date" eventTopic="datesSelect"></div>                                                                                          
              <div class="atg_commerce_csr_nonSelectedItem atg_dateRangePicker_empty"></div>
              <div class="atg_commerce_csr_nonSelectedItem atg_dateRangePicker_empty"></div>
              <div class="atg_commerce_csr_nonSelectedItem atg_dateRangePicker_empty"></div>
              <div class="atg_commerce_csr_nonSelectedItem atg_dateRangePicker_empty"></div>                                    

            </div>
          </li>
          </ul>
        </fieldset>

        <fieldset>
          <legend><fmt:message key="dateRangePicker.months.label"/></legend>
          <ul class="atg_dataForm">
          <li class="atg_dataForm_first">
           <span class="atg_commerce_csr_fieldTitle">
              <input type="radio" name="datePickerMonth" dojoAttachPoint="allMonthsOption" dojoAttachEvent='onclick:onSelectChange' checked="true" value="everyMonth">
              <label>
                <fmt:message key="dateRangePicker.months.everyMonth.label"/>
              </label>
            </span>
          </li>

          <li class="atg_dataForm_first">
            <span class="atg_commerce_csr_fieldTitle">
              <input type="radio" name="datePickerMonth" dojoAttachPoint="selectedMonthsOption" dojoAttachEvent='onclick:onSelectChange' value="selectMonth">
              <label>
                <fmt:message key="dateRangePicker.months.selectMonths"/>
              </label>
            </span>
            <div class="expandedContent atg_dateRangePicker_months"  id="atg_commerce_csr_monthsSelectPanel" dojoAttachPoint="monthsSelectPanel">
              <div class="atg_dateRangePicker_monthsSubOptions" >
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.january'/>" value="0" type="month" eventTopic="monthsSelect"></div> 
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.febuary'/>" value="1" type="month" eventTopic="monthsSelect"></div> 
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.march'/>" value="2"  type="month" eventTopic="monthsSelect"></div> 
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.april'/>" value="3"  type="month" eventTopic="monthsSelect"></div>                                                 
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.may'/>" value="4"  type="month" eventTopic="monthsSelect"></div> 
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.june'/>" value="5"  type="month" eventTopic="monthsSelect"></div> 
              </div>
              <div class="atg_dateRangePicker_monthsSubOptions">
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.july'/>" value="6"  type="month" eventTopic="monthsSelect"></div> 
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.august'/>" value="7"  type="month" eventTopic="monthsSelect"></div>                                                 
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.september'/>" value="8"  type="month" eventTopic="monthsSelect"></div> 
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.october'/>" value="9"  type="month" eventTopic="monthsSelect"></div> 
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.november'/>" value="10" type="month"  eventTopic="monthsSelect"></div> 
                <div dojoType="atg.csc.toggleLink" label="<fmt:message key='dateRangePicker.months.december'/>" value="11" type="month"  eventTopic="monthsSelect"></div>                                                 
              </div>


            </div>
          </li>
        </ul>
      </fieldset>
    </div>
    <!-- Interval Tab-->
    <fmt:message var="intervalTabTitle" key="schedule.type.interval"/>
    <div class="atg_dateRangePicker_intervalTab" id="atg_dateRangePicker_intervalTab" dojoType="dijit.layout.ContentPane" title="${intervalTabTitle}"  selected="selected" dojoAttachEvent="onChange:onScheduleType">  
      <input type="radio" id="atg_schedOrder_interval" name="atg_schedOrder_type" value="interval" checked="true" style="display:none"/>
      <ul class="atg_dataForm">
        <li class="atg_dataForm_first">
          <span class="atg_commerce_csr_fieldTitle">
            <label>
              <fmt:message key="dateRangePicker.recurringEvery.label"/>
            </label>
          </span>
        </li>
        <li class="atg_dataForm_first">
          <input  dojoAttachPoint="intervalValue" name="interval" type="text"  size="3" maxlength="3" dojoAttachEvent="onkeyup:onInterval" value=""/>
          <select name="intervalSelect" dojoAttachPoint="intervalSelect" dojoAttachEvent="onchange:onIntervalOption" >
            <option value="days">
              <fmt:message key="dateRangePicker.days.option.label"/>
            </option>
            <option value="weeks">
              <fmt:message key="dateRangePicker.weeks.option.label"/>
            </option>
          </select>
        </li>
      </ul> 
    </div>
    
  </div>

  </div>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/script/widget/templates/dateRangePicker.jsp#2 $$Change: 1179550 $--%>
