<%--
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/displayCheckoutPanelFooter.tag#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ tag language="java" %>
<%@ attribute name="nextIconOnclickURL" required="true" %>
<%@ attribute name="submitAndScheduleOnclickURL" required="false" %>
<%@ attribute name="scheduleOnclickURL" required="false" %>
<%@ attribute name="cancelActionErrorURL" required="true" %>
<%@ attribute name="order" required="true" type="atg.commerce.order.Order"%>
<%@ attribute name="submitActionButtonName" required="false" %>
<%@ attribute name="formId" required="false" %>
<%@ attribute name="includeForm" required="false" %>

<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="svc-ui"  uri="http://www.atg.com/taglibs/svc/svc-uiTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<dsp:page xml="true">
<dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderIncomplete"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/GetTotalAppeasementsForOrderDroplet"/>

<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  
  <c:set var="disableScheduleButtons" value="${false}"/>  
  <dsp:droplet name="GetTotalAppeasementsForOrderDroplet">
    <dsp:param name="order" value="${order}"/>
    <dsp:oparam name="output">
      <c:set var="disableScheduleButtons" value="${true}"/>  
    </dsp:oparam>
  </dsp:droplet>
   <dsp:droplet name="/atg/commerce/gifts/GiftShippingGroups">
    <dsp:param name="order" value="${order}"/>
    <dsp:oparam name="true">
	  	<c:set var="disableScheduleButtons" value="${true}"/> 
    </dsp:oparam>
  </dsp:droplet>
  
  
  

<c:if test="${includeForm == false}">
  <c:set var="eaSuffix" value="_footer"/>
</c:if>

<c:if test="${empty submitActionButtonName}">
  <fmt:message var="submitActionButtonName" key='displayCheckoutPanelFooter.continueToReviewOrder' />
  </c:if>
    <fmt:message var="submitAndScheduleButtonName" key='common.submitAndSchedule' />
    <fmt:message var="scheduleButtonName" key='common.schedule' />

    <c:if test="${!empty formId}">
      <div class="atg_commerce_csr_finishOrderButtons">
      <input type="button" name="checkoutFooterNextButton" id="checkoutFooterNextButton"
        onclick="<c:out value='${nextIconOnclickURL}'/>"
        value="${submitActionButtonName}"
        form="${formId}"
        dojoType="atg.widget.validation.SubmitButton"
        class="atg_commerce_csr_activeButton"/>
      <span id="ea_csc_order_submit<c:out value='${eaSuffix}'/>"></span>
      </div>
    </c:if>

    <%-- Finish order pages does not have to do any validation in their pages. Thus we need to display
         input tag without atg.widget.validation.SubmitButton  --%>
    <c:if test="${empty formId}">
      <div class="atg_commerce_csr_finishOrderButtons">
      <input type="button" name="checkoutFooterNextButton" id="checkoutFooterNextButton"
        onclick="<c:out value='${nextIconOnclickURL}'/>"
        value="${submitActionButtonName}"
        class="atg_commerce_csr_activeButton"
        />
      <span id="ea_csc_order_submit<c:out value='${eaSuffix}'/>"></span>
      </div>
    
      <script type="text/javascript">
        dojo.addOnLoad(function(){
          var theButton = dojo.byId("checkoutFooterNextButton");
          if (theButton != null) {
            theButton.focus();
          }
        });
      </script>

    <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnExchange">
      <dsp:oparam name="false">
        <%-- Only show the schedule, submit and schedule button for Incomplete orders. --%>
        <dsp:droplet name="IsOrderIncomplete">
          <dsp:oparam name="true">
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param value="${CSRConfigurator.usingScheduledOrders}" name="value"/>
              <dsp:oparam name="true">
                <div class="atg_commerce_csr_finishOrderButtons">
                  <input type="button" name="checkoutFooterSubmitAndScheduleButton" id="checkoutFooterSubmitAndScheduleButton"
                    onclick="<c:out value='${submitAndScheduleOnclickURL}'/>"
                    value="${submitAndScheduleButtonName}"
                    form="${formId}"
                    <c:if test="${disableScheduleButtons}">
                    	disabled
                    </c:if>
                    />
                <span id="ea_csc_order_submit_create_schedule<c:out value='${eaSuffix}'/>"></span>
                </div>
                <div class="atg_commerce_csr_finishOrderButtons">
                  <input type="button" name="checkoutFooterScheduleButton" id="checkoutFooterScheduleButton"
                    onclick="<c:out value='${scheduleOnclickURL}'/>"
                    value="${scheduleButtonName}"
                    form="${formId}"
                    <c:if test="${disableScheduleButtons}">
                    	disabled
                    </c:if>
                    />
                  <span id="ea_csc_order_create_schedule<c:out value='${eaSuffix}'/>"></span>
                </div>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>

</dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/displayCheckoutPanelFooter.tag#1 $$Change: 946917 $--%>
