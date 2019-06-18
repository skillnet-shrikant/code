<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
 
<dsp:page xml="true">
	
	<dsp:importbean bean="/com/mff/commerce/csr/legacy/LegacyOrderSearchFormHandler"/>
   <body>
    <br />
	<dsp:form formid="legacyrOrderSearchForm" id="legacyrOrderSearchForm" action="#" method="post">

	<div>
       &nbsp;&nbsp;&nbsp;<span style="white-space: pre;">Specify at least one search requirement. firstname and lastname or order number with date range, Search fields are not case sensitive. If entering an Order Number, enter either a full or partial number.</span>
	</div>
	 <div>
		<table>
		  <tr> 
		    <td>Order Id:</td>
		       <td>
	 			    <dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[0].name" type="hidden" value="id"/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[0].method" name="idMethod" type="hidden" value="1"/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[0].dataType" type="hidden" value="string"/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[0].relation" type="hidden" value="=="/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[0].caseSensitive" type="hidden" value="1"/>
					<dsp:input type="text" id="order" bean="LegacyOrderSearchFormHandler.searchAttributes[0].values" name="id" 
							   beanvalue="LegacyOrderSearchFormHandler.searchAttributes[0].values[0]" size="30" maxlength="30"/>
			 	</td>			 
			</tr>
			<tr>
			<tr><span></span></tr>
			<td>FirstName:</td>
		    	<%-- 
				<dsp:input type="text" id="order" bean="StoreOrderSearchFormHandler.orderNumber" name="orderNumber" 
					beanvalue="StoreOrderSearchFormHandler.orderNumber" size="30" maxlength="30"/>
				--%>
				<td>
	 			    <dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[1].name" type="hidden" value="profile.firstName"/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[1].method" name="firstNameMethod" type="hidden" value="1"/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[1].dataType" type="hidden" value="string"/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[1].relation" type="hidden" value="=="/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[1].caseSensitive" type="hidden" value="1"/>
					<dsp:input type="text" id="order" bean="LegacyOrderSearchFormHandler.searchAttributes[1].values" name="firstName" 
							   beanvalue="LegacyOrderSearchFormHandler.searchAttributes[1].values[0]" size="30" maxlength="30"/>
			 	</td>
			 <td>LastName:</td>
		    				<td>
	 			    <dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[4].name" type="hidden" value="profile.lastName"/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[4].method" name="firstNameMethod" type="hidden" value="1"/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[4].dataType" type="hidden" value="string"/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[4].relation" type="hidden" value="=="/>
					<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[4].caseSensitive" type="hidden" value="1"/>
					<dsp:input type="text" id="order" bean="LegacyOrderSearchFormHandler.searchAttributes[4].values" name="lastName" 
							   beanvalue="LegacyOrderSearchFormHandler.searchAttributes[4].values[0]" size="30" maxlength="30"/>
			 	</td>
			</tr>
			<tr><span></span></tr>
			 <tr> 
		  		 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <td>Start Date:</td>
			<td>
				<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[2].name" type="hidden" value="submittedDate"/>
				<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[2].dataType" type="hidden" value="datetime"/>
				<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[2].method" type="hidden" value="0"/>
				<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[2].caseSensitive" type="hidden" value="1"/>
				<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[2].relation" type="hidden" value=">="/>
						   		   
				<input type="text" id="startDate" maxlength="10" size="10" name="startDate" 
					dojoType="dijit.form.DateTextBox" constraints ="{datePattern:'yyyy-MM-dd'}" />
	            <img id="startDateImg"
	                src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
	                width="16"
	                height="16"
	                border="0"
	                title="Start Date"
	                onclick="dojo.byId('startDate').focus()"/>
            </td>
            <td>
	             &nbsp;&nbsp;End Date:</td>
		    <td>
				<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[3].name" type="hidden" value="submittedDate"/>
				<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[3].dataType" type="hidden" value="datetime"/>
				<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[3].method" type="hidden" value="0"/>
				<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[3].caseSensitive" type="hidden" value="1"/>
				<dsp:input bean="LegacyOrderSearchFormHandler.searchAttributes[3].relation" type="hidden" value="<="/>
						   
				 <input type="text" id="endDate" maxlength="10" size="10" name="endDate" 
					dojoType="dijit.form.DateTextBox" constraints ="{datePattern:'yyyy-MM-dd'}" />
	            <img id="endDateImg"
	                src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
	                width="16"
	                height="16"
	                border="0"
	                title="End Date"
	                onclick="dojo.byId('endDate').focus()"/>
			</td>
          </tr>
          <tr><span></span></tr>
          <tr> 
		    <td>email:</td>
		       <td>
	 			    <dsp:input type="text" id="order" bean="LegacyOrderSearchFormHandler.searchAttributes[5].values" name="email" 
							   beanvalue="LegacyOrderSearchFormHandler.searchAttributes[5].values[0]" size="30" maxlength="30"/>
			 	</td>			 
			</tr>
			<tr><span></span></tr>
			<tr> 
		    <td>phone:</td>
		       <td>
	 			   <dsp:input type="text" id="order" bean="LegacyOrderSearchFormHandler.searchAttributes[6].values" name="phone" 
							   beanvalue="LegacyOrderSearchFormHandler.searchAttributes[6].values[0]" size="30" maxlength="10"/>
			 	</td>			 
			</tr>
			<tr><span></span></tr>
			
		 </table>
	  </div>
	 
	   <br /><br />
       <div style="margin-left:520px;">
       		<dsp:input type="hidden" value="true" id="legacyOrderSearch" bean="LegacyOrderSearchFormHandler.search" />
       		<input type="button" id="legacyOrderSearchButton" value="Search" onclick="searchStoreOrders();return false;" />
      </div>
	  <br /><br />
	</dsp:form>
	
    <dsp:getvalueof var="searchResults" bean="LegacyOrderSearchFormHandler.searchResults" />
	<dsp:include page="displaySearchResults.jsp" otherContext="${CSRConfigurator.contextRoot}" flush="false">
		<dsp:param name="searchResults" value="${searchResults}" />
	</dsp:include>
  </body>

  <script>
  	function searchStoreOrders(){
    	atgSubmitAction({
    		form : document.getElementById('legacyrOrderSearchForm'),
    		panels : [ 'csLegacyOrderSearchP' ],
    		panelStack: ["legacyOrderPS"]}
     ); 
  	}
  </script>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>