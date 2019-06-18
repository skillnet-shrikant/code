<%--
 Column Renderer for columns in the approvals panel that relate to appeasements.
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/appeasementColumnRenderer.jsp#1 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%><%@ include file="/include/top.jspf"%>
<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />
  <dsp:importbean bean="/atg/commerce/custsvc/appeasement/AppeasementItemLookup" />

  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

    <dsp:getvalueof var="field" param="field"/>
    <dsp:getvalueof var="colIndex" param="colIndex"/>
    <dsp:getvalueof var="approvalItemMap" param="approvalItemMap"/>
    <dsp:getvalueof var="repositoryId" param="repositoryId"/>
    <dsp:getvalueof var="profileItemMap" param="profileItemMap"/>
    <dsp:getvalueof var="agentItemMap" param="agentItemMap"/>
    <dsp:getvalueof var="siteConfigMap" param="siteConfigMap"/>
    <dsp:getvalueof var="currencyCode" param="currencyCode"/>     
  
    <c:choose>
  
      <c:when test="${field == 'appeasementId'}">
        "appeasementId":"${approvalItemMap.appeasementId}"
      </c:when>
      
      <c:when test="${field == 'orderId'}">
        <dsp:droplet name="AppeasementItemLookup">
          <dsp:param name="id" value="${approvalItemMap.appeasementId}" />
          <dsp:oparam name="output">
            <dsp:tomap var="appeasement" param="element" />
            "orderId":"<a class=\"blueU\" onclick=\"atg.commerce.csr.order.viewExistingOrder(\'${appeasement.order.id}\',\'PENDING_APPROVAL\');return false;\" title=\"Order ID\" href=\"#\">${appeasement.order.id}</a>"
          </dsp:oparam>
        </dsp:droplet>
      </c:when>
    
    
      <c:when test="${field == 'appeasementType'}">
        <dsp:droplet name="AppeasementItemLookup">
          <dsp:param name="id" value="${approvalItemMap.appeasementId}" />
          <dsp:oparam name="output">
            <dsp:tomap var="appeasement" param="element" />
            "appeasementType":"<fmt:message key="approvals.appeasementType.${appeasement.type}"/>"
          </dsp:oparam>
        </dsp:droplet>
      </c:when>
    
      <c:when test="${field == 'appeasementAmount'}">
        <csr:formatNumber var="result" value="${approvalItemMap.appeasementTotal}" type="currency" currencyCode="${currencyCode}"/>
        "appeasementAmount":"${result}"
      </c:when>
      
      <c:when test="${field == 'approveLink'}">
        <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
        <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels,cmcApprovalsPS" contentHeader="true"/>
        "approveLink":"<a href=\"#\" class=\"blueU\" onclick=\"submitAppeasementApproval({approvalId: \'${repositoryId}\', customerId: \'${profileItemMap.id}\', successURL: \'${successURL}\', errorURL: \'${errorURL}\'});return false;\" title=\"Accept\"><fmt:message key='approvals.approve'/></a>"
       
      </c:when>
      
      <c:when test="${field == 'rejectLink'}">
        <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
        <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels,cmcApprovalsPS" contentHeader="true"/>
        "rejectLink":"<a href=\"#\" class=\"blueU\" onclick=\"submitAppeasementReject({approvalId: \'${repositoryId}\', customerId: \'${profileItemMap.id}\', successURL: \'${successURL}\', errorURL: \'${errorURL}\'});return false;\" title=\"Reject\"><fmt:message key='approvals.reject'/></a>"
        
      </c:when>
    
      <c:otherwise>
      </c:otherwise>
    </c:choose>
    
  </dsp:layeredBundle>

</dsp:page>
<%-- Version: $Change: 1179550 $$DateTime: 2015/07/10 11:58:13 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/appeasementColumnRenderer.jsp#1 $$Change: 1179550 $--%>
