 <%--

 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/script/resources.js.jsp#3 $$Change: 1174351 $
 @updated $DateTime: 2015/06/18 03:41:11 $$Author: rkalva $

/*<ORACLECOPYRIGHT>
 * Copyright </A> &copy; 1994, 2015, Oracle and/or its affiliates. All rights reserved.
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 * UNIX is a registered trademark of The Open Group.
 *
 * This software and related documentation are provided under a license agreement
 * containing restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use, copy,
 * reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish,
 * or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 *
 * The information contained herein is subject to change without notice and is not warranted to be error-free.
 * If you find any errors, please report them to us in writing.
 *
 * U.S. GOVERNMENT RIGHTS Programs, software, databases, and related documentation and technical data delivered to U.S.
 * Government customers are "commercial computer software" or "commercial technical data" pursuant to the applicable
 * Federal Acquisition Regulation and agency-specific supplemental regulations.
 * As such, the use, duplication, disclosure, modification, and adaptation shall be subject to the restrictions and
 * license terms set forth in the applicable Government contract, and, to the extent applicable by the terms of the
 * Government contract, the additional rights set forth in FAR 52.227-19, Commercial Computer Software License
 * (December 2007). Oracle America, Inc., 500 Oracle Parkway, Redwood City, CA 94065.
 *
 * This software or hardware is developed for general use in a variety of information management applications.
 * It is not developed or intended for use in any inherently dangerous applications, including applications that
 * may create a risk of personal injury. If you use this software or hardware in dangerous applications,
 * then you shall be responsible to take all appropriate fail-safe, backup, redundancy,
 * and other measures to ensure its safe use. Oracle Corporation and its affiliates disclaim any liability for any
 * damages caused by use of this software or hardware in dangerous applications.
 *
 * This software or hardware and documentation may provide access to or information on content,
 * products, and services from third parties. Oracle Corporation and its affiliates are not responsible for and
 * expressly disclaim all warranties of any kind with respect to third-party content, products, and services.
 * Oracle Corporation and its affiliates will not be responsible for any loss, costs,
 * or damages incurred due to your access to or use of third-party content, products, or services.
 </ORACLECOPYRIGHT>*/

--%>



<%@ include file="/include/top.jspf" %>

<dspel:page  xml="false">
<dspel:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
<dspel:importbean bean="/atg/svc/repository/service/StateHolderService"
                  scope="request"
                  var="stateHolder"/>

<svc-ui:httpCacheHeader/>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <dspel:droplet name="HasAccessRight">
    <dspel:param name="accessRight" value="Allowed to Set Statement-Level security"/>
    <dspel:oparam name="accessGranted">
      <c:set var="statementSecurity" value="true"/>
    </dspel:oparam>
    <dspel:oparam name="accessDenied">
      <c:set var="statementSecurity" value="false"/>
    </dspel:oparam>
  </dspel:droplet>

    var aResources            = new Array();

    function getResource(key)
    {
      return aResources[key];
    }

    aResources["framework.cellIdParameterName"]                = "<c:out value='${framework.cellIdParameterName}'/>";
    aResources["framework.panelIdParameterName"]               = "<c:out value='${framework.panelIdParameterName}'/>";
    aResources["imgArrowDown"]                                 = "<c:out value='${imageLocation}'/>/iconcatalog/14x14/bullets/arrowdown.gif";
    aResources["imgArrowRight"]                                = "<c:out value='${imageLocation}'/>/iconcatalog/14x14/bullets/arrowright.gif";
    aResources["imgAlertSmallRed"]                             = "<c:out value='${imageLocation}'/>/iconcatalog/global/icon_alertSmallRed.gif";
    aResources["imgAlertSmall"]                                = "<c:out value='${imageLocation}'/>/iconcatalog/global/icon_alertSmall.gif";
    aResources["imgAlert"]                                     = "<c:out value='${imageLocation}'/>/icons/icon_alert.gif";
    aResources["imgBulletHover"]                               = "<c:out value='${imageLocation}'/>/iconcatalog/14x14/bullets/icon_bulletHover.gif";
    aResources["imgBulletOpen"]                                = "<c:out value='${imageLocation}'/>/iconcatalog/14x14/bullets/icon_bulletOpen.gif";
    aResources["imgBulletOff"]                                 = "<c:out value='${imageLocation}'/>/iconcatalog/14x14/bullets/icon_bulletOff.gif";
    aResources["securityImage"]                                = "<c:out value='${imageLocation}'/>/iconcatalog/21x21/wsywig_inserts/icon_personalize_wyswig.gif";
    aResources["deleteImage"]                                  = "<c:out value='${imageLocation}'/>/iconcatalog/21x21/table_icons/icon_delete.gif";
    aResources["popupURL"]                                     = "<c:out value='${UIConfig.contextRoot}/script/popup/popup.jsp?${stateHolder.windowIdParameterName}=${windowId}'/>";
    aResources["calanderURL"]                                  = "<c:url context='/CAF' value='/images/calendar/calendar.gif'/>";
    <fmt:message key='response.error.date.range.invalid' var="resourceString"/>
    aResources["response.error.date.range.invalid"]            = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='response.error.date.range.invalid' var="response.error.header"/>
    aResources["response.error.header"]                        = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='response.error.search' var="resourceString"/>
    aResources["response.error.search"]                        = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='response.error.search.text.empty' var="resourceString"/>
    aResources["response.error.search.text.empty"]             = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='response.error.no.site.selected' var="resourceString"/>
    aResources["response.error.no.site.selected"]              = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";

    // Keyboard popup help dialog
    <fmt:message key='keyboard.popup.shortcut' var="resourceString"/>
    aResources["keyboard.popup.shortcut"]                      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.popup.name' var="resourceString"/>
    aResources["keyboard.popup.name"]                          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.popup.description' var="resourceString"/>
    aResources["keyboard.popup.description"]                   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.popup.area' var="resourceString"/>
    aResources["keyboard.popup.area"]                          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    
    //Spell checker
    <fmt:message key='editor.spellcheck.none' var="resourceString"/>
    aResources["editor.spellcheck.none"]                       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='editor.spellcheck' var="resourceString"/>
    aResources["editor.spellcheck"]                            = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='editor.spellcheckignore' var="resourceString"/>
    aResources["editor.spellcheckignore"]                      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='editor.spellcheckignoreall' var="resourceString"/>
    aResources["editor.spellcheckignoreall"]                   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    

    // Keyboard shortcut keys for Service
    <fmt:message key='keyboard.service.help.name' var="resourceString"/>
    aResources["keyboard.service.help.name"]                   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.help.description' var="resourceString"/>
    aResources["keyboard.service.help.description"]            = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.nextTopLevel.name' var="resourceString"/>
    aResources["keyboard.service.nextTopLevel.name"]           = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.nextTopLevel.description' var="resourceString"/>
    aResources["keyboard.service.nextTopLevel.description"]    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.previousTopLevel.name' var="resourceString"/>
    aResources["keyboard.service.previousTopLevel.name"]       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.previousTopLevel.description' var="resourceString"/>
    aResources["keyboard.service.previousTopLevel.description"]= "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.nextPanel.name' var="resourceString"/>
    aResources["keyboard.service.nextPanel.name"]              = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.nextPanel.description' var="resourceString"/>
    aResources["keyboard.service.nextPanel.description"]       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.previousPanel.name' var="resourceString"/>
    aResources["keyboard.service.previousPanel.name"]          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.previousPanel.description' var="resourceString"/>
    aResources["keyboard.service.previousPanel.description"]   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.respondTab.name' var="resourceString"/>
    aResources["keyboard.service.respondTab.name"]             = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.respondTab.description' var="resourceString"/>
    aResources["keyboard.service.respondTab.description"]      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.ticketsTab.name' var="resourceString"/>
    aResources["keyboard.service.ticketsTab.name"]             = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.ticketsTab.description' var="resourceString"/>
    aResources["keyboard.service.ticketsTab.description"]      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.customersTab.name' var="resourceString"/>
    aResources["keyboard.service.customersTab.name"]           = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.customersTab.description' var="resourceString"/>
    aResources["keyboard.service.customersTab.description"]    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.mixMaxGlobalContext.name' var="resourceString"/>
    aResources["keyboard.service.mixMaxGlobalContext.name"]    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.mixMaxGlobalContext.description' var="resourceString"/>
    aResources["keyboard.service.mixMaxGlobalContext.description"] = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.activeTickets.name' var="resourceString"/>
    aResources["keyboard.service.activeTickets.name"]          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.activeTickets.description' var="resourceString"/>
    aResources["keyboard.service.activeTickets.description"]   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.ticketDetails.name' var="resourceString"/>
    aResources["keyboard.service.ticketDetails.name"]          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.ticketDetails.description' var="resourceString"/>
    aResources["keyboard.service.ticketDetails.description"]   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.customerDetails.name' var="resourceString"/>
    aResources["keyboard.service.customerDetails.name"]        = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.customerDetails.description' var="resourceString"/>
    aResources["keyboard.service.customerDetails.description"] = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.addNote.name' var="resourceString"/>
    aResources["keyboard.service.addNote.name"]                = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.addNote.description' var="resourceString"/>
    aResources["keyboard.service.addNote.description"]         = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.addCallNote.name' var="resourceString"/>
    aResources["keyboard.service.addCallNote.name"]            = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.addCallNote.description' var="resourceString"/>
    aResources["keyboard.service.addCallNote.description"]     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.startCall.name' var="resourceString"/>
    aResources["keyboard.service.startCall.name"]              = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.startCall.description' var="resourceString"/>
    aResources["keyboard.service.startCall.description"]       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.endCall.name' var="resourceString"/>
    aResources["keyboard.service.endCall.name"]                = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.endCall.description' var="resourceString"/>
    aResources["keyboard.service.endCall.description"]         = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.endCallStartNew.name' var="resourceString"/>
    aResources["keyboard.service.endCallStartNew.name"]        = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.endCallStartNew.description' var="resourceString"/>
    aResources["keyboard.service.endCallStartNew.description"] = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.newProfle.name' var="resourceString"/>
    aResources["keyboard.service.newProfle.name"]              = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.newProfle.description' var="resourceString"/>
    aResources["keyboard.service.newProfle.description"]       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.searchProfile.name' var="resourceString"/>
    aResources["keyboard.service.searchProfile.name"]          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.searchProfile.description' var="resourceString"/>
    aResources["keyboard.service.searchProfile.description"]   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.customerInfo.name' var="resourceString"/>
    aResources["keyboard.service.customerInfo.name"]           = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.customerInfo.description' var="resourceString"/>
    aResources["keyboard.service.customerInfo.description"]    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.ticketSearch.name' var="resourceString"/>
    aResources["keyboard.service.ticketSearch.name"]           = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.ticketSearch.description' var="resourceString"/>
    aResources["keyboard.service.ticketSearch.description"]    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.dockUndockPanel.name' var="resourceString"/>
    aResources["keyboard.service.dockUndockPanel.name"]        = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.dockUndockPanel.description' var="resourceString"/>
    aResources["keyboard.service.dockUndockPanel.description"] = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.hideUtilities.name' var="resourceString"/>
    aResources["keyboard.service.hideUtilities.name"]          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.hideUtilities.description' var="resourceString"/>
    aResources["keyboard.service.hideUtilities.description"]   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.minimizeRestorePanel.name' var="resourceString"/>
    aResources["keyboard.service.minimizeRestorePanel.name"]   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.minimizeRestorePanel.description' var="resourceString"/>
    aResources["keyboard.service.minimizeRestorePanel.description"] = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.minimizeUtilities.name' var="resourceString"/>
    aResources["keyboard.service.minimizeUtilities.name"]      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.minimizeUtilities.description' var="resourceString"/>
    aResources["keyboard.service.minimizeUtilities.description"]= "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.openFirebug.name' var="resourceString"/>
    aResources["keyboard.service.openFirebug.name"]            = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.openFirebug.description' var="resourceString"/>
    aResources["keyboard.service.openFirebug.description"]     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";

    // Keyboard shortcut functional areas
    <fmt:message key='keyboard.area.workspace' var="resourceString"/>
    aResources["keyboard.area.workspace"]                      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.area.globalContext' var="resourceString"/>
    aResources["keyboard.area.globalContext"]                  = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.area.customer' var="resourceString"/>
    aResources["keyboard.area.customer"]                       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.area.ticketing' var="resourceString"/>
    aResources["keyboard.area.ticketing"]                      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";

    // Embedded Assistance (EA) help content for Service
    <fmt:message key='ea.service.helpContent.ea_service_customer_search' var="resourceString"/>
    aResources["ea.service.helpContent.ea_service_customer_search"]   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.service.helpContent.atg_arm_contentBrowserTitle' var="resourceString"/>
    aResources["ea.service.helpContent.atg_arm_contentBrowserTitle"]  = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.service.helpContent.atg_arm_linkedDocumentsTitle' var="resourceString"/>
    aResources["ea.service.helpContent.atg_arm_linkedDocumentsTitle"] = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.service.helpContent.atg_arm_addAttachment' var="resourceString"/>
    aResources["ea.service.helpContent.atg_arm_addAttachment"]        = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";

    // Embedded Assistance (EA) tooltips for Service"]
    <fmt:message key='ea.service.tooltip.ticketSave' var="resourceString"/>
    aResources["ea.service.tooltip.ticketSave"]                = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.service.tooltip.customerLink' var="resourceString"/>
    aResources["ea.service.tooltip.customerLink"]              = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";

    // Nav Items
    <fmt:message key='option.input.user.disable.message' var="resourceString"/>
    aResources["navitem.options.action.preferences.disabled"]  = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='capture.abandonSession.message' var="resourceString"/>
    aResources["navitem.options.action.preferences.enabled"]   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='utility.navigation.docs.url' var="resourceString"/>
    aResources["navitem.options.action.documentation.url"]     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='popup.activeTickets.title' var="resourceString"/>
    aResources["popup.activeTickets.title"]                    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    
     //Ticketing popup titles
    <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
    
    <fmt:message key='defer-ticket' var="resourceString"/>
    aResources["popup.defer-ticket.title"]                     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='escalate-ticket' var="resourceString"/>
    aResources["popup.escalate-ticket.title"]                  = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='close-ticket' var="resourceString"/>
    aResources["popup.close-ticket.title"]                     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='enter-note' var="resourceString"/>
    aResources["popup.enter-note.title"]                       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='add-call-note' var="resourceString"/>
    aResources["popup.add-call-note.title"]                    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='assign-ticket-to-agent' var="resourceString"/>
    aResources["popup.assign-ticket-to-agent.title"]           = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='associate-ticket' var="resourceString"/>
    aResources["popup.associate-ticket.title"]                 = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='close-as-duplicate-ticket' var="resourceString"/>
    aResources["popup.close-as-duplicate-ticket.title"]        = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='release-ticket' var="resourceString"/>
    aResources["popup.release-ticket.title"]                   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='send-to-group' var="resourceString"/>
    aResources["popup.send-to-group.title"]                    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    
    </dspel:layeredBundle>
 </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/script/resources.js.jsp#3 $$Change: 1174351 $--%>
