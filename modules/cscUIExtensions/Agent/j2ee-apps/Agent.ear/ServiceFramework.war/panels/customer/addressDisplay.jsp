<%--
 Display an individual address

 @param addrMeta The AddressMetaInfo for the addresss

 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/addressDisplay.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <dspel:getvalueof var="addrMeta" param="addrMeta"/>
    <c:set var="addressId" 
      value="${addrMeta.addressRepositoryItem.repositoryId}"/>
    <c:if test="${not empty addrMeta.params.defaultOptions}">
      <%-- 
        If there's a defaultInfo in the meta's params, then this
        address represents one or more default addresses.
      --%>
      <div class="atg_svc_iconSet">
        <c:forEach var="defaultType" items="${addrMeta.params.defaultOptions}">
          <ul>
            <c:if test="${not empty defaultType.value.defaultSymbolRenderer}">
              <li>
                <dspel:include 
                  otherContext="${defaultType.value.defaultSymbolRendererContext}"
                  src="${defaultType.value.defaultSymbolRenderer}">
                  <dspel:param name="addrMeta" value="${addrMeta}" />
                  <dspel:param name="defaultType" value="${defaultType}" />
                </dspel:include>
              </li>
            </c:if>
          </ul>
        </c:forEach>
      </div>
    </c:if>
    <div class="atg_svc_addressWrapper">
    <dl class="atg_svc_shipAddress">
	  <dd>
		<c:forEach var="n" end="1" items="${addrMeta.nicknames}">
                  <c:set var="nickname" value="${n}"/>
        </c:forEach>
        ${fn:escapeXml(nickname)}
      </dd>
      <dd>
        <c:choose>
          <c:when test="${!empty addrMeta.address.middleName }">
            <fmt:message key="customer.name.prefix.first.middle.last.suffix"> 
              <fmt:param value="${fn:escapeXml(addrMeta.address.prefix)}" />
              <fmt:param value="${fn:escapeXml(addrMeta.address.firstName)}" />
              <fmt:param value="${fn:escapeXml(addrMeta.address.middleName)}" />
              <fmt:param value="${fn:escapeXml(addrMeta.address.lastName)}" />
              <fmt:param value="${fn:escapeXml(addrMeta.address.suffix)}" />
            </fmt:message>
          </c:when>
          <c:otherwise>
            <fmt:message key="customer.name.prefix.first.last.suffix">
              <fmt:param value="${fn:escapeXml(addrMeta.address.prefix)}" />
              <fmt:param value="${fn:escapeXml(addrMeta.address.firstName)}" />
              <fmt:param value="${fn:escapeXml(addrMeta.address.lastName)}" />
              <fmt:param value="${fn:escapeXml(addrMeta.address.suffix)}" />
            </fmt:message>
          </c:otherwise>
        </c:choose>
      </dd>
      <dd>
        ${fn:escapeXml(addrMeta.address.address1)}
      </dd>
      <dd>
        ${fn:escapeXml(addrMeta.address.address2)}
      </dd>
      <dd>
        ${fn:escapeXml(addrMeta.address.address3)}
      </dd>
      <dd>
        ${fn:escapeXml(addrMeta.address.city)} 
        ${fn:escapeXml(addrMeta.address.state)}
        ${fn:escapeXml(addrMeta.address.postalCode)}
      </dd>
      <dd>
        <dspel:getvalueof var="places" bean="/atg/core/i18n/CountryList.places"/>
        <c:forEach var="country" items="${places}">
          <c:if test="${country.code == addrMeta.address.country}">
            ${country.displayName}
          </c:if>
        </c:forEach>
      </dd>
      <dd>
        ${fn:escapeXml(addrMeta.address.phoneNumber)}
      </dd>
    </dl>
    <dspel:droplet name="/atg/dynamo/droplet/Switch">
    <dspel:param param="mode" name="value"/>
    <dspel:oparam name="edit">
    <ul class="atg_svc_shipAddressControls default" >
      <li>
        <svc-ui:frameworkPopupUrl var="addressEdit"
          value="/include/addresses/addressEditor.jsp"
          context="/agent"
          addressId="${addressId}"
          windowId="${windowId}"/>
        <a href="#" class="atg_tableIcon atg_propertyEdit" 
          title="<fmt:message key='address.edit.mouseover'/>"
          onClick="showPopupWithResults({
            popupPaneId: 'addressPopup',
            url: '${addressEdit}',
            title: '<fmt:message key="address.editor.editTitle"/>',
            onClose: function( args ) {
              if ( args.result == 'save' ) {
                atgSubmitAction({
                  panels : ['customerInformationPanel'],
                  panelStack : ['customerPanels','globalPanels'],
                  form : dojo.byId('transformForm')
                });
              }
            }
          });
          return false;">
          <fmt:message key="address.edit.label"/>
        </a>
      </li>
      <c:if test="${addrMeta.deletable}">
        <svc-ui:frameworkPopupUrl var="addressDelete"
          value="/include/addresses/addressDeleter.jsp"
          context="/agent"
          addressId="${addressId}"
          windowId="${windowId}"/>
        <li>
          <a href="#" class="atg_tableIcon atg_propertyDelete" 
            title="<fmt:message key='address.delete.mouseover'/>"
            onClick="showPopupWithResults({
              popupPaneId: 'addressDeletePopup',
              url: '${addressDelete}',
              title: '<fmt:message key="address.deleter.deleteAddress"/>',
              onClose: function( args ) {
                if ( args.result == 'delete' ) {
                  atgSubmitAction({
                    panels : ['customerInformationPanel'],
                    panelStack : ['customerPanels','globalPanels'],
                    form : dojo.byId('transformForm')
                  });
                }
              }
            }); return false;">
            <fmt:message key="address.delete.label"/>
          </a>
        </li>
      </c:if>
    </ul>      
    </dspel:oparam>
    </dspel:droplet>
  </dspel:layeredBundle>
</div>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/addressDisplay.jsp#1 $$Change: 946917 $--%>
