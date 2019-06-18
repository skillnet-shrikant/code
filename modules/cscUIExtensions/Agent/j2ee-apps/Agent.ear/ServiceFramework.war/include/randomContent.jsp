<%--
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/randomContent.jsp#1 $ $Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $ $Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">

<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <%-- Random content for testing cross-panel updates within framework --%>

<%
  java.util.Random generator = new java.util.Random();
  int cols = generator.nextInt(10);
  int rows = generator.nextInt(100);
  pageContext.setAttribute("cols", new Integer(cols));
  pageContext.setAttribute("rows", new Integer(rows));

  int highlight = generator.nextInt(6);
  switch (highlight) {
  case 0:
    pageContext.setAttribute("highlight", "ffffe0");
    break;
  case 1:
    pageContext.setAttribute("highlight", "ffe0ff");
    break;
  case 2:
    pageContext.setAttribute("highlight", "e0ffff");
    break;
  case 3:
    pageContext.setAttribute("highlight", "ffe0e0");
    break;
  case 4:
    pageContext.setAttribute("highlight", "e0ffe0");
    break;
  case 5:
    pageContext.setAttribute("highlight", "e0e0ff");
    break;
  default:
    pageContext.setAttribute("highlight", "e0e0e0");
    break;
  }
%>

  <table style="width=100%">
    <tr style="background-color:#e0e0e0;">
      <c:forEach begin="0" end="${cols}" varStatus="status">
        <td>
          <b><c:out value="${status.count}"/></b>
        </td>
      </c:forEach>
    </tr>
    <c:forEach begin="0" end="${rows}" varStatus="status">
      <c:set var="rowColor" value="ffffff"/>
      <c:if test="${status.index mod 2 eq 1}">
        <c:set var="rowColor" value="${highlight}"/>
      </c:if>
      <tr style="background-color:#<c:out value="${rowColor}"/>;">
        <c:forEach begin="0" end="${cols}">
          <td>
            <%= generator.nextInt(1000) %>
          </td>
        </c:forEach>
      </tr>
    </c:forEach>
  </table>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/randomContent.jsp#1 $$Change: 946917 $--%>
