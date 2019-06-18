<%--
Display the appropriate Gift list Information

Expected params
giftlistId : The Id of the gift list.

@version $Id:
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistLookupDroplet"/>
  <dsp:getvalueof var="giftlistId" param="giftlistId"/>
    <dsp:droplet name="GiftlistLookupDroplet">
      <dsp:param name="id" value="${giftlistId}" />
      <dsp:param name="elementName" value="giftlist"/>
      <dsp:oparam name="output">
        <li class="atg_commerce_csr_giftwishListName">
          <dsp:valueof param="giftlist.owner.firstName"/>&nbsp;
          <dsp:valueof param="giftlist.owner.lastName"/>, <dsp:valueof param="giftlist.eventName"/>
        </li>
      </dsp:oparam>
    </dsp:droplet><%-- End GiftlistLookupDroplet --%>
</dsp:page>
</c:catch>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/displayGiftlistInformation.jsp#1 $$Change: 946917 $--%>
