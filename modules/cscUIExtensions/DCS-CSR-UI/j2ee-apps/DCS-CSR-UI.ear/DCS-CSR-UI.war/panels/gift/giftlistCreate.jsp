<%--
This page defines the create gift list panel.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlistCreate.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <c:set var="giftlistFormHandlerPath" value="/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler"/>
  <dsp:importbean var="glfh" bean="${giftlistFormHandlerPath}"/>
  <dsp:importbean bean="/atg/commerce/custsvc/gifts/GiftlistUIState"/>
  <dsp:importbean var="defaultPageFragment" bean="/atg/commerce/custsvc/ui/fragments/gift/GiftlistCreateDefault" />
  <dsp:importbean var="extendedPageFragment" bean="/atg/commerce/custsvc/ui/fragments/gift/GiftlistCreateExtended" />
  <dsp:getvalueof var="giftlistId" param="giftlistId" scope="request" />
  
  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
    <svc-ui:frameworkPopupUrl var="url"
      value="/panels/gift/giftlistCreate.jsp"
      context="${CSRConfigurator.contextRoot}"
      giftlistId="${giftlistId}"
      windowId="${windowId}"/>
    <svc-ui:frameworkPopupUrl var="successUrl"
      value="/panels/gift/giftlistCreate.jsp"
      context="${CSRConfigurator.contextRoot}"
      success="true"
      giftlistId="${giftlistId}"
      windowId="${windowId}"/> 
        
    <div class="atg_commerce_csr_popupPanel">   
      <c:choose>
        <c:when test="${param.success}">
          <script type="text/javascript">
            hidePopupWithResults( 'giftlistCreatePopup', {result : 'save'});
          </script>
        </c:when>   
        <c:otherwise>
          <dsp:droplet name="/atg/targeting/TargetingForEach">
            <dsp:param name="targeter" bean="/atg/registry/Slots/UserMessagingSlot"/>
            <dsp:oparam name="outputStart"> 
              <span class="atg_commerce_csr_common_content_alert">
                <ul>
            </dsp:oparam>
            <dsp:oparam name="output"> 
              <dsp:getvalueof var="details" param="element.messageDetails" />
              <li>${fn:escapeXml(details[0].description)}</li>
            </dsp:oparam>
            <dsp:oparam name="outputEnd"> 
                </ul>
              </span>
            </dsp:oparam>
            <dsp:oparam name="empty">
            </dsp:oparam>
          </dsp:droplet>
        </c:otherwise>
      </c:choose>
      
      <c:choose>
        <c:when test="${empty giftlistId}" >
          <c:set var="formId" value="createGiftlistForm"/>
        </c:when>
        <c:otherwise>
          <c:set var="formId" value="editGiftlistForm"/>
        </c:otherwise>
      </c:choose>
    
      <dsp:form formid="${formId}" id="${formId}">
        <c:choose>
          <c:when test="${empty giftlistId}" >
            <%-- create action --%>
            <dsp:input type="hidden" value="${successUrl}"
              bean="${giftlistFormHandlerPath}.saveGiftlistSuccessURL"/>
            <dsp:input type="hidden" value="${url}"
              bean="${giftlistFormHandlerPath}.saveGiftlistErrorURL"/>
            <dsp:input type="hidden" value="" priority="-100" id="${formId}_createGiftlistAction" bean="${giftlistFormHandlerPath}.saveGiftlist"/>
          </c:when>
          <c:otherwise>
            <%-- update action --%>
            <dsp:input type="hidden" value="" priority="-100" id="${formId}_updateGiftlistAction" bean="${giftlistFormHandlerPath}.updateGiftlist"/>
            <dsp:input type="hidden" value="${successUrl}"
              bean="${giftlistFormHandlerPath}.updateGiftlistSuccessURL"/>
            <dsp:input type="hidden" value="${url}"
              bean="${giftlistFormHandlerPath}.updateGiftlistErrorURL"/> 
          </c:otherwise>
        </c:choose>      
  
        <c:choose>
          <c:when test="${not empty param.giftlistId}">
            <dsp:setvalue value="${param.giftlistId}"
             bean="GiftlistUIState.workingGiftlistId"/>
            <dsp:input type="hidden" value="${param.giftlistId}"
             priority="1000" bean="${giftlistFormHandlerPath}.giftlistId"/>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
          
        <dsp:droplet name="/atg/targeting/RepositoryLookup">
          <dsp:param name="elementName" value="giftlist"/>
          <dsp:param name="id" value="${giftlistId}"/>
          <dsp:param bean="/atg/commerce/gifts/Giftlists" name="repository"/>
          <dsp:oparam name="output">
            <dsp:tomap var="giftlistMap" param="giftlist"/>
          </dsp:oparam>
        </dsp:droplet>
                
        <c:if test="${not empty defaultPageFragment.URL}">
          <dsp:include src="${defaultPageFragment.URL}" otherContext="${defaultPageFragment.servletContext}">
            <dsp:param name="formId" value="${formId}" />
            <dsp:param name="giftlistMap" value="${giftlistMap}" />
            <dsp:param name="giftlistFormHandler" value="${giftlistFormHandlerPath}" />
            <dsp:param name="submitButtonId" value="saveChoice"/>
           </dsp:include> 
        </c:if>
        <c:if test="${not empty extendedPageFragment.URL}">
          <dsp:include src="${extendedPageFragment.URL}" otherContext="${extendedPageFragment.servletContext}">
            <dsp:param name="formId" value="${formId}" />
            <dsp:param name="giftlistMap" value="${giftlistMap}" />
            <dsp:param name="giftlistFormHandler" value="${giftlistFormHandlerPath}" />
            <dsp:param name="submitButtonId" value="saveChoice"/>
          </dsp:include>    
        </c:if>
        
        <div class="atg_commerce_csr_panelFooter">
          <input value="<fmt:message key='giftlists.create.save'/>" type="button" id="saveChoice" name="saveChoice" 
            onClick="atgSubmitPopup({url: '${url}', form: document.getElementById('${formId}'), popup: getEnclosingPopup('giftlistCreatePopup')});return false;" />
          <input value="<fmt:message key='giftlists.create.cancel'/>" type="button" 
            name="cancelChoice" onClick="hidePopupWithResults('giftlistCreatePopup', {result:'cancel'});return false;" />
         </div>
      </dsp:form>
    </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/gift/giftlistCreate.jsp#1 $$Change: 946917 $--%>
