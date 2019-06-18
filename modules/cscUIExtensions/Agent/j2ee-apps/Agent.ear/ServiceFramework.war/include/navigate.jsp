<%--
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigate.jsp#1 $ $Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $ $Author: jsiddaga $
--%>
<span class="result">
  <c:if test="${total ne null}">
    <fmt:message key="searchResults.paging.context">
      <c:if test="${total != 0}">
        <fmt:param value="${offset + 1}"/>
      </c:if>
      <c:if test="${total == 0}">
        <fmt:param value="${offset + 0}"/>
      </c:if>
      <fmt:param value="${highIndex + 1}"/>
      <fmt:param value="${total}"/>
    </fmt:message>
  </c:if>
</span>
&nbsp;
<%-- Paging --%>
<c:if test="${total > highIndex+1 or (total == highIndex+1 and offset > 0)}">
  <fmt:message key="searchResults.paging.label.first"
               var="firstButtonTitle"/>
  <svc-ui:pagingButton link="${firstButtonTitle}"
                       pagingOperation="first"
                       style="paging"
                       styleDisabled="disabled"
                       styleDown="paging"
                       styleHover="paging"
                       title="${firstButtonTitle}"/>
  <fmt:message key="text.separator.vertical"/>
  <fmt:message key="searchResults.paging.label.previous"
               var="previousButtonTitle"/>
  <svc-ui:pagingButton link="${previousButtonTitle}"
                       pagingOperation="previous"
                       style="paging"
                       styleDisabled="disabled"
                       styleDown="paging"
                       styleHover="paging"
                       title="${previousButtonTitle}"/>
  <fmt:message key="text.separator.vertical"/>
  <fmt:message key="searchResults.paging.label.next"
               var="nextButtonTitle"/>
  <svc-ui:pagingButton link="${nextButtonTitle}"
                       pagingOperation="next"
                       style="paging"
                       styleDisabled="disabled"
                       styleDown="paging"
                       styleHover="paging"
                       title="${nextButtonTitle}"/>
  <fmt:message key="text.separator.vertical"/>
  <fmt:message key="searchResults.paging.label.last"
               var="lastButtonTitle"/>
  <svc-ui:pagingButton link="${lastButtonTitle}"
                       pagingOperation="last"
                       style="paging"
                       styleDisabled="disabled"
                       styleDown="paging"
                       styleHover="paging"
                       title="${lastButtonTitle}"/>
</c:if>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigate.jsp#1 $$Change: 946917 $--%>
