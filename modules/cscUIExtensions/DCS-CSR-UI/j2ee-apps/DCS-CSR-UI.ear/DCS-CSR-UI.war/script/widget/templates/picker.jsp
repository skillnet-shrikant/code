<%--
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/script/widget/templates/picker.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<div>
<div dojoType="dijit.layout.TabContainer" style="width: 100%;" doLayout="false" id="dateRangePickerTabs">
  <div id="intervalTab" dojoType="dijit.layout.ContentPane" title="Interval"  selected="selected">  
    <input type="radio" id="atg_schedOrder_interval" name="atg_schedOrder_type" value="interval" checked="true" style="display:none"/>
    <ul>
    <li>
      <span>
        <label>
          Recurring every
        </label>
      </span>
      <input  name="firstName" type="text"  size="25" maxlength="40" value=""/>
      <select>
        <option>Day(s)</option>
        <option>Weeks(s)</option>
      </select>
    </li>
    </ul>
  </div>
  <div id="calendarTab" dojoType="dijit.layout.ContentPane" title="Calendar">
    <input type="radio" id="atg_schedOrder_calendar" name="atg_schedOrder_type" value="calendar" style="display:none"/>
    <ul>
      <li>
        <span>
          <input type="radio" name="datePickerDay" dojoAttachEvent='onclick:onSelectChange' value="everyDay" checked="true">
          <label>
            Every Day
          </label>
        </span>
      </li>
      <li>
        <span>
          <input type="radio" name="datePickerDay" dojoAttachEvent='onclick:onSelectChange' value="selectDay">
          <label>
            Select Day(s) of the week (Mon, Tue, ...)
          </label>
        </span>
        <div class="expandedContent">
          <select multiple size=5 name="days">
            <option value="1">Monday</option>
            <option value="2">Tuesday</option>
            <option value="3">Wednesday</option>
            <option value="4">Thursday</option>
            <option value="5">Friday</option>
            <option value="6">Saturday</option>
            <option value="7">Sunday</option>
          </select>
        </div>
      </li>
      <li>
        <span>
          <input type="radio" name="datePickerDay" dojoAttachEvent='onclick:onSelectChange' value="selectDate">
          <label>
            Select Date(s) of the week (1, 2, 3, ...)
          </label>
        </span>
        <div  class="expandedContent">
          <select multiple size=5 name="dates">
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="4">4</option>
            <option value="5">5</option>
            <option value="6">6</option>
            <option value="7">7</option>
            <option value="8">8</option>
            <option value="9">9</option>
            <option value="10">10</option>
            <option value="11">11</option>
            <option value="12">12</option>
            <option value="13">13</option>
            <option value="14">14</option>
            <option value="15">15</option>
            <option value="16">16</option>
            <option value="17">17</option>
            <option value="18">18</option>
            <option value="19">19</option>
            <option value="20">20</option>
          </select>
        </div>
      </li>
      <li>
        <span>
          <input type="radio" name="datePickerMonth" dojoAttachEvent='onclick:onSelectChange' checked="true">
          <label>
            Every Month
          </label>
        </span>
      </li>
      <li>
        <span>
          <input type="radio" name="datePickerMonth" dojoAttachEvent='onclick:onSelectChange'>
          <label>
            Select Month(s)
          </label>
        </span>
        <div class="expandedContent">
          <select multiple size=5 name="months">
            <option value="0">Jan</option>
            <option value="1">Feb</option>
            <option value="2">Mar</option>
            <option value="3">Apr</option>
            <option value="4">May</option>
            <option value="5">Jun</option>
            <option value="6">Jul</option>
            <option value="7">Aug</option>
            <option value="8">Sep</option>
            <option value="9">Oct</option>
            <option value="10">Nov</option>
            <option value="11">Dec</option>
          </select>
        </div>
      </li>
    </ul>
  </div>
</div>
</div>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/script/widget/templates/picker.jsp#1 $$Change: 946917 $--%>
