<%--
Approval Column Renderer
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/columnRenderer.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%><%@ include file="/include/top.jspf"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />

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
    <c:when test="${!empty siteConfigMap.favicon}">
      <c:set var="siteIconIMG" value="<img src='${siteConfigMap.favicon}' title='${siteConfigMap.name}'>"/>
    </c:when>
    <c:otherwise>
      <c:set var="siteIconIMG" value="<img src='${CSRConfigurator.defaultSiteIconURL}'>"/>
    </c:otherwise>
  </c:choose>
    
  <c:choose>
  <c:when test="${field == 'siteIcon'}">
    "siteIcon":"${siteIconIMG}"
  </c:when>
  
  <c:when test="${field == 'orderId'}">
    "orderId":"<a class=\"blueU\" onclick=\"atg.commerce.csr.order.viewExistingOrder(\'${approvalItemMap.orderId}\',\'PENDING_APPROVAL\');return false;\" title=\"Order ID\" href=\"#\">${approvalItemMap.orderId}</a>"
  </c:when>
  
  <c:when test="${field == 'creationDate'}">
      <web-ui:formatDate type="both" value="${approvalItemMap.creationDate}" dateStyle="short" timeStyle="short" var="creationDate"/>
      "creationDate":"${creationDate}"
  </c:when>
  

  <c:when test="${field == 'name'}">
    <c:choose>
      <c:when test="${!empty profileItemMap.lastName || !empty profileItemMap.firstName}">
        
        <fmt:message var="name" key="lastname-firstname">
          <fmt:param value="${profileItemMap.lastName}"/>
          <fmt:param value="${profileItemMap.firstName}"/>
        </fmt:message>
  
        "name":"${fn:escapeXml(name)}"
      </c:when>
      <c:otherwise>
        "name":"<fmt:message key='no-name'/>"
      </c:otherwise>
    </c:choose>
  </c:when>
  
  <c:when test="${field == 'orderTotal'}">
    <csr:formatNumber var="result" value="${approvalItemMap.orderTotal}" type="currency" currencyCode="${currencyCode}"/>
    "orderTotal":"${result}"
  </c:when>
  
  <c:when test="${field == 'appeasements'}">
    <csr:formatNumber var="result" value="${approvalItemMap.appeasementTotal}" type="currency" currencyCode="${currencyCode}"/>
    "appeasements":"${result}"
  </c:when>
  
  <c:when test="${field == 'originator'}">
        "originator":"${fn:escapeXml(agentItemMap.firstName)} ${fn:escapeXml(agentItemMap.lastName)}"
  </c:when>

  <c:when test="${field == 'approveLink'}">
    <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
    <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels,cmcApprovalsPS" contentHeader="true"/>
    "approveLink":"<a href=\"#\" class=\"blueU\" onclick=\"submitApproval({approvalId: \'${repositoryId}\', customerId: \'${profileItemMap.id}\', successURL: \'${successURL}\', errorURL: \'${errorURL}\'});return false;\" title=\"Accept\"><fmt:message key='approvals.approve'/></a>"
   
  </c:when>
  
  <c:when test="${field == 'rejectLink'}">
      <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
      <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels,cmcApprovalsPS" contentHeader="true"/>
      "rejectLink":"<a href=\"#\" class=\"blueU\" onclick=\"submitReject({approvalId: \'${repositoryId}\', customerId: \'${profileItemMap.id}\', successURL: \'${successURL}\', errorURL: \'${errorURL}\'});return false;\" title=\"Reject\"><fmt:message key='approvals.reject'/></a>"
    
  </c:when>
  
  <c:otherwise>
  </c:otherwise>
  </c:choose>
  
</dsp:layeredBundle>

</dsp:page>
<%-- Version: $Change: 1179550 $$DateTime: 2015/07/10 11:58:13 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/approvals/columnRenderer.jsp#2 $$Change: 1179550 $--%>
