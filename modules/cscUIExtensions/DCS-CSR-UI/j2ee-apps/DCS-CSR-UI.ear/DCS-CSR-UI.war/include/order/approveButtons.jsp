<%--
This page renderst eh Approve and Reject Buttons for an order if the agent has the access right and the order has an approval
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/approveButtons.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  
    <dsp:getvalueof var="order" param="order"/>
    
    
    <dsp:droplet name="/atg/commerce/custsvc/approvals/order/IsOrderPendingApprovalDroplet">
      <dsp:param name="orderId" value="${order.id}"/>
      <dsp:oparam name="true">
        <dsp:getvalueof var="element" param="element"/>
        
          <dsp:tomap var="approvalMap" param="element"/>
        
          <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
          
          <svc-ui:frameworkUrl var="errorURL" panelStacks=""/>
          <svc-ui:frameworkUrl var="successURL" panelStacks="globalPanels,cmcExistingOrderPS" contentHeader="true"/>
      
          <li class="atg_commerce_csr_last">
          
            <input type="button" onclick="submitApproval({approvalId: '${approvalMap.repositoryId}', customerId: '${approvalMap.customerId}', successURL: '${successURL}', errorURL: '${errorURL}'});return false;" value="<fmt:message key='approvals.approve'/>"/>
          
            <input type="button" onclick="submitReject({approvalId: '${approvalMap.repositoryId}', customerId: '${approvalMap.customerId}', successURL: '${successURL}', errorURL: '${errorURL}'});return false;" value="<fmt:message key='approvals.reject'/>"/>
            
          </li>
          
          </dsp:layeredBundle>
        	
        <dsp:include src="/include/approvals/approvalsAction.jsp" otherContext="${CSRConfigurator.contextRoot}"/>
      </dsp:oparam>
    </dsp:droplet>
    
      
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/approveButtons.jsp#1 $$Change: 946917 $--%>