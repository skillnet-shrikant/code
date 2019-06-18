<%--
This page fragment generates a series of dsp:option tags, one for each
state or Canadian province we want to let the user select as part of an
address.  
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:getvalueof var="countryPickerId" param="countryPickerId"/>
<dspel:getvalueof var="statePickerId" param="statePickerId"/>
<dspel:getvalueof var="formHandlerFieldName" param="formHandlerFieldName"/>
<dspel:getvalueof var="missingMessage" param="missingMessage"/>
<dspel:getvalueof var="inlineIndicator" param="inlineIndicator"/>
<dspel:getvalueof var="promptMessage" param="promptMessage"/>

<dspel:select id="${countryPickerId}" bean="${formHandlerFieldName}" onchange="populateState(${statePickerId},this)">
  <dspel:tagAttribute name="dojoType" value="atg.widget.validation.SimpleComboBox" />
  <dspel:tagAttribute name="missingMessage" value="${missingMessage}" />
  <dspel:tagAttribute name="inlineIndicator" value="${inlineIndicator}" />
  <dspel:tagAttribute name="promptMessage" value="${promptMessage}" />

<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <dspel:option value="">
    <fmt:message key="common.country.title"/>
  </dspel:option>
</dspel:layeredBundle>
<dspel:layeredBundle basename="atg.commerce.util.CountryStateResources">
  <dspel:droplet name="/atg/dynamo/droplet/ForEach">
    <dspel:param name="array" bean="/atg/core/i18n/CountryList.places"/>
    <dspel:setvalue param="country" paramvalue="element"/>
    <dspel:oparam name="output">
      <dspel:getvalueof var="code" vartype="java.lang.String" param="country.code">
        <dspel:option value="${code}">
          <c:out value="${country.displayName}"/>
        </dspel:option>
      </dspel:getvalueof>
    </dspel:oparam>
  </dspel:droplet>
</dspel:layeredBundle>
</dspel:select>
			  
 <c:set var="allStates">
   <json:object>
     <dspel:droplet name="/atg/dynamo/droplet/ForEach">
       <dspel:param name="array" bean="/atg/core/i18n/CountryList.places"/>
       <dspel:setvalue param="country" paramvalue="element"/>
       <dspel:oparam name="output">
         <dspel:getvalueof var="code" vartype="java.lang.String" param="country.code">
           <dspel:droplet name="/atg/commerce/util/StateListDroplet">
             <dspel:param name="countryCode" value="${fn:toUpperCase(code)}"/>
             <dspel:oparam name="output">
               <dspel:getvalueof var="states" param="states"/>
               <c:if test="${fn:length(states)>0}">
                 <json:object name="${code}">
                   <json:array name="states" var="state" items="${states}">
                     <json:object>
                       <json:property name="code" value="${state.code}"/>
                       <json:property name="name" value="${state.displayName}"/>
                     </json:object>
                   </json:array>
                 </json:object>
               </c:if>
             </dspel:oparam>
           </dspel:droplet>
         </dspel:getvalueof>
       </dspel:oparam>
     </dspel:droplet>
   </json:object>
 </c:set>
   <script type="text/javascript">  
     populateState = function (statePicker,countryObj) {
       var allStates = ${allStates};
       //var statePicker = document.getElementById(statePickerId);
       var statePicker = statePicker;
       var countryCode = countryObj.value;
       
       // Empty options just in case new drop down is shorter
       if ( statePicker.type == 'select-one' ) {
         for (var i = 0; i < statePicker.options.length; i++) {
           statePicker.options[i] = null;
         }
         statePicker.options.length=null;
         statePicker.options[0] = new Option('','');
         statePicker.selectedIndex = 0;
       }
       // Populate options
       if(allStates[countryCode]){
         var statesArray = allStates[countryCode]['states'];
         var optionCntr = 1;
         for (var loop = 0; loop < statesArray.length; loop++) {
           stateCode = statesArray[loop]['code'];
           stateName = statesArray[loop]['name'];
           if ( stateCode != '' ) {
             statePicker.options[optionCntr] = new Option(stateName, stateCode);
           }
           optionCntr++;
         }
       }
    };
  </script>
</dspel:page>
		
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/countrypicker.jsp#1 $$Change: 946917 $--%>
