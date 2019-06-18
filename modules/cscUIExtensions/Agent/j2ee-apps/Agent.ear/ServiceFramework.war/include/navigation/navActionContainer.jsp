<%--
 Generic Navigation Action Sub-Component Renderer
 This file renders the action sub-component of the nav item
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navActionContainer.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<dspel:importbean bean="/atg/dynamo/droplet/ForEach" />
<dspel:getvalueof var="navActionContainer" param="navActionContainer"/>
<dspel:getvalueof var="navActions" param="navItem.navActionContainer.allActions"/>
<dspel:getvalueof var="navItemId" param="navItemId"/>

  <%-- Render the nav action container structure  --%>
  <c:choose>
    <c:when test="${fn:length(navActions) == 1 && not empty navActionContainer.label}">
      
      <%-- There will only be a single item to render, based on the length tested above --%>
      <dspel:droplet name="ForEach">
        <dspel:param name="array" param="navItem.navActionContainer.allActions"/>
        <dspel:getvalueof var="navAction" param="element"/>
      
        <%-- Extract nav action and render it within list --%>
        <dspel:oparam name="output">
          <a class="gcn_btn_action" id="navActionContainer_<c:out value="${navItemId}"/>" href="#" title="<c:out value="${navActionContainer.toolTip}"/>" onclick="<c:out value="${navAction.javaScriptFunctionCall}"/>"><div><c:out value="${navActionContainer.label}"/></div></a>
        </dspel:oparam>
      </dspel:droplet>
    </c:when>
    <c:otherwise>
      <a id="navActionContainer_<c:out value="${navItemId}"/>" href="#" onclick="showNavActionPopupMenu('<c:out value="${navItemId}"/>')" class="gcn_btn_action" title="<c:out value="${navActionContainer.toolTip}"/>"><div><c:out value="${navActionContainer.label}"/><span></span></div></a>

    </c:otherwise>
  </c:choose>

<%-- JavaScript to control the popup and positioning of the action menu  --%>
<script type="text/javascript">
  // Positioning of the nav item popup, based on the location of the container and proximity to the left margin
  function showNavActionPopupMenu(navItemId) {
    var navActionContainerObject = document.getElementById("navActionContainer_"+navItemId);
    var navActionPopupMenu = document.getElementById("navActionPopup_"+navItemId);

    if (navActionContainerObject != null && navActionPopupMenu != null) {
      if (navActionPopupMenu.style.display=="block") {
        navActionPopupMenu.style.display="none";
      }
      else {
        navActionPopupMenu.style.display="block";
        var offsetRight = 14;
        var containerRectangle = navActionContainerObject.getBoundingClientRect();
        var popupRectangle = navActionPopupMenu.getBoundingClientRect();
        var popupWidth = popupRectangle.right - popupRectangle.left;
        var leftPixelValue = parseInt(containerRectangle.right - popupWidth + offsetRight);
        
        if (leftPixelValue < 3) {
          navActionPopupMenu.style.left = "3px";
        }
        else {
          navActionPopupMenu.style.left = leftPixelValue+"px";
        }
      }
    }
  }


  dojo.addOnLoad(function () {
    var _domIdContainer = "navActionContainer_<c:out value="${navItemId}"/>";
    var _domObjectContainer = document.getElementById(_domIdContainer);
    if (_domObjectContainer != null) {
       _domObjectContainer.onmouseout=showHideNavActionPopup_<c:out value="${navItemId}"/>;
    }
  });
  
  // This function is triggered when the mouse leaves the nav container area and tests to see whether the nav popup should
  // be hidden or kept visible. Because we already have code to hide the nav action popup when a user mouses out of the popup, this
  // function specifically addresses the behavior of mousing out of the container without first entering the popup.
  function showHideNavActionPopup_<c:out value="${navItemId}"/>(e) {
  
    // If the mouse has left the NavActionContainer, determine whether the NavAction popup is displayed, and if so, 
    // calculate whether the mouse has moved off both elements, thereby triggering the closing of the popup
    var _navActionPopup = document.getElementById("navActionPopup_<c:out value="${navItemId}"/>");
    var _navItemContainer = document.getElementById("navActionContainer_<c:out value="${navItemId}"/>");

    if ((_navActionPopup != null) && (_navActionPopup.style.display=="block")) {
      var _navItemRectangle = _navItemContainer.getBoundingClientRect();
      var _navPopupRectangle = _navActionPopup.getBoundingClientRect();
      
      var _event = e;
    	if (!e) {
    	  _event = window.event;
    	}

      if (_event.pageY || _event.pageX) {
    		_mouseY = _event.pageY;
    		_mouseX = _event.pageX;
    	}
	    else if (_event.clientY || _event.clientX) {
		    _mouseY = _event.clientY + document.documentElement.scrollTop + document.body.scrollTop;
		    _mouseX = _event.clientX + document.documentElement.scrollLeft + document.body.scrollLeft;
	    }
	    
      //console.debug("_mouseX=" + _mouseX + ", _mouseY=" + _mouseY + " | C-Top=" + _navItemRectangle.top + ", C-Bottom=" + _navItemRectangle.bottom + ", C-Left=" + _navItemRectangle.left + ", C-Right=" + _navItemRectangle.right + " | P-Top=" + _navPopupRectangle.top + ", P-Bottom=" + _navPopupRectangle.bottom + ", P-Left=" + _navPopupRectangle.left + ", P-Right=" + _navPopupRectangle.right);
      var _topPadding = 11;
      var _leftPadding = 20;
      
      // Target any smaller nav design so that the mouse sensitivity is greater
      if (_navPopupRectangle.top - _navPopupRectangle.bottom < 21) {
        if (_mouseX > _navItemRectangle.right+8) {
          _navActionPopup.style.display="none";
        }
        
        if (_mouseY > _navPopupRectangle.top-_topPadding) {
          return;
        }
      }

      if (_mouseX > _navItemRectangle.right) {
        _navActionPopup.style.display="none";
      }


      if (_mouseY <  _navItemRectangle.top) {
        _navActionPopup.style.display="none";
      }
      
      
      if ((_mouseX < (_navItemRectangle.left+_leftPadding)) && (_mouseY <  _navItemRectangle.bottom)) {
       _navActionPopup.style.display="none";
      }
    }

  }

</script>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navActionContainer.jsp#1 $$Change: 946917 $--%>