<%--
 Related tickets JSON data
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/relatedTicketsData.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page>

<dsp:importbean var="relatedTicketsFormHandler" bean="/atg/commerce/custsvc/order/RelatedTicketsTableFormHandler" scope="request" />
<json:object prettyPrint="${UIConfig.prettyPrintResponses}">
<json:property name="resultLength" value="${relatedTicketsFormHandler.totalItemCount}"/>
        <json:array name="results" items="${relatedTicketsFormHandler.searchResults}" var="ticketItem">
          <json:object>
            <dsp:tomap var="ticketItemMap" value="${ticketItem}"/>
            <json:property name="id" value="${ticketItemMap.id}"/>
            <json:property name="description" value="${ticketItemMap.description}"/>
            <web-ui:formatDate type="both" value="${ticketItemMap.creationTime}" dateStyle="short" timeStyle="short" var="creationDate"/>
            <json:property name="creationDate" value='${creationDate}'/>
            <dsp:tomap var="status" value="${ticketItemMap.subStatus}"/>
            <c:set var="statusString"><c:out value="${status.parentStatus}"/><c:out value=" (${status.subStatusName})"/></c:set>
            <json:property name="status" value='${statusString}'/>
            <dsp:tomap var="userItemMap" value="${ticketItemMap.user}"/>
            <json:property name="firstName" value='${userItemMap.firstName}'/>
            <json:property name="lastName" value='${userItemMap.lastName}'/>
            <json:property name="email" value='${userItemMap.email}'/>
            <dsp:tomap var="addressItemMap" value="${userItemMap.homeAddress}"/>
            <json:property name="phone" value='${addressItemMap.phoneNumber}'/>
            <json:property name="zipCode" value='${addressItemMap.postalCode}'/>
          </json:object>
        </json:array>
  </json:object>
</dsp:page>

<%-- Version:$$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/relatedTicketsData.jsp#1 $$Change: 946917 $--%>
