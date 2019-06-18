<%--
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/errorDetail.jsp#1 $ $Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $ $Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>

<jsp:useBean id="now" class="java.util.Date" />
<dspel:page xml="true">

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

    <%-- Error details --%>

    <div id="errorList">

    <table>
      <tr>
        <td>
          <strong>
            <fmt:message key="error.header"/>
          </strong>
        </td>
      </tr>
      <tr>
        <td>
          <c:choose>

          <%-- JSP tags throw this one --%>
          <c:when test="${not empty requestScope['javax.servlet.jsp.jspException']}">
            <c:set value="${requestScope['javax.servlet.jsp.jspException']}"
                   var="frameworkException"/>
          </c:when>

          <%-- DropletExceptions is a vector, but this page is designed only to show one exception --%>
          <c:when test="${not empty requestScope['DropletExceptions']}">
            <c:set value="${requestScope['DropletExceptions'][0]}"
                   var="frameworkException"/>
          </c:when>

          <%-- Web application container throws this one --%>
          <c:when test="${not empty requestScope['javax.servlet.error.exception']}">
            <c:set value="${requestScope['javax.servlet.error.exception']}"
                   var="frameworkException"/>
          </c:when>

          <%-- FrameworkExceptions is a vector based on DropletExceptions --%>
          <c:when test="${not empty sessionScope['FrameworkExceptions']}">
            <c:set value="${sessionScope['FrameworkExceptions'][0]}"
                   var="frameworkException"/>
          </c:when>

          <c:otherwise>
          </c:otherwise>
          </c:choose>

          <c:choose>
          <c:when test="${not empty frameworkException}">

            <a class="errorLink"
               href="#"
               onclick="showStackTrace();">
              <dspel:img src="${imageLocation}/nav/nav-arrow-closed.gif"
                         id="imgErrorDetailArrowClosed"
                         style="display:inline"
                         width="12"
                         height="12" />
              <dspel:img src="${imageLocation}/nav/nav-arrow-down.gif"
                         id="imgErrorDetailArrowDown"
                         style="display:none"
                         width="12"
                         height="12"
                         align="absmiddle" />
              <c:choose>
              <c:when test="${not empty frameworkException.message}">
                <c:out value="${frameworkException.message}"/>
              </c:when>
              <c:otherwise>
                <fmt:message key="error.message.detail"/>
              </c:otherwise>
              </c:choose>
            </a>

            <div id="errorDetails" style="display:none">
              <span class="errorDetails">
                <br/>
                <%
                   Throwable error = (Throwable)pageContext.getAttribute("frameworkException");
                   String stack = atg.core.exception.StackTraceUtils.getStackTrace(error, 10, 10);
                   stack = atg.core.util.StringUtils.replace(stack, '\n', "<br/>");
                   stack = atg.core.util.StringUtils.replace(stack, '\t', "&nbsp;&nbsp;&nbsp;");
                   out.println(stack);
                %>
              </span>
            </div>
          </c:when>
          <c:otherwise>
            <fmt:message key="error.message.type.unknown"/>
          </c:otherwise>
          </c:choose>
          <br/>
        </td>
      </tr>
      <tr>
      <td>
        <fmt:formatDate value="${now}" pattern="HH:mm z MMM dd yyyy" />
      </td>
     </tr>
    </table>

    </div>

  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/errorDetail.jsp#1 $$Change: 946917 $--%>
