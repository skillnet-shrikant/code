<%--
 Generic Navigation Item Renderer
 This file delegates to the search, context, and action sub-components respectively
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navItem.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<dspel:getvalueof var="navItem" param="navItem"/>

<%-- Render structure of nav item --%>
<div id="navItem_<c:out value="${navItem.id}"/>" class="gcn_btn">
  <div class="gcn_btn_core">
    <div class="gcn_btn_coreInner">
      
    <%-- Check for nav search component. If found, pass the nav search component into the nav search JSP for rendering --%>
    <c:if test="${not empty navItem.navSearch}">			  
      <dspel:include src="/include/navigation/navSearch.jsp">
        <dspel:param name="navItemId" param="navItem.id" />
        <dspel:param name="navSearch" param="navItem.navSearch" />
      </dspel:include>
    </c:if>	
    
    <%-- Check for nav context component. If found, pass the nav context component into the nav context JSP for rendering --%>
    <c:if test="${not empty navItem.navContext}">			  
      <dspel:include src="/include/navigation/navContext.jsp">
        <dspel:param name="navItemId" param="navItem.id" />
        <dspel:param name="navContext" param="navItem.navContext" />
      </dspel:include>
    </c:if>	
    
    <%-- Check for nav action container component. If found, pass the nav action container component into the nav action container JSP for rendering --%>
    <c:if test="${not empty navItem.navActionContainer}">			  
      <dspel:include src="/include/navigation/navActionContainer.jsp">
        <dspel:param name="navItemId" param="navItem.id" />
        <dspel:param name="navActionContainer" param="navItem.navActionContainer" />
      </dspel:include>
    </c:if>
    </div>
  </div>
  
  <%-- Render the hover label of the nav item --%>
  <div class="gcn_btn_label"><c:out value="${navItem.label}"/></div>
  
  <%-- Check for nav action items. If found, pass the nav action items component into the nav action items JSP for rendering --%>
  <dspel:getvalueof var="navActions" param="navItem.navActionContainer.allActions"/>
  <c:if test="${not empty navActions}">
    <%-- Append the nav action container to the nav action identifier to ensure a unique DOM id --%>
    <div id="navActionPopup_<c:out value="${navItem.id}"/>" class="navActionMenu" style="margin-top:-15px"> 
      <dspel:include src="/include/navigation/navActionItems.jsp">
        <dspel:param name="navActions" param="navItem.navActionContainer.allActions" />
      </dspel:include>
    </div>
  </c:if>
  
</div>

<script type="text/javascript">
  dojo.addOnLoad(function () {
    var _domIdPopup = "navActionPopup_<c:out value="${navItem.id}"/>";
    var _domObjectPopup = document.getElementById(_domIdPopup);
     _domObjectPopup.onmouseout=checkMouseLocationPopup;
  });

  function checkMouseLocationPopup(e) {
  	// Browser-specifc event handling
  	var _event = e;
  	if (!e) {
  	  _event = window.event;
  	}
  	
  	// Ignore the mouse events for the <div> links in the nav action popup
  	var _target = (window.event) ? _event.srcElement : _event.target;
  	if (_target.nodeName == 'DIV') {
  	  return;
  	}
  	
  	// Check to see whether the mouse actually left the layer
  	var _relatedTarget = (_event.relatedTarget) ? _event.relatedTarget : _event.toElement;
  	if ((_relatedTarget != null) && (_relatedTarget.nodeName == 'A')) {
  	  return;
  	}
  	this.style.display="none";
  }
</script>


</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navItem.jsp#1 $$Change: 946917 $--%>
