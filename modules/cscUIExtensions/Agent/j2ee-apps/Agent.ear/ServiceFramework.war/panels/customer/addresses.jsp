<%--
 This page defines the Customer Addresses Panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/addresses.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

    <dspel:importbean var="addressBook"
      bean="/atg/svc/agent/profile/AddressBook"/>
    <dspel:importbean var="profile"
      bean="/atg/userprofiling/ServiceCustomerProfile"/>

<script type="text/javascript">
  if (!dijit.byId("addressPopup")) {
    new dojox.Dialog({id: "addressPopup",
                      cacheContent: "false",
                      executeScripts: "true",
                      scriptHasHooks: "true",
                      style: "display:none;"
                    });
  }
  
  if (!dijit.byId("addressDeletePopup")) {
    new dojox.Dialog({id: "addressDeletePopup",
                      cacheContent: "false",
                      executeScripts: "true",
                      scriptHasHooks: "true",
                      style: "display:none;"
                    });
  }
  
</script>
  <fmt:message key="customer.addresses.addAddress.label" var="addAddressLabel"/>
  <div id="atg_service_customerinfo_addresses_subPanel" class="atg_svc_subPanel">
    <div class="atg_svc_subPanelHeader" >       
        <ul class="atg_svc_panelToolBar">
          <li class="atg_svc_header">
            <h4 id="atg_commerce_csr_customerinfo_addresses"><fmt:message key="customer.addresses.title.label"/> </h4>
          </li>
          <dspel:getvalueof var="mode" param="mode"/>
          <c:if test="${mode == 'edit' and addressBook.canAcceptNewAddress}">         
            <li class="atg_svc_last">          
              <svc-ui:frameworkPopupUrl var="addressEdit"
                value="/include/addresses/addressEditor.jsp"
                context="/agent"
                windowId="${windowId}"/>
              <a href="#"
                class="atg_svc_popupLink"
                onClick="showPopupWithResults({
                  popupPaneId: 'addressPopup',
                  title: '${addAddressLabel}',
                  url: '${addressEdit}',
                  onClose: function( args ) {
                    if ( args.result == 'save' ) {
                      atgSubmitAction({
                        panels : ['customerInformationPanel'],
                        panelStack : ['customerPanels','globalPanels'],
                        form : dojo.byId('transformForm')
                      });
                    }
                  }});
                  return false;">
                <fmt:message key="customer.addresses.addAddress.label"/>
              </a>              
            </li>
          </c:if>
         </ul>
      </div>
      

    <c:set target="${addressBook}" property="profile" value="${profile}"/>

    <c:choose>
      <c:when test="${addressBook.containsNonBlankAddress}">
        <%-- Display default addresses first --%>
        <div class="atg_svc_addresses">
          <c:forEach var="ami" items="${addressBook.addressMetaInfos}">
            <c:if test="${not ami.value.blank}">
              <c:if test="${not empty ami.value.params.defaultOptions}">
                <div class="atg_svc_customerInfo_addresses atg_svc_iconD" title="<fmt:message key='customer.addresses.defaultAddress.label'/>">
                  <dspel:include src="/panels/customer/addressDisplay.jsp" otherContext="${UIConfig.contextRoot}">
                    <dspel:param name="addrMeta" value="${ami.value}"/>
                  </dspel:include>
                </div>
              </c:if>
            </c:if>
          </c:forEach>
        <%-- Display any non-default addresses --%>
          <c:forEach var="ami" items="${addressBook.addressMetaInfos}">
            <c:if test="${not ami.value.blank}">
              <c:if test="${empty ami.value.params.defaultOptions}">
                <div class="atg_svc_customerInfo_addresses">
                  <dspel:include src="/panels/customer/addressDisplay.jsp" otherContext="${UIConfig.contextRoot}">
                    <dspel:param name="addrMeta" value="${ami.value}"/>
                  </dspel:include>
                </div>
              </c:if>
            </c:if>
          </c:forEach>
        </div>
      </c:when>
      <c:otherwise>
        <div class="emptyLabel">
          <fmt:message key="customer.addresses.noAddresses.label"/>
        </div>
      </c:otherwise>
    </c:choose>
    </div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/addresses.jsp#1 $$Change: 946917 $--%>
