 <%--

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/script/resources.js.jsp#4 $$Change: 1179550 $
 @updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $

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

<dsp:page  xml="false">
<svc-ui:httpCacheHeader/>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  
  <web-ui:formatNumber value='1.99' var="example" scope="request"/>
  
    //aResources is created in Service.Framework.Agent
    <fmt:message key='keyboard.service.commerceTab.name' var="resourceString"/>
    aResources["keyboard.service.commerceTab.name"]            = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.service.commerceTab.description' var="resourceString"/>
    aResources["keyboard.service.commerceTab.description"]     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";

    <fmt:message key='keyboard.csc.saveOrder.name' var="resourceString"/>
    aResources["keyboard.csc.saveOrder.name"]                  = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.saveOrder.description' var="resourceString"/>
    aResources["keyboard.csc.saveOrder.description"]           = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.cancelOrder.name' var="resourceString"/>
    aResources["keyboard.csc.cancelOrder.name"]                = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.cancelOrder.description' var="resourceString"/>
    aResources["keyboard.csc.cancelOrder.description"]         = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.viewOrder.name' var="resourceString"/>
    aResources["keyboard.csc.viewOrder.name"]                  = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.viewOrder.description' var="resourceString"/>
    aResources["keyboard.csc.viewOrder.description"]           = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.createOrder.name' var="resourceString"/>
    aResources["keyboard.csc.createOrder.name"]                = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.createOrder.description' var="resourceString"/>
    aResources["keyboard.csc.createOrder.description"]         = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.searchOrder.name' var="resourceString"/>
    aResources["keyboard.csc.searchOrder.name"]                = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.searchOrder.description' var="resourceString"/>
    aResources["keyboard.csc.searchOrder.description"]         = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.orderDetails.name' var="resourceString"/>
    aResources["keyboard.csc.orderDetails.name"]               = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.orderDetails.description' var="resourceString"/>
    aResources["keyboard.csc.orderDetails.description"]        = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.productCatalog.name' var="resourceString"/>
    aResources["keyboard.csc.productCatalog.name"]             = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.productCatalog.description' var="resourceString"/>
    aResources["keyboard.csc.productCatalog.description"]      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.productSearch.name' var="resourceString"/>
    aResources["keyboard.csc.productSearch.name"]              = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.productSearch.description' var="resourceString"/>
    aResources["keyboard.csc.productSearch.description"]       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.shoppingCart.name' var="resourceString"/>
    aResources["keyboard.csc.shoppingCart.name"]               = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.shoppingCart.description' var="resourceString"/>
    aResources["keyboard.csc.shoppingCart.description"]        = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.shipping.name' var="resourceString"/>
    aResources["keyboard.csc.shipping.name"]                   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.shipping.description' var="resourceString"/>
    aResources["keyboard.csc.shipping.description"]            = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.billing.name' var="resourceString"/>
    aResources["keyboard.csc.billing.name"]                    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.billing.description' var="resourceString"/>
    aResources["keyboard.csc.billing.description"]             = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.completeOrder.name' var="resourceString"/>
    aResources["keyboard.csc.completeOrder.name"]              = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.completeOrder.description' var="resourceString"/>
    aResources["keyboard.csc.completeOrder.description"]       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.returnItems.name' var="resourceString"/>
    aResources["keyboard.csc.returnItems.name"]                = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.returnItems.description' var="resourceString"/>
    aResources["keyboard.csc.returnItems.description"]         = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.refundType.name' var="resourceString"/>
    aResources["keyboard.csc.refundType.name"]                 = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.refundType.description' var="resourceString"/>
    aResources["keyboard.csc.refundType.description"]          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.receiveReturns.name' var="resourceString"/>
    aResources["keyboard.csc.receiveReturns.name"]             = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.receiveReturns.description' var="resourceString"/>
    aResources["keyboard.csc.receiveReturns.description"]      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.shipMultiple.name' var="resourceString"/>
    aResources["keyboard.csc.shipMultiple.name"]               = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.shipMultiple.description' var="resourceString"/>
    aResources["keyboard.csc.shipMultiple.description"]        = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.shipOne.name' var="resourceString"/>
    aResources["keyboard.csc.shipOne.name"]                    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.shipOne.description' var="resourceString"/>
    aResources["keyboard.csc.shipOne.description"]             = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.createReturnExchange.name' var="resourceString"/>
    aResources["keyboard.csc.createReturnExchange.name"]       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.createReturnExchange.description' var="resourceString"/>
    aResources["keyboard.csc.createReturnExchange.description"]= "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.createAppeasementProcess.name' var="resourceString"/>
    aResources["keyboard.csc.createAppeasementProcess.name"]       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='keyboard.csc.createAppeasementProcess.description' var="resourceString"/>
    aResources["keyboard.csc.createAppeasementProcess.description"]= "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";


    // Keyboard shortcut functional areas
    <fmt:message key='keyboard.area.commerce' var="resourceString"/>
    aResources["keyboard.area.commerce"]                      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    
    // Embedded Assistance (EA) Help Contents for CSC
    <fmt:message key='ea.csc.helpContent.ea_csc_order_search' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_search"]                     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_product_view' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_product_view"]                     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_product_item_price' var="resourceString"><fmt:param value="${example}"/></fmt:message>
    aResources["ea.csc.helpContent.ea_csc_product_item_price"]               = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_submit' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_submit"]                     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_submit_footer' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_submit_footer"]              = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_submit_create_schedule' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_submit_create_schedule"]     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_submit_create_schedule_footer' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_submit_create_schedule_footer"] = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_create_schedule' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_create_schedule"]            = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_create_schedule_footer' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_create_schedule_footer"]     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_scheduled_days_of_week' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_scheduled_days_of_week"]     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_scheduled_weeks' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_scheduled_weeks"]            = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_scheduled_dates_in_month' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_scheduled_dates_in_month"]   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_scheduled_actions' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_scheduled_actions"]          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_scheduled_status_failed' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_scheduled_status_failed"]    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_copy' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_copy"]                       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_order_view_cancel' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_order_view_cancel"]                = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_purchased_isReturnable' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_purchased_isReturnable"]           = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_instore_pickup_available' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_instore_pickup_available"]         = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.helpContent.ea_csc_instore_pickup_billing_logic' var="resourceString"/>
    aResources["ea.csc.helpContent.ea_csc_instore_pickup_billing_logic"]     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";

    // Embedded Assistance (EA) Tooltips for CSC
    <fmt:message key='ea.csc.tooltip.orderLink' var="resourceString"/>
    aResources["ea.csc.tooltip.orderLink"]                     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.tooltip.orderSave' var="resourceString"/>
    aResources["ea.csc.tooltip.orderSave"]                     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.tooltip.orderCancel' var="resourceString"/>
    aResources["ea.csc.tooltip.orderCancel"]                   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.tooltip.submitOrderButton' var="resourceString"/>
    aResources["ea.csc.tooltip.submitOrderButton"]             = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.tooltip.submitCreateScheduleButton' var="resourceString"/>
    aResources["ea.csc.tooltip.submitCreateScheduleButton "]   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='ea.csc.tooltip.createScheduleButton' var="resourceString"/>
    aResources["ea.csc.tooltip.createScheduleButton"]          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";

    //CSC Billing validation errors
    <fmt:message key='csc.billing.negativeAmount' var="resourceString"/>
    aResources["csc.billing.negativeAmount"]                   = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='csc.billing.invalidAmount' var="resourceString"/>
    aResources["csc.billing.invalidAmount"]                    = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='csc.billing.maxAmountReached' var="resourceString"/>
    aResources["csc.billing.maxAmountReached"]                 = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='csc.billing.zeroBalance' var="resourceString"/>
    aResources["csc.billing.zeroBalance"]                      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='csc.billing.form.error' var="resourceString"/>
    aResources["csc.billing.form.error"]                       = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='csc.billing.invalidMonth' var="resourceString"/>
    aResources["csc.billing.invalidMonth"]                     = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='csc.billing.invalidYear' var="resourceString"/>
    aResources["csc.billing.invalidYear"]                      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='csc.billing.invalidDate' var="resourceString"/>
    aResources["csc.billing.invalidDate"]                      = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='csc.billing.invalidCreditCardType' var="resourceString"/>
    aResources["csc.billing.invalidCreditCardType"]            = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='csc.billing.invalidCreditCardNumber' var="resourceString"/>
    aResources["csc.billing.invalidCreditCardNumber"]          = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    <fmt:message key='csc.billing.invalidCVVNumber' var="resourceString"/>
    aResources["csc.billing.invalidCVVNumber"]                 = "<svc-ui:getEndcodedJavascriptString originalString='${resourceString}'/>";
    
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/script/resources.js.jsp#4 $$Change: 1179550 $--%>